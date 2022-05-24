/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.cvt;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class to store the matrix that represents Central
 * Detector hits.
 * @author gavalian
 */
public class CvtArray2D {
    
    private float[] array = new float[90*256];
    public CvtArray2D(){}
    
    public int  getIndex(int x, int y){
        return (256*y + x);
    }
    
    public  void set(int index, float value){array[index] = value;}
    public  void set(int x, int y, float value){ set(getIndex(x,y),value);}
    public float get(int x, int y) { return array[getIndex(x,y)];}
    
    
    public int countLines(int start, int end){
        int counter = 0;
        for(int line = start; line < end; line++){
            int hits = countInLine(line);
            if(hits>0) counter++;
        }
        return counter;
    }
    
    public int countInLine(int line){
        int counter = 0;
        for(int x = 0; x < 256; x++) 
            if(get(x,line)>0) counter++;
        return counter;
    }
    
    public float[] getOutput(){
        float[] output = new float[84];
        for(int i = 0; i < 84; i++){
            if(countInLine(i)>0){
                output[i] = 1.0f;
            } else output[i] = 0.0f;
        }
        return output;
    }
    
    public String getInputLSVM(){
        return CvtArrayIO.arrayToLSVM(array, 0.5);
    }
    
    public int countCentral(){
        int counter = 0; 
        for(int line = 0; line < 84; line++)
            if(countInLine(line)>0) counter++;
        return counter;
    }
    
    public int countZ(){
        int counter = 0; 
        for(int line = 84; line < 87; line++)
            if(countInLine(line)>0) counter++;
        return counter;
    }
    
    public int countC(){
        int counter = 0; 
        for(int line = 87; line < 90; line++)
            if(countInLine(line)>0) counter++;
        return counter;
    }
    
    public int getType(){
        
        int central = countCentral();
        int zc      = countZ();
        int cc      = countC();
        
        if(zc>=1&&cc>=1&&central==6) return 1;
        if(zc>=1&&cc>=1&&central==4) return 2;
        if(zc<1&&central==6) return 3;
        //if(central==6&&zc==0&&cc==0) return 2;
        //if(central==6&&zc==0) return 3;
        //if(central==6&&cc==0) return 4;
        
        //if(zc>=1&&cc>=1&&central==4) return 11;
        //if(central==4&&zc==0&&cc==0) return 12;
        //if(central==4&&zc==0) return 13;
        //if(central==4&&cc==0) return 14;

        if(zc>=1&&cc>=1&&central==2) return 21;
        if(central==2&&zc==0&&cc==0) return 22;
        if(central==2&&zc==0) return 23;
        if(central==2&&cc==0) return 24;
        
        if(central==0&&zc==3) return 25;
        
        return 0;
    }
    
    public String lineToString(int line){
        StringBuilder str = new StringBuilder();
        for(int x = 0; x < 256; x++){
            if(get(x,line)>0) str.append("X"); else str.append("-");
        }
        return str.toString();
    }
    
    public void show(){
        for(int y = 0; y < 90; y ++){
            System.out.println(lineToString(y));
        }
    }
    
    public List<Integer> getActiveList(float[] output, double threshold){
        List<Integer> index = new ArrayList<>();
        for(int i = 0; i < output.length; i++){ if(output[i]>threshold) index.add(i); }
        return index;
    }
    
}
