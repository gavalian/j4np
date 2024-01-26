/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.graphics;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CanvasLayout {
    
    protected List<Rectangle2D> regionLayouts = new ArrayList<>();
    protected NodeInsets      canvasInsets = new NodeInsets(); 
    
    public CanvasLayout add(double xmin, double ymin, double xsize, double ysize){
        regionLayouts.add(new Rectangle2D.Double(xmin, ymin, xsize, ysize));
        return this;
    }
     
    public NodeInsets getInsets(){ return canvasInsets;}
    
    public static CanvasLayout grid(int xdiv, int ydiv){
        CanvasLayout layout = new CanvasLayout();
        double  xStep = 1.0/xdiv;
        double  yStep = 1.0/ydiv;
        int  counter = 0;
        for(int y = 0; y < ydiv; y++){
            for(int x = 0; x < xdiv; x++){
                double xPosition = x*xStep;
                double yPosition = y*yStep;
                //System.out.println(" counter = ");
                //if(counter<graphicsComponents.size()){
                layout.regionLayouts.add(new Rectangle2D.Double(xPosition, yPosition, xStep, yStep));
                //graphicsComponents.get(counter).alignMode(Node2D.ALIGN_RELATIVE);
                //}
                counter++;
            }
        }
        return layout;
    }
    
    public static double[] uniform(int count){
        double[] x = new double[count];
        for(int i = 0; i < count; i++) x[i] = 1.0/count;
        return x;
    }
    
    public CanvasLayout addRow(double ystart, double ysize, double[] xsizes){
        double xstart = 0.0;
        for(int i = 0; i < xsizes.length; i++){
            this.add(xstart, ystart, xsizes[i], ysize);
            xstart += xsizes[i];
        }
        return this;
    }
    
    public CanvasLayout addColumn(double xstart, double xsize, double[] ysizes){
        double ystart = 0.0;
        for(int i = 0; i < ysizes.length; i++){
            this.add(xstart, ystart, xsize, ysizes[i]);
            ystart += ysizes[i];
        }
        return this;
    }
    
    public CanvasLayout addColumn(double xstart, double xsize, int ndiv){
        double[] ysizes = new double[ndiv];
        for(int n = 0; n < ysizes.length; n++) ysizes[n] = 1./ndiv;
        this.addColumn(xstart, xsize, ysizes);
        return this;
    }
    
    public int size(){ return this.regionLayouts.size();}
    public Rectangle2D getBounds(int index) { return regionLayouts.get(index);}
    
    public void show(){
        System.out.println("-- Layout --");
        for(Rectangle2D rect : this.regionLayouts) System.out.println(rect);
    }
    
    public static void main(String[] args){
        CanvasLayout layout = CanvasLayout.grid(2, 2);
        layout.show();
        
        CanvasLayout layout2 = new CanvasLayout();
        layout2.addRow(0.0, 0.25, new double[]{0.1,0.4,0.25,0.25});
        layout2.show();
    }
}
