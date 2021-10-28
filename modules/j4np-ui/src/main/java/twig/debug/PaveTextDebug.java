/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.debug;

import twig.graphics.TGCanvas;
import twig.widgets.LatexText;
import twig.widgets.Line;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class PaveTextDebug {
    
    public static void example1(){
        TGCanvas c = new TGCanvas(500,500);        
        
        Line l1 = new Line(0,0.5,1.0,0.5);
        Line l2 = new Line(0.5,0.,0.5,1.0);
        Line l3 = new Line(0.25,0.,0.25,1.0);
        Line l4 = new Line(0.75,0.,0.75,1.0);
        
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
    
    public static void main(String[] args){
        PaveTextDebug.example1();
    }
}
