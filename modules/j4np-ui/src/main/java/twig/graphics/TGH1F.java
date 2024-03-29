/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;
import twig.data.DataPoint;
import twig.data.DataRange;
import twig.data.H1F;
import twig.widgets.MarkerTools;

/**
 *
 * @author gavalian
 */
public class TGH1F extends TDataNode2D {
    
    private DataPoint point = new DataPoint();
    
    public TGH1F(H1F h){
        this.dataSet = h; setOptions("F");
    }
    
    public TGH1F(H1F h, String options){
        dataSet = h; 
        setOptions(options);
        if(options.length()<1) setOptions("F");
    }
    
    @Override
    public void    getDataBounds(DataRange range){
        if(options.contains("R")==true){
            dataSet.getRange(range);  
            range.padX(0.0, 0.0);
            range.padY(0.0, 0.20);
            range.set(range.getRange().getY(), 
                    range.getRange().getY() + range.getRange().getHeight(),
                    range.getRange().getX(), 
                    range.getRange().getX() + range.getRange().getWidth());
            return;
        }
        if(dataSet!=null){
            dataSet.getRange(range);  
            range.padX(0.0, 0.0);
            range.padY(0.0, 0.20);
        }
    }
    
    public void drawGradient(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        TStyle style = getStyle();
        GeneralPath path = this.getGeneralPath(r, tr);
        Color fcStart = style.getPalette().getColor(this.dataSet.attr().getFillColor());
        Color fcEnd   = new Color(fcStart.getRed(),fcStart.getGreen(),fcStart.getBlue(),0);
        GradientPaint gradient = new GradientPaint((int) r.getX(),
                (int) (r.getY()),fcStart, (int) r.getX(),(int) (r.getY()+r.getHeight()),fcEnd);
        //System.out.printf(" Y = %d\n",(int) r.getY() );
        g2d.setPaint(gradient);
        
        g2d.fill(path);
        Color lc = getStyle().getPalette().getColor(this.dataSet.attr().getLineColor());
        int   lw = dataSet.attr().getLineWidth();
        int   ls = dataSet.attr().getLineStyle();
        g2d.setColor(lc);
        g2d.setStroke(style.getLineStroke(ls, lw));
        g2d.draw(path);
    }
    
    public void drawPattern(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        GeneralPath path = this.getGeneralPath(r, tr);
        g2d.setColor(Color.red);
        g2d.setClip(path);
        int fc = dataSet.attr().getFillColor();
        int lc = dataSet.attr().getLineColor();
        int style = dataSet.attr().getFillStyle();
        //System.out.println("fill style = " + style);
        if(fc>=0){
            Color fillcolor = getStyle().getPalette().getColor(fc);
            MarkerTools.drawRectangleFillStyle(g2d, path, r, fillcolor, style);
            /*
            g2d.setColor(getStyle().getPalette().getColor(fc));
            double howfar = r.getX() + r.getWidth() + r.getHeight();
            for(double x = r.getX(); x < howfar; x += 8.0){
                g2d.drawLine((int) x, (int) (r.getY()+r.getHeight()),
                        (int) (x - r.getHeight()), (int) r.getY());
            }*/
        }
        
        g2d.setClip(null);
        g2d.setColor(getStyle().getPalette().getColor(lc));        
        g2d.draw(path);
    }
    
    
    public GeneralPath getGeneralPath(Rectangle2D r, Translation2D tr){
        int nPoints = dataSet.getSize(0); 
        GeneralPath line = new GeneralPath();
        dataSet.getPoint(point, 0);
        double xp = point.x - point.xerror*0.5;
        double yp = 0.0;
        
        double xc = r.getX() + tr.relativeX(xp, r);
        double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
        line.moveTo(xc, yc);
        
        for(int p = 0; p < nPoints; p++){
            dataSet.getPoint(point, p);
            xp = point.x - point.xerror*0.5;
            yp = point.y;
            xc = r.getX() + tr.relativeX(xp, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
            line.lineTo(xc, yc);
            xp = point.x + point.xerror*0.5;
            yp = point.y;
            xc = r.getX() + tr.relativeX(xp, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
            line.lineTo(xc, yc);
        }
        yp = 0.0;
        yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
        line.lineTo(xc, yc);
        return line;
    }
    
    public void drawOutline(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        TStyle style = getStyle();
        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        
        int markerColor = this.dataSet.attr().getMarkerColor();
        int markerSize = this.dataSet.attr().getMarkerSize();
        int markerStyle = this.dataSet.attr().getMarkerStyle();        
        int fillColor = this.dataSet.attr().getFillColor();
        
        int nPoints = dataSet.getSize(0); 
        GeneralPath line = new GeneralPath();
        dataSet.getPoint(point, 0);
        double xp = point.x - point.xerror*0.5;
        double yp = 0.0;
        
        double xc = r.getX() + tr.relativeX(xp, r);
        double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
        line.moveTo(xc, yc);
        
        for(int p = 0; p < nPoints; p++){
            dataSet.getPoint(point, p);
            xp = point.x - point.xerror*0.5;
            yp = point.y;
            xc = r.getX() + tr.relativeX(xp, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
            line.lineTo(xc, yc);
            xp = point.x + point.xerror*0.5;
            yp = point.y;
            xc = r.getX() + tr.relativeX(xp, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
            line.lineTo(xc, yc);
        }
        yp = 0.0;
        yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
        line.lineTo(xc, yc);
        //System.out.println(" FILL COLOR = " + fillColor);
        if(fillColor>=0){
            Color fColor = style.getPalette().getColor(fillColor);
            //System.out.println(" INT = " + fillColor + "  " + fColor);
            g2d.setColor(fColor);
            g2d.fill(line);
        }
        
        if(lineColor>=0){
            Color lColor = style.getPalette().getColor(lineColor);
            g2d.setColor(lColor);
            g2d.setStroke(new BasicStroke(lineWidth));
            g2d.draw(line);
        }
    }
    
    
    public void drawArea(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        TStyle style = getStyle();
        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        
        int  markerColor = this.dataSet.attr().getMarkerColor();
        int   markerSize = this.dataSet.attr().getMarkerSize();
        int  markerStyle = this.dataSet.attr().getMarkerStyle();        
        int    fillColor = this.dataSet.attr().getFillColor();
        int    fillStyle = this.dataSet.attr().getFillStyle();
                            
        int nPoints = dataSet.getSize(0); 
        GeneralPath line = new GeneralPath();
        dataSet.getPoint(point, 0);
        
        double xp = point.x - point.xerror*0.5;
        double yp = 0.0;
        
        double xc = r.getX() + tr.relativeX(xp, r);
        double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
        line.moveTo(xc, yc);
        
        for(int p = 0; p < nPoints; p++){
            dataSet.getPoint(point, p);
            xp = point.x;
            yp = point.y;
            xc = r.getX() + tr.relativeX(xp, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
            line.lineTo(xc, yc);            
        }
        yp = 0.0;
        yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
        line.lineTo(xc, yc);
        
        if(fillStyle>=0){
            Color fColor = style.getPalette().getColor(fillColor);
            g2d.setColor(fColor);
            g2d.fill(line);
        }
        
        if(lineWidth>0){
            Color lColor = style.getPalette().getColor(lineColor);
            g2d.setColor(lColor);
            g2d.setStroke(style.getLineStroke(fillStyle, lineWidth));
            g2d.draw(line);
        }
    }
    
    public void drawPointErrors(Graphics2D g2d, Rectangle2D r, Translation2D tr, boolean drawPoint, boolean drawLine) {
        TStyle style = getStyle();
        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        
        int markerColor = this.dataSet.attr().getMarkerColor();

        int markerSize = this.dataSet.attr().getMarkerSize();
        int markerStyle = this.dataSet.attr().getMarkerStyle();        
        int fillColor = this.dataSet.attr().getFillColor();
        
        
        Color mColor = style.getPalette().getColor(markerColor);
        Color lColor = style.getPalette().getColor(lineColor);

        int nPoints = dataSet.getSize(0); 
                                
        for(int p = 0; p < nPoints; p++){
            dataSet.getPoint(point, p);
            
            double xp = point.x;
            double yp = point.y;
            double xc = r.getX() + tr.relativeX(xp, r);
            double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
            
            double xe = tr.getLengthX(point.xerror, r);
            double ye = tr.getLengthY(point.yerror, r);
            if(drawLine){
                g2d.setColor(lColor);
                g2d.setStroke(new BasicStroke(lineWidth));
                
                g2d.drawLine( (int) (xc-xe*0.5),(int) yc, 
                        (int) (xc+xe*0.5),(int) yc
                );
                
                g2d.drawLine( (int) (xc),(int) (yc-ye*0.5), 
                        (int) (xc),(int) (yc+ye*0.5)
                );  
            }
        }
       
        if(drawPoint){
            for(int p = 0; p < nPoints; p++){
                dataSet.getPoint(point, p);
                
                double xp = point.x;
                double yp = point.y;
                double xc = r.getX() + tr.relativeX(xp, r);
                double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
                MarkerTools.drawMarker(g2d, xc, yc, mColor, mColor, markerSize, 
                        0, markerStyle);
            }
        }
    }
    
    
    public void drawBar(Graphics2D g2d, Rectangle2D r, Translation2D tr) {

        TStyle style = getStyle();
        
        boolean reversed = this.hasOption("R");
        
        int lineWidth = this.dataSet.attr().getLineWidth();
        int lineColor = this.dataSet.attr().getLineColor();
        
        int markerColor = this.dataSet.attr().getMarkerColor();

        int markerSize = this.dataSet.attr().getMarkerSize();
        int markerStyle = this.dataSet.attr().getMarkerStyle();        
        int fillColor = this.dataSet.attr().getFillColor();
        
        
        Color mColor = style.getPalette().getColor(markerColor);
        Color lColor = style.getPalette().getColor(lineColor);
        //Color fColor = style.getPalette().getColor(fillColor);
        
        int nPoints = dataSet.getSize(0); 
        g2d.setColor(lColor);
        g2d.setStroke(new BasicStroke(lineWidth));
        
        for(int p = 0; p < nPoints; p++){

            dataSet.getPoint(point, p);
            
            double xp = point.x;
            double yp = point.y;
            
            double xc = r.getX() + tr.relativeX(xp, r);
            double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
            double yb = r.getY() + r.getHeight() - tr.relativeY(0.0, r);
                        
            
            if(reversed){
                //xc = r.getY() + tr.relativeY(xp, r);
                //yc = r.getX() + tr.relativeX(yp, r);
                //yb = r.getX() + tr.relativeX(0.0, r);
                //System.out.println("----------------------");
                //System.out.println(r);
                //System.out.printf(" relative [%3d]  = %9.6f %9.6f %9.6f\n", 
                //        p,tr.relativeY(xp, r),tr.relativeX(yp, r),tr.relativeX(0.0, r));
                xc = r.getY() + tr.relativeY(xp, r);
                
                yc = (int) (r.getX() + tr.relativeX(yp, r));
                yb = (int) (r.getX() + tr.relativeX(0.0, r));
                if(yb-yc!=0) g2d.drawLine( (int) (yb),(int) xc, 
                        (int) (yc),(int) xc);
                
            } else {
                g2d.drawLine( (int) (xc),(int) yc, 
                        (int) (xc),(int) yb
                );      
            }
        }
        /*
        if(options.contains("P")==true){
            for(int p = 0; p < nPoints; p++){
                dataSet.getPoint(point, p);
                double xp = point.x;
                double yp = point.y;
                double xc = r.getX() + tr.relativeX(xp, r);
                double yc = r.getY() + r.getHeight() - tr.relativeY(yp, r);
                MarkerTools.drawMarker(g2d, xc, yc, mColor, mColor, markerSize, 
                        0, markerStyle);
            }
        }*/
    }
    
    @Override
    public void drawLegend(Graphics2D g2d, int x, int y, int w, int h){
        
        TStyle style = this.getStyle();
        String opt = this.getOptions();
        boolean drawn = false;
        
        
        if(opt.contains("E")||opt.contains("B")){
            Color lc = style.getPalette().getColor(this.dataSet.attr().getLineColor());
            int   lwidth = dataSet.attr().getLineWidth();
            int   lstyle = dataSet.attr().getLineStyle();
            g2d.setColor(lc);
            g2d.setStroke(style.getLineStroke(lstyle, lwidth));
            g2d.drawLine(x-w/2,y,x+w/2,y);
            drawn = true;
        }
        
        if(opt.contains("P")){
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
            drawn = true;
        }
        
        if(opt.contains("A")||opt.contains("F")||opt.contains("G")||opt.length()==0||drawn==false){
            Color lc = style.getPalette().getColor(this.dataSet.attr().getLineColor());
            int   lwidth = dataSet.attr().getLineWidth();
            int   lstyle = dataSet.attr().getLineStyle();
            
            Rectangle2D rect = new Rectangle2D.Double(x - w/2,
                    y-h/2,w,h);
            if(dataSet.attr().getFillStyle()==0){
                if(dataSet.attr().getFillColor()>=0){
                    g2d.setColor(style.getPalette().getColor(dataSet.attr().getFillColor()));
                    g2d.fill(rect);
                }
                g2d.setColor(lc);
                g2d.setStroke(style.getLineStroke(lstyle, lwidth));
                g2d.draw(rect);
            }
            drawn = true;
            //return;
        }
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        
        if(options.contains("F")==true){
            this.drawOutline(g2d, r, tr);
            return;
            //this.drawPointErrors(g2d, r, tr,hasOption("P"),hasOption("E"));
            //return;
        }
        
        if(options.contains("G")==true){
            this.drawGradient(g2d, r, tr);
            return;
            //return;
        }
        
        if(options.contains("B")==true){
            this.drawBar(g2d, r, tr);
            return;
            //return;
        }
        
        if(options.contains("A")==true){
            this.drawArea(g2d, r, tr);
            return;
            //return;
        }
        
       if(options.contains("E")||options.contains("P")){           
           this.drawPointErrors(g2d, r, tr,hasOption("P"),hasOption("E"));           
           return;
       } 
       
       this.drawOutline(g2d, r, tr);
       /*else {
           int style = dataSet.attr().getFillStyle();
           if(style>0){
               this.drawPattern(g2d, r, tr);
           } else {
               this.drawOutline(g2d, r, tr);
           }
       }*/
    }
}
