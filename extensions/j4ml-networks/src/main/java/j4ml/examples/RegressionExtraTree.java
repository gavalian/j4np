/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.examples;

import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.extratrees.ExtraTrees;
import j4ml.extratrees.data.Matrix;

import j4np.utils.io.TextFileReader;
import java.util.Arrays;


/**
 *
 * @author gavalian
 */
public class RegressionExtraTree {
    
    ExtraTrees tree = null;
    
    public void evaluate(DataList list){
        
        int nrows = list.getList().size();
        int[][] matrix = new int[2][2];
        double error = 0.0;
        
        for(int i = 0; i < nrows; i++){
            double[] first = list.getList().get(i).getFirst();
            double[] second = list.getList().get(i).getSecond();
            DataRow row = new DataRow(first);
            double value = tree.getValue(row);
            System.out.printf("%9.5f %9.5f %9.5f %s\n",second[0],value,
                    second[0]-value, Arrays.toString(first));
            error += Math.abs(second[0]-value);
        }
        
        System.out.printf("\n\n Absolute Error %.6f, relative = %.6f\n",
                error,error/nrows);
    }
    
    public ExtraTrees getTree(DataList list){
        
        int ndata = list.getList().size();
        int ndim  = 6;
        
        double[] output = new double[ndata];
        double[] v = new double[ndata*ndim];                
        Matrix m = new Matrix(v, ndata, ndim);
        
        for(int row = 0; row < ndata; row++){
            double[] first = list.getList().get(row).getFirst();
            double[] second = list.getList().get(row).getSecond();
            for(int col = 0; col < 6; col++){
                m.set(row, col, first[col]);
            }
            
            output[row] = second[0];
        }        
        return new ExtraTrees(m,output);
    }
    
    public DataList importData(String file){
        DataList list = new DataList();
        TextFileReader reader = new TextFileReader(file);
        reader.setSeparator(",");
        int counter = 0;
        while(reader.readNext()==true) {
            //pair.show();
            counter++;
            try{
                double[] value = reader.getAsDoubleArray(0, 1);
                //value[0] = value[0]/10.0;
                double[] features = reader.getAsDoubleArray(4, 6);
                DataEntry pair = new DataEntry(features,value);
                //pair.show();
                list.add(pair);
            } catch (Exception e){
                System.out.printf("file = %s\n",file);
                System.out.printf("something went wrong at line # %d\n",counter);
                System.out.printf("line [%s]\n",reader.getString());
            }
        }
        return list;
    }
    
    public void train(DataList list){
        tree = this.getTree(list);
        tree.learnTrees(50, 50, 150);
        System.out.println(tree);         
    }
    
    public static void main(String[] args){
        RegressionExtraTree tree = new RegressionExtraTree();
        //DataPairList  train = tree.importData("regression_train.csv");
        //DataPairList  test = tree.importData("regression_test.csv");        
        DataList  train = tree.importData("regression_train_6n.csv");
        DataList  test = tree.importData("regression_test_6n.csv");        
        //DataPairList  train = tree.importData("xaa");
        //DataPairList  test = tree.importData("xab");
        tree.train(train);
        tree.evaluate(test);
    }
}
