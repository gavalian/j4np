/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author gavalian
 */
public class Translation2D {
    
    private Rectangle2D  transBounds = new Rectangle2D.Double();
    
    public Translation2D(double xmin, double xmax, double ymin, double ymax){
        transBounds.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
    }
    
    public void set(double xmin, double xmax, double ymin, double ymax){
        transBounds.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
    }
    
    public double getLengthX(double length, Rectangle2D r){
        double fraction = length/transBounds.getWidth();
        return r.getWidth()*fraction;
    }
    
    public double getLengthY(double length, Rectangle2D r){
        double fraction = length/transBounds.getHeight();
        return r.getHeight()*fraction;
    }
    
    public double getX(double x, Rectangle2D r){
        double fraction = (x-transBounds.getX())/transBounds.getWidth();
        return r.getX()+fraction*r.getWidth();
    }
    
    public double getY(double y, Rectangle2D r){
        double fraction = (y-transBounds.getY())/transBounds.getHeight();
        return r.getY()+fraction*r.getHeight();
    }
    
    public double relativeX(double x, Rectangle2D r){
        double fraction = (x-transBounds.getX())/transBounds.getWidth();
        return fraction*r.getWidth();
    }
    
    public double relativeY(double y, Rectangle2D r){
        double fraction = (y-transBounds.getY())/transBounds.getHeight();
        return fraction*r.getHeight();
    }
    
    public void show(){
        System.out.printf("translation 2d:>> x = (%8.4f,%8.4f), y = (%8.4f,%8.4f)\n",
                transBounds.getX(),transBounds.getWidth(),
                transBounds.getY(),transBounds.getHeight()
                );
    }
    
}
