/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import java.util.ArrayList;
import java.util.List;
import twig.data.GraphErrors;
import twig.data.TDataFactory;
import twig.math.F1D;

/**
 *
 * @author gavalian
 */
public class TGStyleFactory {
    
    
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
