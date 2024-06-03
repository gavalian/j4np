/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsNetwork;
import j4ml.ejml.EJMLModel;
import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Query;
import j4np.hipo5.io.HipoReader;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CentralDataReader {
    
    Bank b = null;
    public CentralDataReader(){
        
    }
    
    public float[] getFeatures(Bank b, int row){
        float[] features = new float[4];
        
        Vector3D v = new Vector3D();
        v.setXYZ(b.getFloat("xo", row), b.getFloat("yo", row), b.getFloat("zo", row));
        features[0] = (float) ((v.rho()-60)/80);
        features[1] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
        v.setXYZ(b.getFloat("xe", row), b.getFloat("ye", row), b.getFloat("ze", row));
        features[2] = (float) ((v.rho()-60)/80);
        features[3] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
        return features;
    }
    
    public float[] addFeatures(float[] f1, float[] f2, float[] f3, float[] f4){
        float[] fr = new float[f1.length+f2.length+f3.length+f4.length];
        int order = 0;
        for(int i = 0; i < f1.length; i++){ fr[order] = f1[i]; order++;}
        for(int i = 0; i < f2.length; i++){ fr[order] = f2[i]; order++;}
        for(int i = 0; i < f3.length; i++){ fr[order] = f3[i]; order++;}
        for(int i = 0; i < f4.length; i++){ fr[order] = f4[i]; order++;}
        return fr;
    }
    
    public float[] getFeatures(Bank b, int[] index){
        float[] f1 = this.getFeatures(b, index[0]);
        float[] f2 = this.getFeatures(b, index[1]);
        float[] f3 = this.getFeatures(b, index[2]);
        float[] f4 = this.getFeatures(b, index[3]);
        return this.addFeatures(f1, f2, f3, f4);
    }
    
    public DataList getDataList(Bank b, List<Integer> index){
        DataList list = new DataList();
        float[] f1 = getFeatures(b, 
                new int[]{index.get(0),index.get(1),index.get(2),index.get(3)
                });

        float[] l1 = new float[]{1.0f,0.0f};
        list.add(new DataEntry(f1,l1));

        float[] f2 = getFeatures(b, 
                new int[]{index.get(0),index.get(1),index.get(4),index.get(5)
                });
        
        float[] l2 = new float[]{1.0f,0.0f};
        list.add(new DataEntry(f2,l2));
        
        float[] f3 = getFeatures(b, 
                new int[]{index.get(2),index.get(3),index.get(4),index.get(5)
                });
        
        float[] l3 = new float[]{1.0f,0.0f};
        list.add(new DataEntry(f3,l3));
               
       return list;
    }
    
    public DataList getDataListFalse(Bank b, List<Integer> index){
        DataList list = new DataList();
        DataEntry e1 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(2),index.get(3)},0);
        if(e1!=null) list.add(e1);
        DataEntry e2 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(2),index.get(3)},1);
        if(e2!=null) list.add(e2);
        DataEntry e3 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(2),index.get(3)},2);
        if(e3!=null) list.add(e3);
        DataEntry e4 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(2),index.get(3)},3);
        if(e4!=null) list.add(e4);
        
        DataEntry e5 = getDataEntryFalse(b, new int[]{index.get(2),index.get(3),index.get(4),index.get(5)},0);
        if(e5!=null) list.add(e5);
        DataEntry e6 = getDataEntryFalse(b, new int[]{index.get(2),index.get(3),index.get(4),index.get(5)},1);
        if(e6!=null) list.add(e6);
        DataEntry e7 = getDataEntryFalse(b, new int[]{index.get(2),index.get(3),index.get(4),index.get(5)},2);
        if(e7!=null) list.add(e7);
        DataEntry e8 = getDataEntryFalse(b, new int[]{index.get(2),index.get(3),index.get(4),index.get(5)},3);
        if(e8!=null) list.add(e8);
        
        DataEntry e9 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(4),index.get(5)},0);
        if(e9!=null) list.add(e9);
        DataEntry e10 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(4),index.get(5)},1);
        if(e10!=null) list.add(e10);
        DataEntry e11 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(4),index.get(5)},2);
        if(e11!=null) list.add(e11);
        DataEntry e12 = getDataEntryFalse(b, new int[]{index.get(0),index.get(1),index.get(4),index.get(5)},3);
        if(e12!=null) list.add(e12);
        
        return list;
    }
    
    public DataEntry getDataEntryFalse(Bank b, int[] index, int which){
        int key = index[which];
        int layer  = b.getInt("layer", key);
        int sector = b.getInt("sector", key);
        int bin = -1;
        for(int i = 0; i < b.getRows(); i++){
            if(b.getInt("layer", i)==layer&&b.getInt("sector", i)==sector&&b.getInt("status", i)==0){
                bin = i;
            }
        }
        if(bin>=0){
            index[which] = bin;
            float[] f1 = getFeatures(b, index);
            return new DataEntry(f1,new float[]{0.0f,1.0f});
        }
        return null;
    }
    public DataList  readFile(String h5file, int max){
        DataList list = new DataList();
        
        HipoReader r = new HipoReader(h5file);
        Bank[] b = r.getBanks("cvtml::clusters");
        CentralDataReader cr = new CentralDataReader();
        
        Query q = new Query(b[0],"status==1");
        int counter = 0;
        while(r.nextEvent(b)){
            if(b[0].getRows()>0){
               float[] ft = cr.getFeatures(b[0], 4);
               //System.out.printf("%5d : %s\n",i,Arrays.toString(ft));
               List<Integer> iter = q.getIterator(b[0]);
               //for(int k = 0; k < iter.size();k++) { System.out.printf(" %d [%2d] " ,iter.get(k));
               //System.out.println();
               if(iter.size()>5){
                   DataList listTrue = cr.getDataList(b[0], iter);                           
                   DataList listFalse = cr.getDataListFalse(b[0], iter);
                   
                   //System.out.println(" reduction ");
                   listFalse.reduce(listTrue.getList().size());
                   list.getList().addAll(listTrue.getList());
                   list.getList().addAll(listFalse.getList());
               }
            }
            
            counter++; if(counter>max) break;
        }
        return list;
    }
    
    
    public void readFileShow(String h5file){
        HipoReader r = new HipoReader(h5file);
        Bank[] b = r.getBanks("cvtml::clusters");
        
        EJMLModel model = new EJMLModel("regression6.network");
        
        System.out.println(model.summary());
        
        int counter = 0;
        float[] output = new float[2];
        
        while(r.nextEvent(b)){
            if(b[0].getRows()>0){
                System.out.println(" event = ");
                DataClusters c = new DataClusters();
                c.analyze(b[0]);
                c.show();
                
                List<DataClusters.Segment> segments = c.getSegments();
                
                for(DataClusters.Segment s : segments) {
                    float[] features = this.getFeatures(b[0], s.index);
                    float[] result = new float[2];
                    model.feedForwardSoftmax(features, result);
                    //System.out.println(Arrays.toString(features) + " ===> "  +  Arrays.toString(output));
                    s.probability = result[0];
                    System.out.println(s);
                    System.out.println(Arrays.toString(features) + " ===> "  +  Arrays.toString(result));
                }
                
            }counter++;
            if(counter>1) break;
        }
    }
    
    public static void main(String[] args){
        
        CentralDataReader cr = new CentralDataReader();
        
        
        cr.readFileShow("/Users/gavalian/Work/Software/project-10.8/study/central/munosecSVTVz2.hipo");
        /*DataList datafull = cr.readFile("/Users/gavalian/Work/Software/project-10.8/study/central/munosecSVTVz1.hipo",20000);
        //for(int i =0; i < 5; i++) datafull.shuffle();
        
        DataList[] data = DataList.split(datafull, 0.8,0.2);
        
        data[0].shuffle();
        data[0].show();
        
        DeepNettsNetwork regression = new DeepNettsNetwork();
        regression.activation(ActivationType.RELU); // or ActivationType.TANH
        regression.outputActivation(ActivationType.SOFTMAX);
        regression.learningRate(0.001);
        regression.init(new int[]{16,24,24,8,2});
        
        for(int k = 0; k < 12; k++){
            regression.train(data[0],1000);
            //regression.train(list2,12000);
            System.out.println(" saving intermidiate......");
            regression.save("regression"+ k +".network");
            regression.evaluate(data[1]);
            data[1].export("evaluation"+k+".csv");
        }
        */
        /*HipoReader r = new HipoReader("/Users/gavalian/Work/Software/project-10.8/study/central/munosecSVTVz1.hipo");
        Bank[] b = r.getBanks("cvtml::clusters");
        
        
        Query q = new Query(b[0],"status==1");
        
        for(int i = 0; i < 50; i++){
            r.nextEvent(b);
            if(b[0].getRows()>0){
               float[] ft = cr.getFeatures(b[0], 4);
               System.out.printf("%5d : %s\n",i,Arrays.toString(ft));
               List<Integer> iter = q.getIterator(b[0]);
               for(int k = 0; k < iter.size();k++) System.out.printf(" %d " ,iter.get(k));
               System.out.println();
               if(iter.size()>5){
                   DataList list = cr.getDataList(b[0], iter);
                   
                   list.show();
                   
                   DataList list2 = cr.getDataListFalse(b[0], iter);
                   
                   System.out.println(" FALSE ");
                   list2.show();
                   System.out.println(" reduction ");
                   list2.reduce(3);
                   list2.show();
               }
            }
        }*/
    }
}
