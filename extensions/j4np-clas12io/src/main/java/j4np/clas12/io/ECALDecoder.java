/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.io;

import j4np.clas12.decoder.GenericDecoder;
import j4np.data.evio.EvioNode;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ECALDecoder extends GenericDecoder {
    public ECALDecoder(){
        
        //List<Integer> known = Arrays.asList(1,7,13,19,25,31,2,8,14,20,26,32,
        //        3,9,15,21,27,33,4,10,16,22,28,34);
        //this.crates.addAll(known);
        this.decoderName = "ec";
        this.translationTable = "/daq/tt/ec";
        this.fittingTable = "/daq/fadc/ec";
        dataNodes.clear();
        dataNodes.add(Clas12NodeUtils.createNodeTDC(11, 1, 8192));
        dataNodes.add(Clas12NodeUtils.createNodeADC(11, 2, 8192));
        //System.out.println(" data node size for ECAL = " + dataNodes.size());
    }
        
    @Override
    public void decode(EvioNode node, int[] identity) {
        if(identity[2]==0xe101){
            if(this.containsCrate(identity[0])==true) {}
                //this.decodeCompositeADC(identity[0],node, dataNodes.get(1));
        }
        if(identity[2]==57622){
            if(this.containsCrate(identity[0])==true) 
                this.decodeCompositeTDC(identity[0],node, dataNodes.get(0));
        }
    }
    
    
}
