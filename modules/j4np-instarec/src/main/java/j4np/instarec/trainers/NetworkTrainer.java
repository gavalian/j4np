/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.trainers;

import deepnetts.net.FeedForwardNetwork;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.core.Tracks;
import j4np.instarec.utils.DataEntry;
import j4np.instarec.utils.DataList;
import j4np.instarec.utils.DeepNettsIO;
import j4np.instarec.utils.DeepNettsTrainer;
import j4np.instarec.utils.EntryTransformer;
import j4np.instarec.utils.NeuralModel;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class NetworkTrainer {
    
    public static class NetworkConfig {
        public FeedForwardNetwork     network = null;       
        public EntryTransformer   transformer = null;
        public DeepNettsTrainer       trainer = null;
    }
    
    public static DataList readRegression(String file, int sector, int charge, int tag, int max){        
        HipoReader r = new HipoReader(file,tag);
        DataList dataList = new DataList();
        
        Tracks tr = new Tracks(120);        
        Event  ev = new Event();
        int counter = 0;
        
        while(r.next(ev)==true&&counter<max){            
            ev.read(tr.dataNode());
            if(tr.getRows()>0&&tr.count(0)==6){
                float[] data = new float[6];
                //System.out.println(" count = " + tr.count(0));
                //for(int i = 0; i < data.length; i++) data[i] = (float) (tr.dataNode().getDouble(17+i, 0)/112.);
                tr.getInput6raw(data, 0);
                int tcharge = tr.charge(0);
                int tsector = tr.sector(0);
                
//if(regression==true){                
                float[] output = new float[3];                
                //for(int i = 0; i < data.length; i++) datar[i] = data[i];
                Vector3 v = new Vector3();
                Vector3 z = new Vector3();
                tr.vector(v, 0);
                tr.vertex(z, 0);
                double chi2 = tr.chi2(0);
                float[] reg = tr.getVectorOutput(v, 0);
                for(int i = 0; i < 3; i++) output[i] = reg[i];   
                
                if(sector==tsector&&tcharge==charge&&chi2<500&&z.z()>-15&&z.z()<5){
                    if(output[0]>0.0&&output[0]<1.0&&
                         output[1]>0.0&&output[1]<1.0&&
                            output[2]>0.0&&output[2]<1.0){
                    counter++;dataList.add(new DataEntry(data,output));
                    }
                }
            }
        }
        return dataList;
    }
    
    public static DataList readRegression(String file, int sector, int charge, int max){
        DataList data = new DataList();
        int offset = 1; if(charge>0) offset = 21;
        for(int i = 0; i < 20;i++){
            DataList list = NetworkTrainer.readRegression(file, sector, charge, i+offset, max);
            //System.out.printf(" tag = %d, size = %d\n",i+offset,list.getList().size());
            data.getList().addAll(list.getList());
        }
        return data;
    }
    
    public static NetworkConfig create(int[] layers){
        NetworkConfig config = new NetworkConfig();
        config.network = DeepNettsTrainer.createRegression(layers);
        config.transformer = new EntryTransformer();
        config.transformer.input().add(6, 1, 112);
        config.transformer.output().add(3, 0.0,1.0);
        config.trainer = new DeepNettsTrainer(config.network);
        config.trainer.updateLayers();
        return config;
    }
    
    public static NetworkConfig load(String file){
        NetworkConfig config = new NetworkConfig();
        config.network = DeepNettsIO.read(file);
        config.transformer = DeepNettsIO.getTransformer(file);
        config.trainer = new DeepNettsTrainer(config.network);
        config.trainer.updateLayers();
        return config;
    }
    
    public static void trainRegression(String file, String network, int sector, int charge){
        
        DataList data = NetworkTrainer.readRegression(file, sector, charge, 15000);
        
        NetworkConfig config = NetworkTrainer.create(new int[]{6,12,12,6,3});
        
        if(network!=null) config = NetworkTrainer.load(network);
        data.show();
        data.shuffle();
        data.shuffle();
        data.scan();
        DataSet traindata = config.trainer.convert(data, config.transformer);
        System.out.printf("training for %d %d\n",sector,charge);
        config.trainer.train(traindata, 25);
        
        String name = String.format("trackregression6_%d_%s.json", sector,charge<0?"n":"p");
        config.trainer.save(name, config.transformer);
    }
    
    public static void validateRegression(String file, String network, int sector, int charge){
        //NeuralModel model = NeuralModel.archiveFile(network, 
         //       String.format("%d/%s/trackregression6.network", sector,charge<0?"n":"p"), 
          //      2, "default");
        
        NeuralModel model = NeuralModel.jsonFile(network); 
                
        
        System.out.println(model.summary());
        
        DataList data = NetworkTrainer.readRegression(file, sector, charge, 1200);
        H2F h2 = new H2F("h2",120,0.,1,120,-1.0,1.0);
        for(int i = 0; i < data.getList().size(); i++){
            float[] output = new float[3];
            model.predict(data.getList().get(i).features(), output);
            data.getList().get(i).setInfered(output);
            data.getList().get(i).show();
            h2.fill(data.getList().get(i).labels()[0], data.getList().get(i).labels()[0]-output[0]);
        }
        TGCanvas c = new TGCanvas();
        c.draw(h2);
    }
    
    public static void main(String[] args){        
        for(int s = 1; s <= 6; s++) NetworkTrainer.trainRegression("ml_data_1.hipo","regressionnetwork.json",s,-1);
        for(int s = 1; s <= 6; s++) NetworkTrainer.trainRegression("ml_data_1.hipo","regressionnetwork.json",s, 1);
        //NetworkTrainer.trainRegression("ml_data_1.hipo",null,2,-1);
        //NetworkTrainer.validateRegression("ml_data_2.hipo","trackregression6_2_n.json",1,-1);
    }
}
