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
    public CompositeNode    index = null;
    public CompositeNode   header = null;
    public CompositeNode   timeStamp = null;
    
    public DataBankStore(){
        tdcCache = Clas12NodeUtils.createNodeTDC(  1, 1, 8192);
        tdcNode  = Clas12NodeUtils.createNodeTDC( 33, 1, 8192*2);
        
        adcCache = Clas12NodeUtils.createNodeADC(  1, 1, 8192);
        adcCachePulse = Clas12NodeUtils.createNodeADCPulse( 1, 1, 8192*2);
        
        adcNode  = Clas12NodeUtils.createNodeADC( 34, 1, 8192*2);

        index    = Clas12NodeUtils.createIndexNode(1, 1, 512);
        
        header = Clas12NodeUtils.createHeaderNode(31, 20, 2);
        timeStamp = new CompositeNode(35,1,"il",512);
    }
}
