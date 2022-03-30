/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.data;

import j4np.utils.io.DataArrayUtils;

/**
 *
 * @author gavalian
 */
public class DataEntry {
    
    private float[]   first = null;
    private float[]  second = null;
    private float[] infered = null;
    
    public DataEntry(int nf, int ns){
        first  = new float[nf];
        second = new float[ns];
    }
    
    public DataEntry(double[] af, double[] as){
        if(af!=null) first  = DataArrayUtils.toFloat(af);
        if(as!=null) second = DataArrayUtils.toFloat(as);
    }
    
    public final void  set(double[] af, double[] as){
        first  = DataArrayUtils.toFloat(af);
        second = DataArrayUtils.toFloat(as);
    }
        
    
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
    
    public void show(){
        if(first!=null)
            System.out.print(DataArrayUtils.floatToString(first," ") );        
        System.out.print(" => ");
        if(second!=null) System.out.print(DataArrayUtils.floatToString(second," "));
        System.out.println();
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
}
