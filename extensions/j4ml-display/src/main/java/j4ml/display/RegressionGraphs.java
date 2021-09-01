/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.display;

import java.util.ArrayList;
import java.util.List;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;

/**
 *
 * @author gavalian
 */
public class RegressionGraphs {
    
    List<GraphErrors> graphs = new ArrayList<>();
    List<H2F> h2s = new ArrayList<>();
    List<H1F> h1s = new ArrayList<>();
    
    public RegressionGraphs(){        

        h2s.add(new H2F("h2",80,0.0,1.0,80, 0.0,1.0));
        h2s.add(new H2F("h2",80,0.0,1.0,80,-0.5,0.5));
        h1s.add(new H1F("h1",80,-0.2,0.2));
        h1s.add(new H1F("h1",80,-0.2,0.2));
        h1s.add(new H1F("h1",80,-0.2,1.2));                
        
        h1s.get(0).setLineWidth(1);
        h1s.get(1).setLineWidth(1);
        h1s.get(2).setLineWidth(1);
        
        h1s.get(0).setFillColor(4);
        h1s.get(1).setFillColor(5);
        h1s.get(2).setFillColor(3);
        
        for(int i = 0; i < 3; i++){

            GraphErrors graph = new GraphErrors();
            graph.setMarkerColor(2);
            graph.setMarkerSize(4);
            graphs.add(graph);
        }
    }
    
    public void addEvaluation(double real, double inferred){
        
        h2s.get(0).fill(real, inferred);
        h2s.get(1).fill(real, real - inferred);
        
        h1s.get(0).fill(real-inferred);
        h1s.get(1).fill((real-inferred)/real);
        h1s.get(2).fill(real);
        
        //graphs.get(0).addPoint(real, real - inferred, 0.0,0.0);
                
        //graphs.get(1).addPoint(real,  inferred, 0.0,0.0);
    }
    
    public List<H2F>  getH2(){return h2s;}
    public List<H1F>  getH1(){return h1s;}
    
    public H1F getResolution(){        
        return h1s.get(0);
    }
    
    public List<GraphErrors> getGraphs(){return graphs;}
    
}
