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
public class BaseHipoStructure {
    
    protected ByteBuffer     structBuffer = null;
    protected ByteOrder      bufferOrder  = ByteOrder.LITTLE_ENDIAN;            
    
    private   double    expansionFraction = 0.15;
    /**
     * Index of size element and the first element in the 
     * buffer.
     */

    protected int DEFAULT_BUFFER_SIZE = 1024*4;
    protected int IDENTIFIER_POSITION = 0;
    protected int BUFFERSIZE_POSITION = 4;
    protected int       DATA_POSITION = 16;
    
    
    
    protected int      DATA_LENGTH_OFFSET = 4;
    protected int  DATA_GROUP_TYPE_OFFSET = 0;
    protected int     DATA_BUFFER_PADDING = 24;
    
    public BaseHipoStructure(){
        structBuffer = ByteBuffer.wrap(new byte[this.DEFAULT_BUFFER_SIZE]);
        structBuffer.order(bufferOrder);
    }
    
    public BaseHipoStructure(int group, int item, int type, byte[] data){
        int size = data.length;
        this.createStructureWithSize(size+8+this.DATA_BUFFER_PADDING);
        setGroup(group).setItem(item).setType(type);        
        this.structBuffer.putInt(this.DATA_LENGTH_OFFSET, size&0x00FFFFFF);
        System.arraycopy(data, 0, this.structBuffer.array(), 8, size);
    }
    
    public BaseHipoStructure(int group, int item, int type, String format, byte[] data){
        
        byte[] fmtbytes = format.getBytes();        
        int size = data.length + fmtbytes.length;
        
        int wordLength = (size&0x00FFFFFF)|((fmtbytes.length<<24)&0xFF000000);

        this.createStructureWithSize(size+8+this.DATA_BUFFER_PADDING);
        setGroup(group).setItem(item).setType(type);
        this.structBuffer.putInt(this.DATA_LENGTH_OFFSET, wordLength);
        
        System.arraycopy(data, 0, this.structBuffer.array(), 8 + fmtbytes.length, data.length);
        System.arraycopy(fmtbytes, 0, this.structBuffer.array(), 8, fmtbytes.length);
    }
    
    public BaseHipoStructure(int size){
        structBuffer = ByteBuffer.wrap(new byte[size]);
        structBuffer.order(bufferOrder);
    }
        
    protected final void createStructureWithSize(int size){
        structBuffer = ByteBuffer.wrap(new byte[this.DEFAULT_BUFFER_SIZE]);
        structBuffer.order(bufferOrder);
    }
    
    
    public final void  setFormatAndLength(String format, int dataLenght){
        byte[] fmtbytes = format.getBytes();
        int wordLength = ((dataLenght+fmtbytes.length)&0x00FFFFFF)|((fmtbytes.length<<24)&0xFF000000);
        this.structBuffer.putInt(this.DATA_LENGTH_OFFSET, wordLength);        
        System.arraycopy(fmtbytes, 0, this.structBuffer.array(), 8, fmtbytes.length);
    }
    
    public final BaseHipoStructure setGroup(int group){
        int word = this.structBuffer.getInt(this.DATA_GROUP_TYPE_OFFSET);
        word = (word&0xFFFF0000)|(group&0x0000FFFF);
        this.structBuffer.putInt(this.DATA_GROUP_TYPE_OFFSET, word);
        return this;
    }
    
    public final BaseHipoStructure setItem(int item){
        int word = this.structBuffer.getInt(this.DATA_GROUP_TYPE_OFFSET);
        word = (word&0xFF00FFFF)|((item&0x000000FF)<<16);
        this.structBuffer.putInt(this.DATA_GROUP_TYPE_OFFSET, word);
        return this;
    }
    
    public final BaseHipoStructure setType(int type){
        int word = this.structBuffer.getInt(this.DATA_GROUP_TYPE_OFFSET);
        word = (word&0x00FFFFFF)|((type&0x000000FF)<<24);
        this.structBuffer.putInt(this.DATA_GROUP_TYPE_OFFSET, word); 
        return this;
    }
    
    public final int getGroup(){
        return this.structBuffer.getInt(this.DATA_GROUP_TYPE_OFFSET)&0x0000FFFF;
    }
    
    public final int getItem(){
        return (this.structBuffer.getInt(this.DATA_GROUP_TYPE_OFFSET)>>16)&0x000000FF;
    }
    
    public final int getType(){
        return (this.structBuffer.getInt(this.DATA_GROUP_TYPE_OFFSET)>>24)&0x000000FF;
    }
    
    public final int getLength(){
        return this.structBuffer.getInt(this.DATA_LENGTH_OFFSET)&0x00FFFFFF;
    }
    
    public final int getHeaderLength(){
        return this.structBuffer.getInt(this.DATA_LENGTH_OFFSET)>>24&0x000000FF;
    }
    
    public final int getDataOffset(){
        return (getHeaderLength() + 8);
    }
    
    public final int getDataLength(){
        return getLength()-getHeaderLength();
    }
    
    public BaseHipoStructure setOrder(ByteOrder order){
        bufferOrder = order;
        structBuffer.order(order);
        return this;
    }
    
    /*
    public int getDataOffset(){
        return this.DATA_POSITION;
    }*/
    
    public void require(int nbytes){
        if(structBuffer.capacity()<nbytes){
            double sizeWithFraction = nbytes + nbytes*this.expansionFraction;
            int    size = (int) sizeWithFraction;
            /*System.out.printf("data buffer expansion : require = %8d, size set = %8d\n",
                    nbytes,size);*/
            structBuffer = ByteBuffer.wrap(new byte[size]);
            structBuffer.order(bufferOrder);
        }
    }
    
    protected ByteBuffer getByteBuffer(){
        return structBuffer;
    }
    
    public int getCapacity(){
        return structBuffer.capacity();
    }
    
    public BaseHipoStructure setIdentifier(int identifier){
        structBuffer.putInt(IDENTIFIER_POSITION, identifier); return this;
    }
    
    public BaseHipoStructure setSize(int size){
        
        int hdrSize = this.getHeaderLength();
        int   group = this.getGroup();
        int    item = this.getItem();
        int    type = this.getType();
        this.require(hdrSize+8+size+this.DATA_BUFFER_PADDING);
        this.setGroup(group).setItem(item).setType(type);
        
        structBuffer.putInt(BUFFERSIZE_POSITION, size); 
        return this;
    }
    
    public int getSize(){
        return structBuffer.getInt(this.DATA_LENGTH_OFFSET)&0x00FFFFFF;
    }
    
    public int getIdentifier(){
        return structBuffer.getInt(IDENTIFIER_POSITION);
    }
    
    public void info(){
        System.out.printf("[base]>> group = %4d, item = %4d, type = %4d, length = %5d, header = %5d, data %5d\n",
                this.getGroup(),this.getItem(), this.getType(), this.getLength(), this.getHeaderLength(), this.getDataLength());
        
        /*
        System.out.printf("[base] type = %08X, size = %6d, capacity = %6d\n",
                structBuffer.getInt(this.IDENTIFIER_POSITION),
                getSize(), getCapacity()
                );*/
    }
    
    public static void main(String[] args){
        
        BaseHipoStructure struct = new BaseHipoStructure(145,12,11,"ciss", new byte[]{5,6,7,8,9,12,14});
        
                
        struct.info();
        
        struct.setGroup(1245);
        struct.setItem(23);
        struct.setType(11);
        
        struct.info();
        
        /*
        struct.setIdentifier(0xC0DA).setSize(1024);
        struct.info();
        struct.require(4100);
        struct.info();
        */
    }
}
