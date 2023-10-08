/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.clustering;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ClusterFinder {
    
    boolean normalize = false;
    double  threshold = 0.5;
    int     rangeSize = 1;
    
    public ClusterFinder(double thr){
        this.threshold = thr;
    }
    public void setNormalize(boolean flag){normalize = flag;}
    
    protected int findMax(float[] array){
        int   index = 0;
        float   max = array[0];
        for(int i = 0; i < array.length;i++){
            if(array[i]>max){ max = array[i]; index = i;}
        }
        return index;
    }

    private void normalize(float[] array){
        int maxBin = this.findMax(array);
        float maxValue = array[maxBin];
        for(int i = 0; i < array.length; i++)
           array[i] = array[i]/maxValue;
    }
    
    public void normalizeLog(float[] array){
        int maxBin = this.findMax(array);
        this.normalize(array);
        for(int i = 0; i < array.length; i++){
            double value = array[i]*100;
            if(value<1.0) value = 1;
            array[i] = (float) value;
        }
        
        //float maxValue = array[maxBin];
        for(int i = 0; i < array.length; i++){
           double value = Math.log(array[i]);
           array[i] = (float) (value/2.0);
        }
        this.normalize(array);
    }
    
    private int getMinimum(int bin){
        int min = bin - rangeSize;
        if(min<0) min =0;
        return min;
    }
    
    private int getMaximum(int bin, int maxSize){
        int min = bin + rangeSize;
        if(min>maxSize) min = maxSize;
        return min;
    }
    
    private double getMean(int bin, float[] array){
        int min = this.getMinimum(bin);
        int max = this.getMaximum(bin,array.length-1);
        double summ = 0.0; double denom = 0.0;
        for(int i = min; i < max; i++){
            summ += array[i]*i; denom += array[i];
        }
        if(denom==0) return 0;
        return summ/denom;
    }
    
    private void clear(int bin, float[] array){
        int min = this.getMinimum(bin);
        int max = this.getMaximum(bin,array.length-1);
        for(int i = min; i <= max; i++) array[i] = 0.0f;
        //System.out.printf(" clearing %5d %5d\n",min,max);
    }
    
    public List<Double> find(float[] array){        
        boolean keep = true;
        List<Double> list = new ArrayList<>();
        if(this.normalize == true) this.normalizeLog(array);
        while(keep==true){
            int bin = findMax(array);
            if(array[bin]<threshold){ 
                keep = false;
            } else {
                double mean = getMean(bin, array);
                this.clear(bin, array);
                list.add(mean);
            }
        }
        return list;
    }
    
    public double[] fromList(List<Double> list){
        double[] array = new double[list.size()];
        for(int i = 0; i < array.length; i++) array[i] = list.get(i);
        return array;
    }
}
