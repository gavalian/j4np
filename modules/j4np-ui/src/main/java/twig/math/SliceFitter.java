/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.math;

import java.util.ArrayList;
import java.util.List;
import twig.data.DataVector;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public class SliceFitter {
    
    private List<SliceFitResult> results = new ArrayList<>();
    private DataVector           fitAxis = new DataVector();

    private int         threshold = 120; // minimum number of entries
    
    public SliceFitter(){
        
    }
    
    public void fitX(H2F h2){        
       fitAxis.clear();
       results.clear();
       int nBins = h2.getXAxis().getNBins();       
              
       for(int i = 0; i < nBins; i++){
           H1F h = h2.sliceX(i);
           if(h.integral()>threshold){
               double xp = h2.getXAxis().getBinCenter(i);
               SliceFitResult res = new SliceFitResult();
               res.fit(h);
               res.function.attr().setTitleX(String.format("f%d",i+1));
               res.function.attr().setLegend(String.format("fit slice %d/%d", 
                       i+1,nBins));
               fitAxis.add(xp);
               results.add(res);
           }
       }
       System.out.printf(">>>> slice fitter fraction %d/%d\n",results.size(),nBins);
    } 
    
    public GraphErrors getGraph(int parameter){
        GraphErrors gr = new GraphErrors();
        for(int s = 0; s < fitAxis.getSize(); s++){
            gr.addPoint(fitAxis.getValue(s), 
                    results.get(s).function.parameter(parameter).value(), 
                    0.0,
                    results.get(s).function.parameter(parameter).error());
        }
        return gr;
    }
    
    public List<SliceFitResult> getResults(){ return this.results;}
    
    public void show(TGDataCanvas c){
        c.cd(0);
        for(int i = 0; i < results.size(); i++){
            c.region().draw(results.get(i).histogram)
                    .draw(results.get(i).function,"same");
            c.next();
        }
    }
}
