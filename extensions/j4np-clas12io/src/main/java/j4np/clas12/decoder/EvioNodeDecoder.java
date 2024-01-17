/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.ccdb.DatabaseProvider;
import j4np.clas12.decoder.DataBankStore;
import j4np.data.evio.EvioNode;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public abstract class EvioNodeDecoder {
    
    protected List<CompositeNode> dataNodes = new ArrayList<>();  
    public abstract void decode(EvioNode node, int[] identity);
    public abstract void decode(EvioNode node, int[] identity, DataBankStore store, Event hipoEvent);
    public void initDecoder(DatabaseProvider provider){}
    public void finilize(){}
    
    /*public String getTranslationTable(){ return null;}
    public Map<Long,Long>  loadTranslation(DatabaseProvider provider, String table){ return null;}
    */
    
    
    public List<CompositeNode> getNodes() {return dataNodes;}
    public void translate(Map<Long,Long> translation){}
    public void show(){}
    public void reset(){ for(CompositeNode n : dataNodes) n.setRows(0);}
}
