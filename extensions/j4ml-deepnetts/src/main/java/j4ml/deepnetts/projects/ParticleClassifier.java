/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.projects;

import deepnetts.data.TabularDataSet;
import j4ml.deepnetts.network.DeepNettsClassifier;
import j4np.utils.io.CSVFileReader;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.DataPairList;
import java.util.Arrays;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class ParticleClassifier {
    
    private String trainingData = "";
    private DataPairList data = null;
    
    
    private double[] minimum = new double[28];
    private double[] maximum = new double[]{
        10,
        0.65,0.65,0.65,450,450,450,650,650,650,
        0.75,0.75,0.75,450,450,450,650,650,650,
        0.3,0.3,0.3,450,450,450,650,650,650
    };
    
    /*private double[] maximum = new double[]{
        10,
        1,1,1,450,450,450,650,650,650,
        1,1,1,450,450,450,650,650,650,
        1,1,1,450,450,450,650,650,650
    };*/
    
    public ParticleClassifier(String filename){
        trainingData = filename;
        for(int i = 0; i < minimum.length; i++) minimum[i] = 0.0;
    }
    
    public int[] getRange(int start, int end){
        int[] range = new int[end-start+1];
        for(int i = 0; i < range.length;i++)
            range[i] = i+ start;
        return range;
    }
    
    public int[] getRange(int[] prefix, int start, int end){
        int length = end-start+1;
        int[] range = new int[end-start+1 + prefix.length];
        for(int i = 0; i < prefix.length; i++) range[i] = prefix[i];
        for(int i = 0; i < length;i++)
            range[i+prefix.length] = i + start;
        return range;
    }
    
    public String[] generateNames(int input, int output){
        String[] names = new String[input+output];
        for(int i = 0; i < input; i++) names[i] = "in" + i;
        for(int i = 0; i < output; i++) names[i+input] = "out" + i;        
        return names;
    }
    
    public DataPairList loadData(){
        CSVFileReader csv = new CSVFileReader(trainingData);
        int[] range = getRange(new int[]{2}, 12,38);
        System.out.println(range.length + " " + Arrays.toString(range));
        csv.setInputOutput(new int[]{0}, range);
        data = csv.getData();
        data.scan();
        
        DataPairList norm = data.getNormalized(minimum, maximum);
        
        norm.scan();
        
        norm.turnClassifier(2);
        
        norm.scan();
        return norm;
    }
    
    
    public DataPairList loadDataSF(){
        CSVFileReader csv = new CSVFileReader(trainingData);
        int[] range = getRange(new int[]{2}, 12,38);
        System.out.println(range.length + " " + Arrays.toString(range));
        csv.setInputOutput(new int[]{0}, range);
        data = csv.getData();
        data.scan();
        
        for(int i = 0; i < data.getList().size(); i++){
            double[] second = data.getList().get(i).getSecond();
            double   p      = second[0];
            
            for(int k = 0; k < 3; k++) second[k+ 1] = second[k+ 1]/p;
            for(int k = 0; k < 3; k++) second[k+10] = second[k+10]/p;
            for(int k = 0; k < 3; k++) second[k+19] = second[k+19]/p;
        }
        data.scan();
        
        
        DataPairList norm = data.getNormalized(minimum, maximum);
        
        norm.scan();
        
        norm.turnClassifier(2);
        
       
        //return null;
        norm.scan();
        return norm;
    }
    
    public DataSet convert(DataPairList list){
        TabularDataSet  dataset = new TabularDataSet(28,2);
        for(int i = 0; i < list.getList().size(); i++){
            float[] output = DataArrayUtils.toFloat(list.getList().get(i).getFirst());
            float[]  input = DataArrayUtils.toFloat(list.getList().get(i).getSecond());
            dataset.add(new TabularDataSet.Item(input, output));
        }
        
        String[] columns = generateNames(28,2);
        dataset.setColumnNames(columns);
        return dataset;
    }
    
    public void train(){
        DeepNettsClassifier network = new DeepNettsClassifier();
        network.init(new int[]{28,28,28,28,2});
        DataPairList dataList = this.loadData();
        DataSet dataSet = convert(dataList);
        dataSet.shuffle();
        DataSet[] sets = dataSet.split(0.7,0.3);        
        network.train(sets[0], 2000);
        network.evaluate(sets[1]);
    }
    
    public void trainSF(){
        DeepNettsClassifier network = new DeepNettsClassifier();
        network.init(new int[]{28,28*4,28*4,28*2,2});
        DataPairList dataList = this.loadDataSF();
        
        DataSet dataSet = convert(dataList);
        dataSet.shuffle();
        DataSet[] sets = dataSet.split(0.7,0.3);        
        network.train(sets[0], 1200);
        network.evaluate(sets[1]);
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.0/data/pid_features_all.csv";
        ParticleClassifier classifier = new ParticleClassifier(file);
        //classifier.loadData();
        //classifier.train();        
        classifier.trainSF();
        //classifier.loadDataSF();
    }
}
