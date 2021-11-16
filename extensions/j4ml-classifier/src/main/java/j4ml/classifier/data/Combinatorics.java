/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.classifier.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Combinatorics {
    
    List<Double>[] layers = new List[6];
    
    public Combinatorics(){
        for(int i = 0; i < layers.length; i++){
            layers[i] = new ArrayList<Double>();
        }
    }
    
    public boolean hasThat(List<Double> values, double value){
        for(Double v : values){
            if(Math.abs(v-value)<0.005) return true;
        }
        return false;
    }
    
    public void add(int layer, double mean){
        if(this.hasThat(layers[layer],mean)==false){
            layers[layer].add(mean);
        } else {
            System.out.println("ooooow : has that");
        }
    }
    
    public void reset(){
        for(List l : layers) l.clear();
    }
    
    public List<double[]> getCombinations(){
        
        List<double[]> data = new ArrayList<>();        
        for(int l1 = 0; l1 < layers[0].size(); l1++){
            for(int l2 = 0; l2 < layers[1].size(); l2++){
                for(int l3 = 0; l3 < layers[2].size(); l3++){
                    for(int l4 = 0; l4 < layers[3].size(); l4++){
                        for(int l5 = 0; l5 < layers[4].size(); l5++){
                            for(int l6 = 0; l6 < layers[5].size(); l6++){
                                double[] means = new double[6];
                                means[0] = layers[0].get(l1);
                                means[1] = layers[1].get(l2);
                                means[2] = layers[2].get(l3);
                                means[3] = layers[3].get(l4);
                                means[4] = layers[4].get(l5);
                                means[5] = layers[5].get(l6);
                                data.add(means);
                            }
                        }
                    }
                }
            }
        }
        return data;
    }
    
        
    
    public static double distance(double[] a, double[] b){
        double dist = 0.0;
        for(int i = 0; i < a.length; i++)
            dist += Math.abs(a[i]-b[i]);
        return dist;
    }
    
    public void show(){
        System.out.println("---- combinatorics");
        for(int i = 0; i < 6; i++){
            System.out.printf("L %4d : %s\n",i+1,Arrays.toString(layers[i].toArray()));
        }
    }
}
