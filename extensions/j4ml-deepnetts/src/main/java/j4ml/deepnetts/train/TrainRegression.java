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
    
    public TabularDataSet readFile(String filename, int output, int features, int charge){

        TabularDataSet  dataset = new TabularDataSet(features,output);

        TextFileReader reader = new TextFileReader();
        reader.open(filename);
        int outputOffset = 0;
        int inputOffset  = 0;
        
        if(charge>0){
            outputOffset = 6;
            inputOffset  = 36;
        }
        while(reader.readNext()==true){
            String[] tokens = reader.getString().split(",");
            int sector = Integer.parseInt(tokens[1 + outputOffset]);
            //System.out.printf(" sector = %d\n",sector);
            float[]  outData = new float[output];
            for(int j = 0; j < output; j++){
                outData[j] = Float.parseFloat(tokens[2+j + outputOffset]);
            }
            
            double theta = Math.toRadians(outData[1]*40+5);
            double ct = Math.cos(theta);
            //outData[1] = (float) ((ct - 0.7071067811865476)/
            //        Math.abs(0.9961946980917455-0.7071067811865476));
            //System.out.println("theta bin = " + outData[1]);
            //System.out.println(outData);
            //System.out.println("length = " + tokens.length);
            float[] inData = new float[features];
            for(int j = 0; j < features; j++) {
                //System.out.println("parsing [" + j + "] = " + tokens[j+6]);
                //inData[j] = Float.parseFloat(tokens[j+6]);
                double wire = Double.parseDouble(tokens[j+12 + inputOffset]);
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
    
    public void train(String filename, int output, int features, int charge){
        DataSet data = this.readFile(filename, output, features, charge);
        System.out.println("loaded = " + data.size());
        network.init(new int[]{36,72,72,3});
        network.initTrainer();
        data.shuffle();
        DataSet[] dataSet = data.split(0.8,0.2);
        
        network.train(dataSet[0], 2400);
        network.save(String.format("network_output_mode.nnet" ));
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
        int mode = 2;
        if(args.length>0){
            mode = Integer.parseInt(args[0]);
        }
        

        TrainRegression reg = new TrainRegression();
        if(mode==1){
            reg.train("dc_physics_features_36_trim_fixed.csv",3,36, -1);
        } else {
            reg.train("dc_physics_features_36_trim_fixed.csv",3,36,1);
        }
        
        //reg.train("dc_regression_36.csv", 3, 36);
        //reg.train("dc_regression_06_pos.csv", 3, 6);
    }
}
