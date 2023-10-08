/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class WidgetDebug {
    public static void polygonDebug(){
        TGCanvas c = new TGCanvas();
        H1F h = TDataFactory.createH1F(1200, 120, 0.0, 1.0, 0.6, 0.15);
        h.attr().setFillColor(54);
        
        Polygon p = new Polygon(new double[]{0.4,0.5,0.8,0.7,0.4}, new double[]{10,35,42,15,10});
        p.attrLine().setLineWidth(3);
        
        c.draw(h).draw(p);        

        Polygon b = Polygon.box(0.4, 20);
        b.attrLine().setLineStyle(3);
        b.attrLine().setLineWidth(2);
        b.attrFill().setFillColor(135);
        b.move(0.2, 5);
        
        Polygon b2 = Polygon.box(0.1, 1.0);
        b2.setNDF(true);
        b2.attrLine().setLineStyle(3);
        b2.attrLine().setLineWidth(1);
        b2.attrFill().setFillColor(132);
        b2.move(0.8, 0.0);        
        c.draw(b).draw(b2);
    }
    
    public static void main(String[] args){
        WidgetDebug.polygonDebug();
    }
}
