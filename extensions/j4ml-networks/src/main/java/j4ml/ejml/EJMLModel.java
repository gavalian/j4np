package j4ml.ejml;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
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
            for (int j = 0; j < output.numCols(); ++j)
                output.set(i, j, Math.max(0, output.get(i, j)));
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
            for (int j = 0; j < output.numCols(); ++j)
                output.set(i, j, Math.exp(output.get(i, j)));

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
        switch (this.ejmlModelType){
            
            case SOFTMAX: feedForwardSoftmax(input, result); return;
            case TANH_LINEAR: feedForwardTanhLinear(input, result); return;
            case RELU_LINEAR: feedForwardReLULinear(input, result); return;
            
            default: feedForward(input, result);
        }
        /*
        if(this.ejmlModelType==ModelType.SOFTMAX){
            this.feedForwardSoftmax(input, result); return;
        }
        this.feedForward(input, result);
        */
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
    
    private void feedForwardTanhLinear(float[] input, float[] results) {
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
    
    public static void main(String[] args) {
        //EJMLModelEvaluator model = new EJMLModelEvaluator("model.csv");
        EJMLModel model = new EJMLModel("network/5038/default/trackClassifier.network");
        //float[] in = new float[]{0.1161f, 0.0000f, 0.1403f, 0.1607f, 0.2604f, 0.2783f};
        float[] out = new float[3];
        model.feedForward(new float[]{0.26339f, 0.30208f, 0.23214f, 0.25298f, 0.15051f, 0.15689f}, out);
        System.out.println(Arrays.toString(out));
        model.feedForward(new float[]{0.73393f, 0.73661f, 0.78036f, 0.79911f, 0.15051f, 0.15689f}, out);
        System.out.println(Arrays.toString(out));                
    }
}
