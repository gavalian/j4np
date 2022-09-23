/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.common;

import j4np.utils.io.DataArrayUtils;

/**
 *
 * @author gavalian
 */
public class DriftMatrix {
    
    double[] matrix = new double[6*112];
    
    public void fill(int layer, int wire){
        int row = (layer-1)/6;
        double value = matrix[row*112+(wire-1)];
        int index = (wire-1)*6 + row;
        //matrix[row*112+(wire-1)] = value + 1.0;
         matrix[index] = value + 1.0;
        //System.out.println(" row = " + row + " value = " + value);
    }
    
    public void normalize(){
        for(int i = 0; i < matrix.length; i++){
            matrix[i] = matrix[i]/6.0;
        }
    }
    
    public void show(){
        int icount=0;
        System.out.println();
        for(int i = 0; i < 6; i++){
            for(int k = 0; k < 112; k++){
                if(matrix[icount]<0.0001) System.out.print(".");
                else System.out.print("X");
                icount++;
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public int getIndex(int layer, int wire){
        int row = (layer-1)/6;
        return row*112+(wire-1);
    }
    
    public double getValue(int index){ return matrix[index];}
    
    public String toCSV(){
        return DataArrayUtils.doubleToString(matrix, ",");
    }
    
    public String toLSVM(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < matrix.length; i++){
            if(matrix[i]>0.0) str.append(String.format(" %d:%.4f", i+1,matrix[i]));            
        }
        return str.toString();
    }
}
