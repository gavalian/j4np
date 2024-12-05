/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class PieChart {
    
    List<DataVector> chartVectors = new ArrayList<>();
    List<int[]>       chartColors = new ArrayList<>();
    
    //public double[]   chartData = null;
    //public int[]    chartColors = null;
    
    public PieChart(double[] data){
        DataVector vec = new DataVector(data);
        chartVectors.add(vec);
        int[] colors = new int[data.length];
        for(int i = 0; i < data.length; i++) colors[i] = 2+i;
        chartColors.add(colors);
    }
    
    public PieChart setColors(int layer, int... data){
        int[] colors = new int[data.length];
        System.arraycopy(data, 0, colors, 0, data.length);
        chartColors.add(colors);
       return this;
    }
    
    public double total(int layer){
        return chartVectors.get(layer).sum();
    }
    
    
    public int    count(int layer){
        return this.chartVectors.get(layer).getSize();
    }
    
    public int getLayers(){return this.chartVectors.size();}
    
    public double fraction(int layer, int index){
        double total = this.total(layer);
        double value = this.chartVectors.get(layer).getValue(index);
        return total==0.0?1:value/total;
    }
}
