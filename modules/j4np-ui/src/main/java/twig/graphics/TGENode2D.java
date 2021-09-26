/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Node2D;
import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;
import twig.data.DataPoint;
import twig.data.DataRange;
import twig.data.GraphErrors;
import twig.widgets.MarkerTools;

/**
 *
 * @author gavalian
 */
public class TGENode2D extends TDataNode2D {
    
    private DataPoint point = new DataPoint();    
    
    public TGENode2D(GraphErrors gr){
        this.dataSet = gr;        
    }
    
    public TGENode2D(GraphErrors gr, String options){
        dataSet = gr; setOptions(options);
    }
    @Override
    public void    getDataBounds(DataRange range){
        if(dataSet!=null){
            dataSet.getRange(range);  
            range.padX(0.1, 0.1);
            range.padY(0.1, 0.1);            
        }
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {

        TStyle style = getStyle();
        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        
        int markerColor = this.dataSet.attr().getMarkerColor();
        int markerSize = this.dataSet.attr().getMarkerSize();
        int markerStyle = this.dataSet.attr().getMarkerStyle();
        
        Color mColor = style.getPalette().getColor(markerColor);
        Color lColor = style.getPalette().getColor(lineColor);
        
        
        if(hasOption("F")==true){
            
            GeneralPath line = new GeneralPath();
            int nPoints = dataSet.getSize(0);        
            int fillColor = this.dataSet.attr().getFillColor();
            Color fColor = style.getPalette().getColor(fillColor);
            
            for(int i = 0; i < nPoints; i++){
                dataSet.getPoint(point, i);
                double relativeX = tr.relativeX(point.x,r);
                double relativeY = tr.relativeY(point.y,r);
                double    errorY = tr.getLengthY(point.yerror, r);
                int     errorBar = (int) (errorY/2.0);
                int coordX = (int) (r.getX() + relativeX);
                int coordY = (int) (r.getY() + r.getHeight() - relativeY);
               if(i==0) line.moveTo(coordX, coordY-errorY*0.5);
               line.lineTo(coordX, coordY+errorY*0.5);
            }
            
            for(int i = nPoints-1; i >= 0; i--){
                dataSet.getPoint(point, i);
                double relativeX = tr.relativeX(point.x,r);
                double relativeY = tr.relativeY(point.y,r);
                double    errorY = tr.getLengthY(point.yerror, r);
                int     errorBar = (int) (errorY/2.0);
                int coordX = (int) (r.getX() + relativeX);
                int coordY = (int) (r.getY() + r.getHeight() - relativeY);
               //if(i==0) line.moveTo(coordX, coordY-errorY);
               line.lineTo(coordX, coordY-errorY*0.5);
            }
            g2d.setColor(fColor);
            g2d.fill(line);
        }
        
        //g2d.setColor(style.getPalette().getColor(lineColor));
        //g2d.setStroke(new BasicStroke(lineWidth));
        BasicStroke lineStroke = new BasicStroke(lineWidth);
        int nPoints = dataSet.getSize(0);        
        
        for(int i = 0; i < nPoints; i++){
            dataSet.getPoint(point, i);
            double relativeX = tr.relativeX(point.x,r);
            double relativeY = tr.relativeY(point.y,r);
            double    errorY = tr.getLengthY(point.yerror, r);
            int     errorBar = (int) (errorY/2.0);
            int coordX = (int) (r.getX() + relativeX);
            int coordY = (int) (r.getY() + r.getHeight() - relativeY);
            
            //g2d.drawOval(coordX,coordY, 6, 6);
            g2d.setColor(lColor);
            g2d.setStroke(lineStroke);
            g2d.drawLine(coordX, coordY-errorBar, coordX, coordY+errorBar);
            
            MarkerTools.drawMarker(g2d, coordX, coordY, mColor, lColor, markerSize, 0, markerStyle);
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
