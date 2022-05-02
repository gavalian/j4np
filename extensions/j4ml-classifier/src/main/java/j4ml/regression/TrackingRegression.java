/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.regression;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.deepnetts.DeepNettsRegression;

/**
 * 
 * @author gavalian
 */
public class TrackingRegression {
    
    DeepNettsRegression   network = null;
    public int            nEpochs = 250;
    public String      outputFile = "";
    
    
    public static DataNormalizer features = new DataNormalizer(new double[]{1,1,1,1,1,1},
                new double[]{112,112,112,112,112,112});
        
    public static  DataNormalizer labels = new DataNormalizer(new double[]{0.5,5.0,40.0},
                new double[]{6.5,35.0,120.0});
    
    
    public TrackingRegression(){
        
    }
    
    public void train(DataList list){
        network = new DeepNettsRegression(ActivationType.TANH,ActivationType.LINEAR);
        network.init(new int[]{6,12,12,12,3});
        network.train(list, nEpochs);
        if(outputFile.length()>2){
            network.save(outputFile);
        }
    }
    
    public void test(DataList list){
        long then = System.nanoTime();
        network.evaluate(list);
        long now = System.nanoTime();
        
        int nsize = list.getList().size();
        double msec = (now-then)*1e-6;
        double  sec = (now-then)*1e-9;
        double rate = ((double) nsize)/sec;
        double average = msec/nsize;
        System.out.printf(":::: \n");
        System.out.printf(":::: data evaluated  : %d\n",nsize);
        System.out.printf(":::: evaluation time : %.2f nsec\n",msec);
        System.out.printf(":::: evaluation per instance : %.2f nsec\n",average);
        System.out.printf(":::: evaluation frequency    : %.2f Hz\n",rate);
        System.out.printf(":::: \n");                
    }
    
    public void normalize(DataList list){
        DataList.normalizeInput(list, features);
        DataList.normalizeOutput(list, labels);
    }
    
    public void denormalize(DataList list){
        DataList.denormalizeOutput(list, labels);
        DataList.denormalizeInfered(list, labels);
    }
            
}
