/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.networks;

import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.Tracks;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralClassifier {
    
    EJMLModel model = null;//new EJMLModel();
    private Schema  clusters = null;
    private Schema    tracks = null;
    
    public NeuralClassifier(){
        
    }
    
    public void init(HipoReader r){
        this.init(r.getSchemaFactory());
    }
    
    public void init(SchemaFactory sf){
        clusters = sf.getSchema("nnet::clusters").copy();
          tracks = sf.getSchema("nnet::tracks").copy();
    }
    
    public void loadFromFile(String networkFile, int run){
        
        ArchiveProvider ap = new ArchiveProvider(networkFile);
        int runNumber = ap.findEntry(run);
        
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",runNumber,"default");        
        List<String> networkContent = ArchiveUtils.getFileAsList(networkFile,archiveFile);
        model = EJMLModel.create(networkContent);
        model.setType(EJMLModel.ModelType.SOFTMAX); 
    }
    
    public void getCandidatesFromBank(Bank b, int sector, Tracks list){
        TrackConstructor constructor = new TrackConstructor();
        constructor.reset();
        list.dataNode().setRows(0);
        int nrows = b.getRows();
        for(int r = 0; r < nrows; r++){
            int   sl = b.getInt("superlayer", r);
            int   id = b.getInt("id", r);
            float cm = b.getFloat("mean", r);
            constructor.add(1, sl, id, cm);
        }
        
        constructor.sectors[sector-1].create(list, 1, new TrackConstructor.CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>(25.0/112.)) return false;
                if(Math.abs(m3-m4)>(25.0/112.)) return false;
                if(Math.abs(m5-m6)>(25.0/112.)) return false;
                return true;
            }        
        }); 
    }
    
    protected void getSector(TrackConstructor constructor, int sector, Tracks list){
        list.dataNode().setRows(0);
        constructor.sectors[sector-1].create(list, 1, new TrackConstructor.CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>(25.0/112.)) return false;
                if(Math.abs(m3-m4)>(25.0/112.)) return false;
                if(Math.abs(m5-m6)>(25.0/112.)) return false;
                return true;
            } 
        }); 
    }
    
    public void process(Event e){
        Bank b = new Bank(clusters, 800);
        e.read(b);
        Tracks list = new Tracks(100000);
        TrackConstructor constructor = new TrackConstructor();
        
        int nrows = b.getRows();
        for(int r = 0; r < nrows; r++){
            int   sl = b.getInt("superlayer", r);
            int   id = b.getInt("id", r);
            float cm = b.getFloat("mean", r);
            constructor.add(1, sl, id, cm);
        }
        
        for(int s = 1; s <=6; s++){
            this.getSector(constructor, s, list);
            System.out.printf(">>>>> sector = %4d, candidates = %5d\n",s,list.dataNode().getRows());
            this.evaluate(list);
        }
    }
    
    protected void resolve(Tracks list){
        int index = list.getHighestIndex(0,list.dataNode().getRows()-1);
        
    }
    
    public float[] evaluate(float[] input){
        float[] output = new float[3];
        model.feedForward(input, output);
        return output;
    }
    
    
    public void evaluate(NeuralDataList list){
        float[]  input = new float[6];
        float[] output = new float[3];
        int nrows = list.size();
        //System.out.println(" evaluating rows = " + nrows);
        for(int row = 0; row < nrows; row++){
            list.getInput(input, row);
            model.feedForward(input, output);
            list.applyOutput(output, row);
        }
    }
}
