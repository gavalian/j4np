/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.math;

import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;
import twig.data.TDirectory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class PeakFinder {
    
    protected  H1F    reference = null;
    protected  H1F      derived = null;
    protected  Func1D   pdfFunc = null;
    
    protected  List<Func1D>        fittedFunc = new ArrayList<>();
    protected  List<Func1D> fittedFuncGenetic = new ArrayList<>();
    public int nEpochs = 100;
    public int nPopulation = 5000;
    public double mutateFraction = 0.45;
    private double rangeMin = 0.0;
    private double rangeMax = 0.0;
    
    private String[]  background = new String[]{
        "[p0]","[p0]+[p1]*x","[p0]+[p1]*x+[p2]*x*x", 
        "[p0]+[p1]*x+[p2]*x*x+[p3]*x*x*x"};
    
    public PeakFinder(){}
    public PeakFinder(H1F hr){ 
        reference = hr;
        rangeMin = reference.getAxisX().min();
        rangeMax = reference.getAxisX().max();
        this.createDerived();
    }
    
    public PeakFinder(H1F hr, double min, double max){ 
        reference = hr;
        this.setRange(min, max);
        this.createDerived();
    }
    
    public H1F getDerived(){return this.derived;}
    
    public List<Func1D> getFittedFunctions(){ return this.fittedFunc;}
    public List<Func1D> getGeneticFunctions(){ return this.fittedFunc;}
    
    public final PeakFinder setRange(double min, double max){
        double scale = this.reference.getAxisX().max()
                -this.reference.getAxisX().min();
        this.rangeMin = (min - this.reference.getAxisX().min())/scale;
        this.rangeMax = (max - this.reference.getAxisX().min())/scale;
        return this;
    }
    
    protected final void createDerived(){
        derived = new H1F(reference.getName()+"::derived",
                reference.getAxisX().getNBins(), 0.0, 1.0);
        for(int k = 0; k < reference.getAxisX().getNBins(); k++)
            derived.setBinContent(k, reference.getBinContent(k));
        double max = reference.getMax();
        derived.normalize(max);
    }
    
    public Func1D  getPdf(){ return this.pdfFunc;}
    
    private F1D createFunction(int order, double min, double max){
        F1D func = new F1D("peak_"+order,
                "[n"+order+"]*gaus(x,[m"+order+"],[s"+order+"])",
                min,max);
        func.parameter(0).setLabel("N_"+order);
        func.parameter(1).setLabel("#mu_"+order);
        func.parameter(2).setLabel("#sigma_"+order);
        
        for(int k = 0; k < func.getNPars(); k++){
            func.setParameter(k, 0.5);
            func.setParLimits(k, 0.0, 1.0);
        }
        return func;
    }
    
    private Func1D createFinderPDF(int backOrder, int nPeaks){
        PDF1D pdf = new PDF1D("peakFinder", rangeMin, rangeMax);
        List<Func1D> funcList = new ArrayList<>();
        
        F1D   bkgfunc = new F1D("background",background[backOrder],rangeMin, rangeMax);
        
        for(int i = 0; i < bkgfunc.getNPars(); i++){
            bkgfunc.setParameter(i, 0.5);
            bkgfunc.setParLimits(i,-5.0,5.0);            
        }
        
        funcList.add(bkgfunc);
        //pdf.add(new F1D("background",background[backOrder],rangeMin, rangeMax));
        //pdf.add(bkgfunc);
        
        for(int i = 0; i < nPeaks; i++){
            funcList.add(createFunction(i,rangeMin,rangeMax));
        }
        
        pdf.add(funcList);
        return pdf;
    }
    
    public void fit(int backOrder, int nPeaks){
        
        GeneticDataFitter gfitter = new GeneticDataFitter();
        gfitter.fraction(mutateFraction).population(nPopulation).epochs(nEpochs);
        
        
        pdfFunc = this.createFinderPDF(backOrder, nPeaks);
        for(int i = 0; i < pdfFunc.getNPars(); i++){
            pdfFunc.setParameter(i, 0.5);
            pdfFunc.setParLimits(i, -5.0, 5.0);
        }
        pdfFunc.setParLimits(backOrder+1, 0.0, 1.0);
        pdfFunc.setParLimits(backOrder+2, 0.0, 1.0);
        pdfFunc.setParLimits(backOrder+3, 0.0, 1.0);
        //fitter.fit(population, derived);
        //pdfFunc.fit(derived);
        gfitter.fit(pdfFunc, derived);
        
        double scale = reference.getAxisX().max()-reference.getAxisX().min();
        double min = reference.getAxisX().min() + this.rangeMin*(scale);
        double max = reference.getAxisX().min() + this.rangeMax*(scale);

        F1D func = new F1D("fit("+reference.getName()+")","[amp]*gaus(x,[mean],[sigma])",min,max);        
        func.setParameter(0, reference.getMax()*pdfFunc.getParameter(backOrder+1));
        func.setParameter(1, reference.getAxisX().min()+scale*pdfFunc.getParameter(backOrder+2));
        func.setParameter(2, scale*pdfFunc.getParameter(backOrder+3));
        func.attr().set("lc=5,lw=2,ls=3");
        System.out.println("SCALE = " + scale + " MAX = " + reference.getMax());
        
        double width  = this.reference.getAxisX().getBinWidth(0);
        double number = func.getParameter(0)*func.getParameter(2)/Math.sqrt(2*3.1415)/width;
        
        func.attr().setLegend(String.format("fit(%s) [%d]", reference.getName(),(int) number));
        func.show();

        this.fittedFunc.clear();
        this.fittedFunc.add(func);
        
        this.fittedFuncGenetic.clear();
        this.fittedFuncGenetic.addAll(((PDF1D ) this.pdfFunc).funcList);
        pdfFunc.show();
    }
    
    public static void main(String[] args){
        TDirectory dir = new TDirectory("rgb_m2212.twig");
        dir.show();
        H1F h = (H1F) dir.get("/rgb/conv");
        
        PeakFinder pf = new PeakFinder(h);
        pf.createDerived();
        pf.nEpochs = 25;
        TGCanvas c = new TGCanvas();
        c.draw(pf.derived,"EP");
        
        pf.setRange(0.8, 1.2);
        pf.fit(2, 1);
        
        c.draw(pf.getPdf(),"same");
        
        
        TGCanvas c2 = new TGCanvas();
        c2.draw(h,"EP");
        c2.draw(pf.fittedFunc.get(0),"same");
        
         TGCanvas c3 = new TGCanvas();
         c3.draw(h,"EP");
         for(Func1D f : pf.getGeneticFunctions()) c3.draw(f,"same");
    }
}
