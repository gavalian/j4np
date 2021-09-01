/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TDataFactory {
    
    public static GraphErrors createGraph(int color, int level){
        GraphErrors graph = new GraphErrors();
        graph.addPoint(1.0, 3.0 + level*2.2 , 0.0, 0.8);
        graph.addPoint(2.0, 2.0 + level*2.2 , 0.0, 0.6);
        graph.addPoint(3.0, 1.5 + level*2.2 , 0.0, 0.3);
        graph.addPoint(4.0, 1.2 + level*2.2 , 0.0, 0.4);
        graph.addPoint(5.0, 1.0 + level*2.2 , 0.0, 0.5);
        return graph;
    }
    
    public static List<GraphErrors> createGraphColors(int size){
        List<GraphErrors> graphs = new ArrayList<>();
        for(int i = 0; i < size; i++){
            GraphErrors gr = TDataFactory.createGraph(i, i+1);
            gr.attr().setMarkerColor(i+1);
            gr.attr().setMarkerStyle(i+1);
            gr.attr().setLineColor(i+1);
            gr.attr().setMarkerSize(10);            
        }
        return graphs;
    }
    
}
