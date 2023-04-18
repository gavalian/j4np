/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.util.Arrays;
import java.util.Random;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.math.F1D;
import twig.math.GeneticDataFitter;
import twig.math.PDF1D;

/**
 *
 * @author gavalian
 */
public class GeneticFitterStudy {
    
    
    public String study(double mean, double sigma){
        H1F    h = TDataFactory.createH1F(22000, 120, 0.0, 1.0, mean, sigma);
        h.unit();
        F1D func = new F1D("func","[a]+[b]*gaus(x,[c],[d])",0.1,0.9);
        func.setParameters(0.5,0.5,0.5,0.1);
        for(int i = 0; i < func.getNPars(); i++) func.setParLimits(i, 0, 2.0);
        func.fit(h,"NQ");
        
        GeneticDataFitter genfit = new GeneticDataFitter();
        /*genfit.fit(h, 0.1, 0.9);
        
        return String.format("%.5f,%.5f,%.5f,%.5f,%.5f,%.5f\n",mean,sigma,
                func.getParameter(2),func.getParameter(3),
                genfit.gfunc.function.getParameter(2),
                genfit.gfunc.function.getParameter(3)
                );*/
        return "";
    }
    
    
    public static void study(){
        GeneticFitterStudy study = new GeneticFitterStudy();
        
        Random r = new Random();
        double[] sigmas = new double[]{0.2,0.1,0.05,0.02,0.01};
        for(int k = 0; k < sigmas.length; k++){
            for(int i = 0; i < 50; i++){
                double mean = r.nextDouble()*0.6 + 0.2;
                String  result = study.study(mean, sigmas[k]);
                System.out.printf("%d,%s",k,result);
            }
        }
    }
    public static void main(String[] args){
        
        H1F h = TDataFactory.createH1F(8200, 240, 0.0, 1.0, 0.25, 0.05);
        TGCanvas c = new TGCanvas(400,400);
        h.unit();
        h.attr().setLineColor(8);
        
        c.draw(h);
        
        F1D fb = new F1D("fb","[p0]",0.05,0.5);
        F1D fs = new F1D("fb","[a]*gaus(x,[m],[s])",0.05,0.5);
                
        PDF1D pdf = new PDF1D("pdf",0.05,0.5);
        
        pdf.add(Arrays.asList(fb,fs));
        for(int i = 0; i < pdf.getNPars(); i++){
            pdf.setParameter(i, 0.5);
            pdf.setParLimits(i, -5, 5);

        }
        
        pdf.show();
        
        GeneticDataFitter gf = new GeneticDataFitter();
        gf.fraction(0.45).population(7000);
        
        gf.fit(pdf, h);
        
        c.draw(pdf,"same");
    }
}
