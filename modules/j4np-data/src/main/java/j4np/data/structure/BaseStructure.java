/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.structure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author gavalian
 */
public class BaseStructure {
    
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
    
    public BaseStructure(){
        structBuffer = ByteBuffer.wrap(new byte[this.DEFAULT_BUFFER_SIZE]);
        structBuffer.order(bufferOrder);
    }
    
    public BaseStructure(int size){
        structBuffer = ByteBuffer.wrap(new byte[size]);
        structBuffer.order(bufferOrder);
    }
    
    public BaseStructure setOrder(ByteOrder order){
        bufferOrder = order;
        structBuffer.order(order);
        return this;
    }
    
    public int getDataOffset(){
        return this.DATA_POSITION;
    }
    
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
    
    public BaseStructure setIdentifier(int identifier){
        structBuffer.putInt(IDENTIFIER_POSITION, identifier); return this;
    }
    
    public BaseStructure setSize(int size){
        structBuffer.putInt(BUFFERSIZE_POSITION, size); return this;
    }
    
    public int getSize(){
        return structBuffer.getInt(BUFFERSIZE_POSITION);
    }
    
    public int getIdentifier(){
        return structBuffer.getInt(IDENTIFIER_POSITION);
    }
    
    public void info(){
        System.out.printf("[base] type = %08X, size = %6d, capacity = %6d\n",
                structBuffer.getInt(this.IDENTIFIER_POSITION),
                getSize(), getCapacity()
                );
    }
    
    public static void main(String[] args){
        BaseStructure struct = new BaseStructure();
        struct.setIdentifier(0xC0DA).setSize(1024);
        struct.info();
        struct.require(4100);
        struct.info();
    }
}
