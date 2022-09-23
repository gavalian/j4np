/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author gavalian
 */
public class DataRange {
    
    Rectangle2D.Double rect = new Rectangle2D.Double();
    double z = 0.0;
    double dept = 0.0;
    
    public DataRange(){
        rect.x = 0.0; rect.width  = 1.0;                 
        rect.y = 0.0; rect.height = 1.0;
    }
    
    public DataRange(double xmin, double xmax, double ymin, double ymax){        
        rect.x = xmin; rect.width  = (xmax-xmin);                 
        rect.y = ymin; rect.height = ymax - ymin;
    }
    
    public void set(double xmin, double xmax, double ymin, double ymax){
        rect.x = xmin; rect.width = (xmax-xmin);                 
        rect.y = ymin; rect.height = ymax - ymin;
    }
    
    public Rectangle2D getRange(){ return rect;}
    
    public void grow(double x, double y){
        if(x<rect.x){ rect.width = rect.width + rect.x - x; rect.x = x;}
        if(x>rect.x+rect.width){ rect.width = rect.width + (x - (rect.x + rect.width));}
        
        if(y<rect.y){ rect.height = rect.height + rect.y - y; rect.y = y;}
        if(y>rect.y+rect.height){ rect.height = rect.height + (y - (rect.y + rect.height));}
    }
    
    public void growY(double fraction){
        rect.height = rect.height + rect.height*fraction;
        
    }
    public void grow(Rectangle2D range){
        this.grow(range.getX(),range.getY());
        this.grow(range.getX()+range.getWidth(), range.getY());
        this.grow(range.getX()+range.getWidth(), range.getY()+range.getHeight());
        this.grow(range.getX(), range.getY()+range.getHeight());        
    }
    
    public void padX(double low, double high){
        double  lowMove = rect.width*low;
        double highMove = rect.width*high;
        rect.width = rect.width + lowMove + highMove;
        rect.x = rect.x - lowMove;
    }
    
    
    public void padY(double low, double high){
        double  lowMove = rect.height*low;
        double highMove = rect.height*high;
        rect.height = rect.height + lowMove + highMove;
        rect.y = rect.y - lowMove;
    }
    
    public static void main(String[] args){
        DataRange  range = new DataRange(0.0,1.0,0.0,1.0);
        DataRange range2 = new DataRange(0.5,1.5,0.8,1.2);
        
        System.out.println(range.getRange());
        System.out.println(range2.getRange());
        
        range.grow(range2.getRange());
        System.out.println("after");
        System.out.println(range.getRange());
        
        range.padX(0, 0.15);
        System.out.println(range.getRange());
        
        range.padY(0.3, 0.3);
        System.out.println(range.getRange());
        
        range.growY(0.2);
        System.out.println(range.getRange());
    }
}
