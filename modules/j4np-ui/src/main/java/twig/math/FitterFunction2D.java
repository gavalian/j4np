/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.math;

import org.freehep.math.minuit.FCNBase;
import twig.data.DataSet;
import twig.data.H2F;


/**
 *
 * @author gavalian
 */
public class FitterFunction2D  implements FCNBase {
    private Func2D    function = null;
    private DataSet  dataset  = null;
    private H2F    datasetH2F  = null;
    private String    fitOptions = "";
    private int       numberOfCalls = 0;
    private long      startTime     = 0L;
    private long      endTime       = 0L;

    
    public FitterFunction2D(Func2D func, DataSet data,String options){
        dataset    = data;
        function   = func;
        fitOptions = options;
        if(dataset instanceof H2F){
            datasetH2F = (H2F) dataset;
        } else {
            System.out.println(" ERROR : data set has to be H2F");
        }
    }

    public Func2D getFunction(){return function;}

    @Override
    public double valueOf(double[] pars) {
        double chi2 = 0.0;
        function.setParameters(pars);        
        chi2 = getChi2(pars,fitOptions);
        
        numberOfCalls++;
        this.function.setChiSquare(chi2);
        endTime = System.currentTimeMillis();
        /*
        if(numberOfCalls%10==0){
            System.out.println("********************************************************");
            System.out.println( " Number of calls =  " + numberOfCalls + " CHI 2 " + chi2);
            function.show();
        }*/
        //function.show();
        /*for(int i = 0; i < pars.length; i++) System.out.printf("%9.5f ",pars[i]);
        System.out.println();
        System.err.println("\n************ CHI 2 = " + chi2);*/
        return chi2;
    }
    
    public double getChi2(double[] pars, String options){        
        double chi2 = 0.0;
        int npointsX = datasetH2F.getXAxis().getNBins();
        int npointsY = datasetH2F.getYAxis().getNBins();
        function.setParameters(pars);
        int ndf = 0;        
        for(int npX = 0; npX < npointsX; npX++){
            for(int npY = 0; npY < npointsY; npY++){
                double x = datasetH2F.getXAxis().getBinCenter(npX);//                
                double y = datasetH2F.getYAxis().getBinCenter(npY);
                double z = datasetH2F.getBinContent(npX, npY);
                double zerr =  Math.sqrt(Math.abs(z));
                boolean usePoint = true;
                
                if(function.inRange(x, y)==true){
                    
                    double zv = function.evaluate(x,y);
                    double normalization = zerr*zerr;            
                    //System.out.printf("z = %9.5f, zv = %9.5f\n",z,zv);
                    if(options.contains("N")==true){
                        normalization = Math.abs(z);
                    }
                
                    if(options.contains("W")==true){
                        normalization = 1.0;
                    }
                
                    if(Math.abs(normalization)>0.000000000001&&z>0.00000000001){
                        chi2 += (zv-z)*(zv-z)/normalization;
                        ndf++;
                    }
                }
            }
        }
        int npars = function.getNPars();
        this.function.setNDF(ndf-npars);
        return chi2;
    }
}
