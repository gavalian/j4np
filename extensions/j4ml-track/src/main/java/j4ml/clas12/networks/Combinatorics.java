/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.networks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class Combinatorics {
    
    List<Double>[] layers = new List[6];
    double[] result = new double[150000*6];
            
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
    
    public List<double[]> getCombinationsOptimized(){

        int c = 0;
        List<double[]> data = new ArrayList<>();        
        for(int l1 = 0; l1 < layers[0].size(); l1++){
            for(int l2 = 0; l2 < layers[1].size(); l2++){
                for(int l3 = 0; l3 < layers[2].size(); l3++){
                    for(int l4 = 0; l4 < layers[3].size(); l4++){
                        for(int l5 = 0; l5 < layers[4].size(); l5++){
                            for(int l6 = 0; l6 < layers[5].size(); l6++){
                                /*double[] means = new double[6];
                                means[0] = layers[0].get(l1);
                                means[1] = layers[1].get(l2);
                                means[2] = layers[2].get(l3);
                                means[3] = layers[3].get(l4);
                                means[4] = layers[4].get(l5);
                                means[5] = layers[5].get(l6);
                                data.add(means);*/
                                result[c] = layers[0].get(l1); c++;
                                result[c] = layers[1].get(l2); c++;
                                result[c] = layers[2].get(l3); c++;
                                result[c] = layers[3].get(l4); c++;
                                result[c] = layers[4].get(l5); c++;
                                result[c] = layers[5].get(l6); c++;
                            }
                        }
                    }
                }
            }
        }
        data.add(result);
        return data;
    } 
    public static int shareCount(double[] a, double[] b){
        int count = 0;        
        for(int i = 0; i < a.length; i++)
            if(Math.abs(a[i]-b[i])<0.000001) count++;        
        return count;
    }
    
    public static boolean doShare(double[] a, double[] b){
        for(int i = 0; i < a.length; i++)
            if(a[i]-b[i]<0.000001) return true;
        return false;
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
    
    public static void main(String[] args){
        
        Combinatorics c = new Combinatorics();
        Random  r = new Random();
        int  iter = 400000;
        
        long then = System.currentTimeMillis();
        
        for(int n = 0; n < iter; n++){
            c.reset();
            for(int i = 0; i < 6; i++){
                double start = 0.0;
                for(int k = 0; k < 4; k++){
                    double next = r.nextDouble()*16+4;
                    start += next;
                    c.add(i, start);
                }
            }
            List<double[]> clusters = c.getCombinationsOptimized();
            //List<double[]> clusters = c.getCombinations();
        }
        long now = System.currentTimeMillis();
        c.show();
        
        //System.out.println(" size = " + clusters.size());
        System.out.printf(" time %d \n",now-then);
    }
}
