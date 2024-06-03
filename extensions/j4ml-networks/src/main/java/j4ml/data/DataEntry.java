/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.data;

import j4np.utils.io.DataArrayUtils;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author gavalian
 */
public class DataEntry {
    
    private float[]       first = null;
    private float[]      second = null;
    private float[]     infered = null;
    private float[]  parameters = null;
    
    
    public DataEntry(){
        first  = null;
        second = null;
    }
    
    public DataEntry(int nf, int ns){
        first  = new float[nf];
        second = new float[ns];
    }
    
    public DataEntry(double[] af, double[] as){
        if(af!=null) first  = DataArrayUtils.toFloat(af);
        if(as!=null) second = DataArrayUtils.toFloat(as);
    }
    
    public DataEntry(float[] af, float[] as){
        first  = af;
        second = as;
    }
    
    public final void  set(double[] af, double[] as){
        first  = DataArrayUtils.toFloat(af);
        second = DataArrayUtils.toFloat(as);
    }
        
    public void setParameters(float[] params){
        parameters = params;
    }
    
    public float[] getParameters(){ return this.parameters;}
    
    public double[] getFirst(){ return first==null? null:DataArrayUtils.toDouble(first);}
    public double[] getSecond(){return second==null? null:DataArrayUtils.toDouble(second);}    
    
    public float[]  floatFirst(){ 
       float[] data = new float[first.length];
       for(int k = 0; k < data.length; k++) data[k] = (float) first[k];
       return data;
    }
    
    public float[]  floatSecond(){ 
       float[] data = new float[second.length];
       for(int k = 0; k < data.length; k++) data[k] = (float) second[k];
       return data;
    }
    
    public final double[] getInferedDouble(){ 
        double[] l = new double[infered.length];
        for(int i = 0; i < l.length;i++) l[i] = infered[i];
        return l;
    }
    
    public final float[] getInfered(){ return infered;}
    
    public final void setInput(float[] data){
        this.first = data;
    }
    
    public final void setOutput(float[] data){
        this.second = data;
    }
    
    public final void setInfered(float[] outinf){
        infered = outinf;
    }
    
    public void transform(int nclasses){
        float[] label = new float[nclasses];
        for(int i = 0; i < label.length; i++)
           label[i] =0.0f;
        int which = (int) second[0];
        if(which<0||which>=label.length){
            System.out.println("ERROR: the label is larger than provided length\n");
            return;
        }
        label[which] = 1.0f;
        this.second = label;
    }
    
    public float[] features(){ return this.first;}
    public float[] labels(){return this.second;}
    
    public double[] featuresDouble(){ return first==null? null:DataArrayUtils.toDouble(first);}
    public double[] labelsDouble(){return second==null? null:DataArrayUtils.toDouble(second);}
    
    public void show(){
        if(first!=null)
            System.out.print(DataArrayUtils.floatToString(first," ") );        
        System.out.print(" => ");
        if(second!=null) System.out.print(DataArrayUtils.floatToString(second," "));
        if(infered!=null) {
            System.out.print(" :::: ");
            System.out.print(DataArrayUtils.floatToString(infered," "));
        }
        System.out.println();
    }
    
    public void swap(Random r, int[] index, double min, double max){
        int     ir = r.nextInt(index.length);
        double  dist = min + r.nextDouble()*(Math.abs(max-min));
        double direction = r.nextDouble();
        double value = this.first[index[ir]];
        
        if(direction<0.5){
           value = value - dist; 
        } else {
            value = value + dist;
        }
        if(value>1.0) value = value - 2*dist;
        if(value<0.0) value = value + 2*dist;
        this.first[index[ir]] = (float) value;
    }
    
    public String toCSVString(){
        StringBuilder str = new StringBuilder();
        str.append(DataArrayUtils.floatToString(first,","));
        str.append(DataArrayUtils.floatToString(second,","));
        if(infered!=null){
            str.append(DataArrayUtils.floatToString(infered,","));
        }
        return str.toString();
    }
    
    public static int getLabelClass(float[] output){
        float maxval = output[0];
        int   maxbin = 0;
        for(int i = 0 ; i < output.length; i++){
            if(output[i]>maxval){ maxval = output[i]; maxbin = i;}
        }
        return maxbin;
    }
    
    public static float[] combine(List<float[]> f){
        int size = 0;
        for(float[] a : f) size += a.length;
        float[] result = new float[size];
        int counter = 0;
        for(int i = 0; i < f.size(); i++){
            for(int l = 0; l < f.get(i).length; l++){
                result[counter] = f.get(i)[l]; counter++;
            }
        }
        return result;
    }
}
