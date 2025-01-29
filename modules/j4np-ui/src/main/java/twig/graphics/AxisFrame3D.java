/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import j4np.geom.prim.Camera3D;
import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Quad3D;
import j4np.geom.prim.QuadMesh3D;
import j4np.geom.prim.Screen3D;
import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author gavalian
 */
public class AxisFrame3D extends Node2D {
    
    // For 3D plots the golden settings is 18 degrees viewing angle
    // and the camera places at Z=-6.
    Camera3D   camera = new Camera3D(18,new Point3D(0,0,-6));
    
    //Camera3D   camera =  Camera3D.cameraYZ(18, -6);
//Camera3D   camera = new Camera3D(18,new Point3D(0,-6,0));
    Screen3D   screen = new Screen3D(1,1);
    
    QuadMesh3D         axisBox = QuadMesh3D.box(0.5,0.5,0.5);    
    List<Quad3D>  axisBoxFaces = new ArrayList<>();
    
    List<QuadMesh3D>  meshes = new ArrayList<>();    
    List<Line3D>       lines = new ArrayList<>();
        
    
    protected Stroke   lineStroke = new BasicStroke(0.5f);
    
    public AxisFrame3D(){
        super(100,100);
        QuadMesh3D mesh = QuadMesh3D.box(0.2, 0.4, 0.2);
        axisBoxFaces.addAll(axisBox.getFaces());        
        meshes.add(mesh);
        lines.add(new Line3D(-0.5, 0.0, 0.0, 0.5, 0.0, 0.0));
        lines.add(new Line3D( 0.0,-0.5, 0.0, 0.0, 0.5, 0.0));
        lines.add(new Line3D( 0.0, 0.0,-0.5, 0.0, 0.0, 0.5));
        
        this.refreshCamera();
    }
    
    protected final void refreshCamera(){
        //this.camera.setViewingAngleDeg(5);
        
        this.camera.incrementRotationX(Math.toRadians(30));
        this.camera.incrementRotationY(Math.toRadians(-30));       
    }
    
    public void initCosineMeshes(){
        this.meshes.clear();
        int   count = 8;
        double size = 1.0/count;
        
        for(int x = 0; x < count; x++){
            for(int z = 0; z < count; z++){
                double xp = -0.5+x*size+size*0.5;
                double yp = -0.5+z*size+size*0.5;
                //System.out.printf("%8.5f %8.5f\n",xp,yp);
                double zp = 0.5+0.2*(Math.sin(5*yp)*Math.sin(5*xp));
                QuadMesh3D mesh = QuadMesh3D.box(size*0.5, zp*0.5, size*0.5);
                mesh.translateXYZ(xp,-0.5+zp*0.5,yp);
                this.meshes.add(mesh);
            }
        }
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){                
        Rectangle2D bounds = getBounds().getBounds();
        screen.set(bounds.getWidth(),bounds.getHeight());
        screen.setOffsets(bounds.getX(), bounds.getY());
        camera.setAspectRatio(1);
        
        //System.out.println(" CAMERA LOCATION = " + camera.getLocation().toString());
        //g2d.drawRect((int) bounds.getX(), (int) bounds.getY(), 
        //        (int) bounds.getWidth(), (int) bounds.getHeight());
        //g2d.drawLine((int) bounds.getX(), (int) bounds.getY(), 
        //        (int) bounds.getWidth(), (int) bounds.getHeight());
        Point3D location = camera.getLocation();
        Quad3D.ZBuffer sorter = new Quad3D.ZBuffer(location);
        
        /**
         *  Drawing the Axis Box if the axis box draw option is on
         */
        Color axisBoxColor = new Color(220,240,220);
        Collections.sort(axisBoxFaces, sorter);
        
        for(int i = 0; i < 3; i++){
            camera.fillPath(g2d, screen, true, axisBoxColor, Color.BLACK, lineStroke, axisBoxFaces.get(i).points());
        }
        
        /*for(Line3D l : lines){
            //l.show();
            camera.drawLine(g2d, screen, l, lineStroke, Color.BLACK);
        }*/                        
        
        for(QuadMesh3D mesh : meshes){
            List<Quad3D> shapes = new ArrayList<>();
            shapes.addAll(mesh.getFaces());
            //Quad3D.ZBuffer sorter = new Quad3D.ZBuffer(location);
            Collections.sort(shapes, sorter);
            int counter = 0;
            for(Quad3D q : shapes){
                //System.out.printf(" shape # %d - distance =  %8.5f\n",counter,q.distance(location));
                counter++;
                //camera.drawPath(g2d, screen, true, Color.BLACK, lineStroke, q.points());
                //System.out.println(Color.ORANGE);
                camera.fillPath(g2d, screen, true, new Color(255,100,0,150), Color.BLACK, lineStroke, q.points());
            }
        }
    }
    
    @Override
    public void applyMouseClick(MouseEvent e){
         System.out.println("should I do something?");
    }
    
    @Override
    public void applyMouseDrag(int x, int y, int xmove, int ymove){
        //System.out.println(" Oooops, I'm being dragged\n");
        double xdrag = (xmove-x)/screen.getWidth();
        double ydrag = (ymove-y)/screen.getHeight();
        camera.incrementRotationY(0.4*xdrag);
        camera.incrementRotationX(0.4*ydrag);
        camera.adjust();
    }
    
    @Override
    public void applyMouseWheelMoved(MouseWheelEvent e){
        double zoom = e.getPreciseWheelRotation();
        this.camera.zoom(zoom*0.005);
        this.repaint();
        //System.out.printf("[wheel] %8d %8d - zoom = %8.5f\n",e.getX(),e.getY(),e.getPreciseWheelRotation());
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        Canvas2D  canvas = Canvas2D.createFrame(frame, 500, 500);
        //canvas.setBackground(Color.red);
        AxisFrame3D af3d = new AxisFrame3D();
        af3d.initCosineMeshes();
        canvas.addNode(af3d);
        canvas.divide(1,1);
        canvas.repaint();
    }
}

