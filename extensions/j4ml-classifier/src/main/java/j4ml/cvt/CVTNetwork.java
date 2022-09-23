/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.cvt;

import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsClassifier;
import j4ml.deepnetts.DeepNettsNetwork;
import j4np.utils.io.TextFileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class CVTNetwork {
    
    
    public static DataList readFile(String file, int max){
        DataList list = new DataList();
        TextFileReader r = new TextFileReader();
        r.open(file);
        int counter = 0;
        while(r.readNext()==true&&counter<max){
            
            
            counter++;
            
            String line = r.getString();
            String[] tokens = line.split(";");
            String[] lsvm = tokens[2].trim().split("\\s+");
            
            
            float[] input = CvtArrayIO.lsvmToArray(tokens[2], 90*256);
            float[] output = CvtArrayIO.csvToArray(tokens[1]);
            float[] params = CvtArrayIO.csvToArray(tokens[0]);
                       
            //System.out.println("--- adding entry");
            list.add(new DataEntry(input,output));
        }
        return list;
    }
    
    public static int[] compare(float[] desired, float[] result, double threshold){
        int[] measure = new int[]{0,0,0,0,0};
        for(int i = 0; i < desired.length; i++){

            if(desired[i]>threshold&&result[i]>threshold) measure[0]++;
            if(desired[i]<threshold&&result[i]>threshold) measure[1]++;
            if(desired[i]>threshold&&result[i]<threshold) measure[2]++;
            if(desired[i]>threshold) measure[3]++;
            if(result[i]>threshold)  measure[4]++;
        }
        return measure;
    }
    
    
    
    public static void train(DataList tr, DataList test, int epochs){
        System.out.println(">>>>> TRAINING DATA SIZE = " + tr.getList().size());
        DataSet ds = DeepNettsNetwork.convert(tr, true);
        
        System.out.println(">>>>> CONVERTED DATA SIZE = " 
                + ds.getItems().size() + " OLD = " + tr.getList().size());
        
        ConvolutionalNetwork neuralNet = ConvolutionalNetwork.builder()
                .addInputLayer(256, 90,1)
                .addConvolutionalLayer(3, 3, 1, 2)
                .addMaxPoolingLayer(4, 4, 2)
                .addConvolutionalLayer(3, 3, 1, 2)
                .addMaxPoolingLayer(4, 4, 2)
                .addFullyConnectedLayer(84)
                .hiddenActivationFunction(ActivationType.RELU)
                .addOutputLayer(84, ActivationType.SIGMOID)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(123)
                .build();
        
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        trainer.setLearningRate(0.001f) // za ada delta 0.00001f za rms prop 0.001
                .setMaxError(0.003f)
                .setOptimizer(OptimizerType.SGD) // use adagrad optimization algorithm
                .setLearningRate(0.01f)
                .setMaxEpochs(epochs);
        
        
        for(int i = 0; i < 20; i++){
            trainer.train(ds);
            
            try {
                
                FileIO.writeToFile(neuralNet, "cnn_logreg_"+((i+1)*epochs)+".nnet");
                FileIO.writeToFileAsJson(neuralNet, "cnn_logreg.nnet.json");
                
            } catch (IOException ex) {
                Logger.getLogger(CVTNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
        
        long time = 0L;
        for(int i = 0; i < test.getList().size(); i++){
            float[]   input = test.getList().get(i).floatFirst();
            float[] desired = test.getList().get(i).floatSecond();
            long then = System.currentTimeMillis();
            neuralNet.setInput(new Tensor(input));            
            float[] result  = neuralNet.getOutput();
            long now = System.currentTimeMillis();
            time += now-then;
            int[] measure = CVTNetwork.compare(desired, result, 0.5);
            //System.out.println(i + " --m " + Arrays.toString(measure) + " : " + Arrays.toString(result));
            //System.out.println(Arrays.toString(desired));
        }
        System.out.printf(" evaluated %d , in %d ms, rate = %.2f\n",
                test.getList().size(),time, ((double) time)/test.getList().size());
    }
    
    public static void retrain(String inNetwork, String outNetwork, String file, int nEpochs, int maxLines){
         //String file = "cvt_training_data_regression_noghost.lsvm";
         //String file = "xaa.lsvm";
        try {
            ConvolutionalNetwork nnet = FileIO.createFromFile(inNetwork, ConvolutionalNetwork.class);
            
            DataList tr = CVTNetwork.readFile(file, maxLines);
            tr.shuffle();
            BackpropagationTrainer trainer = new BackpropagationTrainer(nnet);//nnet.getTrainer();
            trainer.setLearningRate(0.001f) // za ada delta 0.00001f za rms prop 0.001
                .setMaxError(0.0001f)
                .setOptimizer(OptimizerType.SGD) // use adagrad optimization algorithm
                .setLearningRate(0.01f)
                .setMaxEpochs(nEpochs);
            System.out.println("data size = " + tr.getList().size());
            DataSet ds = DeepNettsClassifier.toDataSet(tr);
            System.out.println(" data is null ? " + (ds == null));
            trainer.train(ds);
            FileIO.writeToFile(nnet, outNetwork);
        }  catch (IOException ex) {
            Logger.getLogger(CVTNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CVTNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void evaluate(String network, String file) {
        //String file = "cvt_training_data_regression_noghost.lsvm";
        
        try {
            ConvolutionalNetwork nnet = FileIO.createFromFile(network, ConvolutionalNetwork.class);
            DataList test = CVTNetwork.readFile(file, 10000);
            System.out.println("FILE    : " + file);
            System.out.println("NETWORK : " + network);
            //System.out.println(nnet);
            for(double nt = 0.1; nt > 0.0001; nt -= 0.002 ){
                double threshold = nt;
                int       counter = 0;
                int   counterTrue = 0;
                int  counterFalse = 0;
                int         noise = 0;
                int    edatanoise = 0;
                
                int counterPartial = 0;
                long then = System.currentTimeMillis();
                for(int i = 0; i < test.getList().size(); i++){
                    float[]   input = test.getList().get(i).floatFirst();
                    float[] desired = test.getList().get(i).floatSecond();
                    nnet.setInput(new Tensor(input));            
                    float[] result  = nnet.getOutput();
                    int[] measure = CVTNetwork.compare(desired, result, threshold);
                    //System.out.println(i + " [m] " + Arrays.toString(measure));
                    noise += measure[1];
                    
                    if(measure[0]>=measure[3]){
                        counterTrue++;
                    } else {
                        counterFalse++;
                    }
                    
                    if(measure[3]==6&&measure[0]>=4&&measure[0]<6) counterPartial++;
                    counter++;
                }
                
                long now = System.currentTimeMillis();
                System.out.printf("%12.4f : # %9d, true = %9d, partial = %9d, false = %9d ,  noise = %8.3f, time = %d ms, rate = %d evt/sec\n",
                        threshold,counter,counterTrue,counterPartial,counterFalse,
                        ((double) noise)/counter,now-then,(int) ((double) counter*1000)/(now-then) );
            }
        } catch (IOException ex) {
            Logger.getLogger(CVTNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CVTNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    public static void processCommand(String[] args){        
        String file = "/Users/gavalian/Work/Software/project-10.4/distribution/j4np/extensions/j4ml-classifier/cvt_regression_train.lsvm";
        int    nentries = 12000;
        int      epochs = 25;
        
        if(args.length>1) file = args[1];
        if(args.length>2) nentries = Integer.parseInt(args[2]);
        if(args.length>3) epochs = Integer.parseInt(args[3]);
        
        DataList     tr = CVTNetwork.readFile(file, nentries);
        DataList[] data = DataList.split(tr, 0.95,0.05);
        //tr.show();
        System.out.println("******************");
        System.out.printf("* training data set = %d\n", data[0].getList().size());
        System.out.printf("* testing  data set = %d\n", data[1].getList().size());
        System.out.println("******************\n");
        
        CVTNetwork.train(data[0],data[1],epochs);
    }
    
    public static void main(String[] args){
        
        if(args[0].compareTo("train")==0){
            CVTNetwork.processCommand(args);
            return;
        }
        
        if(args[0].compareTo("test")==0){
            CVTNetwork.evaluate(args[1],args[2]);
            return;
        }
        
        System.out.println("\n\n>>>> error : use 'train' or 'test' as argument\n\n");
        //for(double th = 0.5; th >0.08; th -= 0.05)
        //CVTNetwork.evaluate();
        
        //CVTNetwork.retrain();
        /*
        String file = "/Users/gavalian/Work/Software/project-10.4/distribution/j4np/extensions/j4ml-classifier/cvt_regression_train.lsvm";
        int    nentries = 12000;
        int      epochs = 25;
        
        if(args.length>0) file = args[0];
        if(args.length>1) nentries = Integer.parseInt(args[1]);
        if(args.length>2) epochs = Integer.parseInt(args[2]);
        
        DataList tr = CVTNetwork.readFile(file, nentries);
        DataList[] data = DataList.split(tr, 0.8,0.2);
        //tr.show();
        System.out.println("******************");
        System.out.printf("* training data set = %d\n", data[0].getList().size());
        System.out.printf("* testing  data set = %d\n", data[1].getList().size());
        System.out.println("******************\n");
        
        CVTNetwork.train(data[0],data[1],epochs);*/
    }
}
