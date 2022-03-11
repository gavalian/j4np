/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package j4ml.deepnetts;

import deepnetts.data.TabularDataSet;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import j4np.utils.io.DataPairList;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import org.apache.logging.log4j.LogManager;


/**
 *
 * @author gavalian
 */
public class DeepNettsEncoder {
    
    FeedForwardNetwork neuralNet = null;
    BackpropagationTrainer trainer  = null;
    
    
    public DeepNettsEncoder(int[] layers){init(layers);}
    
    public DeepNettsEncoder(){}
    
    public final void init(int[] layers){
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
        
        trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.000004f);
        trainer.setLearningRate(0.001f);
        trainer.setMomentum(0.9f);
        
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(2000);
        
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
    /*
    public void train(DataSet ds, int nEpochs){
    
    initTrainer();
    trainer.setMaxEpochs(nEpochs);
    trainer.setCheckpointEpochs(100);
    
    trainer.train(ds);
    
    System.out.println("accuracy = " + trainer.getTrainingAccuracy());
    System.out.println("loss = " + trainer.getTrainingLoss());
    }*/        
    //trainer.setCheckpointEpochs(100);
    public void train(DataSet trSet, int nEpochs){
        trainer.setMaxEpochs(nEpochs);
        System.out.println("adding listener ");
        
        DeepNettsClassifier.ProgressListener pl = new DeepNettsClassifier.ProgressListener(nEpochs);
        
        LogManager.shutdown();
        
        //LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        //Configuration config = ctx.getConfiguration();
        //Layout layout = PatternLayout.createDefaultLayout(config);
        
        //System.out.println("check logger = " + LogManager.exists(DeepNetts.class.getName()));
        //System.out.println("check logger = " + log.isInfoEnabled());
        
        trainer.addListener(pl);
        System.out.println("*********");
        System.out.println("* Start Training Network with data set size = " + trSet.getItems().size());
        System.out.println("* Number of epochs = " + nEpochs);
        System.out.println("*********");
        //trainer.setMaxEpochs(25);
        //for(int k = 0; k < nEpochs/25; k++){
        trainer.train(trSet);
        System.out.println("*********");
        System.out.println("* Finished Training" );
        System.out.println("* " + pl.statusString(trainer));
        System.out.println("*********");
    }
    
    public void evaluate(String file, DataPairList ds){
        DataSet set = this.convert(ds);
        this.evaluate(file, set);
    }
    
    public void test(DataPairList ds){
        
        DataSet set = this.convert(ds);
        Iterator iter = set.iterator();
        
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[] desired = item.getTargetOutput().getValues();
            float[]  output = neuralNet.predict(input);
            
            
        }
    }
    
    public void evaluate(String file, DataSet ds){
        
        Iterator iter = ds.iterator();
        TextFileWriter w = new TextFileWriter();
        w.open(file);
        
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[] desired = item.getTargetOutput().getValues();
            float[]  output = neuralNet.predict(input);
            
            String data = String.format("%s,%s", Arrays.toString(desired),Arrays.toString(output));
            String export = data.replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "");
            w.writeString(export);
        }
        w.close();
    }
    
    private DataSet convert(DataPairList list){
        
        int nInputs = list.getList().get(0).getFirst().length;
        int nOutputs = list.getList().get(0).getSecond().length;
        
        TabularDataSet  dataset = new TabularDataSet(nInputs,nOutputs);
        for(int k = 0; k < list.getList().size(); k++){
            float[]  inBuffer = list.getList().get(k).floatFirst();
            float[] outBuffer = list.getList().get(k).floatSecond();            
            dataset.add(new TabularDataSet.Item(inBuffer, outBuffer));                
        }
        String[] names = DataSetUtils.generateNames(nInputs, nOutputs);
        dataset.setColumnNames(names);
        return dataset;
    }
    
    public void train(DataPairList dpl, int nEpochs){
        DataSet converted = this.convert(dpl);
        this.train(converted, nEpochs);
    }
    
    public static void main(String[] args){
        
        CsvDataProvider provider = new CsvDataProvider("mc_t_f9.csv",9,2);
        DataSet ds = provider.getData();
        ds.shuffle();
        DataSet[] data = ds.split(0.8,0.2);
        System.out.println(ds);
        DeepNettsRegression reg = new DeepNettsRegression();
        
        reg.init(new int[]{9,12,2});
        reg.train(data[0], 725);
        reg.evaluate("evaluate.csv", data[1]);
    }
   
}
