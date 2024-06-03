/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central2;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsNetwork;
import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Query;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author gavalian
 */
public class CentralReader {
    public static Random rand = new Random();
    
    public static void show(Bank b){
        Query q1 = new Query(b,"layer==1");
        Query q2 = new Query(b,"layer==2");
        //b.show();
        List<Integer> l1 = q1.getIterator(b);
        List<Integer> l2 = q2.getIterator(b);
        System.out.println(" an event");
        for(Integer i : l1) System.out.printf("%4d ",i);
        System.out.println();
        for(Integer i : l2) System.out.printf("%4d ",i);
        System.out.println();
        
    }
    
    public static List<Trajectory> getTrajectory(Bank b, int[] idx){
        Query[] q = new Query[2];
        for(int i = 0; i < 2; i++){
            q[i] = new Query(b,String.format("layer==%d", idx[i]));
        }
        List<Trajectory> list = new ArrayList<>();
        List<Integer> iter1 = q[0].getIterator(b);
        List<Integer> iter2 = q[1].getIterator(b);
        for(int i1 = 0; i1 < iter1.size(); i1++){
            for(int i2 = 0; i2 < iter2.size(); i2++){
                int l1 = b.getInt("sector", i1);
                int l2 = b.getInt("sector", i2);
                if(l1==l2){
                    Trajectory t = new Trajectory();
                    t.index.add(iter1.get(i1));
                    t.index.add(iter2.get(i2));
                    list.add(t);
                }
            }
        }
        return list;
    }
    public static DataList getEvent(Bank b){
        return getEvent(b,false);
    }
    
    public static List<Trajectory> getEventTrajectories(Bank b){
        List<Trajectory> list1 = CentralReader.getTrajectory(b, new int[]{1,2});
        List<Trajectory> list2 = CentralReader.getTrajectory(b, new int[]{3,4});
        List<Trajectory> list3 = CentralReader.getTrajectory(b, new int[]{5,6});
        List<Trajectory> list12 = Trajectory.combine(list1, list2);
        List<Trajectory> list = Trajectory.combine(list12, list3);
        return list;
    }
    
     public static DataList getEvent2(Bank b){
    
        DataList data = new DataList();
        
        int[] rows = new int[]{1,2};
        double layer = rand.nextDouble();
        if(layer<0.5) rows = new int[]{3,4};
        
        List<Trajectory> list1 = CentralReader.getTrajectory(b, rows);
        //List<Trajectory> list2 = CentralReader.getTrajectory(b, new int[]{3,4});
        List<Trajectory> list2 = CentralReader.getTrajectory(b, new int[]{5,6});
        
        int which = 8;
        
        double key = rand.nextDouble();
        if(key>0.35) which = 9;
        if(key>0.65) which = 11;
        
        int how = 7;
        
        double ckey = rand.nextDouble();
        if(ckey>0.35) how = 10;
        if(ckey>0.65) how = 12;
        //System.out.printf("working on %d %d\n",how, which);
        List<Trajectory> list3 = CentralReader.getTrajectory(b, new int[]{how,which});
        
        List<Trajectory> list12 = Trajectory.combine(list1, list2);
        
        for(Trajectory t : list12) t.setStatus(b);
        for(Trajectory t : list3) t.setStatus(b);
        
        if(list12.size()>0&&list12.get(0).status==1&&list3.size()>0){
            List<Trajectory> candTrue = new ArrayList<>();
            candTrue.add(list12.get(0));
            list12.remove(0);
            List<Trajectory> candFalse = Trajectory.filter(list12, candTrue.get(0), 3);
            //System.out.println("EXTRACTED");
            //System.out.println(candTrue.get(0));
            //System.out.println(candFalse.get(0));
            //System.out.println(list3.get(0));
            if(list3.get(0).status==1&&candFalse.size()>0){
                Trajectory tt = Trajectory.combine(candTrue.get(0),list3.get(0));
                Trajectory tf = Trajectory.combine(candFalse.get(0),list3.get(0));
                List<float[]> featTrue = tt.getFeatures(b);
                List<float[]> featFalse = tf.getFeatures(b);
                //System.out.println(Arrays.toString(DataEntry.combine(featTrue)));
                //System.out.println(Arrays.toString(DataEntry.combine(featFalse)));
                data.add(new DataEntry(DataEntry.combine(featTrue),new float[]{1.0f,0.0f}));
                data.add(new DataEntry(DataEntry.combine(featFalse),new float[]{0.0f,1.0f}));
            }
        }
        
        
        
        //List<Trajectory>   list = Trajectory.combine(list12, list3);
        
        //System.out.println(" SIZE = " + list.size());
        //for(Trajectory t : list){ t.setStatus(b); System.out.println(t);}
        
        
        
        return data;
    }
     
    public static DataList getEvent(Bank b, boolean all){
    
        DataList data = new DataList();
        List<Trajectory> list1 = CentralReader.getTrajectory(b, new int[]{1,2});
        List<Trajectory> list2 = CentralReader.getTrajectory(b, new int[]{3,4});
        List<Trajectory> list3 = CentralReader.getTrajectory(b, new int[]{5,6});
        
        
        
        List<Trajectory> list12 = Trajectory.combine(list1, list2);
        List<Trajectory> list = Trajectory.combine(list12, list3);
        
        if(all==true){

            for(Trajectory t : list){ 
                t.setStatus(b);
                List<float[]> features = t.getFeatures(b);
                //System.out.println(" SIZE = " + features.size());
                float[] f = DataEntry.combine(features);
                if(t.status>0){
                    data.add(new DataEntry(f,new float[]{1.0f,0.0f}));                    
                } else {
                    data.add(new DataEntry(f,new float[]{0.0f,1.0f}));
                }
            }
            
            return data;
        }
        
        
        for(Trajectory t : list){ t.setStatus(b);}
        
        if(list.size()<1) return data;
        if(list.get(0).status==1){
            int howMany = rand.nextInt(3);
            List<Trajectory>  listF = Trajectory.filter(list, list.get(0), howMany+2);
            
            if(listF.size()>0){
                List<float[]> f2 = listF.get(0).getFeatures(b);
                float[] ff = DataEntry.combine(f2);
                data.add(new DataEntry(ff,new float[]{0.0f,1.0f}));
            }
            /*for(Trajectory tf : listF){
                List<float[]> f2 = tf.getFeatures(b);
                float[] ff = DataEntry.combine(f2);
                data.add(new DataEntry(ff,new float[]{0.0f,1.0f}));
            }*/
            
            List<float[]> features = list.get(0).getFeatures(b);
            //System.out.println(" SIZE = " + features.size());
            float[] f = DataEntry.combine(features);            
            data.add(new DataEntry(f,new float[]{1.0f,0.0f}));
            //System.out.println(Arrays.toString(f));
            
        }
        return data;
    }
    
    public static void evaluate(String file){
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("cvtml::clusters");

        EJMLModel model = new EJMLModel("cvt6.network");
        for(int i = 0; i < 600; i++){
            r.nextEvent(b);
            
            List<Trajectory> traj = CentralReader.getEventTrajectories(b[0]);
            System.out.println(" an event with size = " + traj.size() + "  BANK = SIZE = " + b[0].getRows());
            for(int j = 0; j < traj.size(); j++){
                float[] results = new float[2];                
                List<float[]> features = traj.get(j).getFeatures(b[0]);
                model.feedForwardSoftmax(DataEntry.combine(features), results);
                traj.get(j).probability = results[0];
            }
                        
            
            int  highest = Trajectory.getHighest(traj);
            if(highest>=0){
                Trajectory reference = traj.get(highest);
                traj.remove(highest);
                
                List<Trajectory> reduced = Trajectory.reduce(traj, reference);
                
                reduced.add(0, reference);
                for(Trajectory t : reduced) if(t.probability>0.95) System.out.println(t);
            }
        }
    }
    
    public static void read(){
        String file = "/Users/gavalian/Work/Software/project-10.8/study/central/MLSample1.hipo";
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("cvtml::clusters");
        DataList data = new DataList();
        
        for(int i = 0; i < 140000; i++){
            r.nextEvent(b);
            DataList list = CentralReader.getEvent2(b[0]);
            data.getList().addAll(list.getList());
        }
        
        data.shuffle();
        data.shuffle();
        data.show();
        
        DeepNettsNetwork regression = new DeepNettsNetwork();
        regression.activation(ActivationType.RELU); // or ActivationType.TANH
        regression.outputActivation(ActivationType.SOFTMAX);
        regression.learningRate(0.001);
        regression.init(new int[]{24,24,24,8,2});
        
        regression.train(data, 5000);
        
        regression.save("cvt6.network");
    }
    
    public static void train(){
         String file = "/Users/gavalian/Work/Software/project-10.8/study/central/munosecSVTVz2.hipo";
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("cvtml::clusters");
        DataList data = new DataList();
        
        for(int i = 0; i < 460000; i++){
            r.nextEvent(b);
            if(b[0].getRows()>0){
                DataList data2 = CentralReader.getEvent(b[0], false);
                
                //data.show();
                data.getList().addAll(data2.getList());
                /*CentralReader.show(b[0]);
                List<Trajectory> list1 = CentralReader.getTrajectory(b[0], new int[]{1,2});
                List<Trajectory> list2 = CentralReader.getTrajectory(b[0], new int[]{3,4});
                List<Trajectory> list3 = CentralReader.getTrajectory(b[0], new int[]{5,6});
                
                List<Trajectory> list12 = Trajectory.combine(list1, list2);
                
                List<Trajectory> list = Trajectory.combine(list12, list3);
                
                for(Trajectory t : list){ t.setStatus(b[0]); System.out.println(t);}
                //List<Trajectory> listF = Trajectory.filter(list, list.get(0), 2);
                
                //for(Trajectory t : listF){ t.setStatus(b[0]);System.out.println(t); }
                
                
                List<float[]> features = list.get(0).getFeatures(b[0]);
                System.out.println(" SIZE = " + features.size());
                float[] f = DataEntry.combine(features);
                
                System.out.println(Arrays.toString(f));
                */
                /*
                
                List<Trajectory> listM = Trajectory.combine(list2, list3);
                System.out.println(" SIZE = " + list.size());
                for(Trajectory t : list){ t.setStatus(b[0]);System.out.println(t); }
                System.out.println(" SIZE M = " + listM.size());
                for(Trajectory t : listM){ t.setStatus(b[0]);System.out.println(t); }*/
            }
        }
        
        data.shuffle();
        data.shuffle();
        data.show();
        
        DeepNettsNetwork regression = new DeepNettsNetwork();
        regression.activation(ActivationType.RELU); // or ActivationType.TANH
        regression.outputActivation(ActivationType.SOFTMAX);
        regression.learningRate(0.001);
        regression.init(new int[]{24,24,24,8,2});
        
        regression.train(data, 500);
        
        regression.save("cvt6.network");
    }
    public static void main(String[] args){
        
        CentralReader.read();
        //CentralReader.train();
        //CentralReader.evaluate("/Users/gavalian/Work/Software/project-10.8/study/central/munosecSVTVz1.hipo");
       
        
    }
}
 