/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.io.Clas12NodeUtils;
import j4np.hipo5.data.CompositeNode;

/**
 *
 * @author gavalian
 */
public class DataBankStore {
    
    public CompositeNode tdcCache = null;
    public CompositeNode  tdcNode = null;
    public CompositeNode adcCache = null;
    public CompositeNode adcCachePulse = null;
    public CompositeNode  adcNode = null;
                
    
    public CompositeNode      cache  = null;
    public CompositeNode      pulse  = null;
    public CompositeNode      header = null;
    public CompositeNode   timeStamp = null;
    public CompositeNode       index = null;
    
    public DataBankStore(){
        
        /*tdcCache = Clas12NodeUtils.createNodeTDC(  1, 1, 8192);
        tdcNode  = Clas12NodeUtils.createNodeTDC( 33, 1, 8192*2);
        
        adcCache = Clas12NodeUtils.createNodeADC(  1, 1, 8192);
        adcCachePulse = Clas12NodeUtils.createNodeADCPulse( 1, 1, 8192*2);
        
        adcNode  = Clas12NodeUtils.createNodeADC( 34, 1, 8192*2);
        */
        index     = Clas12NodeUtils.createIndexNode(1, 1, 512);        
        header    = Clas12NodeUtils.createHeaderNode(42, 1, 2);
        timeStamp = new CompositeNode(42,2,"il",512);
    }
    
    public void reset(){
        if(tdcCache!=null) tdcCache.setRows(0);
        if(tdcNode != null) tdcNode.setRows(0);
        if(adcCache != null) adcCache.setRows(0);
        if(adcCachePulse != null) adcCachePulse.setRows(0);
        if(adcNode != null) adcNode.setRows(0);         
    
        if(cache  != null) cache.setRows(0);
        if(pulse  != null) pulse.setRows(0);
        if(header != null) header.setRows(0);
        if(timeStamp != null) timeStamp.setRows(0);
        if(index != null) index.setRows(0);
    }
    public static DataBankStore createDecoder(){
        DataBankStore store = new DataBankStore();
        store.cache = Clas12NodeUtils.createNodeTDC(  1, 1, 8192);
        store.pulse = Clas12NodeUtils.createNodeADCPulse( 1, 1, 8192*10);
        return store;
    }
    
    public static DataBankStore createTranslate(){
        DataBankStore store = new DataBankStore();
        store.tdcCache = Clas12NodeUtils.createNodeTDC(  42,  11, 8192);
        store.tdcNode  = Clas12NodeUtils.createNodeTDC(  42,  11, 8192);
        store.adcCache = Clas12NodeUtils.createNodeADC(  42,  12, 8192);
        store.adcNode  = Clas12NodeUtils.createNodeADC(  42,  12, 8192);
        return store;
    }
    public static DataBankStore createFitter(){
        DataBankStore store = new DataBankStore();
        store.cache = Clas12NodeUtils.createNodeADC(  1, 1, 8192);
        store.pulse = Clas12NodeUtils.createNodeADCPulse( 1, 1, 8192*10);
        return store;
    }
    
    public void refactorTDC(CompositeNode node, int group, int item){
        node.refactor(group,item,"bbbsbil");
    }
    public void refactorADC(CompositeNode node, int group, int item){
        node.refactor(group,item,"bbbsbifs");
    }
}
