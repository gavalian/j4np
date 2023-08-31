/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.networks;

import j4np.hipo5.data.CompositeNode;
import j4np.neural.data.Tracks;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TrackConstructor {
    
    Tracks   trackList = new Tracks();    
    public Combinatorics[]   sectors = new Combinatorics[6]; 
    
    public TrackConstructor(){
        for(int s = 0; s < 6; s++) sectors[s] = new Combinatorics();
    }
    
    public void reset(){for(Combinatorics c : sectors) c.reset();}
    
    public void construct(CompositeNode dcc){
        int nrows = dcc.getRows();
        for(int k = 0; k < nrows; k++){
            //int sector =  
        }
    }
          
    public void add(int sector, int superlayer, int cluster, double mean){
        sectors[sector-1].add(superlayer, cluster, mean);
    }
    
    
    public void show(){
        for(int i = 0; i < sectors.length; i++) {
            System.out.println("::: SECTOR " + i);
            sectors[i].show();
        };
    }
    
    public static interface CombinationCuts {
        public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6);
    }
    
    public static class Combinatorics {
        
        CompositeNode[] superLayers = new CompositeNode[6];
        protected int   meansIndex = 17;
        protected int clusterIndex = 11;
        
        public Combinatorics(){
            for(int i = 0; i < 6; i++) { superLayers[i] = new CompositeNode(1,2,"sff",128); superLayers[i].setRows(0);}
        }
        
        public void reset(){
            for(int i = 0; i < 6; i++) { superLayers[i].setRows(0);}
        }
        
        public void add(int slayer, int cid, double mean){
            int rows = superLayers[slayer-1].getRows();
            superLayers[slayer-1].putShort(0, rows, (short) cid);
            superLayers[slayer-1].putFloat(1, rows, (float) mean);
            superLayers[slayer-1].putFloat(2, rows, (float) 0.0);
            superLayers[slayer-1].setRows(rows+1);
        }
        
        public String getLayerString(int layer){
            StringBuilder str = new StringBuilder();
            int nrows = superLayers[layer].getRows();
            for(int i = 0; i < nrows; i++){
                str.append(String.format(" %4d : %7.4f", 
                        superLayers[layer].getInt(0, i),
                        superLayers[layer].getDouble(1, i)));
            }
            return str.toString();
        }
        
        public void show(){
            
            for(int i = 0; i < superLayers.length; i++){
                
                //System.out.printf(" %d -> %d\n",i,superLayers[i].getRows());
                System.out.println(this.getLayerString(i));
            }
        }
        
        public void create(Tracks list, int sector){
            create( list,  sector,null);
        }
        
        public void create(Tracks list, int sector, CombinationCuts cuts){
            list.dataNode().setRows(0);
            int row = 0;
            boolean writeTrack = false;
            for(int l1 = 0; l1 < superLayers[0].getRows(); l1++){
                for(int l2 = 0; l2 < superLayers[1].getRows(); l2++){
                    for(int l3 = 0; l3 < superLayers[2].getRows(); l3++){
                        for(int l4 = 0; l4 < superLayers[3].getRows(); l4++){
                            for(int l5 = 0; l5 < superLayers[4].getRows(); l5++){
                                for(int l6 = 0; l6 < superLayers[5].getRows(); l6++){
                                    //double[] means = new double[6];
                                    
                                    double m1 = superLayers[0].getDouble(1, l1);
                                    double m2 = superLayers[1].getDouble(1, l2);
                                    double m3 = superLayers[2].getDouble(1, l3);
                                    double m4 = superLayers[3].getDouble(1, l4);
                                    double m5 = superLayers[4].getDouble(1, l5);
                                    double m6 = superLayers[5].getDouble(1, l6);
                                    writeTrack = true;
                                    if(cuts!=null){
                                        //writeTrack = true;                                    
                                        //System.out.println("********** INSIDE THE THING " + cuts.validate(m1, m2, m3, m4, m5, m6));
                                        if(cuts.validate(m1, m2, m3, m4, m5, m6)==false) writeTrack = false;
                                    }                                   
                                    
                                    if(writeTrack==true){
                                        list.dataNode().putFloat(meansIndex  , row, (float) superLayers[0].getDouble(1, l1));
                                        list.dataNode().putFloat(meansIndex+1, row, (float) superLayers[1].getDouble(1, l2));
                                        list.dataNode().putFloat(meansIndex+2, row, (float) superLayers[2].getDouble(1, l3));
                                        list.dataNode().putFloat(meansIndex+3, row, (float) superLayers[3].getDouble(1, l4));
                                        list.dataNode().putFloat(meansIndex+4, row, (float) superLayers[4].getDouble(1, l5));
                                        list.dataNode().putFloat(meansIndex+5, row, (float) superLayers[5].getDouble(1, l6));
                                        
                                        list.dataNode().putInt(clusterIndex  , row,  superLayers[0].getInt(0, l1));
                                        list.dataNode().putInt(clusterIndex+1, row,  superLayers[1].getInt(0, l2));
                                        list.dataNode().putInt(clusterIndex+2, row,  superLayers[2].getInt(0, l3));
                                        list.dataNode().putInt(clusterIndex+3, row,  superLayers[3].getInt(0, l4));
                                        list.dataNode().putInt(clusterIndex+4, row,  superLayers[4].getInt(0, l5));
                                        list.dataNode().putInt(clusterIndex+5, row,  superLayers[5].getInt(0, l6));
                                        
                                        list.dataNode().putShort( 0, row, (short) 0);
                                        list.dataNode().putFloat( 1,row, 0.0f);
                                        list.dataNode().putShort( 2,row, (short) sector);
                                        list.dataNode().putShort( 3, row, (short) 0);
                                        row++;
                                        list.dataNode().setRows(row);
                                    }
                                    //data.add(means);
                                }
                            }
                        }
                    }
                }
            }            
        }
    }
    
    
    
    public static void main(String[] args){
        
        TrackConstructor tc = new TrackConstructor();
        tc.add(1, 1, 1,  0.5);
        tc.add(1, 1, 7,  0.2);
        tc.add(1, 2, 3,  0.3);
        tc.add(1, 3, 5,  0.5);
        tc.add(1, 3,15,  0.2);
        tc.add(1, 4, 7,  0.5);
        tc.add(1, 5, 8,  0.3);
        tc.add(1, 6, 9,  0.5);
        tc.add(1, 6, 11, 0.2);
        
        tc.show();
        
        Tracks tkl = new Tracks();
        tc.sectors[0].create(tkl,1);
        
        tkl.show();
    }
    
}
