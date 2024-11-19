/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.trainers;

import deepnetts.net.FeedForwardNetwork;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.core.InstaRecNetworks;
import j4np.instarec.core.TrackConstructor;
import j4np.instarec.core.TrackConstructor.Combinatorics;
import j4np.instarec.core.TrackFinderNetwork.TrackBuffer;
import j4np.instarec.core.TrackFinderUtils;
import j4np.instarec.core.Tracks;
import j4np.instarec.network.DataExtractor;
import j4np.instarec.utils.DataEntry;
import j4np.instarec.utils.DataList;
import j4np.instarec.utils.DeepNettsIO;
import j4np.instarec.utils.DeepNettsTrainer;
import j4np.instarec.utils.EntryTransformer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.visrec.ml.data.DataSet;

/**
 *
 * @author gavalian
 */

record TrackIndex(int index, int matches, double distance){}

public class Trainer {
    
    public static int inputSize = 6;
    
    TrackConstructor.CombinationCuts cuts = new TrackConstructor.CombinationCuts() {
        double SMALL = 0.000001;
        @Override
        public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
            if(m1>SMALL&&m2>SMALL) if(Math.abs(m1-m2)>15.0) return false;
            if(m3>SMALL&&m4>SMALL) if(Math.abs(m3-m4)>15.0) return false;
            if(m5>SMALL&&m6>SMALL) if(Math.abs(m5-m6)>15.0) return false;
            return true;
        }        
    };
    
    public static void makeTracks(CompositeNode node, Tracks t, int sector){
        Combinatorics c = new Combinatorics();
        
        for(int i = 0; i < node.getRows(); i++){
            if(node.getInt(1, i)==sector){
                c.add(node.getInt(2, i), node.getInt(0, i)
                       , node.getDouble(3, i), node.getDouble(4, i));
            }
        }        
        c.create(t, sector);
    }
    
    public static List<TrackIndex> compare(Tracks list, Tracks t, int which){
       List<TrackIndex> index = new ArrayList<>();
        int[] ids = new int[6]; float[] feat = new float[12];
       t.getClusters(ids, which);
       t.getInput12(feat, which);
       for(int i = 0; i < list.getRows(); i++){
           index.add(new TrackIndex(i,list.contains(i, ids),list.distance(i, feat)));
       }
       return index;
    }
    
    public static DataList read(String file, int tag, int max){
        HipoReader   r = new HipoReader(file,tag);
        DataList  list = new DataList();
        Event e = new Event();
        CompositeNode node = new CompositeNode(1024);
        
        int counter = 0;
        Tracks tr = new Tracks(1024);
        Tracks tc = new Tracks(2048);
        int howMany = 0;
        while(r.next(e)&&counter<max){
            
            e.read(tr.dataNode());
            e.read(node, 32000, 2);
            //System.out.println("event");
            //node.print();
            
            Trainer.makeTracks(node, tc, tr.sector(0));
            float[] f = new float[12];
            
            tr.getInput12(f, 0);

            List<TrackIndex> tracks = Trainer.compare(tc, tr, 0);
            //tr.show();
            //tc.show();
            List<TrackIndex> tracks54 = tracks.stream()
                    .filter(track -> (track.matches()==4||track.matches()==3))
                    .collect(Collectors.toList());
            
            tracks54.sort(Comparator.comparing(TrackIndex::distance));
            
            //for(TrackIndex indx : tracks54){System.out.println(indx);}
            
            if(tr.count(0)==6&&tracks54.size()>0){
                counter++;
                int charge = tr.charge(0);
                
                for(int jj = 0; jj < tracks54.size(); jj++){
                    
                    float[] input = new float[12];
                    float[] input_6 = new float[6];
                    
                    float[] input54 = new float[12];
                    float[] input54_6 = new float[6];
                    
                    tr.getInput12raw(input, 0);
                    tr.getInput6raw(input_6, 0);
                    tc.getInput12raw(input54, tracks54.get(0).index());
                    tc.getInput6raw(input54_6, tracks54.get(0).index());
                    
                    if(inputSize==6){
                        if(charge<0) list.add(new DataEntry(input_6, new float[]{0.0f,1.0f,0.0f}));
                        else list.add(new DataEntry(input_6, new float[]{0.0f,0.0f,1.0f}));
                        list.add(new DataEntry(input54_6, new float[]{1.0f,0.0f,0.0f}));
                    } else {
                        if(charge<0) list.add(new DataEntry(input, new float[]{0.0f,1.0f,0.0f}));
                        else list.add(new DataEntry(input, new float[]{0.0f,0.0f,1.0f}));
                        list.add(new DataEntry(input54, new float[]{1.0f,0.0f,0.0f}));
                    }
                    
                }
                //if(tracks.get(tracks.size()-1).matches()!=6){
                    //System.out.println("--- error in candiadate making.....");
                    //node.print();tr.show();tc.show();
                    //howMany++;
                //}
            }
        }        
        //System.out.println(" missed = " + howMany);
        return list;
    }
    
    public void classifier(String file, int max){
        DataList data = new DataList();
        
        for(int j = 1; j <= 20; j++){
            DataList tmp1 = Trainer.read(file, j, max);
            DataList tmp2 = Trainer.read(file, j+20, max);
            data.getList().addAll(tmp1.getList());
            data.getList().addAll(tmp2.getList());
            data.shuffle();
        }
        data.show();
        
        System.out.println("collisions start");
        //data.collisions();
        System.out.println("collisions   end");
        
        FeedForwardNetwork network = DeepNettsTrainer.createClassifier(new int[]{6,12,12,3});
        EntryTransformer   transformer = new EntryTransformer();
        transformer.input().add(6, 0, 112);
        transformer.output().add(3, 0.0, 1.0);
        DeepNettsTrainer   trainer = new DeepNettsTrainer(network);
        trainer.updateLayers();
        DataSet dataset = trainer.convert(data, transformer);
        System.out.println("====== Training " + data.getList().size());
        trainer.train(dataset, 32);
        
        trainer.save("trackclassifier12.json", transformer);
    }
    public static DataList getData(String file, int max){
        DataList data = new DataList();
        
        for(int j = 1; j <= 20; j++){
            DataList tmp1 = Trainer.read(file, j, max);
            DataList tmp2 = Trainer.read(file, j+20, max);
            data.getList().addAll(tmp1.getList());
            data.getList().addAll(tmp2.getList());
            data.shuffle();
        }
        return data;
    }
    
    public void classifier(String file, String netfile, int max){
        DataList data = new DataList();
        
        for(int j = 1; j <= 20; j++){
            DataList tmp1 = Trainer.read(file, j, max);
            DataList tmp2 = Trainer.read(file, j+20, max);
            data.getList().addAll(tmp1.getList());
            data.getList().addAll(tmp2.getList());
            data.shuffle();
        }
        
        HipoReader r = new HipoReader("missed.h5");
        Tracks tr = new Tracks(5);
        Event e = new Event();
        DataList missed = new DataList();
        while(r.next(e)==true){
            e.read(tr.dataNode(),32000,1);
            if(tr.getRows()>0){
                float[] f = new float[12];
                tr.getInput12raw(f, 0);
                if(tr.charge(0)<0) 
                    missed.add(new DataEntry(f,new float[]{0.0f,1.0f,0.0f})); 
                else missed.add(new DataEntry(f,new float[]{0.0f,0.0f,1.0f}));
            }
        }
        data.show();
        
        missed.show();
        
        data.getList().addAll(missed.getList());
        data.shuffle();
        
        FeedForwardNetwork network = DeepNettsIO.read(netfile);//DeepNettsTrainer.createClassifier(new int[]{12,24,12,6,3});        
        EntryTransformer   transformer = DeepNettsIO.getTransformer(netfile);
        
        //transformer.input().add(12, -8, 120);
        //transformer.output().add(3, 0.0, 1.0);
        DeepNettsTrainer   trainer = new DeepNettsTrainer(network);
        trainer.updateLayers();
        DataSet dataset = trainer.convert(missed, transformer);
        
        trainer.train(dataset, 24);
        
        trainer.save("trackclassifier12derived_3.json", transformer);
    }
    
    
    public void retrain(String file){
        HipoReader r = new HipoReader(file);
        Bank []    b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters","RUN::config");
        Tracks tcv = new Tracks(128);
        
        DataList  trueData = new DataList();
        DataList falseData = new DataList();
        
        TrackBuffer buffer = new TrackBuffer();
        InstaRecNetworks nets= new InstaRecNetworks();
        nets.initJson("etc/networks/clas12gold.network", 0, "default");
        
        float[] features = new float[12]; float[] lables = new float[3];
        int[]   clusters = new int[6];
        while(r.nextEvent(b)==true){
            DataExtractor.getTracks(tcv, b[0], b[1]);
            
            TrackFinderUtils.fillConstructor(buffer.constructor, b[1]);
            
            for(int i = 0; i < tcv.getRows(); i++){
                if(tcv.count(i)==6){
                    int sector = tcv.sector(i);
                    buffer.constructor.sectors[sector-1].create(buffer.tracks, sector, cuts);
                    System.out.println("One Track");
                    tcv.getClusters(clusters, i);
                    for(int j = 0; j < buffer.tracks.getRows(); j++){
                        buffer.tracks.getInput12raw(features, j);
                        nets.getClassifierModel().predict(features, lables);
                        int label = nets.getClassifierModel().getLabel(lables);
                        if(label>0){
                            buffer.tracks.setStatus(j, label);
                            buffer.tracks.dataNode().putFloat(1, j, lables[label]);
                        } else {
                            buffer.tracks.setStatus(j, -1);
                            buffer.tracks.dataNode().putFloat(1, j, 0.0f);
                        }
                    }
                    
                    List<TrackIndex> list = Trainer.compare( buffer.tracks, tcv, i);
                    
                    list.sort(Comparator.comparing(TrackIndex::matches));
                    
                    if(list.get(list.size()-1).matches()==6){
                        tcv.show(i);
                        //for(TrackIndex t : list) System.out.println(t);
                        //buffer.tracks.show();
                        double prob = buffer.tracks.probability(list.get(list.size()-1).index());
                        System.out.println(" probability = " + prob);
                        boolean confusion = false;
                        for(int j = 0; j < list.size(); j++){
                            int index = list.get(j).index();
                            double p = buffer.tracks.probability(index);
                            if(p>prob) {
                                buffer.tracks.show(index);
                                float[] f = new float[12];
                                buffer.tracks.getInput12raw(f, index);
                                falseData.add(new DataEntry(f,new float[]{1.0f,0.0f,0.0f}));
                                confusion = true;
                            }
                        }
                        
                        if(confusion){

                            int idx = list.get(list.size()-1).index();
                            int   c = buffer.tracks.status(idx);
                            float[] input = new float[12];
                            buffer.tracks.getInput12raw(input, idx);
                            if(c==1) trueData.add(new DataEntry(input,new float[]{0.0f,1.0f,0.0f}));
                            else trueData.add(new DataEntry(input,new float[]{0.0f,0.0f,1.0f}));
                        }
                    }

                }
            }
            
                        
        }
        
        String netfile = "trackclassifier12derived.json";
        
        System.out.println(" false size = " + falseData.getList().size() + "  true = " + trueData.getList().size());
        
        DataList data = Trainer.getData("ml_data_1.hipo",  20000);
        trueData.getList().addAll(falseData.getList());
        trueData.shuffle();
        trueData.getList().addAll(data.getList());
        trueData.shuffle();
        
        FeedForwardNetwork network = DeepNettsIO.read(netfile);//DeepNettsTrainer.createClassifier(new int[]{12,24,12,6,3});        
        EntryTransformer   transformer = DeepNettsIO.getTransformer(netfile);
        
        //transformer.input().add(12, -8, 120);
        //transformer.output().add(3, 0.0, 1.0);
        DeepNettsTrainer   trainer = new DeepNettsTrainer(network);
        trainer.updateLayers();
        trueData.show();
        DataSet dataset = trainer.convert(trueData, transformer);
        
        trainer.train(dataset, 12);
        
        trainer.save("trackclassifier12derived_2.json", transformer);
    }
    public static void main(String[] args){
        //DataList list = Trainer.read("ml_data_1.hipo", 1, 100);
        //list.show();
        
        Trainer trainer = new Trainer();
        
        //trainer.classifier("ml_data_1.hipo", "trackclassifier12derived_2.json", 2000);
        trainer.classifier("ml_data_1.hipo", 1000);
        //trainer.retrain("rec_clas_005342.evio.00370.hipo");
        
    }
}
