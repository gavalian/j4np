/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class Histogram2Ddebug {
    
    public static void drawConfusionMatrix(){
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kBird);
        TGCanvas c = new TGCanvas();
        H2F h = new H2F("h2",3,-0.5,2.5, 3, -0.5,2.5);
        h.attr().setTitleX("True Class");
        h.attr().setTitleY("Predicted Class");
        h.setBinContent(0, 0, 95);
        h.setBinContent(1, 1, 90);
        h.setBinContent(2, 2, 92);
        h.setBinContent(0, 1, 15);
        h.setBinContent(2, 1, 5);
        h.setBinContent(2, 0, 2);
        
        c.draw(h,"FS");
    }
    
    public static void main(String[] args){
        Histogram2Ddebug.drawConfusionMatrix();
    }
}
