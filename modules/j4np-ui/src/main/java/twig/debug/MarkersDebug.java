/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.debug;

import twig.data.GraphErrors;
import twig.graphics.TGCanvas;
import twig.widgets.LatexText;
import twig.widgets.Line;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class MarkersDebug {
    
    public static void example1(){
    
        TGCanvas c = new TGCanvas("region_2_diagram", 900,700);
        GraphErrors gr = new GraphErrors("gr");

        c.view().region().getInsets().left(5).right(5).bottom(5).top(5);
        Line line = new Line(0.6,0.0,0.4,1.0);
        line.setNDF(true).setWidth(3).setStyle(2);
        
        for(int row = 0 ; row < 13; row++){
            for(int col = 0; col < 15; col++){
                if(row!=6) gr.addPoint(col+(row%2)*0.5, row);
            }
        }

        GraphErrors hits = new GraphErrors("hits");
        hits.addPoint(6, 12);
        hits.addPoint(6.5, 11);
        hits.addPoint(5.5, 11);
        
        hits.addPoint(6.0, 10);        
        hits.addPoint(6.5, 9);        
        hits.addPoint(7.0, 8);
        hits.addPoint(7.5, 7);
        hits.addPoint(6.5, 7);
        
        
        hits.addPoint(7.5, 5);        
        hits.addPoint(8, 4);        
        hits.addPoint(7.5, 3);        
        hits.addPoint(8.5, 3);        
        hits.addPoint(8, 2);
        hits.addPoint(8.5, 1);
        hits.addPoint(9, 0);
        
        
        hits.attr().setMarkerColor(5);
        hits.attr().setMarkerSize(50);
        hits.attr().setMarkerStyle(9);
        hits.attr().setMarkerOutlineColor(1);
        hits.attr().setMarkerOutlineWidth(1);                
        
        gr.attr().setMarkerSize(50);
        gr.attr().setMarkerStyle(9);
        gr.attr().setMarkerColor(26);
        gr.attr().setMarkerOutlineColor(1);
        gr.attr().setMarkerOutlineWidth(1);
       
        PaveText t1 = new PaveText("Super Layer 3",0.75,1.01,false,24);
        t1.setNDF(true);//.setRotate(LatexText.TextRotate.LEFT);
        
        PaveText t2 = new PaveText("Super Layer 4",0.75,0.53,false,24);
        t2.setNDF(true);//.setRotate(LatexText.TextRotate.LEFT);
        
        PaveText t3 = new PaveText("Region 2",-0.02,0.40,false,36);
        t3.setNDF(true).setRotate(LatexText.TextRotate.LEFT);
        
        c.view().region().drawFrame(false);
        c.view().region()
                .draw(gr).draw(hits,"same")
                .draw(line).draw(t1).draw(t2).draw(t3);
        
       
        
        c.repaint();
        
        hits.show();
    }
    
    public static void main(String[] args){
        MarkersDebug.example1();
    }
}
