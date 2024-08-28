/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.central;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Query;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.instarec.utils.EJMLModel;
import j4np.physics.Vector3;
import j4np.utils.io.TextFileReader;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * int[] layerC = new int[]{ 7, 10, 12};
 * int[] layerZ = new int[]{ 8,  9, 11};
 * 
 * @author gavalian
 */
public class CentralTrack {
    
    public int       status = 0;
    public int[]      index = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
    public float[] features = new float[12];
    
    public static int[][] patterns = new int[][]{
        {0,1,2,3,   4,  5}, {0,1,2,3,   6,  8}, {0,1,2,3,   6, 10},
        {0,1,2,3,   6,  7}, {0,1,2,3,   6,  8}, {0,1,2,3,   6, 10},     
        //{0,1,2,3,   6,  9}, {0,1,2,3,   8,  9}, {0,1,2,3,   9, 10},        
        {0,1,2,3,   7, 11}, {0,1,2,3,   8, 11}, {0,1,2,3,  10, 11},        
        //-------------
        {0,1,4,5,   6,  7}, {0,1,4,5,   6,  8}, {0,1,4,5,   6, 10},        
        {0,1,4,5,   6,  9}, {0,1,4,5,   8,  9}, {0,1,4,5,   9, 10},        
        {0,1,4,5,   7, 11}, {0,1,4,5,   8, 11}, {0,1,4,5,  10, 11},
        //-------------
        {2,3,4,5,   6,  7}, {2,3,4,5,   6,  8}, {2,3,4,5,   6, 10},        
        {2,3,4,5,   6,  9}, {2,3,4,5,   8,  9}, {2,3,4,5,   9, 10},        
        {2,3,4,5,   7, 11}, {2,3,4,5,   8, 11}, {2,3,4,5,  10, 11},        
    };
    
    public CentralTrack(){
        
    }
    
    public boolean complete(int[] orders){
        for(int i = 0; i < orders.length; i++) 
            if(index[orders[i]]<=0) return false;
        return true;
    }
    
    
    public static CentralTrack  getSegment(CentralTrack trk, int segment){
        CentralTrack ts = new CentralTrack();
        ts.status = trk.status;
        boolean addTrack = true;
        for(int j = 0; j < patterns[segment].length; j++){
            int indx = patterns[segment][j];
            ts.index[indx] = trk.index[indx];
            if(ts.index[indx]<=0) addTrack = false;
        }
        if(addTrack==false) return null;
        return ts;
    }
    
    public static List<CentralTrack> getSegmented(CentralTrack trk){
        List<CentralTrack> segments = new ArrayList<>();
        for(int i = 0; i < patterns.length; i++){
            CentralTrack ts = new CentralTrack();
            ts.status = trk.status;
            boolean addTrack = true;
            for(int j = 0; j < patterns[i].length; j++){
                int indx = patterns[i][j];
                ts.index[indx] = trk.index[indx];
                if(ts.index[indx]<=0) addTrack = false;
            }
            if(addTrack) segments.add(ts);
        }
        return segments;
    }    
    
    public static int compare(int[] indexOne, int[] indexTwo){
        int count = 0;
        for(int i = 0; i < indexOne.length; i++) if(indexOne[i]==indexTwo[i]) count++; 
        return count;
    }
    
    public static boolean equivalent(int[] indexOne, int[] indexTwo){
        for(int i = 0; i < indexOne.length; i++){
            if(indexOne[i]==0&&indexTwo[i]!=0) return false;
        }
        //for(int i = 0; i < 6; i++) if(indexOne[i]!=indexTwo[i]) return false;
        return true;
    }
    
    public static List<CentralTrack> removeMatches(List<CentralTrack> tlist, List<CentralTrack> reference){
        List<CentralTrack> unmatched = new ArrayList<>();
        for(int k = 0; k < tlist.size(); k++){
            boolean keep = true;
            
            for(int j = 0; j < reference.size(); j++){
                int comp = compare(reference.get(j).index, tlist.get(k).index);
                
                if(comp==12) keep = false;
                //System.out.printf( " %d,%d compare = %d  %s\n",k,j,comp, keep);
            }
            if(keep) unmatched.add(tlist.get(k));
        }
        return unmatched;
    }
    
    public static CentralTrack findMatch(CentralTrack trk, List<CentralTrack> tracks){
        for(int i = 0; i < tracks.size(); i++){
            if(CentralTrack.equivalent(trk.index, tracks.get(i).index)){
                int match = compare(trk.index,tracks.get(i).index);
                //System.out.printf(" %5d : %d\n",i,match);                
                if(match>7&&match<10){                    
                    return tracks.get(i);
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString(){
        return String.format("%6d : %s", status, Arrays.toString(index));
    }
    
    public List<CentralTrack> getSeeds(Bank seeds){
        List<CentralTrack> tracks = new ArrayList<>();
        
        int nrows = seeds.getRows();
        for(int i = 0; i < nrows; i++){
            CentralTrack t = new CentralTrack();                
            t.status = seeds.getInt("id", i);
            t.index  = seeds.getIntArray(12, "cl1", i);
            List<CentralTrack> segments = CentralTrack.getSegmented(t);
            tracks.addAll(segments);
        }
        return tracks;
    }
    
    public List<CentralTrack> getTracks(Bank seeds){
        List<CentralTrack> tracks = new ArrayList<>();
        int nrows = seeds.getRows();
        for(int i = 0; i < nrows; i++){
            CentralTrack t = new CentralTrack();                
            t.status = seeds.getInt("id", i);
            t.index  = seeds.getIntArray(12, "cl1", i);
            tracks.add(t);
        }
        return tracks;
    }
    
    public List<CentralTrack> getSeeds(Bank seeds, boolean isTrue){
        List<CentralTrack> tracks = new ArrayList<>();
        
        int nrows = seeds.getRows();
        for(int i = 0; i < nrows; i++){
            CentralTrack t = new CentralTrack();                
            t.status = seeds.getInt("id", i);
            t.index  = seeds.getIntArray(12, "cl1", i);
            if(isTrue){
                if(t.status==1){
                    List<CentralTrack> segments = CentralTrack.getSegmented(t);
                    tracks.addAll(segments);
                }
            } else {
                if(t.status!=1){
                    List<CentralTrack> segments = CentralTrack.getSegmented(t);
                    tracks.addAll(segments);
                }
            }
        }
        return tracks;
    }
    
    
    public void process(String file){
        List<String> lines = TextFileReader.readFile("central24.network");
        EJMLModel model = EJMLModel.create(lines);
        
        System.out.println(" NETWORK:");
        System.out.println(model.summary());
        
        HipoReader r = new HipoReader(file);        
        Bank[] b = r.getBanks("cvtml::seeds","cvtml::clusters");
        while(r.nextEvent(b)==true){
            
            /*
            List<CentralTrack> tracks = getSeeds(b[0]);
            System.out.println("event -----------");
            for(CentralTrack t : tracks){
                float[]  features = CentralUtils.getFeaturesArray(b[1], t.index);
                float[]  result   = new float[2];
                
                model.feedForwardSoftmax(features, result);
                //System.out.println(Arrays.toString(features));
                System.out.println(t + "  " + Arrays.toString(result));
            }*/
            //System.out.printf("Event with combinations = %d\n",tracks.size());
        }
    }
    
    public static boolean valid(CentralTrack t, Bank b){
        for(int i = 0; i < t.index.length; i++){
            if(t.index[i]>0){
                int status = b.getInt("status", t.index[i]-1);
                if(status!=1) return false;
            }
        }
        return true;
    }
    
    public int findCluster(Bank b, int index){
        int sector = b.getInt("sector", index);
        int  layer = b.getInt("layer", index);
        System.out.println(" sector / layer = " + sector + " " + layer);
        for(int i = 0; i < b.getRows(); i++){
            if(i!=index){
                int sc = b.getInt("sector", i);
                int lc = b.getInt("layer", i);
                if(sc==sector&&lc==layer) return i;
            }
        }
        return -1;
    }
    
    public double distance(Bank b, int index1, int index2){
        Vector3 v1 = new Vector3(
                b.getFloat("xo", index1),
                b.getFloat("yo", index1),
                b.getFloat("zo", index1)
        );
        Vector3 v2 = new Vector3(
                b.getFloat("xo", index2),
                b.getFloat("yo", index2),
                b.getFloat("zo", index2)
        );
        return Math.sqrt( ( v1.x() - v2.x())*( v1.x() - v2.x())
                + ( v1.y() - v2.y())*( v1.y() - v2.y())
                + ( v1.z() - v2.z())*( v1.z() - v2.z())
        ) ;
    }
    
    public void extract2(String file){
        HipoReader r = new HipoReader(file);        
        Bank[] b = r.getBanks("cvtml::seeds","cvtml::clusters","RUN::config");
        
        HipoWriter hw = HipoWriter.create("central.h5", r);
        Event event = new Event();
        
        TextFileWriter w = new TextFileWriter("central2.csv");
        int counter = 0; int counter_all = 0;
        Random rn = new Random();
        while(r.next(event)==true){
            event.read(b);
            
            CentralTracks tracks = new CentralTracks();
            
            for(int i = 0; i < b[0].getRows(); i++){
                int[] cid = b[0].getIntArray(12, "cl1", i);
                tracks.fill(cid);
            }
            System.out.println("--new event");
            //tracks.show();
            List<int[]> list = new ArrayList<>();
            for(int j = 0; j < tracks.node().getRows(); j++){
                int[] data = new int[6];
                tracks.getSubTrack(j, data, 0);
                if(tracks.isComplete(data)&&list.size()==0&&tracks.status(data, b[1])==1){
                    list.add(data);
                }
            }
            
            System.out.println("size = " + list.size());
            if(list.size()==1){
                int[] cid = list.get(0);
                System.out.println(Arrays.toString(list.get(0)));
                //String dataTrue = Arrays.toString(list.get(0)).replaceAll("\\[", "").replaceAll("\\]", "");
                int which = rn.nextInt(4);
                int it = -1;
                
                for(int kk = 0; kk < 6; kk++){                                    
                    int itk = this.findCluster(b[1], cid[kk]);
                    double md = 100.0;
                    if(itk>=0){
                        double dist = this.distance(b[1], itk, cid[kk]);
                        if(dist<md){
                            md = dist; it = itk; which = kk;
                        }
                        //System.out.printf("%5d , distance = %f\n",kk,this.distance(b[1], itk, cid[kk]));
                    }
                }
                //System.out.println("which = " + which +  " cid = " + cid[which] + " it = " + it);

                if(it>=0){
                    int[] cidf = new int[6];
                    for(int i = 0; i < 6; i++) cidf[i] = cid[i];
                    double dist = this.distance(b[1], it, cid[which]);
                    System.out.printf(", distance = %f\n",this.distance(b[1], it, cid[which]));
                    cidf[which] = it;
                    System.out.println(Arrays.toString(cid));
                    System.out.println(Arrays.toString(cidf));
                    
                    float[] ft = tracks.getFeatures(cid, b[1]);
                    float[] ff = tracks.getFeatures(cidf, b[1]);
                    String dataTrue = Arrays.toString(ft).replaceAll("\\[", "").replaceAll("\\]", "");
                    String dataFalse = Arrays.toString(ff).replaceAll("\\[", "").replaceAll("\\]", "");
                   if(dist<10&&dist>1){
                       w.writeString(dataTrue+",1.0,0.0");                   
                       w.writeString(dataFalse+",0.0,1.0");
                   }
                }
            }
        }
        w.close();
    }
    public void extract(String file){
        
        HipoReader r = new HipoReader(file);        
        Bank[] b = r.getBanks("cvtml::seeds","cvtml::clusters","RUN::config");
        
        HipoWriter hw = HipoWriter.create("central.h5", r);
        Event event = new Event();
        
        TextFileWriter w = new TextFileWriter("central2.csv");
        int counter = 0; int counter_all = 0;
        Random rn = new Random();
        while(r.next(event)==true){
            event.read(b);
            List<CentralTrack> tracks = getTracks(b[0]);            
            counter_all++;            
            
            for(CentralTrack t : tracks){
                
                if(CentralTrack.valid(t, b[1])) counter++;
                
                if(t.status==1&&CentralTrack.valid(t, b[1])){

                    CentralTrack trk = CentralTrack.getSegment(t, 3);
                    if(trk!=null){
                        //hw.addEvent(event);
                        //System.out.println("================");
                        Query q = new Query(b[1],"status==1");
                        List<Integer> index = q.getIterator(b[1]);
                        System.out.println(Arrays.toString(t.index));
                        System.out.println(Arrays.toString(trk.index));
                        int which = rn.nextInt(4);
                        int it = this.findCluster(b[1], trk.index[which]-1);
                        //for(Integer indx : index) System.out.printf("%3d ",indx+1);
                        //System.out.println();
                        //System.out.println(" index = " + it + " " + b[1].getInt("sector",it) + " / " + b[1].getInt("layer",it));
                        int[] iitt = new int[6]; for(int j = 0; j < 6; j++) iitt[j] = t.index[j]-1;
                        int[] iiff = new int[6]; for(int j = 0; j < 6; j++) iiff[j] = t.index[j]-1;
                        iiff[which] = it;
                        
                        //System.out.println(Arrays.toString(iitt));
                        //System.out.println(Arrays.toString(iiff));

                        if(it>=0){
                            hw.addEvent(event);
                            StringBuilder st = new StringBuilder();
                            StringBuilder sf = new StringBuilder();
                            for(int k = 0; k < 6; k++) {
                                float[] f = CentralUtils.getFeatures(b[1], iitt[k]);
                                st.append(Arrays.toString(f));
                            }
                            for(int k = 0; k < 6; k++) {
                                float[] f = CentralUtils.getFeatures(b[1], iiff[k]);
                                sf.append(Arrays.toString(f));
                            }
                            //System.out.println(st.toString().replaceAll("\\]\\[", ",").replaceAll("\\]", "").replaceAll("\\[", ""));                            
                            //System.out.println(sf.toString().replaceAll("\\]\\[", ",").replaceAll("\\]", "").replaceAll("\\[", ""));
                            w.writeString(st.toString().replaceAll("\\]\\[", ",").replaceAll("\\]", "").replaceAll("\\[", "")+",1,0");
                            w.writeString(sf.toString().replaceAll("\\]\\[", ",").replaceAll("\\]", "").replaceAll("\\[", "")+",0,1");
                            
                        }
                    }
                    //b[0].show();
                    //b[1].show();
                  /*  List<CentralTrack> tt = CentralTrack.getSegmented(t);
                    List<CentralTrack> tracksF = getSeeds(b[0],false);
                    
                    for(int i = 0; i < tracksF.size(); i++){
                        CentralTrack trk = CentralTrack.getSegment(tracksF.get(i), 0);
                        
                        if(trk!=null){
                            System.out.println("********* " + CentralTrack.valid(trk, b[1]));
                            String label = "1,0";
                            if(CentralTrack.valid(trk, b[1])==false) label = "0,1";                                                        
                            String sf = CentralUtils.getFeaturesString(b[1], trk.index) + label;
                            if(CentralTrack.valid(trk, b[1])==true) 
                                w.writeString(sf);
                        }
                    }*/
                                        
                }
            }
            
        }
        hw.close();
        w.close();        
        System.out.println(" counter = " + counter + "  " + counter_all);
        
    }
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/Software/project-11.0/study/central/AISample_2.hipo";
    
        CentralTrack central = new CentralTrack();
        
        //central.process(file);
        
        central.extract2(file);
        
        /*
        HipoReader r = new HipoReader(file);
        
        Bank[] b = r.getBanks("cvtml::seeds","cvtml::clusters");
                        
        TextFileWriter w = new TextFileWriter("central.csv");
        
        List<CentralTrack> tracks = new ArrayList<>();
        List<CentralTrack> tfalse = new ArrayList<>();
        TGCanvas c = new TGCanvas();
        H1F h = new H1F("h",120,0,250);
        c.draw(h);
        //for(int jjj = 0; jjj < 30; jjj++){
        while(r.nextEvent(b)==true){
            //b[0].show();
            //r.nextEvent(b);
            
            tracks.clear();
            tfalse.clear();
            for(int i = 0; i < b[0].getRows(); i++){
                CentralTrack t = new CentralTrack();                
                //System.out.println("patterns = " + CentralTrack.patterns.length);
                t.status = b[0].getInt("id", i);
                t.index  = b[0].getIntArray(12, "cl1", i);
                if(t.status==1){
                    //System.out.println(t);
                    tracks = CentralTrack.getSegmented(t);
                    
                    //for(CentralTrack item : list) System.out.println("\t " + item);
                } else {
                    List<CentralTrack> tt = CentralTrack.getSegmented(t);
                    tfalse.addAll(tt);
                }                         
            }
                                    
            if(tracks.size()>12){
                for(int k = 0; k < tracks.size(); k++){
                    List<float[]> features = CentralUtils.getFeatures(b[1], tracks.get(k).index);
                    String datat = CentralUtils.array2string(features);
                    w.writeString(datat + "1,0");
                }
            }
           
        }
        
        w.close();*/
    }
}
