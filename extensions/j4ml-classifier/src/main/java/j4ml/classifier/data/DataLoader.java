/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.classifier.data;

import deepnetts.data.TabularDataSet;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.DataPair;
import j4np.utils.io.DataPairList;
import java.util.List;
import java.util.Random;
import javax.visrec.ml.data.DataSet;

import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Node;
import org.jlab.jnp.hipo4.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class DataLoader {
    
    public static DataPairList loadCombinatorics(String filename, int tag, int max){
        
        Combinatorics combi = new Combinatorics();
        
        DataPairList list = new DataPairList();
        HipoReader reader = new HipoReader();
        reader.setDebugMode(0);
        reader.setTags(tag);
        reader.open(filename);
        Event event = new Event();
        int counter = 0;
        boolean keepReading = true;
        
        while(reader.hasNext()&&keepReading==true){
            
            reader.nextEvent(event);
            
            Node means = event.read(  1001, 4);
            Node charge = event.read( 1001, 1);
            Node vertex = event.read(1001, 7);
            
            Node layers = event.read(2001, 1);
            Node avgs   = event.read(2001, 2);
            
            double vz = vertex.getFloat(2);
            int q = charge.getShort(1);
            
            if(vz>-15&&vz<5){
                double[] first = new double[means.getDataSize()];
                for(int i = 0; i < first.length;i++) first[i] = means.getFloat(i);
                DataPair realTrack = null;
                if(q>0){
                    realTrack = new DataPair(first,new double[]{0.0,1.0,0.0});
                    list.add(new DataPair(first,new double[]{0.0,1.0,0.0}));
                } else {
                    realTrack = new DataPair(first,new double[]{0.0,0.0,1.0});
                    list.add(new DataPair(first,new double[]{0.0,0.0,1.0}));
                }
                
                
                combi.reset();
                for(int j = 0; j < layers.getDataSize(); j++){
                    combi.add(layers.getInt(j)-1, avgs.getFloat(j));
                }
                List<double[]> ctrk = combi.getCombinations();
                //System.out.println("adding combinations # " + ctrk.size());
                for(int j = 0; j < ctrk.size(); j++){
                    double distance = combi.distance(ctrk.get(j), realTrack.getFirst());
                    if(distance<0.001){
                        double[] output = new double[3];
                        for(int oc = 0 ; oc < 3; oc++) output[oc] = realTrack.getSecond()[oc];
                        DataPair dp = new DataPair(ctrk.get(j),output);
                        list.add(dp);
                    } else {
                        DataPair dp = new DataPair(ctrk.get(j),new double[]{1.0,0.0,0.0});
                        list.add(dp);
                    }
                    
                    //System.out.printf("%8.5f : ",distance);dp.show();
                }                
            }                                    
            counter++;
            if(max>0) if(counter>=max) keepReading = false;
        }        
        return list;
    }
    
    
    public static DataPairList load(String filename, int tag, int max){
        DataPairList list = new DataPairList();
        HipoReader reader = new HipoReader();
        reader.setDebugMode(0);
        reader.setTags(tag);
        reader.open(filename);
        Event event = new Event();
        int counter = 0;
        boolean keepReading = true;
        
        while(reader.hasNext()&&keepReading==true){
            reader.nextEvent(event);
            
            Node means = event.read(  1001, 4);
            Node charge = event.read( 1001, 1);
            Node vertex = event.read(1001, 7);
            double vz = vertex.getFloat(2);
            int q = charge.getShort(1);
            
            if(vz>-15&&vz<5){
                double[] first = new double[means.getDataSize()];
                for(int i = 0; i < first.length;i++) first[i] = means.getFloat(i);
                if(q>0){
                    list.add(new DataPair(first,new double[]{0.0,1.0,0.0}));
                } else {
                    list.add(new DataPair(first,new double[]{0.0,0.0,1.0}));
                }
            }
            counter++;
            if(max>0) if(counter>=max) keepReading = false;
        }
        return list;
    }
    
    public static DataPairList getEventTracks(Event event){
        
        Combinatorics combi = new Combinatorics();        
        DataPairList list = new DataPairList();
        
        Node means = event.read(  1001, 4);
        Node charge = event.read( 1001, 1);
        Node vertex = event.read(1001, 7);
        
        Node layers = event.read(2001, 1);
        Node avgs   = event.read(2001, 2);
        
        double vz = vertex.getFloat(2);
        int q = charge.getShort(1);
        
        if(vz>-15&&vz<5){
            double[] first = new double[means.getDataSize()];
            for(int i = 0; i < first.length;i++) first[i] = means.getFloat(i);
            DataPair realTrack = null;
            if(q>0){
                realTrack = new DataPair(first,new double[]{0.0,1.0,0.0});
                list.add(new DataPair(first,new double[]{0.0,1.0,0.0}));
            } else {
                realTrack = new DataPair(first,new double[]{0.0,0.0,1.0});
                list.add(new DataPair(first,new double[]{0.0,0.0,1.0}));
            }
            
            
            combi.reset();
            for(int j = 0; j < layers.getDataSize(); j++){
                combi.add(layers.getInt(j)-1, avgs.getFloat(j));
            }
            
            //combi.show();
            List<double[]> ctrk = combi.getCombinations();
            //System.out.println("adding combinations # " + ctrk.size());
            for(int j = 0; j < ctrk.size(); j++){
                double distance = combi.distance(ctrk.get(j), realTrack.getFirst());
                if(distance<0.001){
                    double[] output = new double[3];
                    for(int oc = 0 ; oc < 3; oc++) output[oc] = realTrack.getSecond()[oc];
                    DataPair dp = new DataPair(ctrk.get(j),output);
                    list.add(dp);
                } else {
                    DataPair dp = new DataPair(ctrk.get(j),new double[]{1.0,0.0,0.0});
                    list.add(dp);
                }                
                //System.out.printf("%8.5f : ",distance);dp.show();
            }                
        }    
        return list;
    }
    
    public static DataPairList loadCombinatorics(String filename, int max){
        DataPairList list = new DataPairList();
        for(int k = 0; k < 40; k++){
            DataPairList dl = DataLoader.loadCombinatorics(filename, k+1, max);
            list.getList().addAll(dl.getList());
        }
        return list;
    }
    
    public static DataPairList load(String filename, int max){
        DataPairList list = new DataPairList();
        for(int k = 0; k < 40; k++){
            DataPairList dl = DataLoader.load(filename, k+1, max);
            list.getList().addAll(dl.getList());
        }
        return list;
    }
    
    public static String[] generateNames(int input, int output){
        String[] names = new String[input+output];
        for(int i = 0; i < input; i++) names[i] = "in" + i;
        for(int i = 0; i < output; i++) names[i+input] = "out" + i;        
        return names;
    }
    
    public static DataSet convertList(DataPairList list){
        TabularDataSet  dataset = new TabularDataSet(6,3);
        for(int i = 0; i < list.getList().size(); i++){
            float[]  input = DataArrayUtils.toFloat(list.getList().get(i).getFirst());
            float[] output = DataArrayUtils.toFloat(list.getList().get(i).getSecond());
            dataset.add(new TabularDataSet.Item(input, output));
        }
        String[] columns = DataLoader.generateNames(6,3);
        dataset.setColumnNames(columns);
        
        System.out.printf("INPUT/OUTPUT = %5d/%5d\n",
                dataset.getNumInputs(),dataset.getNumOutputs());
        return dataset;
    }
    
    public static DataPairList generateFalse(DataPairList list){
        Random rmean = new Random();
        Random rlayer = new Random();
        Random rsign = new Random();
        
        DataPairList result = new DataPairList();
        int size = list.getList().size();
        for(int n = 0; n < size; n++){
            DataPair  pair = list.getList().get(n);
            double[] first = new double[pair.getFirst().length];
            for(int i = 0; i < first.length; i++) first[i] = pair.getFirst()[i];
            int   layer = rlayer.nextInt(5) + 1;
            
            double mean = rmean.nextDouble()*8.0+3.5;
            double sign = rsign.nextDouble();
            
            if(sign<0.5){
                if( (first[layer] - mean)>0.0){
                    first[layer] = first[layer] - mean;
                } else {
                    first[layer] = first[layer] + mean;
                }
            } else {
                if( (first[layer] + mean)>112.0){
                    first[layer] = first[layer] - mean;
                } else {
                    first[layer] = first[layer] + mean;
                }
            }
            result.add(new DataPair(pair.getFirst(),pair.getSecond()));
            result.add(new DataPair(first,new double[]{1.0,0.0,0.0}));
        }
        return result;
    }
    
    
    public static void main(String[] args){
        HipoReader reader = new HipoReader();
        reader.open("data_extract_classifier.hipo");
        
        Event event = new Event();
        
        for(int i = 0; i < 400000; i++){
            reader.nextEvent(event);
            DataPairList list = DataLoader.getEventTracks(event);
            //System.out.println("--- event # " + i + "  size = " + list.getList().size());
            for(DataPair pair : list.getList()){
                //pair.show();
            }
        }
    }
}