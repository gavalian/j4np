/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class PieChart implements Widget {

    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        double radius = 200;
        double radius2 = 260;
        double radius3 = 320;
        
        double posX = r.getCenterX();
        double posY = r.getCenterY();
        g2d.setColor(Color.ORANGE);
        
        g2d.setStroke(new BasicStroke(25,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));        
        g2d.drawArc((int) (posX-radius*0.5), (int) (posY-radius*0.5), 
                (int) radius, (int) radius, 0, 270);
        g2d.setColor(new Color(209,65,36));
        g2d.drawArc((int) (posX-radius2*0.5), (int) (posY-radius2*0.5), 
                (int) radius2, (int) radius2, 90, 360);
        
        g2d.setColor(new Color(58,131,130));
        g2d.drawArc((int) (posX-radius3*0.5), (int) (posY-radius3*0.5), 
                (int) radius3, (int) radius3, 45, 180);
        
        
        //Font fbrushed = TStyle.createFont("fonts/Tahoma-Font/TAHOMA_0.TTF");
        Font fbrushed = TStyle.createFont();
        Font fbr = fbrushed.deriveFont(48);
        
        //g2d.setFont(new Font("Avenir",Font.BOLD,120));
        g2d.setFont(new Font("Brushed",Font.BOLD,120));
        //g2d.setFont(fbr);
        g2d.setColor(Color.cyan);
        g2d.drawString("Twig", (int) (posX), (int) (posY+100));
        //System.out.println(" is null = " + (fbrushed==null));
    }

    @Override
    public boolean isNDF() {
        return true;
    }
    public static void main(String[] args){
        
        TGCanvas c = new TGCanvas("network_arch",900,500);
        
        c.view().region().drawFrame(false);
        
        PieChart pv = new PieChart();
        c.view().region().getInsets().left(20);
        c.view().region().draw(pv);
    }
}
