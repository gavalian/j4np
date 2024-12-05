/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.data.base.DataFrame;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.instarec.core.TrackConstructor.CombinationCuts;
import j4np.instarec.network.DataExtractor;
import j4np.instarec.utils.EJMLModel;
import j4np.physics.Vector3;
import j4np.utils.asciitable.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class TrackFinderNetwork {
    
    
    
    public enum NetworkMode {        
        FIXER_12, FIXER_6, CLASSIFIER_12, CLASSIFIER_6  
    };
    
    
    NetworkMode modeFixer = NetworkMode.FIXER_6;
    NetworkMode modeClassifier = NetworkMode.CLASSIFIER_6;
    
    EJMLModel modelClassifier = null;
    EJMLModel      modelFixer = null;
    String[]      schemaNames = new String[]{"HitBasedTrkg::Clusters","TimeBasedTrkg::TBTracks"};
    List<Schema>      schemas = new ArrayList<>();
    SchemaFactory    cfactory = new SchemaFactory();
    InstaRecNetworks instarec = null;
    
    Bank btb = null;
    
    private boolean useCuts = true;

    CombinationCuts trackCuts = new CombinationCuts() {
        double SMALL = 0.000001;
        @Override
        public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
            if(m1>SMALL&&m2>SMALL) if(Math.abs(m1-m2)>15.0) return false;
            if(m3>SMALL&&m4>SMALL) if(Math.abs(m3-m4)>15.0) return false;
            if(m5>SMALL&&m6>SMALL) if(Math.abs(m5-m6)>15.0) return false;
            return true;
        }        
    };

    CombinationCuts    cuts = trackCuts;
    
    long[]  candidateStats = new long[6];
    long[] candidateStats5 = new long[6];
    long[]   resolvedStats = new long[6];
    long[]  resolvedStats5 = new long[6];
    long   eventsProcessed = 0L;
    
    public TrackFinderNetwork(){
        
    }
    
    
    public void setUseCuts(boolean flag){
        if(flag==false){
            cuts = null;
        } else {
            cuts = trackCuts;
        }
    }
    public void init(SchemaFactory factory){
       
        schemas.add(factory.getSchema(schemaNames[0]));
        System.out.println(schemas.get(0));
        
        if(factory.hasSchema("instarec::tracks")==true){
            schemas.add(factory.getSchema("instarec::tracks"));
        } else {
            cfactory.initFromDirectory("etc/bankdefs");
            if(cfactory.hasSchema("instarec::tracks")){
                schemas.add(cfactory.getSchema("instarec::tracks"));
                factory.addSchema(cfactory.getSchema("instarec::tracks"));
                
                cfactory.show();
            }
        }
        
        /*Schema.SchemaBuilder schemaBuilder = new Schema.SchemaBuilder("mltr::segments",32100,1);        
        //schema.parse("5I4F3BLL");
        schemaBuilder.addEntry(           "id", "S", "particle id");
        schemaBuilder.addEntry(       "sector", "B", "x-component of momentum");
        schemaBuilder.addEntry(   "superlayer", "B", "y-component of momentum");
        schemaBuilder.addEntry(         "mean", "F", "z-component of momentum");
        schemaBuilder.addEntry(        "slope", "F", "particle charge");
        schemaBuilder.addEntry(       "status", "B", "particle charge");
        schemas.add(schemaBuilder.build());
      
        btb = factory.getBank("TimeBasedTrkg::TBTracks", 15);*/
    }
    /**
     * Initialize the networks from the archive file
     * @param archive
     * @param run 
     */
    public void init(String archive,int run){
        
        instarec = new InstaRecNetworks(archive,run);
        instarec.initJson(archive, run, "default");
        
        instarec.show();
        
        
        /*try {
            modelClassifier = EJMLLoader.load(archive, "trackclassifier12.network", run, "default");
        } catch (Exception e){
            modelClassifier = null;
        } finally {
            System.out.println(modelClassifier.summary());
        }
        
        try {
            modelFixer = EJMLLoader.load(archive, "trackfixer12.network", run, "default");
        } catch (Exception e){
            modelFixer = null;
        } finally {
            System.out.println(modelFixer.summary());
        }*/
    }
    
    public void evaluate(Tracks trk, int row){
        float[]  input = new float[12];
        float[] output = new float[3];
        trk.getInput12(input, row);
        instarec.getClassifier().feedForwardSoftmax(input, output);
        int index = EJMLModel.getLabel(output);
        if(index==0){ 
            trk.dataNode().putShort(0,row,(short) -1);
            trk.dataNode().putFloat(1,row, 0.0f);
        } else {
            trk.dataNode().putShort(0,row,(short) index);
            trk.dataNode().putFloat(1,row, output[index]);
        }
    }
    
    public void evaluateOLD(Tracks trk, int row){
        float[]  input = new float[12];
        float[] output = new float[3];
        trk.getInput12(input, row);
        instarec.getClassifier().feedForwardSoftmax(input, output);
        int index = EJMLModel.getLabel(output);
        if(index==0){ 
            trk.dataNode().putShort(0,row,(short) -1);
            trk.dataNode().putFloat(1,row, 0.0f);
        } else {
            trk.dataNode().putShort(0,row,(short) index);
            trk.dataNode().putFloat(1,row, output[index]);
        }
    }
    
    public void validate(Tracks trk){
        float[]    input = new float[12];
        float[]  corrupt = new float[12];
        float[]   output = new float[3];
        
        int nrows = trk.getRows();
        for(int row = 0; row < nrows; row++){
            if(trk.status(row)>0){
                for(int k = 0; k < 6; k++){
                    trk.getInput12(corrupt, row);
                    corrupt[k*2] = 0.0f; corrupt[k*2+1] = 0.0f;
                    this.instarec.getFixer().feedForwardReLULinear(corrupt, input);
                    this.instarec.getClassifier().feedForwardSoftmax(input, output);
                    if(Float.isNaN(output[0])==true){
                        trk.setStatus(row, -1);
                    } else if(output[0]>0.5) trk.setStatus(row, -1);
                }
            }            
        }
    }
    
    public void printSummary(){
        String[]  header = new String[]{"sector","candidate (6 SL) /event", 
            "tracks (6 SL) /event",
            "candidate (5 SL) /event", "tracks (5 SL) /event"
        };
        String[][]  data = new String[6][5];
        
        System.out.println("+----------------------------------------+");
        
        System.out.println("|    Events Processed : " + this.eventsProcessed);        
        for(int i = 0; i < 6; i++){
            data[i][0] = "sector  " + (i+1);
            data[i][1] = String.format("%.5f", ((double) this.candidateStats [i])/this.eventsProcessed);
            data[i][2] = String.format("%.5f", ((double) this.resolvedStats [i])/this.eventsProcessed);
            data[i][3] = String.format("%.5f", ((double) this.candidateStats5[i])/this.eventsProcessed);
            data[i][4] = String.format("%.5f", ((double) this.resolvedStats5[i])/this.eventsProcessed);
        }
        //System.out.println(Arrays.toString(this.candidateStats));
        //System.out.println(Arrays.toString(this.candidateStats5));
        
        String table = Table.getTable(header,data, new Table.ColumnConstrain(0,12)
                );
                
        System.out.println(table);
    }
    
    public TrackFinderNetwork setClassifierMode(NetworkMode mode){
        this.modeClassifier = mode; return this;
    }
    
    
    public void createCombinations(Bank b, TrackBuffer buffer){
        buffer.constructor.reset();
        TrackFinderUtils.fillConstructor(buffer.constructor, b);
    }
    
    public void evaluate(Tracks trk){
        
        float[]  input = new float[12];
        float[] output = new float[3];
        float[] input6 = new float[6];
        
        int nrows = trk.getRows();
        for(int row = 0; row < nrows; row++){        
                trk.getInput12raw(input, row);
                instarec.getClassifierModel().predict(input, output);
                int index = instarec.getClassifierModel().getLabel(output);
                /*if (Float.isNaN(output[0])){
                System.out.println(Arrays.toString(input)+" => " + Arrays.toString(output));
                }*/
            if(index==0){ 
                trk.dataNode().putShort(0,row,(short) -1);
                trk.dataNode().putFloat(1,row, 0.0f);
            } else {
                trk.dataNode().putShort(0,row,(short) index);
                trk.dataNode().putShort(3, row, (short) (index==1?-1:1));
                trk.dataNode().putFloat(1,row, output[index]);
            }
        }
    }
    
    public void evaluate6(Tracks trk){
        
        float[]  input = new float[6];
        float[] output = new float[3];
        float[] input6 = new float[6];
        
        int nrows = trk.getRows();
        for(int row = 0; row < nrows; row++){        
                trk.getInput6raw(input, row);
                instarec.getClassifier6().predict(input, output);
                int index = instarec.getClassifier6().getLabel(output);
                /*if (Float.isNaN(output[0])){
                System.out.println(Arrays.toString(input)+" => " + Arrays.toString(output));
                }*/
            if(index==0){ 
                trk.dataNode().putShort(0,row,(short) -1);
                trk.dataNode().putFloat(1,row, 0.0f);
            } else {
                trk.dataNode().putShort(0,row,(short) index);
                trk.dataNode().putShort(3, row, (short) (index==1?-1:1));
                trk.dataNode().putFloat(1,row, output[index]);
            }
        }
    }
    
    
    public void evaluateOLD(Tracks trk){
        
        float[]  input = new float[12];
        float[] output = new float[3];
        float[] input6 = new float[6];
        
        int nrows = trk.getRows();
        for(int row = 0; row < nrows; row++){
        
            if(this.modeClassifier==NetworkMode.CLASSIFIER_12){
                trk.getInput12(input, row);
                instarec.getClassifier().feedForwardSoftmax(input, output);
            } else {
                trk.getInput6(input6, row);
                instarec.classifier6.feedForwardSoftmax(input6, output);
            }
            int index = EJMLModel.getLabel(output);
            /*if (Float.isNaN(output[0])){
                System.out.println(Arrays.toString(input)+" => " + Arrays.toString(output));
            }*/
            if(index==0){ 
                trk.dataNode().putShort(0,row,(short) -1);
                trk.dataNode().putFloat(1,row, 0.0f);
            } else {
                trk.dataNode().putShort(0,row,(short) index);
                trk.dataNode().putShort(3, row, (short) (index==1?-1:1));
                trk.dataNode().putFloat(1,row, output[index]);
            }
        }
    }
    
    public void evaluate5(Tracks trk){
        
        float[]  input = new float[12];
        float[] output = new float[12];
        
        int nrows = trk.getRows();
        for(int row = 0; row < nrows; row++){
            trk.getInput12(input, row);
            int which = TrackFinderUtils.which(input);
            if(which>=0){
                
                //System.out.println("********** EVENT  + which = " + which);
                //System.out.println("input  ----- " + Arrays.toString(input));
                //System.out.println("output ----- " + Arrays.toString(output));
                
                //System.out.print("--- track before fixing ");
                //trk.show(row);
                instarec.getFixer().feedForwardReLULinear(input, output);
                trk.dataNode().putFloat(17+which/2, row, output[which]*112);
                trk.dataNode().putFloat(23+which/2, row, output[which+1]*112);
                                
                //System.out.print("--- track after  fixing ");
                //trk.show(row);
            }
        }
    }
    public void cleanup(Tracks tracks){
        int nrows = tracks.getRows();
        for(int j = 0; j < nrows-1; j++){
            for(int i = j+1; i < nrows; i++){
                if(tracks.status(j)>0){
                    if(tracks.contains(j, i)>0){
                        if(tracks.probability(j)>tracks.probability(i)){
                            tracks.setStatus(i, 0);
                        } else { tracks.setStatus(j, 0);}
                    }
                }
            }
        }
    }
    public void evaluateParameters(Tracks tracks){
        float[]  input = new float[6];
        float[] output = new float[3];
        
        int nrows = tracks.getRows();
        
        for(int j = 0; j < nrows; j++){
            int sector = tracks.sector(j);
            int charge = tracks.charge(j);
            int order  = charge>0?1:0;
            try {
                if(instarec.regression[order][sector-1]!=null){
                    tracks.getInput6raw(input, j);
                    instarec.regression[order][sector-1].predict(input, output);
                    //System.out.println("--- sector = " + sector);
                    //System.out.println("input -- " + Arrays.toString(input));
                    //System.out.println("output -- " + Arrays.toString(output));
                    
                    Vector3 vec = tracks.getVector(j, output);
                    //System.out.println(vec.mag() + "   " + vec);
                    tracks.dataNode().putFloat(5, j, (float) vec.x());
                    tracks.dataNode().putFloat(6, j, (float) vec.y());
                    tracks.dataNode().putFloat(7, j, (float) vec.z());
                }
                
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void constructor(Event e, TrackBuffer buffer){
        Bank[] b = e.read(schemas.get(0));
        TrackFinderUtils.fillConstructor(buffer.constructor, b[0]);
        buffer.constructor.sectors[0].create(buffer.tracks, 1);
    }
    
    
    public static void  constructor(Bank b, TrackBuffer buffer, int sector){        
        TrackFinderUtils.fillConstructor(buffer.constructor, b);
        buffer.constructor.sectors[sector-1].create(buffer.tracks, sector);
    }
    
    public InstaRecNetworks network(){
        return this.instarec;
    }
    
    
    public void  calculateTracks(){
        
    }
    
    public Tracks processBank(Bank b){                
        
        Tracks result = new Tracks(100);
        result.dataNode().setRows(0);
        if(b.getRows()==0) return result;
        int index_status = b.getSchema().getEntryOrder("status");

        for(int i = 0; i < b.getRows(); i++){
            b.putShort(index_status, i, (short) 1);
        }
        
        TrackBuffer buffer = new TrackBuffer();
        TrackFinderUtils.fillConstructor(buffer.constructor, b);
        //b.show();
        //buffer.constructor.show();
        int count = 0;
        for(int s = 0; s < 6; s++){
            count = result.getRows();
            buffer.constructor.sectors[s].create(buffer.tracks, s+1, cuts);
            this.evaluate(buffer.tracks);
            this.candidateStats[s] += buffer.tracks.getRows();
            TrackFinderUtils.copyFromTo(buffer.tracks, result);
            this.resolvedStats[s] += result.getRows()-count;
        }
        
        /*
        List<Integer> excluded = new ArrayList<>();
        int[] cid = new int[6];
        
        for(int r = 0; r < result.size(); r++){
            result.getClusters(cid, r);
            for(int c = 0; c < cid.length; c++) excluded.add(cid[c]);            
        }
        //b.getSchema().show();
        
        //System.out.println(b.getFloat("wireL2", 0));
        
        TrackFinderUtils.fillConstructor(buffer.constructor, b, excluded);
        int pre5 = result.getRows();
        
        
        for(int s = 0; s < 6; s++){
            count = result.getRows();
            buffer.constructor.sectors[s].create5(buffer.tracks, s+1, cuts);
            //buffer.tracks.show();
            this.evaluate5(buffer.tracks);
            this.evaluate(buffer.tracks);
            
            this.candidateStats5[s] += buffer.tracks.getRows();
            // ---- uncomment this ----
            // TrackFinderUtils.copyFromTo(buffer.tracks, result);
            
            
            this.resolvedStats5[s]  += result.getRows()-count;
            //buffer.tracks.show();
            //int before = result.size();
            //int after = result.size();
            //if(after>before)
            //System.out.printf(" before = %5d , after = %5d\n",before,after);
            //System.out.printf("sector %d - seeds %d\n",s+1,buffer.tracks.getRows());
        }*/
        
        //System.out.println(" pre/after 5 = " + pre5 + " " + result.getRows() + "   diff -  " + (result.getRows()-pre5));
        //this.evaluateParameters(result);
        //result.show();        
        return result;
    }
    
    public Bank recreate(Bank c, Tracks t){
        int size = t.getRows();
        
        Bank cn = new Bank(c.getSchema(),size*6);
        
        Map<Integer,Integer> map = c.getMap("id");
        int counter = 0;
        for(int r = 0; r < size; r++){
            for(int j = 0; j < 6; j++){
                int kk = t.dataNode().getInt(11+j, r);
                int id = (r+1)*1000 + kk;
                
                t.dataNode().putInt(11+j, r, counter+1);                
                int row = map.get(kk); 
                c.copyTo(cn,row, counter);
                cn.putShort("id", counter, (short) (counter+1));
                counter++;
            }
        }
        return cn;
    }
    
    public void processEvent(Event e){
        
        Schema schema  = schemas.get(0);
        int nRowLength = schemas.get(0).getEntryLength();
        int       size = e.scanLength(schema.getGroup(),schema.getItem());

        if(size>0){
            this.eventsProcessed++;
            int rows = size/nRowLength;
            //int sanity = size%nRowLength;
            //System.out.println("row Length = " + nRowLength + " size = " + size + "  rows = " + rows + " / " + sanity);
            Bank b = new Bank(schema, rows+7);
            e.read(b);
            Tracks t = this.processBank(b);
            
            if(t!=null){
                if(t.getRows()>0){
                    Bank br = new Bank(schemas.get(1),t.getRows());
                    t.dataNode().copy(br);
                    e.write(br);
                }
                /*
                if(t.getRows()>0) {
                    
                    System.out.println("--- recreating");
                    this.recreate(b, t);
                    
                    Bank br = new Bank(schemas.get(1),t.getRows());
                    t.dataNode().copy(br);
                    e.remove(br.getSchema());
                    //b.show();
                    
                    
                    e.write(br);
                }*/
            }
            //b.show();
        }
        /*
        Bank[] hbc = e.read(schemas.get(0));      
        Tracks t = this.processBank(hbc[0]);
        if(t!=null){
            if(t.getRows()>0) {
                Bank b = new Bank(schemas.get(1),t.getRows());
                t.dataNode().copy(b);
                //b.show();
                e.write(b);
            }
        }*/
    }
    
    public void process8(Event e){
        int position = e.scan(32101, 10);
        if(position>0){
            int length = e.scanLengthAt(32101,10,position);
            Leaf leaf = new Leaf(32101,10,"sbbbbff",length+128);
            e.readAt(leaf, 32101, 10, position);
            int[] stats = new int[12];
            //leaf.print();
            
            Tracks listUn   = new Tracks(100000);
            Tracks listRe   = new Tracks(300);                
            TrackConstructor tc = new TrackConstructor(); 
            TrackFinderUtils.fillConstructor(tc,leaf);
            //tc.show();
            int previous = 0;
            for(int sector = 0 ; sector < 6; sector++){
                tc.sectors[sector].create(listUn, sector+1, cuts);
                stats[sector*2] = listUn.getRows();
                evaluate6(listUn);
                //listUn.show();
                TrackFinderUtils.copyFromTo(listUn, listRe);
                stats[sector*2+1] = listRe.getRows() - previous;
                previous = listRe.getRows();
            }
            
            this.evaluateParameters(listRe);
            this.cleanup(listRe);
            //listRe.show();
            //Leaf statsLeaf = new Leaf(32000,21,"6i6i",2);
            //statsLeaf.setRows(1);
            //for(int i = 0; i < 12; i++) statsLeaf.putInt(0, i, stats[i]);
            //Node statsNode = new Node(32000,21, stats);
            //e.write(statsLeaf);
            e.write(listRe.dataNode());
        }
    }
    
    public void process8f(DataFrame<Event> frame){
        for(int j = 0; j < frame.getCount(); j++){
            Tracks listUn   = new Tracks(50000);
            Tracks listRe   = new Tracks(300);   
            TrackConstructor tc = new TrackConstructor(); 
            Leaf leaf = new Leaf(32101,10,"2s2b2f3b",2048);
            
            Event e = (Event) frame.getEvent(j);
            int position = e.scan(32101, 10);
            if(position>0){
                
                int length = e.scanLengthAt(32101,10,position);
                e.readAt(leaf, 32101, 10, position);
                //leaf.print();
                
                listUn.dataNode().setRows(0);
                listRe.dataNode().setRows(0);

                TrackFinderUtils.fillConstructor(tc,leaf);
            //tc.show();
            for(int sector = 0 ; sector < 6; sector++){
                tc.sectors[sector].create(listUn, sector+1, cuts);

                evaluate6(listUn);
                //listUn.show();
                TrackFinderUtils.copyFromTo(listUn, listRe);
            }
            
            this.evaluateParameters(listRe);
            //listRe.show();
            e.write(listRe.dataNode());
        }
        }
    }
    
    public void process(Event e){
        
        Bank[] bankhb = e.read(schemas.get(0));       
        Bank   banksg = new Bank(schemas.get(1),bankhb[0].getRows());
        TrackFinderUtils.getSegmentBank(banksg,bankhb[0]);                
        
        //banksg.show();
        
        Tracks listUn   = new Tracks(100000);
        Tracks listRe   = new Tracks(300);
                
        TrackConstructor tc = new TrackConstructor(); 
        
        //TrackFinderUtils.fillConstructor(tc, banksg);
        TrackFinderUtils.fillConstructor(tc, bankhb[0]);
        e.read(btb);
        
        //if (btb.getRows()==0) return;
        
        //System.out.println(" NEW EVENT "  + bankhb[0].getRows());
        //bankhb[1].show();
        //tc.show();


        //btb.show();
        //tc.show();
        for(int sector = 0 ; sector < 6; sector++){
            tc.sectors[sector].create(listUn, sector+1, cuts);
            //System.out.println("SECTOR " + (sector+1) + "  cluster size = " + banksg.getRows());
            evaluate(listUn);
            //System.out.println(" SECTOR " + (sector+1));
            //listUn.show();
            TrackFinderUtils.copyFromTo(listUn, listRe);
        }
        
        List<Integer> excluded = new ArrayList<>();
        int[] cid = new int[6];
        
        for(int r = 0; r < listRe.size(); r++){
            listRe.getClusters(cid, r);
            for(int c = 0; c < cid.length; c++) excluded.add(cid[c]);            
        }
        
        //listRe.show();
        tc.reset();
        TrackFinderUtils.fillConstructor(tc, banksg, excluded);
        for(int sector = 0 ; sector < 6; sector++){
            tc.sectors[sector].create5(listUn, sector+1, cuts);
            evaluate5(listUn);
            evaluate(listUn);
            //listUn.show();
            TrackFinderUtils.copyFromTo(listUn, listRe);
        }
        //listRe.show();
        e.write(listRe.dataNode());
        
    }
    
    public static class TrackBuffer {
        public TrackConstructor constructor = new TrackConstructor();
        public Tracks                tracks = new Tracks(85000);
    }
    
    
    public static void debug(String file, int event, boolean debug){
        TrackFinderNetwork net = new TrackFinderNetwork();
        net.init("etc/networks/clas12default.network", 2);
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks(net.schemaNames);
        Event e = new Event();
        r.getEvent(e, event);
        
        e.read(b);
        
        Tracks cv = new Tracks(128);
        Tracks ai = new Tracks(128);
        
        DataExtractor.getTracks(cv, b[1], b[0]);
        
        cv.show();
        
        TrackBuffer buffer = new TrackBuffer();
        
        for(int i = 0; i < cv.size(); i++){
            System.out.println("TRACK # - " + i);
            cv.show(i);
            int sector = cv.sector(i);
            TrackFinderNetwork.constructor(b[0], buffer, sector);
            
            net.evaluate(buffer.tracks);
            if(debug) buffer.tracks.show();
            ai.dataNode().setRows(0);
            TrackFinderUtils.copyFromTo(buffer.tracks, ai);
            ai.show();
            float[] f = new float[12];
            for(int j = 0; j < ai.getRows(); j++){
                ai.getInput12(f, j);
                System.out.println(Arrays.toString(f));
            }
        }
    }
    
    public void show(Bank b, int sector){
        TrackBuffer buff = new TrackBuffer();
        TrackFinderUtils.fillConstructor(buff.constructor, b);
        buff.constructor.sectors[sector-1].create(buff.tracks, sector);
        this.evaluate(buff.tracks);
        buff.tracks.show();
    }
    
    public void evaluate(int[] array, Bank b){
        Map<Integer,Integer> map = b.getMap("id");
        float[] f = new float[12];
        float[] a = new float[12];
        for(int i = 0; i < 6; i++){
            float w1 = b.getFloat("wireL1",map.get(array[i]));
            float w6 = b.getFloat("wireL6",map.get(array[i]));
            
            f[i*2] = w1/112.0f;
            f[i*2+1] = w6/112.0f;
            a[i*2] = w1;
            a[i*2+1] = w6;
        }
        float[] result = new float[3];
        this.instarec.getClassifier().feedForwardSoftmax(f, result);
        System.out.println("EVALUATE::");
        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(f)+" ==> " + Arrays.toString(result));
    }
    public static void processFile(String file){
        TrackFinderNetwork net = new TrackFinderNetwork();
        net.init("etc/networks/clas12gold.network", 0);
        //net.init("clas12default.network", 15);
        
        HipoReader r = new HipoReader(file);
        net.init(r.getSchemaFactory());
        //net.setUseCuts(false);
        HipoWriter w = HipoWriter.create("wout.h5", r);
        
        Event e = new Event();
        
        //for(int i = 0; i < 2000; i++){
        while(r.hasNext()){
            r.nextEvent(e);
            
            //System.out.println("----- event");
            net.processEvent(e);
            w.addEvent(e);
        }
        w.close();
        
        net.printSummary();
    }
    public static void main(String[] args){
        
        //String file = "rec_clas_005342.evio.00000.hipo";
        
        //String file = "rec_clas_005342.evio.00370.hipo";
        String file = "/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629.hipo";        
        //String file = "cook_very_new_10.h5";
        //String file = "recon_noSegmentRemoval_allCandCrossLists_noSeedCut_newConvChoiceRoutine_toTB.hipo";
        //String file = "wout.h5";
        TrackFinderNetwork net = new TrackFinderNetwork();
        net.init("etc/networks/clas12default.network", 0);
        //net.init("clas12default.network", 15);
        
        HipoReader r = new HipoReader(file);
        net.init(r.getSchemaFactory());
        Bank[] b = r.getBanks("DC::tdc");
        
        Leaf leaf = new Leaf(32101,10,"sbbbbff",2048);
        
        //net.setUseCuts(false);
        HipoWriter w = HipoWriter.create("wout.h5", r);
        
        Event e = new Event();
        DriftChamber dc = new DriftChamber();
        dc.init(r.getSchemaFactory());
        //for(int i = 0; i < 200; i++){
        DataFrame<Event>  frame = new DataFrame<>();
        
        for(int i = 0; i < 8; i++) frame.addEvent(new Event());
        
        while(r.hasNext()){
            r.nextFrame(frame);
            //r.nextEvent(e);
            //e.read(b);
            //dc.segmentsFromBank(leaf, b[0]);
            for(int i = 0 ; i < frame.getCount(); i++){
                dc.processEvent((Event) frame.getEvent(i)); 
                net.process8((Event) frame.getEvent(i));
            }
            /*
            dc.processEvent8f(frame);
            net.process8f(frame);
            */
        }
        w.close();
        
        net.printSummary();
    }
}
