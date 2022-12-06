/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.trk;

import j4np.clas12.data.DetectorDataUtils;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.io.OptionParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import twig.data.Axis;
import twig.data.DataSetSerializer;
import twig.data.H2F;
import twig.data.H3F;

/**
 *
 * @author gavalian
 */
public class TrackExtractor {
    
    Bank dcTDC = null;
    Bank dcTDCbkg = null;
    Bank dcTRK = null;
    Bank dcCLT = null;
    Bank dcHIT = null;
    public String outputFile = "denoiser.h5";
    
    TrackSelector selector6 = null;    
    String[] banks = {"TimeBasedTrkg::TBTracks","TimeBasedTrkg::TBClusters"};    
    Axis axis = new Axis(20,0.5,10.5);
    
    public String    backFile = "";    
    public boolean  backMerge = false;
    public int      maxEvents = 10_000_000;
    public int[]   tagCounter = new int[]{0,0,0,0,0,0,0,0};
    
    public TrackExtractor(){
        
        selector6 = new TrackSelector() {
            //public int sector = 1;
            @Override
            public boolean select(Track trk){
                //if(trk.sector!=sector) return false;
                if(trk.chi2>10) return false;
                if(trk.vector.mag()<0.5||trk.vector.mag()>10.0) return false;
                if(trk.vertex.z()<-15||trk.vertex.z()>5) return false;
                if(trk.clusterCount()<5) return false;
                return true;
            }
        };
    }
    
    public H2F getTrack(Track t, Bank b){
        H2F h = new H2F("track",112,0.5,112.5,36,0.5,36.5);
        for(int r = 0; r < b.getRows(); r++){
            int sector = b.getInt("sector", r);
            if(sector==1){
                int cid = b.getInt("clusterID", r);
                int flag = 0;
                for(int i = 0; i < t.clusters.length;i++)
                    if(t.clusters[i]==cid) flag++;
                if(flag>0){
                    int  supl = b.getInt("superlayer", r);
                    int layer = b.getInt("layer", r);
                    int wire  = b.getInt("wire", r);
                    h.fill(wire, (supl-1)*6+layer);
                }
            }
        }
        return h;
    }
    
    public void fillTrack(H2F h, Track t, Bank b, int sec){
        for(int r = 0; r < b.getRows(); r++){
            int sector = b.getInt("sector", r);
            if(sector==sec){
                int cid = b.getInt("clusterID", r);
                int flag = 0;
                for(int i = 0; i < t.clusters.length;i++)
                    if(t.clusters[i]==cid) flag++;
                if(flag>0){
                    int  supl = b.getInt("superlayer", r);
                    int layer = b.getInt("layer", r);
                    int wire  = b.getInt("wire", r);
                    h.fill(wire, (supl-1)*6+layer);
                }
            }
        }
    }
    
    public H2F getHits(Bank b){
        H2F h = new H2F("hits",112,0.5,112.5,36,0.5,36.5);
        for(int r = 0; r < b.getRows(); r++){
            int sector = b.getInt("sector", r);
            if(sector==1){
                    int layer = b.getInt("layer", r);
                    int wire  = b.getInt("component", r);
                    h.setBinContent(wire-1, layer-1,1.0);
            }
        }
        return h;
    }
    
    public Bank getDC(Bank b, H2F h){
        List<Integer> index = new ArrayList<>();
        List<Integer> track = new ArrayList<>();        
        for(int r = 0; r < b.getRows(); r++){
            int sector = b.getInt("sector", r);
            int  layer = b.getInt("layer", r);
            int   wire = b.getInt("component", r);
            if(sector==1){
                if(h.getBinContent(wire-1, layer-1)>0){
                  track.add(r);
                } else {
                    index.add(r);
                }
            }
        }
        List<Integer> indexAll = new ArrayList<>();
        indexAll.addAll(index); indexAll.addAll(track);
        //System.out.println(" hits = " + index.size() + "  track = " + track.size());
        Bank dc = b.reduce(indexAll);
        return dc;
    }
    
    public List<Node> createHodes(H2F track, H3F dc, int sector){
        H2F slice = dc.sliceZ(sector);
        slice.add(track);
        slice.discretize(0.5);
        Node n1 = new Node(340,1,track.getContentArrayFloat());
        Node n2 = new Node(340,2,slice.getContentArrayFloat());        
        slice.sub(track);
        Node n3 = new Node(340,3,slice.getContentArrayFloat());
        return Arrays.asList(n1,n2,n3);
    }
    
    public int count(H2F h, double th){
        float[] a = h.getContentArrayFloat();
        int c = 0;
        for(int i = 0; i < a.length; i++)
            if(a[i]>th) c++;
        return c;
    }
    
    public int getTag(List<Track> tr){
        if(tr.size()==1&&tr.get(0).clusterCount()==6) return 1;
        if(tr.size()==1&&tr.get(0).clusterCount()==5) return 2;
        if(tr.size()==2){
            if(tr.get(0).clusterCount()==6&&tr.get(1).clusterCount()==6) return 3;
            if(tr.get(0).clusterCount()==6&&tr.get(1).clusterCount()==5) return 4;
            if(tr.get(0).clusterCount()==5&&tr.get(1).clusterCount()==6) return 4;
            if(tr.get(0).clusterCount()==5&&tr.get(1).clusterCount()==5) return 5;
        }
        return 0;
    }
    
    public void process(String file){
        
        HipoReader r = new HipoReader(file);
        this.init(r);
        
        HipoReader rb = new HipoReader();
        
        if(this.backMerge==true){
            rb.open(this.backFile);
            this.dcTDCbkg = rb.getBank("DC::tdc");
        }
        
        HipoWriter w = new HipoWriter();
        w.open(this.outputFile);
        
        Event e = new Event();
        Event eout = new Event();
        Event eb = new Event();
        
        int counter = 1;
        
        while(r.hasNext()){
                        
            r.nextEvent(e);
            
            e.read(dcTDC);e.read(dcCLT);e.read(dcTRK); e.read(dcHIT);            
            List<Track>  tracks = Track.read(dcTRK, dcCLT);
            
            H3F dc = DetectorDataUtils.getDetectorMap(36, 112, dcTDC);
            List<Track>  selected = Track.filter(tracks, selector6);
            
            //System.out.println("-----");
            for(int sector = 1; sector <= 6; sector++){
                
                final int local = sector;
                List<Track> sectorTracks = selected.stream()
                        .filter(t -> t.sector == local)
                        .collect(Collectors.toList());
                //sectorTracks.forEach(System.out::println);
                
                if(sectorTracks.size()>0){
                    
                    counter++;
                    
                    int tag = this.getTag(sectorTracks);
                    
                    if(sectorTracks.size()<tagCounter.length){
                        tagCounter[tag]++;
                    }

                    H2F htrk = new H2F("track",112,0.5,112.5,36,0.5,36.5);
                    //System.out.println("---  tag = " + tag );
                    //sectorTracks.forEach(System.out::println);
                    //for(Track t : sectorTracks) 
                    //    System.out.println( t.clusterCount() + " >>>> " + t);
                    for(int i = 0; i < sectorTracks.size(); i++){
                         this.fillTrack(htrk,sectorTracks.get(i), dcHIT,sector);
                      //   System.out.println("count " + i + " = " + count(htrk,0.5));
                    }

                    //H2F hits = dc.sliceZ(sector-1);
                    
                    if(this.backMerge==true){
                        if(rb.hasNext()==false) rb.rewind();
                        rb.nextEvent(eb);
                        eb.read(dcTDCbkg);
                        H3F dch3 = DetectorDataUtils.getDetectorMap(36, 112, dcTDCbkg);
                        
                        eout.reset();
                        eout.setEventTag(tag);
                        List<Node> nodes = this.createHodes(htrk, dch3, sector);
                        for(Node n : nodes) eout.write(n);
                        if(tagCounter[eout.getEventTag()]<this.maxEvents){
                            w.add(eout);
                        }
                    } else {
                        eout.reset();
                        eout.setEventTag(tag);
                        List<Node> nodes = this.createHodes(htrk, dc, sector);
                        for(Node n : nodes) eout.write(n);
                        if(tagCounter[eout.getEventTag()]<this.maxEvents){
                            w.add(eout);
                        }
                    }                                       
                }
            }
            
          
           //if(counter>this.maxEvents) break;
        }
        w.close();
    }
    
    public void init(HipoReader r){
        dcTDC = r.getBank("DC::tdc");
        dcTRK = r.getBank(banks[0]);
        dcCLT = r.getBank(banks[1]);
        dcHIT = r.getBank("TimeBasedTrkg::TBHits");
    }
    
    public static void main(String[] args){
        
        OptionParser opt = new OptionParser();
        
        opt.addOption("-b", "t.h5","background file name")
                .addOption("-m", "false","use background for merging")
                .addOption("-n", "1000000","number of events")
                .addOption("-o", "denoiser_all.h5", "output file name");
        
        opt.parse(args);
        
        String file = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        file = opt.getInputList().get(0);
        
        TrackExtractor te = new TrackExtractor();
        te.backMerge = opt.getOption("-m").stringValue().equals("true");
        te.backFile  = opt.getOption("-b").stringValue();
        te.maxEvents = opt.getOption("-n").intValue();
        te.outputFile = opt.getOption("-o").stringValue();
        te.process(file);
    }
}
