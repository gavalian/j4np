/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public interface StyleNode {
    public void    setStyle(TStyle style);
    public TStyle  getStyle();
    //public void    draw(Graphics2D g2d, Rectangle2D r, Translation2D tr);
}
