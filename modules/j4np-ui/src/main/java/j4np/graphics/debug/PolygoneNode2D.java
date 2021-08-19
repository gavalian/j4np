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
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class PolygoneNode2D extends Node2D {
    
    private List<Double> xpoints = new ArrayList<>();
    private List<Double> ypoints = new ArrayList<>();
    public boolean      fillPolygon = true;
    public boolean         drawLine = true;
    public Color        lineColor   = Color.BLACK;
    public Color        nodeColor   = Color.BLACK;
    
    public Color        fillColor   = new Color(210,210,210);
    
    public int          lineSize    = 2;
    public int          nodeSize    = 8;
    
    
    public PolygoneNode2D(int x, int y, int w, int h){
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
        
        GeneralPath     path = new GeneralPath();

        for(int i = 0; i < xpoints.size(); i++){
            double x = parent.transformX(xpoints.get(i));
            double y = parent.transformY(ypoints.get(i));
            if(i==0){ path.moveTo(x, y); } else { path.lineTo(x, y);}
           
        }
        if(this.fillPolygon == true){
            path.lineTo(parent.transformX(xpoints.get(0)),
                    parent.transformY(ypoints.get(0)));                        
            g2d.setColor(fillColor);
            g2d.fill(path);            
        } 
        
        if(drawLine==true){
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(lineSize));        
            g2d.draw(path);
        }
        
        g2d.setColor(nodeColor);
        int offset = nodeSize/2;
        for(int i = 0; i < xpoints.size(); i++){
            double x = parent.transformX(xpoints.get(i));
            double y = parent.transformY(ypoints.get(i));
            g2d.fillOval((int) (x-offset), (int) (y-offset), nodeSize, nodeSize);
        }
    }
}
