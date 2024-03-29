/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.classifier.mlp;

import deepnetts.data.TabularDataSet;
import j4ml.classifier.data.Combinatorics;
import j4ml.classifier.data.DataLoader;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.data.DataNormalizer;
import j4np.utils.io.OptionExecutor;
import j4np.utils.io.OptionStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.visrec.ml.data.DataSet;


/**
 *
 * @author gavalian
 */
public class ClassifierMLP implements OptionExecutor {
    
    
    ClassifierNetwork classifier = new ClassifierNetwork();
    int nEpochs = 125;
    DataList   previous = new DataList();
    String outputFileName = "trackClassifier.network";
    List<String>  summary = new ArrayList<>();
    
    public ClassifierMLP(){
        
    }
    public int getTrueMatch(DataList list){
        for(int i = 0; i < list.getList().size();i++){
           double[] desired = list.getList().get(i).getSecond();
           if(desired[0]<0.2) return i;
        }
        return -1;
    }
    
    public int getTrueLabel(DataList list){

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
        
        DataList result = DataLoader.load(filename,max);
        //list.show();        
        DataList resultAll = DataLoader.generateFalse(result);
        
        DataNormalizer dnorm = new DataNormalizer(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112});
        
        DataList.normalizeInput(result, dnorm);
        
        //System.out.println("Neural Netwrok Training Data Size = " + result.getList().size());
        //list.show();
        //list.scan();
        
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
        DataList result = DataLoader.loadCombinatorics(filename, max);
        
        /*for(DataPair pair : list.getList()){
            //System.out.println(pair);
            pair.show();
        }*/
        DataNormalizer dnorm = new DataNormalizer(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112});
        DataList.normalizeInput(result, dnorm);
        /*DataList result = list.getNormalizedFirst(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112}
                );*/        
        DataSet     dataset = DataLoader.convertList(result);
        dataset.shuffle();
        classifier.evaluate(dataset);
    }
    
    public DataList  getFalseTracks(Event event){
        
        DataList  dataResult = new DataList();
        
        DataList res = DataLoader.getEventTracks(event);
        DataNormalizer dnorm = new DataNormalizer(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112});
        
        DataList.normalizeInput(res, dnorm); 
                
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
                            res.getList().get(trueIndex).getFirst(),res.getList().get(maxIndex).getFirst() );
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
            
            DataList res = DataLoader.getEventTracks(event);
            DataNormalizer dnorm = new DataNormalizer(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112});
            
            DataList.normalizeInput(res, dnorm);

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
                        res.getList().get(trueIndex).getFirst(),res.getList().get(maxIndex).getFirst() );
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
        summary.add(String.format("false track selected %d, true track selected %d",
                misID[0],misID[1]));
        System.out.printf("false track selected %d, true track selected %d\n",
                misID[0],misID[1]);
        System.out.println("********************************");
    }
    
    public void printSummary(){
        System.out.println("\n\n");
        System.out.println("********************************");
        System.out.println("*       TRAINING SUMMARY       *");
        System.out.println("********************************");
        for(int i = 0; i < summary.size(); i++){
            System.out.printf(" stage %4d : %s\n",i+1,summary.get(i));
        }
        System.out.println("********************************");
    }
    
    public DataList further(String filename, int tag, int max){
        HipoReader reader = new HipoReader();
        reader.setDebugMode(0);
        reader.setTags(tag);
        reader.open(filename);
        Event event = new Event();
        
        int counter = 0;
        DataList list = new DataList();
        
        while(reader.hasNext()==true&&counter<max){
            counter++;
            reader.nextEvent(event);
            DataList ds = this.getFalseTracks(event);
            if(ds.getList().size()>0){
                list.getList().addAll(ds.getList());
            }
        }
        return list;
    }
            
    public void trainFurther(String filename, int max){
        
       DataList  falseList = new DataList();
       for(int k = 0; k < 40; k++){
           DataList list = this.further(filename, k+1, max);
           falseList.getList().addAll(list.getList());
       }
        
       System.out.printf("***>>> Further Extension size = %d\n",falseList.getList().size());
        falseList.show();;
        
        DataList result = DataLoader.load(filename,max);
        //list.show();        
        DataList resultAll = DataLoader.generateFalse(result);
        
        DataNormalizer dnorm = new DataNormalizer(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112});
        DataList.normalizeInput(result, dnorm);

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
        
        DataList trResult = DataLoader.load(filename,max);
        //list.show();        
        DataList trResultAll = DataLoader.generateFalse(trResult);
        DataNormalizer dnorm = new DataNormalizer(new double[]{0.0,0.0,0.0,0.0,0.0,0.0},
                new double[]{112,112,112,112,112,112});
        
        DataList.normalizeInput(trResult, dnorm);
   
        DataList result = DataLoader.loadCombinatorics(filename, max);
        
        /*for(DataPair pair : list.getList()){
            //System.out.println(pair);
            pair.show();
        }*/
        DataList.normalizeInput(result, dnorm);
        //result.getList().addAll(result1.getList());        
                
        
        int dataCount = result.getList().size();
        int falsePositives = 0;
        int truePositives = 0;
        
        DataList addList = new DataList();
        System.out.println("data set size = " + result.getList().size());
        
        for(int i = 0; i < dataCount; i++){
            DataEntry pair = result.getList().get(i);
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
    
    @Override
    public void execute(String[] args) {
        OptionStore store = new OptionStore("track-classifier");
        store.addCommand("-train", "train clas12 track classifier");
        store.getOptionParser("-train").addRequired("-t", "training file name");
        store.getOptionParser("-train").addRequired("-v", "validation file name file name");
        store.getOptionParser("-train").addOption("-o", "trackClassifier.network", "network file name");
        store.getOptionParser("-train").addOption("-n", "25000", "maximum number of samples to use");
        store.getOptionParser("-train").addOption("-e", "25", "number of epochs to use");
        
        store.parse(args);
        
        if(store.getCommand().compareTo("-train")==0){
            ClassifierMLP mlp = new ClassifierMLP();
            String filet = store.getOptionParser("-train").getOption("-t").stringValue();
            String filev = store.getOptionParser("-train").getOption("-v").stringValue();
            
            int    ntrain = store.getOptionParser("-train").getOption("-n").intValue();
            int nvalidate = store.getOptionParser("-train").getOption("-n").intValue();
            
            int  nfurther = store.getOptionParser("-train").getOption("-n").intValue();
            int   nEpochs = store.getOptionParser("-train").getOption("-e").intValue();
            String  output = store.getOptionParser("-train").getOption("-o").stringValue();
            
            mlp.outputFileName = output;
            mlp.nEpochs = nEpochs;
            mlp.train(filet, ntrain);            
            mlp.analyze(filev, nvalidate);
            
            mlp.outputFileName = output + ".1";
            mlp.trainFurther(filet, nfurther);
            mlp.analyze(filev, nvalidate);
            
            mlp.printSummary();
                        
            //return;
        }
        
        //store.printUsage();
    }
   
    public static void main(String[] args){
        
        ClassifierMLP mlp = new ClassifierMLP();
        
        mlp.execute(args);
        /*
        ClassifierMLP mlp = new ClassifierMLP();  
        
        String filet = "data_extracted_4029_full_1.hipo";
        String filev = "data_extracted_4029_full_2.hipo";
        
        int ntrain    = 35000;
        int nvalidate = 25000;
        int ntrainfurther = 35000;
        
        mlp.nEpochs = 125;
        
        mlp.train(filet, ntrain);
        
        mlp.analyze(filev, nvalidate);
        
        for(int i = 0; i < 8; i++){
            mlp.outputFileName = String.format("trackClassifier_%d.network", i+1);
            mlp.trainFurther(filet, ntrainfurther);
            mlp.analyze(filev, nvalidate);
        }
        */
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
