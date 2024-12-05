/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.monitor;

import j4np.graphics.Node2D;
import j4np.graphics.NodeRegion2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import javax.swing.JPanel;

/**
 *
 * @author gavalian
 */
public class Superlayer2D extends Node2D {
    
    public int[][] wires = new int[6][112];
    
    public Superlayer2D(JPanel parent){
        super(0,0);this.setSuperParent(parent);}
    public Superlayer2D(){super(0,0);}
    public Superlayer2D(int x, int y) {
        super(x, y);
    }
    
    protected Rectangle2D getRectangle(int l, int w, NodeRegion2D b){
        double wdt = b.getWidth()/112.0;
        double hgt = b.getHeight()/6.0;
        double x = w*(wdt);
        double y = l*(hgt);        
        return new Rectangle2D.Double(b.getX()+x,b.getY()+y,wdt,hgt);
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){ 
        NodeRegion2D b = getBounds();
        
        g2d.drawLine((int) b.getX(), (int) b.getY(),
                (int) (b.getX()+b.getWidth()),
                (int) (b.getY()+b.getHeight())
                );
        
        /*System.out.println("----");
        for(int l = 0; l < 6; l++){
            System.out.println(Arrays.toString(wires[l]));
        }*/
        g2d.setStroke(new BasicStroke(1));
        for(int l = 0; l < 6; l++){
            for(int w = 0; w < 112; w++){
                Rectangle2D r = this.getRectangle(l, w, b);
                if(wires[l][w]>0){
                    g2d.setColor(Color.YELLOW);
                    g2d.fill(r);                    
                } else {
                     g2d.setColor(new Color(0,0,60));
                    g2d.fill(r);
                }
                g2d.setColor(Color.GRAY);
                g2d.draw(r);
            }
        }
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect((int) (b.getX()+2), (int) (b.getY()+2),
                (int) (b.getX()+b.getWidth()-6),
                (int) (b.getY()+b.getHeight()-6)
                );
        
    }
    
    public void reset(){
        for(int l = 0; l < 6; l++)
            for(int w = 0; w < 112; w++) wires[l][w] = 0;
        this.repaint();
    }
    
    public void update(){
        this.repaint();
    }
    
    @Override
    public boolean mousePressed(double X, double Y){
        NodeRegion2D b = getBounds();
        double xstep = b.getWidth()/112;
        double ystep = b.getHeight()/6;
        int w = (int) ((X-b.getX())/xstep);
        int l = (int) ((Y-b.getY())/ystep);
        System.out.println("mouse pressed " + X + " " + Y + "  Wire = " + w + " Layer = " + l);
        try { if(wires[l][w]==0)  wires[l][w] = 1; else wires[l][w]=0;} catch (Exception e){}
        this.repaint();
        return true;
    }
}
