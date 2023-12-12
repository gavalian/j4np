/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author gavalian
 */
public class Node {
    
    ByteBuffer   nodeBuffer   = null;
    DataType     nodeType     = null;
    
    /**
     * Description of header bytes order.
     */
    private final int            headerLength = 8;
    private final int  headerLengthDataOffset = 6;
    private final int     hederTypeDataOffset = 2;
    
    protected Node(){
        
    }
    
    protected Node(DataType type){
        nodeType = type;
    }
    
    public Node(int group, int item, DataType type, int length){
        createNode(group, item, type, length);
    }
    
    public Node(int group, int item, String value){
        createNode(group,item,value);
    }
        
    public Node(int group, int item, float[] value){
        createNode(group,item,DataType.FLOAT,value.length);
        for(int i = 0; i < value.length;i++) this.setFloat(i, value[i]);        
    }
    
    public Node(int group, int item, double[] value){
        createNode(group,item,DataType.DOUBLE,value.length);
        for(int i = 0; i < value.length;i++) this.setDouble(i, value[i]);        
    }
    
    public Node(int group, int item, int[] value){
        createNode(group,item,DataType.INT,value.length);
        for(int i = 0; i < value.length;i++) this.setInt(i, value[i]);        
    }
    
    public Node(int group, int item, long[] value){
        createNode(group,item,DataType.LONG,value.length);
        for(int i = 0; i < value.length;i++) this.setLong(i, value[i]);        
    }
    
    public Node(int group, int item, byte[] value){
        createNode(group,item,DataType.BYTE,value.length);
        for(int i = 0; i < value.length;i++) this.setByte(i, value[i]);        
    }
    
    public Node(int group, int item, short[] value){
        createNode(group,item,DataType.SHORT,value.length);
        for(int i = 0; i < value.length;i++) this.setInt(i, value[i]);        
    }
    /**
     * Initialize HipoNode from a byte array. 
     * @param buffer byte array with the data
     */
    public Node(byte[] buffer){
        nodeBuffer = ByteBuffer.wrap(buffer);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        nodeType = getType();
    }
    
    protected void allocate(int length){
        byte[] nodeBytes = new byte[length];
        nodeBuffer = ByteBuffer.wrap(nodeBytes);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        nodeType = DataType.UNDEFINED;
    }
    protected void allocate(int length,DataType type){
        byte[] nodeBytes = new byte[length];
        nodeBuffer = ByteBuffer.wrap(nodeBytes);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        nodeType = type;
    }
    public final void createNode(int group, int item, String value){
        byte[] array = value.getBytes();
        createNode(group,item,DataType.STRING,array.length);
        System.arraycopy(array, 0, nodeBuffer.array(), 8, array.length);
        //for(int i = 0; i < array.length; i++) { setByte(i,array[i]);}
    }
    /**
     * Creates a node for given type
     * @param group group id
     * @param item item id
     * @param type type of the node
     * @param length number of elements in the node
     */
    public final void createNode(int group, int item, DataType type, int length){
        int bytesPerEntry = type.getSize(); 
        int totalLength = this.headerLength + length*bytesPerEntry;
        byte[] array = new byte[totalLength];
        nodeBuffer   = ByteBuffer.wrap(array);
        nodeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        short groupID  = ByteUtils.getShortFromInt(group);
        int   lengthID = length*bytesPerEntry;
        byte  itemID   = ByteUtils.getByteFromInt(item);
        byte  typeID   = ByteUtils.getByteFromInt(type.getType());
        
        nodeBuffer.putShort( 0,  groupID); // byte 0 and 1 (16 bits) are group ID
        nodeBuffer.put(      2,   itemID); // byte 2 ( 8 bits) is item id
        nodeBuffer.put(      3,   typeID); // byte 3 describes the type 
        nodeBuffer.putInt(   4, lengthID&0x00FFFFFF);
        
        nodeType = getType();
    }
    
    protected void setGroupItem(int group, int item){
        short groupID  = ByteUtils.getShortFromInt(group);
        byte  itemID   = ByteUtils.getByteFromInt(item);
        nodeBuffer.putShort( 0,  groupID);
        nodeBuffer.put(      2,   itemID);
    }
    /**
     * returns number of elements in the data array. This is not the buffer length.
     * @return n elements of the array if the node is a primitive data.
     */
    public int getDataSize(){
        DataType   type = getType();
        int    bufferLength = nodeBuffer.getInt(4)&0x00FFFFFF;
        int           ndata = bufferLength/type.getSize();
        return ndata;
    }
    /**
     * returns the size of the buffer, including the header.
     * @return internal ByteBuffer size
     */
    public int getBufferSize(){
        return nodeBuffer.capacity();
    }
    /**
     * returns the byte[] array of the buffer 
     * @return array of bytes
     */
    public byte[] getBufferData(){
        return nodeBuffer.array();
    }
    /**
     * returns the group id of the node.
     * @return group id
     */
    public int getGroup(){
        short groupid = nodeBuffer.getShort(0);
        return (int) groupid;
    }
    /**
     * returns the item id of the node
     * @return item id
     */
    public int getItem(){
        byte itemid = nodeBuffer.get(2);
        return (int) itemid;
    }
    
    public void show(){
        String dataString = getDataString();
        System.out.print(String.format("id (%4d/%4d) :: type %2d,  size = %4d >>> ",
                this.getGroup(), this.getItem(), nodeType.getType(), this.getDataSize()));
        System.out.println(dataString);
    }
    
    public void show(int count){
        for(int i = 0; i < count; i++){
            System.out.printf("%08X ", this.nodeBuffer.getInt(i*4));
        }
        System.out.println();
    }
    /**
     * returns string representation of the data.
     * @return data string
     */
    public String getDataString(){
        StringBuilder str = new StringBuilder();
        DataType  type = getType();
        if(type==DataType.STRING){
            int size = this.getDataSize();
            byte[] bytes = new byte[size];
            System.arraycopy(this.nodeBuffer.array(), 8, bytes, 0, size);
            return new String(bytes);
        }
        if(type==DataType.BYTE){
            int ndata = getDataSize();
            for(int i = 0; i < ndata; i++){
                str.append(String.format(" %9d", getByte(i)));
            }
        }
        
        if(type==DataType.SHORT){
            int ndata = getDataSize();
            for(int i = 0; i < ndata; i++){
                str.append(String.format(" %9d", getShort(i)));
            }
        }
        
        if(type==DataType.INT){
            int ndata = getDataSize();
            for(int i = 0; i < ndata; i++){
                str.append(String.format(" %9d", getInt(i)));
            }
        }
        if(type==DataType.LONG){
            int ndata = getDataSize();
            for(int i = 0; i < ndata; i++){
                str.append(String.format(" %9d", getLong(i)));
            }
        }
        if(type==DataType.FLOAT){
            int ndata = getDataSize();
            for(int i = 0; i < ndata; i++){
                str.append(String.format(" %9.4f", getFloat(i)));
            }
        }
        
        if(type==DataType.DOUBLE){
            int ndata = getDataSize();
            for(int i = 0; i < ndata; i++){
                str.append(String.format(" %9.4f", getDouble(i)));
            }
        }
        
        if(type==DataType.VECTOR3F){
            int ndata = getDataSize();
            for(int i = 0; i < ndata; i++){
                str.append(String.format("(%.5f,%.5f,%.5f) ", getVectorX(i),getVectorY(i),getVectorZ(i)));
            }
        }
        return str.toString();
    }    
    /**
     * returns a String with header information.
     * @return string representation of the header.
     */
    public String getHeaderString(){
        StringBuilder str = new StringBuilder();
        short group = nodeBuffer.getShort(0);
        byte  item  = nodeBuffer.get(2);
        byte  type  = nodeBuffer.get(3);
        int   len   = nodeBuffer.getInt(4);
        str.append(String.format("(%8d,%4d) <%2d> [%6d]", group,item,type,len));
        return str.toString();
    }
    /**
     * returns type of the elements stored in the buffer.
     * @return 
     */
    public final DataType  getType(){
        int type = (int) nodeBuffer.get(3);
        return DataType.getTypeById(type);
    }
    /**
     * returns offset of the data element in the ByteBuffer.
     * includes the header length and size of element type.
     * @param index index if the element.
     * @return 
     */
    private int getOffset(int index){
        DataType type = getType();
        return this.headerLength + type.getSize()*index;
    }    
    /**
     * returns a String object from the node. Strings are stored 
     * as byte[]. The data is copied into String.
     * @return String representation of byte array.
     */
    public String getString(){
        if(nodeType!=DataType.STRING){
            printWrongTypeMessage(DataType.STRING);
            return "";
        }
        int offset = getOffset(0);
        int length = getDataSize();
        byte[] array = new byte[length];
        System.arraycopy(nodeBuffer.array(), offset, array, 0, length);
        return new String(array);
    }
    
    /**
     * returns a byte element from the array.
     * @param index index of the element
     * @return 
     */
    public byte getByte(int index){
        if(nodeType!=DataType.BYTE){
            printWrongTypeMessage(DataType.BYTE);
            return (byte) 0;
        }
        int offset = getOffset(index);
        return nodeBuffer.get(offset);
    }
    
    public byte[] getByte(){
        byte[] array = new byte[this.getDataSize()];
        System.arraycopy(this.nodeBuffer.array(), headerLength, array, 0, array.length);
        return array;
    }
    /**
     * returns a float array containing the data from the node.
     * needs to be optimized with ArrayCopy !
     * @return float[] array of the node.
     */
    public float[] getFloat(){
        if(nodeType!=DataType.FLOAT){
            printWrongTypeMessage(DataType.FLOAT);
            return new float[0];
        }
        float[] result = new float[this.getDataSize()];
        for(int i = 0; i < result.length; i++) result[i] = getFloat(i);
        return result;
    }
    /**
     * returns a float array containing the data from the node.
     * needs to be optimized with ArrayCopy !
     * @return float[] array of the node.
     */
    public double[] getDouble(){
        if(nodeType!=DataType.DOUBLE){
            printWrongTypeMessage(DataType.DOUBLE);
            return new double[0];
        }
        double[] result = new double[this.getDataSize()];
        for(int i = 0; i < result.length; i++) result[i] = getDouble(i);
        return result;
    }
    /**
     * returns a float number of the element with given index
     * @param index index of the element.
     * @return 
     */
    public float getFloat(int index){
        if(nodeType!=DataType.FLOAT){
            printWrongTypeMessage(DataType.FLOAT);
            return 0.0f;
        }
        int offset = getOffset(index);
        return nodeBuffer.getFloat(offset);
    }
    /*
    public short[] getDataShort(){
        
    }*/
    /**
     * Get X component of the vector from the node with TYPE VECTOR3F
     * @param index index of the vector
     * @return X component
     */
    public float getVectorX(int index){
        if(nodeType!=DataType.VECTOR3F){
            printWrongTypeMessage(DataType.VECTOR3F);
            return (float) 0;
        }
        int start  = getOffset(0);
        int offset = start + (index*nodeType.getSize());
        return nodeBuffer.getFloat(offset);
    }
    /**
     * Get Y component of the vector from the node with TYPE VECTOR3F
     * @param index index of the vector
     * @return Y component
     */    
    public float getVectorY(int index){
        if(nodeType!=DataType.VECTOR3F){
            printWrongTypeMessage(DataType.VECTOR3F);
            return (float) 0;
        }
        int start  = getOffset(0);
        int offset = start + (index*nodeType.getSize()+4);
        return nodeBuffer.getFloat(offset);
    }
    /**
     * Get Z component of the vector from the node with TYPE VECTOR3F
     * @param index index of the vector
     * @return Z component
     */    
    public float getVectorZ(int index){
        if(nodeType!=DataType.VECTOR3F){
            printWrongTypeMessage(DataType.VECTOR3F);
            return (float) 0;
        }
        int start  = getOffset(0);
        int offset = start + (index*nodeType.getSize()+8);
        return nodeBuffer.getFloat(offset);
    }
    /**
     * returns the value of the short array for given index.
     * @param index index of the array
     * @return the value
     */
    public short getShort(int index){
        if(nodeType!=DataType.SHORT){
            printWrongTypeMessage(DataType.SHORT);
            return (short) 0;
        }
        int offset = getOffset(index);
        return nodeBuffer.getShort(offset);
    }
    
    public int getInt(int index){
        if(nodeType!=DataType.BYTE&&nodeType!=DataType.SHORT&&
                nodeType!=DataType.INT){
            printWrongTypeMessage(DataType.INT);
            return 0;
        }

        int offset = getOffset(index);
        
        if(nodeType==DataType.INT){
            return nodeBuffer.getInt(offset);
        }
        
        if(nodeType==DataType.SHORT){
            return (int) nodeBuffer.getShort(offset);
        }
        
        if(nodeType==DataType.BYTE){
            return (int) nodeBuffer.get(offset);
        }
        
        return 0;        
    }
    
    public int[] getInt(){
        if(this.nodeType!=DataType.INT){
            this.printWrongTypeMessage(DataType.INT);
            return new int[0];
        }

        int[] array = new int[this.getDataSize()];
        for(int i = 0; i < array.length; i++) array[i] = this.getInt(i);
        //System.arraycopy(this.nodeBuffer.array(), headerLength, array, 0, array.length*4);
        return array;
    }
    
    public short[] getShort(){
        if(this.nodeType!=DataType.SHORT){
            this.printWrongTypeMessage(DataType.SHORT);
            return new short[0];
        }
        
        short[] array = new short[this.getDataSize()];
        for(int i = 0; i < array.length; i++) array[i] = this.getShort(i);
        /*System.out.println(" copying " + headerLength +  "  " + (array.length*2) +
                "   " + this.nodeBuffer.array().length);
        System.arraycopy(this.nodeBuffer.array(), headerLength, array, 0, array.length);*/
        return array;
    }
    
    public long getLong(int index){
        if(nodeType!=DataType.LONG){
            printWrongTypeMessage(DataType.LONG);
            return 0;
        }
        int offset = getOffset(index);
        return nodeBuffer.getLong(offset);
    }
    
    public double getDouble(int index){
        if(nodeType!=DataType.DOUBLE){
            printWrongTypeMessage(DataType.DOUBLE);
            return 0.0;
        }
        int offset = getOffset(index);
        return nodeBuffer.getDouble(offset);
    }
    /**
     * Set content of the node with type BYTE, for element index
     * @param index element index
     * @param value byte value to set
     */
    public void setByte(int index, byte value){                
        if(nodeType!=DataType.BYTE){
            printWrongTypeMessage(DataType.BYTE);
            return;
        }
        int offset = getOffset(index);
        nodeBuffer.put(offset, value);
    }
    
    public void putByte(byte[] array){
        int capacity = this.nodeBuffer.capacity();
        if(array.length < capacity){
            System.out.println("[node::putByte] error, the byte array is smaller than the node capacity");
            return;
        }
        System.arraycopy(array, 0, nodeBuffer.array(), 8, capacity-8);
    }
    
    public void setShort(int index, short value){
        if(nodeType!=DataType.SHORT){
            printWrongTypeMessage(DataType.SHORT);
            return;
        }
        int offset = getOffset(index);
        nodeBuffer.putShort(offset, value);
    }        
    /**
     * set integer value to the element of the node
     * @param index order of the array
     * @param value value to set
     */
    public final void setInt(int index, int value){

        if(nodeType==DataType.INT){
            int offset = getOffset(index);
            nodeBuffer.putInt(offset, value);
            return;
        }
        
        if(nodeType==DataType.SHORT){
            if(value>Short.MIN_VALUE&&value<Short.MAX_VALUE){
                short short_value = (short) value;
                int offset = getOffset(index);
                nodeBuffer.putShort(offset, short_value);
                return;
            } else {
                System.out.println("[HipoNode::setInt] --> setting int value to short failed. Value is "
                + " out of range. " + value);
                return;
            }
        }
        
        if(nodeType==DataType.BYTE){
            if(value>Byte.MIN_VALUE&&value<Byte.MAX_VALUE){
                byte byte_value = (byte) value;
                int offset = getOffset(index);
                nodeBuffer.putShort(offset, byte_value);
                return;
            } else {
                System.out.println("[HipoNode::setInt] --> setting int value to short failed. Value is "
                + " out of range. " + value);
                return;
            }
        }
        
        printWrongTypeMessage(DataType.INT);
         
        /*if(nodeType!=DataType.INT){
            printWrongTypeMessage(DataType.INT);
            return;
        }*/
        //int offset = getOffset(index);
        //nodeBuffer.putInt(offset, value);
    } 
    
    public void setLong(int index, long value){
        if(nodeType!=DataType.LONG){
            printWrongTypeMessage(DataType.LONG);
            return;
        }
        int offset = getOffset(index);
        nodeBuffer.putLong(offset, value);
    }
    
    public final void setFloat(int index, float value){
        if(nodeType!=DataType.FLOAT){
            printWrongTypeMessage(DataType.FLOAT);
            return;
        }
        int offset = getOffset(index);
        nodeBuffer.putFloat(offset, value);
    }
    
    public final void setDouble(int index, double value){
        if(nodeType!=DataType.DOUBLE){
            printWrongTypeMessage(DataType.DOUBLE);
            return;
        }
        int offset = getOffset(index);
        nodeBuffer.putDouble(offset, value);
    }
    /**
     * sets all components for the vector with index
     * @param index order of the vector
     * @param x x-component of the vector
     * @param y y-component of the vector
     * @param z z-component of the vector
     */
    public void setVector(int index, float x, float y, float z){
        setVectorX(index,x); setVectorY(index,y); setVectorZ(index,z);        
    }
    /**
     * sets X component of the vector with index
     * @param index index of the vector
     * @param value X component value to set
     */
    public void setVectorX(int index, float value){
        if(nodeType!=DataType.VECTOR3F){
            printWrongTypeMessage(DataType.VECTOR3F);
            return;
        }
        int start  = getOffset(0);
        int offset = start + (index*nodeType.getSize());
        nodeBuffer.putFloat(offset,value);
    }
   /**
     * sets X component of the vector with index
     * @param index index of the vector
     * @param value X component value to set
     */
    public void setVectorY(int index, float value){
        if(nodeType!=DataType.VECTOR3F){
            printWrongTypeMessage(DataType.VECTOR3F);
            return;
        }
        int start  = getOffset(0);
        int offset = start + (index*nodeType.getSize()+4);
        nodeBuffer.putFloat(offset,value);
    }
   /**
     * sets X component of the vector with index
     * @param index index of the vector
     * @param value X component value to set
     */
    public void setVectorZ(int index, float value){
        if(nodeType!=DataType.VECTOR3F){
            printWrongTypeMessage(DataType.VECTOR3F);
            return;
        }
        int start  = getOffset(0);
        int offset = start + (index*nodeType.getSize()+8);
        nodeBuffer.putFloat(offset,value);
    }    
    /**
     * prints error message when wrong type is selected for the node.
     * @param type mistaken type
     */
    private void printWrongTypeMessage(DataType type){
        System.out.print("[hipo node] --> error : ");
        System.out.print(String.format("(%d,%d)", getGroup(), getItem()));
        System.out.println(" requested type="+type.getName()+" has type="+nodeType.getName());
    }
    /**
     * main program for tests
     * @param args 
     */
    public static void main(String[] args){
        /*
        HipoNode node = new HipoNode(1200,1,DataType.SHORT,5);
        
        for(int i = 0; i < 5; i++) { node.setShort(i, (short) ((i+1)*2) );}
        System.out.println(node.getHeaderString() + " : " + node.getDataString());
        
        HipoNode nodeF = new HipoNode(1200,1,DataType.FLOAT,8);
        for(int i = 0; i < 8; i++) { nodeF.setFloat(i,  (float)  ((i+1)*2.0+0.5) );}
        System.out.println(nodeF.getHeaderString() + " : " + nodeF.getDataString());
        
        HipoNode nodeString = new HipoNode(20,1,"Histogram");
        
        System.out.println("VALUE = [" + nodeString.getString() + "]");
        */
        /*
        HipoNode node = new HipoNode(300,1,DataType.VECTOR3F,8);
        for(int i = 0; i < node.getDataSize(); i++){
            node.setVector(i, (float) Math.random(),(float) Math.random(), (float) Math.random());            
        }
        System.out.println("DATA SIZE = " + node.getDataSize());
        System.out.println(node.getDataString());
        */
        
        Node node = new Node(300,1,DataType.SHORT,20);
        for(int i = 0; i < 20; i++){
            int value = (i+1)*20;
            node.setInt(i, value);
        }

        System.out.println(node.getDataString());
        
        short[] buff = node.getShort();
        for(int i = 0; i < buff.length ; i++){
            System.out.println( i + " = " + buff[i]);
        }

        Node nodeB = new Node(300,1,DataType.BYTE,20);
        for(int i = 0; i < 20; i++){
            int value = (i+1)*2;
            nodeB.setByte(i, (byte) value);
        }
        byte[] buffb = nodeB.getByte();
        for(int i = 0; i < buffb.length ; i++){
            System.out.println( i + " = " + buffb[i]);
        }
    }
}
