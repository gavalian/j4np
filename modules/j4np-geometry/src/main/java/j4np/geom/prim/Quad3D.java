/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.prim;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Quad3D implements Face3D {
    
    protected Point3D[] points = new Point3D[]{
        new Point3D(), new Point3D(), 
        new Point3D(), new Point3D()
    };
    
    public Quad3D(){
        
    }
    
    public static Quad3D rectXY(double sizeX, double sizeY){
        Quad3D q = new Quad3D();
        q.points[0].set(-sizeX*0.5, -sizeY*0.5,0.0);
        q.points[1].set(-sizeX*0.5,  sizeY*0.5,0.0);
        q.points[2].set( sizeX*0.5,  sizeY*0.5,0.0);
        q.points[3].set( sizeX*0.5, -sizeY*0.5,0.0);
        return q;
    }
    
    public Point3D[] points(){return this.points;}
    
    public static Quad3D rectYZ(double ysize, double zsize){
        Quad3D quad = new Quad3D();
        quad.point(0).set( 0.0, -ysize*0.5, -zsize*0.5);
        quad.point(1).set( 0.0, -ysize*0.5,  zsize*0.5);
        quad.point(2).set( 0.0,  ysize*0.5,  zsize*0.5);
        quad.point(3).set( 0.0,  ysize*0.5, -zsize*0.5);
        return quad;
    }
    
    public static Quad3D rectXZ(double xsize, double zsize){
        Quad3D quad = new Quad3D();
        quad.point(0).set(-xsize*0.5, 0, -zsize*0.5);
        quad.point(1).set(-xsize*0.5, 0,  zsize*0.5);
        quad.point(2).set( xsize*0.5, 0,  zsize*0.5);
        quad.point(3).set( xsize*0.5, 0, -zsize*0.5);
        return quad;
    }
    
    public double distance(Point3D point){
        double distance = 0.0;
        for(Point3D p : points)            
            distance += point.distance(p);
        return distance*0.25;
    }
    
    @Override
    public Point3D point(int index) {
        return points[index];
    }

    @Override
    public int intersection(Line3D line, List<Point3D> intersections) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int intersectionRay(Line3D line, List<Point3D> intersections) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int intersectionSegment(Line3D line, List<Point3D> intersections) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int reflection(Line3D line, Line3D reflection) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void translateXYZ(double dx, double dy, double dz) {
        for(Point3D p : points) p.translateXYZ(dx, dy, dz);
    }

    @Override
    public void rotateX(double angle) {
        for(Point3D p : points) p.rotateX(angle);
    }

    @Override
    public void rotateY(double angle) {
        for(Point3D p : points) p.rotateY(angle);
    }

    @Override
    public void rotateZ(double angle) {
        for(Point3D p : points) p.rotateZ(angle);
    }

    @Override
    public void show() {
        System.out.println("Quad3D : ");
        for(int i = 0; i < points.length; i++)
            System.out.println("\t"+points[i]);
    }
    
    public static class ZBuffer implements Comparator<Quad3D> {
        private Point3D origin = null;
        public ZBuffer(Point3D from){
            origin = from;
        }
        
        @Override
        public int compare(Quad3D o1, Quad3D o2) {
            double d1 = o1.distance(origin);
            double d2 = o2.distance(origin);
            return Double.compare(d2, d1);
        }
        
    }
}
