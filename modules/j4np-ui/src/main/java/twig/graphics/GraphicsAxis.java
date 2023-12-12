/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Properties;
import twig.config.TStyle;
import twig.widgets.LatexText;
import twig.widgets.LatexText.TextAlign;
import twig.widgets.LatexText.TextRotate;

/**
 *
 * @author gavalian
 */
public class GraphicsAxis {
    
    public enum AxisType {
        VERTICAL, HORIZONTAL, GENERIC
    }
    
    protected Rectangle2D         rect = new Rectangle2D.Double();
    protected Translation2D      trans = new Translation2D(0.0,1.0,0.0,1.0);    
    protected Point2D.Double    offset = new Point2D.Double();
    
    protected LatexText          latex = new LatexText("");
    protected LatexText          title = new LatexText("");        
    
    private String     axisScale = "10^3";
    
    protected int      gridLineHeight = 0;
        
    protected Properties  properties = new Properties();
    protected AxisType      axisType = AxisType.GENERIC;
    
    public GraphicsAxis(){
       /* properties.setProperty("axis.line.color", "2");
        properties.setProperty("axis.grid.color", "41");
        properties.setProperty("axis.label.color", "4");
        properties.setProperty("axis.title.color", "5");
        */
       this.initProperties();
    }
    
    public GraphicsAxis(String ttl){
        title.setText(ttl);
        latex.setFont(new Font("PT Serif",Font.BOLD,18));
        title.setFont(new Font("PT Serif",Font.BOLD,18));
        this.initProperties();
    }
    
    public GraphicsAxis setType(AxisType type){axisType = type; return this;}
    
    
    public GraphicsAxis setFont(Font f){
        latex.setFont(f); title.setFont(f); return this;
    }
    
    private void initProperties(){
        properties.clear();
        properties.setProperty("axis.line.color",  "2");
        properties.setProperty("axis.line.width",  "2");
        properties.setProperty("axis.ticks.major.size", "12");
        properties.setProperty("axis.ticks.minor.size", "6");
        properties.setProperty("axis.ticks.offset",  "12");
        properties.setProperty("axis.grid.color",    "41");
        properties.setProperty("axis.label.color",    "4");
        properties.setProperty("axis.title.color",    "5");
    }
    
    public GraphicsAxis setGridLineHeight(int height){ this.gridLineHeight= height; return this;}
    public GraphicsAxis setScale(String scale){ axisScale = scale; return this;}
    
    public GraphicsAxis setProperty(String p, String value){
        properties.setProperty(p, value);
        return this;
    }
    
    public void drawAxisLablesVertical(Graphics2D g2d, 
            double xstart, double ystart, double xend, double yend,
            double ax_min, double ax_max,
            List<Double> ticks, 
            List<String> labels, 
            List<Double> minors){
        
        TStyle style = TStyle.getInstance();
        Color tc = style.getPalette().getColor(Integer.parseInt(properties.getProperty("axis.label.color", "1")));
        int   ts = Integer.parseInt(properties.getProperty("axis.ticks.major.size", "12"));
        double x = Math.round(xstart);
        double y = Math.round(ystart);
        rect.setRect(x, 0, 0, Math.abs(ystart-yend));
        System.out.println(rect);
        latex.setColor(tc);            
        for(int t = 0; t < ticks.size(); t++){
            double position = trans.getY(ticks.get(t), rect);
            System.out.printf(" tick # %d, tick = %8.5f , position = %8.5f\n",
                    t,ticks.get(t),position);
            latex.setText(labels.get(t));
            latex.drawString(g2d, (int) x, (int) (ystart+position), TextAlign.RIGHT, TextAlign.RIGHT);                
        }
    }
    
    public void drawAxis(Graphics2D g2d, 
            double xstart, double ystart, double xend, double yend,
            double ax_min, double ax_max,
            List<Double> ticks, List<String> labels, List<Double> minors){
        this.drawAxisGeneric(g2d, xstart, ystart, xend, yend, ax_min, ax_max, ticks, labels, minors);
        if(this.axisType==AxisType.VERTICAL){
            this.drawAxisLablesVertical(g2d, xstart, ystart, xend, yend, ax_min, ax_max, ticks, labels, minors);
        } 
        /*else {
            this.drawAxisLables(g2d, xstart, ystart, xend, yend, ax_min, ax_max, ticks, labels, minors);
        } */       
    }
    
    
    public void drawAxisGeneric(Graphics2D g2d,            
            double xstart, double ystart, double xend, double yend,
            double ax_min, double ax_max,
            List<Double> ticks, List<String> labels, List<Double> minors){
        
        TStyle style = TStyle.getInstance();
        
        Color lc = style.getPalette().getColor(Integer.parseInt(properties.getProperty("axis.line.color" , "1")));
        Color tc = style.getPalette().getColor(Integer.parseInt(properties.getProperty("axis.label.color", "1")));
        Color gc = style.getPalette().getColor(Integer.parseInt(properties.getProperty("axis.grid.color" , "41")));

        int   lw = Integer.parseInt(properties.getProperty("axis.line.width", "1"));
        int   ts = Integer.parseInt(properties.getProperty("axis.ticks.major.size", "12"));
        int   tms = Integer.parseInt(properties.getProperty("axis.ticks.minor.size", "8"));
                       
        AffineTransform original = g2d.getTransform();        
        
        double    ydiff = (yend-ystart); double xdiff = (xend-xstart);
        double   length = Math.sqrt(ydiff*ydiff+xdiff*xdiff);
        double rotation = Math.atan2(ydiff, xdiff);
        
        //System.out.println("rotation = " + Math.toDegrees(rotation));
        //offset.setLocation(xstart, ystart);
        getShift(offset, xstart, ystart, rotation);        
        double x = Math.round(xstart+offset.x);
        double y = Math.round(ystart-offset.y);
        
        rect.setRect(x, y, length, 0);        
        g2d.setStroke(new BasicStroke(lw));
        g2d.rotate(rotation);        
        g2d.setColor(lc);
        g2d.drawLine((int) (x), (int) (y),
                (int) (x+length), (int) (y));
        
        if(this.gridLineHeight!=0){
            g2d.setColor(gc);            
            for(int h = 0; h < ticks.size();h++){
                double position = trans.getX(ticks.get(h), rect);
                g2d.drawLine((int) position, (int) y, (int) position, (int) (y-gridLineHeight));
            }
        }
        
        g2d.setColor(lc);
        for(int m = 0; m < minors.size(); m++){
            double position = trans.getX(minors.get(m), rect);
            g2d.drawLine((int) position, (int) y, (int) position, (int) (y+tms));
        }
        
        for(int t = 0; t < ticks.size(); t++){
            double position = trans.getX(ticks.get(t), rect);
            g2d.setColor(lc);
            g2d.drawLine((int) position, (int) y, (int) position, (int) (y+ts));
        }
        
        if(axisType!=AxisType.VERTICAL){
            latex.setColor(tc);            
            for(int t = 0; t < ticks.size(); t++){
                double position = trans.getX(ticks.get(t), rect);
                //g2d.setColor(tc);
                latex.setText(labels.get(t));     
                if(ts>0){
                    latex.drawString(g2d, (int) position, (int) (y+ts), TextAlign.CENTER, TextAlign.TOP);
                } else {
                    latex.drawString(g2d, (int) position, (int) (y+ts), TextAlign.CENTER, TextAlign.BOTTOM);
                }
            }
            latex.setText(axisScale);
            latex.drawString(g2d, (int) (x+length+15), (int) y, TextAlign.TOP, TextAlign.BOTTOM);            
        }
        g2d.setTransform(original);
        
        //g2d.setColor(Color.red);
        //g2d.drawLine((int) xstart , (int) ystart, (int) xend, (int) ystart);
    }
    
    
    protected void transform(Rectangle2D.Double rect, double angle){
        
    }
    
    protected void getShift(Point2D.Double pnt, double x, double y, double angle){
        //pnt.setLocation(, angle);
        double r = Math.sqrt(x*x+y*y);
        double t = Math.atan2(y,x);
        double tn =  t-angle;
        double xn = r*Math.cos(tn);
        double yn = r*Math.sin(tn);
        pnt.setLocation( xn-x,y-yn);
        //System.out.println(pnt);
    }
}
