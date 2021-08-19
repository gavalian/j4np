/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics.debug;

import j4np.graphics.Node2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class PathNode2D extends Node2D {
    
    private List<Double> xpoints = new ArrayList<>();
    private List<Double> ypoints = new ArrayList<>();
    
    public boolean      fillPolygon = true;
    public boolean         drawLine = true;
    public Color        lineColor   = Color.BLACK;
    public Color        nodeColor   = Color.BLACK;
    
    public Color        fillColor   = new Color(210,210,210);
    
    public int          lineSize    = 2;
    public int          nodeSize    = 8;
    
    public List<Integer>     skip = new ArrayList<>();
    public boolean       skipDraw = false;
    
    public PathNode2D(int x, int y, int w, int h){
        super(x,y,w,h);
    }
    
    public void addPoint(double x, double y){
        xpoints.add(x);
        ypoints.add(y);
    }
    
    public void addPoints(double[] xp, double[] yp){
        for(int i = 0; i < xp.length; i++){
            addPoint(xp[i],yp[i]);
        }
    }
    
    public void drawLayer(Graphics2D g2d, int layer){
        Node2D parent = this.getParent();
        System.out.println(parent.getTranslation());
        

        if(drawLine==true){
            g2d.setColor(lineColor);
            for(int i = 0; i < xpoints.size()-1; i++){
                double x = parent.transformX(xpoints.get(i));
                double y = parent.transformY(ypoints.get(i));
                
                if(skip.contains(i)==false&&skip.contains(i+1)==false){
                    g2d.setStroke(new BasicStroke(this.lineSize));
                    double x2 = parent.transformX(xpoints.get(i+1));
                    double y2 = parent.transformY(ypoints.get(i+1));
                    g2d.drawLine((int) x, (int) y, (int) x2, (int) y2);
                } else {
                    if(skipDraw==true){
                        Stroke dashed = new BasicStroke(this.lineSize, 
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_BEVEL,
                                0, new float[]{9}, 0);
                        g2d.setStroke(dashed);
                        double x2 = parent.transformX(xpoints.get(i+1));
                        double y2 = parent.transformY(ypoints.get(i+1));
                        g2d.drawLine((int) x, (int) y, (int) x2, (int) y2);
                    }
                }
                
            }
        }
        
        
        
        g2d.setColor(nodeColor);
        int offset = nodeSize/2;
        for(int i = 0; i < xpoints.size(); i++){
            double x = parent.transformX(xpoints.get(i));
            double y = parent.transformY(ypoints.get(i));
            
            if(skip.contains(i)==false){
                
                g2d.fillOval((int) (x-offset), (int) (y-offset), nodeSize, nodeSize);
            } else {
                if(skipDraw==true){
                    Stroke dashed = new BasicStroke(this.lineSize, 
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_BEVEL,
                            0, new float[]{2}, 0);
                    g2d.setStroke(dashed);
                    g2d.drawOval((int) (x-offset), (int) (y-offset), nodeSize, nodeSize);
                }
            }
        }
    }
}
