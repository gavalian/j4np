/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import deepnetts.net.layers.activation.ActivationType;
import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.deepnetts.DeepNettsNetwork;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.List;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class CentralTrainer {
    public CentralTrainer(){
        
    }
    public DataList read(Event event){
        DataList list = new DataList();
        CompositeNode node = new CompositeNode(17,1,"2s4f4f",2048);
        event.read(node, 17, 1);
        DataLoader.DataEventSvt data = new DataLoader.DataEventSvt(node);
        data.analyze();
        List<DataLoader.DataSegment> segments = data.getSegments(new int[]{0,1,2});
        List<DataLoader.DataSegment> segments2 = data.getSegments(new int[]{1,2,3});
        List<DataLoader.DataSegment> segments3 = data.getSegments(new int[]{2,3,4});
        List<DataLoader.DataSegment> segments4 = data.getSegments(new int[]{3,4,5});        
        segments.addAll(segments2);
        segments.addAll(segments3);
        segments.addAll(segments4);
        for(DataLoader.DataSegment s :segments )
        {
            if(s.countStatus()==3||s.countStatus()==2){
                //System.out.println(s);
                DataEntry entry = new DataEntry(s.getFeatures(),s.getLabel3());
                list.add(entry,true);
            }
        }
        return list;
    }
    
    public DataList read(String h5file){
        DataList list = new DataList();
        CompositeNode node = new CompositeNode(17,1,"2s4f4f",2048);
        HipoReader r = new HipoReader(h5file);
        Event event = new Event();
        Random rn = new Random();
        while(r.hasNext()){
            r.next(event);
            event.read(node, 17, 1);
            DataLoader.DataEventSvt data = new DataLoader.DataEventSvt(node);
            data.analyze();
            List<DataLoader.DataSegment> segments = data.getSegments(new int[]{0,1,2});
            List<DataLoader.DataSegment> segments2 = data.getSegments(new int[]{1,2,3});
            List<DataLoader.DataSegment> segments3 = data.getSegments(new int[]{2,3,4});
            List<DataLoader.DataSegment> segments4 = data.getSegments(new int[]{3,4,5});
            
            segments.addAll(segments2);
            segments.addAll(segments3);
            segments.addAll(segments4);
            for(DataLoader.DataSegment s :segments )
            {
                if(s.countStatus()==3){
                    //System.out.println(s);
                    DataEntry entry = new DataEntry(s.getFeatures(),s.getLabel3());
                    list.add(entry,true);
                }
                
                if(s.countStatus()==2){
                    double w = rn.nextDouble();
                    if(w<0.15){
                        DataEntry entry = new DataEntry(s.getFeatures(),s.getLabel3());
                        list.add(entry,true);
                    }
                }
                
                if(s.countStatus()==1){
                    double w = rn.nextDouble();
                    if(w<0.05){
                        DataEntry entry = new DataEntry(s.getFeatures(),s.getLabel3());
                        list.add(entry,true);
                    }
                }
            }
        }
        return list;
    }
    
    public static void main(String[] args){
        
        CentralTrainer tr = new CentralTrainer();
        DataList list = tr.read("cvt_output.h5_f.h5");
        
        list.show();
        list.scan();
        
        DataList[]  data = DataList.split(list, 0.1,0.1,0.8);
        DataList list2 = new DataList();
        
        //for(int i = 0; i < 16000; i++) list2.add(data[0].getList().get(i));
        data[0].shuffle();
        
        DeepNettsNetwork regression = new DeepNettsNetwork();
        regression.activation(ActivationType.TANH); // or ActivationType.TANH
        regression.outputActivation(ActivationType.LINEAR);
        regression.learningRate(0.001);
        regression.init(new int[]{12,12,12,8,3});
        for(int k = 0; k < 12; k++){
            regression.train(data[0],1000);
            //regression.train(list2,12000);
            System.out.println(" saving intermidiate......");
            regression.save("regression"+ k +".network");
            regression.evaluate(data[1]);
            data[1].export("evaluation"+k+".csv");
        }
    }
}
