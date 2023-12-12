/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.evio;

import j4np.data.base.DataEvent;
import j4np.data.base.DataUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author gavalian
 */
public class EvioBlock {
    
    private long      blockFilePosition = 0L;
    protected byte[]        blockBuffer = new byte[2];
    private ByteBuffer  blockByteBuffer = null;
    private ByteOrder    blockByteOrder = ByteOrder.LITTLE_ENDIAN;
    private int                position = 0;
    private int          structureIndex = 0;
    
    public EvioBlock(){
        allocate(1024*1024);
    }
    
    public EvioBlock(int sizeInBytes){
        allocate(sizeInBytes);
    }
    
    protected final void allocate(int size){
        if(blockBuffer.length<size){
            blockBuffer = new byte[size+1024];
            blockByteBuffer = ByteBuffer.wrap(blockBuffer);
            blockByteBuffer.order(blockByteOrder);
        }
    }
    
    public final void init(byte[] reference, int start, int length){
        this.allocate(length);
        System.arraycopy(reference, start, blockBuffer, 0, length);
        position = 32;
    }
    
    protected void scan(){
        structureIndex = 32;
    }
    
    public boolean hasNext(){
        int blockLength = blockByteBuffer.getInt(0)*4;
        int itemLength  = blockByteBuffer.getInt(structureIndex)*4;
        if(structureIndex + itemLength < blockLength) return true;
        return false;
    }
    
    public boolean moveNext(){
        int blockLength = blockByteBuffer.getInt(0)*4;
        int itemLength  = blockByteBuffer.getInt(structureIndex)*4;
        //System.out.printf(" local position = %8d block length = %8d\n",
        //        structureIndex,blockLength);
        structureIndex += itemLength + 4;
        return true;
    }
    
    public void  getEvent(EvioEvent event){
        int eventLength = blockByteBuffer.getInt(structureIndex)*4 + 4;
        event.require(eventLength);
        System.arraycopy(blockBuffer, structureIndex, 
                event.getBuffer().array(), 0, eventLength);
    }
    
    public final void setPosition(long pos){
        blockFilePosition = pos;
    }
    
    public long nextPosition(){
        int length = blockByteBuffer.getInt(0);
        return (blockFilePosition + length*4);
    }
    
    public int nextLength(){
        int length = blockByteBuffer.getInt(0);
        int nextLength = blockByteBuffer.getInt(length*4);
        return nextLength*4;
    }
    
    public void show(){
        //String data = DataUtils.getStringArray(blockByteBuffer, 10,20);
        /*System.out.printf("block : position = %14d, length = %8d, next pos = %12d, next length = %8d\n",
                this.blockFilePosition,blockByteBuffer.getInt(0),
                nextPosition(),  nextLength());*/
        //System.out.printf("block : \n%s\n",data);        
    }
}
