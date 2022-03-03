/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.math.F1D;

/**
 *
 * @author gavalian
 */
public class TwigFitExample {
    
    public static void main(String[] args){
        
        H1F h = TDataFactory.createH1Fs(750, 80, 0.0, 1.0, 0.4, 0.05);
        
        GraphErrors g = h.getGraph();
        
        g.attr().setTitleX("Random X");
        g.attr().setTitleY("Counts");        
        g.attr().setLegend("random gauss");
        g.attr().setMarkerColor(2);
        g.attr().setLineColor(2);
        g.attr().setMarkerSize(8);
        
        F1D func = new F1D("func",
                "[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0.0,1.0);
        
        func.attr().setLegend("fit with gauss+pol2");
        func.setParameters(new double[]{1,1,150,0.5,0.1});
        func.setParLimits(2,  50, 10000);
        func.setParLimits(3, 0.3, 0.6);
        func.setParLimits(4, 0.01, 1.0);
                
        func.fit(g,"N");
        func.show();
        
        func.attr().setLineColor(3);
        func.attr().setLineWidth(2);
        func.attr().setLineStyle(2);
                
        TGCanvas c = new TGCanvas(530,450);
        c.view().region().draw(g).draw(func,"same");
        c.view().region().showLegend(0.02, 0.98);
        c.view().region().showStats(0.6, 0.95);
        c.view().region().axisLimitsX(0, 1);
    }
}
