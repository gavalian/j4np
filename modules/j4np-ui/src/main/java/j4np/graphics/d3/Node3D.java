/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.graphics.d3;

import j4np.geom.prim.Camera3D;
import j4np.geom.prim.CameraView;
import j4np.geom.prim.CameraView.CollectionLine3D;
import j4np.geom.prim.Line3D;
import j4np.geom.prim.Point3D;
import j4np.geom.prim.Quad3D;
import j4np.geom.prim.QuadMesh3D;
import j4np.geom.prim.Screen3D;
import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import twig.config.TAxisAttributes;
import twig.config.TStyle;
import twig.data.H2F;
import twig.data.TDataFactory;
import twig.graphics.TGAxis;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class Node3D extends JPanel {
    
    CameraView  view = new CameraView();
    
    TGAxis     axisX = new TGAxis();
    TGAxis     axisY = new TGAxis();
    TGAxis     axisZ = new TGAxis();
    
    Camera3D   camera = new Camera3D();
    Screen3D   screen = new Screen3D(1,1);
    
    public int cameraRotationY = -30;
    public int cameraRotationX = 30;
    public int cameraPosition  = -6;
    public int cameraOperture  = 50;
    public int cameraAngle     = 16; // should be 15
    
    QuadMesh3D  dataMesh = null;
    
    
    
    public BasicStroke  defaultStroke = new BasicStroke(1);
    
    public Node3D(){ 
        super();
        this.rehashCamera();
        //this.setLayout(new BorderLayout());
        //this.add(view,BorderLayout.CENTER);        
    }
    
    public final void rehashCamera(){
        this.camera.setViewingAngleDeg(this.cameraAngle);
        this.camera.setOperture(((double) this.cameraOperture)/100.0 );
        this.camera.movePosition(this.cameraPosition);
        camera.moveDirection(Math.toRadians(cameraRotationY), Math.toRadians(cameraRotationX));        
    }
    
    public void drawGrid(Graphics2D g2d, int w, int h){
        g2d.setColor(new Color(200,200,200));
        for(int x = 0; x < w; x+=50){
            g2d.drawLine(x, 0, x, h);
        }
        
        for(int y = 0; y < h; y+=50){
            g2d.drawLine(0, y, w, y);
        }
    }
    
    public void setDataMesh(QuadMesh3D qm){
        this.dataMesh = qm;
    }
    
    public CameraView getView(){ return view;}
    
    public static class QuadMeshProviderWalls extends QuadMesh3D {
        Color grayWall = new Color(245,245,245);
        
        @Override
        public void getQuad(Quad3D quad, int index){
            if(index==0){
                quad.points()[0].set(0., 0.,  0.);
                quad.points()[1].set(0., 1.,  0.);
                quad.points()[2].set(0., 1., -1.);
                quad.points()[3].set(0., 0., -1.);
                for(Point3D p : quad.points()) p.translateXYZ(-0.5, -0.5, 0.5);
            } else {
                quad.points()[0].set(0., 0.,  0.);
                quad.points()[1].set(0., 1.,  0.);
                quad.points()[2].set(1., 1.,  0.);
                quad.points()[3].set(1., 0.,  0.);
                for(Point3D p : quad.points()) p.translateXYZ(-0.5, -0.5, 0.5);
            }
        }
        
        @Override
        public Color getColor(int index){
            return grayWall;
        }
        
        @Override
        public int getCount(){
            return 2;
        }
    }
    
    public static class QuadMeshProviderH2F extends QuadMesh3D {
        
        H2F h2 = null;
        
        public QuadMeshProviderH2F(int stats, int bins){
            h2 = TDataFactory.createH2F(stats, bins);
            System.out.println(h2.getMaximum());
            h2.normalize(h2.getMaximum());
            System.out.println(h2.getMaximum());
        }
        
        @Override
        public void getQuad(Quad3D quad, int index){
            double offset = -0.5;
            
            int x = index/(h2.getAxisX().getNBins()-1);
            int y = index%(h2.getAxisX().getNBins()-1);
            
            double step = ((double)1)/(h2.getAxisX().getNBins()-1);
            
            quad.points()[0].set(offset + x*step, -0.5+h2.getBinContent(x, y), offset + y*step);
            quad.points()[1].set(offset + x*step,  -0.5+h2.getBinContent(x, y+1),offset + (y+1)*step);
            quad.points()[2].set(offset + (x+1)*step,-0.5+h2.getBinContent(x+1, y+1), offset + (y+1)*step);
            quad.points()[3].set( offset + (x+1)*step, -0.5+h2.getBinContent(x+1, y), offset + y*step);
            
            //for(int i = 0; i < quad.points.length; i++)
            //    quad.translateXYZ(0, -15, 0);
        }
        public int getBinX(int index){
            return index/(h2.getAxisX().getNBins()-1);
        }
        public int getBinY(int index){
            return index%(h2.getAxisX().getNBins()-1);
        }
        
        @Override
        public Color getColor(int index){
            if(index<12) return Color.red;
           // return new Color(0,0,255,60);
            int bx = this.getBinX(index);
            int by = this.getBinY(index);
            Color c = TStyle.getInstance().getPalette().palette2d().getColor3D(h2.getBinContent(bx, by), 0, 1, true);
            return c;
        }
        
        @Override
        public int getCount(){
            return (h2.getAxisX().getNBins()-1)*(h2.getAxisY().getNBins()-1);
        }
    }
    
    public void drawAxis3D(Graphics2D g2d, double x1, double y1, double x2, double y2){
        Point3D p = new Point3D(x1,y1,0.0);
        double angle = Math.atan2(y2-y1, x2-x1);
        System.out.println(" angle = " + Math.toDegrees(angle));
        
        AffineTransform original = g2d.getTransform();
        AffineTransform at = new AffineTransform();
        at.setToRotation(Math.toRadians(Math.toDegrees(angle)),0,0);
        //at.scale(2, 2);
        
        g2d.setTransform(at);
        
        double length = Math.sqrt((x1-x2)*(x1-x2)+(y2-y1)*(y2-y1));
        p.rotateZ(-angle);
        Rectangle2D r = new Rectangle2D.Double(p.x(), p.y(), length, 0);
        //Translation2D tr = new Translation2D(0,1,0,1);
        Translation2D tr = new Translation2D(1,0,1,0);
        
        TGAxis axis = new TGAxis(TAxisAttributes.AxisType.AXIS_X);
        axis.getAttributes().setAxisBoxDraw(Boolean.FALSE);
        axis.getAttributes().setAxisTickMarkSize(-8);
        axis.getAttributes().setAxisTitle(String.format("axis rotation %.2f", Math.toDegrees(angle)));
        axis.draw(g2d, r, tr);
        //-------------
        g2d.setTransform(original);
    }
    
    public void drawAxis(Graphics2D g2d, Screen3D screen){
        Line3D line = new Line3D(-0.5,-0.5,-0.5, 0.5,-0.5,-0.5);
        BasicStroke stroke = new BasicStroke(1);
        g2d.setStroke(stroke);g2d.setColor(Color.BLACK);
        camera.drawLine(g2d, screen, line, stroke, Color.BLACK);
        
        Line3D projected = new Line3D();
        camera.getLine(line, projected);
        
        this.drawAxis3D(g2d, 
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
        
        this.drawAxis3D(g2d, 
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
        this.drawAxis3D(g2d, 
                screen.getX(projected.origin().x()),
                screen.getY(projected.origin().y()),
                screen.getX(projected.end().x()),
                screen.getY(projected.end().y())
                );
        //line.translateXYZ(0.0, 0.0, 1);
        //camera.drawLine(g2d, screen, line, stroke, Color.BLACK);
    }
    
    
    public void drawWalls(Graphics2D g2d, Screen3D screen){
        Color wc = new Color(220,220,220);
        Quad3D q1 = Quad3D.rectXY(1, 1);
        q1.translateXYZ(0.0, 0 , 0.5);
        camera.fillPath(g2d, screen, true, wc, Color.BLACK, this.defaultStroke, q1.points());
        
        Quad3D q2 = Quad3D.rectXY(1, 1);
        q2.rotateY(Math.toRadians(90));
        q2.translateXYZ(-0.5, 0 , 0);
        camera.fillPath(g2d, screen, true, wc, Color.BLACK, this.defaultStroke, q2.points());
      
        Quad3D q3 = Quad3D.rectXZ(1, 1);
        //q3.rotateY(Math.toRadians(90));
        q3.translateXYZ(0.0, -0.5 , 0);
        camera.fillPath(g2d, screen, true, wc, Color.BLACK, this.defaultStroke, q3.points());
    }
    
    public CollectionLine3D getAxis(){
        int grayLevel = 205;
        double size = 1.0;
        CollectionLine3D coll = new CollectionLine3D();
        coll.addLine(new Line3D(0.0,0.0,0.0,size,0.0,0.0), new Color(grayLevel,grayLevel,grayLevel));
        coll.addLine(new Line3D(0.0,0.0,0.0,0.0,size,0.0), new Color(grayLevel,grayLevel,grayLevel));
        coll.addLine(new Line3D(0.0,0.0,0.0,0.0,0.0,-size), new Color(grayLevel,grayLevel,grayLevel));
        
        coll.addLine(new Line3D(size,0.0,0.0,size,size,0), new Color(grayLevel,grayLevel,grayLevel));
        coll.addLine(new Line3D(0.0,size,0.0,size,size,0.0), new Color(grayLevel,grayLevel,grayLevel));
        
        coll.addLine(new Line3D(0.0,size,0.0,0.0,size,-size), new Color(grayLevel,grayLevel,grayLevel));
        
        coll.addLine(new Line3D(0.0,0.0,-size,0.0,size,-size), Color.BLACK);
        coll.addLine(new Line3D(0.0,0.0,-size,size,0.0,-size), Color.BLACK);
        coll.addLine(new Line3D(size,0.0,0.0,size,0.0,-size), Color.BLACK);
        int nTicks = 8;
        for(int k = 0; k < nTicks+1; k++){
            coll.addLine(new Line3D(0.0,k*(1./nTicks),-size,0.0, k*(1./nTicks), -size-0.02));
        }
        
        for(int k = 0; k < nTicks+1; k++){
            coll.addLine(new Line3D(k*(1./nTicks),0.0,-size, k*(1./nTicks),0.0, -size-0.02));
        }
        
        for(int k = 0; k < nTicks+1; k++){
            coll.addLine(new Line3D(size,0.0, -k*(1./nTicks),size+0.02,0.0, -k*(1./nTicks)));
        }
        
        for(int i = 0; i < coll.getCount();i++) coll.getLine(i).translateXYZ(-size/2, -size/2, size/2);
        return coll;
    }
    
    
    
    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
        
        int w = this.getSize().width;
        int h = this.getSize().height;
        
        
        this.drawGrid(g2d, w, h);
        
        
        screen.set(w, h);
        //screen.setScale(1.0,0.7);
        
        BasicStroke stroke = new BasicStroke(1);
        
        //camera.drawLine(g2d, screen, new Line3D(-1,-1,0,1,1,0), stroke, Color.RED);
        camera.setAspectRatio(1.0);
        
        this.drawWalls(g2d, screen);
        this.drawAxis(g2d, screen);
        
        
        int nQuads = dataMesh.getCount();
        Quad3D quad = new Quad3D();
        for(int i = 0; i < nQuads; i++){
            dataMesh.getQuad(quad, i);
            //quad.show();
            //Point3D tr = new Point3D();
            //camera.getPoint(quad.point(0), tr);
            //System.out.println(" Projected = " + tr);
            //camera.drawPath(g2d, screen, true, Color.red, stroke, quad.points());
           camera.fillPath(g2d, screen, true, dataMesh.getColor(i), new Color(80,80,80), stroke, quad.points());
           //camera.fillPath(g2d, screen, true, dataMesh.getColor(i), null, stroke, quad.points());
           // camera.fillPath(g2d, screen, true, new Color(120,120,0,50), Color.BLACK, stroke, quad.points());
        }
        
        
    }
    
    
    public static void main(String[] args) {
        StudioWindow.changeLook();
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        QuadMeshProviderH2F mesh = new QuadMeshProviderH2F(80000,40);
        Node3D n3d = new Node3D();
        //n3d.getView().createAxis(1.0);
        /*n3d.getView().addCollection(n3d.getAxis());
        n3d.getView().addMesh(new QuadMeshProviderWalls());
        n3d.getView().addMesh(mesh);
        */
        
        n3d.setDataMesh(mesh);
        n3d.getView().meshQuadsFill = true;
        JPanel controls = n3d.getView().createControls();
        
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        
        pane.add(n3d,BorderLayout.CENTER);
        pane.add(controls,BorderLayout.PAGE_END);
        
        f.add(pane);
        f.setSize(800, 800);
        f.pack();
        f.setSize(800, 800);
        
        f.setVisible(true); 
    }
    
}
