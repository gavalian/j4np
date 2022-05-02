/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

/**
 *
 * @author gavalian
 */
public class Range {
    
    private double rangeMin = 0.0; 
    private double rangeMax = 1.0;
    
    public Range() {}
    
    public Range(double min, double max) {
        this.set(min, max);
    }
    
    public final void set(double min, double max){
        if(min<max){
            rangeMin = min;
            rangeMax = max;
        } else {
            rangeMin = max;
            rangeMax = min;
        }
    }
    
    public boolean contains(double value){
        return (value>=rangeMin&&value<=rangeMax);
    }
    
    public double length(){ return (rangeMax - rangeMin);}
    
    public double min(){ return rangeMin;}
    public double max(){ return rangeMax;}
    
}
