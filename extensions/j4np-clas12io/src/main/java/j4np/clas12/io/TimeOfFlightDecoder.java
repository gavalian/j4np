/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.io;

import j4np.clas12.decoder.GenericDecoder;
import j4np.data.evio.EvioNode;

/**
 *
 * @author gavalian
 */
public class TimeOfFlightDecoder extends GenericDecoder {
    public TimeOfFlightDecoder(){
        this.decoderName = "ftof";
        this.translationTable = "/daq/tt/ftof";
        this.fittingTable = "/daq/fadc/ftof";
        dataNodes.clear();
        dataNodes.add(Clas12NodeUtils.createNodeTDC(13, 1, 8192));
        dataNodes.add(Clas12NodeUtils.createNodeADC(13, 2, 8192));
    }
    
    
    @Override
    public void decode(EvioNode node, int[] identity) {
        
        //if(this.containsCrate(identity[0])==true) System.out.printf(" decoding %d %d %d\n",identity[0],identity[1],identity[2]);
        if(identity[2]==0xe101){
            if(this.containsCrate(identity[0])==true) {}
                //this.decodeCompositeADC(identity[0],node, dataNodes.get(1));
        }
        if(identity[2]==57607){
            if(this.containsCrate(identity[0])==true) {
                //System.out.println("got it");
                this.decodeArrayTDC(identity[0],node, dataNodes.get(0));
            }
        }
    }
}
