/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import twig.config.TAttributesFill;
import twig.config.TAttributesLine;
import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public class Circle implements Widget {
    
    public boolean isNDF = true;
    public TAttributesLine attrLine = new TAttributesLine();
    public TAttributesFill attrFill = new TAttributesFill();
    public Rectangle2D dim2d = new Rectangle2D.Double(0.5,0.5,0.2,0.2);
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        //System.out.println("drawing circle");
        int xo = (int) tr.getX(dim2d.getX(), r);
        int yo = (int) tr.getY(dim2d.getY(), r);
        int xl = (int) tr.getLengthX(dim2d.getWidth(), r);
        int yl = (int) tr.getLengthY(dim2d.getHeight(), r);
                
        TStyle style = TStyle.getInstance();
        g2d.setStroke(style.getLineStroke(attrLine.getLineStyle(), attrLine.getLineWidth()));
        
        if(attrFill.getFillColor()>0){
            g2d.setColor(style.getPalette().getColor(attrFill.getFillColor()));
            g2d.fillArc(xo-xl/2,yo-yl/2,xl,yl,0,360);
        }
        
        
        g2d.setColor(style.getPalette().getColor(attrLine.getLineColor()));
        
        g2d.drawArc(xo-xl/2,yo-yl/2,xl,yl,0,360);        
        //g2d.drawArc(xo-xl/2,yo-yl/2+40,xl,yl,0,-180);
        
    }

    @Override
    public boolean isNDF() {
        return isNDF;
    }

    @Override
    public void configure(JComponent parent) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
