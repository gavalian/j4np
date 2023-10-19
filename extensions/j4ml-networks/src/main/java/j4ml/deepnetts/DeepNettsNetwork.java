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
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.ejml.EJMLModel;
import j4np.utils.io.TextFileWriter;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author gavalian
 */
public class DeepNettsNetwork {
    
    FeedForwardNetwork        neuralNet = null;
    BackpropagationTrainer     trainer  = null;
    
    ActivationType hiddenActivation = ActivationType.RELU;
    ActivationType   lastActivation = ActivationType.SIGMOID;
    
    LossType           lossFunction = LossType.MEAN_SQUARED_ERROR;
    double learningRate = 0.001;
    
    public DeepNettsNetwork(){
        
    }
    
    public final DeepNettsNetwork outputActivation(ActivationType type){
        this.lastActivation = type; return this;
    }
    
    public final DeepNettsNetwork lossType(LossType type){
        this.lossFunction = type; return this;
    }
    
    public final DeepNettsNetwork learningRate(double rate){
        this.learningRate = rate; return this;
    }
    
    public final DeepNettsNetwork activation(ActivationType type){
        this.hiddenActivation = type; return this;
    }
    
    public final DeepNettsNetwork init(int... layers){
        
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
        trainer.setMaxError(0.000004f);
        trainer.setLearningRate((float) this.learningRate);
        trainer.setMomentum(0.9f);
        
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.setMaxEpochs(2000);
        
        return this;
    }

    public static DataSet convert(DataList list, boolean transform){
        
        int nInputs = list.getList().get(0).getFirst().length;
        int nOutputs = list.getList().get(0).getSecond().length;        
        TabularDataSet  dataset = new TabularDataSet(nInputs,nOutputs);
        
        while(list.getList().isEmpty()==false){
            //for(int k = 0; k < list.getList().size(); k++){
            DataEntry entry = list.getList().get(list.getList().size()-1);
            list.getList().remove(list.getList().size()-1);            
            float[]  inBuffer = entry.floatFirst();
            float[] outBuffer = entry.floatSecond();
            dataset.add(new TabularDataSet.Item(inBuffer, outBuffer));                
        }
        
        String[] names = DataSetUtils.generateNames(nInputs, nOutputs);
        dataset.setColumnNames(names);
        return dataset;
    }
    
    public void evaluate(DataList ds){
        
        for(int i = 0; i < ds.getList().size(); i++){
            float[]  input  = ds.getList().get(i).floatFirst();
            float[]  output = neuralNet.predict(input);
            float[]  result = new float[output.length];
            for(int r = 0; r < result.length; r++) { result[r] = output[r];}
            ds.getList().get(i).setInfered(result);
        }
       /* DataList p = new DataList();
        DataSet set = this.convert(ds);
        Iterator iter = set.iterator();
        
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            float[]  input  = item.getInput().getValues();
            float[] desired = item.getTargetOutput().getValues();
            float[]  output = neuralNet.predict(input);
            double[]  first = DataArrayUtils.toDouble(input);
            double[] second = DataArrayUtils.toDouble(output);
            p.add(new DataEntry(first,second));
        }*/
        //return p;
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
    
    public void train(DataSet trSet, int nEpochs){
        
        //trainer.setCheckpointEpochs(100);
        trainer.setMaxEpochs(nEpochs);
        System.out.println("adding listener ");
        
        DeepNettsClassifier.ProgressListener pl = new DeepNettsClassifier.ProgressListener(nEpochs);
        
        LogManager.shutdown();
        
        trainer.addListener(pl);
        System.out.println("*********");
        System.out.println("* Start Training Network with data set size = " + trSet.getItems().size());
        System.out.println("* Trainer learning rate  = " + this.learningRate);
        System.out.println("*********");
        //trainer.setMaxEpochs(25);
        //for(int k = 0; k < nEpochs/25; k++){ 
        trainer.train(trSet);       
        
        System.out.println("*********");
        System.out.println("* Finished Training" );
        System.out.println("* " + pl.statusString(trainer));
        System.out.println("*********");
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
            str.append(String.format("%e", array[i]));
        } str.append("]");
        return str.toString();
    }
    
    public String  toArray(double[] array){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < array.length; i++){
            if(i!=0) str.append(",");
            str.append(String.format("%e", array[i]));
        } str.append("]");
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
    
    public static EJMLModel getNetwork(List<String> json){
        StringBuilder str = new StringBuilder();
        for(String line : json) str.append(line);
        return DeepNettsNetwork.getNetwork(str.toString());
    }
    
    public static EJMLModel getNetwork(String json){
        DeepNettsNetwork network = new DeepNettsNetwork();
        network.fromJson(json);
        List<String>  networkContent = network.getNetworkStream();
        EJMLModel              model = EJMLModel.create(networkContent);
        return model;
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
    public void fromJson(String json){
        
        JsonObject jsonObject = (JsonObject) Json.parse(json);
        if(jsonObject.get("layers")==null){
            System.out.println("--- error"); 
        }
        if(jsonObject.get("weights")==null){
            System.out.println("--- error");
        }
        if(jsonObject.get("biases")==null){
            System.out.println("--- error");
        }
        
        JsonArray layers = jsonObject.get("layers").asArray();
        int   layersSize = layers.size();
        int[]    layersLayout = new int[layersSize];
        
        for(int i = 0; i < layersSize; i++){
            layersLayout[i] = layers.get(i).asInt();
            //System.out.println(i + " " + layersLayout[i]);
        }
        
        this.init(layersLayout);
        
        JsonArray weights = jsonObject.get("weights").asArray();
        int wSize = weights.size();
        
        for(int i = 0; i < wSize; i++){
            JsonArray  item = weights.get(i).asArray();
            float[]  weight = new float[item.size()];
            for(int k = 0; k < weight.length; k++) weight[k] = item.get(i).asFloat();
            this.neuralNet.getLayers().get(i+1).getWeights().setValues(weight);
        }
        
        JsonArray biases = jsonObject.get("biases").asArray();
        int bSize = biases.size();
        
        for(int i = 0; i < bSize; i++){
            JsonArray  item = biases.get(i).asArray();
            float[]  bias = new float[item.size()];
            for(int k = 0; k < bias.length; k++) bias[k] = item.get(i).asFloat();
            this.neuralNet.getLayers().get(i+1).setBiases(bias);
        }
        
        System.out.println(this.neuralNet);
    }
    
    public String  getJson(){
        StringBuilder str = new StringBuilder();
        str.append("{\n\"layers\": ");

        int  nLayers = neuralNet.getLayers().size();
        int[] layers = new int[nLayers];
        
        for(int i = 0; i < nLayers; i++){
            layers[i] = neuralNet.getLayers().get(i).getWidth();
            neuralNet.getLayers().get(i).init();
        }
        
        str.append(toArray(layers));
        str.append(",\n");
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
    
    public static void main(String[] args){
        DeepNettsNetwork n = new DeepNettsNetwork();
        n.activation(ActivationType.RELU)
                .outputActivation(ActivationType.SIGMOID)
                .init(new int[]{2,2,3});
        //DataList list = new DataList();
        //list.add(new DataEntry(new double[]{0.5,0.5,0.5,0.7,0.7,0.7},new double[]{0.7,0.8,0.9}));
        //n.train(list, 25);
        System.out.println(n.getJson());
        
        DeepNettsNetwork nr = new DeepNettsNetwork();
        nr.fromJson(n.getJson());
        //List<String> network = n.getNetworkStream();
    }
}
