/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.regression;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.util.FileIO;
import j4ml.data.CSVReader;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4ml.deepnetts.DeepNettsRegression;
import j4ml.ejml.EJMLModel;
import j4np.utils.base.ArchiveUtils;
import j4np.utils.io.TextFileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class RegressionStudyMLP {
    public static DataNormalizer features = new DataNormalizer(new double[]{1,1,1,1,1,1},
                new double[]{112,112,112,112,112,112});
        
    public static  DataNormalizer labels = new DataNormalizer(new double[]{0.5,5.0,40.0},
                new double[]{6.5,35.0,120.0});
    
    
    public static  DataNormalizer labelsBoth = new DataNormalizer(new double[]{0.5,5.0,0.0},
            new double[]{6.5,45.0,120.0});
    
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
    
    
    public static void trainSector2(){
        //String file = "/Users/gavalian/Work/DataSpace/regression/mc_epi_train_negative.csv";
        String file = "/Users/gavalian/Work/DataSpace/regression/mc_epi_train_negative.csv";
        
        CSVReader r = new CSVReader(file,new int[]{0,1,2,3,4,5}, new int[]{6,7,8});
        
        DataList list = r.getData();
        
        list.scan();
        
        DataList.normalizeInput(list, features);
        DataList.normalizeOutput(list, labelsBoth);
        
        list.shuffle();
        list.shuffle();
        
        list.scan();
        
        DeepNettsRegression reg = new DeepNettsRegression(ActivationType.TANH,ActivationType.LINEAR);
        reg.init(new int[]{6,12,12,12,12,3});
        reg.train(list, 7500);
        
        try {
            FileIO.writeToFile(reg.getNetwork(), "network_neg.nnet");
            List<String>  content = reg.getNetworkStream();
            ArchiveUtils.writeFile("regression2ejml.network", "networks/0000/negative.nnet", content);
        } catch (IOException ex) {
            Logger.getLogger(RegressionStudyMLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void evaluateSector2(String file){
            
            FeedForwardNetwork net_pos = null;//FileIO.createFromFile("network_pos.nnet", FeedForwardNetwork.class);
            FeedForwardNetwork net_neg = null;//FileIO.createFromFile("network_neg.nnet", FeedForwardNetwork.class);
            //ArchiveUtils
                    //.writeFile("regression2ejml.network", "networks/0000/negative.nnet", content);
            
            //EJMLModel modelPos = new EJMLModel( EJMLModel.ModelType.TANH_LINEAR.name());
            //EJMLModel.create(lines);
            
            List<String> content_pos = ArchiveUtils.getFileAsList("regression2ejml.network", "networks/0000/positive.nnet");
            List<String> content_neg = ArchiveUtils.getFileAsList("regression2ejml.network", "networks/0000/negative.nnet");
            
            EJMLModel modelPos = EJMLModel.create(content_pos);
            EJMLModel modelNeg = EJMLModel.create(content_neg);
            
            modelPos.setType(EJMLModel.ModelType.TANH_LINEAR);
            modelNeg.setType(EJMLModel.ModelType.TANH_LINEAR);
            
            TextFileReader r = new TextFileReader();
            r.open(file);
            while(r.readNext()==true){
                String dataLine = r.getString();
                String[] tokens = dataLine.trim().split("\\s+");
                int charge = Integer.parseInt(tokens[1]);
                double[]  input = new double[6];
                double[] output = new double[3];
                for(int i = 0; i < 6 ;i++){
                    input[i] = Double.parseDouble(tokens[12+i]);
                }
                for(int i = 0; i < 3 ;i++){
                    output[i] = Double.parseDouble(tokens[4+i]);
                }
                DataList list = new DataList();
                list.add(new DataEntry(input,output));
                
                DataList.normalizeInput(list, features);
                DataList.normalizeOutput(list, labelsBoth);
                
                if(charge>0){
                    
                   float[] result = new float[3];
                    modelPos.getOutput(list.getList().get(0).floatFirst(), result);
                    list.getList().get(0).setInfered(result);
                    //DataList.denormalizeOutput(list, labelsBoth);
                    DataList.denormalizeInfered(list, labelsBoth);
                } else {
                    
                    //net_neg.setInput(list.getList().get(0).floatFirst());
                    float[] result = new float[3];
                    modelNeg.getOutput(list.getList().get(0).floatFirst(), result);
                    list.getList().get(0).setInfered(result);
                    
                    DataList.denormalizeInfered(list, labelsBoth);
                }
                //list.show();
                System.out.println(dataLine.trim() + " " + String.format("%f %f %f",
                        list.getList().get(0).getInfered()[0],
                        list.getList().get(0).getInfered()[1],
                        list.getList().get(0).getInfered()[2]
                        ));
            }                            
    }
    
    public static void main(String[] args){
        //String file = "/Users/gavalian/Work/Software/project-10.4/data/regression/data_negative_full.csv";
        
        //String eval = "/Users/gavalian/Work/DataSpace/regression/mc_epi_evalu_0013_hb.txt";
        String file = "/Users/gavalian/Work/Software/project-10.5/study/data/mlp_study_data_tb.csv";
        
        RegressionStudyMLP.study(file,ActivationType.RELU, ActivationType.LINEAR);
        RegressionStudyMLP.study(file,ActivationType.RELU, ActivationType.TANH);
        RegressionStudyMLP.study(file,ActivationType.RELU, ActivationType.SIGMOID);
        
        RegressionStudyMLP.study(file,ActivationType.TANH, ActivationType.TANH);
        RegressionStudyMLP.study(file,ActivationType.TANH, ActivationType.LINEAR);
        RegressionStudyMLP.study(file,ActivationType.SIGMOID, ActivationType.LINEAR);
        RegressionStudyMLP.study(file,ActivationType.SIGMOID, ActivationType.SIGMOID);
        
        
        //RegressionStudyMLP.trainSector2();
        //String eval2 = "/Users/gavalian/Work/Software/project-10.4/distribution/artin/projects/pe/xgboost-java/mc_e1pi_hb.txt";
        //RegressionStudyMLP.evaluateSector2(eval2);
        
    }
}
