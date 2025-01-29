/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import twig.config.TAttributesFill;
import twig.config.TAttributesLine;
import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public class Polygon implements Widget {

    private boolean ndfCoord = false;
    List<Point2D.Double> points = new ArrayList<>();
    
    TAttributesLine  attrLine = new TAttributesLine();
    TAttributesFill  attrFill = new TAttributesFill();
    
    private boolean isQuadratic = false;
    
    public Polygon(){
        
    }
    
    public Polygon(double[] x, double[] y){
        for(int i = 0; i < x.length; i++) addPoint(x[i],y[i]);
    }
    
    public Polygon(double[] x, double[] y, boolean ndf){        
        for(int i = 0; i < x.length; i++) addPoint(x[i],y[i]);
        this.ndfCoord = ndf;
    }
    
    
    public TAttributesLine attrLine(){ return attrLine;}
    public TAttributesFill attrFill(){ return attrFill;}
    
    public final Polygon addPoint(double x, double y){
        points.add(new Point2D.Double(x,y)); return this;
    }
    
    public final Polygon addPoints(double[] x, double[] y){
        for(int i = 0; i < x.length; i++)
            points.add(new Point2D.Double(x[i],y[i])); 
        return this;
    }
    
    public Polygon setNDF(boolean flag){
        this.ndfCoord = flag; return this;
    }
    
    public Polygon setQuardatic(boolean flag){ this.isQuadratic = flag; return this;}
    public TAttributesFill fill(){ return attrFill;}
    public TAttributesLine line(){ return attrLine;}
    
    
    public Polygon move(double xt, double yt){
        for(Point2D.Double p : points){
            p.setLocation(p.x+xt, p.y+yt);
        }
        return this;
    }
    
    public Polygon rotate(double angle){
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        for(Point2D.Double p : points){            
            double xx = p.x;
            p.setLocation(c*xx - s*p.y,s*xx + c*p.y);
            //p.setLocation(p.x+xt, p.y+yt);
        }
        return this;
    }
    
    public Polygon rotateDeg(double angle){
        double s = Math.sin(Math.toRadians(angle));
        double c = Math.cos(Math.toRadians(angle));
        for(Point2D.Double p : points){
           
            double xx = p.x;
            p.setLocation(c*xx - s*p.y,s*xx + c*p.y);
            //p.setLocation(p.x+xt, p.y+yt);
        }
        return this;
    }
    
    public static Polygon box(double xsize, double ysize){
        return Polygon.trap(xsize, xsize, ysize);
    }
    
    public static Polygon trap(double xsize_l, double xsize_h, double ysize){
        Polygon p = new Polygon();
        p.addPoint(-xsize_l/2, -ysize/2);
        p.addPoint( xsize_l/2, -ysize/2);
        p.addPoint( xsize_h/2,  ysize/2);
        p.addPoint(-xsize_h/2,  ysize/2);
        p.addPoint(-xsize_l/2, -ysize/2);
        return p;  
    }
    
    public GeneralPath getPath(){
        GeneralPath path = new GeneralPath();
        path.moveTo(points.get(0).x, points.get(0).y);
        for(int i = 1; i < points.size(); i++) path.lineTo(points.get(i).x, points.get(i).y);
        return path;
    }
    
    private void drawLinear(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        TStyle style = TStyle.getInstance();
        
        int lstyle = this.attrLine.getLineStyle();
        int lcolor = this.attrLine.getLineColor();
        int lwidth = this.attrLine.getLineWidth();
        

        int fcolor = this.attrFill.getFillColor();                
        
        GeneralPath path = new GeneralPath();
        
        
        double xc = (int) tr.getX(points.get(0).x, r);
        double yc = r.getY() + r.getHeight() - tr.getY(points.get(0).y, r) + r.getY();
        
        if(ndfCoord==false){
            xc = r.getX() + tr.relativeX(points.get(0).x, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(points.get(0).y, r);
        }
        path.moveTo(xc,yc);
        
        for(int i = 1; i < this.points.size(); i++){
            xc = (int) tr.getX(points.get(i).x, r);
            yc = r.getY() + r.getHeight() - tr.getY(points.get(i).y, r) + r.getY();
            
            if(ndfCoord==false){
                xc = r.getX() + tr.relativeX(points.get(i).x, r);
                //System.out.println("debug = " + r.getX() + "  -> " + tr.relativeX(points.get(i).x, r));
                yc = r.getY() + r.getHeight() - tr.relativeY(points.get(i).y, r);
            }
            //System.out.println(" x = " + points.get(i).x + " xc = " + xc);
            //System.out.println(" y = " + points.get(i).y + " yc = " + yc);
            
            path.lineTo(xc,yc);
        }
        
        //System.out.println("style = " + attrFill.getFillStyle());
        if(attrFill.getFillStyle()>=1){
            //System.out.println("fill color = " + fcolor);
            g2d.setColor(style.getPalette().getColor(fcolor));
            g2d.fill(path);
        }
        
        
        if(fcolor>=0){
            g2d.setColor(style.getPalette().getColor(fcolor));
            g2d.fill(path);
        }
        g2d.setColor(style.getPalette().getColor(lcolor));
        g2d.setStroke(style.getLineStroke(lstyle, lwidth));
        //System.out.println(" drawing path " + path);
        g2d.draw(path);
    }
    
    private void drawQuadratic(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        TStyle style = TStyle.getInstance();
        
        int lstyle = this.attrLine.getLineStyle();
        int lcolor = this.attrLine.getLineColor();
        int lwidth = this.attrLine.getLineWidth();
        

        int fcolor = this.attrFill.getFillColor();                
        
        Path2D path = new Path2D.Double();
        //path = new GeneralPath();
        
        
        double xc = (int) tr.getX(points.get(0).x, r);
        double yc = r.getY() + r.getHeight() - tr.getY(points.get(0).y, r) + r.getY();
        
        if(ndfCoord==false){
            xc = r.getX() + tr.relativeX(points.get(0).x, r);
            yc = r.getY() + r.getHeight() - tr.relativeY(points.get(0).y, r);
        }
        path.moveTo(xc,yc);
        
        for(int i = 1; i < this.points.size(); i++){
            xc = (int) tr.getX(points.get(i).x, r);
            yc = r.getY() + r.getHeight() - tr.getY(points.get(i).y, r) + r.getY();
            
            if(ndfCoord==false){
                xc = r.getX() + tr.relativeX(points.get(i).x, r);
                //System.out.println("debug = " + r.getX() + "  -> " + tr.relativeX(points.get(i).x, r));
                yc = r.getY() + r.getHeight() - tr.relativeY(points.get(i).y, r);
            }
            //System.out.println(" x = " + points.get(i).x + " xc = " + xc);
            //System.out.println(" y = " + points.get(i).y + " yc = " + yc);
            path.lineTo(xc,yc);
        }
        
        //System.out.println("style = " + attrFill.getFillStyle());
        if(attrFill.getFillStyle()>=1){
            //System.out.println("fill color = " + fcolor);
            g2d.setColor(style.getPalette().getColor(fcolor));
            g2d.fill(path);
        }
        
        g2d.setColor(style.getPalette().getColor(lcolor));
        g2d.setStroke(style.getLineStroke(lstyle, lwidth));
                
        g2d.draw(path);
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {   
        //System.out.println(" drawing Polygon " + r);
       this.drawLinear(g2d, r, tr);
    }

    @Override
    public boolean isNDF() {
        return ndfCoord;
    }

    @Override
    public void configure(JComponent parent) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
