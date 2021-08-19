/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.train;

import deepnetts.data.TabularDataSet;
import j4ml.deepnetts.network.DeepNettsRegression;
import j4np.utils.io.CSVFileReader;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.TextFileReader;
import java.util.Iterator;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class TrainRegression {
    
    DeepNettsRegression network = new DeepNettsRegression();

    public TrainRegression(){
        
    }
    
    public TabularDataSet readFile(String filename, int output, int features){

        TabularDataSet  dataset = new TabularDataSet(features,output);

        TextFileReader reader = new TextFileReader();
        reader.open(filename);

        while(reader.readNext()==true){
            String[] tokens = reader.getString().split(",");
            int sector = Integer.parseInt(tokens[1]);
            //System.out.printf(" sector = %d\n",sector);
            float[]  outData = new float[output];
            for(int j = 0; j < output; j++){
                outData[j] = Float.parseFloat(tokens[2+j]);
            }
            
            float[] inData = new float[features];
            for(int j = 0; j < features; j++) {
                //inData[j] = Float.parseFloat(tokens[j+6]);
                double wire = Double.parseDouble(tokens[j+6]);
                double wireShifted = wire*112 + 112*(sector-1);
                inData[j] =  (float) (wireShifted/(112.0*6));
            }
            //if(sector==2){
                //System.out.printf("%s => %s\n",DataArrayUtils.floatToString(inData, ","),
                //        DataArrayUtils.floatToString(outData, ","));
                dataset.add(new TabularDataSet.Item( inData, outData));
            //}
        }
        String[] names = new String[features+output];
        
        for(int i = 0; i < names.length; i++){ names[i] = "f"+i; }
        dataset.setColumnNames(names);
        return dataset;
    }
    
    public void train(String filename, int output, int features){
        DataSet data = this.readFile(filename, output, features);
        System.out.println("loaded = " + data.size());
        network.init(new int[]{36,72,72,3});
        network.initTrainer();
        data.shuffle();
        DataSet[] dataSet = data.split(0.8,0.2);
        
        network.train(dataSet[0], 240);
        
        int size = dataSet[1].size();
        
        Iterator iter = dataSet[1].iterator();
        
        while(iter.hasNext()){
            TabularDataSet.Item  item = (TabularDataSet.Item) iter.next();
            //System.out.println("object = " + item.getInput().getValues());
            float[]  prediction = network.getNetwork().predict(item.getInput().getValues());
            float[]  desired = item.getTargetOutput().getValues();
            for(int k = 0; k < prediction.length; k++){
                System.out.printf("%8.5f %8.5f",prediction[k],desired[k] );//(prediction[k]-desired[k])/desired[k]);
            }
            System.out.println();
        }
        /*for(int i = 0; i < size; i++){
            network.getNetwork().predict(dataSet[1].)
        }*/
    }
    
    public static void main(String[] args){
        TrainRegression reg = new TrainRegression();
        reg.train("dc_regression_36_pos.csv", 3, 36);
        //reg.train("dc_regression_06_pos.csv", 3, 6);
    }
}
