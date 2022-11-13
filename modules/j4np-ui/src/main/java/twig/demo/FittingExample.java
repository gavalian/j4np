/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import java.awt.Color;
import twig.config.TPalette;
import twig.config.TStyle;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGDataCanvas;
import twig.math.F1D;
import twig.widgets.LatexText;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class FittingExample extends TwigDemo {

    @Override
    public String getName(){return "Fitting";}
    
    @Override
    public void drawOnCanvas(TGDataCanvas c) {
        
        GraphErrors g = TDataFactory.getGraph(1);
        
        g.attr().setTitleX("x");
        g.attr().setTitleY("e^x");
        g.attr().setLegend("exponent (ae^b^x)");
        // Short hand for setting different properties
        // lc - line color, lw - line width, mc - marker color,
        // ms - marker size, mt - marker style
        g.attr().set("lc=2,lw=1,mc=32,ms=12,mt=4");
        
        g.attr().setMarkerOutlineColor(2);
        g.attr().setMarkerOutlineWidth(1);
        
        F1D func = new F1D("func","[a]*exp([b]*x)",0.5,2.5);
        func.fit(g);
        func.attr().set("lc=5,ls=3");
        
        PaveText resfit = new PaveText(0.05,0.7); 
        resfit.addLines(func.getStats(""));
        
        resfit.setBackgroundColor(new Color(240, 240, 255));
        resfit.setBorderColor(Color.black);
        
        c.divide(3,2);
        c.region(0).draw(g,"PE").draw(func,"same");
        c.region(0).showLegend(0.05, 0.98);
        c.region(0).draw(resfit);
        
        
        
        H1F h = TDataFactory.createH1F(2500, 80, 0.0, 1.0, 0.4, 0.05);
        
        h.attr().set("lc=2");
        
        F1D func2 = new F1D("func2","[p0]+[amp]*gaus(x,[mean],[sigma])",0.0,1.0);
        
        func2.setParameters(new double[]{10.0,150,0.4,0.3});
        

        func2.setParLimits(1, 0.0, 1000.0);
        func2.setParLimits(2, 0.0, 1.0);
        func2.setParLimits(3, 0.0, 0.2);
        
        func2.fit(h);
        func2.attr().set("lc=1,ls=4,lw=2");
        
        PaveText results = new PaveText(1.02,1.02); 
        results.addLines(func2.getStats(""));
        results.setAlign(LatexText.TextAlign.TOP_RIGHT);
        
        c.region(1).draw(h,"FLE").draw(func2,"same");
        c.region(1).draw(results);
    }

    @Override
    public String getCode() {
        return "";
    }
    
}
