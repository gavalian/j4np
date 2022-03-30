/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.debug;

import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.H2F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class PaletteDebug2D {
    public static void draw2D(){
        TGCanvas c = new TGCanvas();
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kNeon);        
        H2F h = TDataFactory.createH2F(800000, 120);
        h.attr().setTitleX("random gaussian X");
        h.attr().setTitleY("random gaussian Y");
        c.view().region().draw(h);
        c.repaint();
    }
    
    public static void main(String[] args){
        PaletteDebug2D.draw2D();
    }
}
