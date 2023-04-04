/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.awt.Color;
import java.util.Random;
import twig.config.TPalette;
import twig.config.TStyle;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.math.F1D;
import twig.widgets.MultiPaveText;

/**
 *
 * @author gavalian
 */
public class TH1DrawStyles {
    
    public static void gradient(){
        Random r = new Random();
        
        TGCanvas c = new TGCanvas(600,600);
                        
        H1F h = TDataFactory.createH1F(250, 40, 0, 1.0, 0.3, 0.045);
        for(int i=0; i < 1800; i++ ){
            double value = r.nextDouble()+r.nextDouble();
            h.fill(value);
        }
        H1F hc = TDataFactory.createH1F(15000);
        h.attr().setLineStyle(0);
        hc.attr().setLineStyle(0);
        h.attr().setFillColor(2);
        hc.attr().setFillColor(3);
        hc.attr().setFillStyle(2);
        
        h.attr().setLegend("random Gaussian");
        c.view().region().draw(h,"EP");//.draw(hc,"same");
        
        F1D func = new F1D("func","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0.0,1.0);
        func.setParameters(1,1,50,0.3,0.02);
        func.fit(h);
        func.parameter(0).setLabel("p_0");
        func.parameter(1).setLabel("p_1");
        func.parameter(2).setLabel("#alpha");
        func.parameter(3).setLabel("#mu");
        func.parameter(4).setLabel("#sigma");
        func.attr().set("lw=2");
        func.attr().setLegend("fit function");
        
        c.draw(func,"same");
        c.region(0).setBackgroundColor(215, 187, 141);
        c.region(0).getAxisFrame().setBackgroundColor(203, 198, 171);
        c.region(0).showStats(0.98, 0.98);
        (( MultiPaveText ) c.region(0).getAxisFrame().getWidgets().get(0))
                .getBorder().attrFill.setFillColor(TPalette.colorToInt(new Color(215,187,171)));
        
         (( MultiPaveText ) c.region(0).getAxisFrame().getWidgets().get(0))
                .setPosition(1.02, 1.02);
         
         c.region().getAxisFrame().getAxisX().getAttributes().setAxisGridDraw(Boolean.TRUE);
         c.region().getAxisFrame().getAxisY().getAttributes().setAxisGridDraw(Boolean.TRUE);
        System.out.println("color will be " + TPalette.colorToInt(new Color(215,187,171,255)));
        
        Color c0 = TStyle.getInstance().getPalette().getColor(TPalette.colorToInt(new Color(215,187,171,255)));
        System.out.println(c0);
        c.repaint();
        
    }
    
    public static void main(String[] args){
        TH1DrawStyles.gradient();
    }
    
}
