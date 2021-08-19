/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics.debug;

import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author gavalian
 */
public class DriftChambers2D extends Node2D {
    
    public static int  DC_WIDTH = 20;
    public static int  DC_OFFSET_X = 20;
    public static int  DC_OFFSET_Y = 20;
    public static int  DC_LENGTH = 160;
    
    public Color    oddSuperLayerColor = new Color(240,255,240);
    public Color   evenSuperLayerColor = new Color(240,240,255);
    public Color          outlineColor = new Color(150,150,150);
    
    public int sizeX = 100;
    public int sizeY = 100;
    
    public DriftChambers2D(int x, int y){
        super(x,y);
        sizeX = x; sizeY = y;
    }
    
    public static double[][] dcTracksSmooth = new double[][]{
        {0.15 ,0.20 ,0.3 ,0.45 ,0.65 ,0.85 },
        {0.75, 0.73, 0.67, 0.58, 0.40, 0.22},
        {0.75 ,0.20 ,0.3 ,0.45 ,0.40 ,0.85 },
        {0.75, 0.20, 0.67, 0.58, 0.40, 0.85}
    };
    

    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){
        
        Node2D parent = getParent();
        
        double margin = 0.0;//05;
        double step = (1.0 - 2*margin)/6.0;
        
        for(int i = 0; i < 6; i++){
            
            double xu_s = margin;
            double xu_e = 1.0 - margin;
            double yu_s = margin + i*step; 
            double yu_e = yu_s + step;
            
            double xc_start = parent.transformX(xu_s);
            double   xc_end = parent.transformX(xu_e);
            double yc_start = parent.transformY(yu_s);
            double   yc_end = parent.transformY(yu_e);
            
            System.out.printf("%8.5f %8.5f %8.5f %8.5f \n",
                    xc_start,xc_end,yc_start,yc_end);
            if(i%2==0){
                g2d.setColor(evenSuperLayerColor);
            } else g2d.setColor(oddSuperLayerColor);
            
            g2d.fillRect((int) xc_start, (int) yc_start,
                    (int) (xc_end-xc_start), 
                    (int) (yc_end-yc_start));
            
            g2d.setColor(outlineColor);
            g2d.drawRect((int) xc_start, (int) yc_start,
                    (int) (xc_end-xc_start), 
                    (int) (yc_end-yc_start));
            
        }
        
    }
    
    
    public static PathNode2D createTrack(double[] array){
        PathNode2D path = new PathNode2D(0,0,100,100);
        for(int i = 0; i < array.length; i++){
            path.addPoint(array[i], i*(1.0/6)+(1./12.0));
        }
        return path;
    }
    
    public static void main(String[] args){
        int x = 300; int y = 600;
        JFrame frame = new JFrame();
        
        Canvas2D canvas = Canvas2D.createFrame(frame, x, y);
        Node2D     node = new Node2D(0,0,100,100);
        node.setTranslation(0.0, 0.0, 1.0, 1.0);
        
        node.getInsets().set(8, 8, 8, 8);
        DriftChambers2D dc = new DriftChambers2D(x,y);
        node.addNode(dc);
        
        PathNode2D trackn = DriftChambers2D.createTrack(DriftChambers2D.dcTracksSmooth[0]);
        PathNode2D trackp = DriftChambers2D.createTrack(DriftChambers2D.dcTracksSmooth[1]);
        
        node.addNode(trackn);
        node.addNode(trackp);
        
        
        canvas.addNode(node); //dc.setParent(canvas);
    }
}
