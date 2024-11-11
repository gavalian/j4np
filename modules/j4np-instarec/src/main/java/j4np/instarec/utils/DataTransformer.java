/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataTransformer {

    public static class DataRange {
        
        public float rMin = 0.0f;
        public float rMax = 0.0f;
        public float revLength = 0.0f;
        public DataRange(double min, double max){ 
            rMin = (float) min; rMax = (float) max;
            revLength = 1.0f/(rMax-rMin);
        }        
        public float value(float vt){ return (vt-rMin)*revLength;}
        public float transform(float value){
            return value*(rMax-rMin)+rMin;
        }
        @Override
        public String toString(){return String.format("%12.5f , %12.5f", rMin,rMax);}
    }
    
    List<DataRange> transformers = new ArrayList<>();
    
    public DataTransformer(){}
    public void add(DataRange range){ transformers.add(range);}
    public void add(double min, double max){ transformers.add(new DataRange(min,max));}
    public void add(int count, double min, double max){
        for(int i = 0; i < count; i++) this.add(min, max);
    }
    
    public void show(){
        for(int i = 0; i < this.transformers.size(); i++){
            System.out.println(this.transformers.get(i));
        }
    }
    
    public float[] getValue(float[] array){
        if(array.length!=this.transformers.size()){
            System.out.println(" error in data transformers. missmatch in size array = " 
                    + array.length + "  transformers = " + this.transformers.size());
            return null;
        }
        float[] result = new float[array.length];
        for(int i = 0; i < array.length; i++) result[i] = transformers.get(i).value(array[i]);
        return result;
    }
    public float[] transform(float[] array){
        if(array.length!=this.transformers.size()){
            System.out.println(" error in data transformers. missmatch in size array = " 
                    + array.length + "  transformers = " + this.transformers.size());
            return null;
        }
        float[] result = new float[array.length];
        for(int i = 0; i < array.length; i++) result[i] = transformers.get(i).transform(array[i]);
        return result;
    }
    
    public String toArray(){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < this.transformers.size();i++){
            if(i!=0) str.append(",");
            str.append(String.format("%.12f,%.12f",
                    this.transformers.get(i).rMin, this.transformers.get(i).rMax));
        } str.append("]");
        return str.toString();
    }
    
}
