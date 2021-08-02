/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

/**
 *
 * @author gavalian
 */
public class DataPair {
    
    private double[]  first = null;
    private double[] second = null;
    
    public DataPair(int nf, int ns){
        first  = new double[nf];
        second = new double[ns];
    }
    
    public DataPair(double[] af, double[] as){
        first  = af;
        second = as;
    }
    
    public double[] getFirst(){return first;}
    public double[] getSecond(){return second;}    
    
    public void show(){
        if(first!=null)
            System.out.print(DataArrayUtils.getDataString(first) );        
        System.out.print(" => ");
        if(second!=null) System.out.print(DataArrayUtils.getDataString(second));
        System.out.println();
    }
}
