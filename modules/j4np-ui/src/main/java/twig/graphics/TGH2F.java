/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;
import twig.data.DataPoint;
import twig.data.DataRange;
import twig.data.H1F;
import twig.data.H2F;
import twig.widgets.MarkerTools;

/**
 *
 * @author gavalian
 */
public class TGH2F extends TDataNode2D {
    
    private DataPoint     point = new DataPoint();
    private DataRange dataRange = new DataRange();
    
    public TGH2F(H2F h){
        this.dataSet = h;
    }
    
    public TGH2F(H2F h, String options){
        dataSet = h; setOptions(options);
    }
    
    @Override
    public void    getDataBounds(DataRange range){
        if(dataSet!=null){
            dataSet.getRange(range);  
            //range.padX(0.5, 0.5);
            //range.padY(0.5, 0.5);            
        }
    }
    
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        dataSet.getRange(dataRange);
        TStyle style = getStyle();
        H2F rfh = (H2F) dataSet;
        
        double xc = r.getX();
        double yc = r.getY();
        
        //double xLen = r.getWidth();
        //double yLen = r.getHeight();
        
        int nBinsX = dataSet.getSize(0);
        int nBinsY = dataSet.getSize(1);
        
        
        //double startX = r.getX() + tr.relativeX(dataRange.getRange().getX(), r);
        //double startY = r.getY() + r.getHeight() - tr.relativeY(dataRange.getRange().getY(), r);
        Color cZero = style.getPalette().palette2d().getColor(0);
        
        boolean drawBack = options.contains("F");
        boolean drawLogZ = options.contains("Z");
        
        
        if(options.contains("F")==true){
           
            g2d.setColor(cZero);
            g2d.fillRect((int) r.getX(), (int) r.getY(), 
                    (int) r.getWidth(), (int) r.getHeight());
            //System.out.println(" Filling background with " + c + " rect : " + r);
        }
        
        double histoMax = rfh.getMaximum();
        
        for(int xb = 0; xb < nBinsX; xb++){
                                                
            for(int yb = 0; yb < nBinsY; yb++){
                
                dataSet.getPoint(point, xb,yb);
                
                double startX = r.getX() + tr.relativeX(point.x, r)
                        - tr.getLengthX(point.xerror/2.0, r);
                
                double startY = r.getY() + r.getHeight() 
                        - tr.relativeY(point.y, r) 
                        - tr.getLengthY(point.yerror/2.0, r);
                
                double xLen = tr.getLengthX(point.xerror, r);
                double yLen = tr.getLengthY(point.yerror, r);
                
                //System.out.printf("start coordinate [%d,%d] at %5d x %5d %.2f %.2f\n", xb,yb,
                //        (int) startX, (int) startY,xLen,yLen);
                //if(drawLogZ==false){
                //    Color color = style.getPalette().palette2d().getColor3D(point.z, 0, rfh.getMaximum(), drawLogZ);
                //    g2d.setColor(color);
                //} else {
                Color color = style.getPalette().palette2d().getColor3D(point.z, 0, histoMax, drawLogZ);
                
                if(point.z==0&&drawBack==true) color = cZero;
                
                g2d.setColor(color);
               // }
        
                //System.out.println(xb + " " + yb + " " + color + " " + point.z);
                //g2d.setColor(color);
                g2d.fillRect((int) startX, (int) startY, (int) (xLen+1), (int) (yLen+1));
                g2d.fillRect((int) Math.floor(startX-0.5), (int) Math.floor(startY-0.5), (int) (xLen+1), (int) (yLen+1));
                //g2d.setColor(new Color(240,140,140));
                //g2d.setStroke(new BasicStroke(2));                
                //g2d.drawRect((int) startX, (int) startY, (int) xLen, (int) yLen);
            }
        }               
    }
}
