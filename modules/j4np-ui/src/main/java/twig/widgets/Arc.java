/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

/**
 *
 * @author gavalian
 */
public class Arc implements Widget {

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    private double  xPosition = 0;
    private double  yPosition = 0;
    private double     width = 0.5;
    private double    height = 0.5;
    private double startTheta = 0;
    private double   endTheta = 0;
    private int      lineWidth = 2;
    
    public Arc(double x, double y, double w, double h, double sta, double eda){
        xPosition = x; yPosition = y; width = w; height = h;
        startTheta = sta; endTheta = eda;
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        int x = (int) tr.getX(xPosition, r);
        int y = (int) ( r.getY() + r.getHeight() - tr.getY(yPosition, r) + r.getY());
        
        int w = (int) tr.getLengthX(width, r);
        int h = (int) tr.getLengthX(height, r);
        g2d.setStroke(new BasicStroke(lineWidth));
        g2d.drawArc(x, y, (int) w, (int) h, (int) startTheta, (int) endTheta);
    }

    @Override
    public boolean isNDF() {
        return true;
    }

    @Override
    public void configure(JComponent parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
