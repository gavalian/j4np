/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import twig.graphics.TGCanvas;
import twig.widgets.LatexText;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class HipoLayouts implements Widget {
   
    Color[]  colors = new Color[]{
        new Color(0xFF,0x90,0x00), new Color(0x94,0xF9,0x00), new Color(0x1C,0xFA,0x90)};
    
    
    public void drawBox(Graphics2D g2d, Color c, String text, int x, int y, int width, int height, boolean is3d){
        LatexText t = new LatexText(text);
        t.setFont(new Font("Avenir",Font.PLAIN,18));
        
        
        if(is3d==true){
            Color cl = c.brighter();
            float tilt = 0.3f;
            int[] xp = new int[]{ x, (int) (x+width*tilt), 
            (int) (x+width*tilt+width), x+width,x};
            int[] yp = new int[]{y,(int) (y-height*tilt), (int) (y-height*tilt), y,y};
            
            int[] xp2 = new int[]{ x+width, (int) (x+width*tilt+width), 
            (int) (x+width*tilt+width), x+width};
            int[] yp2 = new int[]{y,(int) (y-height*tilt), (int) (y+height-height*tilt), y+height};
            
            g2d.setColor(cl);
            g2d.fillPolygon(xp,yp,5);
            g2d.fillPolygon(xp2,yp2,4);
            g2d.setColor(c.darker());
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            g2d.drawPolygon(xp,yp,5);
            g2d.drawPolygon(xp2,yp2,4);
            
        }
        g2d.setColor(c);
        g2d.fillRect(x, y, width, height);
        g2d.setColor(c.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, width, height);
        //if(text.length()==0)
        t.drawString(g2d, x+width/2, y+height/2, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
       /* for(int i = 0; i < 5; i++){
            this.drawBox(g2d, colors[0], "A", 100+30*i, 100, 30, 30, true);
        }
        
        for(int i = 0; i < 8; i++){
            this.drawBox(g2d, colors[1], "X", 100+30*i, 200, 30, 30, true);
        }
        
        for(int i = 0; i < 7; i++){
            this.drawBox(g2d, colors[2], "Z", 100+30*i, 300, 30, 30, true);
        }*/

       this.drawBox(g2d, colors[1], " ", 100, 100, 20, 150, true);
       this.drawBox(g2d, colors[2], " ", 130, 150, 20, 70, true);
       this.drawBox(g2d, colors[0], "B", 180, 150, 80, 25, false);
           
    }

    @Override
    public boolean isNDF() {
        return true;
    }

    @Override
    public void configure(JComponent parent) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public static void main(String[] args){
        TGCanvas c = new TGCanvas("network_arch",1100,780);
        
        c.view().region().drawFrame(false);
        c.view().region().setDebugMode(true);
        HipoLayouts f = new HipoLayouts();
        c.view().region().draw(f);
        c.view().export("file_structure.pdf", "pdf");
    }

}
