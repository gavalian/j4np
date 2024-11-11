/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.deepnetts;

import deepnetts.data.TabularDataSet;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.data.DataTransformer;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author gavalian
 */
public class DeepNetts {
    
    
    public FeedForwardNetwork        neuralNet = null;
    public BackpropagationTrainer     trainer  = null;
    
    ActivationType hiddenActivation = ActivationType.RELU;
    ActivationType   lastActivation = ActivationType.SIGMOID;
    
    protected DataTransformer    inputTransformer = null;
    protected DataTransformer   outputTransformer = null;
    
    private boolean hasListener = false;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(DeepNettsNetwork.class.getName());
    LossType           lossFunction = LossType.MEAN_SQUARED_ERROR;
    double learningRate = 0.001;
    int    emptyCycle = 5;

    public DeepNetts(){       }
    
    public final DeepNetts outputActivation(ActivationType type){
        this.lastActivation = type; return this;
    }
    
    public final DeepNetts lossType(LossType type){
        this.lossFunction = type; return this;
    }
    
    public final DeepNetts learningRate(double rate){
        this.learningRate = rate; return this;
    }
    
    public final DeepNetts activation(ActivationType type){
        this.hiddenActivation = type; return this;
    }
    
    public final DeepNetts init(int... layers){
        
        FeedForwardNetwork.Builder b = FeedForwardNetwork.builder();        
        b.addInputLayer(layers[0]);
        
        for(int i = 1; i < layers.length-1; i++){
            b.addFullyConnectedLayer(layers[i], this.hiddenActivation);            
        }        
        b.addOutputLayer(layers[layers.length-1], this.lastActivation)
                .lossFunction(lossFunction)
                .randomSeed(456);
        
        neuralNet = b.build();
        
        System.out.println(neuralNet);
        
        trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.0000000004f);
        trainer.setLearningRate((float) this.learningRate);
        trainer.setMomentum(0.9f);        
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(2000);        
        return this;
    }
        
    public  void  initTrainer(){
        trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.0000000004f);
        trainer.setLearningRate((float) this.learningRate);
        trainer.setMomentum(0.9f);        
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(2000);
    }
    
    private DataSet convert(DataList list){        
        int nInputs = list.getList().get(0).getFirst().length;
        int nOutputs = list.getList().get(0).getSecond().length;        
        TabularDataSet  dataset = new TabularDataSet(nInputs,nOutputs);
        for(int k = 0; k < list.getList().size(); k++){
            float[]  inBuffer = list.getList().get(k).floatFirst();
            float[] outBuffer = list.getList().get(k).floatSecond();   
            float[]  inValues = inBuffer;
            float[] outValues = outBuffer;
            if(this.inputTransformer!=null) 
                inValues = this.inputTransformer.getValue(inBuffer);
            if(this.outputTransformer!=null) 
                outValues = this.outputTransformer.getValue(outBuffer);
            
            //System.out.println(Arrays.toString(inValues) + "  " + Arrays.toString(outValues));
            
            dataset.add(new TabularDataSet.Item(inValues, outValues));                
        }        
        String[] names = DataSetUtils.generateNames(nInputs, nOutputs);
        dataset.setColumnNames(names);
        return dataset;
    }
    public String  toArray(int[] array){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < array.length; i++){
            if(i!=0) str.append(",");
            str.append(array[i]);
        } str.append("]");
        return str.toString();
    }
    
    public String  toArray(float[] array){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < array.length; i++){
            if(i!=0) str.append(",");
            str.append(String.format("%.12f", array[i]));
        } str.append("]");
        return str.toString();
    }
    
    public String  toArray(double[] array){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < array.length; i++){
            if(i!=0) str.append(",");
            str.append(String.format("%.12f", array[i]));
        } str.append("]");
        return str.toString();
    }
    
    public String getActivationString(){
        if(this.hiddenActivation == ActivationType.RELU) return "RELU";
        if(this.hiddenActivation == ActivationType.SIGMOID) return "SIGM";
        if(this.hiddenActivation == ActivationType.TANH) return "TANH";
        if(this.hiddenActivation == ActivationType.LINEAR) return "LIN";
        if(this.hiddenActivation == ActivationType.SOFTMAX) return "SOFTMAX";        
         return "UNKNOWN";
    }
    
    public String getActivationStringOutput(){
        if(this.lastActivation == ActivationType.RELU) return "RELU";
        if(this.lastActivation == ActivationType.SIGMOID) return "SIGM";
        if(this.lastActivation == ActivationType.TANH) return "TANH";
        if(this.lastActivation == ActivationType.LINEAR) return "LIN";
        if(this.lastActivation == ActivationType.SOFTMAX) return "SOFTMAX";        
         return "UNKNOWN";
    }
    public void train(DataList dpl, int nEpochs){
        DataSet converted = this.convert(dpl);
        this.train(converted, nEpochs);
    }
    
    public void train(DataSet trSet, int nEpochs){
        
        //trainer.setCheckpointEpochs(100);
        trainer.setMaxEpochs(nEpochs);
        //System.out.println("adding listener ");
        
        DeepNettsClassifier.ProgressListener pl = new DeepNettsClassifier.ProgressListener(nEpochs);
        
        LogManager.shutdown();
        
        if(hasListener==false){ trainer.addListener(pl); hasListener = true;}
        
        System.out.println("*********");
        System.out.println("* Start Training Network with data set size = " + trSet.getItems().size());
        System.out.println("* Trainer learning rate  = " + this.learningRate);
        System.out.println("*********");
        //trainer.setMaxEpochs(25);
        //for(int k = 0; k < nEpochs/25; k++){ 
        //System.out.println(Arrays.toString(neuralNet.getLayers().get(1).getWeights().getValues()));
        System.out.println(" TRAINING : " + this.getJson());
        trainer.train(trSet); 
        System.out.println(" AFTER : " + this.getJson());
        System.out.println("*********");
        System.out.println("* Finished Training" );
        System.out.println("* " + pl.statusString(trainer));
        System.out.println("*********");
    }
    
    public String  getJson(){
        
        StringBuilder str = new StringBuilder();
        str.append("{\n\"architecture\": ");

        int  nLayers = neuralNet.getLayers().size();
        int[] layers = new int[nLayers];
        
        for(int i = 0; i < nLayers; i++){
            layers[i] = neuralNet.getLayers().get(i).getWidth();
            //neuralNet.getLayers().get(i).init();
        }
        str.append(toArray(layers)).append(",");
        
        if(this.inputTransformer!=null){
            str.append("\n\"input\": ").append(this.inputTransformer.toArray());
            str.append(",");
        }
        if(this.outputTransformer!=null){
            str.append("\n\"output\": ").append(this.outputTransformer.toArray());
            str.append(",");
        }
        str.append("\n\"hiddenActivation\":").append(String.format("\"%s\",", 
                this.getActivationString()));
        str.append("\n\"outputActivation\":").append(String.format("\"%s\",", 
                this.getActivationStringOutput()));

        str.append("\n");
        str.append("\"weights\": [\n");
        for(int i = 1; i < nLayers; i++) 
        {
            if(i!=1) str.append(",\n");
            //Tensor t = neuralNet.getLayers().get(i).getWeights();
            //System.out.println( i + " + " + (t==null));
            str.append(toArray(neuralNet.getLayers().get(i).getWeights().getValues()));
            //str.append("\n");
            //layers[i] = neuralNet.getLayers().get(i).getWidth();
        }
        str.append("\n],\n");
        
        str.append("\"biases\": [\n");
        for(int i = 1; i < nLayers; i++) 
        {
            if(i!=1) str.append(",\n");
            //Tensor t = neuralNet.getLayers().get(i).getWeights();
            //System.out.println( i + " + " + (t==null));
            str.append(toArray(neuralNet.getLayers().get(i).getBiases()));
            //str.append("\n");
            //layers[i] = neuralNet.getLayers().get(i).getWidth();
        }
        str.append("\n]\n");
        
        str.append("}\n");
        return str.toString();
    }
    public void show(){
        System.out.println(this.neuralNet);
    }
    
    
    public void write(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(neuralNet);
        } catch (Exception e){}
    }
    
    public NeuralNetwork read(String file){
        NeuralNetwork nnet = null;
        try (ObjectInputStream ois = new PrintUIDs(new FileInputStream(file))) {
            nnet = (NeuralNetwork) ois.readObject();
            System.out.println(" LOAD success = " + (nnet==null));
        } catch (Exception e){}
        return nnet;
    }
    
    public void load(String file){
        neuralNet = (FeedForwardNetwork) read(file);
        /*trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.0000000004f);
        trainer.setLearningRate((float) this.learningRate);
        trainer.setMomentum(0.9f);        
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(2000);        */
    }
    public static String concat(List<String> lines){
        StringBuilder str = new StringBuilder();
        for(String line: lines) str.append(line);
        return str.toString();
    }
    
    public static FeedForwardNetwork create(String file){
        List<String> lines = TextFileReader.readFile(file);
        FeedForwardNetwork network = DeepNettsIO.fromJson(DeepNetts.concat(lines));
        return network;
    }
    
    public static void main(String[] args){
        
        
        
        
        
        DataList  data = new DataList();
        data.add(new DataEntry(new float[]{1,1,1,1,1,1}, new float[]{1,0}));
        data.add(new DataEntry(new float[]{2,2,2,2,2,2}, new float[]{0,1}));
        data.add(new DataEntry(new float[]{5,5,5,5,5,5}, new float[]{0,1}));
        data.add(new DataEntry(new float[]{7,7,7,7,7,7}, new float[]{1,0}));
        
        data.show();
        
        
        DataList data2 = DataList.fromCSV("sample.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7,8});
        
        
        FeedForwardNetwork network = DeepNetts.create("class6_2.json");
        DeepNettsTrainer   trainer = new DeepNettsTrainer(network);
        
        trainer.updateLayers();
        
        //trainer.train(data2, 128);
        
        /*
        DeepNetts dn = new DeepNetts();
        
        dn.activation(ActivationType.RELU);
        dn.outputActivation(ActivationType.SOFTMAX);

        
        dn.init(new int[]{6,12,3});
        
        
        dn.inputTransformer = new DataTransformer();
        dn.inputTransformer.add(6, 0, 112);
        
        //dn.outputTransformer = new DataTransformer();
        //dn.outputTransformer.add(2, 0, 1);
        
        dn.show();
        //DataSet ds = dn.convert(data2);        
        //System.out.println(ds);        
        //System.out.println(dn.getJson());        
        //dn.train(data2, 24);        
        
        //System.out.println(dn.getJson());        
        //TextFileWriter.write("class6_2.json", Arrays.asList(dn.getJson()));
        
        
        
        List<String> lines = TextFileReader.readFile("class6_2.json");
        FeedForwardNetwork net = DeepNettsIO.fromJson(DeepNetts.concat(lines));
        
        System.out.println(net);
        
        DeepNettsTrainer tr = new DeepNettsTrainer(net);
        DataSet dset = dn.convert(data2);
        tr.train(dset);
        
        //String jsonDN = DeepNettsIO.toJson(dn.neuralNet);
        //System.out.println(jsonDN);
        
        //FeedForwardNetwork nnet = DeepNettsIO.createFeedForwardNetworkFromJson(jsonDN);        
        //System.out.println(nnet);
        
        /*
        System.out.println("\n\n\n---- start new training");
        DeepNetts d2 = new DeepNetts();
        
        d2.neuralNet = dn.neuralNet;
        System.out.println(" BEFORE : " + d2.getJson());
        d2.initTrainer();
        System.out.println(" AFTER : " + d2.getJson());
        //d2.train(data2, 1);
        
        DeepNettsTrainer tr = new DeepNettsTrainer(dn.neuralNet);
        DataSet dset = dn.convert(data2);
        
        tr.train(dset);*/
//System.out.println(dn.getJson());        
        //TextFileWriter.write("class6.json", Arrays.asList(dn.getJson()));                
        //dn.write("testnet.deepnetts");
        
        
        //DeepNetts deep = new DeepNetts();
        //deep.load("testnet.deepnetts");
        //deep.show();
        /*DataTransformer t = new DataTransformer();
        t.add(12, -5, 114);
        t.show();
        
        System.out.println(t.toArray());*/
    }
}
