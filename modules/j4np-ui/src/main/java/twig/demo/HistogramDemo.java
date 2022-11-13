/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import twig.config.TStyle;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public class HistogramDemo extends TwigDemo {

    
    @Override
    public String getName(){return "H Draw Styles";}
    
    @Override
    public void drawOnCanvas(TGDataCanvas c) {
        
        TStyle.setStyle(TStyle.TwigStyle.MONITOR);
        
        String[] options = {"FE","A","B","BP","G","EP"};
        H1F[] h = new H1F[6];
        for(int i = 0; i < h.length; i++)
            h[i] = TDataFactory.createH1F(900, 40, 0.0, 1.0, 0.4, 0.1);
        

        c.divide(3,2);
        
        for(int i = 0; i < h.length; i++){
            h[i].attr().setTitleX("random X");
            h[i].attr().setTitleY("count");            
            h[i].attr().setFillColor(62);
            h[i].attr().setLineWidth(1);
            h[i].attr().setLegend("options = \"" + options[i] + "\"");            
        }
        
        for(int i = 0; i < h.length; i++){
            c.region(i).draw(h[i],options[i]);
            c.cd(i).region().showLegend(0.05,0.98);
        }
    }

    @Override
    public String getCode() {
        return "TStyle.setStyle(TStyle.TwigStyle.MONITOR);\n"+                       
                "String[] options = {\"FE\",\"A\",\"B\",\"BP\",\"G\",\"EP\"};\n";                    
    }
    
}
