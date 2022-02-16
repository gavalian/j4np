/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.debug;

import java.awt.Color;
import java.awt.Font;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.widgets.LatexText;
import twig.widgets.Legend;
import twig.widgets.Line;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class PaveTextDebug {
    
    public static void example1(){
        
        TGCanvas c = new TGCanvas(500,500);        
        c.view().region().setDebugMode(true);
        Line l1 = new Line(0,0.5,1.0,0.5);
        Line l2 = new Line(0.5,0.,0.5,1.0);
        Line l3 = new Line(0.25,0.,0.25,1.0);
        Line l4 = new Line(0.75,0.,0.75,1.0);
        
        l1.setLineColor(new Color(220,220,220));
        
        PaveText text = new PaveText("Some text",0.25,0.5);
        PaveText text2 = new PaveText("Some text",0.75,0.5);
        PaveText text3 = new PaveText("Some text",0.5,0.8);
        PaveText text4 = new PaveText("Some text",0.5,0.2);
        
        text2.setAlign(LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        text3.setAlign(LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        text3.setRotate(LatexText.TextRotate.LEFT);
        text4.setRotate(LatexText.TextRotate.RIGHT);
        
        c.view().region().draw(l1).draw(l2).draw(l3).draw(l4);        
        c.view().region().draw(text).draw(text2).draw(text3).draw(text4);
        
        c.repaint();
        
    }
    
    
    public static void exmapleLegend(){
    
        TGCanvas c = new TGCanvas(700,500);        
        c.view().region().setDebugMode(true);
        
        H1F h = TDataFactory.createH1F(2500);        
        H1F h2 = TDataFactory.createH1F(4500);
        H1F h3 = TDataFactory.createH1F(6500);

        h.setName("conventional");
        h2.setName("denoised");
        
        GraphErrors gr = h3.getGraph();
        
        
        gr.attr().setMarkerColor(1);
        gr.attr().setMarkerSize(8);
        gr.attr().setLineColor(5);
        gr.attr().setLineWidth(2);
        
        h2.attr().setLineColor(2);
        //h2.attr().setFillColor(42);
        
        h.attr().setLineColor(4);
        h.attr().setFillColor(84);
        h.attr().setFillStyle(2);
        
        Legend leg = new Legend(0.02,0.98);
        leg.add(h,"conventiona tracking");
        leg.add(h2,"de-noised conventional");
        leg.add(gr,"de-noised ai");
        
        leg.setFont(new Font("Avenir",Font.BOLD,18));
        c.view().region().draw(gr).draw(h2,"same").draw(h,"same");
        
        c.view().region().draw(leg);
        c.view().region().axisLimitsX(0.0, 1.0);
        
        PaveText pt = new PaveText(0.68,0.98);
        pt.drawBox = false; pt.fillBox = false;
        pt.setFont(new Font("Avenir Next",Font.PLAIN,18));
        
        pt.addLines(h.getStatText());
        pt.addLines(h2.getStatText());
        
        c.view().region().draw(pt);
        c.repaint();
    }
    
    public static void main(String[] args){
        //PaveTextDebug.example1();
        PaveTextDebug.exmapleLegend();
    }
}
