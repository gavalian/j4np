/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    
    public static void drawBenchmarks(){
        TGCanvas c = new TGCanvas();
        H1F hb = new H1F("hb",9,0.5,9.5);
        hb.setBinContent(0, 14);
        hb.setBinContent(2, 18);
        hb.setBinContent(4, 12);
        hb.setBinContent(6, 22);
        hb.setBinContent(8, 28);
        hb.attr().set("lc=2,lw=25");
        List<Double>   ticks = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        
        ticks.add(1.0);
        ticks.add(3.0);
        ticks.add(5.0);
        ticks.add(7.0);
        ticks.add(9.0);
        
        strings.add("1-");
        strings.add("1+");
        strings.add("1-:1+");
        strings.add("1-:1-");
        strings.add("1+:1+");
        strings.add("1+:1+");
        
        c.draw(hb,"BR");
        c.region().axisY().getAttributes().setAxisTicksPosition(
               Arrays.asList(1.0,3.0,5.0,7.0,9.0)
        );
        
        c.region().axisY().getAttributes().setAxisTicksString(
                strings);
        
        c.repaint();
    }
    
    public static void drawColors(){
        Random r = new Random();        
        TGCanvas c = new TGCanvas(600,600);                        
        H1F h = TDataFactory.createH1F(2500, 240, 0, 1.0, 0.3, 0.045);
        for(int i=0; i < 1800; i++ ){
            double value = r.nextDouble()+r.nextDouble();
            h.fill(value);
        }
        
        h.attr().setTitleY("counts");
        h.attr().setTitleX("x-axis");
        TPalette p = TStyle.getInstance().getPalette();
        int cint = TPalette.createColor(250, 250, 51,250);
        int cint2 = TPalette.createColorFromString("#FF00CC");
        
        //h.attr().set("fc=#FAFA33,mc=#FF0000,lc=#000000");
        h.attr().set("fc=#B80000,mc=#FF0000,lc=5");
        
        //System.out.println(" COLOR INT = " + cint);
        
        Color col = p.getColor(cint2);
        //System.out.println(" color = " + col);
        c.region().set("fc=#EEEEEE,ac=1,lw=1");
        c.setBackground(null);
        //c.region().setBackgroundColor(cint, cint, cint)
        //c.region().getInsets().left(120);
        c.draw(h,"PEF");
    }
    
    public static void main(String[] args){
        //TH1DrawStyles.gradient();
        //TH1DrawStyles.drawBenchmarks();
        TH1DrawStyles.drawColors();
    }
    
}
