/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.regression;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.CSVReader;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.deepnetts.DeepNettsRegression;

/**
 *
 * @author gavalian
 */
public class RegressionStudyMLP {
    public static DataNormalizer features = new DataNormalizer(new double[]{1,1,1,1,1,1},
                new double[]{112,112,112,112,112,112});
        
    public static  DataNormalizer labels = new DataNormalizer(new double[]{0.5,5.0,40.0},
                new double[]{6.5,35.0,120.0});
    
    
    public static void study(String file, ActivationType hiden, ActivationType output){
        CSVReader r = new CSVReader(file,new int[]{0,1,2,3,4,5}, new int[]{6,7,8});
        
        DataList list = r.getData();
        
        list.scan();
        
        DataList.normalizeInput(list, features);
        DataList.normalizeOutput(list, labels);
        
        list.shuffle();
        list.shuffle();
        
        list.scan();
        
        DataList[] data = DataList.split(list, 0.6,0.4);
        
        DeepNettsRegression reg = new DeepNettsRegression(hiden,output);
        
        reg.init(new int[]{6,12,24,24,12,3});
        reg.train(data[0], 625);
        
        reg.evaluate(data[1]);
        
        DataList.denormalizeInfered(data[1], labels);
        DataList.denormalizeOutput(data[1], labels);
        data[1].export("results_"+hiden.name()+"_"+output.name()+".csv");
        
    }
    
    public static void main(String[] args){
        //String file = "/Users/gavalian/Work/Software/project-10.4/data/regression/data_negative_full.csv";
        String file = "/Users/gavalian/Work/Software/project-10.4/j4np-1.0.4/data_flat_tb.csv";
        
        RegressionStudyMLP.study(file,ActivationType.RELU, ActivationType.LINEAR);
        RegressionStudyMLP.study(file,ActivationType.RELU, ActivationType.TANH);
        RegressionStudyMLP.study(file,ActivationType.RELU, ActivationType.SIGMOID);
        
        RegressionStudyMLP.study(file,ActivationType.TANH, ActivationType.TANH);
        RegressionStudyMLP.study(file,ActivationType.TANH, ActivationType.LINEAR);
        RegressionStudyMLP.study(file,ActivationType.SIGMOID, ActivationType.LINEAR);
        RegressionStudyMLP.study(file,ActivationType.SIGMOID, ActivationType.SIGMOID);
    }
}
