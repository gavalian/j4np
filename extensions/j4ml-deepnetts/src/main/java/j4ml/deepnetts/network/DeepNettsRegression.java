/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.network;

import deepnetts.data.TabularDataSet;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import java.util.ArrayList;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import org.jlab.jnp.readers.TextFileWriter;

/**
 *
 * @author gavalian
 */
public class DeepNettsRegression {
    
    FeedForwardNetwork neuralNet = null;
    BackpropagationTrainer trainer  = null;
        
    public DeepNettsRegression(){}
    
    public void init(int[] layers){
        
        FeedForwardNetwork.Builder b = FeedForwardNetwork.builder();
        b.addInputLayer(layers[0]);
        for(int i = 1; i < layers.length-1; i++){
            b.addFullyConnectedLayer(layers[i], ActivationType.RELU);
        }
        b.addOutputLayer(layers[layers.length-1], ActivationType.SIGMOID)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .randomSeed(456);
        
        neuralNet = b.build();
        
        System.out.println(neuralNet);
    }
    
    public  String getString(float[] array, int start, int length,  String delim){
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
    
    public  String getString(float[] array, String delim){
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
    
    public static String[] generateNames(int input, int output){
          String[] names = new String[input+output];
        for(int i = 0; i < input; i++) names[i] = "in" + i;
        for(int i = 0; i < output; i++) names[i+input] = "out" + i;        
        return names;
    }
    
    public List<String>  getNetworkStream(){
        
        List<String> stream = new ArrayList<String>();
        int nLayers = neuralNet.getLayers().size();
         for(int i = 1; i < nLayers; i++){
             // Write Input Layers and Output Layers
             stream.add(neuralNet.getLayers().get(i-1).getWidth()
                     +","+neuralNet.getLayers().get(i).getWidth());
             //System.out.println(DataSetReader.getString(neuralNet.getLayers().get(i).getWeights().getValues(),","));
             float[] weigths = neuralNet.getLayers().get(i).getWeights().getValues();
             int     width   = neuralNet.getLayers().get(i).getWidth();
             int     widthP  = neuralNet.getLayers().get(i-1).getWidth();
             for(int k = 0; k < widthP; k++){
                 //System.out.println(DataSetReader.getString(neuralNet.getLayers().get(i).getWeights().getValues(),
                 //        k*width, width,","));
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
        
        List<String> networkLines = this.getNetworkStream();
        for(int i = 0; i < networkLines.size(); i++){
            writer.writeString(networkLines.get(i));
        }
        writer.close();
    }
    
    public void initTrainer(){
        trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.0000004f);
        trainer.setLearningRate(0.01f);
        trainer.setMomentum(0.9f);
        //trainer.setL1Regularization(0.0001f);
        //trainer.setOptimizer(OptimizerType.MOMENTUM);
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(200);
    }
    
    public FeedForwardNetwork getNetwork(){ return neuralNet;}
    
    public void train(DataSet ds, int nEpochs){
        
        initTrainer();
        trainer.setMaxEpochs(nEpochs);        
        trainer.setCheckpointEpochs(100);
        
        trainer.train(ds);
        
        System.out.println("accuracy = " + trainer.getTrainingAccuracy());
        System.out.println("loss = " + trainer.getTrainingLoss());
    }
    
    
    
    public static void main(String[] args){        
        
    }
   
}
