/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import java.awt.Font;
import twig.data.H1F;
import twig.data.TDataFactory;

/**
 *
 * @author gavalian
 */
public class TGCanvasDebug {
    
    public static void example1(){
        TGCanvas c = new TGCanvas();
        H1F h = TDataFactory.createH1F(2500);
        h.attr().setFillColor(83);
        h.attr().setLineWidth(2);
        c.view().region(0).getAxisFrame().addDataNode(new TGH1F(h));
        c.view().region(0).getAxisFrame().getAxisX().setFixedLimits(0, 0.8);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisLineDraw(Boolean.FALSE);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisLineWidth(2);
        
        //c.view().region(0).getAxisFrame().getAxisX().getAttributes().set
    }
    
    public static void example2(){
        
        TGCanvas c = new TGCanvas(550,600);
        
        H1F h = TDataFactory.createH1F(2500);
        
        h.attr().setMarkerSize(8);
        //h.attr().setLineColor(2);
        h.attr().setLineWidth(2);
        h.attr().setTitleX("M^x(pK_s) [GeV]");
        h.attr().setTitleY("Counts/20 MeV");
        
        //h.attr().setMarkerStyle(2);
        c.view().region(0).getAxisFrame().addDataNode(new TGH1F(h,"EP"));
        c.view().region(0).setAxisLabelFont(new Font("Times",Font.PLAIN,20));
        c.view().region(0).setAxisTitleFont(new Font("Times",Font.PLAIN,24));
        //c.view().region(0).getAxisFrame().getAxisX().setFixedLimits(0, 0.8);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisTickMarkSize(8);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisTickMarkSize(8);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        c.view().region(0).getAxisFrame().getAxisX().getAttributes().setAxisLineWidth(2);
        c.view().region(0).getAxisFrame().getAxisY().getAttributes().setAxisLineWidth(2);
    }
    
    public static void main(String[] args){
        TGCanvasDebug.example2();
    }
}
