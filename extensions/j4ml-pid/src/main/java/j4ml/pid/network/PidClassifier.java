/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.pid.network;

import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import j4ml.pid.data.DataProvider;
import j4np.utils.io.DataPairList;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

/**
 *
 * @author gavalian
 */
public class PidClassifier {
    
    FeedForwardNetwork neuralNet = null;
    BackpropagationTrainer trainer  = null;
    
    public void init(int[] layers){
        FeedForwardNetwork.Builder b = FeedForwardNetwork.builder();
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
    
    public static void train28(){
        DataProvider provider = new DataProvider();
        DataPairList data_p = provider.loadData("/Users/gavalian/Work/dataspace/pid/rec_out_electron.hipo", 1);
        DataPairList data_n = provider.loadData("/Users/gavalian/Work/dataspace/pid/rec_out_pion.hipo", 0);
        
        data_p.getList().addAll(data_n.getList());
        
        data_p.scan();
        
        DataSet ds = provider.convert(data_p);
        
        PidClassifier classifier = new PidClassifier();
        classifier.init(new int[]{28,56,56,28,2});
        ds.shuffle();
        DataSet[] dsplit = ds.split(0.7,0.3);
        classifier.train(dsplit[0], 250);
        classifier.save("pidClassifier.network");
        classifier.evaluate(dsplit[1]);
    }
    
    public static void train19(){
        DataProvider provider = new DataProvider();
        DataPairList data_p = provider.loadData19("/Users/gavalian/Work/dataspace/pid/rec_out_electron.hipo", 1);
        DataPairList data_n = provider.loadData19("/Users/gavalian/Work/dataspace/pid/rec_out_pion.hipo", 0);
        
        data_p.getList().addAll(data_n.getList());
        
        data_p.scan();
        
        DataSet ds = provider.convert(data_p);
        
        PidClassifier classifier = new PidClassifier();
        classifier.init(new int[]{28,56,56,28,2});
        ds.shuffle();
        DataSet[] dsplit = ds.split(0.7,0.3);
        classifier.train(dsplit[0], 250);
        classifier.save("pidClassifier19.network");
        classifier.evaluate(dsplit[1]);
    }
    
    public static void main(String[] args){
        PidClassifier.train28();
    }
}
