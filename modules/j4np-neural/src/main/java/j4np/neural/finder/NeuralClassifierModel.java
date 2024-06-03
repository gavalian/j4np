/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.finder;

import j4ml.data.DataEntry;
import j4ml.ejml.EJMLModel;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.Tracks;
import j4np.neural.networks.NeuralClusterFinder;
import j4np.neural.networks.NeuralDataList;
import j4np.neural.data.TrackConstructor;
import j4np.neural.data.TrackConstructor.CombinationCuts;
import j4np.utils.base.ArchiveProvider;
import j4np.utils.base.ArchiveUtils;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class NeuralClassifierModel {
    
    EJMLModel model = null;//new EJMLModel();
    EJMLModel modelFixer = null;//new EJMLModel();
    private Schema  clusters = null;
    private Schema    tracks = null;
    
    public NeuralClassifierModel(){
        
    }
    
    public void init(HipoReader r){
        this.init(r.getSchemaFactory());
    }
    
    public void init(SchemaFactory sf){
        clusters = sf.getSchema("nnet::clusters").copy();
        tracks = sf.getSchema("nnet::tracks").copy();
    }
    /**
     * read neural network file from file, which is in Text file format.
     * @param networkFile
     * @param run 
     */
    public void loadFromFile(String networkFile, int run){
        
        ArchiveProvider ap = new ArchiveProvider(networkFile);
        int runNumber = ap.findEntry(run);
        System.out.printf(":::: classifier archive provider found run # %d for requested (run=%d)\n",runNumber, run);
        
        String archiveFile = String.format("network/%d/%s/trackClassifier.network",runNumber,"default");
        System.out.printf(":::: looking for file : %s\n",archiveFile);
        
        List<String> networkContent = ArchiveUtils.getFileAsList(networkFile,archiveFile);
        model = EJMLModel.create(networkContent);
        model.setType(EJMLModel.ModelType.SOFTMAX);
        System.out.println(model.summary());
        
        modelFixer = EJMLLoader.load(networkFile, "trackFixer.network", run, "default");
        System.out.println(modelFixer.summary());
    }
    
    public EJMLModel getModel(){ return model;}
    
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
        
    protected void getSector(TrackConstructor constructor, int sector, Tracks list){
        list.dataNode().setRows(0);        
        CombinationCuts cuts = new CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>25.0) return false;
                if(Math.abs(m3-m4)>25.0) return false;
                if(Math.abs(m5-m6)>25.0) return false;
                return true;
            }        
        };
        
        constructor.sectors[sector-1].create(list, sector,cuts);
        
        /*
        Tracks nList = new Tracks(4096);
        constructor.sectors[sector-1].create(nList, sector,cuts);
        System.out.printf(" track candidates without a cut = %8d, with cuts %8d\n",
                list.size(),nList.size());
        */
    }
    
    protected void getSector5(TrackConstructor constructor, int sector, Tracks list){
        list.dataNode().setRows(0);        
        CombinationCuts cuts = new CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>25.0) return false;
                if(Math.abs(m3-m4)>25.0) return false;
                if(Math.abs(m5-m6)>25.0) return false;
                return true;
            }        
        };
        
        constructor.sectors[sector-1].create5(list, sector,cuts);
        
        /*
        Tracks nList = new Tracks(4096);
        constructor.sectors[sector-1].create(nList, sector,cuts);
        System.out.printf(" track candidates without a cut = %8d, with cuts %8d\n",
                list.size(),nList.size());
        */
    }
    
    protected void fillConstructor(TrackConstructor tc, CompositeNode cnode){
        int nrows = cnode.getRows();
        for(int r = 0; r < nrows; r++){
            tc.add(cnode.getInt(1, r),
                    cnode.getInt(2,r),
                    cnode.getInt(0, r),
                    cnode.getDouble(3, r));
        }
    }
    
    
    protected void fillConstructor(TrackConstructor tc, CompositeNode cnode, int[] matches){
        int nrows = cnode.getRows();
        for(int r = 0; r < nrows; r++){
            
            int cid = cnode.getInt(0, r);
            if(this.contains(matches, cid)==false){
                tc.add(cnode.getInt(1, r),
                        cnode.getInt(2,r),
                        cnode.getInt(0, r),
                        cnode.getDouble(3, r));
            } else {
                //System.out.printf("not filling the CID %d\n", cid);
            }
        }
    }
    
    public void process(Event e){
        
        //Bank b = new Bank(clusters, 800);
        CompositeNode cnode = new CompositeNode(32100,1,"3b2f",256);
        
        //e.read(b);
        e.read(cnode, 32100, 1);
        
        Tracks list    = new Tracks(100000);
        Tracks listAll = new Tracks(100);
        Tracks listResolved = new Tracks(100);
        
        TrackConstructor constructor = new TrackConstructor();
        
        /*int nrows = b.getRows();
        for(int r = 0; r < nrows; r++){
            int    s = b.getInt("sector", r);
            int   sl = b.getInt("superlayer", r);
            int   id = b.getInt("id", r);
            float cm = b.getFloat("mean", r);
            
            constructor.add(s, sl, id, cm);
        }*/
        
        this.fillConstructor(constructor, cnode);
        
        for(int s = 1; s <=6; s++){
            this.getSector(constructor, s, list);
            //System.out.printf(">>>>> sector = %4d, candidates = %5d\n",s,list.dataNode().getRows());
            this.evaluate(list);
            if(list.dataNode().getRows()>0) {
                //System.out.println("----------------------- ROWS = " + list.getRows());
                //list.dataNode().print();
                int rows = listAll.dataNode().getRows();
                for(int k = 0; k < list.getRows(); k++){
                    if(list.dataNode().getInt(0, k)>0){
                        listAll.dataNode().setRows(rows+1);
                        listAll.dataNode().copyRow(list.dataNode(), k, rows);
                        rows++;
                    }
                }
            }
            //this.evaluate(list);
        }
        
        //System.out.println("-----------------------");
        //System.out.println("before resolve");
        //listAll.dataNode().print();
        resolve(listAll);
        //System.out.println("after  resolve");
        //listAll.dataNode().print();
        listResolved.dataNode().setRows(0);
        for(int k = 0; k < listAll.dataNode().getRows();k++){
            if(listAll.status(k)>10){
                int rows = listResolved.dataNode().getRows();
                listResolved.dataNode().setRows(rows+1);
                listResolved.dataNode().copyRow(listAll.dataNode(), k, rows);                
            }
        }
        
        //listResolved.dataNode().print();
        
        int ntracks = listResolved.getRows();
        
        int[] resolvedSegments = new int[ntracks*6];
        int   counter = 0;
        for(int i = 0; i < ntracks; i++){            
            for(int k = 0; k < 6; k++) { resolvedSegments[counter] = listResolved.dataNode().getInt(11+k, i);counter++;}
        }
        
        //System.out.println(Arrays.toString(resolvedSegments));
        
        constructor.reset();
        this.fillConstructor(constructor, cnode,resolvedSegments);
        float[] input = new float[6];
        float[] output = new float[6];
        float[] result = new float[3];
        int[]   segments = new int[6];
        
        listAll.dataNode().setRows(0);
        for(int s = 1; s <=6; s++){
            this.getSector5(constructor, s, list);
            
            
            int nrows = list.getRows();
            for(int k = 0; k < nrows; k++){
                list.getInput(input, k);
                list.getClusters(segments, k);                
                int order = this.which(segments);
                modelFixer.feedForward(input, output);                
                //input[order] = output[order];
                list.dataNode().putFloat(17+order, k, output[order]*112);                
                
            }
            
            this.evaluate(list);
            
            if(list.dataNode().getRows()>0) {
                //System.out.println("----------------------- ROWS = " + list.getRows());
                //list.dataNode().print();
                int rows = listAll.dataNode().getRows();
                for(int k = 0; k < list.getRows(); k++){
                    if(list.dataNode().getInt(0, k)>0){
                        listAll.dataNode().setRows(rows+1);
                        listAll.dataNode().copyRow(list.dataNode(), k, rows);
                        rows++;
                    }
                }
            }
        
        }
        
        this.resolve(listAll);
        //System.out.println("listResolved size = " + listResolved.getRows());
        
        
        
        for(int k = 0; k < listAll.dataNode().getRows();k++){
            if(listAll.status(k)>10){
                int rows = listResolved.dataNode().getRows();
                listResolved.dataNode().setRows(rows+1);
                listResolved.dataNode().copyRow(listAll.dataNode(), k, rows);                
            }
        }
        
        
        
        if(listResolved.getRows()>0){
            CompositeNode tnode = this.createNode(listResolved);
            //tnode.print();
            e.write(tnode);
        }
        
    }
    
    public int which(int[] segments){
        for(int i = 0; i < segments.length; i++) { if(segments[i]<0) return i;}
        return -1;
    }
    protected CompositeNode createNode(Tracks trk){
        
        //Bank nntrk = new Bank(this.tracks,trk.getRows());
        CompositeNode tnode = new CompositeNode(32100,2,"3sf6s6f",trk.getRows()+5);
        tnode.setRows(trk.getRows());
        for(int i = 0; i < trk.getRows(); i++){
            int status = trk.status(i);
            
            tnode.putShort(0,i,(short) (i+1));
            tnode.putShort(1,i,(short) trk.sector(i));
            
            if(status==11) tnode.putShort(2,i,(short) -1); 
            else tnode.putShort(2,i,(short) 1);
            
            tnode.putFloat(3, i, (float) trk.probability(i));
            
            for(int j = 0; j < 6; j++){
                tnode.putShort(  4+j, i, (short) trk.dataNode().getInt(11+j,i));
                tnode.putFloat( 10+j, i, (float) trk.dataNode().getDouble(17+j,i));
            }
        }
        return tnode;
    }
    
    protected Bank createBank(Tracks trk){
        Bank nntrk = new Bank(this.tracks,trk.getRows());
        for(int i = 0; i < trk.getRows(); i++){
            int status = trk.status(i);
            
            nntrk.putShort(0,i,(short) (i+1));            
            nntrk.putShort(1,i,(short) trk.sector(i));
            
            if(status==11) nntrk.putShort(2,i,(short) -1); 
            else nntrk.putShort(2,i,(short) 1);
            
            nntrk.putFloat(3, i, (float) trk.probability(i));
            
            for(int j = 0; j < 6; j++){
                nntrk.putShort(  4+j, i, (short) trk.dataNode().getInt(11+j,i));
                nntrk.putFloat( 10+j, i, (float) trk.dataNode().getDouble(17+j,i));
            }
        }
        return nntrk;
    }
    
    protected void resolve(Tracks list){
        int index  = list.getHighestIndex(0,list.dataNode().getRows()-1, new int[]{1,2});
        if(index<0) return;
        int status = list.status(index);
        list.setStatus(index, status+10);
        //System.out.printf("resolve : found status %5d, at index %5d\n",status,index);
        while(status>0){
            //System.out.printf("resolve : found status %5d, at index %5d\n",status,index);
            list.setStatus(index, status+10);
            for(int i = 0; i < list.size(); i++){
                if(i!=index){
                    if(list.match(index, i)==true) list.setStatus(i, -1);
                }
            }
            index  = list.getHighestIndex(0,list.dataNode().getRows()-1, new int[]{1,2});
            if(index<0) return;
            status = list.status(index); 
            //System.out.printf(">>>> internal resolve : found status %5d, at index %5d\n",status,index);
            if(status==11||status==12) status = -1;
        }
        
    }
    
    
    public boolean contains(int[] segments, int id){
        for(int i = 0; i < segments.length; i++){
            if(segments[i]==id) return true;
        }
        return false;
    }
    
    public static void test(){
        String network = "etc/networks/clas12default.network";
        HipoReader r = new HipoReader("etc/validation/neural_validation.h5");
        Event event = new Event();

        NeuralClassifierModel nc = new NeuralClassifierModel();
        nc.loadFromFile(network, 10);
        
        int counter = 0;
        while(r.hasNext()){
            r.next(event);
            counter++;
            //System.out.printf("****** EVENT # %d\n",counter);
            nc.process(event);
            
            CompositeNode node = new CompositeNode(32100,2,"sssff",128);
            
            event.read(node,32100,2);

            //node.print();
        }
    }
    
    public static void main(String[] args){
        
        
        NeuralClassifierModel.test();
        /*
        String network = "etc/clas12rga.network";
        String    file = "rec_clas_005442_converted.h5";
        
        NeuralClassifierModel nc = new NeuralClassifierModel();
        nc.loadFromFile(network, 10);
        
        HipoReader r = new HipoReader(file);
        
        //nc.init(r);
        Event e = new Event();
        
        while(r.hasNext()==true){
            r.next(e);
            nc.process(e);
        }*/
        
    }
}
