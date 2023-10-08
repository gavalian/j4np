/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.awt.Color;
import javax.swing.JFrame;
import twig.config.TPalette;
import twig.config.TStyle;
import twig.config.TStyle.TwigStyle;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGAxisFrame;
import twig.graphics.TGDataCanvas;
import twig.graphics.TGRegion;

/**
 *
 * @author gavalian
 */
public class AxisDebug {
    
    public static void logarithmic(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TGDataCanvas dc = new TGDataCanvas();
        
        TGRegion af = new TGRegion(true);
        
        af.getAxisFrame().getAxisY().isLogarithmic = true;
        af.getAxisFrame().setLogY(true);
        
        dc.addNode(af);
        frame.add(dc);

        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
    
    public static void linear(){
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        TStyle.getInstance().setDefaultAxisBackgroundColor(new Color(200,200,200));
        TStyle.getInstance().setCanvasBackgroundColor(81);
        TStyle.getInstance().setAxisLineColor(4);
        TStyle.getInstance().setAxisLabelColor(4);
        TStyle.getInstance().setAxisTitleColor(4);
        TStyle.getInstance().setDefaultGridLineColor(4);

        TStyle.setStyle(TwigStyle.PRESENTATION);        
        
        H1F h = TDataFactory.createH1F(3500);
        h.attr().set("lc=3,fc=55,lw=2");

        TPalette p = new TPalette();
        int yellow = TPalette.createColor(247,204, 76, 255);
        
        Color c = p.getColor(yellow);
        
        System.out.println(" color = " + yellow + "  int value = " + c + " alpha = " + c.getAlpha());
        
        h.attr().setFillColor(yellow);
        
        TGDataCanvas dc = new TGDataCanvas();        
        TGRegion af = new TGRegion(true);
        
        af.getAxisFrame().getAxisX().getAttributes().setAxisTitle("X axis");
        af.getAxisFrame().getAxisY().getAttributes().setAxisTitle("Y axis");
        af.draw(h,"A");
        //af.getAxisFrame().getAxisY().isLogarithmic = true;
        //af.getAxisFrame().setLogY(true);
        
        dc.addNode(af);
        frame.add(dc);

        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
    
    public static void zAxisDebug(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TGDataCanvas dc = new TGDataCanvas();
        
        TGRegion af = new TGRegion(true);
        
        af.getAxisFrame().getAxisZ().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        
        //af.getAxisFrame().setLogY(true);
        
        dc.addNode(af);
        frame.add(dc);

        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
        
    }
    public static void main(String[] args){
        //AxisDebug.logarithmic();
        AxisDebug.linear();
        //AxisDebug.zAxisDebug();
    }
}
