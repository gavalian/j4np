/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.evio;

import j4np.data.base.DataEvent;
import j4np.data.base.DataNode;
import j4np.data.base.DataNodeCallback;
import j4np.data.structure.BaseStructure;
import java.nio.ByteBuffer;

/**
 *
 * @author gavalian
 */
public class EvioEvent extends BaseStructure implements DataEvent {

    private DataNodeCallback callback = null;

    public EvioEvent(){
        super(10*1024);
        IDENTIFIER_POSITION = 4;
        DATA_POSITION = 8;
        BUFFERSIZE_POSITION = 0;
    }




    
    public void setCallback(DataNodeCallback cb){
        this.callback = cb;
    }
    
    public void scan(){
        
        int  eventSize = getByteBuffer().getInt(0)*4;
        int   identity = getByteBuffer().getInt(4);
        
        /*System.out.printf("event : size = %8d (%5d) , identity = (%5d, %4d), type = %4d\n",
                eventSize, eventSize/4,
                EvioDataUtils.decodeTag(identity),
                EvioDataUtils.decodeNum(identity),
                EvioDataUtils.decodeType(identity)
                );
        */
        int position = 8;
        int nodeSize = 0;
        
        while(position+4<eventSize){
            
            nodeSize = getByteBuffer().getInt(position);
            identity = getByteBuffer().getInt(position+4);
            
            int type = EvioDataUtils.decodeType(identity);
            int tag  = EvioDataUtils.decodeTag(identity);
            int num  = EvioDataUtils.decodeNum(identity);
            
            /*System.out.printf("\tnode : size = %9d , identity = (%5d, %4d), type = %4d , pos = %6d\n",
                    nodeSize,
                    EvioDataUtils.decodeTag(identity),
                    EvioDataUtils.decodeNum(identity),
                    EvioDataUtils.decodeType(identity),position
            );*/
            
            if(type==14) scanBank(position,tag,num);
            
            position += nodeSize*4 + 4;
        }
    }
    
    public void scanBank(int pos, int tag, int num){
        int[] iden = new int[4];
        iden[0] = tag;
        iden[1] = num;
        
        int   bankSize = getByteBuffer().getInt(pos)*4;
        int   identity = getByteBuffer().getInt(pos+4);
        int   bankEndPosition = pos + bankSize;
        /*System.out.printf("\t\t bank : size = %8d (%5d) , identity = (%5d, %4d), type = %4d, pos = %6d\n",
                bankSize, bankSize/4,
                EvioDataUtils.decodeTag(identity),
                EvioDataUtils.decodeNum(identity),
                EvioDataUtils.decodeType(identity), pos
                );
        */
        int position = pos+8;
        int nodeSize = 0;
        
        while(position+4< bankEndPosition){
            
            nodeSize = getByteBuffer().getInt(position);
            identity = getByteBuffer().getInt(position+4);
            
            int tag_n = EvioDataUtils.decodeTag(identity);
            int num_n = EvioDataUtils.decodeNum(identity);
            if(callback!=null){
                iden[2] = tag_n;
                iden[3] = num_n;
                callback.apply(position, iden);
            }
            /*System.out.printf("\t\t\tnode : size = %9d , identity = (%5d, %4d), type = %4d, pos = %6d\n",
                    nodeSize,
                    EvioDataUtils.decodeTag(identity),
                    EvioDataUtils.decodeNum(identity),
                    EvioDataUtils.decodeType(identity),position
            );*/
            position += nodeSize*4 + 4;
        }
    }

    @Override
    public void getAt(DataNode node, int position) {
        int length = this.structBuffer.getInt(position);
        node.allocate(length+4);
        System.arraycopy(this.structBuffer.array(), position, 
                node.getBuffer().array(), 0, length + 4);
        node.verify();
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
        require(size); return true;
    }

    @Override
    public int identifier() {
        return this.structBuffer.getInt(this.IDENTIFIER_POSITION);        
    }

    @Override
    public boolean verify() {
        return true;
    }
}
