/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Background2D;
import j4np.graphics.Canvas2D;
import java.awt.Color;
import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public class TGDataCanvas extends Canvas2D {
    
    public TGDataCanvas(){
        Color color = TStyle.getInstance().getPalette().getColor(30004);
        Background2D back = Background2D.createBackground(color.getRed(),color.getGreen(),color.getBlue());
        setBackground(back);
        divide(1,1);
    }
    
    @Override
    public void divide(int cols, int rows){
        this.getGraphicsComponents().clear();
        for(int i = 0; i < cols*rows; i++){
            TGRegion pad = new TGRegion();
            this.addNode(pad);
        }
        this.arrange(cols, rows);
        this.repaint();
    }
    
    public TGRegion region(int index){ 
        return (TGRegion) getGraphicsComponents().get(index);         
    }
    
    
}

