/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Node2D;
import j4np.graphics.Translation2D;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;
import twig.data.DataRange;
import twig.data.DataSet;
import twig.data.GraphErrors;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class TDataNode2D implements Widget {
    
    protected DataSet dataSet = null;    
    protected TStyle  tStyle  = null;
    protected String  options = "";
    
    public TDataNode2D(){
        //super(0,0,1,1);
    }
    
    public DataSet   getDataSet() { return dataSet;}
    public void      setOptions(String opt){ options = opt;}
    public String    getOptions(){ return options;}
    public boolean   hasOption(String opt){return options.contains(opt);}
    
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

    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isNDF() {
        return false;
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
