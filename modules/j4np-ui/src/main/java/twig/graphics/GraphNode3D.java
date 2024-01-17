/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import j4np.geom.prim.Camera3D;
import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Quad3D;
import j4np.geom.prim.Screen3D;
import j4np.graphics.Node2D;
import j4np.graphics.Translation2D;
import j4np.graphics.d3.Node3D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.config.TAxisAttributes;
import twig.config.TStyle;
import twig.data.DataSet;
import twig.data.Graph3D;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDataFactory;

/**
 *
 * @author gavalian
 */
public class GraphNode3D extends Node2D {
    
    
    Camera3D   camera = new Camera3D(18,new Point3D(0,0,-6));
    Screen3D   screen = new Screen3D(1,1);
    
    TGAxis axisX = new TGAxis(TAxisAttributes.AxisType.AXIS_X);
    TGAxis axisY = new TGAxis(TAxisAttributes.AxisType.AXIS_X);
    TGAxis axisZ = new TGAxis(TAxisAttributes.AxisType.AXIS_X);
    
    
    Graph3D graph3D = null;
    String  options = "";
    
    
    List<DataCollection>  collection = new ArrayList<>();
    
    public GraphNode3D(Graph3D g3d, String opt){
        super(100,100);
        this.graph3D = g3d; this.options = opt;
        this.addData(g3d, options);
        this.refreshCamera();
    }
    
    public final void addData(DataSet ds, String option){
        collection.add(new DataCollection(ds,option));        
    }
    
     protected final void refreshCamera(){
        this.camera.setViewingAngleDeg(18);
        this.camera.incrementRotationX(Math.toRadians(30));
        this.camera.incrementRotationY(Math.toRadians(-30));
        
        //this.camera.setOperture(((double) this.cameraOperture)/100.0 );
        //this.camera.movePosition(-6);
        //camera.moveDirection(Math.toRadians(-30), Math.toRadians(30));
    }
     
    public void drawAxis3D(Graphics2D g2d, double x1, double y1, double x2, double y2){
        Point3D p = new Point3D(x1,y1,0.0);
        double angle = Math.atan2(y2-y1, x2-x1);
        //System.out.println(" angle = " + Math.toDegrees(angle));
        
        AffineTransform original = g2d.getTransform();
        //AffineTransform at = new AffineTransform();
        //at.setToRotation(Math.toRadians(Math.toDegrees(angle)),0,0);
        //at.scale(2, 2);        
        //g2d.setTransform(at);
        
        g2d.rotate(Math.toRadians(Math.toDegrees(angle)));
        double length = Math.sqrt((x1-x2)*(x1-x2)+(y2-y1)*(y2-y1));
        p.rotateZ(-angle);
        Rectangle2D r = new Rectangle2D.Double(p.x(), p.y(), length, 0);
        //Translation2D tr = new Translation2D(0,1,0,1);
        Translation2D tr = new Translation2D(1,0,1,0);
        
        TGAxis axis = new TGAxis(TAxisAttributes.AxisType.AXIS_X);
        axis.getAttributes().setAxisBoxDraw(Boolean.FALSE);
        axis.getAttributes().setAxisTickMarkSize(-8);
        axis.getAttributes().setAxisTickMarkCount(5);
        
        //axis.getAttributes().setAxisTitle(this.d);//String.format("axis rotation %.2f", Math.toDegrees(angle)));
        axis.draw(g2d, r, tr);
        //-------------
        g2d.setTransform(original);
    }
    
    @Override
    public void applyMouseDrag(int x, int y, int xmove, int ymove){      
        double xdrag = (xmove-x)/screen.getWidth();
        double ydrag = (ymove-y)/screen.getHeight();
        camera.incrementRotationY(0.4*xdrag);
        camera.incrementRotationX(0.4*ydrag);
        camera.adjust();
       
    }
    
    public void drawAxis3D(Graphics2D g2d, TGAxis axis, double x1, double y1, double x2, double y2){
        Point3D p = new Point3D(x1,y1,0.0);
        double angle = Math.atan2(y2-y1, x2-x1);
        //System.out.println(" angle = " + Math.toDegrees(angle));
        
        AffineTransform original = g2d.getTransform();
        //AffineTransform at = new AffineTransform();
        //at.setToRotation(Math.toRadians(Math.toDegrees(angle)),0,0);
        //at.scale(2, 2);        
        //g2d.setTransform(at);
        
        g2d.rotate(Math.toRadians(Math.toDegrees(angle)));
        double length = Math.sqrt((x1-x2)*(x1-x2)+(y2-y1)*(y2-y1));
        p.rotateZ(-angle);
        Rectangle2D r = new Rectangle2D.Double(p.x(), p.y(), length, 0);
        //Translation2D tr = new Translation2D(0,1,0,1);
        Translation2D tr = new Translation2D(axis.getRange().max(),axis.getRange().min(),1,0);
        
        //TGAxis axis = new TGAxis(TAxisAttributes.AxisType.AXIS_X);
        axis.getAttributes().setAxisType(TAxisAttributes.AxisType.AXIS_X);
        axis.getAttributes().setAxisBoxDraw(Boolean.FALSE);
        axis.getAttributes().setAxisTickMarkSize(-8);
        axis.getAttributes().setAxisTickMarkCount(5);
       
        //axis.getAttributes().setAxisTitle(this.d);//String.format("axis rotation %.2f", Math.toDegrees(angle)));
        axis.draw(g2d, r, tr);
        //-------------
        g2d.setTransform(original);
    }
    
    public void drawAxis(Graphics2D g2d, Screen3D screen){
        Line3D line = new Line3D(-0.5,-0.5,-0.5, 0.5,-0.5,-0.5);
        BasicStroke stroke = new BasicStroke(1);
        g2d.setStroke(stroke);g2d.setColor(Color.BLACK);
        //camera.drawLine(g2d, screen, line, stroke, Color.BLACK);
        
        Line3D projected = new Line3D();
        camera.getLine(line, projected);
        //axisX.getAttributes().setAxisTitle(this.dataHist.attr().getTitleX());
        
        this.drawAxis3D(g2d, axisX,
                screen.getX(projected.origin().x()),
                screen.getY(projected.origin().y()),
                screen.getX(projected.end().x()),
                screen.getY(projected.end().y())
                );
        
        line.translateXYZ(0, 0, 1);
        camera.drawLine(g2d, screen, line, stroke, Color.BLACK);
        
        line.set(
                0.5, -0.5, -0.5,
                0.5, -0.5,  0.5
        );
        camera.getLine(line, projected);
        //axisY.getAttributes().setAxisTitle(this.dataHist.attr().getTitleY());
        this.drawAxis3D(g2d, axisY,
                screen.getX(projected.origin().x()),
                screen.getY(projected.origin().y()),
                screen.getX(projected.end().x()),
                screen.getY(projected.end().y())
                );
        
        
        line.translateXYZ(-1, 0, 0);
        camera.drawLine(g2d, screen, line, stroke, Color.BLACK);
        
        line.set(
                -0.5,  0.5, -0.5,
                -0.5, -0.5, -0.5
        );
        
        camera.getLine(line, projected);
        this.drawAxis3D(g2d, axisZ,
                screen.getX(projected.origin().x()),
                screen.getY(projected.origin().y()),
                screen.getX(projected.end().x()),
                screen.getY(projected.end().y())
                );
        //line.translateXYZ(0.0, 0.0, 1);
        //camera.drawLine(g2d, screen, line, stroke, Color.BLACK);
    }
    @Override
    public void drawLayer(Graphics2D g2d, int layer){
        
        Rectangle2D bounds = this.getParent().getBounds().getBounds();
        //System.out.println(this.getBounds().getBounds());
        //this.dataHist.normalize(this.dataHist.getMaximum());                        
        screen.set(bounds.getWidth(),bounds.getHeight());
        screen.setOffsets(bounds.getX(), bounds.getY());
        //screen.setScale(bounds.getHeight()/bounds.getWidth(), 1);
        //screen.setScale(bounds.getWidth()/bounds.getHeight(), 1);        
        //screen.setOffsets(bounds.getX(), bounds.getY());        
        //camera.setAspectRatio(bounds.getWidth()/bounds.getHeight());
        camera.setAspectRatio(1);
        Line3D line = new Line3D(-0.5,-0.5,-0.5,0.5,0.5,0.5);
        Point3D p1 = new Point3D(0.5,0.5,0.5);
        Point3D p2 = new Point3D(0.5,0.5,0.5);
        
        //camera.drawLine(g2d, screen, line, defaultStroke, Color.BLACK);
        //double max = this.dataHist.getMaximum();
        axisZ.setLimits(0, 4);
        //this.drawWalls( g2d, screen);
        this.drawAxis(  g2d, screen);
        BasicStroke stroke = new BasicStroke(2);
       /*
       int size = this.graph3D.getVextorX().size();
       
       Point3D[] points = new Point3D[size];
       for(int k = 0; k < size; k++){
           points[k] = new Point3D(
                   graph3D.getVextorX().getValue(k),
                   graph3D.getVextorZ().getValue(k)-0.5,
                   graph3D.getVextorY().getValue(k)
           );
       }
       BasicStroke stroke = new BasicStroke(2);
       if(options.contains("F")) 
           camera.fillPath(g2d, screen, true, new Color(255,45,45,180), 
                   new Color(255,45,45,180), stroke, points);
       if(options.contains("L")) camera.drawPath(g2d, screen, false, Color.RED, stroke, points);
       if(options.contains("P"))camera.drawPoints(g2d, screen, Color.red, 4, points);
       */
       
       for(int k = 0; k < collection.size(); k++){
           Graph3D g3d = (Graph3D) collection.get(k).data;
           String option = collection.get(k).options;
           Point3D[] pnt = getPoints(g3d);
           int fc = g3d.attr().getFillColor();
           int lc = g3d.attr().getLineColor();
           Color cf = TStyle.getInstance().getPalette().getColor(fc);
           Color cl = TStyle.getInstance().getPalette().getColor(lc);
           if(options.contains("F"))
               camera.fillPath(g2d, screen, true, cf,cl, stroke, pnt);
       }
    }
    
    public Point3D[] getPoints(Graph3D g3d){
         int size = g3d.getVextorX().size();
        Point3D[] points = new Point3D[size];
       for(int k = 0; k < size; k++){
           points[k] = new Point3D(
                   g3d.getVextorX().getValue(k),
                   g3d.getVextorZ().getValue(k)-0.5,
                   g3d.getVextorY().getValue(k)
           );
       } return points;
    }
    
    public static class DataCollection {
        public DataSet data = null;
        public String  options = "";
        public DataCollection(DataSet d, String opt){
            data = d; options = opt;
        }
    }
    public static void main(String[] args){
        
        H1F h = TDataFactory.createH1F(12000, 50, -0.5, 0.5, 0.1, 0.18);
        H1F h2 = TDataFactory.createH1F(12000, 50, -0.5, 0.5, -0.1, 0.18);
        H1F h3 = TDataFactory.createH1F(12000, 50, -0.5, 0.5, 0.3, 0.18);
        System.out.println(h);
        Graph3D g3d = new Graph3D();
        Random r = new Random();
        for(int i = 0; i < 400; i++){
            g3d.addPoint(r.nextDouble()-0.5,
                    r.nextDouble()-0.5,
                    r.nextDouble()-0.5);
            //g3d.addPoint(0, 0.1, 0.05);
            //g3d.addPoint(0, 0.25, .05);
            //g3d.addPoint(0, 0.3, 0.25);
        }
        h.unit(); h2.unit(); h3.unit();
        Graph3D g3h  =  h.getGraph3D( -0.5);
        Graph3D g3h2 = h2.getGraph3D( -0.4);
        Graph3D g3h3 = h3.getGraph3D( -0.3);
        
        g3h.attr().setFillColor(42);
        g3h2.attr().setFillColor(42);
        g3h3.attr().setFillColor(42);
        GraphNode3D g3node = new GraphNode3D(g3h,"F");
        
        g3node.addData(g3h2, "F");
        g3node.addData(g3h3, "F");
        TGCanvas c2 = new TGCanvas(800,800);
        c2.view().region(0).replace(g3node);
    }
}
