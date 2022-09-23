/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.common;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author gavalian
 */
public class DriftDataExtract {
    Bank   tbhits = null;
    Bank tbtr = null;
    Bank tbcl = null;
    Bank dctdc = null;
    
    int maxIter = 1_000_000;
    
    public void updateMatrix(DriftMatrix m, int clusterid, Bank hits){
        int rows = hits.getRows();
        for(int r = 0; r < rows; r++){
            int cid = hits.getInt("clusterID", r);
            if(cid==clusterid){
                int   sup = hits.getInt("superlayer", r);
                int layer = hits.getInt("layer", r);
                int  wire = hits.getInt("wire", r);
                int local = (sup-1)*6 + layer;
                m.fill(local, wire);
            }
        }
    }
    
    public void updateMatrixFromTrack(DriftMatrix m, Track t, Bank hits){
        for(int i = 0; i < t.clusters.length;i++){
            this.updateMatrix(m, t.clusters[i], hits);
        }
    }
    
    public DriftMatrix createMatrix(Bank tdc, int sector){
        DriftMatrix m = new DriftMatrix();
        int rows = tdc.getRows();
        for(int r = 0; r < rows; r++){
            int sec = tdc.getInt("sector", r);
            if(sec==sector){
                int layer = tdc.getInt("layer", r);
                int wire = tdc.getInt("component", r);
                m.fill(layer, wire);
            }
        }
        return m;
    }
    
    public void processFile(String file){
        
        HipoReader r = new HipoReader(file);
        tbtr = r.getBank("TimeBasedTrkg::TBTracks");
        tbcl = r.getBank("TimeBasedTrkg::TBClusters");
        tbhits = r.getBank("TimeBasedTrkg::TBHits");
        dctdc = r.getBank("DC::tdc");
        int counter = 0;
        Event e = new Event();
        while(r.hasNext()){
            r.nextEvent(e);
            e.read(tbtr);
            e.read(tbcl);
            e.read(tbhits);
            e.read(dctdc);
            
            List<Track> tracks = Track.read(tbtr, tbcl);
            List<Track> validated = Track.getValid(tracks);
            
            //tracks.forEach(System.out::println);
            
            for(int sector = 1; sector <=6; sector++){
                int s = sector;
                List<Track> partial = validated.stream()
                        .filter(trk -> trk.sector==s).collect(Collectors.toList());
                //System.out.printf(" sector %d -> track %d\n",sector,partial.size());
                if(partial.size()>0){
                    DriftMatrix m = new DriftMatrix();
                    for(Track tr : partial){
                        this.updateMatrixFromTrack(m, tr, tbhits);
                    }
                    DriftMatrix mdc = this.createMatrix(dctdc, sector);
                    m.normalize(); mdc.normalize();
                    System.out.println("data::0 "+mdc.toLSVM());
                    System.out.println("data::1 "+m.toLSVM());
                    //System.out.println("---");
                    //m.show();
                    //mdc.show();
                    counter++;
                }
            }
            /*
            for(int i = 0; i < tracks.size(); i++){
                DriftMatrix m = new DriftMatrix();
                this.updateMatrixFromTrack(m, tracks.get(i), tbhits);
                int sector = tracks.get(i).sector;
                DriftMatrix mdc = this.createMatrix(dctdc, sector);
                m.show();
                mdc.show();
                System.out.println("1,"+m.toCSV());
                System.out.println("0,"+mdc.toCSV());
            }
            System.out.println();            */
            if(counter>maxIter) break;
        }
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        DriftDataExtract ext = new DriftDataExtract();
        ext.processFile(file);
        /*
        DriftMatrix m = new DriftMatrix();
        m.fill(2, 10);
        m.fill(3, 10);
        m.fill(3, 11);
        m.show();*/
    }
}
