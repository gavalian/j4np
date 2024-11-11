/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.deepnetts;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.AbstractLayer;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import j4ml.data.EntryTransformer;
import j4np.utils.io.TextFileReader;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import java.io.File;
import java.util.List;



/**
 *
 * @author gavalian
 */
public class DeepNettsIO {
    
    
    public static String  toArray(int[] array){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < array.length; i++){
            if(i!=0) str.append(",");
            str.append(array[i]);
        } str.append("]");
        return str.toString();
    }
    
    public static String  toArray(float[] array){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < array.length; i++){
            if(i!=0) str.append(",");
            str.append(String.format("%.12f", array[i]));
        } str.append("]");
        return str.toString();
    }
    
    public static String  toArray(double[] array){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < array.length; i++){
            if(i!=0) str.append(",");
            str.append(String.format("%.12f", array[i]));
        } str.append("]");
        return str.toString();
    }
    public static String getActivationString(ActivationType type){
        if(type == ActivationType.RELU) return "RELU";
        if(type == ActivationType.SIGMOID) return "SIGM";
        if(type == ActivationType.TANH) return "TANH";
        if(type == ActivationType.LINEAR) return "LIN";
        if(type == ActivationType.SOFTMAX) return "SOFTMAX";        
         return "UNKNOWN";
    }
    
    public static String toJson(FeedForwardNetwork network, EntryTransformer transformer){
        StringBuilder str = new StringBuilder();
        str.append("{\n\"architecture\": ");

        int  nLayers = network.getLayers().size();
        int[] layers = new int[nLayers];
        
        for(int i = 0; i < nLayers; i++){
            layers[i] = network.getLayers().get(i).getWidth();
            //neuralNet.getLayers().get(i).init();
        }
        str.append(toArray(layers)).append(",");
        
        if(transformer!=null){
            str.append("\n\"input\": ").append(transformer.input().toArray());
            str.append(",");
        }
        if(transformer!=null){
            str.append("\n\"output\": ").append(transformer.output().toArray());
            str.append(",");
        }
        str.append("\n\"hiddenActivation\":").append(String.format("\"%s\",", 
                DeepNettsIO.getActivationString(network.getLayers().get(1).getActivationType())));
        str.append("\n\"outputActivation\":").append(String.format("\"%s\",", 
                DeepNettsIO.getActivationString(network.getLayers().get(network.getLayers().size()-1).getActivationType())));

        str.append("\n");
        str.append("\"weights\": [\n");
        for(int i = 1; i < nLayers; i++) 
        {
            if(i!=1) str.append(",\n");
            //Tensor t = neuralNet.getLayers().get(i).getWeights();
            //System.out.println( i + " + " + (t==null));
            str.append(toArray(network.getLayers().get(i).getWeights().getValues()));
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
            str.append(toArray(network.getLayers().get(i).getBiases()));
            //str.append("\n");
            //layers[i] = neuralNet.getLayers().get(i).getWidth();
        }
        str.append("\n]\n");
        
        str.append("}\n");
        return str.toString();

    }
    
    public static ActivationType createFromString(String activation){
        switch(activation){
            case    "RELU": return ActivationType.RELU;
            case    "TANH": return ActivationType.TANH;
            case     "LIN": return ActivationType.LINEAR;
            case    "SIGM": return ActivationType.SIGMOID;
            case "SOFTMAX": return ActivationType.SOFTMAX;            
            default: return null;
        }
    }
    
    /**
     * Paring a JSON file produced by this package, and building a FeedForwardNetwork
     * with all the existing weights, for transfer learning.
     * @param json
     * @return FeedForwardNetwork
     */
    public static FeedForwardNetwork fromJson(String json){
        
        JsonObject object = (JsonObject) Json.parse(json); 
        JsonArray    arch = object.get("architecture").asArray();
        int[] architecture = new int[arch.size()];
        for(int r = 0; r < arch.size(); r++) architecture[r] = arch.get(r).asInt();
        FeedForwardNetwork.Builder builder = FeedForwardNetwork.builder();
        builder.addInputLayer(architecture[0]);

        ActivationType hiddenActivation = DeepNettsIO.createFromString(object.get("hiddenActivation").asString());
        ActivationType   lastActivation = DeepNettsIO.createFromString(object.get("outputActivation").asString());

        for(int i = 1; i < architecture.length-1; i++){
            builder.addFullyConnectedLayer(architecture[i], hiddenActivation);            
        }        
        builder.addOutputLayer(architecture[architecture.length-1], lastActivation)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .randomSeed(456);
        FeedForwardNetwork network = builder.build();
        
        
        JsonArray  weights = object.get("weights").asArray();
        JsonArray   biases = object.get("biases").asArray();
        
        //network.getTrainer();
        for(AbstractLayer layer : network.getLayers()) layer.init();
        
        for(int i = 1; i < network.getLayers().size(); i++){
            try {
                System.out.printf("Layer %4d - weigths = %12d, biases = %d with weigths = %d\n",i,
                    network.getLayers().get(i).getWeights().size(),network.getLayers().get(i).getBiases().length,
                    weights.get(i-1).asArray().size());
                int wSize = network.getLayers().get(i).getWeights().size();
                int bSize = network.getLayers().get(i).getBiases().length;
                for(int w = 0; w < wSize; w++) network.getLayers().get(i).getWeights().set(w,weights.get(i-1).asArray().get(w).asFloat());
                for(int b = 0; b < bSize; b++) network.getLayers().get(i).getBiases()[b] = biases.get(i-1).asArray().get(b).asFloat();
            } catch (Exception e) {
                System.out.println(" error with layer " + i);
            }
        }
        return network;
    }
    
    public static String concat(List<String> lines){
        StringBuilder str = new StringBuilder();
        for(String line: lines) str.append(line);
        return str.toString();
    }
    
    public static EntryTransformer getTransformer(String file){
        List<String> lines = TextFileReader.readFile("class6_2.json");
        return DeepNettsIO.getTransformerFromJson(DeepNettsIO.concat(lines));
    }
    
    public static EntryTransformer getTransformerFromJson(String json){
        EntryTransformer transformer = new EntryTransformer();
        JsonObject object = (JsonObject) Json.parse(json);
        if(object.get("input")!=null){
            JsonArray   input = object.get("input").asArray();
            int size = input.size()/2;
            for(int i = 0; i < size; i++){
                transformer.input().add(input.get(2*i).asDouble(),input.get(2*i+1).asDouble());
            }
        }
        if(object.get("output")!=null){
            JsonArray   output = object.get("output").asArray();
            int size = output.size()/2;
            for(int i = 0; i < size; i++){
                transformer.output().add(output.get(2*i).asDouble(),output.get(2*i+1).asDouble());
            }
        }
        return transformer;
    }
    
    public static void show(FeedForwardNetwork network){
        for(int i = 1; i < network.getLayers().size(); i++){
            System.out.printf("Layer %4d : ", i);
            for(int w = 0; w < network.getLayers().get(i).getWeights().size(); w++)
                System.out.printf("%9.5f ", network.getLayers().get(i).getWeights().get(w));
            System.out.println();
        }
    }
}
