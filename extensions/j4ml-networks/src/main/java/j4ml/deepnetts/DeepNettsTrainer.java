/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.deepnetts;

import deepnetts.data.MLDataItem;
import deepnetts.data.TabularDataSet;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.AbstractLayer;
import deepnetts.net.loss.LossFunction;
import deepnetts.net.loss.LossType;
import deepnetts.net.loss.MeanSquaredErrorLoss;
import deepnetts.net.train.opt.OptimizerType;
import j4ml.data.DataList;
import j4ml.data.EntryTransformer;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;
import javax.visrec.ml.eval.Evaluator;

/**
 *
 * @author gavalian
 */
public class DeepNettsTrainer {
    
    FeedForwardNetwork         network = null;
    private LossFunction    lossFunction = null;//= new MeanSquaredErrorLoss();//LossType.MEAN_SQUARED_ERROR;
    
    private transient Evaluator<NeuralNetwork, DataSet<? extends MLDataItem>> eval = new ClassifierEvaluator();
    
    public double     learningRate = 0.001;
    public double         momentum = 0.7;
    public double   regularization = 0.0;    
    public OptimizerType optimizer = OptimizerType.SGD;
    public ProgressStats  progress = new ProgressStats();
    
    public DeepNettsTrainer(FeedForwardNetwork  n){
        network = n;
        this.lossFunction = new MeanSquaredErrorLoss(network);
    }
    
    public void reset(){ progress.reset();}
    
    public void updateLayers(){
        for (AbstractLayer layer : network.getLayers()) {
            layer.setLearningRate((float) this.learningRate);
            layer.setMomentum((float) this.momentum);
            layer.setRegularization((float) this.regularization);
            layer.setBatchMode(false);
            layer.setBatchSize(1);
            layer.setOptimizerType(optimizer);
        }
    }
    
    public void train(DataSet<? extends MLDataItem> train, DataSet<? extends MLDataItem> test){
        lossFunction.reset();
        float[] outputError;
        long then = System.currentTimeMillis();
        for (MLDataItem dataSetItem : train) {
            network.setInput(dataSetItem.getInput());
            outputError = lossFunction.addPatternError(network.getOutput(), dataSetItem.getTargetOutput().getValues());
            network.setOutputError(outputError);
            network.backward();
            network.applyWeightChanges();
        }
        long now = System.currentTimeMillis();
        double  accuracy = calculateAccuracy(test);
        double totalLoss = lossFunction.getTotal();
        this.progress.add(totalLoss, accuracy, now-then);
    }
    
    public void train(DataSet<? extends MLDataItem> train){
        this.train(train,train);
    }
    
    public ProgressStats getStats(){return progress;}
    
    public void train(DataSet<? extends MLDataItem> train, int nEpochs){ 
        train(train,train,nEpochs);
    }
        
    public DataSet convert(DataList list, EntryTransformer transformer){
        
        int nInputs = list.getList().get(0).getFirst().length;
        int nOutputs = list.getList().get(0).getSecond().length;        
        TabularDataSet  dataset = new TabularDataSet(nInputs,nOutputs);
        for(int k = 0; k < list.getList().size(); k++){
            float[]  inBuffer = list.getList().get(k).floatFirst();
            float[] outBuffer = list.getList().get(k).floatSecond();   

            float[]   inValues = transformer.input().getValue(inBuffer);            
            float[]  outValues = transformer.output().getValue(outBuffer);
            
            //System.out.println(Arrays.toString(inValues) + "  " + Arrays.toString(outValues));
            
            dataset.add(new TabularDataSet.Item(inValues, outValues));
        }
        String[] names = DataSetUtils.generateNames(nInputs, nOutputs);
        dataset.setColumnNames(names);
        return dataset;
    }
    
    public void train(DataSet<? extends MLDataItem> train, DataSet<? extends MLDataItem> test, int nEpochs){        

        for(int epoch = 0; epoch < nEpochs; epoch++){
            this.train(train, test);
            this.progress.show();
        }
        
        ///double acc1 = this.calculateAccuracy(trainingSet);
        /*
        for (AbstractLayer layer : network.getLayers()) {
            layer.setLearningRate((float) this.learningRate);
            layer.setMomentum((float) this.momentum);
            layer.setRegularization((float) this.regularization);
            layer.setBatchMode(false);
            layer.setBatchSize(1);
            layer.setOptimizerType(optimizer);
        }
        
        //double acc2 = this.calculateAccuracy(trainingSet);        
        //System.out.printf(" ACC1 = %f, ACC2 = %f\n",acc1,acc2);
        
        for(int i = 0; i < 125; i++){
            lossFunction.reset();
            long then = System.currentTimeMillis();
            for (MLDataItem dataSetItem : trainingSet) {
                network.setInput(dataSetItem.getInput());
                outputError = lossFunction.addPatternError(network.getOutput(), dataSetItem.getTargetOutput().getValues());
                //System.out.println(Arrays.toString(outputError) + "  total = "  + lossFunction.getTotal());
                network.setOutputError(outputError);
                network.backward();
                network.applyWeightChanges();
            }
            long now = System.currentTimeMillis();
            double accuracy = this.calculateAccuracy(trainingSet);
            double totalTrainingLoss = lossFunction.getTotal();
            System.out.printf("Epoch %d , loss = %e  time = %d --->  %.8f\n",i,totalTrainingLoss,(now-then), accuracy);
        }*/
    }
    
    
    private float calculateAccuracy(DataSet<? extends MLDataItem> validationSet) {
        EvaluationMetrics pm = eval.evaluate(network, validationSet);
        return pm.get(EvaluationMetrics.ACCURACY);
    }
        public void save(String file, EntryTransformer transformer){
        String json = DeepNettsIO.toJson(network, transformer);
        TextFileWriter.write(file, Arrays.asList(json));
    }
        
    public static class ProgressStats {
        
        List<Long>       times = new ArrayList<>();
        List<Double>      loss = new ArrayList<>();
        
        List<Double>  accuracy = new ArrayList<>();
        public ProgressStats(){}
        
        public void add(double tloss, double tacc, long ttime){
            times.add(ttime); loss.add(tloss); accuracy.add(tacc);
        }
        
        public String getLast(){
            int row = times.size()-1;
            if(row<0) return "empty";
            return String.format(">>> epoch %5d , loss = %16e , time = %12d, accuracy = %12.5f", 
                    row+1,loss.get(row),times.get(row),accuracy.get(row));
        }
        
        public void show(){
            System.out.println(getLast());
        }
        
        public void reset(){times.clear(); loss.clear(); accuracy.clear();}
    }
    
    
    public static void main(String[] args){
        
        String previous = "class12classifier_7.json";
        String     next = "class12classifier_8.json";
        
        DataList data = DataList.fromCSV("sample.csv", new int[]{0,1,2,3,4,5}, new int[]{6,7,8});

        FeedForwardNetwork network = DeepNetts.create(previous);
        DeepNettsTrainer   trainer = new DeepNettsTrainer(network);
        EntryTransformer   transformer = DeepNettsIO.getTransformer(previous);
        
        //trainer.learningRate = 0.001;
        //trainer.momentum = 0.4;
        trainer.updateLayers();
        
        DataSet dataset = trainer.convert(data, transformer);
        System.out.println("");
        
        trainer.train(dataset, 512);
        
        trainer.save(next, transformer);
    }
}
