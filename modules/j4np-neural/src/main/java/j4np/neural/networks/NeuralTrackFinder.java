/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.networks;

import j4np.neural.data.TrackConstructor;
import j4np.neural.regression.NeuralRegressionModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.neural.classifier.NeuralClassifier;
import j4np.neural.data.Tracks;
import j4np.neural.data.TrackReader;
import j4np.neural.data.TrackConstructor.CombinationCuts;

/**
 *
 * @author gavalian
 */
public class NeuralTrackFinder {
    
    public String neuralNetworkFile = "";
    public int    runNumber  = 0;
    
    public int    counterUpper = 0;
    public int    counterLower = 0;
    public int    counterClean = 0;
    
    protected NeuralClassifier  classifier = new NeuralClassifier();
    protected TrackConstructor constructor = new TrackConstructor();
    
    public NeuralTrackFinder(String file, int run){
        neuralNetworkFile = file; runNumber = run;
    }
    
    public void initializeNetworks(){
        classifier.loadFromFile(neuralNetworkFile, runNumber);
    }
    
    public NeuralClassifier getClassifier(){ return classifier;}
    
    public void getCandidatesFromBank(Bank b, Tracks list){
        constructor.reset();
        list.dataNode().setRows(0);
        int nrows = b.getRows();
        for(int r = 0; r < nrows; r++){
            int   sl = b.getInt("superlayer", r);
            int   id = b.getInt("id", r);
            float cm = b.getFloat("mean", r);
            constructor.add(1, sl, id, cm);
        }
        constructor.sectors[0].create(list, 1, new CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>(25.0/112.)) return false;
                if(Math.abs(m3-m4)>(25.0/112.)) return false;
                if(Math.abs(m5-m6)>(25.0/112.)) return false;
                return true;
            }        
        }); 
    }
    
    public void processBank(Bank b){
        
        constructor.reset();
        
        int nrows = b.getRows();
        for(int r = 0; r < nrows; r++){
            int   sl = b.getInt("superlayer", r);
            int   id = b.getInt("id", r);
            float cm = b.getFloat("mean", r);
            constructor.add(1, sl, id, cm);
        }
        
        Tracks list = new Tracks();
        
        constructor.sectors[0].create(list, 1, new CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>(25.0/112.)) return false;
                if(Math.abs(m3-m4)>(25.0/112.)) return false;
                if(Math.abs(m5-m6)>(25.0/112.)) return false;
                
                /*double m1d = (m1+m2)/2.0;
                double m2d = (m3+m4)/2.0;
                double m3d = (m5+m6)/2.0;
                if(Math.abs(m1d-m2d)>25.) return false;
                if(Math.abs(m2d-m3d)>65.) return false;
                if(Math.abs(m3d-m1d)>65.) return false;*/
                return true;
            }
        
        });        
        
        classifier.evaluate(list);
        
        int nC = list.dataNode().getRows();
        
        //System.out.println(">>>>>>> ROWS = " + nC);
        double prob1 = list.dataNode().getDouble(1, 0);
        double prob2 = list.dataNode().getDouble(1, nC-1);
        boolean isClean = true;
        for(int i = 1 ; i < nC-1; i++){
            double p = list.dataNode().getDouble(1, i);
            if(p>=prob1){ counterUpper++; isClean = false;}
            if(p>=prob2){ counterLower++; isClean = false;}
        }
        if(isClean) counterClean++;
        //list.show();
    }
    
    public void showStats(){
        System.out.printf("Statistics\n");
        System.out.printf("counter upper : %d\n", counterUpper);
        System.out.printf("counter lower : %d\n", counterLower);
        System.out.printf("counter clean : %d\n", counterClean);
    }
    
    public void apply(){
        
    }
    
    public void adjust(){
        
    }
    
    
    public static void processRegression(String filename){
        
    }
    public static void main(String[] args){
        
        NeuralRegressionModel reg = new NeuralRegressionModel();
        reg.loadFromFile("clas12default.network",1);
        SchemaFactory sf = new SchemaFactory();
        sf.readFile("etc/neuralnetwork.json");
        sf.show();
        
        String file = "/Users/gavalian/Work/Software/project-10.7/study/regression/filter_output_n.h5";
        HipoReader r = new HipoReader(file);
        for(Schema s : sf.getSchemaList())
            r.getSchemaFactory().addSchema(s);
        
        r.getSchemaFactory().show();
        
        HipoWriter w =  HipoWriter.create("nnet_output.h5", r);
        reg.init(r);
        
        Bank[] banks = r.getBanks("TimeBasedTrkg::TBTracks","TimeBasedTrkg::TBClusters");
        Tracks  list = new Tracks();
        Event e = new Event();
        while(r.hasNext()==true){
            r.next(e);
            e.read(banks);
            TrackReader.reco2tracks(list, banks[0], banks[1]);
            
            if(list.dataNode().getRows()==2){
            
                Bank b  = new Bank (sf.getSchema("nnet::tracks"),2);
                Bank bp = new Bank (sf.getSchema("nnet::particle"),4);
                for(int i = 0; i < 2; i++){
                    b.putInt("sector", i, list.sector(i));
                    b.putInt("charge", i, list.charge(i));
                    for(int k = 0; k < 6; k++) b.putFloat(k+10, i, (float) (list.dataNode().getDouble(17+k, i)/112.0));
                }
                //list.show();
                //b.show();
                e.write(b);
                reg.processEvent(e);
                e.read(bp);
                //bp.show();
                w.addEvent(e);
            }
        }
        
        w.close();
        /*
        NeuralTrackFinder finder = new NeuralTrackFinder("clas12rga.network",5442);
        finder.initializeNetworks();        
        TrackDataReader dr = new TrackDataReader();
        for(int i = 0; i < 1500; i++){
            Bank b = dr.readFromFile("run_5197_in_tr.h5",3,27);
            //b.show();
            finder.processBank(b);
        }
        
        finder.showStats();*/
    }
}
