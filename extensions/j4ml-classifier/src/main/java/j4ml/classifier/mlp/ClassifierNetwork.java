/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.classifier.mlp;

import deepnetts.data.TabularDataSet;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.FeedForwardNetwork.Builder;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.FileIO;
import j4np.utils.io.DataArrayUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;
import org.jlab.jnp.readers.TextFileWriter;

/**
 *
 * @author gavalian
 */
public class ClassifierNetwork {
    
    FeedForwardNetwork neuralNet = null;
    BackpropagationTrainer trainer  = null;
    
    public ClassifierNetwork(){
        
    }
    
    
    public void load(String filename) {
        try {
            neuralNet =  FileIO.createFromFile(filename, FeedForwardNetwork.class);
            
        } catch (IOException ex) {
            Logger.getLogger(ClassifierNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassifierNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void init(int[] layers){
        Builder b = FeedForwardNetwork.builder();
        b.addInputLayer(layers[0]);
        
        for(int i = 1; i < layers.length-1; i++){
            b.addFullyConnectedLayer(layers[i], ActivationType.RELU);
        }
        
        b.addOutputLayer(layers[layers.length-1], ActivationType.SOFTMAX)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .randomSeed(456);
        
        neuralNet = b.build();
         
        System.out.println(neuralNet);
        
        trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.000004f);
        trainer.setLearningRate(0.001f);
        trainer.setMomentum(0.9f);
        
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(2000);
        
    }
    
    public static String getString(float[] array, int start, int length,  String delim){
        StringBuilder str = new StringBuilder();
        //str.append("[");
        for(int i = 0; i < length; i++)
        {
            if(i!=0) str.append(delim);
            str.append(String.format("%.12f", array[i+start]));
        }
        //str.append("]");
        return str.toString();
    }
    
    public static String getString(float[] array, String delim){
        StringBuilder str = new StringBuilder();
        //str.append("[");
        for(int i = 0; i < array.length; i++)
        {
            if(i!=0) str.append(delim);
            str.append(String.format("%.12f", array[i]));
        }
        //str.append("]");
        return str.toString();
    }

    public List<String>  getNetworkStream(){
        
        List<String> stream = new ArrayList<String>();
        int nLayers = neuralNet.getLayers().size();
        for(int i = 1; i < nLayers; i++){
            //System.out.println(neuralNet.getLayers().get(i-1).getWidth()
            //        +","+neuralNet.getLayers().get(i).getWidth());
            stream.add(neuralNet.getLayers().get(i-1).getWidth()
                    +","+neuralNet.getLayers().get(i).getWidth());
            //System.out.println(DataSetReader.getString(neuralNet.getLayers().get(i).getWeights().getValues(),","));
            float[] weigths = neuralNet.getLayers().get(i).getWeights().getValues();
            int     width   = neuralNet.getLayers().get(i).getWidth();
            int     widthP  = neuralNet.getLayers().get(i-1).getWidth();
            for(int k = 0; k < widthP; k++){
                //System.out.println(DataSetReader.getString(neuralNet.getLayers().get(i).getWeights().getValues(),
                //        k*width, width,","));
                //neuralNet.getLayers().get(i).getWeights().getValues()
                //DataArrayUtils.floatToString();
                stream.add(this.getString(neuralNet.getLayers().get(i).getWeights().getValues(),
                        k*width, width,","));
            }
            //System.out.println(DataSetReader.getString(neuralNet.getLayers().get(i).getBiases(),","));
            stream.add(this.getString(neuralNet.getLayers().get(i).getBiases(),","));
        }
        return stream;
    }
    
    public void save(String filename){
        TextFileWriter writer = new TextFileWriter();
        writer.open(filename);
        List<String> dataLines = this.getNetworkStream();
        for(String line : dataLines){
            writer.writeString(line);
        }
        writer.close();
    }
    
    public void train(DataSet trSet, int nEpochs){
        
        trainer.setCheckpointEpochs(100);
        trainer.setMaxEpochs(nEpochs);
        trainer.train(trSet);
        
        System.out.println("accuracy = " + trainer.getTrainingAccuracy());
        System.out.println("loss = " + trainer.getTrainingLoss());
    }    
    
    public void evaluate(DataSet set){
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        EvaluationMetrics em = evaluator.evaluate(neuralNet, set);
        System.out.println("CLASSIFIER EVALUATION METRICS");
        System.out.println(em);
        System.out.println("CONFUSION MATRIX");
        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        System.out.println(cm); 
    }
    
    public static float[] getVector(String vecString, int vecSize){
        float[] vector = new float[vecSize];
        String[] tokens = vecString.split("\\s+");
        for(int i = 1; i < tokens.length; i++){
            String[] pair = tokens[i].split(":");
            int item = Integer.parseInt(pair[0]);
            float value = Float.parseFloat(pair[1]);
            vector[item-1] = value;
        }
        return vector;
    }
    /*
    public static DataSet readDataset(String filename, int max, int skip){
        TabularDataSet  dataset = new TabularDataSet(6,3);

        TextFileReader reader = new TextFileReader();
        reader.open(filename);
        int counter = 0;
        
        for(int s = 0; s < skip ; s++){
            List<String> lines = reader.readLines(1);
        }
        
        while(true){
            List<String> lines = reader.readLines(1);
            if(lines.size()<1) break;
            if(lines.get(0).length()<3) break;
            //System.out.println("-> " + lines.get(0));
            float[] input = DeepNettsClassifier.getVector(lines.get(0),6);
            float[] output = new float[]{0.0f,0.0f,0.0f};
            String[] tokens = lines.get(0).split("\\s+");
            Integer  itemClass = Integer.parseInt(tokens[0]);
            output[itemClass]  = 1.0f;
            //dataset.add(new DataSetRow(input,output));
            //dataset
            dataset.add(new TabularDataSet.Item(input, output));
            counter++;
            if(counter>=max) break;
        }
        System.out.printf("[READ-DATASET] data set imported, rows = %d\n",counter);
        //System.out.println(dataset);
        return dataset;
    }*/
}
