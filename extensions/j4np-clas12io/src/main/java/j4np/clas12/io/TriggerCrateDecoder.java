/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.io;

import j4np.clas12.decoder.EvioNodeDecoder;
import j4np.clas12.decoder.DataBankStore;
import j4np.data.evio.EvioDataUtils;
import j4np.data.evio.EvioNode;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;

/**
 *
 * @author gavalian
 */
public class TriggerCrateDecoder extends EvioNodeDecoder {

    public TriggerCrateDecoder(){
        dataNodes.add(new CompositeNode(6,1,"i",15));
    }
    
    @Override
    public void decode(EvioNode node, int[] identity) {        
        int   id = node.getIdentifier();
        int  tag = EvioDataUtils.decodeTag(id);
        int type = EvioDataUtils.decodeType(id);
        if(tag==57610&&type==1){
            dataNodes.get(0).setRows(1);
            dataNodes.get(0).putInt(0, 0, node.getInt(4));
        }
    }

    @Override
    public void decode(EvioNode node, int[] identity, DataBankStore store, Event hipoEvent) {
        
    }
    
}
