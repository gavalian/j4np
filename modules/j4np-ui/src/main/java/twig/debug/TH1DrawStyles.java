/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class TH1DrawStyles {
    
    public static void gradient(){
        TGCanvas c = new TGCanvas(600,600);
                        
        H1F h = TDataFactory.createH1F(25000);
        H1F hc = TDataFactory.createH1F(15000);
        h.attr().setLineStyle(0);
        hc.attr().setLineStyle(0);
        h.attr().setFillColor(2);
        hc.attr().setFillColor(3);
        hc.attr().setFillStyle(2);
        c.view().region().draw(h,"G");//.draw(hc,"same");
        c.repaint();
        
    }
    
    public static void main(String[] args){
        TH1DrawStyles.gradient();
    }
    
}
