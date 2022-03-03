/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

/**
 *
 * @author gavalian
 */
public class NodeInsets {
    
    private double insetLeft   = 0.0;
    private double insetRight  = 0.0;
    private double insetTop    = 0.0;
    private double insetBottom = 0.0;
    
    private boolean relativeInsets = false;
    
    public NodeInsets(){
        
    }
    
    public NodeInsets(double top, double left, double bottom, double right){
        this.set(top, left, bottom, right);
    }
    
    public void set(double top, double left, double bottom, double right){
        this.insetTop = top;
        this.insetLeft = left;
        this.insetBottom = bottom;
        this.insetRight = right;
    }
    
    public NodeInsets right(double r){insetRight = r; return this;}
    public NodeInsets top(double t){insetTop   = t; return this;}
    public NodeInsets left(double l){insetLeft  = l; return this;}
    public NodeInsets bottom(double b){insetBottom  = b; return this;}
    
    public double getLeft(){ return insetLeft;}
    public double getRight(){ return insetRight;}
    public double getTop(){ return insetTop;}
    public double getBottom(){ return insetBottom;}
    
    public void     relative(boolean flag){ this.relativeInsets = flag;}
    public boolean  relative(){ return this.relativeInsets;}
    
    public void copyFrom(NodeInsets inset){
        this.insetTop = inset.insetTop;
        this.insetBottom = inset.insetBottom;
        this.insetLeft = inset.insetLeft;
        this.insetRight = inset.insetRight;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("INSETS [ %8.2f %8.2f %8.2f %8.2f]", insetLeft, insetTop, insetRight, insetBottom));
        return str.toString();
    }
}