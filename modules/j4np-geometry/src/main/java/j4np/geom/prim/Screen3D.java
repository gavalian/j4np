/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.prim;

/**
 *
 * @author gavalian
 */
public class Screen3D {
    
    public double   width = 1;
    public double  height = 1;
    
    public double  factorX = 1.0;
    public double  factorY = 1.0;
    
    public double offsetX = 0.0;
    public double offsetY = 0.0;
    
    public Screen3D(double w, double h){
        width = w; height = h;
    }
    
    public Screen3D setOffsets(double xo, double yo){
        offsetX = xo; offsetY = yo; return this;
    }
    
    public Screen3D set(double w, double h){
        width = w; height = h; return this;
    }
    
    public void setScale(double sx, double sy){
        factorX = sx; factorY = sy;
    }
    
    public double getX(double x){
        return offsetX + width*0.5 + x*factorX*width;
    }
    
    public double getY(double y){
        return offsetY + height*0.5- y*factorY*height;
    }
    
    public double getWidth(){ return this.width;}
    public double getHeight(){ return this.height;}
}
