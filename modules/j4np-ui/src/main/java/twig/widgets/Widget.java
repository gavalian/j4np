/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public interface Widget {
    public void    setStyle(TStyle style);
    public TStyle  getStyle();
}
