/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author gavalian
 */
public class RectangleNode2D extends Node2D {
    
    private int rectRound = 0;
    
    public RectangleNode2D(int x, int y) {
        super(x, y);
    }
    
    public RectangleNode2D(int x, int y, int w, int h) {
        super(x, y, w, h);
    }
    
    public void setRound(int r){
       rectRound = r; 
    }
    
    @Override
    public void drawLayer(Graphics2D g2d, int layer){
        
        g2d.setColor(this.getBackgroundColor());
        
        Rectangle2D bounds = this.getBounds().getBounds();
        
        g2d.fillRoundRect((int) this.translateX(bounds.getX()),
                (int) this.translateY(bounds.getY()),
                (int) this.getBounds().getBounds().getWidth(),
                (int) this.getBounds().getBounds().getHeight(),rectRound,rectRound
                );
        g2d.setColor(this.getBorderColor());
        
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect((int) this.translateX(bounds.getX()),
                (int) this.translateY(bounds.getY()),
                (int) this.getBounds().getBounds().getWidth(),
                (int) this.getBounds().getBounds().getHeight(),rectRound,rectRound
                ); 
        this.drawChildren(g2d, layer);
    }

}
