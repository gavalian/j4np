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
public class DriftChamberDecoder extends GenericDecoder {
    
    public DriftChamberDecoder(){        
        this.decoderName = "dc";
        this.translationTable = "/daq/tt/dc";
        dataNodes.clear();
        dataNodes.add(Clas12NodeUtils.createNodeTDC(12, 1, 8192));
    }

    
    @Override
    public void decode(EvioNode node, int[] identity) {        
        if(identity[2]==57622){
            //System.out.println(" found the identity ");
            //if(this.containsCrate(identity[0])==true) System.out.println("\t  doing the thing");
            if(this.containsCrate(identity[0])==true) {
                //System.out.printf("\tbefore : %d\n",dataNodes.get(0).getRows());
                this.decodeCompositeTDC(identity[0],node, dataNodes.get(0));
                //System.out.printf("\tafter : %d\n",dataNodes.get(0).getRows());
            }
        }
    }
    
}
