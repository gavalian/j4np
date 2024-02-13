/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.detector.dc;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Path3D;
import j4np.geom.prim.Point3D;
import java.awt.geom.Point2D;

/**
 *
 * @author gavalian
 */
public class DriftChambers {
    
    private Line3D boundaryLeft  = new Line3D();
    private Line3D boundaryRight = new Line3D();
    private double    wedgeAngle = Math.toRadians(30.0);
    private double     topHeight = 300.0;    
    
    private double[]   sLayerHeight = new double[]{1,2,4,5,7,8};
    private double     sLayerOffset = 50.0;
    private double      sLayerScale = 10.0;
    private double      layerOffset = 1.0;
    
    public DriftChambers(){
        boundaryLeft.set( 0, 0, 0, -topHeight*Math.tan(wedgeAngle), topHeight, 0);
        boundaryRight.set(0, 0, 0,  topHeight*Math.tan(wedgeAngle), topHeight, 0);
    }
    
    public DriftChambers setScale(  double sc ){ this.sLayerScale = sc;   return this;}
    public DriftChambers setOffset( double off){ this.sLayerOffset = off; return this;}
    public DriftChambers setLayerOffset( double off){ this.layerOffset = off; return this;}
    public DriftChambers setAngle(  double angle ){ this.wedgeAngle = angle;   return this;}
    
    public void getSuperLayerLine(Line3D line, int slayer){
        double h = sLayerHeight[slayer-1]*sLayerScale + sLayerOffset;
       line.set(-h*Math.tan(wedgeAngle), h, 0.0, h*Math.tan(wedgeAngle), h, 0.0);
    }
    
    public void getLayerLine(Line3D line, int layer){                
        int slayer = (layer-1)/6 + 1;
        int llayer = (layer-1)%6 + 1;
        
        //System.out.printf("%d %d \n",slayer,llayer);
        double h = sLayerHeight[slayer-1]*sLayerScale + sLayerOffset + llayer*layerOffset;
        line.set(-h*Math.tan(wedgeAngle), h, 0.0, h*Math.tan(wedgeAngle), h, 0.0);
    }
    
    public void getLayerLine(Line3D line, int sector, int layer){                
        this.getLayerLine(line, layer);
        line.rotateZ(Math.toRadians((sector-1)*60.0));
    }
    
    public void getLayerPoint(Point3D p, int layer, double wire){
        Line3D line = new Line3D();
        this.getLayerLine(line, layer);
        double ratio = (wire-1)/111.0;
        Point3D lerp = line.lerpPoint(ratio);
        p.copy(lerp);
    }
    
    public void getLayerPoint(Point3D p, int sector, int layer, double wire){
        this.getLayerPoint(p, layer, wire);
        p.rotateZ(Math.toRadians((sector-1)*60.0));
    }
    
    public void getSuperlayerPath(Path3D path, int superlayer){
        path.clear();
        int bottom = (superlayer-1)*6+1;
        int    top = (superlayer-1)*6+6;        
        Line3D line = new Line3D();
        this.getLayerLine(line, bottom);
        path.addPoint(line.origin().x(),line.origin().y(),line.origin().z());
        path.addPoint(line.end().x(),line.end().y(),line.end().z());
        this.getLayerLine(line, top);
        path.addPoint(line.end().x(),line.end().y(),line.end().z());
        path.addPoint(line.origin().x(),line.origin().y(),line.origin().z());

    }
    
    public static void main(String[] args){
        
        DriftChambers dc = new DriftChambers();        
        Line3D line = new Line3D();    
        
        for(int k = 1; k < 36; k++){
            System.out.println("layer = " + k);
            dc.getLayerLine(line, k);
            System.out.println(line);
        }
        /*System.out.println("\n\n\n******\n");
        dc.getLayerLine(line, 25);
        System.out.println(line);
        Point3D p = new Point3D();
        for(int w = 1; w <= 112; w++){
            dc.getLayerPoint(p, 25, w);
            System.out.println("\t" + p);
        }*/
        
        
        Path3D path = new Path3D();
        dc.getSuperlayerPath(path, 1);
        System.out.println("\n\n" + path);
        /*Point3D p = new Point3D();
        dc.getLayerPoint(p, 1, 1);
        System.out.println(p);
        */
    }
}
