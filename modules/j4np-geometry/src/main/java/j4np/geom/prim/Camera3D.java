/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package j4np.geom.prim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

/**
 *
 * @author gavalian
 */
public class Camera3D implements Transformable {
    
    private double     aspectRatio   =  16.0/9.0;
    private double     operture      =  0.5;
    private Rectangle     canvasSize =  new Rectangle();
    private double      viewingAngle = Math.toRadians(60.0);
    private double      viewingAngleTan = Math.tan(Math.toRadians(60.0));
    
    private final Point3D   cameraPoint  = new Point3D();
    private final Vector3D  cameraNormal = new Vector3D();
    private final CoordAxis   cameraAxis = new CoordAxis();
    private final Vector3D  cameraDirection = new Vector3D();
    
    private final Plane3D  cameraScreen = new Plane3D();
    
    private Triangle3D     topScreen     = new Triangle3D();
    private Triangle3D     bottomScreen  = new Triangle3D();
           
    private Point3D    defCameraOrigin = new Point3D(0.,0.,-200.0);
    private Vector3D   defCameraNormal = new Vector3D(1.0,0.0,0.0);
    private Plane3D    defCameraScreen = new Plane3D(0.0,0.0,-100,0.0,0.0,1.0);
    private CoordAxis  defCameraAxis   = new CoordAxis();
        
    
    //-----------------------------------------------------------------
    // Internal Line3D variable used for translation - not thread safe.
    private Line3D           cameraObjectLine = new Line3D();
    private Point3D           cameraIntersect = new Point3D();
    private Vector3D    cameraProjectedVector = new Vector3D();
    private Line3D        projectedObjectLine = new Line3D();
    private Point3D       projectedObjectPoint = new Point3D();
    
    //----------------------------------------------------
    public int cameraRotationY =  -30;
    public int cameraRotationX =   30;
    public int cameraPosition  =   -6;
    public int cameraOperture  =   50;
    public int cameraAngle     =   16;
    
    private CameraParameters defaultParameters = new CameraParameters();
    private CameraParameters currentParameters = new CameraParameters();
    
    public static class CameraParameters {
        
        public double rotationX = 0;
        public double rotationY = 0;
        public double viewAngle = Math.toRadians(20);
        public Point3D     position = new Point3D(0.,0.,-6);
        public Vector3D      normal = new Vector3D(1.0,0.0,0.0);
        public Plane3D      plane = new Plane3D(0.0,0.0,-3,0.0,0.0,1.0);
        
        public void copyFrom(CameraParameters p){
            this.rotationX = p.rotationX;
            this.rotationY = p.rotationY;
            this.viewAngle = p.viewAngle;
            position.copy(p.position);
            normal.copy(p.normal);
            plane.copy(p.plane);
        }
    }
    
    public static class CoordAxis {
        
        public Vector3D ex = new Vector3D(1.0,0.0,0.0);
        public Vector3D ey = new Vector3D(0.0,1.0,0.0);
        public Vector3D ez = new Vector3D(0.0,0.0,1.0);
        
        public CoordAxis(){}
        public void setXZ(Vector3D vx, Vector3D vz){ 
           ex.copy(vx);
           ez.copy(vz);
           ey = ez.cross(ex);
        }
        
        public void rotateY(double angle){ex.rotateY(angle);ey.rotateY(angle);ez.rotateY(angle);}
        public void rotateX(double angle){ex.rotateX(angle);ey.rotateX(angle);ez.rotateX(angle);}
        public void rotateZ(double angle){ex.rotateZ(angle);ey.rotateZ(angle);ez.rotateZ(angle);}
    }
    
    public Camera3D(double x, double y, double z){
        defCameraOrigin.set(x, y, z);
        defCameraNormal.setXYZ(1, 0, 0);
    }
    
    public Camera3D(double view, Point3D pos){
        
        this.defaultParameters.position.set(pos.x(),pos.y(), pos.z());
        this.defaultParameters.normal.setXYZ(1, 0, 0);
        this.defaultParameters.rotationX = 0;
        this.defaultParameters.rotationY = 0;
        this.defaultParameters.viewAngle = view;
        
        this.defaultParameters.plane.set(0, 0, pos.z()/2.0, 0, 0, -1);
        this.currentParameters.copyFrom(defaultParameters);
        this.reset();
        
        /*defCameraOrigin.set(0.0,0.0, z);
        defCameraNormal.setXYZ(1, 0, 0);

        defCameraAxis.ex.setXYZ(1, 0, 0);
        defCameraAxis.ey.setXYZ(0, 1, 0);
        defCameraAxis.ez.setXYZ(0, 0, 1);
        
        defCameraScreen.set(0, 0, z/2.0, 0, 0, 1.0);
        this.moveDirection(0.0, 0.0);*/
    }
    
    public Camera3D(CameraParameters cp){
        defaultParameters = cp;
    }
    
    public static Camera3D cameraYZ(double view, double distance){
        Camera3D c = new Camera3D(view, new Point3D(0,0,distance));
        c.rotateY(Math.toRadians(-90));
        return c;
    }
   
    public Camera3D(){        
        this.movePosition(-200);
        this.moveDirection(0.0, 0.0);
    }
    
    public void zoom(double factor){
        double mag = defaultParameters.position.distance(0, 0, 0);
        double phi = Math.atan2(defaultParameters.position.y(),defaultParameters.position.x());
        double  th = Math.acos(defaultParameters.position.z()/mag);
        mag = mag + factor*mag;
        defaultParameters.position.set(
                mag*Math.sin(th)*Math.cos(phi), 
                mag*Math.sin(th)*Math.sin(phi), 
                mag*Math.cos(th)                 
        );
        this.adjust();
    }
    
    public void reset(){
        cameraPoint.set(defaultParameters.position.x(),
                defaultParameters.position.y(),
                defaultParameters.position.z()
        );
        
        
        cameraNormal.setXYZ(defaultParameters.normal.x(),
                defaultParameters.normal.y(),
                defaultParameters.normal.z()
        );
        
        cameraScreen.copy(this.defaultParameters.plane);
        cameraAxis.ex.setXYZ(1,0,0);
        cameraAxis.ey.setXYZ(0,1,0);
        cameraAxis.ez.setXYZ(0,0,1);
    }
    
    
    public void setViewingAngleDeg(double angleDeg){
        this.setViewingAngle(Math.toRadians(angleDeg));
    }
    
    public void setViewingAngle(double angle){
        this.viewingAngle = angle;
        this.viewingAngleTan = Math.tan(angle);
    }
    
    public void setOperture(double fraction){
        this.operture = fraction;
    }
    
    public final void movePosition( double z){
        defCameraOrigin.set(    0.0, 0.0,   z);
        defCameraNormal.setXYZ( 1.0 ,0.0, 0.0);        
        defCameraScreen.set(0.0,0.0,z*operture,0.0,0.0,1.0);
        defCameraAxis.ex.setXYZ(1, 0, 0);
        defCameraAxis.ey.setXYZ(0, 1, 0);
        defCameraAxis.ez.setXYZ(0, 0, 1);
    }
    
    public void adjust(){
        
        reset();
        
        this.setViewingAngleDeg(this.currentParameters.viewAngle);
        
        cameraPoint.rotateX(this.currentParameters.rotationX);
        cameraNormal.rotateX(this.currentParameters.rotationX);
        cameraScreen.rotateX(this.currentParameters.rotationX);
        cameraAxis.rotateX(this.currentParameters.rotationX);
        
        cameraPoint.rotateY(this.currentParameters.rotationY);  
        cameraNormal.rotateY(this.currentParameters.rotationY);         
        cameraScreen.rotateY(this.currentParameters.rotationY);         
        cameraAxis.rotateY(this.currentParameters.rotationY); 
        //this.moveDirection(Math.toRadians(cameraRotationY), Math.toRadians(cameraRotationX));
        
    }
    
    public void incrementRotationY(double step){
        this.currentParameters.rotationY += step;
        this.adjust();
    }
    
    public void incrementRotationX(double step){
        this.currentParameters.rotationX += step;
        this.adjust();
    }
    
    public void incrementViewAngle(double step){
        this.currentParameters.viewAngle += step;
        this.adjust();
    }
    
    public Point3D getLocation(){ return cameraPoint;}
    
    public final void moveDirection(double angleY, double angleX){
                
        
        cameraScreen.copy(defCameraScreen);
        cameraAxis.ex.copy(defCameraAxis.ex);
        cameraAxis.ey.copy(defCameraAxis.ey);
        cameraAxis.ez.copy(defCameraAxis.ez);
        
        /*cameraPoint.rotateY(angleY);  cameraPoint.rotateX(angleX);
        cameraNormal.rotateY(angleY); cameraNormal.rotateX(angleX);
        cameraScreen.rotateY(angleY); cameraScreen.rotateX(angleX);
        cameraAxis.rotateY(angleY); cameraAxis.rotateX(angleX);
        */
        cameraPoint.rotateX(angleX);
        cameraPoint.rotateY(angleY);  
        cameraNormal.rotateX(angleX);
        cameraNormal.rotateY(angleY); 
        cameraScreen.rotateX(angleX);
        cameraScreen.rotateY(angleY); 
        cameraAxis.rotateX(angleX);
        cameraAxis.rotateY(angleY); 
    }
    
    
    @Override
    public void translateXYZ(double dx, double dy, double dz) {
        cameraPoint.translateXYZ(dx, dy, dz);
        cameraNormal.translateXYZ(dx, dy, dz);
        cameraDirection.translateXYZ(dx, dy, dz);
        cameraScreen.translateXYZ(dx, dy, dz);
        this.setDistance(this.operture);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rotateX(double angle) {
        this.cameraPoint.rotateX(angle);
        this.cameraNormal.rotateX(angle);
        this.cameraDirection.rotateX(angle);
        this.cameraScreen.rotateX(angle);
        //this.setDistance(this.operture);
        //cameraNormal.rotateX(angle);
    }

    @Override
    public void rotateY(double angle) {
        this.cameraPoint.rotateY(angle);
        this.cameraNormal.rotateY(angle);
        this.cameraDirection.rotateY(angle);
        this.cameraScreen.rotateY(angle);
        //this.setDistance(this.operture);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setCanvasSize(int w, int h){
        this.canvasSize.x = 0;
        this.canvasSize.y = 0;
        this.canvasSize.width  = w;
        this.canvasSize.height = h;
    }
    
    public int getCanvasX(Point3D point){
        //int rx = x - this.cameraProjectionPlane.point().x();
        Vector3D ydir = this.cameraDirection.cross(cameraNormal);
        Vector3D pt = new Vector3D(point.x(),point.y(),point.z());
        double xc = this.cameraNormal.dot(pt);
        return (int) (xc+this.canvasSize.width/2.0); 
    }
    
    public int getCanvasY(Point3D point){
        //int rx = x - this.cameraProjectionPlane.point().x();
        Vector3D ydir = this.cameraDirection.cross(cameraNormal);
        
        Vector3D pt = new Vector3D(point.x(),point.y(),point.z());
        double yc = ydir.dot(pt);
        double length = this.canvasSize.height/2;
        return (int) (yc+this.canvasSize.height/2.0); 
    }
    
    @Override
    public void rotateZ(double angle) {
        this.cameraPoint.rotateZ(angle);
        this.cameraNormal.rotateZ(angle);
        this.cameraDirection.rotateZ(angle);
        this.cameraScreen.rotateZ(angle);
        //this.setDistance(this.operture);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setAspectRatio(double ratio){
        this.aspectRatio = ratio;
    }
    
    public void setDistance(double distance){
        this.operture = distance;
        
        Vector3D  normal = new Vector3D(
                -this.cameraNormal.x(),
                -this.cameraNormal.y(),
                -this.cameraNormal.z()
        );
        
        Point3D point = new Point3D(
                this.operture*this.cameraNormal.x(),
                this.operture*this.cameraNormal.y(),
                this.operture*this.cameraNormal.z()
        );
        
        this.cameraScreen.set(point, normal);
    }
    
    public Point3D  getPoint(Point3D point){
        Line3D  line = new Line3D(point.x(),point.y(),point.z(),
                cameraPoint.x(),cameraPoint.y(),cameraPoint.z());
        Point3D interPoint = new Point3D();
        this.cameraScreen.intersection(line, interPoint);
        return interPoint;
    }
    
    public void getPoint(Point3D original, Point3D projected){
        this.cameraObjectLine.set(
                original.x(),original.y(),original.z(),
                cameraPoint.x(),cameraPoint.y(),cameraPoint.z()        
        );
        cameraScreen.intersection(cameraObjectLine, cameraIntersect);
        cameraProjectedVector.setXYZ(
                cameraIntersect.x()-cameraScreen.point().x(),
                cameraIntersect.y()-cameraScreen.point().y(),
                cameraIntersect.z()-cameraScreen.point().z()
        );
        double d = cameraPoint.distance(cameraScreen.point());
        //System.out.println(" aspect ratio = " + this.aspectRatio);
        double maxX = d*this.viewingAngleTan;
        double maxY = d*this.viewingAngleTan/this.aspectRatio;
        
        if(cameraProjectedVector.mag()<0.000001){ projected.set(0.0, 0.0, 0.0); return; }
        double dirX = cameraProjectedVector.dot(cameraAxis.ex)/cameraProjectedVector.mag();
        double dirY = cameraProjectedVector.dot(cameraAxis.ey)/cameraProjectedVector.mag();
        projected.set(cameraProjectedVector.mag()*dirX/maxX, cameraProjectedVector.mag()*dirY/maxY,0.0);        
    }
    
    public void getLine(Line3D original, Line3D projected){
        getPoint(original.origin(), projected.origin());
        getPoint(original.end(), projected.end());
    }
    
    public void drawPoints(Graphics2D g2d, Screen3D screen, Color c, int size, Point3D... points){
        g2d.setColor(c);
        Point3D projected = new Point3D();
        int halfsize = size/2;
        //System.out.println("--- draw points");
        for(int i = 0; i < points.length; i++){
            getPoint(points[i],projected);
            g2d.fillOval(
                    (int) (screen.getX(projected.x())-halfsize),
                    (int)( screen.getY(projected.y())-halfsize)
                    , size, size);
            //points[i].show();
            //projected.show();
        }
        
    }
    public void drawPath(Graphics2D g2d, Screen3D screen, boolean closed, Color c, Stroke stroke, Point3D... points){
        GeneralPath path = new GeneralPath();
        getPath(path,screen, closed,points);
        g2d.setColor(c);g2d.setStroke(stroke);
        g2d.draw(path);
    }
    
    public void fillPath(Graphics2D g2d, Screen3D screen,boolean closed, Color fc, Color c, 
            Stroke stroke, Point3D... points){
        GeneralPath path = new GeneralPath();
        getPath(path,screen, closed,points);
        g2d.setColor(fc);
        g2d.fill(path);
        if(c!=null){
            g2d.setColor(c);g2d.setStroke(stroke);
            g2d.draw(path);
        }
    }
    
    public void getPath(GeneralPath path, Screen3D screen, boolean closed, Point3D... points){
        path.reset();
        this.getPoint(points[0], projectedObjectPoint);
        path.moveTo(
                screen.getX(projectedObjectPoint.x()),
                screen.getY(projectedObjectPoint.y()));
        for(int i = 1; i < points.length; i++){
            this.getPoint(points[i], projectedObjectPoint);
            path.lineTo(
                    screen.getX(projectedObjectPoint.x()),
                    screen.getY(projectedObjectPoint.y()));
        }
        if(closed==true){
            this.getPoint(points[0], projectedObjectPoint);
            path.lineTo(
                    screen.getX(projectedObjectPoint.x()),
                    screen.getY(projectedObjectPoint.y()));
        }
    }
    
    public void drawLine(Graphics2D g2d, Screen3D screen, Line3D original, Stroke stroke, Color color){
        g2d.setColor(color); g2d.setStroke(stroke);
        getLine(original,projectedObjectLine);
        g2d.drawLine(
                (int) screen.getX(projectedObjectLine.origin().x()),
                (int) screen.getY(projectedObjectLine.origin().y()),
                (int) screen.getX(projectedObjectLine.end().x()),
                (int) screen.getY(projectedObjectLine.end().y())
        );
    }
    
    public Point3D getPoint2(Point3D point){
        Line3D  line = new Line3D(point.x(),point.y(),point.z(),
                cameraPoint.x(),cameraPoint.y(),cameraPoint.z());
        Point3D ip = new Point3D();
        this.cameraScreen.intersection(line, ip);
        Vector3D vec = new Vector3D(
                ip.x()-cameraScreen.point().x(),
                ip.y()-cameraScreen.point().y(),
                ip.z()-cameraScreen.point().z()
        );
        
        
        
        
        
        double d = cameraPoint.distance(cameraScreen.point());
        double maxX = d*this.viewingAngleTan;
        double maxY = d*this.viewingAngleTan/this.aspectRatio;
        
        if(vec.mag()<0.000001) return new Point3D(0.0,0.0,0.0);
        double dirX = vec.dot(cameraAxis.ex)/vec.mag();
        double dirY = vec.dot(cameraAxis.ey)/vec.mag();
        Point3D result = new Point3D(vec.mag()*dirX/maxX, vec.mag()*dirY/maxY,0.0);
        //System.out.printf(" distance = %f, direction = %f , X/Y %f / %f ,  max  = %f / %f\n",
        //        vec.mag(), Math.toDegrees(Math.acos(dirX)), result.x(),result.y(), maxX, maxY);
        //double length = cameraScreen.point().distance(interPoint);


        return result;
        //return ip;
    }
    
    public Line3D  getLine(Line3D line){
        Point3D p1 = this.getPoint(line.origin());
        Point3D p2 = this.getPoint(line.end());
        return new Line3D(p1,p2);
    }

    public void show(){
        System.out.println(" Camera : ");
        System.out.println(this.cameraPoint);
        System.out.println(this.cameraNormal);
        System.out.println(this.cameraScreen);
    }    
    
    public static void main(String[] args){
       
        Camera3D c = new Camera3D(15,new Point3D(0,0,-6));        
        c.show();
        
        
        Point3D p1 = new Point3D(0.0,10.0,0.0);
        Point3D p2 = new Point3D(0.0,10.0,0.0);
        
        c.getPoint(p1,p2);
        System.out.println(p2);
        
        //c.moveDirection(0, Math.toRadians(30));
        //c.getPoint2(p);
        
        //c.moveDirection(Math.toRadians(-30), 0);        
        //c.show();
        
        //Point3D p = new Point3D(0.0,0.0,-200);
        //p.rotateX(Math.toRadians(90));
        
        //System.out.println(p);
        

        
        /*
        Point3D p = new Point3D(0.0,50.0,80);        
        Point3D p1 = c.getPoint(p);
        int iter = 1000000;
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            Point3D p2 = c.getPoint(p);
        }
        long now = System.currentTimeMillis();
        System.out.printf(" %d iterations in %d msec \n",iter, now - then);
        System.out.println(p1);*/
    }
}
