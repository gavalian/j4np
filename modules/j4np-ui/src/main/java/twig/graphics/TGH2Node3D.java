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
import j4np.graphics.Node2D;
import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.config.TAxisAttributes;
import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDataFactory;

/**
 *
 * @author gavalian
 */
public class TGH2Node3D extends Node2D {

    H2F dataHist = null;
    String options = "";
    
    Camera3D   camera = new Camera3D(18,new Point3D(0,0,-6));
    Screen3D   screen = new Screen3D(1,1);
    
    BasicStroke   defaultStroke = new BasicStroke(1);
    
    public int cameraOperture  = 50;
    
    
    QuadMesh3D  dataMesh = null;
    
    public TGH2Node3D(H2F data, String opt){
        super(100,100);
        this.dataHist = data; options = opt;
        //dataHist.normalize(dataHist.getMaximum());
        dataMesh = new QuadMeshProviderH2F(dataHist);
        this.refreshCamera();
    }
    
    
    protected final void refreshCamera(){
        this.camera.setViewingAngleDeg(18);
        this.camera.incrementRotationX(Math.toRadians(30));
        this.camera.incrementRotationY(Math.toRadians(-30));
        
        //this.camera.setOperture(((double) this.cameraOperture)/100.0 );
        //this.camera.movePosition(-6);
        //camera.moveDirection(Math.toRadians(-30), Math.toRadians(30));
    }
    
    public void drawWalls(Graphics2D g2d, Screen3D screen){
        Color wc = new Color(220,220,220);
        Quad3D q1 = Quad3D.rectXY(1, 1);
        q1.translateXYZ(0.0, 0 , 0.5);
        camera.fillPath(g2d, screen, true, wc, Color.BLACK, this.defaultStroke, q1.points());
        
        Quad3D q2 = Quad3D.rectXY(1, 1);
        //q2.rotateX(Math.toRadians(90));
        q2.rotateY(Math.toRadians(90));
        q2.rotateX(Math.toRadians(90));
        q2.translateXYZ(-0.5, 0 , 0);
        camera.fillPath(g2d, screen, false, wc, Color.BLACK, this.defaultStroke, q2.points());
      
        Quad3D q3 = Quad3D.rectXZ(1, 1);
        //q3.rotateY(Math.toRadians(90));
        q3.translateXYZ(0.0, -0.5 , 0);
        //camera.fillPath(g2d, screen, true, wc, Color.BLACK, this.defaultStroke, q3.points());
        camera.fillPath(g2d, screen, true, wc, null, this.defaultStroke, q3.points());
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
        
        axis.getAttributes().setAxisTitle(String.format("axis rotation %.2f", Math.toDegrees(angle)));
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
    
    @Override
    public void applyMouseDrag(int x, int y, int xmove, int ymove){

        /*System.out.printf("hey you dragged me.... in x (%d %d) %.6f in y = %.6f",
                x,xmove,
                (xmove-x)/screen.getWidth(), (ymove-y)/screen.getHeight());  
        */
        double xdrag = (xmove-x)/screen.getWidth();
        double ydrag = (ymove-y)/screen.getHeight();
        camera.incrementRotationY(0.2*xdrag);
        camera.incrementRotationX(0.2*ydrag);
        camera.adjust();
        
        //camera.reset();
        //System.out.println("applying drag component " + getName());
        
        /*
        NodeRegion2D  region = this.getScreenBounds();
        
        double xpos = this.getLocalX(x);
        double ypos = this.getLocalY(y);
        System.out.println("DRAG : SCREEN BOUNDS : " + region);
        
        System.out.println(" X / Y " + x + " " + y + " //// " + xpos + " " + ypos);
        if(canMove()==true){
            this.moveTo(x, y);
        }
        
        System.out.println(" AFTER MOVE = " + this.nodeRegion);
        */
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){
        
        Rectangle2D bounds = this.getParent().getBounds().getBounds();
        //System.out.println(this.getBounds().getBounds());
        //this.dataHist.normalize(this.dataHist.getMaximum());
        
        double normalization = this.dataHist.getMaximum();
        
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
        
        this.drawWalls( g2d, screen);
        this.drawAxis(  g2d, screen);
        
        int nQuads = dataMesh.getCount();
        Quad3D quad = new Quad3D();
        ((QuadMeshProviderH2F) dataMesh).setNormalization(normalization);
        for(int i = 0; i < nQuads; i++){
            dataMesh.getQuad(quad, i);
           
           camera.fillPath(g2d, screen, true, dataMesh.getColor(i), new Color(80,80,80), this.defaultStroke, quad.points());
           //camera.fillPath(g2d, screen, true, new Color(50,150,255,75), new Color(80,80,80), this.defaultStroke, quad.points());
           //camera.drawPath(g2d, screen, true, new Color(50,150,255,25), this.defaultStroke, quad.points());
           //camera.drawPath(g2d, screen, true, Color.BLACK, this.defaultStroke, quad.points());
           
        }
    }
    
    public static class QuadMeshProviderH2F extends QuadMesh3D {
        
        H2F h2 = null;
        double norm = 1.0;
        
        public QuadMeshProviderH2F(H2F h){
            h2 = h;
        }
        
        public void setNormalization(double n) { norm = n;}
        @Override
        public void getQuad(Quad3D quad, int index){
            double offset = -0.5;            
            //int x = index/(h2.getAxisX().getNBins()-1);
            //int y = index%(h2.getAxisX().getNBins()-1);
            
            int x = getBinX(index);
            int y = getBinY(index);
            
            double stepX  = ((double)1)/(h2.getAxisX().getNBins()-1);
            double stepY = ((double)1)/(h2.getAxisY().getNBins()-1);

            
            quad.points()[0].set(offset + x*stepX, -0.5+h2.getBinContent(x, y)/norm, offset + y*stepY);
            quad.points()[1].set(offset + x*stepX,  -0.5+h2.getBinContent(x, y+1)/norm,offset + (y+1)*stepY);
            quad.points()[2].set(offset + (x+1)*stepX,-0.5+h2.getBinContent(x+1, y+1)/norm, offset + (y+1)*stepY);
            quad.points()[3].set( offset + (x+1)*stepX, -0.5+h2.getBinContent(x+1, y)/norm, offset + y*stepY);
            
            //for(int i = 0; i < quad.points.length; i++)
            //    quad.translateXYZ(0, -15, 0);
        }
        public int getBinX(int index){
            return index/(h2.getAxisY().getNBins()-1);
        }
        
        public int getBinY(int index){
            return index%(h2.getAxisY().getNBins()-1);
        }
        
        @Override
        public Color getColor(int index){
            if(index<12) return Color.red;
           // return new Color(0,0,255,60);
            int bx = this.getBinX(index);
            int by = this.getBinY(index);
            Color c = TStyle.getInstance().getPalette().palette2d().getColor3D(h2.getBinContent(bx, by)/norm, 0, 1, true);
            return c;
        }
        
        @Override
        public int getCount(){
            return (h2.getAxisX().getNBins()-1)*(h2.getAxisY().getNBins()-1);
        }
    }        
    
    
    public static void main(String[] args){
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kBird);
        TGCanvas c = new TGCanvas(800,800);
        TGCanvas c2 = new TGCanvas(800,800);
        H2F h1 = TDataFactory.createH2F(120000, 24,80);        
        H2F h2 = TDataFactory.createH2F(120000, 40,40);
        H2F h3 = TDataFactory.createH2F(120000, 30,40); 
        H1F h4 = TDataFactory.createH1F(1200);
        H1F h5 = TDataFactory.createH1F(1200);
        H1F h6 = TDataFactory.createH1F(1200);
        
        TGH2Node3D node1 = new TGH2Node3D(h1,"W");
        TGH2Node3D node2 = new TGH2Node3D(h2,"W");
        TGH2Node3D node3 = new TGH2Node3D(h3,"W");
        c.view().divide(2,2);
        c.view().region(0).replace(node1);
        c.view().region(0).getInsets().set(0, 0, 40, 0);
        //c.view().region(1).replace(node2);
        //c.view().region(2).replace(node3);
        c.cd(1).draw(h3).cd(2).draw(h4).cd(3).draw(h5);
        System.out.println(" 0 " +  c.view().region(0).getBounds().getBounds());
        System.out.println(" 1 " + c.view().region(1).getBounds().getBounds());
        System.out.println(" 2 " + c.view().region(2).getBounds().getBounds());
        
        c.repaint();
        H2F h = new H2F("h2",48,0.,1,24,0.,1);
        
        TGH2Node3D node4 = new TGH2Node3D(h2,"W");
        c2.view().region(0).replace(node4);
        c2.view().region(0).getInsets().set(0, 0, 40, 0);
        c2.view().initTimer(300);
        c2.repaint();
        
        Random r = new Random();
        for(int i = 0; i < 500000; i++){
            h2.fill(r.nextDouble(), r.nextDouble());

            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(TGH2Node3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
