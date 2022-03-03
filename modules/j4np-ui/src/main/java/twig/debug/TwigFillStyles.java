/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class TwigFillStyles {
    
    public static void customFillStyle(){
        H1F h1 = TDataFactory.createH1Fs(2500, 80, 0.0, 1.0, 0.7, 0.1);
        H1F h2 = TDataFactory.createH1Fs(2500, 80, 0.0, 1.0, 0.2, 0.05);        
        
        h1.attr().setFillStyle(2);        
        h1.attr().setLineColor(2);
        h1.attr().setFillColor(62);
        h1.attr().setTitleX("Random Gaussian");
        h1.attr().setLegend("gaus #mu=0.7, #sigma=0.1");
        
        //-- easiest way to set data set attributes --------
        // fc - fill color, lc - line color, fs - fill style
        
        h2.attr().set("fc=64,lc=4,fs=12");
        h2.attr().setLegend("gaus #mu=0.2, #sigma=0.05");
        
        TGCanvas c = new TGCanvas(530,450);

        c.view().region()
                .draw(h1)
                .draw(h2,"same");    
        c.view().region().showLegend(0.6, 0.95);
    }
    
    public static void filledHistogram(){
        
        H1F h1 = TDataFactory.createH1Fs(2500, 80, 0.0, 1.0, 0.7, 0.1);
        H1F h2 = TDataFactory.createH1Fs(2500, 80, 0.0, 1.0, 0.2, 0.05);        
        
        h1.attr().setLineColor(2);
        h1.attr().setFillColor(62);
        h1.attr().setTitleX("Random Gaussian");
        h1.attr().setLegend("gaus #mu=0.7, #sigma=0.10");
        
        //-- easiest way to set data set attributes --------
        // fc - fill color, lc - line color, fs - fill style
        
        h2.attr().set("fc=64,lc=4");
        h2.attr().setLegend("gaus #mu=0.2, #sigma=0.05");
        
        TGCanvas c = new TGCanvas(530,450);

        c.view().region()
                .draw(h1)
                .draw(h2,"same");    
        c.view().region().showLegend(0.6, 0.95);
    }
    
    public static void main(String[] args){        
        TwigFillStyles.filledHistogram();
        TwigFillStyles.customFillStyle();
    }
}
