/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.utils;

import j4np.instarec.network.DataExtractor;
import j4np.instarec.network.DataExtractor.DataPair;
import j4np.instarec.utils.ActivationFunction.ActivationRELU;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import j4np.utils.io.TextFileReader;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author gavalian
 */
public class NeuralModel {

    public static class DataRange {
        public float rMin = 0.0f;
        public float rMax = 0.0f;
        public float revLength = 0.0f;
        public DataRange(double min, double max){ 
            rMin = (float) min; rMax = (float) max;
            revLength = 1.0f/(rMax-rMin);
        }        
        public float value(float vt){ return (vt-rMin)*revLength;}
        public float transform(float value){
            return value*(rMax-rMin)+rMin;
        }
        @Override
        public String toString(){return String.format("%12.5f , %12.5f", rMin,rMax);}
    }
    
    private SimpleMatrix[] LAYERS = null;
    private SimpleMatrix[] BIASES = null;
    
    private int inputSize;
    private int outputSize;
    
    private DataRange[]   inputRange = null;
    private DataRange[]  outputRange = null;
    private int[]       architecture = null;
    
    private ActivationFunction hiddenActivation = new ActivationFunction.ActivationRELU();
    private ActivationFunction outputActivation = new ActivationFunction.ActivationSIGM();
    
    
    public NeuralModel(){ }
    
    public static NeuralModel jsonFile(String file){
        List<String> lines = TextFileReader.readFile(file);
        NeuralModel  model = new NeuralModel();
        String json = model.concat(lines);        
        model.build(json);
        System.out.println(model.summary());
        return model;
    }
    
    public static NeuralModel json(String json){
        NeuralModel  model = new NeuralModel();
        model.build(json);
        //System.out.println(model.summary());
        return model;
    }
    
    public static NeuralModel archiveFile(String archive, String file, int run, String variation){
        File f = new File(archive);
        if(f.exists()==false){System.out.println("achive:: (error) archive does not exist : " + archive); return null;}
        ArchiveProvider ap = new ArchiveProvider(archive);
        int refrun = ap.findEntry(run);
        String archiveFile = String.format("network/%d/%s/%s",refrun,variation,file);
        //System.out.println(" looking for file : " + archiveFile);
        if(ArchiveUtils.hasFile(archive, archiveFile)==false) { System.out.println("achive:: file does not exist : " + archiveFile); return null;}
        List<String> networkContent = ArchiveUtils.getFileAsList(archive,archiveFile);
        StringBuilder str = new StringBuilder();
        for(String content : networkContent) str.append(content);
        return NeuralModel.json(str.toString());
    }
    
    public NeuralModel setActivation(ActivationFunction hidden, ActivationFunction output){
        this.hiddenActivation = hidden; this.outputActivation = output; return this;
    }
    
    public void setInputRange(double min, double max){
        inputRange = new DataRange[inputSize];
        for(int i = 0; i < inputSize; i++){ inputRange[i] = new DataRange(min,max);}
    }
    
    public void setOutputRange(double min, double max){
        outputRange = new DataRange[outputSize];
        for(int i = 0; i < outputSize; i++){ outputRange[i] = new DataRange(min,max);}
    }
    
    public float[] getInput(float[] values){
        //System.out.println(" get input ");
        float[] input = new float[values.length];
        for(int i = 0; i < input.length; i++) input[i] = inputRange[i].value(values[i]);
        return input;
    }
    
    public void transformOutput(float[] output){
        for(int i = 0; i < output.length; i++) output[i] = outputRange[i].transform(output[i]);
    }
    
    
    protected SimpleMatrix createWeights(int inputs, int outputs, JsonArray array){
        float[][] Layer = new float[inputs][outputs];
        int counter = 0;
        for(int i = 0; i < inputs; i++){       
            for(int o = 0; o < outputs; o++){                
                    Layer[i][o] = array.get(counter).asFloat();
                counter++;
            }
        }
        return new SimpleMatrix(Layer);
    }
    
    public int getLabel(float[] output){
        float max = output[0]; int index = 0;
        for(int i = 1; i < output.length; i++)
            if(output[i]>max){max = output[i]; index = i;}
        return index;
    }
    
    public void benchmark(int iterations){
        
        Random r = new Random();
        float[]  output = new float[outputSize];
        float[]   input = new float[inputSize];
        for(int k = 0; k < input.length; k++) input[k] = r.nextFloat();
        //List<float[]> inputs = new ArrayList<>();
        for(int j = 0 ; j < 10; j++){
            int counter = 0; 
            
            long then = System.currentTimeMillis();
            for(int i = 0; i < iterations; i++){
                this.predict(input, output); counter++;            
            }
            long  now = System.currentTimeMillis();
            System.out.printf("try %5d, iterations = %d, time = %d msec\n",j+1, counter, now-then);
        }
    }
    
    protected SimpleMatrix createBiases(int inputs, JsonArray array){
        float[] biases = new float[inputs];        
        int counter = 0;
        for(int i = 0; i < inputs; i++){
                biases[i] = array.get(i).asFloat();
        }
        return new SimpleMatrix(new float[][]{biases});
    }
    
    public double getWeightSumm(int layer){
        return LAYERS[layer].determinant();
    }
    
    protected void build(String json){
        
        JsonObject object = (JsonObject) Json.parse(json);        
        JsonArray    arch = object.get("architecture").asArray();
        //int[]      layers = new int[arch.size()];
        this.architecture = new int[arch.size()];
        for(int r = 0; r < arch.size(); r++) this.architecture[r] = arch.get(r).asInt();
        inputSize = architecture[0]; outputSize = architecture[architecture.length-1];
        
        // - define the input translations
        if(object.get("input")!=null){
            JsonArray   input = object.get("input").asArray();
            if( input.size()==2){this.setInputRange(input.get(0).asDouble(), input.get(1).asDouble());} else {
                if(input.size()==2*inputSize){ 
                    inputRange = new DataRange[inputSize];
                    for(int i = 0; i < inputSize; i++){ 
                        inputRange[i] = new DataRange(input.get(i*2).asDouble(),input.get(i*2+1).asDouble());  
                    }
                }
            }
        } else { this.inputRange = null; }
        
        if(object.get("hiddenActivation")!=null){
            this.hiddenActivation = NeuralModel.getActivationFunction(object.get("hiddenActivation").asString());
        }
        
        if(object.get("outputActivation")!=null){
            this.outputActivation = NeuralModel.getActivationFunction(object.get("outputActivation").asString());
        }
        
        if(object.get("output")!=null){
            JsonArray  output = object.get("output").asArray();
            if(output.size()==2){this.setOutputRange(output.get(0).asDouble(), output.get(1).asDouble());}
            else if(output.size()==outputSize*2){
                outputRange = new DataRange[outputSize];
                for(int i = 0; i < outputSize; i++){ 
                    outputRange[i] = new DataRange(output.get(i*2).asDouble(),output.get(i*2+1).asDouble());
                }
            }
        } else {this.outputRange = null;}
       // int[]      layers = new int[arch.size()];                          
        
        LAYERS = new SimpleMatrix[architecture.length-1];
        BIASES = new SimpleMatrix[architecture.length-1];
        
        JsonArray  weights = object.get("weights").asArray();
        JsonArray   biases = object.get("biases").asArray();
        
        for(int l = 0; l < architecture.length-1; l++){
            int nIn = architecture[l]; int nOut = architecture[l+1];
            LAYERS[l] = this.createWeights(nIn, nOut, weights.get(l).asArray());
            BIASES[l] = this.createBiases(nOut, biases.get(l).asArray());
        }
    }
    
    public static ActivationFunction getActivationFunction(String name){
        return switch (name) {
            case "RELU" -> new ActivationFunction.ActivationRELU();
            case "ReLU" -> new ActivationFunction.ActivationRELU();
            case "SIGM" -> new ActivationFunction.ActivationSIGM();
            case "TANH" -> new ActivationFunction.ActivationTANH();
            case "LIN" -> new ActivationFunction.ActivationLIN();
            case "SOFTMAX" -> new ActivationFunction.ActivationSOFTMAX();
            default -> null;
        };
    }
    
    public String info(){
        StringBuilder str = new StringBuilder();
        return "6/12/3";
    }
    public String summary(){
        
        StringBuilder str = new StringBuilder();
        
        //str.append(String.format(" Inputs : %d\n",inputSize));
        //str.append(String.format("Outputs : %d\n",outputSize));
        
        for(int i = 0; i < inputSize; i++)
            if(inputRange!=null)
                str.append(String.format(" Input Range : %s\n", 
                        this.inputRange[i].toString()));        
        for(int i = 0; i < outputSize; i++)
            if(outputRange!=null)
                str.append(String.format("Output Range : %s\n", 
                        this.outputRange[i].toString()));
        
        for(int i = 0; i < LAYERS.length; i++){
            str.append(String.format("{ layer %4d : activation %12s, parameters %12d , w = %24e , b = %24e }\n",
                    i, i!=LAYERS.length-1?hiddenActivation.name():outputActivation.name(),
                    LAYERS[i].numCols()*LAYERS[i].numRows(),
                    LAYERS[i].elementSum(),BIASES[i].elementSum()));
        }
        
        return str.toString();
    }
    
    public static SimpleMatrix applyActivation(ActivationFunction func, SimpleMatrix matrix){
        SimpleMatrix output = new SimpleMatrix(matrix);
        for (int i = 0; i < output.numRows(); ++i)
            for (int j = 0; j < output.numCols(); ++j){
                output.set(i, j, func.apply((float) output.get(i, j)));
            }
        return output;
    }
    
    public static SimpleMatrix applyActivationNoCopy(ActivationFunction func, SimpleMatrix output){
        for (int i = 0; i < output.numRows(); ++i)
            for (int j = 0; j < output.numCols(); ++j){
                output.set(i, j, func.apply((float) output.get(i, j)));
            }
        return output;
    }
    
    public void forward(){
        
    }
    
    public void predict(float[] input, float[] output){
                
        float[] inputInRange = (inputRange==null)?input:getInput(input);
        //float[] inputInRange = getInput(input);
        
        //String response = inputRange==null?"aaa":"bbb";
        
        //System.out.println(response);
        SimpleMatrix matrix = new SimpleMatrix(new float[][] {inputInRange});
        //System.out.println("==>  INPUT = " + Arrays.toString(inputInRange));
        
        for(int i = 0; i < LAYERS.length; i++){
            //matrix = NeuralModel.applyActivation(hiddenAcctivation, matrix.mult(LAYERS[i]).plus(BIASES[i]));
            //System.out.println("BEFORE \n" + matrix);
            if(i == LAYERS.length - 1){
                matrix = NeuralModel.applyActivationNoCopy(outputActivation, matrix.mult(LAYERS[i]).plus(BIASES[i]));
            } else
                matrix = NeuralModel.applyActivationNoCopy(hiddenActivation, matrix.mult(LAYERS[i]).plus(BIASES[i]));
            //System.out.println("AFTER \n" + matrix);
             //System.out.println(matrix);
            //System.out.println(" AFTER LAYER " + i + " \n" + matrix);
        }
        
        //System.out.println(matrix);
        if(this.outputActivation.type()==5){
            float accumulate = 0.0f;
            for(int i = 0; i < output.length; i++){
                output[i] = (float) matrix.get(0, i);
                accumulate += output[i];
            }
            for(int j = 0; j < output.length; j++) output[j] = output[j]/accumulate;
        } else {
            if(this.outputRange==null){
                for(int j = 0; j < output.length; j++) output[j] = (float) matrix.get(0, j);
            } else {
                for(int j = 0; j < output.length; j++) output[j] = outputRange[j].transform((float) matrix.get(0, j));
            }
        }
    }
    
    public String concat(List<String> lines){
        StringBuilder str = new StringBuilder();
        for(String line: lines) str.append(line);
        return str.toString();
    }
    
    public static void main(String[] args){        
        
        NeuralModel model = NeuralModel.jsonFile("class12classifier_8.json");
        System.out.println(model.summary());
        /*
        List<DataPair>  pairs = DataExtractor.csv("sample.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7,8});
        float[] output = new float[3];
        int counter = 0;
        long then = System.currentTimeMillis();
        for(int i = 0; i < pairs.size(); i++){
            //System.out.println("---------------------");
            model.predict(pairs.get(i).input(), output);
            //if(Float.isNaN(output[0])){System.out.println("----ooops " + Arrays.toString(pairs.get(i).output()) + Arrays.toString(pairs.get(i).input())); counter++;}
            
            //System.out.println(Arrays.toString(pairs.get(i).input()) 
            //        + "  " + Arrays.toString(pairs.get(i).output()) + " " + 
            //        Arrays.toString(output));
        }
        long now = System.currentTimeMillis();
        System.out.printf("  processed = %d , NaNs = %d  - %f  %d\n",pairs.size(), counter, ((double) counter)/pairs.size(),now-then);*/
        /*
        NeuralModel model = NeuralModel.jsonFile("etc/trackregression12_1_n.json");
        EJMLModel    ejml = new EJMLModel("etc/trackregression12_1_n.network");
        
        float[][] data = ModelData.regression;
        //System.out.println(Arrays.toString(data[0]));
        
        float[]  output = new float[3];
        float[] output2 = new float[3];
        for(int i = 0; i < 3; i++){
            ejml.feedForwardTanhLinear(data[i], output);
            System.out.println(Arrays.toString(output) + " ***>>> " + Arrays.toString(data[i]));
        }
        
        model.setActivation(new ActivationFunction.ActivationTANH(), new ActivationFunction.ActivationLIN());
        System.out.println("\n\n----------------\n new model \n");        
        for(int i = 0; i < 3; i++){
            model.predict(data[i], output2);   

            System.out.println(Arrays.toString(output2) + " ***>>> " + Arrays.toString(data[i]));
        }
        */
        //model.benchmark(12500000);
        /*
        NeuralModel  model = new NeuralModel();
        
        List<String> lines = TextFileReader.readFile("classifier.json");
        String json = model.concat(lines);
        //System.out.println(json);
        
        model.build(json);
        //model.setActivation(new ActivationFunction.ActivationRELU(), new ActivationFunction.ActivationSOFTMAX());
        System.out.println(model.summary());
        float[] output = new float[3];
        for(int i = 0; i < ModelData.classifier.length; i++){
            System.out.println("----");
            model.predict(ModelData.classifier[i], output);
            System.out.println(Arrays.toString(output)+" <<<**** " + Arrays.toString(ModelData.classifier[i]));
        }
        
        NeuralModel model2 = NeuralModel.jsonFile("etc/trackregression12_1_n.json");
        for(int i = 0; i < ModelData.regression.length; i++){
            System.out.println("----");
            model2.predict(ModelData.regression[i], output);
            System.out.println(Arrays.toString(output)+" <<<**** " + Arrays.toString(ModelData.regression[i]));
        }
        */
        /*
        List<float[]> inputs = Arrays.asList(
                new float[] {0.16340f, 0.16964f, 0.18080f, 0.19866f, 0.27902f, 0.29688f},
                new float[] {0.12819f, 0.15402f, 0.13839f, 0.16592f, 0.20164f, 0.23075f});
        
        float[] output = new float[3];
        float[]  input = new float[]{-5,48,56,57,68,114};
        
        EJMLModel ejml = new EJMLModel("trackclassifier6.network");
        System.out.println("---- EJML inference ---");
        for(int i = 0; i < inputs.size(); i++){
            ejml.feedForwardSoftmax(inputs.get(i), output);
            System.out.println(Arrays.toString(inputs.get(i)) + " ==> " + Arrays.toString(output));
        }
        
        System.out.println("\n\n\n\n---- NETWORK inference ---");
        for(int i = 0; i < inputs.size(); i++){
            //for(int k = 0; k < inputs.get(i).length; k++) inputs.get(i)[k] *= 112.0; 
            model.predict(inputs.get(i), output);
            System.out.println(Arrays.toString(inputs.get(i)) + " ==> " + Arrays.toString(output));
        }
        
        //model.predict(input, output);
        
        
        System.out.println(ejml.LAYERS[0]);
        System.out.println(model.LAYERS[0]);
        */
        //model.benchmark(12500000);
    }
}