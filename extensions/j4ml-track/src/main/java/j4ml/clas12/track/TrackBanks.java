/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.track;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.utils.ProgressPrintout;
import j4np.utils.io.DataArrayUtils;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 *
 * @author gavalian
 */
public class TrackBanks {
    
    private String tdcBankName      = "DC::tdc";
    private String hitsBankName     = "HitBasedTrkg::HBHits";    
    private String clustersBankName = "TimeBasedTrkg::TBClusters";
    private String trackBankName    = "TimeBasedTrkg::TBTracks";
    
    private Random            rand  = new Random();
    private List<Track>  tracksList = new ArrayList<>();
    
    TextFileWriter   writer6p  = new TextFileWriter();
    TextFileWriter   writer36p = new TextFileWriter();
    
    TextFileWriter   writer6n  = new TextFileWriter();
    TextFileWriter   writer36n = new TextFileWriter();
    
    TextFileWriter  writer6Reg = new TextFileWriter();
    TextFileWriter  writer12Reg = new TextFileWriter();
    TextFileWriter  writer36Reg = new TextFileWriter();
    
    List<Bank>  bankList = new ArrayList<>();
    private int counter = 0;
    
    ClusterStore store = new ClusterStore();
    ClusterCombinations combi = new ClusterCombinations();
        
    public TrackBanks(){
        
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
    
    public DCSector getSector(int[] clusters){
        DCSector sector = new DCSector();
        int nrows = bankList.get(0).getRows();
        for(int r = 0; r < nrows; r++){
            int clusterID = bankList.get(0).getInt("clusterID", r);
            boolean belongs = false;
            for(int c = 0; c < clusters.length; c++) 
                if(clusterID==clusters[c]) belongs = true;
            if(clusterID==-1) belongs = false;
            
            if(belongs){
                int superlayer = bankList.get(0).getInt("superlayer",r);
                int      layer = bankList.get(0).getInt("layer",r);
                int       wire = bankList.get(0).getInt("wire",r);
                sector.setWire(superlayer-1, layer-1, wire-1, 1);
            }
        }
        return sector;
    }
    
    public void init(HipoReader reader){
       bankList.clear();
       bankList.add(reader.getBank(hitsBankName));
       bankList.add(reader.getBank(clustersBankName));
       bankList.add(reader.getBank(trackBankName));
       
       writer6p.open("dc_tracks_06_features_positive.lsvm");
       writer36p.open("dc_tracks_36_features_positive.lsvm");
       
       writer6n.open("dc_tracks_06_features_negative.lsvm");
       writer36n.open("dc_tracks_36_features_negative.lsvm");
       
       writer6Reg.open("dc_regression_06_features.lsvm");
       writer12Reg.open("dc_regression_12_features.lsvm");
       writer36Reg.open("dc_regression_36_features.lsvm");
    }
    
    public void read(Event event){
        for(int i = 0; i < bankList.size(); i++) event.read(bankList.get(i));
        tracksList = Track.read(bankList.get(2), bankList.get(1));
    }
    
    /*
    public void process(){
        
        Bank clusters = bankList.get(1);
        Bank      tdc = bankList.get(0);
        int nrows = this.bankList.get(1).getRows();
        for(int r = 0; r < nrows; r++){
            int cluster = clusters.getInt("id",r);
            for(int h = 0; h < 12; h++){
                String column = String.format("Hit%d_ID",h+1);
                int index = clusters.getInt(column,r);
                tdc.setByte("order", index-1, (byte) cluster);
            }
        }
    }
    */
    
    public List<Track>  getBySector(int sector){
        List<Track> tracks = new ArrayList<>();
        for(int i = 0; i < this.tracksList.size(); i++){
            if(tracksList.get(i).sector==sector
                    &&tracksList.get(i).complete()==true
                    &&tracksList.get(i).isValid()==true
                    ) 
                tracks.add(tracksList.get(i));
        }
        return tracks;
    }
    
    public List<Track>  getBySector(int sector, boolean checkValid){
        List<Track> tracks = new ArrayList<>();
        for(int i = 0; i < this.tracksList.size(); i++){
            if(tracksList.get(i).sector==sector
                    &&tracksList.get(i).complete()==true){
                tracks.add(tracksList.get(i));
            }
        }
        return tracks;
    }
    
    public void print(){
        boolean printCounter = false;
        for(int i = 1 ; i <= 6; i++){
            List<Track> tracks = getBySector(i);
            if(tracks.size()==2){
                /*System.out.printf("found tracks = %5d, at sector %3d\n",
                        tracks.size(), i);
                */
                String id_1 = DataArrayUtils.intToString(tracks.get(0).clusters," ");
                String id_2 = DataArrayUtils.intToString(tracks.get(1).clusters," ");
                
                String id_1_2 = DataArrayUtils.intToString(
                        this.swap(
                                tracks.get(0).clusters,tracks.get(1).clusters,
                                rand.nextInt(2)+1)
                        ," ");
                
                String id_2_1 = DataArrayUtils.intToString(
                        this.swap(
                                tracks.get(1).clusters,tracks.get(0).clusters,
                                rand.nextInt(2)+1)
                        ," ");
                
                int[] ids_1_2 = this.swap(
                                tracks.get(0).clusters,tracks.get(1).clusters,
                                rand.nextInt(2)+1);
                int[] ids_2_1 = this.swap(
                                tracks.get(1).clusters,tracks.get(0).clusters,
                                rand.nextInt(2)+1);
                
                DCSector s_1 = getSector(tracks.get(0).clusters);
                DCSector s_2 = getSector(tracks.get(1).clusters);
                
                DCSector s_1_2 = getSector(ids_1_2);
                DCSector s_2_1 = getSector(ids_2_1);
                int charge_1 = 1;
                int charge_2 = 1;
                
                if(tracks.get(0).charge<0) charge_1 = 2;
                if(tracks.get(1).charge<0) charge_2 = 2;
                
                System.out.println("----------------------");
                for(int kk = 0; kk < 6; kk++) {
                    System.out.printf("%d : %8.3f ", kk+1,s_1.getSuperLayerSlope(kk));
                } System.out.println();
                s_1.show();
                /*System.out.printf("%d  %s\n", charge_1 ,s_1.getFeaturesString());
                System.out.printf("%d  %s\n",0, s_1_2.getFeaturesString());
                System.out.printf("%d  %s\n", charge_2 ,s_2.getFeaturesString());
                System.out.printf("%d  %s\n",0, s_2_1.getFeaturesString());
                */
                if(tracks.get(0).charge>0){
                    writer6p.writeString(String.format("%d  %s", 1 ,s_1.getFeaturesString()));
                    writer6p.writeString(String.format("%d  %s",0, s_1_2.getFeaturesString()));
                    writer36p.writeString(String.format("%d  %s", 1 ,s_1.getFeaturesStringExtended()));
                    writer36p.writeString(String.format("%d  %s",0, s_1_2.getFeaturesStringExtended()));
                } else {
                    writer6n.writeString(String.format("%d  %s", 2 ,s_1.getFeaturesString()));
                    writer6n.writeString(String.format("%d  %s",0, s_1_2.getFeaturesString()));
                    writer36n.writeString(String.format("%d  %s", 2 ,s_1.getFeaturesStringExtended()));
                    writer36n.writeString(String.format("%d  %s",0, s_1_2.getFeaturesStringExtended()));
                }
                
                if(tracks.get(1).charge>0){
                    writer6p.writeString(String.format("%d  %s", 1 ,s_2.getFeaturesString()));
                    writer6p.writeString(String.format("%d  %s",0, s_2_1.getFeaturesString()));
                    writer36p.writeString(String.format("%d  %s", 1 ,s_2.getFeaturesStringExtended()));
                    writer36p.writeString(String.format("%d  %s",0, s_2_1.getFeaturesStringExtended()));
                } else {
                    writer6n.writeString(String.format("%d  %s", 2 ,s_2.getFeaturesString()));
                    writer6n.writeString(String.format("%d  %s",0, s_2_1.getFeaturesString()));
                    writer36n.writeString(String.format("%d  %s", 2 ,s_2.getFeaturesStringExtended()));
                    writer36n.writeString(String.format("%d  %s",0, s_2_1.getFeaturesStringExtended()));
                }

                /*System.out.println(" clusters 1 : " + id_1);
                System.out.println(" clusters 2 : " + id_2);
                System.out.println(" clusters 3 : " + id_1_2);
                System.out.println(" clusters 4 : " + id_2_1);
                */
                
                for(Track t : tracks){
                    //System.out.println(t);
                    DCSector sector = getSector(t.clusters);
                    //sector.show();
                    //System.out.println(sector.getFeaturesString());
                    //System.out.println(sector.getFeaturesStringExtended());
                }                
                counter++;
                printCounter = true;
            }
        }
        
        //if(printCounter) System.out.printf("counter = %8d\n",counter);
    }
    
    
    public void oneTrack(){
        
        
        for(int s = 1; s <= 6; s++){
            
            List<Track> trackComplete = this.getBySector(s, false);
            List<Track>    trackValid = this.getBySector(s);
            
            if(trackValid.size()==1&&trackComplete.size()==1){
                
            
                int   nrows = bankList.get(1).getRows()/2;
                Bank  cbank = bankList.get(1);
                
                store.reset();
                //ClusterStore store = new ClusterStore();
                //System.out.println(" SECTOR = " + s + "   rows = " + nrows);
                for(int r = 0; r < nrows; r++){
                    int cid    = cbank.getInt("id",r);
                    int sector = cbank.getInt("sector",r);
                    int slayer = cbank.getInt("superlayer",r);
                    double wire = cbank.getFloat("avgWire",r);
                    //System.out.printf("%3d %3d %3d %8.3f\n",cid,sector,slayer,wire);
                    if(sector==s){
                        
                        store.add(slayer-1, cid, wire);
                    }
                }
                
                
                
                store.getCombinationsFull(combi);
                
                int index = combi.find(trackValid.get(0).clusters);
                int trkCharge = 0;
                
                if(index>=0) {
                    if(trackValid.get(0).charge>0){
                        combi.setRow(index).setStatus(1);
                        trkCharge = 1;
                    } else {
                        combi.setRow(index).setStatus(2);
                        trkCharge = -1;
                    }
                }
                
                if(combi.getSize()>4&&index>=0&&combi.getSize()<36){
                    //System.out.println(trackComplete.get(0));
                    //System.out.println("combinations = " + combi.getSize() + "  best index = " + index);                    
                    //System.out.println(combi.getString(false));
                    if(trkCharge>0){
                        writer6p.writeString("------");
                        writer36p.writeString("------");
                        for(int k = 0; k < combi.getSize(); k++){
                            int status = combi.setRow(k).getStatus();
                            DCSector sector = getSector(combi.getLabels(k));
                            writer6p.writeString(String.format("%d  %s",status, sector.getFeaturesString()));
                            writer36p.writeString(String.format("%d  %s",status, sector.getFeaturesStringExtended()));
                        }
                    } else {
                        writer6n.writeString("------");
                        writer36n.writeString("------");
                        for(int k = 0; k < combi.getSize(); k++){
                            int status = combi.setRow(k).getStatus();
                            DCSector sector = getSector(combi.getLabels(k));
                            writer6n.writeString(String.format("%d  %s",status, sector.getFeaturesString()));
                            writer36n.writeString(String.format("%d  %s",status, sector.getFeaturesStringExtended()));
                        }
                    }
                }
                //System.out.println(trackComplete.get(0));
                //System.out.println(store.toString());
            }
            
        }
    }
    
    
    public void show(){

        //List<Track> trackList = Track.read(bankList.get(2), bankList.get(1));        
        System.out.println("showing track list : " + tracksList.size());
        for(int i = 0; i < tracksList.size(); i++){
            System.out.println(tracksList.get(i));
            DCSector sector = getSector(tracksList.get(i).clusters);
            //sector.show();
        }        
        //bankList.get(0).show();
    }
    
    public int countCharge(List<Track> ts, int charge){
        int count = 0;
        for(Track t : ts) if(t.charge==charge) count++;
        return count;
    }
        
    public void regression(){
        
        List<Track>    trackValid = Track.getValid(tracksList);
        List<Track>    t = Track.getComplete(trackValid);
        
        if(t.size()==2&&countCharge(t,1)==1&&countCharge(t,-1)==1){
            //System.out.println("found an event with two tracks");
            int nIndex = 0;
            int pIndex = 1;
            if(t.get(0).charge>0){ nIndex = 1; pIndex = 0;}
            //for(Track tr : t) System.out.println(tr);
            //System.out.println(t.get(nIndex));
            //System.out.println(t.get(pIndex));
            DCSector s_1 = this.getSector(t.get(nIndex).clusters);
            DCSector s_2 = this.getSector(t.get(pIndex).clusters);
            
            double[] f06_1 = s_1.getFeatures6();
            double[] f06_2 = s_2.getFeatures6();
            
            double[] f12_1 = s_1.getFeatures12();
            double[] f12_2 = s_2.getFeatures12();
            
            double[] f36_1 = s_1.getFeatures36();
            double[] f36_2 = s_2.getFeatures36();
            
            String nStringData = String.format("%.4f,%.4f,%.4f,%.4f", 
                    t.get(nIndex).vector.mag(),t.get(nIndex).vector.theta(),
                    t.get(nIndex).vector.phi(),t.get(nIndex).vertex.z()
                    );
            
            String pStringData = String.format("%.4f,%.4f,%.4f,%.4f",
                    t.get(pIndex).vector.mag(),t.get(pIndex).vector.theta(),
                    t.get(pIndex).vector.phi(),t.get(pIndex).vertex.z()
                    );
            String  data_06 = nStringData + "," + pStringData + "," +
                    DataArrayUtils.doubleToString(f06_1, ",") + DataArrayUtils.doubleToString(f06_2, ",");
            String  data_12 = nStringData + "," + pStringData + "," +
                    DataArrayUtils.doubleToString(f12_1, ",") + DataArrayUtils.doubleToString(f12_2, ",");
            String  data_36 = nStringData + "," + pStringData + "," +
                    DataArrayUtils.doubleToString(f36_1, ",") + DataArrayUtils.doubleToString(f36_2, ",");
            
            //System.out.println(data_06);
            //System.out.println(data_12);
            //System.out.println(data_36);
            
            this.writer6Reg.writeString(data_06.substring(0, data_06.length()-1));
            this.writer12Reg.writeString(data_12.substring(0, data_12.length()-1));
            this.writer36Reg.writeString(data_36.substring(0, data_36.length()-1));
            
            //System.out.print(DataArrayUtils.doubleToString(f06_1, ","));
            //System.out.println(DataArrayUtils.doubleToString(f06_2, ","));
        }
    }
    
    public void close(){
        writer6p.close();
        writer36p.close();
        writer6n.close();
        writer36n.close();
        
        writer6Reg.close();
        writer12Reg.close();
        writer36Reg.close();        
    }
    
    public static void main(String[] args){
        
        
        List<String> files = new ArrayList<>();
        
        files.add("/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00055-00059.hipo");
        
        if(args.length>0){
            files.clear();
            for(int i = 0; i < args.length; i++){
                files.add(args[i]);
            }
        }
        
        HipoReader chain = new HipoReader();
        //chain.addFile("/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00055-00059.hipo");
        //chain.addFiles(files);
        chain.open(files.get(0));
        
        TrackBanks tracks = new TrackBanks();
        
        tracks.init(chain);
        
        Event event = new Event();
        ProgressPrintout progress = new ProgressPrintout();
        //for(int i = 0; i < 10000; i++){
        while(chain.hasNext()){
            chain.nextEvent(event);
            tracks.read(event);
            //tracks.show();            
            //tracks.print();
            //tracks.oneTrack();
            tracks.regression();
            progress.updateStatus();
        }
        
        tracks.close();
    }
}
