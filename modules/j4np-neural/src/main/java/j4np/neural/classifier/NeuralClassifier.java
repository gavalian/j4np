/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.classifier;

import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.Tracks;
import j4np.neural.networks.NeuralClusterFinder;
import j4np.neural.networks.NeuralDataList;
import j4np.neural.data.TrackConstructor;
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
        System.out.println(model.summary());
    }
    
    public int getHighestIndex(float[] output){
        int index = 0; float max = output[0];
        for(int i = 0; i < output.length; i++)
            if(output[i]>max){ max = output[i]; index = i;}
        return index;
    }
    public void evaluate(Tracks trk){
        float[]  input = new float[6];
        float[] output = new float[3];
        
        int nrows = trk.getRows();
        for(int row = 0; row < nrows; row++){
            trk.getInput(input, row);
            model.feedForwardSoftmax(input, output);
            int index = this.getHighestIndex(output);
            if(index==0){ 
                trk.dataNode().putShort(0,row,(short) -1);
                trk.dataNode().putFloat(1,row, 0.0f);
            } else {
                trk.dataNode().putShort(0,row,(short) index);
                trk.dataNode().putFloat(1,row, output[index]);
            }
        }
    }
    
    public void getCandidatesFromBank(Bank b, int sector, Tracks list){
        TrackConstructor constructor = new TrackConstructor();
        constructor.reset();
        list.dataNode().setRows(0);
        int nrows = b.getRows();
        for(int r = 0; r < nrows; r++){
            int   s = b.getInt("sector", r);
            int   sl = b.getInt("superlayer", r);
            int   id = b.getInt("id", r);
            float cm = b.getFloat("mean", r);
            constructor.add(s, sl, id, cm);
        }
        constructor.sectors[sector-1].create(list, sector, null);
        /*constructor.sectors[sector-1].create(list, sector, new TrackConstructor.CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>(25.0)) return false;
                if(Math.abs(m3-m4)>(25.0)) return false;
                if(Math.abs(m5-m6)>(25.0)) return false;
                return true;
            }
        });*/ 
    }
    
    protected void getSector(TrackConstructor constructor, int sector, Tracks list){
        list.dataNode().setRows(0);
        constructor.sectors[sector-1].create(list, sector,null);
        /*constructor.sectors[sector-1].create(list, sector, new TrackConstructor.CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>(25.0/112.)) return false;
                if(Math.abs(m3-m4)>(25.0/112.)) return false;
                if(Math.abs(m5-m6)>(25.0/112.)) return false;
                return true;
            } 
        });*/ 
    }
    
    public void process(Event e){
        
        Bank b = new Bank(clusters, 800);
        e.read(b);
        Tracks list = new Tracks(100000);
        TrackConstructor constructor = new TrackConstructor();
        
        int nrows = b.getRows();
        for(int r = 0; r < nrows; r++){
            int    s = b.getInt("sector", r);
            int   sl = b.getInt("superlayer", r);
            int   id = b.getInt("id", r);
            float cm = b.getFloat("mean", r);
            
            constructor.add(s, sl, id, cm);
        }        
        //System.out.println("========== PROCESSING EVENT ");
        //constructor.show();
        
        for(int s = 1; s <=6; s++){
            this.getSector(constructor, s, list);
            //System.out.printf(">>>>> sector = %4d, candidates = %5d\n",s,list.dataNode().getRows());
            this.evaluate(list);
            if(list.dataNode().getRows()>0) {
                //System.out.println("----------------------- ROWS = " + list.getRows());
                //list.dataNode().print();
            }
            //this.evaluate(list);
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
    
    public static void main(String[] args){
        String network = "etc/networks/clas12rga.network";
        String    file = "output.h5";
        
        NeuralClassifier nc = new NeuralClassifier();
        nc.loadFromFile(network, 10);
        
        HipoReader r = new HipoReader(file);
        
        nc.init(r);
        Event e = new Event();
        
        while(r.hasNext()==true){
            r.next(e);
            nc.process(e);
        }
        
        /*
        
        SchemaFactory factory = new SchemaFactory();
        factory.readFile("etc/neuralnetwork.json");
        String file = "rec_clas_006152.00055-00059.h5";
        HipoReader r = new HipoReader(file);
        for(Schema schema : factory.getSchemaList())
            r.getSchemaFactory().addSchema(schema);
        
        NeuralClusterFinder cf = new NeuralClusterFinder();
        NeuralClassifier    nc = new NeuralClassifier();
        nc.init(r);
        
        Event e = new Event();
        for(int i = 0; i < 5000; i++){
            r.nextEvent(e);
            cf.processEvent(e);
            nc.process(e);
        }*/
    }
}
