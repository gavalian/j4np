/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.rich;

import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Shape3D;
import j4np.geom.prim.Triangle3D;
import j4np.geom.prim.Vector3D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class RichGeometry {
    
    private List<Shape3D>   mirrors = new ArrayList<>();
    private Shape3D         aerogel = null;
    Shape3D                 sensors = null;
    private double         boxSizeX = 1.;
    private double         boxSizeY = 1.;
    
    public RichGeometry(){
        construct();
    }
    
    protected final void construct(){
        mirrors.clear();
        double[] rotateY = new double[]{Math.PI/2.0,Math.PI/2.0,Math.PI/2.0,Math.PI/2.0};
        double[] moveX   = new double[]{0.5,0.5,0.5,0.5};
        double[] rotateZ = new double[]{0.0,Math.PI/2.0,Math.PI,3.0*Math.PI/2.0};
        for(int i = 0; i < 4; i++){
            Shape3D square = Shape3D.squareXY(boxSizeX,boxSizeY);
            square.rotateY(rotateY[i]);
            square.translateXYZ(moveX[i], 0.0, boxSizeY/2.0);
            square.rotateZ(rotateZ[i]);
            mirrors.add(square);
        }
        
        this.aerogel =  Shape3D.squareXY(boxSizeX,boxSizeY);
        this.sensors =  Shape3D.squareXY(boxSizeX,boxSizeY);
        this.sensors.translateXYZ(0, 0, boxSizeY);
    }
    
    public void move(double x, double y, double z){
        this.aerogel.translateXYZ(x, y, z);
        this.sensors.translateXYZ(x, y, z);
        for(Shape3D s : mirrors) s.translateXYZ(x, y, z);
    }
    
    public Point3D getIntersection(Line3D line){
        List<Point3D> points = new ArrayList<>();
        this.aerogel.intersection(line, points);
        if(points.isEmpty()==false) return points.get(0);
        return null;
    }
    
    public Point3D getSensorCross(Line3D line){
        List<Point3D> points = new ArrayList<>();
        this.sensors.intersection(line, points);
        if(points.isEmpty()==false) return points.get(0);
        return null;
    }
    
    public int getMirrorCross(Line3D line){
        int order = -1;
        List<Point3D> points = new ArrayList<>();
        
        for(int i = 0; i < this.mirrors.size(); i++){
            mirrors.get(i).intersection(line, points);
            if(points.isEmpty()==false) return i;
        }
        return order;
    }
    
    public Point3D getHit(Line3D line){
        
        List<Point3D> points = new ArrayList<>();
        
        int order = this.getMirrorCross(line);
        if(order<0) return this.getSensorCross(line);
        for(int i = 0; i < mirrors.get(order).size(); i++){
            Triangle3D tri = (Triangle3D) mirrors.get(order).face(i);
            tri.intersection(line, points);
            if(points.isEmpty()==false){
                Line3D reflection = new Line3D();
                tri.reflection(line, reflection);
                return this.getSensorCross(reflection);
            }
        }
        //System.out.println(" order = " + order);
        return null;
    }
    
    public static Line3D getLine(Line3D line, double phi, double theta){
        
        Vector3D ex = new Vector3D(1,0.0,0.0);
        ex.rotateZ(phi);
        
        Vector3D ldir = line.toVector();
        ldir.unit();
        
        Vector3D n = ldir.cross(ex);
        n.unit();
        
        double move = 1.0/Math.tan(theta);
        Line3D   original = new Line3D(line.origin(),ldir);
        Point3D     ortho = original.lerpPoint(move);
        Line3D perp = new Line3D(ortho,n);
        
        return new Line3D(line.origin().x(),line.origin().y(),line.origin().z(),
                perp.end().x(),perp.end().y(),perp.end().z());
    }
    
    public void process(RichParticle p){
        p.getHits().clear();
        Line3D line = p.getLine();
        Point3D point = this.getIntersection(line);
        if(point==null) {
            p.intersection().origin().set(0., 0., 0.);
            p.intersection().end().set(0., 0., 0.);
            return;
        }
        p.intersection().origin().set(point.x(), point.y(), point.z());
        double   length = line.length();
        double distance = point.distance(line.origin());
        double scale = distance/length;
        Point3D lerp = line.lerpPoint(scale*1.2);
        p.intersection().end().set(lerp.x(), lerp.y(), lerp.z());
    }
    
    public void getHits(Random r, RichParticle p, double coneTheta, int nHits){
        for(int k = 0; k < nHits; k++){
            double phi = r.nextDouble()*2.0*Math.PI;
            Line3D line = RichGeometry.getLine(p.intersection(), phi, coneTheta);
            Point3D pos = this.getHit(line);
            if(pos!=null){ 
                p.getHits().add(new RichHit(pos.x(),pos.y(),pos.z()));
            }
        }
    }
    
    public static double getRingTheta(double mass, double momentum){
        double    n = 1.05;
        double beta = momentum/Math.sqrt(mass*mass+momentum*momentum);
        double  cos = 1.0/(n*beta);
        return Math.acos(cos);
    }
}
