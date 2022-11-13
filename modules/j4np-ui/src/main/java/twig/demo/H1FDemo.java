/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import java.util.Arrays;
import twig.config.TStyle;
import twig.config.TStyle.TwigStyle;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 * Histogram plotting demo, introduction to styles
 * @author gavalian
 */
public class H1FDemo {
    
    public static void drawStyles(){
        
        TStyle.setStyle(TwigStyle.MONITOR);
        
        String[] options = {"FE","A","B","BP","G","EP"};
        H1F[] h = new H1F[6];
        for(int i = 0; i < h.length; i++)
            h[i] = TDataFactory.createH1F(900, 40, 0.0, 1.0, 0.4, 0.1);
        
        TGCanvas c = new TGCanvas(1200,800);
        c.view().divide(3,2);
        
        for(int i = 0; i < h.length; i++){
            h[i].attr().setTitleX("random X");
            h[i].attr().setTitleY("count");            
            h[i].attr().setFillColor(62);
            h[i].attr().setLineWidth(1);
            h[i].attr().setLegend("options = \"" + options[i] + "\"");            
        }
        
        for(int i = 0; i < h.length; i++){
            c.cd(i).draw(h[i],options[i]);
            c.cd(i).region().showLegend(0.05,0.98);
        }
        
    }
    
    public static void drawRotation(){
        TStyle.setStyle(TwigStyle.MONITOR);
        H1F  h1 = TDataFactory.createH1F(900, 25, 0.0, 1.0, 0.4, 0.1);
        H1F  h2 = h1.copy();
        
        h1.attr().setLineColor(3);
        h1.attr().setLineWidth(12);
        
        h2.attr().setFillColor(4);
        
        TGCanvas c = new TGCanvas(950,550);
        c.view().divide(2,1);
        c.cd(0).draw(h2).cd(1).draw(h1,"BR");                
    }
    
    public static void benchmark(){
        H1F  b = new H1F("benchmark",0.5,5.5, new float[]{1.f,1.3f,1.2f,1.4f,0.0f});
        H1F bb = new H1F("benchmark",0.5,4.5, new float[]{0.0f,0.0f,1.12f,0.0f});
        //b.show();
        
        b.attr().setLineColor(42);
        b.attr().setLineWidth(35);
        b.attr().setTitleX("Time (ms)");
        b.attr().setLegend("Single-Threaded");
        bb.attr().setLineColor(45);
        bb.attr().setLineWidth(35);
        bb.attr().setLegend("Multi-Threaded");
        
        TGCanvas c = new TGCanvas(800,400);
        c.view().left(150);
        c.draw(b,"BR").draw(bb,"BRsame");
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(false);
        c.region().getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(false);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisLineDraw(false);
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisTicksPosition(Arrays.asList(1.0,2.0,3.0,4.0));
        c.region().getAxisFrame().getAxisY().getAttributes().setAxisTicksString(Arrays.asList("CLARA (AMD)","CLARA (Intel)","REC Util (AMD)","REC Util (Intel)"));
        //c.region().showLegend(0.55, 0.25);
    }
    
    public static void main(String[] args){
        H1FDemo.drawStyles();
        H1FDemo.drawRotation();
        H1FDemo.benchmark();
    }
}
