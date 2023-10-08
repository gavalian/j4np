/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.prim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author gavalian
 */
public class CameraView extends JPanel {
    
    Camera3D camera = null;
    
    private List<QuadMesh3D> meshes = new ArrayList<>();
    
    List<Shape3D> shapes = new ArrayList<>();
    List<Line3D>  lines  = new ArrayList<>();
    List<CollectionLine3D> lineCollList = new ArrayList<>();
    
    public int cameraRotationY = -30;
    public int cameraRotationX = 30;
    public int cameraPosition  = -6;
    public int cameraOperture  = 50;
    public int cameraAngle     = 15;
    
    public Color axisColor = new Color(40,40,40);
    
    public Color   meshOutlineColor = Color.BLACK;
    public boolean    meshQuadsFill = false;
    
    public CameraView(){
        super();
        camera = new Camera3D();
        this.rehashCamera();
    }
    
    public void addMesh(QuadMesh3D quad){
        this.meshes.add(quad);
    }
    
    public void addCollection(CollectionLine3D cl){
        this.lineCollList.add(cl);
    }
    
    public final void rehashCamera(){
        this.camera.setViewingAngleDeg(this.cameraAngle);
        this.camera.setOperture(((double) this.cameraOperture)/100.0 );
        this.camera.movePosition(this.cameraPosition);
        camera.moveDirection(Math.toRadians(cameraRotationY), Math.toRadians(cameraRotationX));        
    }
    
    public void drawQuadMesh(Graphics2D g2d,QuadMesh3D mesh, double width, double height){
        Quad3D quad = new Quad3D();
        Line3D buffer = new Line3D();
        int count = mesh.getCount();
        for(int c = 0; c < count; c++){
            Color color = mesh.getColor(c);
            mesh.getQuad(quad, c);
            this.drawQuad(g2d, buffer, width, height, quad, color);
        }
    }
    
    public void drawQuad(Graphics2D g2d, Line3D lineBuffer, double width, double height, Quad3D quad, Color c){
        g2d.setColor(Color.black);
        //quad.show();
        //Line3D line = new Line3D();
       
        
        /*for(int i = 0; i < 3; i++){
            lineBuffer.setOrigin(quad.point(i));
            lineBuffer.setEnd(quad.point(i+1));
            this.drawLine(g2d, width, height, lineBuffer);
        }
        lineBuffer.setOrigin(quad.point(quad.points.length-1));
        lineBuffer.setEnd(quad.point(0));
        this.drawLine(g2d, width, height, lineBuffer);
          */  
        camera.setAspectRatio(height/width);
        GeneralPath path = this.getQuadPath(camera, quad, width,height);
        g2d.draw(path);
    }
    
    public void fillQuadMesh(Graphics2D g2d,QuadMesh3D mesh, double width, double height){
        Quad3D quad = new Quad3D();
        Line3D buffer = new Line3D();
        int count = mesh.getCount();
        for(int c = 0; c < count; c++){
            Color color = mesh.getColor(c);
            mesh.getQuad(quad, c);
            this.fillQuad(g2d, buffer, width, height, quad, color);
        }
    }
     
    public void fillQuad(Graphics2D g2d, Line3D lineBuffer, double width, double height, Quad3D quad, Color c){
        g2d.setColor(c);
        //Line3D line = new Line3D();
        //this.drawLine(g2d, width, height, lineBuffer);
        camera.setAspectRatio(height/width);
        GeneralPath path = this.getQuadPath(camera, quad, width,height);
        g2d.fill(path);
        g2d.setColor(this.meshOutlineColor);
        g2d.draw(path);
        //Graphics2D g2d, Line3D lineBuffer, double width, double height, Quad3D quad, Color c);
    }
    
    public GeneralPath getQuadPath(Camera3D cam, Quad3D quad, double width, double height){
       GeneralPath path = new GeneralPath();
       Point3D p = cam.getPoint2(quad.points[0]);
       path.moveTo(width*0.5+p.x()*width,height*0.5-p.y()*height);
       for(int i = 1; i < 4; i++){
           Point3D pc = cam.getPoint2(quad.points[i]);
           path.lineTo(width*0.5+pc.x()*width,height*0.5-pc.y()*height);
           //System.out.printf(" move to %8.5f %8.5f\n",pc.x(),pc.y());
       }
       path.lineTo(width*0.5+p.x()*width,height*0.5-p.y()*height);
       return path;
    }
    
    public JPanel createControls(){
                        
        
        JPanel ctr = new JPanel();
        ctr.setLayout(new FlowLayout());

        
        SpinnerNumberModel nsY = new SpinnerNumberModel(this.cameraRotationY, -360, 360, 5);
        JSpinner rotY = new JSpinner(nsY);
        
        
        rotY.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
               Integer value =  (Integer) ( (JSpinner) e.getSource()).getModel().getValue();
               camera.movePosition(cameraPosition);
               camera.setOperture(((double) cameraOperture)/100.0);
               cameraRotationY = value;
               camera.moveDirection(Math.toRadians(cameraRotationY), Math.toRadians(cameraRotationX));
               repaint();
            }
        });
        
        SpinnerNumberModel nsX = new SpinnerNumberModel(this.cameraRotationX, -360, 360, 5);
        JSpinner rotX = new JSpinner(nsX);
        
        
        rotX.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
               Integer value =  (Integer) ( (JSpinner) e.getSource()).getModel().getValue();
               camera.movePosition(cameraPosition);
               camera.setOperture(((double) cameraOperture)/100.0);
               cameraRotationX = value;
               camera.moveDirection(Math.toRadians(cameraRotationY), Math.toRadians(cameraRotationX));
               repaint();
            }
        });
        
        SpinnerNumberModel nsO = new SpinnerNumberModel(50, 0, 100, 5);
        JSpinner oper = new JSpinner(nsO);
        
        
        oper.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
               Integer value =  (Integer) ( (JSpinner) e.getSource()).getModel().getValue();
               cameraOperture = value;
               camera.setOperture(((double) cameraOperture)/100.0);
               camera.movePosition(cameraPosition);
               System.out.println("==========================");
               camera.show();
               camera.moveDirection(Math.toRadians(cameraRotationY), Math.toRadians(cameraRotationX));
               repaint();
            }
        });
        
        
        SpinnerNumberModel nsA = new SpinnerNumberModel(this.cameraAngle, 0, 90, 2);
        JSpinner angle = new JSpinner(nsA);
        
        
        angle.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
               Integer value =  (Integer) ( (JSpinner) e.getSource()).getModel().getValue();
               cameraAngle = value;
               camera.setViewingAngleDeg(cameraAngle);
               /*camera.setOperture(((double) cameraOperture)/100.0);
               camera.movePosition(cameraPosition);
               System.out.println("==========================");
               camera.show();
               camera.moveDirection(Math.toRadians(cameraRotationY), Math.toRadians(cameraRotationX));*/
               repaint();
            }
        });
        ctr.add(new Label("rotate X : "));
        ctr.add(rotX);
        ctr.add(new Label("rotate Y : "));
        ctr.add(rotY);
        ctr.add(new Label("Operture : "));
        ctr.add(oper);        
        ctr.add(new Label("Viewing Angle : "));
        ctr.add(angle);
        return ctr;
    }
    public void drawLineCollection(Graphics2D g2d, CollectionLine3D cl, double width, double height){
        int count = cl.getCount();
        for(int j = 0; j < count; j++){
            g2d.setColor(cl.getColor(j));
            this.drawLine(g2d, width, height, cl.getLine(j));
        }
    }
    
    public void drawLine( Graphics2D g2d, double width, double height, Line3D line){
        Line3D lp = new Line3D();
        camera.setAspectRatio(height/width);
        Point3D po = camera.getPoint2(line.origin());
        Point3D pe = camera.getPoint2(line.end());
        //System.out.println(line);
        //System.out.println(" DRAWING POINT====");
        //System.out.println(po);
        //System.out.println(pe);                
        
        lp.setOrigin(width/2+po.x()*width, height/2-po.y()*height, 0.0);
        lp.setEnd(width/2+pe.x()*width, height/2-pe.y()*height, 0.0);        
        //System.out.println("drawing line " + lp);
        g2d.drawLine((int) (lp.origin().x()),
                (int)  (lp.origin().y()), 
                (int) (lp.end().x()), 
                (int) (lp.end().y()));
    }
    /*
    public void drawFace(Graphics2D g2d,Face3D face){
        Line3D   line = new Line3D();
        Line3D line2d = new Line3D();
        line.setOrigin(face.point(0));line.setEnd(face.point(1));
        
        Point3D p0 = camera.getPoint(face.point(0));
        Point3D p1 = camera.getPoint(face.point(1));
        Point3D p2 = camera.getPoint(face.point(2));
        line2d.setOrigin(p0);line2d.setEnd(p1);drawLine(g2d, line2d);
        line2d.setOrigin(p1);line2d.setEnd(p2);drawLine(g2d, line2d);
        line2d.setOrigin(p2);line2d.setEnd(p0);drawLine(g2d, line2d);
        
    }
    
    public void drawShape(Graphics2D g2d,Shape3D shape){
        int faces = shape.size();
        System.out.println(" num faces = " + faces);
        for(int i = 0; i < faces; i++) this.drawFace(g2d, shape.face(i));
    }*/
    

    
    @Override
    public void paint(Graphics g){
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
        
        int w = this.getSize().width;
        int h = this.getSize().height;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,w,h);
        
        g2d.setColor(axisColor);
        
        //for(Line3D line : lines) this.drawLine(g2d, w, h, line);
        for(CollectionLine3D cl : this.lineCollList) this.drawLineCollection(g2d, cl, w, h);
        
        if(this.meshQuadsFill==false)
            for(QuadMesh3D mesh : meshes) this.drawQuadMesh(g2d, mesh, w, h);
        
        else for(QuadMesh3D mesh : meshes) this.fillQuadMesh(g2d, mesh, w, h);
    }
    
    public void createAxis(double size){
       this.lines.clear();
       if(size>0){
           lines.add(new Line3D( 0, 0, 0, size,    0,    0));
           lines.add(new Line3D( 0, 0, 0,    0, size,    0));
           lines.add(new Line3D( 0, 0, 0,    0,    0, -size));
           for(Line3D l : lines) l.translateXYZ(-size/2,-size/2 ,size/2);
       }
    }
    
    public static class QuadMeshProvider3D extends QuadMesh3D {
        int nSquares = 8;
        public void getQuad(Quad3D quad, int index){
            double offset = -0.5;
            int x = index/nSquares;
            int y = index%nSquares;
            double step = ((double)1)/(nSquares);
            quad.points[0].set(offset + x*step, -0.5, offset + y*step);
            quad.points[1].set(offset + x*step,  -0.5,offset + (y+1)*step);
            quad.points[2].set(offset + (x+1)*step,-0.5, offset + (y+1)*step);
            quad.points[3].set( offset + (x+1)*step, -0.5, offset + y*step);
            
            //for(int i = 0; i < quad.points.length; i++)
            //    quad.translateXYZ(0, -15, 0);
        }
        
        @Override
        public Color getColor(int index){
            return Color.BLUE;
        }
        
        @Override
        public int getCount(){
            return nSquares*nSquares;
        }
    }
    
    public static class CollectionLine3D {
        List<Color>   colorBuffer = new ArrayList<>();
        List<Line3D>  linesBuffer = new ArrayList<>();
        public CollectionLine3D(){}
        public int getCount(){return linesBuffer.size();}
        public Line3D getLine(int index){return linesBuffer.get(index);}
        public Color  getColor(int index){ return colorBuffer.get(index);}
        public void   addLine(Line3D line, Color c){linesBuffer.add(line);colorBuffer.add(c);}
        public void   addLine(Line3D line) {addLine(line,Color.BLACK);}
    }
    
    public static void main(String[] args){
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        QuadMeshProvider3D mesh = new QuadMeshProvider3D();
        CameraView n3d = new CameraView();
        n3d.createAxis(1.0);
        n3d.addMesh(mesh);
        n3d.meshQuadsFill = true;
        JPanel controls = n3d.createControls();
        
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
