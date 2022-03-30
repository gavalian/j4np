/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts;

import deepnetts.data.TabularDataSet;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.FeedForwardNetwork.Builder;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingEvent.Type;
import deepnetts.net.train.TrainingListener;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.FileIO;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4np.utils.io.DataArrayUtils;

import j4np.utils.io.TextFileWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;


/**
 *
 * @author gavalian
 */
public class DeepNettsClassifier {
    
    FeedForwardNetwork      neuralNet = null;
    BackpropagationTrainer   trainer  = null;
    
    public DeepNettsClassifier(){
        
    }
    
    
    public void load(String filename) {
        try {
            neuralNet =  FileIO.createFromFile(filename, FeedForwardNetwork.class);
            
        } catch (IOException ex) {
            Logger.getLogger(DeepNettsClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DeepNettsClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initFromFile(String filename){
        
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
                stream.add(DeepNettsClassifier.getString(neuralNet.getLayers().get(i).getWeights().getValues(),
                        k*width, width,","));
            }
            //System.out.println(DataSetReader.getString(neuralNet.getLayers().get(i).getBiases(),","));
            stream.add(DeepNettsClassifier.getString(neuralNet.getLayers().get(i).getBiases(),","));
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
    
    private DataSet convert(DataList list){
        
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
    
    public DataList evaluate(DataList dpl){
        DataList result = new DataList();
        for(int i = 0; i < dpl.getList().size(); i++){
            float[] input = dpl.getList().get(i).floatFirst();
            this.neuralNet.setInput(input);
            float[] output = this.neuralNet.getOutput();
            result.add(new DataEntry(
                    DataArrayUtils.toDouble(input),
                    DataArrayUtils.toDouble(output)));
        }
        return result;
    }
    
    public void train(DataList dpl, int nEpochs){
        DataSet converted = this.convert(dpl);
        this.train(converted, nEpochs);
    }
    
    public void test(DataList dpl){
        DataSet converted = this.convert(dpl);
        this.evaluate(converted);
    }
    
    public void train(DataSet trSet, int nEpochs){
        
        //trainer.setCheckpointEpochs(100);
        trainer.setMaxEpochs(nEpochs);
        System.out.println("adding listener ");
        
        ProgressListener pl = new ProgressListener(nEpochs);
        
        LogManager.shutdown();
        
        //LoggerContext ctx = (LoggerContext) LogManager.getContext(false);        
        //Configuration config = ctx.getConfiguration();
        //Layout layout = PatternLayout.createDefaultLayout(config);
                
        //System.out.println("check logger = " + LogManager.exists(DeepNetts.class.getName()));
        //System.out.println("check logger = " + log.isInfoEnabled());
        
        trainer.addListener(pl);
        System.out.println("*********");
        System.out.println("* Start Training Network with data set size = " + trSet.getItems().size());
        System.out.println("*********");
        //trainer.setMaxEpochs(25);
        //for(int k = 0; k < nEpochs/25; k++){        
        trainer.train(trSet);       
        System.out.println("*********");
        System.out.println("* Finished Training" );
        System.out.println("* " + pl.statusString(trainer));
        System.out.println("*********");
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
    
    public static class ProgressListener implements TrainingListener {
        
        private int          maxEpochs = 0;
        private int       epochCounter = 0;
        private long          lastTime = 0L;
        private int   printoutInterval = 12;
        private int       dataInterval = 1;
        
        private List<Double>  lossList = new ArrayList<>();
        private List<Double>   accList = new ArrayList<>();
        
        public ProgressListener(int me){
            maxEpochs = me;
        }
        
        public String statusString(BackpropagationTrainer tr){
            return String.format(" [%5d/%5d], loss = %e , accuracy = %12.8f",
                    epochCounter,maxEpochs,
                    tr.getTrainingLoss(),
                    tr.getTrainingAccuracy());
        }
        
        @Override
        public void handleEvent(TrainingEvent te) {
            
            if(te.getType()==Type.EPOCH_FINISHED){
                
                epochCounter++;
                
                if(epochCounter%this.dataInterval==0){
                    lossList.add((double) te.getSource().getTrainingLoss());
                    accList.add((double)  te.getSource().getTrainingAccuracy());
                }
                
                System.out.print("."); System.out.flush();
                
                if(epochCounter%this.printoutInterval==0){
                    System.out.println(statusString(te.getSource()));                            
                    //System.out.println("\n");
                }
                /*
                epochCounter++;
                if(epochCounter%printoutInterval==0){
                    System.out.printf(" >> %6d/%6d   %e, %9.6f\n", 
                            epochCounter,maxEpochs, 
                            te.getSource().getTrainingLoss(),te.getSource().getTrainingAccuracy());
                } else {
                    System.out.print("."); System.out.flush();
                }*/
            }
        }
    }
    
    public static void process(String outputNetwork, int[] networkLayers, int nEpochs, 
            DataProvider train, DataProvider test){
        
        DataSet dataTrain = train.getData();
        DataSet dataTest  = null;
        if(test!=null){ dataTest = test.getData();}
        DeepNettsClassifier cl = new DeepNettsClassifier();
        cl.init(networkLayers);        
        cl.train(dataTrain, nEpochs);
        cl.save(outputNetwork);
        if(dataTest!=null){
            cl.evaluate(dataTest);
        }
    }
    
    public static void main(String[] args){
        DataSet ds = DataSetUtils.getRandomSet(120000, 24, 4);
        DeepNettsClassifier cl = new DeepNettsClassifier();
        cl.init(new int[]{24,48,48,48,4});
        
        cl.train(ds, 48);
    }
}
