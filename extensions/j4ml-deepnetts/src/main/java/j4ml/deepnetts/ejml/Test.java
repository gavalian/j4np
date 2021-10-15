/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.deepnetts.ejml;

import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import j4np.utils.io.LSVMFileReader;
import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Test {
    
    public static double distance(float[] a, float[] b){
        double dist = 0.0;
        for(int i = 0; i < a.length; i++) dist += (a[i]-b[i]);
        return dist;
    }
    
    public static double distance(double[] a, double[] b){
        double dist = 0.0;
        for(int i = 0; i < a.length; i++) dist += (a[i]-b[i]);
        return dist;
    }
    
    public static double[]  diff(double[] a, double[] b){
        double[] c = new double[a.length];
        for(int i = 0; i < c.length; i++){ c[i] = a[i] - b[i];}
        return c;
    }
    
    public static void test3(String networkFile){
        EJMLModelEvaluator model = new EJMLModelEvaluator(networkFile);
        String file = "dc_classifier_testing_06_neg_v2.lsvm";
        //String file = "v2/abc_test.lsvm";
        
        LSVMFileReader lsvm = new LSVMFileReader(file,6);
        lsvm.setClasses(3);
        
        TextFileReader reader = new TextFileReader();
        reader.open(file);
        
        reader.readNext();
        String desc = reader.getString();
        String[] tokens = desc.split("\\s+");
        int nrows = Integer.parseInt(tokens[3]);
        boolean thenStop = false;
        
        List<DataPairList>  dataList = new ArrayList<>();
        
        while(thenStop==false){
            
            List<String> lines = reader.readLines(nrows);
            
            DataPairList list = new DataPairList();
            
            for(String l : lines){                
                DataPair pair = lsvm.toData(l);
                list.add(pair);
            }
            dataList.add(list);
             boolean status = reader.readNext();
            if(status==false){
                thenStop = true;
            } else {
                String desc2 = reader.getString();
                String[] tokens2 = desc2.split("\\s+");
                nrows = Integer.parseInt(tokens2[3]);
                //System.out.println(" NROWS = " + nrows);
            }
        }
        System.out.println("DATA READ EVENTS # " + dataList.size());
        
        
        int[][] matrix = new int[3][3];
        int  counterTrue = 0;
        int  counterFalse = 0;

        for(int i = 0; i < dataList.size(); i++){
            DataPairList list = dataList.get(i);
            int[] label = new int[list.getList().size()];
            float[] prob  = new float[list.getList().size()];
            int index = -1;
            float[] output = new float[3];
            float maxProb  = 0.0f;
            int  maxIndex  = 0;
            //System.out.println("********************* " );
            for(int k = 0; k < list.getList().size();k++){
                int desired = model.getClass(DataArrayUtils.toFloat(list.getList().get(k).getSecond()));
                if(desired!=0) index = k;
                model.feedForwardSoftmax(DataArrayUtils.toFloat(list.getList().get(k).getFirst()), output);
                label[k] = model.getClass(output);
                prob[k]  = output[label[k]];
                if(prob[k]>maxProb&&label[k]==2){
                    maxProb  = prob[k];
                    maxIndex = k;
                }
                //System.out.printf("%5d {} %8.5f : (%3d) (%3d) => %s\n",k, prob[k],desired, label[k],Arrays.toString(list.getList().get(k).getFirst()));
            }
            //System.out.printf("INDEX = %5d %5d\n",index,maxIndex);
            if(index>=0){
                if(maxIndex==index) counterTrue++;
                if(maxIndex!=index&&label[maxIndex]==2){
                    counterFalse++;
                    double[] diff = Test.diff(list.getList().get(index).getFirst(),
                            list.getList().get(maxIndex).getFirst());
                    System.out.println(
                            Arrays.toString(list.getList().get(index).getFirst())
                           + "  ==> " + Arrays.toString(list.getList().get(maxIndex).getFirst())
                                   + "  DIFF " + Arrays.toString(diff)
                    );
                }
            }            
        }
        
        System.out.println(Arrays.toString(matrix[0]));
        System.out.println(Arrays.toString(matrix[1]));
        System.out.println("true = " + counterTrue + " false = " + counterFalse);
    }
    
    public static void test1(String networkFile){
        
        EJMLModelEvaluator model = new EJMLModelEvaluator(networkFile);
        String file = "dc_classifier_testing_06_neg.lsvm";
        //String file = "v2/abc_test.lsvm";
        
        LSVMFileReader lsvm = new LSVMFileReader(file,6);
        lsvm.setClasses(3);
        
        TextFileReader reader = new TextFileReader();
        reader.open(file);
        
        reader.readNext();
        String desc = reader.getString();
        String[] tokens = desc.split("\\s+");
        int nrows = Integer.parseInt(tokens[3]);
        
        System.out.println(" NROWS = " + nrows);
        boolean thenStop = false;
        int counter = 0;

        int[][] matrix = new int[2][2];
        int countPos = 0;
        int countNeg = 0;
        
        while(thenStop==false){
            
            List<String> lines = reader.readLines(nrows);
            
            DataPairList list = new DataPairList();
            
            for(String l : lines){                
                DataPair pair = lsvm.toData(l);
                list.add(pair);
            }
        
            int index = -1;
            
            for(int i = 0; i < list.getList().size(); i++){
                if(list.getList().get(i).getSecond()[1]>0.5) index = i;
            }                        
            
            if(index>=0){
                countPos++;
                countNeg += nrows-1;
                //System.out.println("index = " + index);
                float[]  input = DataArrayUtils.toFloat(list.getList().get(index).getFirst());
                float[] result = new float[3];
                model.feedForwardSoftmax(input, result);
                //System.out.println(">>> " + Arrays.toString(result));
                //double probability = result[1];
                double probability = result[2];
                
                if(probability>0.5){
                    matrix[1][1]++;
                } else {
                    matrix[1][0]++;
                }
                int[]    array = new int[nrows];
                double[] prob  = new double[nrows];
                
                for(int k = 0; k < nrows; k++){
                    
                    input = DataArrayUtils.toFloat(list.getList().get(k).getFirst());
                    
                    model.feedForwardSoftmax(input, result);
                    
                    array[k] = model.getClass(result);
                    
                    
                    double p1 = result[1];
                    
                    double distance = Test.distance(
                            list.getList().get(k).getFirst(),
                            list.getList().get(index).getFirst()
                            );
                    if(p1>probability){
                        matrix[0][1]++;
                        System.out.printf(" %8d / %8d : %4d %8.5f %8.5f ==> %s %s  () %.6f %s\n",
                                counter,nrows,k,probability,p1,
                                Arrays.toString(list.getList().get(index).getFirst()),
                                Arrays.toString(list.getList().get(k).getFirst()),
                                distance, Arrays.toString(result)
                                );
                    } else {
                        matrix[0][0]++;
                    }
                }
                
            }
            
            boolean status = reader.readNext();
            if(status==false){
                thenStop = true;
            } else {
                String desc2 = reader.getString();
                String[] tokens2 = desc2.split("\\s+");
                nrows = Integer.parseInt(tokens2[3]);
                //System.out.println(" NROWS = " + nrows);
            }
            counter++;
        }
        
        System.out.println("entries read = " + counter );
        System.out.println("\nMATRIX\n");
        System.out.printf("positives = %d, %d\n",countPos, countNeg);
        System.out.printf("%12d  %12d\n",matrix[0][0],matrix[0][1]);
        System.out.printf("%12d  %12d\n",matrix[1][0],matrix[1][1]);
    }
    
    
    public static void test2(String networkFile){
        
        EJMLModelEvaluator model = new EJMLModelEvaluator(networkFile);
        String file = "xyz.lsvm";
        //String file = "v2/abc_test.lsvm";
        
        LSVMFileReader lsvm = new LSVMFileReader(file,6);
        lsvm.setClasses(3);
        
        TextFileReader reader = new TextFileReader();
        
        reader.open(file);
        
        int counter = 0;
        int falsecounter = 0;
        ClassifierEvaluate eval = new ClassifierEvaluate(3);
        
        while(reader.readNext()){
            
            DataPair pair = lsvm.toData(reader.getString());
            float[] input = DataArrayUtils.toFloat(pair.getFirst());
            float[] desired = DataArrayUtils.toFloat(pair.getSecond());
            float[] output = new float[3];
            model.feedForwardSoftmax(input, output);
            int desiredClass = model.getClass(desired);
            int outputClass = model.getClass(output);
            
            eval.process(desired, output);
            
            counter++;
            
            if(outputClass!=desiredClass&&desiredClass==0){
                System.out.println(Arrays.toString(input) + " => " + Arrays.toString(output));
                //System.out.printf("%5d %5d\n",outputClass,desiredClass);  
                falsecounter++;
            }
            
            if(outputClass==desiredClass&&desiredClass==2){
                System.out.println("*********** " + Arrays.toString(input) + " => " + Arrays.toString(output));
                //System.out.printf("%5d %5d\n",outputClass,desiredClass);  
                falsecounter++;
            }
            
        }
        
        System.out.printf("%8d %8d\n",counter,falsecounter);
        eval.show();
    }
    
    public static void testTree(String networkFile){
        
        EJMLModelEvaluator model = new EJMLModelEvaluator(networkFile);
        String file = "tree_falsepositive.csv";
        
        TextFileReader r = new TextFileReader(file);
        r.setSeparator(",");
        
        DataPairList list = new DataPairList();
        
        while(r.readNext()==true){
           double[] first = r.getAsDoubleArray(0, 6);
           double[] second = r.getAsDoubleArray(6, 1);
           list.add(new DataPair(first,second));
        }
        float[] output = new float[3];
        int counter = 0;
        int counterLow = 0;
        
        for(int i = 0; i < list.getList().size(); i++){
            float[] input = list.getList().get(i).floatFirst();
            model.feedForwardSoftmax(input, output);
            double factor = output[2]*
                    list.getList().get(i).getSecond()[0];
            System.out.printf("%8.5f %8.5f %8.5f : %s\n",output[0],
                    list.getList().get(i).getSecond()[0],
                    factor,
                    Arrays.toString(output) + " ==> " + Arrays.toString(input)
                    );
            counter++;

            if(factor<0.5) counterLow++;

        }
        System.out.printf("CONFUSIONS %8d %8d\n",counter,counterLow);
    }
    
    public static void main(String[] args){
        //Test.test1("network/5038/default/trackClassifier.network");
        //System.out.println("EVALUATING SHIFT TOW");
        //Test.test2("network/tc_shift_two.nnet");
        //System.out.println("EVALUATING SHIFT ALL");
        //Test.test3("network/tc_shift_closestNeigthbor.nnet");
        //System.out.println("EVALUATING SHIFT EXTENDED");
        //Test.test2("network/tc_shift_extended.nnet");
        System.out.println("EVALUATING SHIFT RANDOM");
        
        Test.testTree("network/tc_shift_closestNeigthbor.nnet");
        //Test.test2("network/tc_shift_random.nnet");
        //Test.test3("network/tc_shift_random.nnet");
        //System.out.println("EVALUATING CLAS12 Network");
        //Test.test2("network/5038/default/trackClassifier.network");
    }
}
