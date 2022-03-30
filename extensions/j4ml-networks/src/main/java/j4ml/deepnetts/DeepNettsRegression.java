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
import j4ml.data.CSVReader;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
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
public class DeepNettsRegression {
    
    FeedForwardNetwork neuralNet = null;
    BackpropagationTrainer trainer  = null;
    
    ActivationType hiddenActivation = ActivationType.RELU;
    ActivationType  lastActivation = ActivationType.SIGMOID;
    
    public DeepNettsRegression(){}
    
    public DeepNettsRegression(ActivationType hidden, ActivationType last){
        setActivation(hidden,last);
    }
    
    public final void setActivation(ActivationType hidden, ActivationType last){
        this.hiddenActivation = hidden; 
        this.lastActivation   = last;
    }
    
    public void init(int[] layers){
        
        FeedForwardNetwork.Builder b = FeedForwardNetwork.builder();
        b.addInputLayer(layers[0]);
        
        for(int i = 1; i < layers.length-1; i++){
            b.addFullyConnectedLayer(layers[i], hiddenActivation);
        }
        
        b.addOutputLayer(layers[layers.length-1], lastActivation)
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
    
    public void evaluate(String file, DataList ds){
        DataSet set = this.convert(ds);
        this.evaluate(file, set);
    }
    
    public void test(DataList ds){
        
        DataSet set = this.convert(ds);
        Iterator iter = set.iterator();
        
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[] desired = item.getTargetOutput().getValues();
            float[]  output = neuralNet.predict(input);
            
            
        }
    }
    
    public void evaluate(DataList ds){
        int size = ds.getList().size();
        for(int i = 0; i < size; i++){
            float[]  input = ds.getList().get(i).floatFirst();
            float[]  desired = ds.getList().get(i).floatSecond();
            float[] output = neuralNet.predict(input);
            float[] infered = new float[output.length];
            System.arraycopy(output, 0, infered, 0, infered.length);
            ds.getList().get(i).setInfered(infered);
            /*System.out.println( " evaluating " + 
                    Arrays.toString(input) + " ==> " + 
                            Arrays.toString(desired) + " ==> " + 
                            Arrays.toString(output)
                            );*/
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
    
    public void train(DataList dpl, int nEpochs){
        DataSet converted = this.convert(dpl);
        this.train(converted, nEpochs);
    }
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/Software/project-10.4/j4np-1.0.4/regression_data_2.csv";
        
        
        CSVReader reader = new CSVReader(file,6,3);                
        reader.setInputOutput(new int[]{0,1,2,3,4,5}, new int[]{6,7,8});
        DataList list = reader.getData();
        
        list.show();

        DataNormalizer in_norm = new DataNormalizer(new double[]{1,1,1,1,1,1},
                new double[]{112,112,112,112,112,112});
        
        DataNormalizer out_norm = new DataNormalizer(new double[]{0.5,5.0,40.0},
                new double[]{6.5,35.0,120.0});
        
        list.scan();
        
        DataList.normalizeInput(list, in_norm);
        DataList.normalizeOutput(list, out_norm);
        
        DataList[] data = DataList.split(list, 0.7,0.3);
        
        DeepNettsRegression reg = new DeepNettsRegression();
        
        reg.init(new int[]{6,12,12,3});
        reg.train(data[0], 225);
        
        reg.evaluate(data[1]);
        
        DataList.denormalizeOutput(data[1], out_norm);
        DataList.denormalizeInfered(data[1], out_norm);
        System.out.println(data[1].toCSVString());
        //reg.evaluate("evaluate.csv", data[1]);
        /*for(int i = 0; i < 25; i++){
            System.out.println(" infered value = " + Arrays.toString(data[1].getList().get(i).getInfered()));
        }*/
    }
   
}
