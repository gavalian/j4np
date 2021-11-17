/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.hipo5.data.Schema.SchemaBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author gavalian
 */
public class Event {
    
    private ByteBuffer             eventBuffer = null;
    
    private int         eventBufferDefaultSize = 200*1024;
    
    private final int       NODE_HEADER_LENGTH =  8;
    private final int      EVENT_LENGTH_OFFSET =  4;
    private final int        EVENT_HEADER_SIZE = 16;
    private final int        EVENT_TAG_OFFSET  =  8;
    private final int        EVENT_MASK_OFFSET = 12;
    
    private EventNodes           eventNodesMap = new EventNodes();
    
    public Event(int size){
        eventBufferDefaultSize = size;
        byte[] bytes = new byte[eventBufferDefaultSize];
        bytes[0] = 'E';
        bytes[1] = 'V';
        bytes[2] = '4';
        bytes[3] = 'a';
        eventBuffer  = ByteBuffer.wrap(bytes);
        //System.out.println("creating event");
        eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
        eventBuffer.putInt(EVENT_LENGTH_OFFSET, 16);
    }
    
    public Event(){
        
        byte[] bytes = new byte[this.eventBufferDefaultSize];
        bytes[0] = 'E';
        bytes[1] = 'V';
        bytes[2] = '4';
        bytes[3] = 'a';
        eventBuffer  = ByteBuffer.wrap(bytes);
        /*bytes[0] = 'E';
        bytes[1] = 'V';
        bytes[2] = '4';
        bytes[3] = 'a';*/
        //System.out.println("creating event");
        eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
        eventBuffer.putInt(EVENT_LENGTH_OFFSET, 16);
    }
    
    public int getEventTag(){
        return this.eventBuffer.getInt(this.EVENT_TAG_OFFSET);
    }
    
    public void setEventTag(int tag){
        this.eventBuffer.putInt(this.EVENT_TAG_OFFSET, tag);
    }
    
    public void setEventMask(int mask){
        eventBuffer.putInt(EVENT_MASK_OFFSET, mask);
    }
    
    public void setEventBitMask(int bit){
        int word = eventBuffer.getInt(EVENT_MASK_OFFSET);
        int modifiedWord = ByteUtils.write(word, 1, bit, bit);
        eventBuffer.putInt(EVENT_MASK_OFFSET, modifiedWord);
    }
    public void clearEventBitMask(){
        eventBuffer.putInt(EVENT_MASK_OFFSET, 0);
    }
    public void unsetEventBitMask(int bit){
        int word = eventBuffer.getInt(EVENT_MASK_OFFSET);
        int modifiedWord = ByteUtils.write(word, 0, bit, bit);
        eventBuffer.putInt(EVENT_MASK_OFFSET, modifiedWord);
    }
    
    public int getEventBitMask(int bit){
        int word = eventBuffer.getInt(EVENT_MASK_OFFSET);
        return ByteUtils.getInteger(word, bit, bit);
    }
    
    public int getEventMask(){
        return eventBuffer.getInt(EVENT_MASK_OFFSET);
    }
    
    
    public void require(int size, boolean copy){
        int eventSize = getEventBufferSize();
        if(this.eventBuffer.capacity()<size){
            byte[] bytes = new byte[size+64];            
            System.arraycopy(eventBuffer.array(), 0, bytes, 0, eventSize);
            eventBuffer  = ByteBuffer.wrap(bytes);
            eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
    }
    
    public boolean hasBanks(List<Schema> schemas){
        int nlist = schemas.size();
        for(int i = 0; i < nlist; i++){
            if(hasBank(schemas.get(i))==false) return false;
        }
        return true;
    }
    
    public boolean hasBank(Schema schema){
        int group = schema.getGroup();
        int item  = schema.getItem();
        return (scan(group,item)>8);
    }
    
    public void require(int size){
        if(this.eventBuffer.capacity()<size){
            byte[] bytes = new byte[size+64];
            eventBuffer  = ByteBuffer.wrap(bytes);
            eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
            eventBuffer.putInt(EVENT_LENGTH_OFFSET, 16);
        }
    }
    
    public void write(Node node){
        int bufferSize = node.getBufferSize();
        if(bufferSize<=8) return;
        int  position = eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        
        System.arraycopy(node.getBufferData(), 0, 
                eventBuffer.array(), position, bufferSize);
        int group  = node.getGroup();
        int  item  = node.getItem();
        int  hash  = getHash(group,item);
        this.eventNodesMap.addLeaf(hash, position);
        eventBuffer.putInt(EVENT_LENGTH_OFFSET, position+bufferSize);
    }
    /**
     * Append a bank to the event. The event buffer size will increase
     * accordingly if there is no space to fit the bank.
     * @param node bank to be appended to the event.
     */
    public void write(Bank node){
        
        int bufferSize = node.getNodeBufferLength();
        if(bufferSize<=8) return;
        int  position = eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        require(position+bufferSize,true);
        //System.out.println(" buffer size = " + bufferSize + " position = "
        //        + position);
        System.arraycopy(node.getByteBuffer().array(), 0, 
                eventBuffer.array(), position, bufferSize);
        
        int group  = node.getNodeGroup();
        int  item  = node.getNodeItem();
        int  hash  = getHash(group,item);
        //this.eventNodesMap.addLeaf(hash, position);
        eventBuffer.putInt(EVENT_LENGTH_OFFSET, position+bufferSize);
    }
    /**
     * remove a bank represented by given schema from the event.
     * @param schema schema of the bank to be removed.
     * @return  true if bank was in the event and was removed, false otherwise
     */
    public boolean remove(Schema schema){
        int __group = schema.getGroup();
        int __item  = schema.getItem();
        return remove(__group,__item);
    }
    /**
     * Remove a node (given by group id and item id) from 
     * the event.
     * @param __group group id of the node (or bank) to be removed
     * @param __item  item id of the node to be removed
     * @return true if the bank was found and removed, false otherwise
     */
    public boolean remove(int __group, int __item){
        int index = scan(__group, __item);
        if(index<0) return false;        
        int length = scanLength(__group, __item);
        int nextPosition = index + 8 + length;
        int eventLength  = getEventBufferSize();
        System.arraycopy(eventBuffer.array(), nextPosition, eventBuffer.array(), index, eventLength - nextPosition);
        eventBuffer.putInt(this.EVENT_LENGTH_OFFSET, eventLength - length - 8);
        return true;
    }
    
    public int getHash(int... indices) {
        int hash_int = 0;
        if(indices.length>=2){
            int group = indices[0];
            int node  = indices[1];
            hash_int = (group<<16)|(node);
        }
        return hash_int;
    }
    
    public Bank read(Bank node, int position){
        int group  = node.getSchema().getGroup();
        int  item  = node.getSchema().getItem();
        short group_p = eventBuffer.getShort( position    );
        byte  item_p  = eventBuffer.get(      position + 2);
        byte  type_p  = eventBuffer.get(      position + 3);
        int   size_p  = eventBuffer.getInt(   position + 4);
        if(group==group_p&&item_p==item&&type_p==DataType.TABLE.getType()){
            node.copyFrom(eventBuffer, position, size_p + 8);
            //System.out.println(" TRUE THAT");
        } else { node.reset(); }
        
        return node;
    }
    
    public Node read(Node node, int position){
        
        short group_p = eventBuffer.getShort( position    );
        byte  item_p  = eventBuffer.get(      position + 2);
        byte  type_p  = eventBuffer.get(      position + 3);
        int   size_p  = eventBuffer.getInt(   position + 4);
        //System.out.println(" size = " + size_p + " type = " + type_p);
        DataType type = DataType.getTypeById(type_p);
        //System.out.println(" TYPE ID = " + type_p + "  TYPE = " + type);
        Node        n = new Node();
        n.allocate(size_p+8, type);
        
        System.arraycopy(eventBuffer.array(), position, n.getBufferData(), 0, size_p+8);        
        return n;
    }
    
    public CompositeBank read(CompositeBank bank){
        read(bank.getMasterBank());
        read(bank.getSlaveBank());
        bank.processIndex();
        return bank;
    }
    
    public Bank read(Bank node){
        int   group  = node.getSchema().getGroup();
        int    item  = node.getSchema().getItem();
        int position = this.scan(group, item);
        if(position>0){
            read(node,position);
        } else {
            node.reset();
        }
        /*
        int group  = node.getNodeGroup();
        int  item  = node.getNodeItem();
        int   hash = getHash(group,item);
        if(eventNodesMap.hasLeaf(hash)==true){
            int position = eventNodesMap.getPosition(hash);
            int   length = eventBuffer.getInt(position+4);
            int     size = 8 + length;
            node.copyFrom(eventBuffer, position, size);
            //System.out.println(" read node with size = " + size);
        }*/
        return node;
    }
    
    public Node read(int group, int item){
        int position = this.scan(group, item);
        if(position>=8){
            short group_p = eventBuffer.getShort( position    );
            byte  item_p  = eventBuffer.get(      position + 2);
            byte  type_p  = eventBuffer.get(      position + 3);
            int   size_p  = eventBuffer.getInt(   position + 4);
            //System.out.println(" size = " + size_p + " type = " + type_p);
            DataType type = DataType.getTypeById(type_p);
            //System.out.println(" TYPE ID = " + type_p + "  TYPE = " + type);
            Node        n = new Node();
            n.allocate(size_p+8, type);
        
            System.arraycopy(eventBuffer.array(), position, n.getBufferData(), 0, size_p+8);
            return n;
        }
        System.out.println("(warning) :>>> no node present with group = " 
                + group + ", item = " + item + " positoin = " + position);
        return new Node(1,1,DataType.FLOAT,0);
    }
    
    public boolean isEmpty(){
        return eventBuffer.getInt(EVENT_LENGTH_OFFSET)<=EVENT_HEADER_SIZE;
    }
    
    public void scan(){
        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //System.out.println(" EVENT LENGTH = " + eventLength 
        //        + " position = " + position);
        //this.eventNodesMap.reset();
        eventNodesMap = new EventNodes();
        while(position+8<eventLength){
            //System.out.println("getting node at " + position);
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4);
            //int   hash  = this.getHash(group,item);
            //eventNodesMap.addLeaf(hash, position);
            System.out.printf("\t at %8d : goup = %8d, item = %4d, type = %4d, size = %8d %n", 
                    position,group,item, type, size);
            position += size + NODE_HEADER_LENGTH;
        }
    }
    
    public int scan(int __group, int __item){

        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();
        
        while(position + NODE_HEADER_LENGTH <eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4);
            if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        }
        return -1;
    }
    
    public void scanShowPrint(){
       int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();
        
        while(position +NODE_HEADER_LENGTH <eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4);
            
            if((type>0&&type<9)&&type!=7){
                Node node = read(group,item);
                node.show();
            }
            
            /*System.out.printf("\t group/item : [%5d / %4d] , position = %5d, type = %4d , size = %4d\n",
                    group,item, position, type,size);*/
            //if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        } 
    }
    
    public Map<Integer,Node> readNodes(int groupID){
        Map<Integer,Node> map = new HashMap<>();
        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();        
        while(position +NODE_HEADER_LENGTH <eventLength){
            short group = eventBuffer.getShort( position );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4);
            //System.out.printf("\t group/item : [%5d / %4d] , position = %5d, type = %4d , size = %4d\n",
            //        group,item, position, type,size);
            DataType typeId = DataType.getTypeById(type);
            if(groupID==group){
                if(type>=1&&type<=8){
                    Node        n = new Node();
                    n.allocate(size+8, typeId);
                    n.setGroupItem(group, item);
                    System.arraycopy(eventBuffer.array(), position, n.getBufferData(), 0, size+8);
                    Integer key = (int) item;
                    map.put(key, n);
                }
            }
            //if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        }
        return map;
    }
    
    public void scanShow(){

        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();
        
        while(position +NODE_HEADER_LENGTH <eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4);
            System.out.printf("\t group/item : [%5d / %4d] , position = %5d, type = %4d , size = %4d\n",
                    group,item, position, type,size);
            //if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        }
        //return -1;
    }
    
    public int scanLengthAt(int __group, int __item, int position){
         short group = eventBuffer.getShort( position    );
         byte  item  = eventBuffer.get(      position + 2);
         byte  type  = eventBuffer.get(      position + 3);
         int   size  = eventBuffer.getInt(   position + 4);
         if(__group==group&&__item==item)   return size;
        return -1;
    }
    
    public int scanLength(int __group, int __item){

        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        this.eventNodesMap.reset();
        
        while(position +NODE_HEADER_LENGTH <eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4);
            if(__group==group&&__item==item)    return size;
            position += size + NODE_HEADER_LENGTH;
        }
        return -1;
    }
    
    public int getEventBufferSize(){
        return eventBuffer.getInt(EVENT_LENGTH_OFFSET);
    }
    
    public final ByteBuffer getEventBuffer(){
        return this.eventBuffer;
    }
    
    public void reset(){
        eventNodesMap.reset();
        eventBuffer.putInt(EVENT_LENGTH_OFFSET, 16);
    }
    
    public void initFrom(byte[] buffer){
        require(buffer.length);
        System.arraycopy(buffer, 0, this.eventBuffer.array(), 0, buffer.length);
    }
    
    public void initFrom(byte[] buffer, int length){
        require(length);
        System.arraycopy(buffer, 0, this.eventBuffer.array(), 0, length);
    }
    
    protected void init(){
        
    }
    
    public void printEventBuffer(){
        System.out.print("EVENT BUFFER : ");
        for(int i = 0; i < 16; i++){
            System.out.print(String.format("%02X ",this.eventBuffer.get(i)));
        }
        System.out.println();
    }
    
    public void show(){
        
        int[] keys = this.eventNodesMap.getKeys();
        
        System.out.println("*** event *** ::: tag = " + this.getEventTag() +
                " mask = " + String.format("%032X", eventBuffer.getInt(EVENT_MASK_OFFSET)) 
                + " size = " + 
                getEventBufferSize() + " node count = " + keys.length);
        
        for(int i = 0; i < keys.length; i++){
           int group = (keys[i]>>16)&0x0000FFFF;
           int  item = (keys[i]&0x0000FFFF);
           int position = eventNodesMap.getPosition(keys[i]);
           System.out.println(String.format("\t%8d %4d : position = %8d", group,item,position));
        }
    }
    
    public static class EventNodes {
        
        //private IntIntMap  leafsMap = null;
        private int[]      results  = null;
        
        public EventNodes(){
            //leafsMap = new IntIntMap(100,0.75F);
            results  = new int[2];
        }
                
        public int getCount(){
            return 0;//leafsMap.size();
        }
        
        public void addLeaf(int hash, int position){
            //leafsMap.put(hash, position);
        }
        
        public boolean hasLeaf(int hash){
            return false;//leafsMap.contains(hash);
        }
        
        public int getPosition(int hash){
            results[0] = -1;
            //leafsMap.get(hash, results);
            return results[0];
        }
        
        public void reset(){
            //leafsMap = new IntIntMap(40,0.75F);
            //leafsMap = new IntIntMap();
            /*int[] keys = leafsMap.keys();
            for(int key: keys){
                //leafsMap.put(key,-1);
                leafsMap.remove(key);
            }*/
        }
        
        public int[] getKeys(){
            return new int[]{0};//leafsMap.keys();
        }
    };
    
    public Event  reduceEvent(List<Bank> banks){
        int   size = this.getEventBufferSize();
        int nbanks = banks.size();
        
        Event reducedEvent = new Event(size);
        
        reducedEvent.setEventTag(getEventTag());
        reducedEvent.setEventMask(getEventMask());
        
        for(int b = 0; b < nbanks; b++){
            Bank bank = banks.get(b);
            read(bank);
            if(bank.getRows()>0){
                reducedEvent.write(bank);
            }
        }        
        return reducedEvent;
    }
    
    public static void main(String[] args){
        
        
        
        SchemaBuilder schemaBuilder = new SchemaBuilder("event",1234,1);
        schemaBuilder.addEntry("pid", "I", "pid");
        schemaBuilder.addEntry( "px", "F", "x-component");
        schemaBuilder.addEntry( "py", "F", "y-component");
        schemaBuilder.addEntry( "pz", "F", "z-component");

        Schema schema = schemaBuilder.build();
        
        schema.show();
        System.out.println(schema.getSchemaString());
        Event event = new Event();
        
        Bank  bank = new Bank(schema,5);
        Node  node = new Node(1432,1,"{event::data}{px/F,py/F,pz/F}");
        
        event.write(bank);
        event.write(node);
        
        event.show();
        
        int indexBank = event.scan(1234, 1);
        int indexNode = event.scan(1432, 1);
        
        System.out.println("--> b = " + indexBank + " n = " + indexNode);
        System.out.println("===> b = " + event.scanLength(1234, 1) + " n= " + event.scanLength(1432, 1));
        event.remove(schema);
        event.show();
        indexBank = event.scan(1234, 1);
        indexNode = event.scan(1432, 1);
        
        System.out.println("--> b = " + indexBank + " n = " + indexNode);
        System.out.println("===> b = " + event.scanLength(1234, 1) + " n= " + event.scanLength(1432, 1));
        
        Bank particle = new Bank(schema);
        
        //event.read(particle, indexBank);
        
        System.out.println(" bank rows = " + particle.getRows());
        
        particle.show();
        System.out.println(particle.nodeString());
                
        System.out.println(node.toString());
                
        node.show();
        Node  str = new Node();
        
        event.read(str, indexNode);
        /*  
        event.printEventBuffer();
        
        for(int i = 0; i < 5; i++){
            event.reset();
            event.write(node);
            node.show();
            System.out.println(node.nodeString());
            event.show();
        }
        System.out.println("DONE Writing.....");
        System.out.println("Initiating reading...");

        Bank noder = new Bank(schema,20);

        for(int i = 0; i < 10; i++){
            event.read(noder);
            noder.show();
        }*/
        System.out.println("DONE reading...");
        
        System.out.println("---------- AFTER");
        event.reset();
        event.write(bank);
        event.write(node);
        event.show();
        event.remove(1234, 1);
        event.scan();
        event.show();
        event.remove(1432, 1);
        event.scan();
        event.show();
    }
}
