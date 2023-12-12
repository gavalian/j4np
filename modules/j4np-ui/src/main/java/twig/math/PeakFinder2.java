/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.data.H1F;
import twig.data.TDirectory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class PeakFinder2 {
    
    H1F reference = null;
    H1F derived   = null;
    
    PDF1D pdftrue = null;
    
    public int nEpochs = 100;
    public int nPopulation = 5000;
    public double mutateFraction = 0.45;
    
    private String[]  background = new String[]{
        "[p0]","[p0]+[p1]*x","[p0]+[p1]*x+[p2]*x*x", 
        "[p0]+[p1]*x+[p2]*x*x+[p3]*x*x*x"};
    
    public PeakFinder2(H1F ref){
        reference = ref; this.createDerived();
    }
    
    protected final void createDerived(){
        derived = new H1F(reference.getName()+"::derived",
                reference.getAxisX().getNBins(), 0.0, 1.0);
        for(int k = 0; k < reference.getAxisX().getNBins(); k++)
            derived.setBinContent(k, reference.getBinContent(k));
        double max = reference.getMax();
        derived.normalize(max);
    }    
    
    
    public PDF1D getFitterPdf(){
        return this.pdftrue;
    }
    
    public PDF1D cratePdf(int backOrder, int peaks, double min, double max){
        
        double xscale = this.reference.getAxisX().max()-this.reference.getAxisX().min();
        double   cmin = (min-reference.getAxisX().min())/xscale;
        double   cmax = (max-reference.getAxisX().min())/xscale;
        
        PDF1D pdf = new PDF1D("pdf",cmin,cmax);
        
        List<Func1D> list = new ArrayList<>();
        F1D back = new F1D("back",background[backOrder],min,max);
        for(int i = 0; i < back.getNPars();i++){
            back.setParameter(i, 0.5);
            back.setParLimits(i, -15, 15);
        }
        
        list.add(back);
        for(int i = 0; i < peaks; i++){
            F1D peak = new F1D("peak","[amp]*gaus(x,[mean],[sigma])",min,max);
            peak.parameter(0).setLabel("N");
            peak.parameter(1).setLabel("#mu");
            peak.parameter(2).setLabel("#sigma");
            for(int p = 0; p < peak.getNPars(); p++){
                peak.setParameter(p, 0.5);
                peak.setParLimits(p, 0.0, 1.0);
            }
            list.add(peak);
        }        
        pdf.add(list);
        return pdf;
    }
    
    public final void fit(int backOrder, int peaks){
        
    }
    
    public final void fit(PDF1D pdf){
        
        GeneticDataFitter gfitter = new GeneticDataFitter();
        gfitter.fraction(mutateFraction).population(nPopulation).epochs(nEpochs);
        
        gfitter.fit(pdf, derived);
        
        double xscale   = this.reference.getAxisX().max()-this.reference.getAxisX().min();
        double rangeMin = xscale*pdf.getMin()+this.reference.getAxisX().min();
        double rangeMax = xscale*pdf.getMax()+this.reference.getAxisX().min();
        double scaleX   = this.reference.getAxisX().max()
                - this.reference.getAxisX().min();
        
        pdftrue = new PDF1D(
                "fitted",rangeMin,rangeMax
        );
        
        List<Func1D> funcs = new ArrayList<>();
        for(Func1D f : pdf.funcList){
            if(f.getName().contains("back")==true){
                F1D func = new F1D("back-fitted",f.getExpression(),
                        rangeMin, rangeMax);
                
                for(int i = 0; i < f.getNPars(); i++)
                    func.setParameter(i, f.getParameter(i));
                
                
                func.setScaleY(this.reference.getMax());
                //func.setScaleY(4000);
                func.setScaleX(1./scaleX);
                func.setShiftX(this.reference.getAxisX().min());
                
                funcs.add(func);
                
            } 
            if(f.getName().contains("peak")==true){
                F1D func = new F1D("peak-fitted",f.getExpression(),
                        rangeMin, rangeMax);
                func.setParameter(0, f.getParameter(0)*reference.getMax());
                func.setParameter(1, f.getParameter(1)*xscale+reference.getAxisX().min());
                func.setParameter(2, f.getParameter(2)*xscale);
                
                func.parameter(0).setLabel("N");
                func.parameter(1).setLabel("#mu");
                func.parameter(2).setLabel("#sigma");
                
                funcs.add(func);
            }
            
        }
        
        
        for(Func1D f : funcs){
            System.out.printf(" after listing scale Y = : %f\n", ((F1D) f).getScaleY());
        }
        pdftrue.add(funcs);
        System.out.println("  maximum = " + reference.getMax());
    }
    
    public H1F derived(){return derived;}
    
    public static void main(String[] args){
        TDirectory dir = new TDirectory("rgb_m2212.twig");
        dir.show();
        H1F h = (H1F) dir.get("/rgb/conv");
        
        PeakFinder2 pf = new PeakFinder2(h);
        /*
        PDF1D pdf = new PDF1D("pdf",0.1,0.9);
        F1D b = new F1D("back","[p0]+[p1]*exp([p2]*x)",0.2,0.6);
        F1D g = new F1D("peak","[amp]*gaus(x,[mean],[sigma])",0.2,0.6);
        b.setParameters(0.5,0.5,0.5);
        b.setParLimits(0, -5, 5);
        b.setParLimits(1, -5, 5);
        b.setParLimits(2, -5, 5);
        
        g.setParameters(0.5,0.5,0.5);
        g.setParLimits(0, 0.0, 1);
        g.setParLimits(1, 0.0, 1);
        g.setParLimits(2, 0.0, 1);
        
        pdf.add(Arrays.asList(b,g));*/
        
        PDF1D pdf = pf.cratePdf(3, 1, 0.7, 1.25);
        pdf.show();
            
        pf.fit(pdf);
        
        
        pf.getFitterPdf().show();
        TGCanvas c = new TGCanvas(800,400);
        
        System.out.printf("SCALE Y = %f\n",( (F1D) pf.getFitterPdf().list().get(0)).scaleY);
        c.view().divide(3, 1);
        h.attr().set("fc=-1");
        
        c.cd(0).draw(pf.derived()).draw(pdf,"same");
        c.cd(1).draw(h,"EP").draw(pf.getFitterPdf(),"same");        
        c.cd(2).draw(pf.getFitterPdf().list().get(0),"same");
        
        /*
        TGCanvas c = new TGCanvas();
        F1D f = new F1D("f","[amp]*gaus(x,[mean],[sigma])",1,4);
        f.setParameters(1,0.4,0.02);
        //f.setScaleY(4);
        f.setScaleX(1./3);
        f.setShiftX(1);
        c.draw(f);*/
    }
}
