/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.classifier.mlp;

import deepnetts.data.TabularDataSet;
import j4ml.classifier.data.Combinatorics;
import j4ml.classifier.data.DataLoader;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import java.util.Arrays;
import javax.visrec.ml.data.DataSet;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class ClassifierMLP {
    
    
    ClassifierNetwork classifier = new ClassifierNetwork();
    int nEpochs = 125;
    DataPairList   previous = new DataPairList();
    String outputFileName = "trackClassifier.network";
    
    public ClassifierMLP(){
        
    }
    public int getTrueMatch(DataPairList list){
        for(int i = 0; i < list.getList().size();i++){
           double[] desired = list.getList().get(i).getSecond();
           if(desired[0]<0.2) return i;
        }
        return -1;
    }
    
    public int getTrueLabel(DataPairList list){

        for(int i = 0; i < list.getList().size();i++){
           double[] desired = list.getList().get(i).getSecond();
           if(desired[0]<0.2){
               if(desired[1]>0.5){
                   return 1;
               } else {
                   return 2;
               }
           }
        }
        return -1;
    }
    
    public double getProbability(float[] output){
        double prob = 0;
        for(int i = 0; i < output.length; i++)
            if(output[i]>prob) prob = output[i];
        return prob;
    }
    
    public void train(String filename, int max){
        
        previous.getList().clear();
        classifier.init(new int[]{6,12,24,12,3});
        
        DataPairList list = DataLoader.load(filename,max);
        //list.show();        
        DataPairList resultAll = DataLoader.generateFalse(list);
        DataPairList result = resultAll.getNormalizedFirst(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112}
                );
        System.out.println("Neural Netwrok Training Data Size = " + result.getList().size());
        result.show();
        result.scan();
        
        DataSet     dataset = DataLoader.convertList(result);
        dataset.shuffle();
        
        //System.out.println(Arrays.toString(dataset.getColumnNames()));
        /*TabularDataSet  dataset2 = new TabularDataSet(6,3);
        dataset2.add(new TabularDataSet.Item(new float[]{0.1f,0.2f,0.3f,0.4f,0.4f,0.5f}, 
                new float[]{1.0f,0.0f,0.0f}));
        dataset2.setColumnNames(new String[]{"a","b","c","d","e","f","g","h","i"});*/
        classifier.train(dataset, nEpochs);
        
        classifier.save("trackClassifierFirstPass.network");
    }
    
    public void evaluate(String filename, int max){
        DataPairList list = DataLoader.loadCombinatorics(filename, max);
        
        /*for(DataPair pair : list.getList()){
            //System.out.println(pair);
            pair.show();
        }*/
        DataPairList result = list.getNormalizedFirst(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112}
                );        
        DataSet     dataset = DataLoader.convertList(result);
        dataset.shuffle();
        classifier.evaluate(dataset);
    }
    
    public DataPairList  getFalseTracks(Event event){
        
        DataPairList  dataResult = new DataPairList();
        DataPairList list = DataLoader.getEventTracks(event);
        DataPairList res = list.getNormalizedFirst(
                new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112});
        
        int     trueIndex = this.getTrueMatch(res);
        int     trueLabel = this.getTrueLabel(res);
        //System.out.println("event # " + counter);
        if(trueIndex>=0){
            
            //System.out.println("true index = " + trueIndex);
                classifier.neuralNet.setInput(res.getList().get(trueIndex).floatFirst());
                float[] output = classifier.neuralNet.getOutput();
                double  trueProb = this.getProbability(output);
                
                int    maxIndex = -1;
                double maxProb  = 0.0;
                int    maxLabel = 0;
                
                for(int i = 0; i < res.getList().size(); i++){
                    if(i!=trueIndex){
                        classifier.neuralNet.setInput(res.getList().get(i).floatFirst());
                        float[] out2 = classifier.neuralNet.getOutput();
                        if(out2[trueLabel]>trueProb){
                            maxIndex = i;
                            maxProb  = out2[trueLabel];
                        }
                    }
                }
                //System.out.printf("prob = %f, label = %d, index = %d",maxIndex);
                
                if(maxIndex>=0){
                    
                    double distance = Combinatorics.distance(
                            list.getList().get(trueIndex).getFirst(),list.getList().get(maxIndex).getFirst() );
                    if(distance > 5.0){
                        dataResult.add(res.getList().get(trueIndex));
                        dataResult.add(res.getList().get(maxIndex));
                    }
                }
        }
        return dataResult;
    }
    
    public int[] analyze(String file, int tag, int max){
        
        HipoReader reader = new HipoReader();
        reader.setDebugMode(0);
        reader.setTags(tag);
        reader.open(file);
        
        Event event = new Event();
        
        int[] result = new int[]{0,0};
        int counter = 0;
        
        while(reader.hasNext()&&counter<max){
            result[1]++;
            reader.nextEvent(event);
            
            DataPairList list = DataLoader.getEventTracks(event);
            DataPairList res = list.getNormalizedFirst(
                    new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                    new double[]{112,112,112,112,112,112});
            
            
            int     trueIndex = this.getTrueMatch(res);
            int     trueLabel = this.getTrueLabel(res);
            //System.out.println("event # " + counter);
            if(trueIndex>=0){
                //System.out.println("true index = " + trueIndex);
                classifier.neuralNet.setInput(res.getList().get(trueIndex).floatFirst());
                float[] output = classifier.neuralNet.getOutput();
                double  trueProb = this.getProbability(output);
                
                int    maxIndex = -1;
                double maxProb  = 0.0;
                int    maxLabel = 0;
                
                for(int i = 0; i < res.getList().size(); i++){
                    if(i!=trueIndex){
                        classifier.neuralNet.setInput(res.getList().get(i).floatFirst());
                        float[] out2 = classifier.neuralNet.getOutput();
                        if(out2[trueLabel]>trueProb){
                            maxIndex = i;
                            maxProb  = out2[trueLabel];
                        }
                    }
                }
            //System.out.printf("prob = %f, label = %d, index = %d",maxIndex);
            
            if(maxIndex>=0){

                    double distance = Combinatorics.distance(
                        list.getList().get(trueIndex).getFirst(),list.getList().get(maxIndex).getFirst() );
                if(distance>2){
                   /* System.out.printf("true index = %3d, true prob = %6.4f, max index = %3d, max prob = %6.4f distance = %8.5f\n",
                            trueIndex,trueProb,maxIndex,maxProb,distance);
                    System.out.println(
                            "true : " + Arrays.toString(list.getList().get(trueIndex).getFirst())
                                    + " \nfalse : " + Arrays.toString(list.getList().get(maxIndex).getFirst()));*/
                    result[0]++;
                }
            }
            
            /*if(maxIndex>=0){
                
                double[] highest = res.getList().get(maxIndex).getSecond();
                classifier.neuralNet.setInput(res.getList().get(maxIndex).floatFirst());
                float[]  output = classifier.neuralNet.getOutput();
                //System.out.println(Arrays.toString(highest) + " > " + Arrays.toString(output));
                
                if(highest[0]>0.5){
                    result[0]++;
                } else {
                    result[1]++;
                }
            }*/
            }
        }
        return result;
    }
    
    public void analyze(String file, int max){
        int[] misID = new int[]{0,0};
        for(int k = 0; k < 40;k++){
            int[] result = this.analyze(file, k+1, max);
            misID[0] += result[0];
            misID[1] += result[1];
        }
        System.out.println("********************************");
        System.out.printf("false track selected %d, true track selected %d\n",
                misID[0],misID[1]);
        System.out.println("********************************");
    }
    
    
    public DataPairList further(String filename, int tag, int max){
        HipoReader reader = new HipoReader();
        reader.setDebugMode(0);
        reader.setTags(tag);
        reader.open(filename);
        Event event = new Event();
        
        int counter = 0;
        DataPairList list = new DataPairList();
        
        while(reader.hasNext()==true&&counter<max){
            counter++;
            reader.nextEvent(event);
            DataPairList ds = this.getFalseTracks(event);
            if(ds.getList().size()>0){
                list.getList().addAll(ds.getList());
            }
        }
        return list;
    }
            
    public void trainFurther(String filename, int max){
        
       DataPairList  falseList = new DataPairList();
       for(int k = 0; k < 40; k++){
           DataPairList list = this.further(filename, k+1, max);
           falseList.getList().addAll(list.getList());
       }
        
       System.out.printf("***>>> Further Extension size = %d\n",falseList.getList().size());
        falseList.show();;
        
        DataPairList list = DataLoader.load(filename,max);
        //list.show();        
        DataPairList resultAll = DataLoader.generateFalse(list);
        DataPairList result = resultAll.getNormalizedFirst(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112}
                );
        
        result.getList().addAll(falseList.getList());
        
        
        System.out.printf("TRAINING DATA SAMPLE = %d\n", result.getList().size());
        
        DataSet     dataset = DataLoader.convertList(result);
        dataset.shuffle();
        
        //System.out.println(Arrays.toString(dataset.getColumnNames()));
        /*TabularDataSet  dataset2 = new TabularDataSet(6,3);
        dataset2.add(new TabularDataSet.Item(new float[]{0.1f,0.2f,0.3f,0.4f,0.4f,0.5f}, 
                new float[]{1.0f,0.0f,0.0f}));
        dataset2.setColumnNames(new String[]{"a","b","c","d","e","f","g","h","i"});*/
        classifier.train(dataset, nEpochs);
        
        classifier.save(this.outputFileName);
    }
    
    public void trainFurther2(String filename, int max){
        
        DataPairList trList = DataLoader.load(filename,max);
        //list.show();        
        DataPairList trResultAll = DataLoader.generateFalse(trList);
        
        DataPairList trResult = trResultAll.getNormalizedFirst(
                new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112}
        );
        
        DataPairList list = DataLoader.loadCombinatorics(filename, max);
        
        /*for(DataPair pair : list.getList()){
            //System.out.println(pair);
            pair.show();
        }*/
        DataPairList result = list.getNormalizedFirst(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112}
                );
        
        //result.getList().addAll(result1.getList());        
                
        
        int dataCount = result.getList().size();
        int falsePositives = 0;
        int truePositives = 0;
        
        DataPairList addList = new DataPairList();
        System.out.println("data set size = " + result.getList().size());
        
        for(int i = 0; i < dataCount; i++){
            DataPair pair = result.getList().get(i);
            double[] labels = pair.getSecond();
            if(labels[0]<0.2){
                addList.add(pair);
                truePositives++;
            } else {
                float[] input = pair.floatFirst();
                classifier.neuralNet.setInput(input);
                float[] output = classifier.neuralNet.getOutput();
                if(output[0]>0.89){
                    falsePositives++;
                    addList.add(pair);
                }
            }
        }
        System.out.printf(" started with %d events\n",result.getList().size());
        System.out.printf("   ended with %d events\n",addList.getList().size());
        System.out.printf("   false positives count = %d\n",falsePositives);
        System.out.printf("    true positives count = %d\n",truePositives);
        
        previous.getList().addAll(addList.getList());
        
        trResult.getList().addAll(previous.getList());
        
        //DataSet     dataset = DataLoader.convertList(addList);
        DataSet     dataset = DataLoader.convertList(trResult);
        
        dataset.shuffle();
        
        classifier.train(dataset, nEpochs);
        classifier.save(this.outputFileName);
    }
    
    public static void main(String[] args){
        
        ClassifierMLP mlp = new ClassifierMLP();  
        
        int ntrain    = 35000;
        int nvalidate = 25000;
        int ntrainfurther = 35000;
        
        mlp.nEpochs = 125;
        mlp.train("data_extract_classifier.hipo", ntrain); 
        mlp.analyze("data_validate_classifier.hipo", nvalidate);
        
        for(int i = 0; i < 8; i++){
            mlp.outputFileName = String.format("trackClassifier_%d.network", i+1);
            mlp.trainFurther("data_extract_classifier.hipo", ntrainfurther);
            mlp.analyze("data_validate_classifier.hipo", nvalidate);
        }
        
        /*for(int i = 0; i < 8; i++){
            mlp.outputFileName = String.format("trackClassifier_%d.network", i+1);
            mlp.evaluate("data_validate_classifier.hipo", nvalidate);
            mlp.analyze("data_validate_classifier.hipo", nvalidate);        
            mlp.trainFurther("data_extract_classifier.hipo", ntrainfurther);            
        }*/
        /*
        mlp.evaluate("data_validate_classifier.hipo", nvalidate);
        mlp.analyze("data_validate_classifier.hipo", nvalidate);
        
        mlp.trainFurther("data_extract_classifier.hipo", ntrainfurther);
        mlp.evaluate("data_validate_classifier.hipo", nvalidate);
        mlp.analyze("data_validate_classifier.hipo", nvalidate);
        */
        //list.show();
    }
}
