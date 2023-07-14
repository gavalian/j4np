/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.math;

import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class FuncHist1D extends Func1D {
    
    List<H1F> histograms = new ArrayList<>();
    List<Dimension1D> ranges = new ArrayList<>();
    
    public FuncHist1D(String name, double min, double max){
        super(name, min, max);        
    }
    
    public void addHistograms(H1F... histos){
        for(H1F h : histos) { 
            h.unit();
            histograms.add(h); 
        }
        this.redoParameterList();
    }
    
    public void addHistograms(List<H1F> histos){
        for(H1F h : histos) { 
            h.unit();
            histograms.add(h); 
        }
        this.redoParameterList();
    }
    
    public void addHisto(H1F h){
        h.unit();
        histograms.add(h);
        this.redoParameterList();
    }
    
    protected void redoParameterList(){
        this.userPars.clear();
        for(int i = 0; i < histograms.size(); i++){
            UserParameter up = new UserParameter(String.format("h%d", i),0.5);
            up.setLabel(histograms.get(i).getName());
            this.userPars.getParameters().add(up);
        }
    }
    
    @Override
    public double evaluate(double x){
        double agregate = 0.0;
        for(int k = 0; k < histograms.size(); k++){
            if(x>this.getMin()&&x<this.getMax()){
                H1F h = histograms.get(k);
                int bin = h.getAxisX().getBin(x);
                if(bin>=0){
                    agregate += h.getBinContent(bin)*getParameter(k);
                }
            }
        }
        return agregate;
    }
    
    public static FuncHist1D fromFile(String filename, double min, double max, String... data){
        TDirectory dir = new TDirectory(filename);
        List<H1F> list = new ArrayList<>();
        
        for(String item : data) list.add((H1F) dir.get(item));
        
        FuncHist1D fh = new FuncHist1D("funchist",min,max);
        fh.addHistograms(list);
        return fh;
    }
}
