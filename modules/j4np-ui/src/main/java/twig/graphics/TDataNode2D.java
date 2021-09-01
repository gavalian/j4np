/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Node2D;
import twig.config.TStyle;
import twig.data.DataRange;
import twig.data.DataSet;
import twig.data.GraphErrors;

/**
 *
 * @author gavalian
 */
public class TDataNode2D extends Node2D {
    
    protected DataSet dataSet = null;    
    protected TStyle  tStyle  = null;
    
    public TDataNode2D(){
        super(0,0,1,1);
    }
    
    public DataSet  getDataSet() { return dataSet;}
    
    public TStyle   getStyle(){ 
        if(tStyle==null) return TStyle.getInstance();
        return tStyle;
    }
    
    public void     setStyle(TStyle ts){ tStyle = ts;}
    
    public void    getDataBounds(DataRange range){
        if(dataSet!=null){
            dataSet.getRange(range);
        }
    }
    
    
}
