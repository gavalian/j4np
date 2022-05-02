/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.common;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.io.OptionStore;
import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;


/**
 *
 * @author gavalian
 */
public class TrackDataExtractor {
    
    public static List<Node> getNode(Track trk){
        
        List<Node> nodeList = new ArrayList<Node>();
        short[] desc = new short[]{(short) trk.sector,(short) trk.charge};
        
        nodeList.add(new Node(1001,1,desc));
        nodeList.add(new Node(1001,2,new float[]{ (float) trk.chi2}));
        nodeList.add(new Node(1001,3,new short[]{
            (short) trk.clusters[0],
            (short) trk.clusters[1],
            (short) trk.clusters[2],
            (short) trk.clusters[3],
            (short) trk.clusters[4],
            (short) trk.clusters[5]
        }));
        
        nodeList.add(new Node(1001,4,new float[] {
            (float) trk.means[0],
            (float) trk.means[1],
            (float) trk.means[2],
            (float) trk.means[3],
            (float) trk.means[4],
            (float) trk.means[5]
        }));
        nodeList.add(new Node(1001,5,new float[]{
            (float) trk.slopes[0],
            (float) trk.slopes[1],
            (float) trk.slopes[2],
            (float) trk.slopes[3],
            (float) trk.slopes[4],
            (float) trk.slopes[5]
        }));
        
        nodeList.add(new Node(1001,6, new float[]{ (float) trk.vector.x(), (float)trk.vector.y(), (float)trk.vector.z()}));
        nodeList.add(new Node(1001,7, new float[]{ (float)trk.vertex.x(),(float) trk.vertex.y(),(float) trk.vertex.z()}));
                
        
        return nodeList;
    }
    
    
    public void export(){
        //TextFileWriter writer 
    }
    
    public void readStore(){
        
    }
    
    public static int getTrackBin(Track t){
        double p = t.vector.mag();
        int bin = -1;
        if(p>0.0&&p<10.0){
            bin = (int) (p/0.5);
        }
        return bin;
    }
    
   /* public static void read(ClusterStore store, Bank bank, int sector){
        store.reset();
        //this.resolvedTracks.reset();
        int nrows = bank.getRows();
        
        for(int i = 0; i < nrows; i++){
                int sec = bank.getInt("sector", i);
                int id     = bank.getInt("id", i);
                int superlayer = bank.getInt("superlayer", i);
                double wire = bank.getFloat("avgWire", i);
                if(sector==sec){
                    store.add(superlayer-1, id, wire);
                }
        }
    }
    */
    public static void extract(String output, List<String> inputs, int max){
        Event event = new Event();
        Event outEvent = new Event();
        HipoWriter writer = new HipoWriter();
        writer.open(output);
        
        H1F haxis = new H1F("haxis",40,0.0,1.0);
        
        for(int i = 0; i < inputs.size(); i++){
            HipoReader chain = new HipoReader(inputs.get(i));
            Bank tBank = chain.getBank("TimeBasedTrkg::TBTracks");
            Bank cBank = chain.getBank("TimeBasedTrkg::TBClusters");
            Bank hBank = chain.getBank("HitBasedTrkg::HBClusters");
            
            while(chain.hasNext()){  
            
                chain.nextEvent(event);
                event.read(cBank);
                event.read(tBank);
                event.read(hBank);
                List<Track>  trkList = Track.read(tBank,cBank);
                for(Track t : trkList){
                    if(t.complete()==true&&t.chi2<10.0&&t.vertex.z()>-25.0&&t.vertex.z()<35.0){                        
                        outEvent.reset();                        
                        List<Node> nodes = TrackDataExtractor.getNode(t);
                        int          bin = TrackDataExtractor.getTrackBin(t);                 
                        if(bin>=0&&bin<20){
                            if(haxis.getBinContent(bin)<max){
                                int chargeBin = bin+1;
                                haxis.incrementBinContent(bin);
                                if(t.charge<0) chargeBin += 20;
                                outEvent.setEventTag(chargeBin);
                                for(Node n : nodes) outEvent.write(n);
                                writer.addEvent(outEvent,chargeBin);
                            }
                        }
                    }                    
                }            
            }
        }
        writer.close();
    }
    
    public static void main(String[] args){
                
        OptionStore storeParser = new OptionStore("run-extract.sh");
        storeParser.addCommand("-extract", "extract training file from cooked data files");
        storeParser.getOptionParser("-extract").addRequired("-o","Output file name");
        storeParser.getOptionParser("-extract").addOption("-max", "32000", "maximum number of tracks per momentum bin");
        
        storeParser.parse(args);
        
        if(storeParser.getCommand().compareTo("-extract")==0){
            String output = storeParser.getOptionParser("-extract").getOption("-o").stringValue();        
            List<String> filesList = storeParser.getOptionParser("-extract").getInputList();//new ArrayList<>();        
        }
    }
}
