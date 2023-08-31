/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.trainers;

import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.Tracks;
import j4np.neural.data.TrackReader;
import j4np.neural.networks.NeuralTrackFinder;
import j4np.neural.networks.TrackConstructor;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class TrackingTrainer {
    
    Random           r = new Random();
    TrackReader dr = new TrackReader();
    NeuralTrackFinder finder = null;
    TrackConstructor constructor = new TrackConstructor();
    
    
    int  truePositive = 0;
    int falsePositive = 0;
    int falseNegative = 0;
    
    public TrackingTrainer(){
        finder = new NeuralTrackFinder("clas12rga.network",5442);
        finder.initializeNetworks();
    }
    
    public TrackingTrainer(String network, int run){
        finder = new NeuralTrackFinder(network, run);
        finder.initializeNetworks();
    }
    
    private static float[]  applyShift(float[] array, int index, double shift){
        double value = array[index] + shift;
        if(value<0||value>111) value = array[index] - shift;        
        array[index] = (float) value;
        return array;
    }
    
    private static float[]  randomNeighbour(Random r, float[] origin, double min, double max){
       double  howMany = r.nextDouble();
       float[] result = new float[origin.length];
       for(int i = 0; i < result.length; i++) result[i] = origin[i];
       
       if(howMany<0.5){
           double      shift = min + r.nextDouble()*(max-min);
           double        dir = r.nextDouble();
           int    superlayer = r.nextInt(6);
           if(dir<0.5) shift = -shift;
           result = applyShift(result,superlayer,shift);
       } else {
           for(int k = 0; k < 2; k++){
               double      shift = min + r.nextDouble()*(max-min);
               double        dir = r.nextDouble();
               int    superlayer = r.nextInt(6);
               if(dir<0.5) shift = -shift;           
               result = applyShift(result,superlayer,shift);
           }
       }
       return result;
    }
    
    public DataList getDataListWithTag(String file, int tBin, int count){
        DataList dList = new DataList();
        
        HipoReader r = new HipoReader();
        r.setTags((long) tBin);
        r.setDebugMode(0);
        r.open(file);
        
        Event e = new Event();
        int counter = 0;
        Tracks tr = new Tracks();
        Vector3 vertex = new Vector3();
        while(r.hasNext()==true&&counter<count){
            r.nextEvent(e);
            TrackReader.event2track(e, tr);
            tr.vertex(vertex, 0);
            if(tr.chi2(0)<10.0&&vertex.z()>-15&&vertex.z()<5){
                float[] input = new float[6];
                tr.getInput(input, 0);
                counter++;
                if(tr.charge(0)>0){
                    dList.add(new DataEntry(input,new float[]{0.0f,1.0f,0.0f}));
                } else {
                    dList.add(new DataEntry(input,new float[]{0.0f,0.0f,1.0f}));
                }
            }
        }
        return dList;
    }
    
    public void addRandomNegative(DataList list){
        int size = list.getList().size();
        for(int i = 0; i < size; i++){
            float[] input = list.getList().get(i).features();
            float[] inputZero = TrackingTrainer.randomNeighbour(r, input, 2.5/112, 25.5/112);
            list.add(new DataEntry(inputZero,new float[]{1.0f,0.0f,0.0f}));
        }
    }
    
    public void trainRandom(String file, int maximum, String network, int run){
        DataList positive = new DataList();
        DataList negative = new DataList();
        
        for(int i = 0; i < 20; i++){
            DataList dl = this.getDataListWithTag(file, i+1, maximum);
            positive.getList().addAll(dl.getList());
        }
        
        for(int i = 0; i < 20; i++){
            DataList dl = this.getDataListWithTag(file, i+21, maximum);
            negative.getList().addAll(dl.getList());
        }
        
        //positive.show();
        this.addRandomNegative(positive);
        this.addRandomNegative(negative);
        positive.show();
        negative.show();
        positive.shuffle();
        negative.shuffle();
        
        DataList trData = new DataList();        
        trData.getList().addAll(positive.getList());
        trData.getList().addAll(negative.getList());
        
        trData.shuffle();
        trData.export("classifier.csv");
        ClassifierTrainer classifier = new ClassifierTrainer();
        classifier.train(trData, network, run, 25);
        
    }
    
    
    public DataList getDataList(String file, int tBin, int count){
        DataList dataList = new DataList();
        
        int cTruePositive      = 0;
        int cFalsePositive     = 0;
        int cFalseNegative     = 0;
        int cTrueNegative      = 0;
        
        int shift = 1;
        if(tBin<21) shift = 21;
        //float[] inputPos = new float[6];
        //float[] inputNeg = new float[6];
        
        Tracks trList = new Tracks();
        
        for(int i = 0; i < count; i++){
            int fBin = r.nextInt(20) + shift;
            Bank b = dr.readFromFile(file, tBin, fBin);
            finder.getCandidatesFromBank(b, trList);
            finder.getClassifier().evaluate(trList);
            double prob1 = trList.probability(0);
            int    nRows = trList.getRows();
            int    index = trList.getHighestIndex(1, nRows-2);
            double prob2 = trList.probability(index);             
            
            if(prob1>0.5&&prob1>prob2){
                cTruePositive++;
                float[] inputPos = new float[6];
                trList.getInput(inputPos, 0);
                float[] inputZero = TrackingTrainer.randomNeighbour(r, inputPos, 2.5/112, 25.5/112);
                if(tBin<21){ dataList.add(new DataEntry(inputPos, new float[]{0.0f,1.0f,0.0f})); }
                else { dataList.add(new DataEntry(inputPos, new float[]{0.0f,0.0f,1.0f}));}
                dataList.add(new DataEntry(inputZero, new float[]{1.0f,0.0f,0.0f}));
            }
            
            if(prob1>0.5&&prob1<=prob2){
                cFalsePositive++;
                float[] inputPos = new float[6];
                float[] inputNeg = new float[6];
                trList.getInput(inputPos, 0);
                trList.getInput(inputNeg, index);
                if(tBin<21){ dataList.add(new DataEntry(inputPos, new float[]{0.0f,1.0f,0.0f})); }
                else { dataList.add(new DataEntry(inputPos, new float[]{0.0f,0.0f,1.0f}));}
                dataList.add(new DataEntry(inputNeg, new float[]{1.0f,0.0f,0.0f}));
            }
            
            if(prob1<0.5) cFalseNegative++;
            //if(prob1<0.5){ cFalseNegative++; }
            //int  nRows = trList.getRows();
            //int  index = trList.getHighestIndex(1, nRows-2);
            //double prob2 = trList.probability(index);            
            //if(prob2>=prob1) cFalsePositive++;
            //if(prob1>0.5&&prob1>prob2) cTruePositive++;
            //trList.show();
        }
        
        System.out.printf(">>> stats for (%4d) : true positive %5d, false positive = %5d, false negative = %5d\n",
                tBin,cTruePositive, cFalsePositive, cFalseNegative);
        return dataList;
    
    }
        
    public DataEntry getNegative(DataEntry a, DataEntry b){
        float[] ia = a.features();
        float[] ib = b.features();
        
        constructor.reset();
        for(int i = 0; i < 6; i++){
            constructor.add(1, i+1, i+1, ia[i]);
        }
        
        for(int i = 0; i < 6; i++){
            constructor.add(1, i+1, i+1+6, ib[i]);
        }
        
        Tracks list = new Tracks();        
        constructor.sectors[0].create(list, 1);
        finder.getClassifier().evaluate(list);
        double prob1 = list.probability(0);
        int    nRows = list.getRows();
        int    index = list.getHighestIndex(1, nRows-2);
        double prob2 = list.probability(index);
        
        if(prob2>=prob1){ 
            float[] inputZero = new float[6];
            list.getInput(inputZero, index);
            
            //System.out.println(" 1 " + list.dataNode().rowToString(0) );
            //System.out.println(" 0 " + list.dataNode().rowToString(index) );
            
            falsePositive++;
            return new DataEntry(inputZero,new float[]{1.0f,0.0f,0.0f});
        }
        
        truePositive++;
        
        if(prob1>0.5){ falseNegative++;}
        float[] inputZero = TrackingTrainer.randomNeighbour(r, ia, 2.5/112., 28./112);
        return new DataEntry(inputZero,new float[]{1.0f,0.0f,0.0f});
    }
    
    public void trainOnFalse(String file, int maximum, String network, int run){        
        //finder = new NeuralTrackFinder("clas12rga.network",5442);
        //finder.initializeNetworks();
        DataList trData = new DataList();
        List<DataList> pos = new ArrayList<>();
        List<DataList> neg = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            DataList dl = this.getDataListWithTag(file, i+1, maximum);
            pos.add(dl);
        }
        
        for(int i = 0; i < 20; i++){
            DataList dl = this.getDataListWithTag(file, i+21, maximum);
            neg.add(dl);
        }

        for(int i = 0; i < pos.size(); i++){
            for(int e = 0; e < pos.get(i).getList().size(); e++){
                int n = r.nextInt(neg.size());
                if(neg.get(n).getList().size()>0){
                    int order = r.nextInt(neg.get(n).getList().size());
                
                    DataEntry entry = this.getNegative(pos.get(i).getList().get(e), neg.get(n).getList().get(order));
                    trData.add(entry);
                }
            }
            System.out.printf(">>>>> POS %4d ::: true positive %9d (%8.5f) , false positive %9d (%8.5f), false negative %9d (%8.5f) total = %9d \n",i,
                truePositive, ((double) truePositive)/(truePositive+falsePositive),
                falseNegative ,((double) falseNegative)/(truePositive+falsePositive),falsePositive,
                ((double) falsePositive)/(truePositive+falsePositive),truePositive+falsePositive);
        }
        
        for(int i = 0; i < neg.size(); i++){
            for(int e = 0; e < neg.get(i).getList().size(); e++){
                
                int n = r.nextInt(pos.size());
                if(pos.get(n).getList().size()>0){
                    int order = r.nextInt(pos.get(n).getList().size());
                
                    DataEntry entry = this.getNegative(neg.get(i).getList().get(e), pos.get(n).getList().get(order));
                    trData.add(entry);
                }
            }
            System.out.printf(">>>>> NEG %4d ::: true positive %9d (%8.5f) , false positive %9d (%8.5f), false negative %9d (%8.5f) total = %9d \n",i,
                truePositive  , ((double) truePositive)/(truePositive+falsePositive),
                falsePositive ,((double) falsePositive)/(truePositive+falsePositive),
                falseNegative ,((double) falseNegative)/(truePositive+falsePositive),
                truePositive+falsePositive);
        }
        
        for(int i = 0; i < pos.size(); i++) trData.getList().addAll(pos.get(i).getList());
        for(int i = 0; i < neg.size(); i++) trData.getList().addAll(neg.get(i).getList());
        
        System.out.printf(">>>>> READ ::: true positive %5d (%8.5f) , false positive %5d (%8.5f), total = %5d \n",
                truePositive, ((double) truePositive)/(truePositive+falsePositive),falsePositive,
                ((double) falsePositive)/(truePositive+falsePositive),truePositive+falsePositive);
        /*
        for(int i = 0; i < 20; i++){
            DataList list = this.getDataList(file, i+1, 8000);
            trData.getList().addAll(list.getList());
        }
        
        for(int i = 20; i < 40; i++){
            DataList list = this.getDataList(file, i+1, 8000);
            trData.getList().addAll(list.getList());
        }*/
        
        trData.shuffle();
        
        ClassifierTrainer classifier = new ClassifierTrainer();
        classifier.train(trData, network, run, 750);
        trData.show();
    }
    
    public static void main(String[] args){
        TrackingTrainer tr = new TrackingTrainer("clas12rga-nuevo.network",5442);
        DataList dl = tr.getDataListWithTag("run_005442_for_ai_tr.h5", 4, 50);
        dl.export("before-fix.csv");
        
        //tr.trainRandom("run_005442_for_ai_tr.h5",6000,"clas12rga-nuevo.network",5442);
        //tr.trainOnFalse("run_005442_for_ai_tr.h5",15000,"clas12rga-nuevo.network",5443);
        
    }
}
