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
public class NodeRegion2D {
    
    private final Rectangle2D  bounds = new Rectangle2D.Double();
    
    public NodeRegion2D(){
        
    }
    
    public void set(double x, double y, double w, double h){
        bounds.setRect(x, y, w, h);
    }
    
    public Rectangle2D  getBounds(){ return bounds;}
    
    public double getX(){ return bounds.getX();}
    public double getY(){ return bounds.getY();}
    public double getWidth(){ return bounds.getWidth();}
    public double getHeight(){ return bounds.getHeight();}
    
    public void moveTo(double x, double y){
        this.bounds.setRect(x, y, bounds.getWidth(),bounds.getHeight());
    }
    
    public void copyFrom(NodeRegion2D reg){
        this.set(reg.getX(), reg.getY(), reg.getWidth(), reg.getHeight());
    }
    
    public void updateRegion(NodeRegion2D region, NodeRegion2D bind, NodeInsets insets){

        double nw = bind.getWidth()*this.getWidth();
        double nh = bind.getHeight()*this.getHeight();
        double nx = bind.getX()*this.getWidth();
        double ny = bind.getY()*this.getHeight();
        nx += insets.getLeft();
        ny += insets.getTop();
        nw -= (insets.getLeft()+insets.getRight());
        nh -= (insets.getBottom()+insets.getTop());
        region.set(getX() + nx, getY() + ny, nw, nh);
        //System.out.println(" UPDATING RELATIVE POSITIONS : " + region.toString());
    }
    
    public void updateRegion(NodeRegion2D region, NodeInsets insets){        
        if(insets.relative()==true){
            region.set(
                    //bounds.getX() + 
                            insets.getLeft()*bounds.getWidth(),
                    //bounds.getY() + 
                            insets.getTop()*bounds.getHeight(),
                    bounds.getWidth() - (insets.getLeft()+insets.getRight())*bounds.getWidth(),
                    bounds.getHeight() - (insets.getTop()+insets.getBottom())*bounds.getHeight()
                    );
        } else {
            region.set(
                    //bounds.getX() + 
                    insets.getLeft(), 
                    //bounds.getY() + 
                    insets.getTop(),
                    bounds.getWidth() - insets.getLeft()-insets.getRight(),
                    bounds.getHeight() - insets.getTop()- insets.getBottom());
        }
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("REGION [ %9.3f, %9.3f, %9.3f, %9.3f]", 
                 bounds.getX(),
                 bounds.getY(), 
                 bounds.getWidth(),
                 bounds.getHeight()));
        return str.toString();
    }
}
