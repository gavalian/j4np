/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.data;

import j4ml.clas12.track.DCSector;
import j4ml.clas12.track.Track;
import j4np.utils.io.LSVMFileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.readers.TextFileWriter;

/**
 *
 * @author gavalian
 */
public abstract class ExtractModule {
    
    protected List<TextFileWriter> writers = new ArrayList<>();
    
    protected   Random            rand  = new Random();
    protected   LSVMFileReader   reader = new LSVMFileReader();
    
    
    public ExtractModule(String file){
        
        TextFileWriter writer = new TextFileWriter(); writer.open(file);
        writers.add(writer);
    }
    
    public ExtractModule(String... files){
        for(int i = 0; i < files.length; i++){
            TextFileWriter writer = new TextFileWriter(); writer.open(files[i]);
            writers.add(writer);
        }
    }
    
    public List<Track> forSector(List<Track>  tracks,int sector){
        List<Track> tList = new ArrayList<>();
        for(Track t : tracks) if(t.sector==sector) tList.add(t);
        return tList;
    }
    
    public int[]  swap(int[] a, int[] b, int howMany){
        int[] index = new int[a.length];
        for(int i = 0; i < index.length; i++) index[i] = a[i];
        for(int i = 0; i < howMany; i++){
            int which = rand.nextInt(6);
            index[which] = b[which];
        }
        return index;
    }
    
    public DCSector getSector(int[] clusters, BankStore store){
        DCSector sector = new DCSector();
        Bank bank = store.getMap().get("TimeBasedTrkg::TBHits");
        int nrows = bank.getRows();
        for(int r = 0; r < nrows; r++){
            int clusterID = bank.getInt("clusterID", r);
            boolean belongs = false;
            for(int c = 0; c < clusters.length; c++) 
                if(clusterID==clusters[c]) belongs = true;
            if(clusterID==-1) belongs = false;
            
            if(belongs){
                int superlayer = bank.getInt("superlayer",r);
                int      layer = bank.getInt("layer",r);
                int       wire = bank.getInt("wire",r);
                sector.setWire(superlayer-1, layer-1, wire-1, 1);
            }
        }
        return sector;
    }
    
    public abstract void process(BankStore store);
    
    public void finish(){
        for(TextFileWriter w : writers) w.close();
    }
}
