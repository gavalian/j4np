/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import twig.config.TPalette2D;
import twig.config.TPalette2D.PaletteName;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class PaletteViewer implements Widget {
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        LatexText textL = new LatexText("Input Layer");
        textL.setFont(new Font("Avenir",Font.PLAIN,12));
        
        PaletteName[]  names = TPalette2D.PaletteName.values();
        double posX = r.getX();
        double lenX = r.getWidth();
        
        double thickness = 2*r.getHeight()/(names.length);
        
        for(int i = 0; i < names.length/4; i++){
            double y = r.getY() + i*2*thickness;
            g2d.setColor(Color.red);
            g2d.drawRect((int) posX, (int) y, (int) lenX, (int) thickness);
            textL.drawString(names[i].name(), g2d, 
                   (int) posX ,(int) y, LatexText.TextAlign.RIGHT, LatexText.TextAlign.TOP, LatexText.TextRotate.NONE);
            
        }
    }

    @Override
    public boolean isNDF() {
        return true;
    }
    public static void main(String[] args){
        
        TGCanvas c = new TGCanvas("network_arch",900,500);
        
        c.view().region().drawFrame(false);
        
        PaletteViewer pv = new PaletteViewer();
        c.view().region().getInsets().left(20);
        c.view().region().draw(pv);
    }

    @Override
    public void configure(JComponent parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
