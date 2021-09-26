/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.math.DataFitter;
import twig.math.F1D;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class TGCanvasDebug {
    
    public static void example1(){
        TGCanvas c = new TGCanvas(500,500);
        H1F h = TDataFactory.createH1F(2500, 40, 0.0, 1.0, 0.4, 0.2);
        
        h.attr().setFillColor(4);
        h.attr().setLineColor(2);
        h.attr().setLineWidth(1);
        h.attr().setMarkerColor(2);
        h.attr().setMarkerSize(8);
        
        c.view().region(0).getAxisFrame().addDataNode(new TGH1F(h,"EP"));
        /*c.view().region(0).getAxisFrame().getAxisX().setFixedLimits(0, 0.8);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisLineDraw(Boolean.FALSE);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisLineWidth(2);
        */
        //c.view().region(0).getAxisFrame().getAxisX().getAttributes().set
    }
    
    public static void example2(){
        
        TGCanvas c = new TGCanvas(500,500);
        
        c.view().divide(2, 2);
        H1F h = TDataFactory.createH1F(2500);
        
        h.attr().setMarkerSize(8);
        h.attr().setLineColor(81);
        h.attr().setMarkerColor(81);
        h.attr().setLineWidth(2);
        h.attr().setTitleX("M(p#pi^+#pi^-) [GeV]");
        h.attr().setTitleY("Counts/20 MeV");
        
        //h.attr().setMarkerStyle(2);
        
        c.view().region(0).getAxisFrame().addDataNode(new TGH1F(h,"EP"));
        c.view().region(0).setAxisLabelFont(new Font("Times",Font.PLAIN,18));
        c.view().region(0).setAxisTitleFont(new Font("Times",Font.PLAIN,22));
        //c.view().region(0).getAxisFrame().getAxisX().setFixedLimits(0, 0.8);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisTickMarkSize(10);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisTickMarkSize(10);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisLineWidth(1);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisLineWidth(1);
        
        F1D func = new F1D("func","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0.0,1.0);
        func.setParameters(new double[]{1,1,20,0.3,0.1});
        
        PaveText text = new PaveText("First line in the text - 42.45 +/- 0.1234",0.05,0.15);
        text.addLine("second line in the text - 0.98456");
        text.setTextColor(Color.orange);
        text.addLine("third line in the text - 1.2345");
        text.setFont(new Font("Helvetica",Font.BOLD,20));
        text.setNDF(true);        
        c.view().region(0).getAxisFrame().addWidget(text);
        
        DataFitter.fit(func, h, "N");
        func.attr().setLineWidth(5);
        func.attr().setLineColor(5);
        func.attr().setLineStyle(1);
        c.view().region(0).getAxisFrame().addDataNode(new TGF1D(func));
        
        List<String>  stats = func.getStats("M");
        
        PaveText  statsPave = new PaveText(stats,0.05,0.85);
        statsPave.setFont(new Font("Helvetica",Font.PLAIN,18));
        statsPave.setTextColor(Color.MAGENTA);
        c.view().region(0).getAxisFrame().addWidget(statsPave);
        c.view().region().setAxisTicksX(new double[]{0.15,0.35,0.75} , 
                new String[]{"0.15","0.35","0.75"});
    }
    
    public static void main(String[] args){
        TGCanvasDebug.example1();
    }
}
