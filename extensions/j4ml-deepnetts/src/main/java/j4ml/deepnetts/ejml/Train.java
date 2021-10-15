/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.ejml;

import deepnetts.data.TabularDataSet;
import j4ml.deepnetts.network.DeepNettsClassifier;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import j4np.utils.io.LSVMFileReader;
import j4np.utils.io.TextFileReader;
import java.util.Arrays;
import java.util.Random;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */
public class Train {
   
    public static DataPairList read(String file){
        DataPairList list = new DataPairList();
        LSVMFileReader lsvm = new LSVMFileReader(file,6);
        lsvm.setClasses(3);
        TextFileReader reader = new TextFileReader();
        reader.open(file);
        while(reader.readNext()==true){
            DataPair pair = lsvm.toData(reader.getString());
            list.add(pair);
            //pair.show();
        }
        return list;
    }
    
    
    public static double[] create(double[] a){
        double[] temp = new double[a.length];
        for(int k = 0; k < a.length; k++){ temp[k] = a[k];}
        return temp;
    }
    
    public static DataPairList generateCN(DataPairList list){
        
        DataPairList data = new DataPairList();
        
        Random rand = new Random();
        
        int size = list.getList().size();
        for(int i = 1; i < size; i++){
            
            data.add(list.getList().get(i));
            
            double[] previous = list.getList().get(i-1).getFirst();
            double[] current  = list.getList().get(i).getFirst();
            
            double[] a = Train.create(current);
            a[4] = previous[4];
            a[5] = previous[5];
            
            data.add(new DataPair(a,new double[]{1,0.0,0.0}));
            
            double[] b = Train.create(current);
            b[0] = previous[0];
            b[1] = previous[1];
            
            data.add(new DataPair(b,new double[]{1,0.0,0.0}));
            
            double[] c = Train.create(current);
            c[2] = previous[2];
            c[3] = previous[3];
            
            data.add(new DataPair(c,new double[]{1,0.0,0.0}));
            
            //System.out.println("current = " + Arrays.toString(current));
            
            for(int jj = 0; jj < 6; jj++){
                double which = rand.nextDouble();
                double shift = 2.5 + rand.nextDouble()*5;
                
                double[] d = Train.create(current);
                //System.out.println("before = " + Arrays.toString(d));
                if(which>0.5&&d[jj]>8/112.){
                    d[jj] = d[jj] + shift/112.;
                } else {
                    d[jj] = d[jj] - shift/112.;
                }                
                //System.out.println("after = " + Arrays.toString(d));
                data.add(new DataPair(d,new double[]{1.0,0.0,0.0}));
            }
        }
        return data;
    }
    
    public static DataPairList generate(DataPairList list){
        DataPairList data = new DataPairList();
        
        Random rand = new Random();
        
        int size = list.getList().size();
        for(int i = 1; i < size; i++){
            data.add(list.getList().get(i-1));
            double[] previous = list.getList().get(i-1).getFirst();
            double[] current  = list.getList().get(i).getFirst();
            
            double[] a = Train.create(current);
            a[4] = previous[4];
            a[5] = previous[5];
            
            data.add(new DataPair(a,new double[]{1,0.0,0.0}));
            
            double[] b = Train.create(current);
            b[0] = previous[0];
            b[1] = previous[1];
            
            data.add(new DataPair(b,new double[]{1,0.0,0.0}));
            
            double[] c = Train.create(current);
            c[2] = previous[2];
            c[3] = previous[3];
            
            data.add(new DataPair(c,new double[]{1,0.0,0.0}));
            
            for(int jj = 0; jj < 6; jj++){
                int which = rand.nextInt(6);
                double[] d = Train.create(current);
                d[which] = previous[which];
                data.add(new DataPair(d,new double[]{1,0.0,0.0}));
            }
        }
        return data;
    }
    
    public static String[] generateNames(int input, int output){
        String[] names = new String[input+output];
        for(int i = 0; i < input; i++) names[i] = "in" + i;
        for(int i = 0; i < output; i++) names[i+input] = "out" + i;        
        return names;
    }
    
    public static DataSet convert(DataPairList list){
        TabularDataSet  dataset = new TabularDataSet(6,3);
        for(int i = 0; i < list.getList().size(); i++){
            float[] output = DataArrayUtils.toFloat(list.getList().get(i).getSecond());
            float[]  input = DataArrayUtils.toFloat(list.getList().get(i).getFirst());
            dataset.add(new TabularDataSet.Item(input, output));
        }
        
        String[] columns = generateNames(6,3);
        dataset.setColumnNames(columns);
        return dataset;
    }
    
    public static void main(String[] args){
        
        DataPairList listPos = Train.read("tracks_montecarlo_pos.lsvm");
        DataPairList listPosGen  = Train.generateCN(listPos);
        
        DataPairList listNeg = Train.read("tracks_montecarlo_neg.lsvm");        
        DataPairList listNegGen  = Train.generateCN(listNeg);   
        
        listPosGen.show();
        System.out.println("***********");
        listNegGen.show();
        
        
        listPosGen.getList().addAll(listNegGen.getList());
       
        DataSet ds = Train.convert(listPosGen);
        ds.shuffle();
        
        
        DataSet[] data = ds.split(0.8,0.2);
        
        DeepNettsClassifier classifier = new DeepNettsClassifier();
        classifier.init(new int[]{6,12,12,12,3});        
        classifier.train(data[0], 2500);
        
        classifier.evaluate(data[1]);
        
        classifier.save("network/tc_shift_closestNeigthbor.nnet");
    }
}
