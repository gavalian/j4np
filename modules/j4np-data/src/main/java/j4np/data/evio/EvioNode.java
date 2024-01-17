/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.evio;

import j4np.data.base.DataNode;
import j4np.data.structure.BaseStructure;
import java.nio.ByteBuffer;

/**
 *
 * @author gavalian
 */
public class EvioNode extends BaseStructure implements DataNode {

    public EvioNode(){
        super(512);
        BUFFERSIZE_POSITION = 0;
        IDENTIFIER_POSITION = 4;
        DATA_POSITION       = 8;
    }
    
    public EvioNode(int size){
        super(size);
        BUFFERSIZE_POSITION = 0;
        IDENTIFIER_POSITION = 4;
        DATA_POSITION       = 8;
    }
    
    @Override
    public ByteBuffer getBuffer() {
        return this.structBuffer;
    }

    @Override
    public int bufferLength() {
        return this.structBuffer.getInt(this.BUFFERSIZE_POSITION);        
    }

    @Override
    public boolean allocate(int size) {
        this.require(size); return true;
    }

    @Override
    public int identifier() {
        return this.structBuffer.getInt(this.IDENTIFIER_POSITION);
    }

    @Override
    public boolean verify() {
        return true;
    }

    @Override
    public int getType() {
        int identifier = identifier();
        return EvioDataUtils.decodeType(identifier);
    }
    
    @Override
    public int count() {
        int length = this.structBuffer.getInt(this.BUFFERSIZE_POSITION)*4 - 4;
        int type   = getType();
        int typeLength = 4;
        return (int) (length/typeLength);
    }

    @Override
    public String format() {
        //int size = this.bufferLength()*4;
        int datapos = this.getDataPosition();
        byte[] str = new byte[datapos-8];
        for(int i = 0; i < str.length; i++) str[i] = this.structBuffer.get(i+8);
        return new String(str);
    }
    
    public void show(){
        int id = identifier();
        System.out.printf("node : (%6d , %4d)  type = %4d , count = %4d\n",
                EvioDataUtils.decodeTag(id),
                EvioDataUtils.decodeNum(id),
                EvioDataUtils.decodeType(id),
                count()
                );
    }

    public void show(int count){
        for(int i = 0; i < count; i++){
            System.out.printf("%3d : %08X ", i*4, this.structBuffer.getInt(i*4));
        }
        System.out.println();
    }
    
    public int getDataPosition(){
        int pos = 8;
        int length = this.bufferLength()*4;
        while(true){
            int trail = this.structBuffer.getInt(pos);
            if((trail&0x0000FFFF)==0x00000400) break;
            pos+=4;
            if(pos>=length-3) break;
        }
        return pos;
    }
    
    @Override
    public double getDouble(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    public int getInt(int index) {
        return this.structBuffer.getInt(index*4+8);
    }

    public int getIntAt(int index) {
        return this.structBuffer.getInt(index+8);
    }

    public long getLong(int index) {
        return this.structBuffer.getLong(index*8+8);
    }
    
    public long getLongAt(int index) {
        return this.structBuffer.getLong(index+8);
    }
    
    public byte getByteAt(int index) {
        return this.structBuffer.get(index+8);
    }
    
    public short getShortAt(int index) {
        return this.structBuffer.getShort(index+8);
    }
    
    public void setInt(int index, int value) {
         this.structBuffer.putInt(index*4+8,value);
    }
    
    @Override
    public double getDouble(int order, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getInt(int order, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
