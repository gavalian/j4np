/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class TrackReader {
    
    SchemaFactory sfac = new SchemaFactory();
    Random        rand = new Random();
    
    public TrackReader(){
        sfac.readFile("etc/neuralnetwork.json");
        sfac.show();
    }
    /**
     * Data format in the hipo file
     * node 1001,1 (short) sector, charge
     * node 1001,2 (float) chi2
     * node 1001,3 (short) cluster ids
     * node 1001,4 (float) cluster means
     * node 1001,5 (float) cluster slopes
     * node 1001,6 (float) particle momentum
     * node 1001,7 (float) particle vertex
     */
    
    public static void event2track(Event e, Tracks list){
        Node node1 = e.read(1001, 1);
        Node node2 = e.read(1001, 2);
        Node node3 = e.read(1001, 3);
        Node node4 = e.read(1001, 4);
        Node node6 = e.read(1001, 6);
        Node node7 = e.read(1001, 7);
        
        int row = 0;//list.bank.getRows();
        list.bank.putShort(0, row, (short) 0);
        list.bank.putShort(2, row, (short) node1.getShort(0));
        list.bank.putShort(3, row, (short) node1.getShort(1));
        for(int k = 0; k < 3; k++) list.bank.putFloat( k+5, row, node6.getFloat(k));
        for(int k = 0; k < 3; k++) list.bank.putFloat( k+8, row, node7.getFloat(k));
        for(int k = 0; k < 6; k++) list.bank.putFloat(k+17, row, (float) (node4.getFloat(k)/112.0));
        list.bank.setRows(row+1);
    }
    
    public static void reco2tracksForSector(Tracks tl, Bank tracks, Bank clusters, int sector){
         tl.dataNode().setRows(0);        
         Map<Integer,Integer> map = clusters.getMap("id");
        //List<Track> list = new ArrayList<>();
        int nrows = tracks.getRows();
        int trows = 0;
        for(int i = 0; i < nrows; i++){
            //Track trk = new Track();
            int trksector = tracks.getInt("sector", i);
            tl.dataNode().putFloat(5, trows,tracks.getFloat("p0_x",  i));
            tl.dataNode().putFloat(6, trows,tracks.getFloat("p0_y",  i));
            tl.dataNode().putFloat(7, trows,tracks.getFloat("p0_z",  i));                    
            tl.dataNode().putFloat(8, trows,tracks.getFloat("Vtx0_x",  i));
            tl.dataNode().putFloat(9, trows,tracks.getFloat("Vtx0_y",  i));
            tl.dataNode().putFloat(10, trows,tracks.getFloat("Vtx0_z",  i));
            tl.dataNode().putShort(3,trows, (short) tracks.getInt("q", i));
            tl.dataNode().putShort(2,trows, (short) trksector);
            
            int ndf = tracks.getInt("ndf", i);
            float chi2 =  (float) (tracks.getFloat("chi2", i)/ndf);
            tl.dataNode().putFloat(4,trows, chi2);
            
            int[] ids = tracks.getIntArray(6, "Cluster1_ID", i);
            boolean writeTrack = true;
            for(int c = 0; c < 6; c++){
                tl.dataNode().putInt(11+c, trows, ids[c]);
                if(ids[c]>=0) {
                    int index = map.get(ids[c]);
                    tl.dataNode().putFloat(17+c, trows, clusters.getFloat("avgWire", index));
                    //trk.slopes[c] = clusters.getFloat("fitSlope", index);
                } else { writeTrack = false;}
            }
            if(writeTrack==true&&chi2<10&&trksector==sector){
                trows++;
                tl.dataNode().setRows(trows);
            }
        }
    }
    public static void reco2tracks(Tracks tl, Bank tracks, Bank clusters){
        tl.dataNode().setRows(0);        
         Map<Integer,Integer> map = clusters.getMap("id");
        //List<Track> list = new ArrayList<>();
        int nrows = tracks.getRows();
        int trows = 0;
        for(int i = 0; i < nrows; i++){
            //Track trk = new Track();
            int sector = tracks.getInt("sector", i);
            tl.dataNode().putFloat(5, trows,tracks.getFloat("p0_x",  i));
            tl.dataNode().putFloat(6, trows,tracks.getFloat("p0_y",  i));
            tl.dataNode().putFloat(7, trows,tracks.getFloat("p0_z",  i));                    
            tl.dataNode().putFloat(8, trows,tracks.getFloat("Vtx0_x",  i));
            tl.dataNode().putFloat(9, trows,tracks.getFloat("Vtx0_y",  i));
            tl.dataNode().putFloat(10, trows,tracks.getFloat("Vtx0_z",  i));
            tl.dataNode().putShort(3,trows, (short) tracks.getInt("q", i));
            tl.dataNode().putShort(2,trows, (short) sector);
            
            int ndf = tracks.getInt("ndf", i);
            float chi2 =  (float) (tracks.getFloat("chi2", i)/ndf);
            tl.dataNode().putFloat(4,trows, chi2);
            
            int[] ids = tracks.getIntArray(6, "Cluster1_ID", i);
            boolean writeTrack = true;
            for(int c = 0; c < 6; c++){
                tl.dataNode().putInt(11+c, trows, ids[c]);
                if(ids[c]>=0) {
                    int index = map.get(ids[c]);
                    tl.dataNode().putFloat(17+c, trows, clusters.getFloat("avgWire", index));
                    //trk.slopes[c] = clusters.getFloat("fitSlope", index);
                } else { writeTrack = false;}
            }
            if(writeTrack==true&&chi2<10){
                trows++;
                tl.dataNode().setRows(trows);
            }
        }
    }
    
    public static Tracks read(String file, long tag, int max){
        HipoReader r = new HipoReader();
        Tracks list = new Tracks();
        r.setTags(tag);
        r.open(file);
        Event e = new Event();
        int counter = 0;
        
        while(r.hasNext()==true&&counter<max){
            counter++;
            r.nextEvent(e);
            Node node1 = e.read(1001, 1);
            Node node2 = e.read(1001, 2);
            Node node3 = e.read(1001, 3);
            Node node4 = e.read(1001, 4);
            Node node6 = e.read(1001, 6);
            Node node7 = e.read(1001, 7);
            
            int row = list.bank.getRows();
            list.bank.putShort(0, row, (short) 0);
            list.bank.putShort(2, row, (short) node1.getShort(0));
            list.bank.putShort(3, row, (short) node1.getShort(1));
            for(int k = 0; k < 3; k++) list.bank.putFloat( k+5, row, node6.getFloat(k));
            for(int k = 0; k < 3; k++) list.bank.putFloat( k+8, row, node7.getFloat(k));
            for(int k = 0; k < 6; k++) list.bank.putFloat(k+17, row, (float) (node4.getFloat(k)/112.0));
            list.bank.setRows(row+1);
        }
        return list;
    }
    
    public static Tracks processEvent(Event e){
        Tracks list = new Tracks();
        Node node1 = e.read(1001, 1);
        Node node2 = e.read(1001, 2);
        Node node3 = e.read(1001, 3);
        Node node4 = e.read(1001, 4);
        Node node6 = e.read(1001, 6);
        Node node7 = e.read(1001, 7);
        
        int row = list.bank.getRows();
        list.bank.putShort(0, row, (short) 0);
        list.bank.putShort(2, row, (short) node1.getShort(0));
        list.bank.putShort(3, row, (short) node1.getShort(1));
        for(int k = 0; k < 3; k++) list.bank.putFloat( k+5, row, node6.getFloat(k));
        for(int k = 0; k < 3; k++) list.bank.putFloat( k+8, row, node7.getFloat(k));
        for(int k = 0; k < 6; k++) list.bank.putFloat(k+17, row, (float) (node4.getFloat(k)/112.0));
        list.bank.setRows(row+1);
        return list;
    }

    public static Bank getBank(Tracks l1, Tracks l2, SchemaFactory sf){
        Bank b = new Bank(sf.getSchema("nnet::clusters"),12);
        //l1.show();
        for(int i =0; i < 6; i++){
            b.putShort("id", i, (short) (i+1));
            b.putShort("sector", i, (short) 1);
            b.putShort("superlayer", i, (short) (i+1));
            b.putFloat("mean", i, (float) l1.dataNode().getDouble(17+i, 0) );
            //System.out.printlf("%d/%d\n",i,l1.dataNode().getDouble(17+i, i));
        }
        
        for(int i =0; i < 6; i++){
            b.putShort("id", i+6, (short) (i+7));
            b.putShort("sector", i+6, (short) 1);
            b.putShort("superlayer", i+6, (short) (i+1));
            b.putFloat("mean", i+6, (float) l2.dataNode().getDouble(17+i, 0) );
        }
        
        return b;
    } 
    
    public Bank readFromFile(String file, int tag1, int tag2){
        //long tag1 = rand.nextInt(40)+1;
        //long tag2 = rand.nextInt(40)+1;
        
        Event e = new Event();
        
        HipoReader r = new HipoReader();
        r.setTags(tag1);
        r.setDebugMode(0);
        r.open(file);
        int  eve1 = rand.nextInt(r.getEventCount());
        r.getEvent(e, eve1);
        
        Tracks list1 = TrackReader.processEvent(e);
        
        r = new HipoReader();
        r.setTags(tag2);
        r.setDebugMode(0);
        r.open(file);
        int  eve2 = rand.nextInt(r.getEventCount());
        r.getEvent(e, eve2);
        Tracks list2 = TrackReader.processEvent(e);
        
        //System.out.printf("### reading tag = %5d, ev = %5d, tag = %5d, ev = %5d\n",
        //        tag1,eve1,tag2,eve2);
        return TrackReader.getBank(list1, list2, sfac);
    }
}
