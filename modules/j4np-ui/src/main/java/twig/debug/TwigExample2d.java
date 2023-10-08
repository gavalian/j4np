/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.awt.Color;
import java.awt.Font;
import twig.data.H2F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.widgets.Line;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class TwigExample2d {
    public static void draw2d1(){        
        TGCanvas c = new TGCanvas(500,500);
        H2F h = TDataFactory.createH2F(45000, 80);
        h.attr().setTitleX("random X");
        h.attr().setTitleY("random Y");
        
        Line line1 = new Line(-1.0,0.4,0.8,-1.0);
        Line line2 = new Line(0.2,-1.0,-0.6,1.0);
        
        line2.setLineColor(Color.red);
        line2.setStyle(6);
        
        c.view().region().draw(h,"");
        c.view().region().draw(line1).draw(line2);
        c.repaint();
    }
    
    public static void draw2d2(){
        
        TGCanvas c = new TGCanvas(500,500);
        H2F h = TDataFactory.createH2F(45000, 60);
        h.attr().setTitleX("random X");
        h.attr().setTitleY("random Y");
        
        Line line1 = new Line(-1.0,0.4,0.8,-1.0);
        Line line2 = new Line(0.2,-1.0,-0.6,1.0);
        
        line2.setLineColor(Color.red);
        line2.setStyle(6);
        
        c.view().region().draw(h,"F");
        c.view().region().draw(line1).draw(line2);
        
        //------ drawing text one the plot inside of box
        PaveText pave = new PaveText(0.05,0.95);
        pave.setFont(new Font("PT Serif",Font.BOLD,18));
        pave.setNDF(true).drawBox = true;
        pave.addLine("2d gassuaian example")
                .addLine("g_1 - #mu = (-0.5,-0.6)")
                .addLine("g_2 - #mu = (0.5,0.7)");
                
        c.view().region().draw(pave);
        c.repaint();
    }
    
    public static void main(String[] args){
        TwigExample2d.draw2d2();
    }
}
