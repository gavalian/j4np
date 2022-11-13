/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import twig.data.GraphErrors;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;
import twig.widgets.Legend;

/**
 *
 * @author gavalian
 */
public class GraphErrorsDemo extends TwigDemo {

    @Override
    public String getName(){return "Graph Styles";}
    
    @Override
    public void drawOnCanvas(TGDataCanvas c) {
        String[] options = {"F","FL","FLP","FLPE","P","PE"};
        
        GraphErrors[] g1 = new GraphErrors[6];
        GraphErrors[] g2 = new GraphErrors[6];
        
        for(int i = 0 ; i < g1.length; i++){
            g1[i] = TDataFactory.getGraph(1);
            g2[i] = TDataFactory.getGraph(2);
            
            g1[i].attr().setFillColor(64);
            g1[i].attr().setLineColor(4);
            g1[i].attr().setLineWidth(2);
            
            g1[i].attr().setMarkerSize(10);                        
            g1[i].attr().setFillColor(175);
            g1[i].attr().setLineColor(5);
            g1[i].attr().setLineWidth(1);
            
            g1[i].attr().setMarkerOutlineColor(5);
            g1[i].attr().setMarkerOutlineWidth(1);
            g1[i].attr().setMarkerColor(0);
            
            g2[i].attr().setMarkerSize(10);
            g2[i].attr().setFillColor(172);
            g2[i].attr().setLineColor(2);
            g2[i].attr().setLineWidth(1);
            
            g2[i].attr().setMarkerOutlineColor(2);
            g2[i].attr().setMarkerOutlineWidth(1);
            g2[i].attr().setMarkerColor(0);
            
            g1[i].attr().setLegend("options = \"" + options[i] + "\""); 
        }
        
        
        //TGCanvas c = new TGCanvas(1200,800);
        c.divide(3,2);
        for(int i = 0; i < g1.length; i++){
            Legend leg = new Legend(0.05,0.98);            
            c.region(i).draw(g1[i],options[i]).draw(g2[i],options[i]+"same");
            leg.add(c.region().getAxisFrame().getDataNodes().get(0));
            c.region(i).draw(leg);
        }
    }

    @Override
    public String getCode() {
        return "some code ";
    }
    
}
