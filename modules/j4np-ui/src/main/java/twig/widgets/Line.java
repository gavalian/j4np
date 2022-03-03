/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public class Line implements Widget {
    
    private double xOrigin = 0;
    private double yOrigin = 0;
    private double xEnd = 0;
    private double yEnd = 0;
    
    private boolean coordNDF = false;
    private int    lineWidth = 1;
    private int    lineStyle = 1;
    private Color  lineColor = Color.black;
    
    
    
    public Line(double x1, double y1, double x2, double y2){
        xOrigin = x1; yOrigin = y1;
        xEnd = x2; yEnd = y2;
    }
    
    public Line setLineColor(Color lc){
        lineColor = lc; return this;
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        g2d.setStroke(TStyle.getInstance().getLineStroke(lineStyle, lineWidth));
        g2d.setColor(lineColor);
        int x1 = (int) tr.getX(xOrigin, r);
        int x2 = (int) tr.getX(xEnd, r);
        int y1 = (int) ( r.getY() + r.getHeight() - tr.getY(yOrigin, r) + r.getY());
        int y2 = (int) ( r.getY() + r.getHeight() - tr.getY(yEnd, r) + r.getY());
        g2d.drawLine(x1,y1,x2,y2);
        //System.out.println(xOrigin + " " + yOrigin + " " + xEnd + );
        //tr.show();
        //System.out.println("R = " + r);
        //System.out.println("Y = " + tr.getY(yOrigin, r) + "  " + tr.getY(yEnd, r));
        //System.out.println("Y = " + tr.getX(xOrigin, r));
        
        g2d.drawLine(0, 410, 0, 410);
    }

    public Line setNDF(boolean flag){
        this.coordNDF = flag; return this;
    }
    
    public Line setWidth(int width){ lineWidth = width; return this;}
    public Line setStyle(int style){ lineStyle = style; return this;}
    
    @Override
    public boolean isNDF() {
        return coordNDF;
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
