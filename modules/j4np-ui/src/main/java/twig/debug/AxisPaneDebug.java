/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import j4np.geom.prim.Point3D;
import j4np.graphics.Translation2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import twig.config.TAxisAttributes.AxisType;
import twig.graphics.TGAxis;

/**
 *
 * @author gavalian
 */
public class AxisPaneDebug extends JPanel {
 
    public AxisPaneDebug(){super();}
    
    
    public void drawGrid(Graphics2D g2d, int w, int h){
        g2d.setColor(new Color(200,200,200));
        for(int x = 0; x < w; x+=50){
            g2d.drawLine(x, 0, x, h);
        }
        
        for(int y = 0; y < h; y+=50){
            g2d.drawLine(0, y, w, y);
        }
    }
    
    public void drawAxis3D(Graphics2D g2d, double x1, double y1, double x2, double y2){
        Point3D p = new Point3D(x1,y1,0.0);
        double angle = Math.atan2(y2-y1, x2-x1);
        System.out.println(" angle = " + Math.toDegrees(angle));
        
        AffineTransform original = g2d.getTransform();
        AffineTransform at = new AffineTransform();
        at.setToRotation(Math.toRadians(Math.toDegrees(angle)),0,0);
        at.scale(2, 2);
        
        g2d.setTransform(at);
        
        double length = Math.sqrt((x1-x2)*(x1-x2)+(y2-y1)*(y2-y1));
        p.rotateZ(-angle);
        Rectangle2D r = new Rectangle2D.Double(p.x(), p.y(), length, 0);
        //Translation2D tr = new Translation2D(0,1,0,1);
        Translation2D tr = new Translation2D(2.0,0,2.0,0);
        
        TGAxis axis = new TGAxis(AxisType.AXIS_X);
        axis.setLimits(0, 2.0);
        axis.getAttributes().setAxisBoxDraw(Boolean.FALSE);
        axis.getAttributes().setAxisTickMarkSize(-8);
        axis.getAttributes().setAxisTitle(String.format("axis rotation %.2f", Math.toDegrees(angle)));
        
        axis.draw(g2d, r, tr);
        //-------------
        g2d.setTransform(original);
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
        
        Shape shape = new Rectangle2D.Float(300, 250, 100, 100);
    
        Graphics2D g2 = (Graphics2D) g;
        
        this.drawGrid(g2d, w, h);
        g2d.setColor(Color.BLACK);
        
        
        //g2d.drawOval(650-2, 250-2, 4, 4);
        //g2d.drawOval(350-2, 300-2, 4, 4);
        
        this.drawAxis3D(g2d, 50, 50, 50, 450);
        this.drawAxis3D(g2d, 50, 150, 350, 300);

        this.drawAxis3D(g2d, 350, 300, 650,200);
        
        
        
        GeneralPath path = new GeneralPath();
        
        path.moveTo(100, 100);
        path.lineTo(200, 200);
        path.lineTo(100, 200);
        g2d.setColor(Color.red);
        
        g2d.draw(path);
        
        path.reset();
        
        path.moveTo(500, 500);
        path.lineTo(600,600);
        path.lineTo(500, 600);
        
        g2d.setColor(Color.blue);
        
        g2d.draw(path);
        
        /*
        
        TGAxis axis = new TGAxis(AxisType.AXIS_X);
        axis.getAttributes().setAxisBoxDraw(Boolean.FALSE);
        axis.getAttributes().setAxisTickMarkSize(-8);
        axis.getAttributes().setAxisTitle("Rotated Axis");
        Rectangle2D r = new Rectangle2D.Double(40, 40, 400,400);
        Translation2D tr = new Translation2D(0,1,0,1);
        
        
        
        g2d.draw(shape);
        g2d.drawString("Some", 300,260);
        AffineTransform at = new AffineTransform();
        at.setToRotation(Math.toRadians(-30));
        at.scale(2, 2);
        //at.setToShear(2, 2);
        //at.setToTranslation(300, 300);
        System.out.println(at.getScaleX());
        
        g2.setTransform(at);
        g2.draw(shape);
        axis.draw(g2d, r, tr);
        g2d.drawString("Some", 300,260);*/
        /*
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,w,h);
        
        
        g2d.setColor(Color.yellow);
        g2d.drawLine(0, 0, w, h);
        
        this.drawGrid(g2d, w, h);
        
        TGAxis axis = new TGAxis(AxisType.AXIS_X);
        axis.getAttributes().setAxisBoxDraw(Boolean.FALSE);
        axis.getAttributes().setAxisTickMarkSize(-8);
        axis.getAttributes().setAxisTitle("Rotated Axis");
        Rectangle2D r = new Rectangle2D.Double(40, 40, 400,400);
        Translation2D tr = new Translation2D(0,1,0,1);
        
        
        int x1 = -200;
        int y1 = -200;
        
        int x2 = 200;
        int y2 = 200;
        
        g2d.setColor(Color.BLACK);
        //g2d.drawLine(x1,y1,x2,y2);
        g2d.drawLine(x1+w/2,y1+h/2,x2+w/2,y2+h/2);
        g2d.fillOval(250-2, 250-2, 4, 4);
        g2d.fillOval(450-2, 450-2, 4, 4);
        //axis.draw(g2d, r, tr);
        AffineTransform original = g2d.getTransform();
        g2d.setTransform(AffineTransform.getRotateInstance(Math.toRadians(-90),
                //0,0));
                0, 0));
         g2d.setColor(Color.RED);
        g2d.drawLine(x1+w/2,y1+h/2,x2+w/2,y2+h/2);
        g2d.setTransform(original);
        
        g2d.setTransform(AffineTransform.getRotateInstance(Math.toRadians(90),
                //0,0));
                w,h));
         g2d.setColor(Color.RED);
        //g2d.drawLine(x1,y1,x2,y2);
        g2d.drawLine(x1+w/2,y1+h/2,x2+w/2,y2+h/2);
        g2d.setTransform(original);
        //axis.draw(g2d, r, tr);
        
        //g2d.dispose();
        
*/
        
    }
    
    public static void main(String[] args){
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AxisPaneDebug pane = new AxisPaneDebug();
                
        f.add(pane);
        f.setSize(800, 800);
        f.pack();
        f.setSize(800, 800);
        
        f.setVisible(true);
    }
}
