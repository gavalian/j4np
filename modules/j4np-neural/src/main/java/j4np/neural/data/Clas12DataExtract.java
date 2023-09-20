/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.Vector3;
import j4np.utils.FileUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.Axis;

/**
 *
 * @author gavalian
 */
public class Clas12DataExtract {
    
    Bank[] dcBanks = null;
    HipoReader   r = null;
    Random rndm = new Random();
    
    public int maxEvents = 45000;
    
    int[] recordBinsTr = new int[40];
    int[] recordBinsVa = new int[40];    
    Axis         pAxis = null;
    
    String  outputFile = "clas12_neural_data";
    
    HipoWriter[]     w = new HipoWriter[2];
    
    String[] bankNames = new String[]{  
        "TimeBasedTrkg::TBTracks",
        "TimeBasedTrkg::TBClusters",
        "TimeBasedTrkg::TBHits"
    };
    
    public Clas12DataExtract(){
        
    }
    
    public void init(String file){
        r = new HipoReader();
        r.setDebugMode(0);
        r.open(file);
        dcBanks = r.getBanks(bankNames);
        
        for(int i = 0; i < recordBinsTr.length; i++){
            recordBinsTr[i] = 0;
            recordBinsVa[i] = 0;
        }
        pAxis = new Axis(20,0.0,10.0);
        
        w[0] = new HipoWriter();
        w[1] = new HipoWriter();
        w[0].open(this.outputFile + "_tr.h5");
        w[1].open(this.outputFile + "_va.h5");
    }
    
    public int getWriterTag(Vector3 v, int charge){
        int bin = this.pAxis.getBin(v.mag()) + 1;
        if(bin<1) return -1;        
        if(charge>0) bin += 20;
        return bin;
    }
    
    public void processFile(String file){
        this.init(file);
        //Random rndm = new Random();
        Tracks  tracks = new Tracks(500);
        Vector3 vector = new Vector3();
        Event outEvent = new Event();
        while(r.hasNext()==true){
            r.nextEvent(dcBanks);            
            for(int s = 1; s <=6; s++){
                TrackReader.reco2tracksForSector(tracks, dcBanks[0], dcBanks[1],s);
                if(tracks.size()>0){
                    tracks.dataNode().print();
                    tracks.vector(vector, 0);
                    int charge = tracks.charge(0);
                    int tag = this.getWriterTag(vector, charge);
                    if(tag>0&&tag<=40){

                        outEvent.reset();
                        outEvent.setEventTag(tag);
                        outEvent.write(tracks.dataNode());
                        double which = rndm.nextDouble();
                        if(which>0.5) w[0].addEvent(outEvent); else w[1].addEvent(outEvent);
                    }
                }
            }
            
        }
        
        w[0].close();
        w[1].close();
    }
    
    public void processFiles(List<String> files){
        this.init(files.get(0));
        Event event = new Event();
        for(String file : files){
            r = new HipoReader();
            r.setDebugMode(0);
            r.open(file);
            while(r.hasNext()==true){
                r.nextEvent(event);
                this.processEvent(event);
            }
        }
        w[0].close();w[1].close();
    }
    protected boolean contains(int c, int[] carray){
        for(int i = 0; i < carray.length; i++) if(c==carray[i]) return true;
        return false;
    }
    public void makeHits(CompositeNode dc, Tracks trk, Bank b, int sector){
        dc.setRows(0);
        int      nrows = b.getRows();
        int[] clusters = new int[6];
        for(int r = 0; r < nrows; r++){

            int sec = b.getInt("sector", r);
            if(sec==sector){
                int cid = b.getInt("clusterID", r);
                boolean writeHit = false;
                for(int i = 0; i < trk.getRows(); i++){
                    trk.getClusters(clusters, i);
                    if(contains(cid,clusters)) writeHit=true;
                }
                
                if(writeHit==true){
                    int superlayer = b.getInt("superlayer", r);
                    int layer = b.getInt("layer", r);
                    int  wire = b.getInt("wire", r);
                    
                    int index = ((superlayer-1)*6 + layer-1)*112 + wire;
                    
                    int row = dc.getRows();
                    dc.putShort(0, row, (short) index);
                    dc.setRows(row+1);
                }
            }
        }
    }
    
    public void makeHitsAll(CompositeNode dc, Bank b, int sector){
        int      nrows = b.getRows();
        int[] clusters = new int[6];
        dc.setRows(0);
        for(int r = 0; r < nrows; r++){
            int sec = b.getInt("sector", r);
            if(sec==sector){
                int superlayer = b.getInt("superlayer", r);
                    int layer = b.getInt("layer", r);
                    int  wire = b.getInt("wire", r);
                    int index = ((superlayer-1)*6 + layer-1)*112 + wire;
                    int row = dc.getRows();
                    dc.putShort(0, row, (short) index);
                    dc.setRows(row+1);
            }
        }
    }
    public void processEvent(Event e){
        
        Tracks  tracks = new Tracks(500);
        CompositeNode cnode = new CompositeNode(32144,1,"s",5000);
        CompositeNode hnode = new CompositeNode(32144,2,"s",5000);
        
        Vector3 vector = new Vector3();
        Vector3 vertex = new Vector3();
        Event outEvent = new Event();
        e.read(dcBanks);
        
        for(int s = 1; s <=6; s++){
            
            TrackReader.reco2tracksForSector(tracks, dcBanks[0], dcBanks[1],s);
            if(tracks.size()>0){
                if(dcBanks[2].getRows()>0){
                    this.makeHits(cnode, tracks, dcBanks[2], s);
                    this.makeHitsAll(hnode, dcBanks[2], s);
                    //cnode.print();
                }
                //tracks.dataNode().print();
                tracks.vector(vector, 0);
                tracks.vertex(vertex, 0);
                int charge = tracks.charge(0);
                int tag = this.getWriterTag(vector, charge);
                if(tag>0&&tag<=40&&vector.mag()<10.&&vertex.z()>-15&&vertex.z()<5){
                    
                    outEvent.reset();
                    outEvent.setEventTag(tag);
                    outEvent.write(tracks.dataNode());
                    //System.out.printf(" node ! size = %d tracks size = %d\n",cnode.getRows(),tracks.getRows());
                    outEvent.write(cnode);
                    outEvent.write(hnode);
                    double which = rndm.nextDouble();
                    if(which>0.5){
                        if(this.recordBinsTr[tag-1]<this.maxEvents){
                            w[0].addEvent(outEvent); this.recordBinsTr[tag-1]++;
                        }
                    } 
                    else { 
                        if(this.recordBinsVa[tag-1]<this.maxEvents){
                            w[1].addEvent(outEvent); this.recordBinsVa[tag-1]++;
                        }
                    }
                }
            }
        }        
    }
    
    public static void main(String[] args){
        String file1 = "/Users/gavalian/Work/DataSpace/005342/rec_clas_005342.evio.00000-00004.hipo";
        String file2 = "/Users/gavalian/Work/DataSpace/005342/rec_clas_005342.evio.00070-00074.hipo";
        String file3 = "/Users/gavalian/Work/DataSpace/005342/rec_clas_005342.evio.00080-00084.hipo";
        List<String> files = FileUtils.getFileListInDir("/Users/gavalian/Work/DataSpace/neural" ,"hipo");
        Clas12DataExtract ext = new Clas12DataExtract();
        ///ext.processFiles(Arrays.asList(file1,file2,file3));
        System.out.println(Arrays.toString(files.toArray()));
        ext.processFiles(files);
    }
}
