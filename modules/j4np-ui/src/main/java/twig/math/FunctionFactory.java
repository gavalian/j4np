/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.math;

import java.util.LinkedHashMap;
import java.util.Map;
import twig.data.H1F;


/**
 *
 * @author gavalian
 */
public class FunctionFactory {
    
    public static Map<String,Func1D>  funcionDesk = new LinkedHashMap<String,Func1D>();
    public static double PI_OVER_2 = Math.PI / 2.0;
    
    
    /**
     * Fast acos using 8 term polynomial approximation from Abramowitz and Stegun, pg. 81.
     *
     * <p>Note: Accuracy to within 3 x 10^-8 radians.</p>
     *
     * @param   x
     * @return  arccos(x)
     */
    public static double acos( double x )
    {
        return PI_OVER_2 - asin( x );
    }
    
    
    /**
     * Fast asin using 8 term polynomial approximation from Abramowitz and Stegun, pg. 81.
     *
     * <p>Note: Accuracy to within 3 x 10^-8 radians.</p>
     *
     * @param   x
     * @return  arcsin(x)
     */
    public static double asin( double x )
    {
        boolean isNeg = x < 0;
        x = Math.abs( x );

        double y1 = x * ( -.0170881256 + ( x * ( .0066700901 + ( x * -.0012624911 ) ) ) );
        double y2 = x * ( -.0501743046 + ( x * ( .0308918810 + y1 ) ) );
        double y = 1.5707963050 + ( x * ( -.2145988016 + ( x * ( .0889789874 + y2 ) ) ) );
        double theta = PI_OVER_2 - ( Math.sqrt( 1.0 - x ) * y );

        if ( isNeg )
        {
            theta = -theta;
        }

        return theta;
    }
     /**
     * Fast asin using 4 term polynomial approximation from Abramowitz and Stegun, pg. 81.
     *
     * <p>Note: Accuracy to within 7 x 10^-5 radians.</p>
     *
     * @param   x
     * @return  arcsin(x)
     */
    public static double asin_4( double x )
    {
        boolean isNeg = x < 0;
        x = Math.abs( x );

        double y = 1.5707288 + ( x * ( -.2121144 + ( x * ( .0742610 + ( x * -.0187293 ) ) ) ) );
        double theta = PI_OVER_2 - ( Math.sqrt( 1.0 - x ) * y );

        if ( isNeg )
        {
            theta = -theta;
        }

        return theta;
    }
    
    public static void registerFunction(Func1D func){
        if(FunctionFactory.funcionDesk.containsKey(func.getName())==true){
            System.out.println("[FunctionFactory] warning : ---> replacing function with name = "
            + func.getName());
        }
        FunctionFactory.funcionDesk.put(func.getName(), func);
    }
    
     public static double atan( double x )
    {
    	double h = Math.sqrt( x * x + 1 );
    	
    	return asin( x / h );
    }
    
    public static double atan2( double y, double x )
    {
    	if ( x > 0 )
    	{
    		return atan( y / x);
    	}
    	else if ( x < 0 )
    	{
    		if ( y >= 0 )
    		{
    			return Math.PI + atan( y / x );
    		}
    		else
    		{
    			return -Math.PI + atan( y / x );
    		}
    	}
    	else
    	{
    		if ( y > 0 )
    		{
    			return Math.PI / 2;
    		}
    		else if ( y < 0 )
    		{
    			return -Math.PI / 2;
    		}
    		else
    		{
    			return 0;  // undefined
    		}
    	}
    }
    public static double  gauss(double x, double mean, double sigma){
        return Math.exp(-(x-mean)*(x-mean)/(2.0*sigma*sigma));
    }
    
    public static double getRandMinMax(double min, double max){
        return min + (max-min)*Math.random();
    }
    
    public static double landau(double x, double mean, double sigma){
        double lambda = (x-mean)/sigma;
        return Math.exp(-0.5*(lambda+Math.exp(-lambda)));
    }
    
    public static Func1D  createFunction(int type){
        if(type == 0){
            F1D func3 = new F1D("func3","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0.0,5.4);
            func3.setParameters(new double[]{15,5,120,2.4,0.45});
            return func3;
        }
        
        if(type == 1){
            F1D func3 = new F1D("func3","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])+[amp2]*gaus(x,[mean2],[sigma2])",0.0,5.4);
            func3.setParameters(new double[]{15,5,120,1.8,0.25, 80,3.2,0.45});
            return func3;
        }
        return null;
    }
    
    public static void main(String[] args){

       // int n_iter = 10000000;
        int n_iter = 1000;
        
        Long start_time = System.currentTimeMillis();
        for(int i = 0; i < n_iter; i++){
            double num  = Math.random()*2.0-1.0;
            double num2  = Math.random()*2.0-1.0;
//            double acos = Math.acos(num);
            double atan = Math.atan2(num,num2);
        }
        Long end_time = System.currentTimeMillis();
        double   time = ((double) (end_time - start_time ))/n_iter;
        System.out.println(String.format("Math  :   %9.5f msec/call",time*1000.0 ));
        start_time = System.currentTimeMillis();
        for(int i = 0; i < n_iter; i++){
            double num  = Math.random()*2.0-1.0;
            double num2  = Math.random()*2.0-1.0;
            //double acos = FunctionFactory.acos(num);
            double atan = FunctionFactory.atan2(num,num2);
        }
        end_time = System.currentTimeMillis();
        double timeF = ((double) (end_time - start_time ))/n_iter;
        System.out.println(String.format("Func  :   %9.5f msec/call",timeF*1000.0 ));
        System.out.println(String.format("Speed :   %9.5f", time/timeF));
        double divergence = 0.0;
        for(int i = 0; i < n_iter; i++){
            double num  = Math.random()*2.0-1.0;
            //double acosF = FunctionFactory.acos(num);
            //double acosM = Math.acos(num);
            double acosF = FunctionFactory.atan(num);
            double acosM = Math.atan(num);
            divergence += Math.abs(acosF-acosM);
        }
        System.out.println(String.format("Divergence : %.8f", divergence));
    }
    
    
    public static double significance(H1F data, Func1D back, Func1D signal){
        double  LpB = 0.0;
        double LpSB = 0.0;
        int nbins = data.getAxis().getNBins();
        for(int i = 0; i < nbins; i++){
            double x = data.getAxis().getBinCenter(i);
            if(x>=back.getMin()&&x<=back.getMax()){
                double ni = data.getBinContent(i);
                double bi = back.evaluate(x);
                double si = signal.evaluate(x);
                LpSB += 2*((si+bi) - ni + ni*Math.log(ni/(si+bi)));
                LpB  += bi - ni + ni*Math.log(ni/bi);
            }
        }
        System.out.printf("LpB = %f, LpSB = %f\n",LpB,LpSB);
        return 1.0;
    }
    
    public static double significance(H1F data, Func1D back, Func1D signal,
            double min, double max){
        double  LpB = 0.0;
        double LpSB = 0.0;
        int nbins = data.getAxis().getNBins();
        for(int i = 0; i < nbins; i++){
            double x = data.getAxis().getBinCenter(i);
            if(x>=min&&x<=max){
                double ni = data.getBinContent(i);
                double bi = back.evaluate(x);
                double si = signal.evaluate(x);
                LpSB += 2*((si+bi) - ni + ni*Math.log(ni/(si+bi)));
                LpB  += bi - ni + ni*Math.log(ni/bi);
            }
        }

        double sig = Math.sqrt(2*(LpB/LpSB));
        System.out.printf("LpB = %f, LpSB = %f, sig = %f\n",
                LpB,LpSB,sig);
        return sig;
    }
    
    
    public static double significance(H1F data, Func1D total, Func1D back, 
            Func1D signal, double min, double max){
        double  LpB = 0.0;
        double LpSB = 0.0;
        int nbins = data.getAxis().getNBins();
        for(int i = 0; i < nbins; i++){
            double x = data.getAxis().getBinCenter(i);
            if(x>=min&&x<=max){
                double ni = data.getBinContent(i);
                double bi = back.evaluate(x);
                double si = signal.evaluate(x);
                double ti = total.evaluate(x);
                
                LpSB += 2*(ti - ni + ni*Math.log(ni/ti));
                LpB  += bi - ni + ni*Math.log(ni/bi);
            }
        }

        double sig = Math.sqrt(2*(LpB/LpSB));
        System.out.printf("LpB = %f, LpSB = %f, sig = %f\n",
                LpB,LpSB,sig);
        return sig;
    }
}
