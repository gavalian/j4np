package j4np.instarec.utils;

import j4np.utils.FileUtils;
import j4np.utils.io.TextFileWriter;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejml.simple.SimpleMatrix;

/**
 * @author Andru Quiroga (aqui-CNU)
 * Tweaks and Adjustments by Gagik Gavalian (Jlab)
 */

public class EJMLModel {

    public enum ModelType {
       SOFTMAX,LINEAR, TANH_LINEAR, RELU_LINEAR,
       SIGMOID_LINEAR, RELU_SIGMOID
    };
    
    private SimpleMatrix[] LAYERS = null;
    private SimpleMatrix[] BIASES = null;
    private int inputSize;
    private int outputSize;
    

    private ModelType ejmlModelType = ModelType.LINEAR;

    public EJMLModel(String filePath) {
        try { buildMatrices(filePath);
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public EJMLModel(String filePath, ModelType type) {
        try { 
            buildMatrices(filePath);
        } catch (IOException e) { e.printStackTrace(); }
        ejmlModelType = type;
    }
    
    
    private EJMLModel() {
        
    }
    
    public static EJMLModel create(List<String> lines){
        EJMLModel instance = new EJMLModel();
        instance.buildMatriciesFromList(lines);
        return instance;
    }
    
    public EJMLModel  setType(ModelType type){
        this.ejmlModelType = type; return this;
    }
    
    public EJMLModel setType(String type){
        if(type.compareTo("SOFTMAX")==0){
            this.ejmlModelType = ModelType.SOFTMAX; return this;
        }
        if(type.compareTo("LINEAR")==0){
            this.ejmlModelType = ModelType.LINEAR; return this;
        }
        System.out.println("[EJML] error : unknown network type : " + type);
        return this;
    }
    
    public static int getLabel(float[] output){
        int index = 0; float max = output[0];
        for(int i = 0; i < output.length; i++)
            if(output[i]>max){ max = output[i]; index = i;}
        return index;
    }
    
    public void printSummary(){
        System.out.println("**********");
        System.out.println("[EJMLModel] EJML Neural Network : " + ejmlModelType);
        System.out.println("[EJMLModel]              INPUTS : " + this.getInputSize());
        System.out.println("[EJMLModel]             OUTPUTS : " + this.getOutputSize());
        System.out.println("[EJMLModel]       HIDDEN LAYERS : " + (this.LAYERS.length-2));
        System.out.println("**********");
        System.out.println();
    }
    
    private static SimpleMatrix elementwiseApplyReLU(SimpleMatrix input) {  // Credits to stanfordnlp
        SimpleMatrix output = new SimpleMatrix(input);
        for (int i = 0; i < output.numRows(); ++i)
            for (int j = 0; j < output.numCols(); ++j){
                output.set(i, j, Math.max(0, output.get(i, j)));
                //System.out.println(i + " " + j + " = " + output.get(i,j) + " " + Math.max(0, output.get(i,j)));
            }
        return output;
    }

    private static SimpleMatrix elementwiseApplySigmoid(SimpleMatrix input) {  // Credits to stanfordnlp
        SimpleMatrix output = new SimpleMatrix(input);
        for (int i = 0; i < output.numRows(); ++i)
            for (int j = 0; j < output.numCols(); ++j)
                output.set(i, j, 1.0 / (1.0 + Math.exp(-output.get(i, j))));
        return output;
    }

    private static SimpleMatrix elementwiseApplyTanh(SimpleMatrix input) {  // Credits to stanfordnlp
        SimpleMatrix output = new SimpleMatrix(input);
        for (int i = 0; i < output.numRows(); ++i)
            for (int j = 0; j < output.numCols(); ++j)
                output.set(i, j, Math.tanh(output.get(i, j)));
        return output;
    }
    
    private static SimpleMatrix elementwiseApplyLinear(SimpleMatrix input) {  // Credits to stanfordnlp
        SimpleMatrix output = new SimpleMatrix(input);
        for (int i = 0; i < output.numRows(); ++i)
            for (int j = 0; j < output.numCols(); ++j)
                output.set(i, j, output.get(i, j));
        return output;
    }
    
    private static SimpleMatrix ApplySoftmax(SimpleMatrix input) {   // Credits to stanfordnlp
        SimpleMatrix output = new SimpleMatrix(input);
        for (int i = 0; i < output.numRows(); ++i)
            for (int j = 0; j < output.numCols(); ++j){       
                //System.out.println(output.get(i,j) + " exp = " + Math.exp(output.get(i, j)));
                //double expv =  Math.exp(output.get(i, j));
                output.set(i, j, Math.exp(output.get(i, j)));
                //output.set(i,j,(float) expv*1.0);
                //System.out.println(" after setting = " + output.get(i, j) + " expv = " + expv);
            }
        
        //System.out.println("sum = " +  output.elementSum());
        return output.scale(1.0 / output.elementSum());
    }

    private void buildMatriciesFromList(List<String> lines) {
        List<SimpleMatrix> allLayers = new ArrayList<>();
        List<SimpleMatrix> allBiases = new ArrayList<>();
        int counter = lines.size();
        int index   = 0;
        int layer = 0;
        //System.out.println("---> loading network list size = " + lines.size());
        while(index<counter){
            String line = lines.get(index);
            String[] size = line.split(",");
            int size_in  = Integer.parseInt(size[0]);
            int size_out = Integer.parseInt(size[1]);
            //System.out.println(" layer " + layer + " size  in = " + size_in + " , out = " + size_out);
            index++;
            allLayers.add(EJMLModel.makeWeightMatrixFromList(lines, index, size_in, size_out));
            index += size_in;
            allBiases.add(EJMLModel.makeBiasesFromList(lines,index, size_out));
            index++;
            
            layer++;
        }
        
        BIASES = new SimpleMatrix[allBiases.size()];
        BIASES = allBiases.toArray(BIASES);
        
        LAYERS = new SimpleMatrix[allLayers.size()];
        LAYERS = allLayers.toArray(LAYERS);
        
        inputSize  = LAYERS[0].numRows();
        outputSize = LAYERS[LAYERS.length-1].numCols();
        
    }
    
    public String summary(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this.LAYERS.length; i++){
            if(i!=0) str.append(",");
            str.append(LAYERS[i].numRows());
        }
        return str.toString();
    }
    
    private void buildMatrices(String path) throws IOException {
        
        List<SimpleMatrix> allLayers = new ArrayList<>();
        List<SimpleMatrix> allBiases = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            int counter = 0;
            while (line != null) {
                String[] ssize = line.split(",");
                int size_in = Integer.parseInt(ssize[0]);
                int size_out = Integer.parseInt(ssize[1]);
                //System.out.println(" processing layer " + counter + " [ "
                //        + size_in + " , " + size_out + "]"
                //);
                counter++;
                allLayers.add(EJMLModel.makeWeightMatrix(br, size_in, size_out));
                allBiases.add(EJMLModel.makeBiases(br, size_out));
                line = br.readLine();
            }

            BIASES = new SimpleMatrix[allBiases.size()];
            BIASES = allBiases.toArray(BIASES);

            LAYERS = new SimpleMatrix[allLayers.size()];
            LAYERS = allLayers.toArray(LAYERS);

            inputSize = LAYERS[0].numRows();
            outputSize = LAYERS[LAYERS.length-1].numCols();
        }
    }

    private static SimpleMatrix makeBiases(BufferedReader br, int size) throws IOException {
        String[] sBiases = br.readLine().split(",");
        float[] biases = new float[size];
        for (int i = 0; i < size; i++)
            biases[i] = Float.parseFloat(sBiases[i]);

        return new SimpleMatrix(new float[][]{biases});
    }

    private static SimpleMatrix makeWeightMatrix(BufferedReader br, int numITensors, int numOTensors) throws IOException {
        float[][] Layer = new float[numITensors][numOTensors];
        for (int i = 0; i < numITensors; i++){
            String[] ss = br.readLine().split(",");
            //System.out.println(" size = " + ss.length);
            for (int j = 0; j < numOTensors; j++)
                Layer[i][j] = Float.parseFloat(ss[j]);
        }

        return new SimpleMatrix(Layer);
    }

    private static SimpleMatrix makeBiasesFromList(List<String> lines, int start, int size) {
        String[] sBiases = lines.get(start).split(",");
        float[] biases = new float[size];
        for (int i = 0; i < size; i++)
            biases[i] = Float.parseFloat(sBiases[i]);
        return new SimpleMatrix(new float[][]{biases});
    }
    
    private static SimpleMatrix makeWeightMatrixFromList(List<String> lines, int start, int numITensors, int numOTensors) {
        float[][] Layer = new float[numITensors][numOTensors];
        for (int i = 0; i < numITensors; i++){
            String[] ss = lines.get(i+start).split(",");
            //System.out.println(" size = " + ss.length);
            for (int j = 0; j < numOTensors; j++)
                Layer[i][j] = Float.parseFloat(ss[j]);
        }
        return new SimpleMatrix(Layer);
    }
        
    public int getInputSize(){
        return inputSize;
    }

    public int getOutputSize(){
        return outputSize;
    }
    /**
     * This function will evaluate the network for for given
     * network type. supports RELU/TANH/SIGMOID/LINEAR and SOFTMAX
     * @param input input array 
     * @param result output from the network
     */
    public void getOutput(float[] input, float[] result){
        //System.out.println(" doing the thing " + this.ejmlModelType);
        
        if(ejmlModelType==ModelType.SOFTMAX){
            feedForwardSoftmax(input, result);
            /*if(this.ejmlModelType==ModelType.SOFTMAX){
            boolean fix = false;
            for(int i = 0; i < result.length; i++){
                if(Float.isInfinite(result[i])||Float.isNaN(result[i])) fix = true;
            }
            
            if(fix)
                for(int i = 0; i < result.length;i++) result[i] = 0.0f;
            result[0] = 1.0f;
            }*/
            return;  
        } 
        
        switch (this.ejmlModelType){
            
            case SOFTMAX: feedForwardSoftmax(input, result); return;
            case TANH_LINEAR: feedForwardTanhLinear(input, result); return;
            case RELU_LINEAR: feedForwardReLULinear(input, result); return;
            
            default: feedForward(input, result);
        }

    }
    
    public void feedForward(float[] input, float[] results) {
        assert input.length == inputSize;

        SimpleMatrix matrix = new SimpleMatrix(new float[][] {input});
        for (int i = 0; i < LAYERS.length; i++) {
            if (i == LAYERS.length - 1)
                matrix = elementwiseApplySigmoid(matrix.mult(LAYERS[i]).plus(BIASES[i]));
            else
                matrix = elementwiseApplyReLU(matrix.mult(LAYERS[i]).plus(BIASES[i]));
        }

        for (int i = 0; i < matrix.numCols(); i++)
            results[i] = (float) matrix.get(0, i);
    }
    
    public void feedForwardReLULinear(float[] input, float[] results) {
        assert input.length == inputSize;

        SimpleMatrix matrix = new SimpleMatrix(new float[][] {input});
        for (int i = 0; i < LAYERS.length; i++) {
            if (i == LAYERS.length - 1)
                matrix = elementwiseApplyLinear(matrix.mult(LAYERS[i]).plus(BIASES[i]));
            else
                matrix = elementwiseApplyReLU(matrix.mult(LAYERS[i]).plus(BIASES[i]));
        }

        for (int i = 0; i < matrix.numCols(); i++)
            results[i] = (float) matrix.get(0, i);
    }
    
    public void feedForwardTanhLinear(float[] input, float[] results) {
        assert input.length == inputSize;

        SimpleMatrix matrix = new SimpleMatrix(new float[][] {input});
        for (int i = 0; i < LAYERS.length; i++) {
            if (i == LAYERS.length - 1)
                matrix = elementwiseApplyLinear(matrix.mult(LAYERS[i]).plus(BIASES[i]));
            else
                matrix = elementwiseApplyTanh(matrix.mult(LAYERS[i]).plus(BIASES[i]));
        }

        for (int i = 0; i < matrix.numCols(); i++)
            results[i] = (float) matrix.get(0, i);
    }
    
    
    public void feedForwardSoftmax(float[] input, float[] results) {
        assert input.length == inputSize;

        SimpleMatrix matrix = new SimpleMatrix(new float[][] {input});
        
        for (int i = 0; i < LAYERS.length; i++) {
            if (i == LAYERS.length - 1)
                matrix = ApplySoftmax(matrix.mult(LAYERS[i]).plus(BIASES[i]));
            else
                matrix = elementwiseApplyReLU(matrix.mult(LAYERS[i]).plus(BIASES[i]));
        }
        //System.out.println("doing the thing");
        for (int i = 0; i < matrix.numCols(); i++)
            results[i] = (float) matrix.get(0, i);
    }

    public int getClass(float[] output){
        double  max = output[0]; 
        int    item = 0; 
        for(int i = 0; i < output.length; i++){
            if(output[i]>max){
                max = output[i]; item = i;
            }
        }
        return item;
    }
    
    
    public static void torchToEjml(String filename, String output, int count){

        String fileContent = FileUtils.readFileAsString(filename);
        JsonObject obj = (JsonObject) Json.parse(fileContent);
        
        TextFileWriter writer = new TextFileWriter();
        writer.open(output);
        
        for(int c = 0; c < count; c++){
            String weight = String.format("model.%d.weight",c*2);
            String bias = String.format("model.%d.bias",c*2);
            
            JsonArray arrWeights = obj.get(weight).asArray();
            JsonArray arrBias = obj.get(bias).asArray();
            
            //System.out.printf(" layer %d : weights size = %d, bias size = %d\n",c*2,arrWeights.size(),arrBias.size());            
            List<double[]> vList = new ArrayList<>();
            
            for(int w = 0; w < arrWeights.size(); w++){                
                JsonArray arrItems = arrWeights.get(w).asArray();               
                //System.out.printf("\t weights %d : size = %d\n",w,arrItems.size());
                double[] buffer = new double[arrItems.size()];
                for(int b = 0; b < buffer.length; b++) buffer[b] = arrItems.get(b).asDouble();
                vList.add(buffer);
            }
            
            int rows = vList.get(0).length;
            int cols = vList.size();
            String hline = String.format("%d,%d", rows,cols);
            System.out.println("file: " + hline);
            writer.writeString(hline);
            for(int k = 0; k < rows; k++){
                StringBuilder str = new StringBuilder();
                for(int ct = 0; ct < cols; ct++){
                    if(ct!=0) str.append(",");
                    str.append(String.format("%e", vList.get(ct)[k]));
                }
                System.out.println("file: " + str.toString());
                writer.writeString(str.toString());
            }
            
            StringBuilder str = new StringBuilder();
            for(int b = 0; b < arrBias.size(); b++){
                if(b!=0) str.append(",");
                str.append(String.format("%e", arrBias.get(b).asDouble()));                
            }
            System.out.println("file: " + str.toString());
            writer.writeString(str.toString());
        }
        writer.close();
    }
    
    
    public void benchmark(int iter){
        float[]  input = new float[this.inputSize];
        float[] output = new float[this.outputSize];
        
        Random r = new Random();
        for(int i = 0; i < input.length; i++) input[i] = r.nextFloat();
        for(int i = 0; i < output.length; i++) output[i] = r.nextFloat();
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            this.feedForward(input, output);
        }
        long now = System.currentTimeMillis();
        double hz = ((double) iter) /(now-then);
        System.out.printf("iterations %d, time = %d \n", iter, now-then);
    }
        
    public static void main(String[] args) {
        try {
            //EJMLModel.torchToEjml("torch_weights.json", "ejml_rich.network", 4);

            EJMLModel model = EJMLLoader.load("etc/networks/clas12default.network", "trackclassifier12.network", 2, "default");
            
            //String data ="0.3112245, 0.30038264, 0.2844388, 0.27359694, 0.3125, 0.3125, 0.29336736, 0.29145408, 0.39760044, 0.4017857, 0.36607143, 0.4330357";
            String data = "0.1875, 0.16517857, 0.18835033, 0.17623298, 0.21819197, 0.22544643, 0.25085035, 0.25659013, 0.68877554, 0.6996173, 0.4030612, 0.4049745";
        
        
            String[] tokens = data.split(",");
            float[]  input = new float[12]; float[] output = new float[3];
            for(int i = 0; i < 12; i++) input[i] = Float.parseFloat(tokens[i]);
            
            model.feedForwardSoftmax(input, output);
            System.out.println(Arrays.toString(output));
        } catch (Exception ex) {
            Logger.getLogger(EJMLModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        
 
        
        
        /*EJMLModel model = new EJMLModel("ejml_rich.network",ModelType.TANH_LINEAR);
        
       float[] out = new float[1];
       
       model.feedForwardTanhLinear(new float[]{0.50793f,0.56494f,0.31774f,0.56008f,0.93249f,0.62549f,0.41117f,0.41584f}, out);
       System.out.println(out[0]);
       model.feedForwardTanhLinear(new float[]{0.50793f,0.56494f,0.31774f,0.56008f,0.93249f,0.62549f,0.4285f,0.6712f},out);
       System.out.println(out[0]);
        */
        /*
        //EJMLModelEvaluator model = new EJMLModelEvaluator("model.csv");
        EJMLModel model = new EJMLModel("network/5038/default/trackClassifier.network");
        //float[] in = new float[]{0.1161f, 0.0000f, 0.1403f, 0.1607f, 0.2604f, 0.2783f};
        float[] out = new float[3];
        model.feedForward(new float[]{0.26339f, 0.30208f, 0.23214f, 0.25298f, 0.15051f, 0.15689f}, out);
        System.out.println(Arrays.toString(out));
        model.feedForward(new float[]{0.73393f, 0.73661f, 0.78036f, 0.79911f, 0.15051f, 0.15689f}, out);
        System.out.println(Arrays.toString(out));                */
    }
}
