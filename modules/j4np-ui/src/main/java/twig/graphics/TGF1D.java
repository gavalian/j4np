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
import twig.math.Func1D;

/**
 *
 * @author gavalian
 */
public class TGF1D extends TDataNode2D {
 private DataPoint point = new DataPoint();
    public TGF1D(Func1D h){
        this.dataSet = h;
    }
    
    public TGF1D(Func1D h, String options){
        dataSet = h; setOptions(options);
    }
    
    @Override
    public void    getDataBounds(DataRange range){
        if(dataSet!=null){
            dataSet.getRange(range);  
            range.padX(0.0, 0.0);
            range.padY(0.0, 0.15);
            //System.out.println("function range = " + range.getRange());
        }
    }
    
    @Override
    public void drawLegend(Graphics2D g2d, int x, int y, int w, int h){
        TStyle style = this.getStyle();
        String opt = this.getOptions();
         Color lc = style.getPalette().getColor(dataSet.attr().getLineColor());
         int   lw = dataSet.attr().getLineWidth();
         int   ls = dataSet.attr().getLineStyle();
         
         double width = w;
         BasicStroke stroke = style.getLineStroke(ls, lw);
         g2d.setColor(lc);
         g2d.setStroke(stroke);
         g2d.drawLine((int) (x-width/2), (int) y,
                    (int) (x+width/2), (int) y);   
    }
    
     @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {

        TStyle style = getStyle();
        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        int lineStyle = this.dataSet.attr().getLineStyle();
        
        int markerColor = this.dataSet.attr().getMarkerColor();
        int markerSize = this.dataSet.attr().getMarkerSize();
        int markerStyle = this.dataSet.attr().getMarkerStyle();        
        int fillColor = this.dataSet.attr().getFillColor();
        
        int nPoints = dataSet.getSize(0);
        
        GeneralPath path = new GeneralPath();
        
        dataSet.getPoint(point, 0);

        double xp = point.x ;
        double yp = point.y; 
        
        double xc = r.getX() + tr.relativeX(xp, r);
        double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
        path.moveTo(xc, yc);
        for(int p = 0; p < nPoints; p++){
            dataSet.getPoint(point, p);
            xc = r.getX() + tr.relativeX(point.x, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(point.y, r);
            path.lineTo(xc, yc);
        }
        
        if(lineColor>=0){
            Color lColor = style.getPalette().getColor(lineColor);
            g2d.setColor(lColor);

            //g2d.setStroke(new BasicStroke(lineWidth));
            g2d.setStroke(style.getLineStroke(lineStyle, lineWidth));
            g2d.draw(path);
        }
    }
    
}
