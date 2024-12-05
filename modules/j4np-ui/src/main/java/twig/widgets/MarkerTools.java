/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;
import twig.data.DataSet;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.math.F1D;
import twig.math.Func1D;

/**
 *
 * @author gavalian
 */
public class MarkerTools {
    
    public static void drawMarker(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        int markerType = type;
        switch(type){
            case 1: MarkerTools.drawMarkerCyrcle(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 2: MarkerTools.drawMarkerRectangle(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 3: MarkerTools.drawMarkerDiamond(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 4: MarkerTools.drawMarkerTriangle(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 5: MarkerTools.drawMarkerTriangleUpsideDown(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 6: MarkerTools.drawMarkerTriangleLeft(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 7: MarkerTools.drawMarkerTriangleRight(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 8: MarkerTools.drawMarkerHexagon(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            case 9: MarkerTools.drawMarkerHexagonUp(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type); break;
            default: MarkerTools.drawMarkerCyrcle(g2d, x, y, fillColor, lineColor, fillSize, lineSize, type);
        }
    }
    
    public static void drawMarkerCyrcle(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        g2d.fillOval((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawOval((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerRectangle(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerDiamond(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];
        
        yPoints[0] = (int) (y-fillSize/2);
        xPoints[0] = (int) (x);
        
        //System.out.println(" x = " + x + " y = " + y);
        xPoints[1] = (int) (x + fillSize/2);
        yPoints[1] = (int) (y );//+ fillSize/2) ;//(x - fillSize/2);
        
        xPoints[2] = (int) (x );//+ fillSize/2);
        yPoints[2] = (int) (y + fillSize/2) ;//(x - fillSize/2);
        
        yPoints[3] = (int) (y);//-fillSize/2);
        xPoints[3] = (int) (x - fillSize/2);
        
        yPoints[4] = (int) (y - fillSize/2);
        xPoints[4] = (int) (x);//- fillSize/2);
        
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 4);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerHexagon(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[7];
        int[] yPoints = new int[7];
        
        
        double x1 = fillSize*0.5*Math.cos(Math.toRadians(60));
        double y1 = fillSize*0.5*Math.sin(Math.toRadians(60));
        double x2 = fillSize*0.5;
        

        xPoints[0] = (int) (x-x1);
        yPoints[0] = (int) (y-y1);
        
        xPoints[1] = (int) (x+x1);
        yPoints[1] = (int) (y-y1);
        
        xPoints[2] = (int) (x+x2);
        yPoints[2] = (int) (y);
        
        xPoints[3] = (int) (x+x1);
        yPoints[3] = (int) (y+y1);
        
        xPoints[4] = (int) (x-x1);
        yPoints[4] = (int) (y+y1);
        
        xPoints[5] = (int) (x-x2);
        yPoints[5] = (int) (y);
        
        xPoints[6] = (int) (x-x1);
        yPoints[6] = (int) (y-y1);
        
        g2d.fillPolygon(xPoints, yPoints, 7);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 7);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerHexagonUp(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[7];
        int[] yPoints = new int[7];
        
        
        double x1 = fillSize*0.5*Math.cos(Math.toRadians(60));
        double y1 = fillSize*0.5*Math.sin(Math.toRadians(60));
        double x2 = fillSize*0.5;
        

        xPoints[0] = (int) (x);
        yPoints[0] = (int) (y-x2);
        
        xPoints[1] = (int) (x+y1);
        yPoints[1] = (int) (y-x1);
        
        xPoints[2] = (int) (x+y1);
        yPoints[2] = (int) (y+x1);
        
        xPoints[3] = (int) (x);
        yPoints[3] = (int) (y+x2);
        
        xPoints[4] = (int) (x-y1);
        yPoints[4] = (int) (y+x1);
        
        xPoints[5] = (int) (x-y1);
        yPoints[5] = (int) (y-x1);
        
        xPoints[6] = (int) (x);
        yPoints[6] = (int) (y-x2);
        
        g2d.fillPolygon(xPoints, yPoints, 7);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 7);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerFish(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[7];
        int[] yPoints = new int[7];
        
        
        double x1 = fillSize*0.5*Math.cos(Math.toRadians(60));
        double y1 = fillSize*0.5*Math.sin(Math.toRadians(60));
        double x2 = fillSize*0.5;
        

        xPoints[0] = (int) (x);
        yPoints[0] = (int) (y-x2);
        
        xPoints[1] = (int) (x+y1);
        yPoints[1] = (int) (y-x1);
        
        xPoints[2] = (int) (x+y1);
        yPoints[2] = (int) (y+x1);
        
        xPoints[3] = (int) (x);
        yPoints[3] = (int) (y+x2);
        
        xPoints[4] = (int) (x-y1);
        yPoints[4] = (int) (y-x1);
        
        xPoints[5] = (int) (x-y1);
        yPoints[5] = (int) (y+x1);
        
        xPoints[6] = (int) (x);
        yPoints[6] = (int) (y-x2);
        
        g2d.fillPolygon(xPoints, yPoints, 7);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 7);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerTriangle(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        
        int yoffset = (fillSize*2)/6-(fillSize)/2;
        
        yPoints[0] = (int) (y-fillSize/2) + yoffset;
        xPoints[0] = (int) (x);
        
        //System.out.println(" x = " + x + " y = " + y);
        xPoints[1] = (int) (x - fillSize/2) ;
        yPoints[1] = (int) (y + fillSize/2) + yoffset ;//(x - fillSize/2);
        
        xPoints[2] = (int) (x + fillSize/2);
        yPoints[2] = (int) (y + fillSize/2) + yoffset;//(x - fillSize/2);
        
        yPoints[3] = (int) (y-fillSize/2) + yoffset;
        xPoints[3] = (int) (x);
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 4);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerTriangleUpsideDown(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        yPoints[0] = (int) (y+fillSize/2);
        xPoints[0] = (int) (x);
        
        //System.out.println(" x = " + x + " y = " + y);
        xPoints[1] = (int) (x - fillSize/2);
        yPoints[1] = (int) (y - fillSize/2) ;//(x - fillSize/2);
        
        xPoints[2] = (int) (x + fillSize/2);
        yPoints[2] = (int) (y - fillSize/2) ;//(x - fillSize/2);
        
        yPoints[3] = (int) (y+fillSize/2);
        xPoints[3] = (int) (x);
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 4);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerTriangleLeft(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        
        yPoints[0] = (int) (y);//+fillSize/2);
        xPoints[0] = (int) (x-fillSize/2);
        
        //System.out.println(" x = " + x + " y = " + y);
        xPoints[1] = (int) (x + fillSize/2);
        yPoints[1] = (int) (y - fillSize/2) ;//(x - fillSize/2);
        
        xPoints[2] = (int) (x + fillSize/2);
        yPoints[2] = (int) (y + fillSize/2) ;//(x - fillSize/2);
        
        yPoints[3] = (int) (y);//+fillSize/2);
        xPoints[3] = (int) (x - fillSize/2);
        
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 4);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawMarkerTriangleRight(Graphics2D g2d, double x, double y, Color fillColor, Color lineColor, int fillSize, int lineSize, int type){
        g2d.setColor(fillColor);
        //g2d.fillRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        
        yPoints[0] = (int) (y);//+fillSize/2);
        xPoints[0] = (int) (x+fillSize/2);
        
        //System.out.println(" x = " + x + " y = " + y);
        xPoints[1] = (int) (x - fillSize/2);
        yPoints[1] = (int) (y + fillSize/2) ;//(x - fillSize/2);
        
        xPoints[2] = (int) (x - fillSize/2);
        yPoints[2] = (int) (y - fillSize/2) ;//(x - fillSize/2);
        
        yPoints[3] = (int) (y);//+fillSize/2);
        xPoints[3] = (int) (x + fillSize/2);
        
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        if(lineColor!=null&&lineSize>0){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));
            g2d.drawPolygon(xPoints, yPoints, 4);
            //g2d.drawRect((int) (x - fillSize/2), (int) (y-fillSize/2), fillSize, fillSize);
        }
    }
    
    public static void drawRectangleFillStyle(Graphics2D g2d, Shape shape, Rectangle2D rect, Color fillcolor, int style){
        
        int step = 4;
        
        switch(style){
            case 2: step = 8; break;
            case 3: step = 12; break;
            case 4: step = 16; break;
            case 5: step = 24; break;
            case 6: step = 32; break;
            case 11: step = 4; break;
            case 12: step = 8; break;
            case 13: step = 12; break;
            case 14: step = 16; break;
            case 15: step = 24; break;
            case 16: step = 32; break;
            default: step = 4; break;
        }
        
        g2d.setColor(fillcolor);
        
        g2d.setStroke(new BasicStroke(1));
        
        g2d.setClip(shape);
        
        double h = rect.getHeight();
        if(style>=10){
            for(double x = (rect.getX()-h);
                    x < (rect.getX()+rect.getWidth());
                    x += step){
                g2d.drawLine((int) x, (int) (rect.getY() + h), (int) (x+h), (int) rect.getY());
            } 
        } else {
            for(double x = rect.getX();
                    x < (rect.getX()+rect.getWidth()+rect.getHeight());
                    x += step){
                g2d.drawLine((int) x, (int) (rect.getY() + h), (int) (x-h), (int) rect.getY());
            } 
        }
        
        g2d.setClip(null);
    }
    
    public static void drawSymbolAt(Graphics2D g2d, Point2D p, DataSet ds, TStyle style, int height){
        if(ds instanceof H1F){            
            Color lc = style.getPalette().getColor(ds.attr().getLineColor());
                        
            double width = 1.6*height;
            
            Rectangle2D rect = new Rectangle2D.Double(p.getX() - width*0.5,
                    p.getY()-height*0.5,width,height);
            
            if(ds.attr().getFillColor()>=0){
                int   fs = ds.attr().getFillStyle();
                int   lw = ds.attr().getLineWidth();
                
                Color fc = style.getPalette().getColor(ds.attr().getFillColor());
                g2d.setColor(fc);
                
                if(fs>0){
                    MarkerTools.drawRectangleFillStyle(g2d,rect, rect, fc, fs);
                } else {
                    g2d.fill(rect);
                }
                //g2d.fillRect((int) (p.getX() - width*0.5),(int) (p.getY()-height*0.5),
                //        (int) width,(int)height);
            }
            
            g2d.setColor(lc);
            g2d.setStroke(new BasicStroke(ds.attr().getLineWidth()));
            
            //g2d.drawRect((int) (p.getX() - width*0.5),(int) (p.getY()-height*0.5),
            //        (int) width,(int)height);
            g2d.draw(rect);
        }
        
        if(ds instanceof Func1D){  
             Color lc = style.getPalette().getColor(ds.attr().getLineColor());
             int   lw = ds.attr().getLineWidth();
             int   ls = ds.attr().getLineStyle();
             
             double width = 1.6*height*0.5;
             BasicStroke stroke = style.getLineStroke(ls, lw);
             g2d.setColor(lc);
             g2d.setStroke(stroke);
             g2d.drawLine((int) (p.getX()-width), (int) p.getY(),
                    (int) (p.getX()+width), (int) p.getY());                          
        }
        
         if(ds instanceof GraphErrors){            
            Color lc = style.getPalette().getColor(ds.attr().getLineColor());
            Color oc = style.getPalette().getColor(ds.attr().getMarkerOutlineColor());
            
            int   lw = ds.attr().getLineWidth();
            g2d.setColor(lc);
            g2d.setStroke(new BasicStroke(lw));
            double width = 1.6*height*0.5;
            g2d.drawLine((int) (p.getX()-width), (int) p.getY(),
                    (int) (p.getX()+width), (int) p.getY());
            
            Color mc = style.getPalette().getColor(ds.attr().getMarkerColor());
            int mstyle = ds.attr().getMarkerStyle();
            int msize  = ds.attr().getMarkerSize();
            int ls     = ds.attr().getMarkerOutlineWidth();
            MarkerTools.drawMarker(g2d, p.getX(), p.getY(), mc, oc, msize, ls, mstyle);
            
         }
    }
}
