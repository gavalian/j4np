/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.hipo5.data.Schema.SchemaBuilder;
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
public class Bank {
    
    /**
     * Byte buffer to keep the node data. Resized automatically
     */
    private ByteBuffer nodeBuffer = null;
    /**
     * The factor used for reallocating the buffer. The new size
     * will be 25% more than the requested size.
     */
    private double reallocateFactor = 0.25;
    /**
     * Size of the header bytes used for describing the node.
     */
    private int    nodeHeaderSize   = 8;
    
    private Schema nodeSchema       = null;
        
    private int        nodeRows     = 0;
    /**
     * Default constructor reserves some bytes
     */
    public Bank(){
        byte[] bytes = new byte[128];
        nodeBuffer   = ByteBuffer.wrap(bytes);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    /**
     * Create a node with given schema.
     * @param sch 
     */
    public Bank(Schema sch){
        nodeSchema   = sch;
        byte[] bytes = new byte[128];
        nodeBuffer   = ByteBuffer.wrap(bytes);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        short  group = (short) nodeSchema.getGroup();
        byte    item = (byte)  nodeSchema.getItem();
        byte    type = (byte)  DataType.getType("TABLE").getType();
        nodeRows     = 0;
        nodeBuffer.putShort( 0, group);
        nodeBuffer.put(      2, item);
        nodeBuffer.put(      3, type);
        nodeBuffer.putInt(   4, 0);
    }
    /**
     * Creates a node with given schema and allocates 
     * space for rows entries.
     * @param sch
     * @param rows 
     */
    public Bank(Schema sch, int rows){
        
        nodeSchema   = sch;
        int nodeSize = nodeSchema.getEntryLength()*rows + nodeHeaderSize;
        //System.out.println("Node allocating Length = " + nodeSize);
        byte[] bytes = new byte[nodeSize];
        
        nodeBuffer   = ByteBuffer.wrap(bytes);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        short  group = (short) nodeSchema.getGroup();
        byte    item = (byte)  nodeSchema.getItem();
        byte    type = (byte)  DataType.getType("TABLE").getType();
        int   length = (int)  (nodeSchema.getEntryLength()*rows);
        nodeRows     = rows;
        nodeBuffer.putShort( 0, group);
        nodeBuffer.put(      2, item);
        nodeBuffer.put(      3, type);
        nodeBuffer.putInt(   4, length);
    }
    
    protected Bank(Schema sc, ByteBuffer buffer, int position, int length){
        nodeSchema   = sc;
        byte[] bytes = new byte[length+24];
        
        nodeBuffer   = ByteBuffer.wrap(bytes);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        System.arraycopy(buffer.array(), position, nodeBuffer.array(), 0, length);
        int size = nodeBuffer.getInt(4);
        int rowLength = this.nodeSchema.getEntryLength();
        nodeRows = size/rowLength;
    }
    /**
     * Copy the content of the given buffer into the current Node
     * structure. 
     * @param buffer buffer to copy from
     * @param position position in the source buffer
     * @param length number of bytes to copy
     */
    protected void copyFrom(ByteBuffer buffer, int position, int length){
        if(length>=nodeBuffer.capacity()){
            int realloc = length + (int) (length * reallocateFactor);
            resize(realloc);
        }
        System.arraycopy(buffer.array(), position, nodeBuffer.array(), 0, length);
        int size = nodeBuffer.getInt(4);
        int rowLength = this.nodeSchema.getEntryLength();
        nodeRows = size/rowLength;
    }

    /**
     * Sets the buffer rows to be rows reallocates the buffer
     * if the buffer size is not sufficient.
     * @param rows number of rows
     */    
    public void setRows(int rows){
        
        int nodeSize = nodeSchema.getEntryLength()*rows + nodeHeaderSize;
        //System.out.println("Node allocating Length = " + nodeSize);
        
        if(nodeSize>nodeBuffer.capacity()){
            byte[] bytes = new byte[nodeSize];
            
            nodeBuffer   = ByteBuffer.wrap(bytes);
            nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
            short  group = (short) nodeSchema.getGroup();
            byte    item = (byte)  nodeSchema.getItem();
            byte    type = (byte)  DataType.getType("TABLE").getType();
            nodeBuffer.putShort( 0, group);
            nodeBuffer.put(      2, item);
            nodeBuffer.put(      3, type);
        }
        
        int   length = (int)  (nodeSchema.getEntryLength()*rows);
        nodeRows     = rows;
        nodeBuffer.putInt(   4, length);
    }

    public double getValue(String name, int row){
        int type = this.nodeSchema.getType(name);
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);
        switch(type){
            case 1: return (double) nodeBuffer.get(offset);
            case 2: return (double) nodeBuffer.getShort(offset);
            case 3: return (double) nodeBuffer.getInt(offset);
            case 4: return (double) nodeBuffer.getFloat(offset);
            case 5: return (double) nodeBuffer.getDouble(offset);
            case 8: return (double) nodeBuffer.getLong(offset);
            default: break;
        }  
        System.out.println("getValue() :: wrong type for entry : " + name);
        //int offset = nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);        
        return 0.0;
    }
    
    public double getValue(int entry, int row){
        int type = this.nodeSchema.getType(entry);
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(entry, row, nodeRows);
        switch(type){
            case 1: return (double) nodeBuffer.get(offset);
            case 2: return (double) nodeBuffer.getShort(offset);
            case 3: return (double) nodeBuffer.getInt(offset);
            case 4: return (double) nodeBuffer.getFloat(offset);
            case 5: return (double) nodeBuffer.getDouble(offset);
            case 8: return (double) nodeBuffer.getLong(offset);
            default: break;
        }  
        System.out.println("getValue() :: wrong type for entry : " + 
                this.nodeSchema.getElementName(entry));
        //int offset = nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);        
        return 0.0;
    }
    /**
     * Reallocate the bytes and create a new Byte Buffer.
     * @param size 
     */
    private void resize(int size){
        byte[] bytes = new byte[size];
        nodeBuffer   = ByteBuffer.wrap(bytes);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    /**
     * returns number of rows in the bank.
     * @return 
     */
    public int getRows(){
        int rowSize = nodeSchema.getEntryLength();
        int  length = nodeBuffer.getInt(4);
        return length/rowSize;
        //return 
    }
    
    public void  reset(){
        nodeBuffer.putInt(   4, 0);        
    }
        
    public Map<Integer,Integer> getMap(String column){
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        int order = this.nodeSchema.getElementOrder(column);
        int rows  = getRows();
        for(int i = 0; i < rows; i++){
            int value = getInt(order,i);
            map.put(value, i);
        }
        return map;
    }
    
    public Map<Integer, List<Integer>> getMapList(String columnKey, String columnList){
        Map<Integer, List<Integer>> map = new HashMap<>();
        int orderKey = nodeSchema.getElementOrder(columnKey);
        int orderList = nodeSchema.getElementOrder(columnList);
        int rows  = getRows();
        for(int i = 0; i < rows; i++){
            int key = getInt(orderKey,i);
            if(map.containsKey(key)==false) map.put(key, new ArrayList<Integer>());
            int value = getInt(orderList,i);
            map.get(key).add(value);
        }
        return map;
    }
    
    public final Schema getSchema(){ return nodeSchema; }
    
    public byte[] getByte(String name){
        int nrows = this.getRows();
        byte[] column = new byte[nrows];
        int element = this.getSchema().getEntryOrder(name);
        for(int r = 0; r < nrows; r++) { column[r] = this.getByte(element, r);}
        return column;
    }
    
    public short[] getShort(String name){
        int nrows = this.getRows();
        short[] column = new short[nrows];
        int element = this.getSchema().getEntryOrder(name);
        for(int r = 0; r < nrows; r++) { column[r] = this.getShort(element, r);}
        return column;
    }
    
    public int[] getInt(String name){
        int nrows = this.getRows();
        int[] column = new int[nrows];
        int element = this.getSchema().getEntryOrder(name);
        for(int r = 0; r < nrows; r++) { column[r] = this.getInt(element, r);}
        return column;
    }
    
    
    
    public int getInt(String name, int row){
        int type = this.nodeSchema.getType(name);
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);
        switch(type){
            case 1: return (int) nodeBuffer.get(offset);
            case 2: return (int) nodeBuffer.getShort(offset);
            default: break;
        }  
        if(type!=3){
            this.printWrongType(0,name, "INT", 
                    DataType.getTypeById(type).getName());
        }
        //int offset = nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);        
        return nodeBuffer.getInt(offset);
    }
    
    public long getLong(String name, int row){
        int type = this.nodeSchema.getType(name);
        if(type!=DataType.LONG.getType()) {
            printWrongType(0,name,"Long",DataType.getTypeById(type).getName());
            return 0L;
            //throw new HipoException("The ");
        }
        int offset = nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);
        return nodeBuffer.getLong(offset);
    }
    
    public float getFloat(String name, int row){
        int type = this.nodeSchema.getType(name);
        if(type!=DataType.FLOAT.getType()) {
            printWrongType(0,name,"Float",DataType.getTypeById(type).getName());
            return (float) 0.0;
            //throw new HipoException("The ");
        }
        int offset = nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);
        return nodeBuffer.getFloat(offset);
    }
    
    public double[] getDouble(String name){
        int nrows = this.getRows();
        double[] column = new double[nrows];
        int element = this.getSchema().getEntryOrder(name);
        for(int r = 0; r < nrows; r++) { column[r] = this.getFloat(element, r);}
        return column;
    }
    
    public double getDouble(String name, int row){
        int type = this.nodeSchema.getType(name);
        if(type!=DataType.DOUBLE.getType()) {
            printWrongType(0,name,"Double",DataType.getTypeById(type).getName());
            return 0.0;
            //throw new HipoException("The ");
        }
        int offset = nodeHeaderSize + nodeSchema.getOffset(name, row, nodeRows);
        return nodeBuffer.getDouble(offset);
    }
    
    public int getInt(int element, int index){
        int type = this.nodeSchema.getType(element);
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        switch(type){
            case 1: return (int) nodeBuffer.get(offset);
            case 2: return (int) nodeBuffer.getShort(offset);
            default: break;
        }        
        if(type!=3){
            this.printWrongType(0,nodeSchema.getElementName(element), "INT", 
                    DataType.getTypeById(type).getName());
        }
        return nodeBuffer.getInt(offset);
    }
    
    public Node  getNode(String name, int group, int item){
        int element = nodeSchema.getElementOrder(name);
        return getNode(element,group,item);
    }
    
    public Node  getNode(int element, int group, int item){
        int  rows = getRows();
        int  type = this.nodeSchema.getType(element);
        
        Node node = new Node(group,item, DataType.getTypeById(type),rows);
        switch(type){
            case 1: for(int i = 0; i < rows; i++) node.setByte(   i, getByte(element,i)); break;
            case 2: for(int i = 0; i < rows; i++) node.setShort(  i, getShort(element,i)); break;
            case 3: for(int i = 0; i < rows; i++) node.setInt(    i, getInt(element,i)); break;
            case 4: for(int i = 0; i < rows; i++) node.setFloat(  i, getFloat(element,i)); break;
            case 5: for(int i = 0; i < rows; i++) node.setDouble( i, getDouble(element,i)); break;
            case 8: for(int i = 0; i < rows; i++) node.setLong(   i, getLong(element,i)); break;   
            default: break;
        }
        return node;
    }
    
    public int[] getIntArray(int length, String name, int row){
        int    order = nodeSchema.getElementOrder(name);
        int[] result = new int[length];
        for(int loop = 0; loop < length; loop++)
            result[loop] = this.getInt(order+loop, row);
        return result;
    }
    
    public float[] getFloatArray(int length, String name, int row){
        int    order = nodeSchema.getElementOrder(name);
        float[] result = new float[length];
        for(int loop = 0; loop < length; loop++)
            result[loop] = this.getFloat(order+loop, row);
        return result;
    }
    
    public float getFloat(int element, int index){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        return nodeBuffer.getFloat(offset);
    }
    
    public double getDouble(int element, int index){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        return nodeBuffer.getDouble(offset);
    }
    
    public short getShort(int element, int index){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        return nodeBuffer.getShort(offset);
    }
    
    public short getShort(String name, int index){
        int type   = nodeSchema.getType(name);
        if(type!=DataType.SHORT.getType()) {
            printWrongType(0,name,"Int",DataType.getTypeById(type).getName());
            return (short) 0;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        return nodeBuffer.getShort(offset);
    }
    public byte getByte(int element, int index){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        return nodeBuffer.get(offset);
    }
    public byte getByte(String name, int index){
        int type = this.nodeSchema.getType(name);
        if(type!=DataType.BYTE.getType()) {
            printWrongType(0,name,"Byte",DataType.getTypeById(type).getName());
            return (byte) 0;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        return nodeBuffer.get(offset);
    }
    public long getLong(int element, int index){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        return nodeBuffer.getLong(offset);
    }
    
    public void putInt(int element, int index, int value){      
        
        int type = this.nodeSchema.getType(element);
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        if(type==3){
            nodeBuffer.putInt(offset, value);
            return;
        }
        
        if(type==1){
            if(value>Byte.MIN_VALUE&&value<Byte.MAX_VALUE){
                nodeBuffer.put(offset,(byte) value);
            } else {
                System.out.printf("bank::error; value [%d] exceeds bounds for byte\n",value);
            }
            return;
        }
        
        if(type==2){
            if(value>Short.MIN_VALUE&&value<Short.MAX_VALUE){
                nodeBuffer.putShort(offset,(short) value);
            } else {
                System.out.printf("bank::putInt::error; value [%d] exceeds bounds for short\n",value);
            }
            return;
        }
        
        System.out.printf("bank::putInt::error; for type = %d, value = %d\n",type,value);
        //int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        //nodeBuffer.putInt(offset, value);        
    }
    
    private void printWrongType(int status, String name, String expected, String passed){
        String method = "GET";
        if(status>0) method = "PUT";
        System.out.println("[bank] Error (" + method + ") :: ( " + nodeSchema.getName() + 
                ") the entry " + name + 
                " has type " + expected + " while requested " + passed  
        );
    }
    /**
     * Copies the content of current bank to the passed bank.
     * Only the entries, which names appear in both banks
     * are copied. The rows is set to the rows of current bank
     * @param bank output bank 
     */
    public void copyTo(Bank bank){
        
        int nentries = nodeSchema.getElements();
        int nrows    = getRows();
        bank.setRows(nrows);
        
        Schema toSchema = bank.getSchema();
        for(int i = 0; i < nentries; i++){
            String name = nodeSchema.getElementName(i);
            if(toSchema.hasEntry(name)==true){
                int order = toSchema.getElementOrder(name);
                int type  = toSchema.getType(order);
                switch(type){
                    case 1: for(int row = 0; row < nrows; row++) 
                        bank.putByte(order, row, getByte(i,row)); break;
                    
                    case 2: for(int row = 0; row < nrows; row++) 
                        bank.putShort(order, row, getShort(i,row)); break;
                        
                    case 3: for(int row = 0; row < nrows; row++) 
                        bank.putInt(order, row, getInt(i,row)); break;
                        
                    case 4: for(int row = 0; row < nrows; row++) 
                        bank.putFloat(order, row, getFloat(i,row)); break;
                        
                    case 5: for(int row = 0; row < nrows; row++) 
                        bank.putDouble(order, row, getDouble(i,row)); break;
                        
                    case 8: for(int row = 0; row < nrows; row++) 
                        bank.putLong(order, row, getLong(i,row)); break;
                        
                    default: break;
                }                
            }
        }
    }
    
    public void copyTo(Bank bank, int srcRow, int dstRow){
        
        int nentries = nodeSchema.getElements();
        int nrows    = getRows();
        //bank.setRows(nrows);
        
        Schema toSchema = bank.getSchema();
        for(int i = 0; i < nentries; i++){
            String name = nodeSchema.getElementName(i);
            if(toSchema.hasEntry(name)==true){
                int order = toSchema.getElementOrder(name);
                int type  = toSchema.getType(order);
                switch(type){
                    case 1:  
                        bank.putByte(order, dstRow, getByte(i,srcRow)); break;
                    
                    case 2:  
                        bank.putShort(order, dstRow, getShort(i,srcRow)); break;
                        
                    case 3:  
                        bank.putInt(order, dstRow, getInt(i,srcRow)); break;
                        
                    case 4: 
                        bank.putFloat(order, dstRow, getFloat(i,srcRow)); break;
                        
                    case 5:  
                        bank.putDouble(order, dstRow, getDouble(i,srcRow)); break;
                        
                    case 8: 
                        bank.putLong(order, dstRow, getLong(i,srcRow)); break;
                        
                    default: break;
                }                
            }
        }
    }
    
    public void putInt(String name, int index, int value){
        
        int order = nodeSchema.getElementOrder(name);
        this.putInt(order, index, value);
        /*
        int type   = nodeSchema.getType(name);
        if(type!=DataType.INT.getType()) {
            printWrongType(1,name,"Int",DataType.getTypeById(type).getName());
            return;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        nodeBuffer.putInt(offset, value);*/
    }
    
    public void putLong(int element, int index, long value){
        
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        nodeBuffer.putLong(offset, value);        
    }
    public void putLong(String name, int index, long value){
        int type   = nodeSchema.getType(name);
        if(type!=DataType.LONG.getType()) {
            printWrongType(1,name,"Long",DataType.getTypeById(type).getName());
            return;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        nodeBuffer.putLong(offset, value);        
    }
    
    public void putShort(int element, int index, short value){        
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        nodeBuffer.putShort(offset, value);        
    }
    
    public void putShort(String name, int index, short value){
        int type   = nodeSchema.getType(name);
        if(type!=DataType.SHORT.getType()) {
            printWrongType(1,name,"Short",DataType.getTypeById(type).getName());
            return;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        nodeBuffer.putShort(offset, value);        
    }
    
    public void putByte(int element, int index, byte value){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        nodeBuffer.put(offset, value);
    }

    public void putByte(String name, int index, byte value){
        int type   = nodeSchema.getType(name);
        if(type!=DataType.BYTE.getType()) {
            printWrongType(1,name,"Byte",DataType.getTypeById(type).getName());
            return;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        nodeBuffer.put(offset, value);
    }
    
    public void putFloat(int element, int index, float value){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        nodeBuffer.putFloat(offset, value);        
    }
    
    public void putDouble(int element, int index, double value){
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(element, index, nodeRows);
        nodeBuffer.putDouble(offset, value);        
    }
    
    public void putFloat(String name, int index, float value){
        int type   = nodeSchema.getType(name);
        if(type!=DataType.FLOAT.getType()) {
            printWrongType(1,name,"Float",DataType.getTypeById(type).getName());
            return;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        nodeBuffer.putFloat(offset, value);        
    }
    
    public void putDouble(String name, int index, double value){
        int type   = nodeSchema.getType(name);
        if(type!=DataType.DOUBLE.getType()) {
            printWrongType(1,name,"Double",DataType.getTypeById(type).getName());
            return;
            //throw new HipoException("The ");
        }
        int offset = this.nodeHeaderSize + nodeSchema.getOffset(name, index, nodeRows);
        nodeBuffer.putDouble(offset, value);        
    }
    
    protected ByteBuffer getByteBuffer(){
        return this.nodeBuffer;
    }
    /**
     * returns the string showing the content of the node
     * @return string for printout
     */
    public String nodeString(){
        StringBuilder str = new StringBuilder();
        int nelements = nodeSchema.getElements();
        int      rows = getRows();
        for(int i =0; i < nelements; i++){
            str.append(String.format("%14s : ", nodeSchema.getElementName(i)));
            for(int r = 0 ; r < rows; r++){
                int type = nodeSchema.getType(i);
                
                switch(type){
                    case 1: str.append(String.format("%10d", getByte(i,r))); break;
                    case 2: str.append(String.format("%10d", getShort(i,r))); break;
                    default: break;
                } 
                if(DataType.getTypeById(type)==DataType.LONG){
                    str.append(String.format("%10d", getLong(i,r)));
                }
                if(DataType.getTypeById(type)==DataType.INT){
                    str.append(String.format("%10d", getInt(i,r)));
                }
                if(DataType.getTypeById(type) ==DataType.FLOAT){
                    str.append(String.format("%10.4f", getFloat(i,r)));
                } 
                
                if(DataType.getTypeById(type) ==DataType.DOUBLE){
                    str.append(String.format("%10.4f", getDouble(i,r)));
                } 
                
                if((r+1)%10==0){
                    str.append(String.format("\n%14s + ", " "));
                }
            }
            str.append("\n");
        }
        return str.toString();
    }
    
    /**
     * Reduces the bank to contain only rows given by the list
     * of indices.
     * @param index
     * @return new bank with reduced number of rows
     */
    public Bank reduce(List<Integer> index){
        Bank b = new Bank(this.nodeSchema,index.size());
        int     nrows = index.size();

        Schema schema = b.getSchema();
        int  nentries = schema.getElements();
        for(int ir = 0; ir < nrows; ir++){
            int sr = index.get(ir);
            for(int ei = 0; ei < nentries; ei++){
                int type = schema.getType(ei);
                //System.out.println(" count = " + nrows + " ir = " + ir + " sr = " + sr + " ei = " + ei);                                                              
                switch(type){
                    case 1: b.putByte(   ei, ir, this.getByte(  ei, sr)); break;
                    case 2: b.putShort(  ei, ir, this.getShort( ei, sr)); break;
                    case 3: b.putInt(    ei, ir, this.getInt(   ei, sr)); break;
                    case 4: b.putFloat(  ei, ir, this.getFloat( ei, sr)); break;
                    case 5: b.putDouble( ei, ir, this.getDouble(ei, sr)); break;
                    case 8: b.putLong(   ei, ir, this.getLong(  ei, sr)); break;
                    default: System.out.println("getReducedBank:: error : type = "
                    + type + " is unknown (prety much to anyone)");
                }
            }
        }
        return b;
    }
    
    public String nodeString(String[] items){
        StringBuilder str = new StringBuilder();
        int      rows = getRows();
        for(int i = 0; i < rows; i++){
            for(int k = 0; k < items.length; k++){
                int type = nodeSchema.getType(items[k]);
                DataType dtype = DataType.getTypeById(type);
                if(dtype==DataType.INT||dtype==DataType.BYTE||dtype==DataType.SHORT){
                    str.append(String.format("%10d ", getInt(items[k],i)));                    
                }
                
                if(dtype==DataType.FLOAT){
                    str.append(String.format("%10.4f ", getFloat(items[k],i)));                    
                }
                
                if(dtype==DataType.DOUBLE){
                    str.append(String.format("%10.4f ", getDouble(items[k],i)));                    
                }
                
                if(dtype==DataType.LONG){
                    str.append(String.format("%10d ", getLong(items[k],i)));                    
                }
            }
            str.append("\n");
        }
        return str.toString();
    }
    
    public String bankString(){
        int nRows = this.getRows();
        StringBuilder str = new StringBuilder();
        str.append(String.format("%d,%d,%s,%d,%d\n",
                nodeSchema.getElements(),
                nRows,nodeSchema.getName(),
                nodeSchema.getGroup(),nodeSchema.getItem()));
        str.append(nodeStringData());
        return str.toString();
    }
    
    public String nodeStringData(){
        StringBuilder str = new StringBuilder();
        int nelements = nodeSchema.getElements();
        int      rows = getRows();
        for(int i =0; i < nelements; i++){
            str.append(String.format("%s: ", nodeSchema.getElementName(i)));
            for(int r = 0 ; r < rows; r++){
                int type = nodeSchema.getType(i);
                if(r!=0) str.append(",");
                switch(type){
                    case 1: str.append(String.format("%d", getByte(i,r))); break;
                    case 2: str.append(String.format("%d", getShort(i,r))); break;
                    default: break;
                }
                
                if(DataType.getTypeById(type)==DataType.LONG){
                    str.append(String.format("%d", getLong(i,r)));
                }
                if(DataType.getTypeById(type)==DataType.INT){
                    str.append(String.format("%d", getInt(i,r)));
                }
                if(DataType.getTypeById(type) ==DataType.FLOAT){
                    str.append(String.format("%.4f", getFloat(i,r)));
                } 
                
                if(DataType.getTypeById(type) ==DataType.DOUBLE){
                    str.append(String.format("%.4f", getDouble(i,r)));
                } 
                
                /*if((r+1)%10==0){
                    str.append(String.format("\n%14s + ", " "));
                }*/
            }
            str.append("\n");
        }
        return str.toString();

    }
    
    public short getNodeGroup(){
        return nodeBuffer.getShort(0);
    }
    
    public short getNodeItem(){
        return (short) nodeBuffer.get(2);
    }
    
    public int getNodeLength(){
        return nodeBuffer.getInt(4);
    }
    
    public int getNodeBufferLength(){
        return 8+nodeBuffer.getInt(4);
    }
    
    public short getNodeType(){
        return (short) nodeBuffer.get(3);
    }
    
    public String headerString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("* NODE * group = %6d, item = %3d, type = %2d, size = %8d", 
                getNodeGroup(),getNodeItem(),getNodeType(),getNodeLength()));
        return str.toString();
    }
    
    public void show(){
        System.out.println(headerString());
        System.out.println(nodeString());
    }
    
    public void show(String[] items){
        System.out.println(nodeString(items));
    }
    
    public void show(String itemsList){
        String[] tokens = itemsList.split(":");
        System.out.println(nodeString(tokens));
    }
    
    public String getSummary(){
        return String.format(" %44s | %8d | %8d | %8d | %8d ",
                                    nodeSchema.getName(), nodeSchema.getGroup(),nodeSchema.getItem(),getRows(), this.getNodeBufferLength());
    }
    
    public void setByte(String sector, int i, byte b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args){
        SchemaBuilder schb = new SchemaBuilder("ai:clusters",1200,1);
        
        schb.addEntry("sector", "S", "")
                .addEntry("layer","S","")
                .addEntry("mean","F","")
                .addEntry("id", "S", "");
        
        Schema sch = schb.build();
        
        Bank bank = new Bank(sch,15);
        bank.show();
        
        System.out.println(bank.nodeStringData());
        
        System.out.println(bank.bankString());
        System.out.println(bank.bankString());
    }
}
