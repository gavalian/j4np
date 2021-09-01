/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Node2D;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;
import twig.data.DataPoint;
import twig.data.GraphErrors;

/**
 *
 * @author gavalian
 */
public class TGENode2D extends TDataNode2D {
    
    public TGENode2D(GraphErrors gr){
        this.dataSet = gr;        
    }
    
    public TGENode2D(GraphErrors gr, String options){
        dataSet = gr;        
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){
        TStyle style = getStyle();
        DataPoint point = new DataPoint();
        int nPoints = dataSet.getSize(0);
        GeneralPath line = new GeneralPath();
        for(int i = 0; i < nPoints; i++){
            
        }
       /* Node2D parent = this.getParent();
        Rectangle2D r = parent.getBounds().getBounds();
        System.out.println(parent.getTranslation());
        System.out.println("border = " + parent.getBounds().getBounds());
        axisX.draw(g2d, r, axisFrameRange);
        axisY.draw(g2d, r, axisFrameRange);
        axisZ.draw(g2d, r, axisFrameRange);*/
    }
}
