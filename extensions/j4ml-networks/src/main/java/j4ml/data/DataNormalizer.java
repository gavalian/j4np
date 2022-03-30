/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.data;

/**
 *
 * @author gavalian
 */
public class DataNormalizer {
    
    private double[] inputMin = null;
    private double[] inputMax = null;
    
    public DataNormalizer(double[] min, double[] max){
        inputMin = min;
        inputMax = max;
    }
    
    public float normalize(double value, int order){
        double nv = (value-inputMin[order])/(inputMax[order]-inputMin[order]);
        return (float) nv;
    }
    
    public float denormalize(double value, int order){
        double nv = inputMin[order] + value*(inputMax[order]-inputMin[order]);
        return (float) nv;
    }
    
}
