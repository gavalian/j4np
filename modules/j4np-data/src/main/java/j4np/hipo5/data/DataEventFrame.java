/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @date 10/30/2018
 * @author gavalian
 */
public class DataEventFrame {
    /*
    * main buffer that keeps the Data frame events indexed
    * in the lower bytes (ints) of the buffer.
    */
    private ByteBuffer dataFrameBuffer = null;
    /**
     * Define variables that are used to write properties
     * of the data frame.
     */
    public static final int         HEADER_SIZE   = 16;
    protected  final int    dataFrameSizePointer  = 4;    
    protected  final int    dataFrameCountPointer = 12;
    protected  final int  dataFrameSizeCompressedPointer = 8;

    /**
     * This is the signature string, represented by single Integer
     * that will mark the beginning of each data chunk that is recorded
     * in the file.
     */
    private    final int  dataFrameSignature    = 0x46445048; // represents "HPDF"
    //private    final int  dataFrameSignature    = 0x66647068; // represents "hpdf"
    
    public DataEventFrame(ByteBuffer buffer){
        dataFrameBuffer = buffer;
    }
    
    public DataEventFrame(int sizeBytes){
        allocate(sizeBytes);
    }
    /**
     * Allocate the buffer with given size.
     * @param size 
     */
    private void allocate(int size){
        byte[] bytes = new byte[size];
        dataFrameBuffer = ByteBuffer.wrap(bytes);
        dataFrameBuffer.order(ByteOrder.LITTLE_ENDIAN);
        dataFrameBuffer.putInt(0, this.dataFrameSignature);
        dataFrameBuffer.putInt(dataFrameSizePointer, HEADER_SIZE);
        dataFrameBuffer.putInt(dataFrameSizeCompressedPointer, HEADER_SIZE);
        dataFrameBuffer.putInt(dataFrameCountPointer, 0);
    }
    /**
     * calculates the buffer size for given size of event 
     * buffer and number of entries in the event buffer.
     * @param entries number of events in the buffer
     * @param bufferSize the size of the buffer holding events
     * @return 
     */
    public static int sizeOf(int entries, int bufferSize){
        int size = HEADER_SIZE + entries*4 + bufferSize;
        return size;
    }
    
    public int  getEntries(){
        return dataFrameBuffer.getInt(dataFrameCountPointer);
    }
    
    protected int getDataOffset(){
        int count = getEntries();
        return count*4 + DataEventFrame.HEADER_SIZE;
    }
    
    public int getEventPosition(int index){
        int dataOffset = getDataOffset();
        if(index==0){
            return dataOffset;
        }
        return dataOffset + dataFrameBuffer.getInt(DataEventFrame.HEADER_SIZE + (index-1)*4);
    }
    
    public int getEventLength(int index){
        int dataOffset = getDataOffset();
        if(index==0) return dataFrameBuffer.getInt(DataEventFrame.HEADER_SIZE);
        return  (
                dataFrameBuffer.getInt(DataEventFrame.HEADER_SIZE + index*4) 
                - dataFrameBuffer.getInt(DataEventFrame.HEADER_SIZE + (index-1)*4)
                );
    }
    
    public byte[]    getEventCopy(int index){
        int position = getEventPosition(index);
        int length   = getEventLength(index);
        byte[]  result = new byte[length];
        System.arraycopy(dataFrameBuffer.array(), position, result, 0, length);
        return result;
    }
    
    public ByteBuffer  getEvent(int index){
        int position = getEventPosition(index);
        int length   = getEventLength(index);
        ByteBuffer buffer = ByteBuffer.wrap(dataFrameBuffer.array(), position, length);
        return buffer;
    }
    
    public void getEvent(ByteBuffer buffer, int index){
        int position = getEventPosition(index);
        int length   = getEventLength(index);
        System.arraycopy(dataFrameBuffer.array(), position, buffer.array(), 0, length);
    }
    
    public int getEventBufferSize(){
        return dataFrameBuffer.getInt(dataFrameSizePointer);
    }
    
    public void show(){
        int count = getEntries();
        int bufferSize = getEventBufferSize();
        System.out.println(
                String.format(" DF : entries = %8d, size = %8d", count, bufferSize));
    }
    
    public ByteBuffer getFrameBuffer(){ return dataFrameBuffer;}
    
    public void compress(DataFrameBuilder.DataFrameBuffer dfb, int compressionType){
        
    }
        
    protected void writeIndexArray(byte[] bytes, int offset, int length){
        
    }
    
    protected void writeEventBuffer(byte[] bytes, int offset, int length){
        
    }
}
