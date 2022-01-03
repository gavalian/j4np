/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.debug;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.graphics.TGF1D;
import twig.graphics.TGH1F;
import twig.math.DataFitter;
import twig.math.F1D;
import twig.math.PDF1D;
import twig.widgets.PaveText;
import twig.widgets.PaveText.PaveTextStyle;

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
        c.view().region().getInsets().left(120);
        c.repaint();
        /*c.view().region(0).getAxisFrame().getAxisX().setFixedLimits(0, 0.8);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisLineDraw(Boolean.FALSE);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisLineWidth(2);
        */
        //c.view().region(0).getAxisFrame().getAxisX().getAttributes().set
    }
    
    public static void example2(){
        
        TGCanvas c = new TGCanvas(700,700);
        
        c.view().divide(1, 2);
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
        
        PaveText text = new PaveText("First line in the text - 42.45 +/- 0.1234",0.25,0.4);
        text.addLine("second line in the text - 0.98456");
        text.setTextColor(Color.orange);
        text.addLine("third line in the text - 1.2345");
        text.setFont(new Font("Helvetica",Font.BOLD,20));
        text.setNDF(true);
        c.view().region(1).getAxisFrame().addWidget(text);
        
        DataFitter.fit(func, h, "N");
        func.attr().setLineWidth(5);
        func.attr().setLineColor(5);
        func.attr().setLineStyle(1);
        c.view().region(0).getAxisFrame().addDataNode(new TGF1D(func));
        
        List<String>  stats = func.getStats("M");
        
        PaveText  statsPave = new PaveText(stats,0.05,0.0);
        statsPave.setFont(new Font("Helvetica",Font.PLAIN,18));
        statsPave.setTextColor(Color.MAGENTA);
        c.view().region(0).getAxisFrame().addWidget(statsPave);
        c.view().region().setAxisTicksX(new double[]{0.15,0.35,0.75} ,
                new String[]{"0.15","0.35","0.75"});
    }
    
    
    public static void example3(){
        
        TGCanvas c = new TGCanvas(500,500);
        
        //c.view().divide(2, 2);
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
        
        
        
        F1D func = new F1D("func","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0.0,1.0);
        func.setParameters(new double[]{1,1,20,0.3,0.1});
        
        F1D fg = new F1D("func","[amp]*gaus(x,[mean],[sigma])",0.0,1.0);
        
        fg.setParameters(new double[]{20,0.3,0.1});
        F1D fb = new F1D("func","[p0]+[p1]*x",0.0,1.0);
        fb.setParameters(new double[]{1,1});
        
        PDF1D pdf1 = new PDF1D("p1",0.0,1.0,
                new String[]{"[p0]+[p1]*x","[a]*gaus(x,[m],[s])"});
        pdf1.setParameters(new double[]{1,1,20,0.3,0.2});
        pdf1.show();
        DataFitter.fit(pdf1, h, "N");
        
        //c.view().region(0).getAxisFrame().addDataNode(new TGF1D(func));
        c.view().region().draw(pdf1.list(), "same").draw(pdf1.getFunction(),"same");
        c.repaint();
        
    }
    
    public static void debugH2F(){
        H2F rh = TDataFactory.createH2F(250000,60);
        //H2F rh = new H2F("",3,-1.0,1.0,3,-1.0,1.0);
        rh.setBinContent(0, 0, 2);
        rh.setBinContent(0, 1, 4);
        rh.setBinContent(1, 1, 6);
        
        TGCanvas c = new TGCanvas(600,900);
        c.view().divide(2, 2);
        c.view().region(0).draw(rh);
        c.view().region(1).draw(rh.projectionX());
        c.view().region(2).draw(rh.projectionY());
        
    }
    
    
    public static void fitExample(){
        
        
        TGCanvas c = new TGCanvas(600,550);
        H1F h = TDataFactory.createH1F(25000, 120, 0, 1, 0.6, 0.05);
        
        
        F1D func = new F1D("func","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0.1,0.9);
        func.setParameters(new double[]{1.,1.,1500,0.5,0.04});
        
        func.attr().setLineWidth(3);
        func.attr().setLineColor(5);
        //h.attr().setLineColor(3);
        h.attr().setLineWidth(3);
        h.attr().setTitleX("Pulse Time (ns)");
        h.attr().setTitleY("Charge");
        
        DataFitter.fit(func, h, "");
        
        c.view().region(0).draw(h);
        c.view().region(0).draw(func,"same");
        
        //List<String> stats = func.getStats("M");
        PaveText    paveStats = new PaveText(func.getStats("M"),0.02,0.68, false,18);
        paveStats.setNDF(true).setMultiLine(true);


        //paveStats.paveStyle = PaveTextStyle.MULTILINE;
        
        PaveText   histStats = new PaveText(h.getStats("M"),0.02,0.98,false,18);
      
        
        c.view().region(0).draw(paveStats).draw(histStats);
        c.repaint();
    }
    
    public static void main(String[] args){
        //TGCanvasDebug.example2();
        //TGCanvasDebug.debugH2F();
        
        TGCanvasDebug.fitExample();
        
        
    }
}
