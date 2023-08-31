/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import java.util.ArrayList;
import java.util.List;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.math.F1D;

/**
 *
 * @author gavalian
 */
public class TGStyleFactory {
    
    
    public static void markersAndColors(TGDataCanvas canvas, int size, String options){
        canvas.divide(1, 1);
        List<GraphErrors>  graphs = new ArrayList<>();
        for(int i = 0; i < size; i++){
            GraphErrors  graph = TDataFactory.createGraph(i+1, i+1); 
            graph.attr().setMarkerSize(12);
            graph.attr().setLineStyle(1);

            graph.attr().setLegend(String.format("marker=%d, color=%d", i+1,i+1));
            graphs.add(graph);
        }
        graphs.get(0).attr().setTitleY("Marker Styles & Colors");
        canvas.cd(0).region().draw(graphs, options);
        canvas.cd(0).region().setAxisTitleSize(20);
        canvas.cd(0).region().setAxisLabelSize(24);
        canvas.repaint();
    }
    
    public static void lineStyles(TGDataCanvas canvas, int size, String options){
        canvas.divide(1, 1);
        
        List<F1D> funcs = new ArrayList<>();
        for(int i = 0 ; i < 15; i++){
            F1D f = new F1D("f1","[a]",1,9);
            f.setParameter(0, i+1);
            f.attr().setLineStyle(i);
            f.attr().setLegend("style="+i);
            f.attr().setLineWidth(3);
            funcs.add(f);
        }
        funcs.get(0).attr().setTitleY("Line Styles");
        canvas.cd(0).region().draw(funcs, "");
        canvas.cd(0).region().getAxisFrame().getAxisY().setFixedLimits(0, 16);
        canvas.repaint();
    }
    
    public static void darkModeShow(TGDataCanvas canvas){
        canvas.divide(1, 1);
        
        canvas.region().setBackgroundColor(40, 45, 40);
        canvas.region().getAxisFrame().setBackgroundColor(50, 55, 50);
        canvas.region().getAxisFrame().getAxisX().getAttributes().setAxisLineColor(41);
        canvas.region().getAxisFrame().getAxisX().getAttributes().setAxisLabelColor(41);
        canvas.region().getAxisFrame().getAxisX().getAttributes().setAxisTitleColor(41);
        canvas.region().getAxisFrame().getAxisX().getAttributes().setAxisGridLineColor(61);
        
        canvas.region().getAxisFrame().getAxisY().getAttributes().setAxisLineColor(41);
        canvas.region().getAxisFrame().getAxisY().getAttributes().setAxisLabelColor(41);
        canvas.region().getAxisFrame().getAxisY().getAttributes().setAxisTitleColor(41);
        canvas.region().getAxisFrame().getAxisY().getAttributes().setAxisGridLineColor(61);
        
        for(int i = 2; i <= 10; i++){
            H1F h = TDataFactory.createH1F(4200*(12-i), 240, 0.0, 1.0, 0.5, 0.12);
            h.attr().setFillColor(i);
            canvas.region().draw(h,"same");
        }        
        canvas.repaint();
    }
    
    public static void markersAndColors(TGDataCanvas canvas){
        canvas.divide(2, 1);
        List<GraphErrors>  graphs = new ArrayList<>();
        for(int i = 0; i < 15; i++){
            GraphErrors  graph = TDataFactory.createGraph(i+1, i+1);
            
            graph.attr().setMarkerSize(12);
            graph.attr().setLineStyle(1);
            graphs.add(graph);
        }
        graphs.get(0).attr().setTitleY("Marker Styles & Colors");

        canvas.cd(0).region().draw(graphs, "PEL");
        canvas.cd(0).region().setAxisTitleSize(20);
        canvas.cd(0).region().setAxisLabelSize(24);
        List<F1D> funcs = new ArrayList<>();
        for(int i = 0 ; i < 15; i++){
            F1D f = new F1D("f1","[a]",1,9);
            f.setParameter(0, i+1);
            f.attr().setLineStyle(i);
            f.attr().setLineWidth(3);
            funcs.add(f);
        }
        funcs.get(0).attr().setTitleY("Line Styles");
        canvas.cd(1).region().draw(funcs, "");
        canvas.cd(1).region().getAxisFrame().getAxisX().setFixedLimits(0, 10);
        canvas.cd(1).region().getAxisFrame().getAxisY().setFixedLimits(0, 16);
        canvas.repaint();
    }
    
    public static void setStyle(TGCanvas c, String style){
        if(style.compareTo("publication")==0){
            
        }
    }
}
