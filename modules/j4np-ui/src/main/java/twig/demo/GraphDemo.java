/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import java.awt.Color;
import twig.config.TStyle;
import twig.data.GraphErrors;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.math.F1D;
import twig.widgets.Legend;
import twig.widgets.Line;
import twig.widgets.PaveText;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class GraphDemo {
    
    public static void drawStyles(){
        
        TStyle.setStyle(TStyle.TwigStyle.MONITOR);
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
        
        
        TGCanvas c = new TGCanvas(1200,800);
        c.view().divide(3,2);
        for(int i = 0; i < g1.length; i++){
            Legend leg = new Legend(0.05,0.98);
            
            c.cd(i).draw(g1[i],options[i]).draw(g2[i],options[i]+"same");
            leg.add(c.region().getAxisFrame().getDataNodes().get(0));
            c.cd(i).draw(leg);
        }
    }
    
    public static void drawMarkers(){
        
        GraphErrors g = TDataFactory.getGraph(1);
        
        g.attr().setTitleX("x");
        g.attr().setTitleY("e^x");
        
        g.attr().setLineColor(2);
        g.attr().setLineWidth(1);
        
        g.attr().setMarkerColor(32);
        g.attr().setMarkerSize(16);
        g.attr().setMarkerStyle(4);
        
        g.attr().setMarkerOutlineColor(2);
        g.attr().setMarkerOutlineWidth(1);
        
        Line l1 = new Line(0.45,6,2.25,6);
        Line l2 = new Line(0.45,8,2.25,8);
        
        l1.setStyle(0);
        l2.setStyle(3);
        
        Polygon poly = Polygon.box(0.2, 0.3).move(0.4, 0.5);
        poly.line().setLineColor(5).setLineStyle(3);//.setLineWidth(2);
        
        Polygon poly2 = new Polygon(new double[]{0.5,1,1.5,0.4,0.5}, new double[]{6,8,5,0.5,6});
        poly2.setNDF(false);
        
        poly2.fill().setFillStyle(10);
        poly2.fill().setFillColor(175);
        poly2.line().setLineColor(5).setLineStyle(2);
        
        Polygon poly3 = new Polygon(new double[]{0.5,1,1.5,0.4}, new double[]{6,8,5,0.5});
        poly3.move(0.5, 2.5);
        
        TGCanvas c = new TGCanvas(600,550);
        c.draw(g);
        c.draw(l1).draw(l2).draw(poly2).draw(poly3);
        
    }
    
    public static void graphFitting(){
         GraphErrors g = TDataFactory.getGraph(1);
        
        g.attr().setTitleX("x");
        g.attr().setTitleY("e^x");
        
         g.attr().setLineColor(2);
        g.attr().setLineWidth(1);
        
        g.attr().setMarkerColor(32);
        g.attr().setMarkerSize(16);
        g.attr().setMarkerStyle(4);
        
        g.attr().setMarkerOutlineColor(2);
        g.attr().setMarkerOutlineWidth(1);
               
        g.attr().setLegend("graph y=ae^b^x");
        
        F1D func = new F1D("func","[a]*exp([b]*x)",0.5,2.25);
        
        func.attr().setLineColor(1);
        func.attr().setLineWidth(1);
        func.attr().setLineStyle(3);
        
        func.fit(g);
        func.attr().setLegend(String.format("f=ae^b^x (a=%.4f,b=%.4f)", 
                func.getParameter(0),func.getParameter(1)));
        // using the following constructor switches the coordinate system
        // to relative, from 0 to 1.0;
        PaveText p = new PaveText(0.25,0.15,true); 
        p.setFillBox(false);
        p.setDrawBox(false);
        
        p.addLine("Randomly Generated points")
                .addLine("From distribution function y=ae^b^x");
        p.setTextColor(new Color(80,0,180));        
// This constructor uses real coordinates from the graph
        PaveText p2 = new PaveText(0.1,0.7,true);
        p2.addLine("The distribution is fitted")
                .addLine("with function f=ae^b^x");
        
        p2.setTextColor(new Color(0,0,175));
        p2.setBackgroundColor(new Color(245,245,255));
        p2.setBorderColor(Color.black);
        
        TGCanvas c = new TGCanvas(500,600);
        c.draw(g).draw(func,"same");
        c.view().region().showLegend(0.05, 0.98);
        c.draw(p).draw(p2);
    }
    
    public static void main(String[] args){
        //GraphDemo.drawStyles();
        //GraphDemo.drawMarkers();
        GraphDemo.graphFitting();
    }
}
