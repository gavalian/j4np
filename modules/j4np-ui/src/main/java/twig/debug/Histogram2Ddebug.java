/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.util.Arrays;
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
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kFall);
        TGCanvas c = new TGCanvas("confusion_matrix",550,500);
        H2F h = new H2F("h2",3,-0.5,2.5, 3, -0.5,2.5);
        h.attr().setTitleX("True Class");
        h.attr().setTitleY("Predicted Class");
        h.setBinContent(0, 2, 99.87);
        h.setBinContent(1, 1, 99.57);
        h.setBinContent(2, 0, 99.44);

        h.setBinContent(0, 0, 0.07);
        h.setBinContent(0, 1, 0.05);

        h.setBinContent(1, 0, 0.24);
        h.setBinContent(1, 2, 0.19);
        
        h.setBinContent(2, 1, 0.25);
        h.setBinContent(2, 2, 0.30);
        
        //c.region().getAxisFrame().getAxisX().getAttributes().setAxisTicksString(Arrays.asList("False","Positive","Negative"));
        //c.region().getAxisFrame().getAxisX().getAttributes().setAxisTicksPosition(Arrays.asList(0.0,1.0,2.0));
        
        c.draw(h,"FS");
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisTicksString(Arrays.asList("False","Positive","Negative"));
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisTicksPosition(Arrays.asList(0.0,1.0,2.0));
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisTicksString(Arrays.asList("False","Positive","Negative"));
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisTicksPosition(Arrays.asList(0.0,1.0,2.0));
        c.region().set("ml=150");
    }
    
    public static void main(String[] args){
        Histogram2Ddebug.drawConfusionMatrix();
    }
}
