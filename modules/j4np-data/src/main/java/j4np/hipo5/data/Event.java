/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataNode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class Event implements DataEvent {
    
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
        bytes[2] = '5';
        bytes[3] = 'b';
        eventBuffer  = ByteBuffer.wrap(bytes);
        /*bytes[0] = 'E';
        bytes[1] = 'V';
        bytes[2] = '4';
        bytes[3] = 'a';*/
        //System.out.println("creating event");
        eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
        eventBuffer.putInt(EVENT_LENGTH_OFFSET, 16);
        eventBuffer.putInt(this.EVENT_TAG_OFFSET, 0);
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
            byte[] bytes = new byte[size+128];            
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
    
    public static DataFrame getDataFrame(int count){
       return Event.getDataFrame(count, 256*1024);
    }
    
    public static DataFrame getDataFrame(int count, int size){
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < count; i++){
            frame.addEvent(new Event(size));
        }
        return frame;
    }
    public void require(int size){
        if(this.eventBuffer.capacity()<size){
            //------------------------- fix this here, this should be copying the existing buffer
            //-----------------------------
            //System.out.printf("::: event buffer resizing from %d to %d\n",
            //        this.eventBuffer.capacity(),size);
            int occupied = this.getEventBufferSize();
            byte[] bytes = new byte[size+64];
            System.arraycopy(eventBuffer.array(), 0, bytes, 0, occupied);
            eventBuffer  = ByteBuffer.wrap(bytes);
            eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
            eventBuffer.putInt(EVENT_LENGTH_OFFSET, 16);
        }
    }
    
    public void write(CompositeNode node){
        if(node.getLength()>0){
            int totalLength = node.getLength() + 8;
            int    position = eventBuffer.getInt(EVENT_LENGTH_OFFSET);
            this.require(position+totalLength + 24);
            System.arraycopy(node.getByteBuffer().array(), 0, 
                eventBuffer.array(), position, totalLength);
            eventBuffer.putInt(EVENT_LENGTH_OFFSET, position+totalLength);
        }
    }
    
    public void write(Leaf node){
        if(node.getLength()>0){
            int totalLength = node.getLength() + 8 ;//+ node.getHeaderLength();
            int    position = eventBuffer.getInt(EVENT_LENGTH_OFFSET);
            this.require(position+totalLength + 24);
            System.arraycopy(node.getByteBuffer().array(), 0, 
                eventBuffer.array(), position, totalLength);
            eventBuffer.putInt(EVENT_LENGTH_OFFSET, position+totalLength);
        }
    }
    
    public void move(Structure struct, int group, int item){
        int position = this.scan(group, item);
        if(position>=16){
            int size = this.eventBuffer.getInt(position+4)&0x00FFFFFF;
            //System.out.printf(" writing [%d,%d] size = %d\n ",group,item,size);
            struct.write(eventBuffer, position, size+8);
        }
    }
    
    public void write(Structure struct){
        int      length = struct.length() + 8;
        int    position = eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        this.require(position+length+64);
        System.arraycopy(struct.buffer.array(), 0, 
                eventBuffer.array(), position, length);
        eventBuffer.putInt(EVENT_LENGTH_OFFSET, position+length);
    }
    
    public void write(Node node){
        int bufferSize = node.getBufferSize();
        if(bufferSize<=8) return;
        int  position = eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        this.require(position+bufferSize + 24);
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
        //System.out.println("LENGTH = " + length);
        
        int nextPosition = index + 8 + length;
        int eventLength  = getEventBufferSize();
        System.arraycopy(eventBuffer.array(), nextPosition, eventBuffer.array(), index, eventLength - nextPosition);
        eventBuffer.putInt(this.EVENT_LENGTH_OFFSET, eventLength - length - 8);
        return true;
    }
    
    public boolean replace(int group, int item, CompositeNode node){
        int position = this.scan(group,item);
        int length = this.scanLengthAt(group,item, position);
        int node_length = node.getLength();
        //System.out.printf("-- node @ %d -- data length = %d, node length = %d\n",
        //        position,length, node_length);
        if(length!=node_length){
            System.out.printf("----- error replacing node (%6d,%6d) dues to size difference %d,%d\n",
                    node.getGroup(),node.getItem(),length, node_length);
            return false;
        }
        System.arraycopy(node.getByteBuffer().array(), 0, this.eventBuffer.array(), position, length);
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
    
    public void read(CompositeNode node, int group, int item){
        int position = scan(group,item);
        if(position<0) { node.reset(); return;}
        int   length = this.scanLengthAt(group,item, position);
        node.initFromBuffer(this.eventBuffer.array(), position, length+8);
    }
    
    public void read(Leaf node, int group, int item){
        int position = scan(group,item);
        if(position<0) { node.reset(); return;}
        int   length = this.scanLengthAt(group,item, position);
        node.initFromBuffer(this.eventBuffer.array(), position, length+8);
    }
    
    public void readAt(Leaf node, int group, int item, int position){
        if(position<0) { node.reset(); return;}
        int   length = this.scanLengthAt(group,item, position);
        node.initFromBuffer(this.eventBuffer.array(), position, length+8);
    }
    
    public Leaf readLeaf(int __cgroup, int __citem, int __lgroup, int __litem){
        int cposition = scan(__cgroup, __citem);
        if(cposition<0) return new Leaf();
        int clength   = scanLengthAt(__cgroup, __citem, cposition);
        int lposition = scan(__lgroup,__litem, cposition+8, clength);
        if(lposition<0) return new Leaf();
        int llength = scanLengthAt(__lgroup,__litem, lposition);
        
        System.out.printf(" reading leaf at %d, with length = %d\n",lposition,llength);
        Leaf leaf = new Leaf(llength+16);
        leaf.initFromBuffer(this.eventBuffer.array(), lposition, llength+8);
        return leaf;
    }
    
    public void read(Leaf node){        
        read(node,node.getGroup(),node.getItem());
    }
    
    public void showAt(int position){
        
        short group_p = eventBuffer.getShort( position    );
        byte  item_p  = eventBuffer.get(      position + 2);
        byte  type_p  = eventBuffer.get(      position + 3);
        int   size_w  = eventBuffer.getInt(   position + 4);
        int   length  = size_w&0x00FFFFFF;
        int   format  = size_w>>24&0x000000FF;
        System.out.printf("[%8d, %5d] t [%3d] f [%4d] s [%5d] ", 
                group_p, item_p, type_p, format, length );
        if(format>0) {
            System.out.printf(" desc [ ");        
            for(int i = 0; i < format; i++){
                System.out.printf("x%02X ", eventBuffer.get(position+8+i));
            } System.out.printf("] ");
        }
        
        int howMany = Math.min(12, length);
        for(int r = 0; r < howMany; r++) System.out.printf("x%02X ",eventBuffer.get(position+8+format+r));
        System.out.printf("\n");
    }
    
    public Node read(Node node, int position){        
        short group_p = eventBuffer.getShort( position    );
        byte  item_p  = eventBuffer.get(      position + 2);
        byte  type_p  = eventBuffer.get(      position + 3);
        int   size_p  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
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
    
    public void read(Bank... banks){
        for(Bank b : banks) this.read(b);
    }
    
    public Bank[] read(Schema... schemas){
        Bank[] b = new Bank[schemas.length];
        for(int i = 0; i < schemas.length; i++){
            int position = this.scan(schemas[i].getGroup(), schemas[i].getItem());
            if(position<0){
                b[i] = new Bank(schemas[i],0);
            } else {
                int length = this.scanLengthAt(schemas[i].getGroup(), schemas[i].getItem(), position);
                b[i] = new Bank(schemas[i],this.eventBuffer, position,length);
            }
        }
        return b;
    }
    
    public Bank read(Bank node){
        int   group  = node.getSchema().getGroup();
        int    item  = node.getSchema().getItem();
        try{
            int position = this.scan(group, item);
            if(position>0){
                read(node,position);
            } else {
                node.reset();
            }
        } catch (Exception e){
            System.out.printf("(corruption error) : failure to scan event size = %d\n",
                    this.getEventBufferSize());
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
    
    public void read(CompositeNode node, int position){
        int length = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
        node.initFromBuffer(this.eventBuffer.array(), position, length+8);
    }
    
    public void read(CompositeNode node){
        int group = node.getGroup();
        int  item = node.getItem();
        try{
            int position = this.scan(group, item);
            if(position>0){
                read(node,position);
            } else {
                node.reset();
            }
        } catch (Exception e){
            System.out.printf("(corruption error) : failure to scan event size = %d\n",
                    this.getEventBufferSize());
            node.reset();
        }
    }
    
    public Node read(int group, int item){
        int position = this.scan(group, item);
        if(position>=8){
            short group_p = eventBuffer.getShort( position    );
            byte  item_p  = eventBuffer.get(      position + 2);
            byte  type_p  = eventBuffer.get(      position + 3);
            int   size_p  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
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
    
    public Structure readAt(int position){
        int size = this.eventBuffer.getInt(position+4)&0x00FFFFFF;
        Structure struct = new Structure(1,1,1,size+8);
        System.out.println(" from event");
        struct.show();
        System.arraycopy(this.eventBuffer.array(), position, struct.buffer.array(), 0, size+8);
        return struct;
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
            int   size  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
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
        
        while(position + NODE_HEADER_LENGTH < eventLength){
            short group = eventBuffer.getShort( position  );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
            if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        }
        return -1;
    }
    
    public int scan(int __group, int __item, int __position, int __maxlength){
        int position = __position;
        int bufferLength = __position + __maxlength;
        while(position + NODE_HEADER_LENGTH < bufferLength){
            short group = eventBuffer.getShort( position  );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
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
            int   size  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
            
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
            int   size  = (eventBuffer.getInt(   position + 4)&0x00FFFFFF);
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
        System.out.println("\n" + getEventHeaderString()+"\n");
        while(position + NODE_HEADER_LENGTH < eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   sizeWord  = eventBuffer.getInt( position + 4);
            int   size  = sizeWord&0x00FFFFFF;
            int   format = (sizeWord>>24)&0x000000FF;
            System.out.printf("\t group/item : [%6d / %4d] , position = %5d, type = %4d , format length = %4d, size = %4d\n",
                    group,item, position, type, format, size);
            //if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        }
        //return -1;
    }
    
    public void scanLeafs(CompositeNode index){
        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();
        index.setRows(0);
        int row = 0;
        while(position + NODE_HEADER_LENGTH < eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   sizeWord  = eventBuffer.getInt( position + 4);
            int   size  = sizeWord&0x00FFFFFF;
            int   format = (sizeWord>>24)&0x000000FF;
            
            index.setRows(row+1);
            index.putInt(0, row, group);
            index.putInt(1, row, item);
            index.putInt(2, row, position);
            index.putInt(3, row, size);
            
            row++;
            //System.out.printf("\t group/item : [%6d / %4d] , position = %5d, type = %4d , format length = %4d, size = %4d\n",
            //        group,item, position, type, format, size);
            //if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        }
    }
    public List<String> scanLeafs(){
        List<String> leafs = new ArrayList<>();
        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();
        System.out.println("\n" + getEventHeaderString()+"\n");
        while(position +NODE_HEADER_LENGTH <eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   sizeWord  = eventBuffer.getInt(   position + 4);
            int     size = sizeWord&0x00FFFFFF;
            int   format = (sizeWord>>24)&0x000000FF;
            String data = String.format("node [%4d, %3d], type = %3d, format = %d, length = %d", group,item,type, format,size);
            //System.out.printf("\t group/item : [%6d / %4d] , position = %5d, type = %4d , size = %4d\n",
            //        group,item, position, type, size&0x00FFFFFF);
            //if(__group==group&&__item==item)    return position;
            leafs.add(data);
            position += size + NODE_HEADER_LENGTH;
        }
        return leafs;
        //return -1;
    }
    
    public int scanLengthAt(int __group, int __item, int position){
         short group = eventBuffer.getShort( position    );
         byte  item  = eventBuffer.get(      position + 2);
         byte  type  = eventBuffer.get(      position + 3);
         int   size  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
         if(__group==group&&__item==item)   return (size&0x00FFFFFF);
        return -1;
    }
    
    public int scanLength(int __group, int __item){

        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();
        
        while(position +NODE_HEADER_LENGTH <eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   size  = eventBuffer.getInt(   position + 4)&0x00FFFFFF;
            //System.out.printf("--- %d %d - %d -- %d\n",group, item, type, size);
            if(__group==group&&__item==item)    return (size&0x00FFFFFF);
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
    
    public Event copy(){
        int size  = getEventBufferSize();
        Event evt = new Event(size+size/2+24);
        evt.initFrom(this.getBuffer().array(), size);
        return evt;
    }
    
    public void copyFrom(Event e){
        int size = e.getEventBufferSize();
        require(size);
        System.arraycopy(e.eventBuffer.array(), 0, this.eventBuffer.array(), 0, size);
    }    
    
    public void copyNodeAt(Event e, int position, int length){        
        int size = e.getEventBufferSize();
        e.require(size+length+56); 
        System.arraycopy(this.eventBuffer.array(), position, e.getEventBuffer().array(), size, length);
        e.getEventBuffer().putInt(4, size+length);
    }
    
    public void copyNode(Event e, int group, int item){
        int position = this.scan(group, item);
        if(position<16) return;
        int length = this.scanLengthAt(group, item, position) + 8;
        this.copyNodeAt(e, position, length);
    }
    
    public void initFrom(byte[] buffer){
        require(buffer.length);
        System.arraycopy(buffer, 0, this.eventBuffer.array(), 0, buffer.length);
    }
    
    public void initFrom(byte[] buffer, int length){
        require(length);
        System.arraycopy(buffer, 0, this.eventBuffer.array(), 0, length);
    }
    
    public void initFrom(byte[] buffer, int position, int length){
        require(length);
        System.arraycopy(buffer, position, this.eventBuffer.array(), 0, length);
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
    
    public String getEventHeaderString(){
        return String.format("... event ... ::: tag = %8d, mask = %032X, size = %8d", 
                getEventTag(),getEventMask(),getEventBufferSize());
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

    @Override
    public void getAt(DataNode node, int position) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ByteBuffer getBuffer() {
        return this.eventBuffer;
    }

    @Override
    public int bufferLength() {
        return this.bufferLength();
    }

    @Override
    public boolean allocate(int size) {
        this.require(size); return true;
    }

    @Override
    public int identifier() {
        return this.eventBuffer.getInt(0);
    }

    @Override
    public boolean verify() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }

    @Override
    public String showString() {
        
        StringBuilder str = new StringBuilder();
        int    position = EVENT_HEADER_SIZE;
        int eventLength = this.eventBuffer.getInt(EVENT_LENGTH_OFFSET);
        //this.eventNodesMap.reset();
        str.append(getEventHeaderString());
        while(position + NODE_HEADER_LENGTH < eventLength){
            short group = eventBuffer.getShort( position    );
            //System.out.println(" group = " + group);
            byte  item  = eventBuffer.get(      position + 2);
            byte  type  = eventBuffer.get(      position + 3);
            int   sizeWord  = eventBuffer.getInt( position + 4);
            int   size  = sizeWord&0x00FFFFFF;
            int   format = (sizeWord>>24)&0x000000FF;
            int   rows = 0;
            switch(type){
                case 1: rows = size; break;
                case 2: rows = size>0?size/2:0; break;
                case 3: rows = size>0?size/4:0; break;
                case 4: rows = size>0?size/5:0; break;
                case 5: rows = size>0?size/8:0; break;
                case 8: rows = size>0?size/8:0; break;
                default: break;
            }
            
            if(type==1) rows = size;
            if(type==2) rows = size>0?size/2:0;
            if(type==3) rows = size>0?size/4:0;
            
            str.append(String.format("%6d,%4d, %5d, %4d , %4d, %4d, %5d\n",
                    group,item, type, position, format, size, rows));
            //if(__group==group&&__item==item)    return position;
            position += size + NODE_HEADER_LENGTH;
        }
        return str.toString();
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

        Event event = new Event();
        Structure st = new Structure(12,120,12,1024);
        
        Leaf node1 = new Leaf(12,1,"sssifff",25);
        node1.setRows(2);
        Leaf node3 = Leaf.random(12, "ssiiff");
        node3.setGroup(12).setItem(3);
        node3.print();
//        event.write(node1);        

        
        Leaf node2 = new Leaf(12,2,"fflf",15);
        node2.setRows(4);
        
        
        st.write(node1.getByteBuffer(), node1.getLength()+8);
        st.write(node2.getByteBuffer(), node2.getLength()+8);
        st.write(node3.getByteBuffer(), node3.getLength()+8);
        
        st.show();
        
        event.write(st);
        event.scanShow(); 
        
        int pos = event.scan(12, 120);
        int len = event.scanLengthAt(12, 120, pos);
        
        int pos1 = event.scan(12, 1, pos+8, len+8);
        int len1 = event.scanLengthAt(12, 1, pos1);
        
        int pos2 = event.scan(12, 2, pos+8, len+8);
        int len2 = event.scanLengthAt(12, 2, pos2);
        
        System.out.printf(" pos = %d, length = %d\n",pos,len);
        System.out.printf(" pos 1 = %d, length = %d\n",pos1,len1);
        System.out.printf(" pos 2 = %d, length = %d\n",pos2,len2);
        
        Leaf leaf  = event.readLeaf(12, 120, 12, 2);
        Leaf leaf1 = event.readLeaf(12, 120, 12, 1);
        Leaf leaf3 = event.readLeaf(12, 120, 12, 3);
         
        leaf.show();
        leaf.print();
        
        leaf1.show();
        leaf1.print();
        
        leaf3.show();
        leaf3.print();
        
        //node1.info();
        
        /*
        
        node2.info();
        
        Leaf node3 = new Leaf(12,3,"2b4f2l",1);
        event.write(node3);
        
        
        event.scanShow();
        
        System.out.println(event.getEventBufferSize());
        
        
        
        CompositeNode node4 = new CompositeNode(480);
        
        
        event.read(node4,12,3);
        
        node4.show();
        node4.print();
        
        event.remove(12, 2);
        event.scanShow();
        
        node4.setItem(33);
        
        event.replace(12,3, node4);
        
        event.scanShow();*/
        
        /*
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
        
        event.read(str, indexNode);*/
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
        /*
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
        event.show();*/
    }
}
