/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import j4np.graphics.Translation2D;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import twig.data.DataRange;

/**
 *
 * @author gavalian
 */
public class TPieChartNode2D extends TDataNode2D {
    @Override
    public void    getDataBounds(DataRange range){
        range.set(0, 0, 1, 1);
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
    }
}
