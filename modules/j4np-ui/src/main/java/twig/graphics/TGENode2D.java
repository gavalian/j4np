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
        this.dataSet = gr; setOptions("PE");
    }
    
    public TGENode2D(GraphErrors gr, String options){
        dataSet = gr; 
        //System.out.println("What was passed is : " + options);
        //setOptions("PEL");
        
        if(options.length()<1){
            setOptions("PE"+options);
        } else {
            setOptions(options);
        }
        
        if(options.compareTo("*")==0) setOptions("PE");
        
    }
    
    @Override
    public void    getDataBounds(DataRange range){
        if(dataSet!=null){
            dataSet.getRange(range);  
            range.padX(0.1, 0.1);
            range.padY(0.1, 0.1);            
        }
    }
    
    protected void drawLine(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        TStyle style = getStyle();        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        int lineStyle = this.dataSet.attr().getLineStyle();
        Color lColor = style.getPalette().getColor(lineColor);
        
        GeneralPath line = new GeneralPath();

        int nPoints = dataSet.getSize(0);        
        dataSet.getPoint(point, 0);
        double relativeX = tr.relativeX(point.x,r);
        double relativeY = tr.relativeY(point.y,r);
        int coordX = (int) (r.getX() + relativeX);
        int coordY = (int) (r.getY() + r.getHeight() - relativeY);
        line.moveTo(coordX, coordY);
        
        for(int i = 1; i < nPoints; i++){            
            dataSet.getPoint(point, i);
            relativeX = tr.relativeX(point.x,r);
            relativeY = tr.relativeY(point.y,r);
            coordX = (int) (r.getX() + relativeX);
            coordY = (int) (r.getY() + r.getHeight() - relativeY);
            line.lineTo(coordX, coordY);
            //g2d.drawOval(coordX,coordY, 6, 6);
        }
        BasicStroke lineStroke = style.getLineStroke(lineStyle, lineWidth);
        g2d.setStroke(lineStroke);
        g2d.setColor(lColor);
        g2d.draw(line);
    }
    
    public void drawFill(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        TStyle style = getStyle();
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
    
    
    public void drawPointErrors(Graphics2D g2d, Rectangle2D r, Translation2D tr, boolean draw_points, boolean draw_errors){
        TStyle style = getStyle();
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        
        int markerColor = this.dataSet.attr().getMarkerColor();
        int markerSize = this.dataSet.attr().getMarkerSize();
        int markerStyle = this.dataSet.attr().getMarkerStyle();
        
        Color mColor = style.getPalette().getColor(markerColor);
        Color lColor = style.getPalette().getColor(lineColor);
        BasicStroke lineStroke = new BasicStroke(lineWidth);
        int markerOutlineColor = this.dataSet.attr().getMarkerOutlineColor();
        int markerOutlineWidth = this.dataSet.attr().getMarkerOutlineWidth();
        
        Color markerDecorColor = style.getPalette().getColor(markerOutlineColor);
        
        int nPoints = dataSet.getSize(0);        
        
        for(int i = 0; i < nPoints; i++){
            dataSet.getPoint(point, i);
            double relativeX = tr.relativeX(point.x,r);
            double relativeY = tr.relativeY(point.y,r);

            double    errorX = tr.getLengthX(point.xerror, r);
            double    errorY = tr.getLengthY(point.yerror, r);
            
            int     errorBarX = (int) (errorX/2.0);
            int     errorBarY = (int) (errorY/2.0);
            
            int coordX = (int) (r.getX() + relativeX);
            int coordY = (int) (r.getY() + r.getHeight() - relativeY);
            //System.out.printf("%8.5f %8.5f  %8.5f  %8.5f\n",point.xerror,point.yerror,
            //        errorX, errorY);
            //g2d.drawOval(coordX,coordY, 6, 6);
            if(draw_errors==true){
                g2d.setColor(lColor);
                g2d.setStroke(lineStroke);
                g2d.drawLine(coordX, coordY-errorBarY, coordX, coordY+errorBarY);
                g2d.drawLine(coordX-errorBarX, coordY, coordX+errorBarX, coordY);
            }
            
            if(draw_points){
                MarkerTools.drawMarker(g2d, coordX, coordY, 
                        mColor, markerDecorColor, markerSize, markerOutlineWidth,
                        markerStyle);
            }
        }
    }
    //public void drawLine(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
    public void drawLegend(Graphics2D g2d, int x, int y, int w, int h){
        TStyle style = this.getStyle();
        String opt = this.getOptions();
        
        if(opt.contains("F")==true){
            int fillcolor = this.dataSet.attr().getFillColor();
            g2d.setColor(style.getPalette().getColor(fillcolor));
            g2d.fillRect(x-w/2-1, y-h/2, w+2, h);
        }
        
        if(opt.contains("L")==true||opt.contains("E")){
            int linecolor = this.dataSet.attr().getLineColor();
            int linewidth = this.dataSet.attr().getLineWidth();
            int linestyle = this.dataSet.attr().getLineStyle();
            
            g2d.setColor(style.getPalette().getColor(linecolor));
            g2d.setStroke(style.getLineStroke(linestyle, linewidth));
            g2d.drawLine(x-w/2, y, x+w/2, y);
        }
        
        if(opt.contains("P")==true){
            int lineWidth = this.dataSet.attr().getLineWidth();
            int lineColor = this.dataSet.attr().getLineColor();
            
            int markerColor = this.dataSet.attr().getMarkerColor();
            int markerSize = this.dataSet.attr().getMarkerSize();
            int markerStyle = this.dataSet.attr().getMarkerStyle();
            
            Color mColor = style.getPalette().getColor(markerColor);
            Color lColor = style.getPalette().getColor(lineColor);
            BasicStroke lineStroke = new BasicStroke(lineWidth);
            int markerOutlineColor = this.dataSet.attr().getMarkerOutlineColor();
            int markerOutlineWidth = this.dataSet.attr().getMarkerOutlineWidth();
            
            Color markerDecorColor = style.getPalette().getColor(markerOutlineColor);
            MarkerTools.drawMarker(g2d, x, y, 
                        mColor, markerDecorColor, markerSize, markerOutlineWidth,
                        markerStyle);
        }
    }
    //}
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {

        if(hasOption("F")==true){
            this.drawFill(g2d, r, tr);
        }
        
        if(hasOption("L")==true){
            this.drawLine(g2d, r, tr);
        }
        
        if(hasOption("P")||hasOption("E")){
            boolean drawP = this.hasOption("P");
            boolean drawE = this.hasOption("E");
            this.drawPointErrors(g2d, r, tr, drawP, drawE);
        }
        /*
        TStyle style = getStyle();
        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        
        int markerColor = this.dataSet.attr().getMarkerColor();
        int markerSize = this.dataSet.attr().getMarkerSize();
        int markerStyle = this.dataSet.attr().getMarkerStyle();
        
        Color mColor = style.getPalette().getColor(markerColor);
        Color lColor = style.getPalette().getColor(lineColor);
        //System.out.println("options = " + options);
        if(hasOption("L")==true){
            //System.out.println(" line is being drawn");
            drawLine(g2d, r, tr);
        }
        
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
        }*/
        
        //g2d.setColor(style.getPalette().getColor(lineColor));
        //g2d.setStroke(new BasicStroke(lineWidth));
        
        /*
        BasicStroke lineStroke = new BasicStroke(lineWidth);
        int markerOutlineColor = this.dataSet.attr().getMarkerOutlineColor();
        int markerOutlineWidth = this.dataSet.attr().getMarkerOutlineWidth();
        
        Color markerDecorColor = style.getPalette().getColor(markerOutlineColor);
        
        int nPoints = dataSet.getSize(0);        
        
        for(int i = 0; i < nPoints; i++){
            dataSet.getPoint(point, i);
            double relativeX = tr.relativeX(point.x,r);
            double relativeY = tr.relativeY(point.y,r);

            double    errorX = tr.getLengthX(point.xerror, r);
            double    errorY = tr.getLengthY(point.yerror, r);
            
            int     errorBarX = (int) (errorX/2.0);
            int     errorBarY = (int) (errorY/2.0);
            
            int coordX = (int) (r.getX() + relativeX);
            int coordY = (int) (r.getY() + r.getHeight() - relativeY);
            //System.out.printf("%8.5f %8.5f  %8.5f  %8.5f\n",point.xerror,point.yerror,
            //        errorX, errorY);
            //g2d.drawOval(coordX,coordY, 6, 6);
            g2d.setColor(lColor);
            g2d.setStroke(lineStroke);
            g2d.drawLine(coordX, coordY-errorBarY, coordX, coordY+errorBarY);
            g2d.drawLine(coordX-errorBarX, coordY, coordX+errorBarX, coordY);
            
            MarkerTools.drawMarker(g2d, coordX, coordY, 
                    mColor, markerDecorColor, markerSize, markerOutlineWidth,
                    markerStyle);
        }*/
       /* Node2D parent = this.getParent();
        Rectangle2D r = parent.getBounds().getBounds();
        System.out.println(parent.getTranslation());
        System.out.println("border = " + parent.getBounds().getBounds());
        axisX.draw(g2d, r, axisFrameRange);
        axisY.draw(g2d, r, axisFrameRange);
        axisZ.draw(g2d, r, axisFrameRange);*/
    }
}
