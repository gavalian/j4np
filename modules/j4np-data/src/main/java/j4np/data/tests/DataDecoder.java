/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.tests;

import j4np.data.evio.EvioNode;
import j4np.hipo5.data.CompositeNode;

/**
 *
 * @author gavalian
 */

/**
 * This code is used to decode composite data structures.
 * Here is the format description:
 * 
 *  * MSB(31)                          LSB(0)
 * <---  32 bits ------------------------>
 * _______________________________________
 * |  tag    | type |    length          | --> tagsegment header
 * |_________|______|____________________|
 * |        Data Format String           |
 * |                                     |
 * |_____________________________________|
 * |              length                 | \
 * |_____________________________________|  \  bank header
 * |       tag      |  type   |   num    |  /
 * |________________|_________|__________| /
 * |               Data                  |
 * |                                     |
 * |_____________________________________|
 * @author gavalian
 */
public class DataDecoder {
    /**
     * This method decodes the DC TDC bank. format is c,i,l,N(c,s)
     * @param struct
     * @param node 
     */
    public static void decode_57622(EvioNode node, int crate, CompositeNode struct){
        
        byte[] buffer = node.getBuffer().array();
        String t = new String();
        //for(int i = 0; i < 100; i++) System.out.printf("%d - %c (%d) ",i,(char) buffer[i],buffer[i]);
        //System.out.println();
        
        
        int position = 36;
        int     iter = 0;
        int   length = node.getBuffer().getInt(0)*4;
        /*System.out.println("  position = " + position + "  length = " + length);
        for(int i = -6; i < 40; i++){
            System.out.printf("%d - %d\n",i,node.getBuffer().getInt(position+i));
        }*/
        while(position+12<length){
            iter++;
            
            byte  slot = node.getBuffer().get(position);
            int   trig = node.getBuffer().getInt(position+1);
            long  time = node.getBuffer().getLong(position+5);
            position +=13;
            int ncount = node.getBuffer().getInt(position);
            position += 4;
           // System.out.printf("iter = %5d, slot = %5d, position %8d, size = %5d\n",iter,slot, position,ncount);
            for(int i = 0; i < ncount ; i++){
                byte  channel = node.getBuffer().get(position);
                short     tdc = node.getBuffer().getShort(position+1);
                //System.out.printf(" data tdc = , ** , " + tdc);
                position += 3;
                int row = struct.getRows();
                struct.setRows(row+1);
                
                struct.putByte(  row, 1, (byte) crate);
                struct.putByte(  row, 2, (byte) slot);
                struct.putShort( row, 3,  channel);
                struct.putByte(  row, 4, (byte) 0);

                struct.putInt(   row, 5, (int) tdc);
            }
        }                
    }
    
    public static void decode_57638(EvioNode node, int crate, CompositeNode struct, DataTranslator tr){
        
        int[]    haddr = new int[]{crate,0,0,0};
        
        byte[]   buffer = node.getBuffer().array();
        short[]  pulse  = new short[400];
        // The format string ends at postiion 23, then
        // another 4 byte header follows. The format for this
        // bank is cm(cms)
        int position = 32;
        int     iter = 0;
        int   length = node.getBuffer().getInt(0)*4;
        //System.out.println(">>>>>>>>> ");
        //for(int i = 0; i < 100; i++) System.out.printf("%d - %c (%d) ",i,(char) buffer[i],buffer[i]);        
        //System.out.println();
        //System.out.printf(">>> Bank Length = %5d\n",length);
        while(position+12<length){
            iter++;
            short    slot = node.getBuffer().get(position);
            byte    countByte = node.getBuffer().get(position+1);
            int         count = 0x000000FF&countByte;
            
            //System.out.printf(" at %5d : slot = %4d, count = %5d\n",position,slot,count);
            
            position += 2;
            for(int loop = 0 ; loop < count; loop++){
                
                byte channelByte = node.getBuffer().get(position);
                byte samplesByte = node.getBuffer().get(position+1);
                
                int channel = 0x000000FF&channelByte;
                int samples  = 0x000000FF&samplesByte;
                position += 2;
                for(int i = 0; i < samples; i++){
                    pulse[i] = node.getBuffer().getShort(position);
                    position += 2;
                }
                
                haddr[1] = slot;
                haddr[2] = channel;
                long hash = DataTranslator.getHash(haddr);
                
                if(tr.getMap().containsKey(hash)==true){
                    long softHash = tr.getMap().get(hash);
                    
                    //System.out.println(" found it ;-) ");
                } else {
                    //System.out.println(" didn't find it ;-( ");
                }
                //System.out.printf("found pulse with at %5d : slot = %3d,  channel = %3d, length = %5d\n",
                //        position,slot,channel,samples);
            }            
        }
        
    }
}
