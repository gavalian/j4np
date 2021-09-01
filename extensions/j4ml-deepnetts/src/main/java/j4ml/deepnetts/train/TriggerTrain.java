/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.train;

import deepnetts.data.TabularDataSet;
import j4ml.deepnetts.ejml.EJMLModelEvaluator;
import j4ml.deepnetts.network.DeepNettsRegression;
import java.util.Iterator;
import java.util.Random;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class TriggerTrain {

    public static TabularDataSet generate(int size){
        TabularDataSet  dataset = new TabularDataSet(9,1);
        Random r = new Random();
        for(int i = 0; i < size ; i++){
            float[] input = new float[9];
            float[] output = new float[1];
            for(int k = 0; k < 9 ;k++) input[k] = r.nextFloat();
            double value = r.nextDouble();
            if(value>=0.5){
                output[0] = 1.0f;
            } else { output[0] = 0.0f;}
            dataset.add(new TabularDataSet.Item( input, output));
        }
        String[] names = new String[10];
        for(int i = 0; i < names.length; i++){ names[i] = "f"+i; }
        dataset.setColumnNames(names);
        return dataset;
    }
    
    public static void benchmark(DeepNettsRegression network, int size){
        DataSet ds = TriggerTrain.generate(size);
         Iterator iter = ds.iterator();
        int counter = 0;
        long then = System.nanoTime();
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  prediction = network.getNetwork().predict(item.getInput().getValues());           
            counter++;
        }
        long now = System.nanoTime();
        double nsPerEvent = ((double) (now-then) ) / counter;
        System.out.printf("benchmark, time = %8d, iter = %8d, bench = %8.2f\n",
                now-then,counter,nsPerEvent);
    }
    
    public static void benchmarkEJML(String filename, int size){
        EJMLModelEvaluator model = new EJMLModelEvaluator(filename);
        DataSet ds = TriggerTrain.generate(size);
         Iterator iter = ds.iterator();
        int counter = 0;
        float[]  output = new float[1];
        
        long then = System.nanoTime();
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input = item.getInput().getValues();
            model.feedForward(input, output);
            counter++;
        }
        long now = System.nanoTime();
        double nsPerEvent = ((double) (now-then) ) / counter;
        System.out.printf("benchmark, time = %8d, iter = %8d, bench = %8.2f\n",
                now-then,counter,nsPerEvent);
    }
    
    public static void main(String[] args){
        DataSet ds = TriggerTrain.generate(10000);
        DeepNettsRegression network = new DeepNettsRegression();
        network.init(new int[]{9,18,9,1});
        network.initTrainer();
        
        network.train(ds, 20);
        
        for(int i = 0; i < 20; i++){
            System.out.println(" [DeppNetts] iteration = " + i);
            TriggerTrain.benchmark(network, 1500000);
        }
        
        network.save("trigger.network");
        
        for(int i = 0; i < 20; i++){
            System.out.println(" [EJML] iteration = " + i);
            TriggerTrain.benchmarkEJML("trigger.network", 1500000);
        }
        
    }
}
