/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.hipo5.data.CompositeNode;
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
      
    public void add(int sector, int superlayer, int cluster, double mean, double slope){
        sectors[sector-1].add(superlayer, cluster, mean, slope);
    }
    
    public void show(){
        for(int i = 0; i < sectors.length; i++) {
            System.out.println("::: SECTOR " + i);
            sectors[i].show();
        }
    }
    
    public static boolean contains(int[] clusters, int id){
        for(int i = 0; i < clusters.length; i++)
            if(clusters[i]==id) return true;
        return false;
    }
    
    public static void filter(Tracks tracks, int[] clusters){
        int nrows = tracks.size();
        int[] ids = new int[6];
        boolean keep = true;
        
        for( int i = 0; i < nrows; i++){
            tracks.getClusters(ids, i);
            keep = true;
            for(int j = 0; j < ids.length; j++){
                if(TrackConstructor.contains(clusters, ids[j])==true) keep = false;
            }
            if(keep==false) tracks.setStatus(i, -1);
        }
    }
    
    public static interface CombinationCuts {
        public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6);
    }
    
    public static class Combinatorics {
        
        CompositeNode[] superLayers = new CompositeNode[6];
        protected int   meansIndex = 17;
        protected int clusterIndex = 11;
        protected int  slopesIndex = 23;
        
        private final int[][] patterns = new int[][]{
            {1,2,3,4,5},
            {0,2,3,4,5},
            {0,1,3,4,5},
            {0,1,2,4,5},
            {0,1,2,3,5},
            {0,1,2,3,4}
        };
        
        private final int[] missing = {0,1,2,3,4,5};
        
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
        
        public void add(int slayer, int cid, double mean, double slope){
            int rows = superLayers[slayer-1].getRows();
            superLayers[slayer-1].putShort(0, rows, (short) cid);
            superLayers[slayer-1].putFloat(1, rows, (float) mean);
            superLayers[slayer-1].putFloat(2, rows, (float) slope);
            superLayers[slayer-1].setRows(rows+1);
        }
        
        public String getLayerString(int layer){
            StringBuilder str = new StringBuilder();
            int nrows = superLayers[layer].getRows();
            for(int i = 0; i < nrows; i++){
                str.append(String.format(" %4d : %7.4f %7.4f", 
                        superLayers[layer].getInt(0, i),
                        superLayers[layer].getDouble(1, i),
                        superLayers[layer].getDouble(2, i)
                        ));
            }
            return str.toString();
        }
        
        public int getCount(){
            int count = 0;
            for(int i = 0; i < this.superLayers.length; i++) count += this.superLayers[i].getRows();
            return count;
        }
        
        public void show(){
            
            for(int i = 0; i < superLayers.length; i++){                
                //System.out.printf(" %d -> %d\n",i,superLayers[i].getRows());
                System.out.println(this.getLayerString(i));
            }
        }
        
        public void show(int sector){
            System.out.println(this.getLayerString(sector));            
        }
        
        public void create(Tracks list, int sector){            
            create( list,  sector,null);
        } 
        
        public void create(Tracks list, int sector, CombinationCuts cuts){
            list.dataNode().setRows(0);
            int row = 0;
            int capacity = list.dataNode().getCapacity();
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
                                        //System.out.println("slope = " + superLayers[0].getDouble(2, l1));
                                        list.dataNode().putFloat(slopesIndex  , row, (float) superLayers[0].getDouble(2, l1));
                                        list.dataNode().putFloat(slopesIndex+1, row, (float) superLayers[1].getDouble(2, l2));
                                        list.dataNode().putFloat(slopesIndex+2, row, (float) superLayers[2].getDouble(2, l3));
                                        list.dataNode().putFloat(slopesIndex+3, row, (float) superLayers[3].getDouble(2, l4));
                                        list.dataNode().putFloat(slopesIndex+4, row, (float) superLayers[4].getDouble(2, l5));
                                        list.dataNode().putFloat(slopesIndex+5, row, (float) superLayers[5].getDouble(2, l6));
                                        
                                        list.dataNode().putInt(clusterIndex  , row,  superLayers[0].getInt(0, l1));
                                        list.dataNode().putInt(clusterIndex+1, row,  superLayers[1].getInt(0, l2));
                                        list.dataNode().putInt(clusterIndex+2, row,  superLayers[2].getInt(0, l3));
                                        list.dataNode().putInt(clusterIndex+3, row,  superLayers[3].getInt(0, l4));
                                        list.dataNode().putInt(clusterIndex+4, row,  superLayers[4].getInt(0, l5));
                                        list.dataNode().putInt(clusterIndex+5, row,  superLayers[5].getInt(0, l6));
                                        
                                        list.dataNode().putShort( 0, row, (short) 0);
                                        list.dataNode().putFloat( 1, row, 0.0f);
                                        list.dataNode().putShort( 2, row, (short) sector);
                                        list.dataNode().putShort( 3, row, (short) 0);
                                        row++;
                                        if(row>=list.dataNode().getMaxRows()-1) return;
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
        
        public void create5(Tracks list, int sector, CombinationCuts cuts){
            list.dataNode().setRows(0);
            int row = 0;
            boolean writeTrack = false;
            
            double[] means = new double[6];
            double[] slope = new double[6];
            
            int[]      ids = new int[6];
            
            for(int i = 0; i < 6; i++)
                for(int l1 = 0; l1 < superLayers[patterns[i][0]].getRows(); l1++){
                    for(int l2 = 0; l2 < superLayers[patterns[i][1]].getRows(); l2++){
                        for(int l3 = 0; l3 < superLayers[patterns[i][2]].getRows(); l3++){
                            for(int l4 = 0; l4 < superLayers[patterns[i][3]].getRows(); l4++){
                                for(int l5 = 0; l5 < superLayers[patterns[i][4]].getRows(); l5++){
                                    //for(int l6 = 0; l6 < superLayers[5].getRows(); l6++){
                                    //double[] means = new double[6];
                                    means[patterns[i][0]] = superLayers[patterns[i][0]].getDouble(1, l1);
                                    means[patterns[i][1]] = superLayers[patterns[i][1]].getDouble(1, l2);
                                    means[patterns[i][2]] = superLayers[patterns[i][2]].getDouble(1, l3);
                                    means[patterns[i][3]] = superLayers[patterns[i][3]].getDouble(1, l4);
                                    means[patterns[i][4]] = superLayers[patterns[i][4]].getDouble(1, l5);
                                    
                                    
                                    slope[patterns[i][0]] = superLayers[patterns[i][0]].getDouble(2, l1);
                                    slope[patterns[i][1]] = superLayers[patterns[i][1]].getDouble(2, l2);
                                    slope[patterns[i][2]] = superLayers[patterns[i][2]].getDouble(2, l3);
                                    slope[patterns[i][3]] = superLayers[patterns[i][3]].getDouble(2, l4);
                                    slope[patterns[i][4]] = superLayers[patterns[i][4]].getDouble(2, l5);
                                    
                                    ids[patterns[i][0]] = superLayers[patterns[i][0]].getInt(0, l1);
                                    ids[patterns[i][1]] = superLayers[patterns[i][1]].getInt(0, l2);
                                    ids[patterns[i][2]] = superLayers[patterns[i][2]].getInt(0, l3);
                                    ids[patterns[i][3]] = superLayers[patterns[i][3]].getInt(0, l4);
                                    ids[patterns[i][4]] = superLayers[patterns[i][4]].getInt(0, l5);
                                    
                                    means[missing[i]] = 0.0;
                                    slope[missing[i]] = 0.0;
                                    ids[missing[i]] = -1;
                                    
                                    /*double m1 = superLayers[0].getDouble(1, l1);
                                    double m2 = superLayers[1].getDouble(1, l2);
                                    double m3 = superLayers[2].getDouble(1, l3);
                                    double m4 = superLayers[3].getDouble(1, l4);
                                    double m5 = superLayers[4].getDouble(1, l5);
                                    double m6 = superLayers[5].getDouble(1, l6);*/
                                    writeTrack = true;
                                    if(cuts!=null){
                                        //writeTrack = true;                                    
                                        //System.out.println("********** INSIDE THE THING " + cuts.validate(m1, m2, m3, m4, m5, m6));
                                        if(cuts.validate(means[0],means[1],means[2], means[3],means[4],means[5])==false) writeTrack = false;
                                    }                           
                                    
                                    if(writeTrack==true){
                                        for(int kk=0;kk<6;kk++) list.dataNode().putFloat( meansIndex + kk , row, (float) means[kk]);
                                        for(int kk=0;kk<6;kk++) list.dataNode().putFloat(slopesIndex + kk , row, (float) slope[kk]);
                                        for(int kk=0;kk<6;kk++) list.dataNode().putInt( clusterIndex + kk , row,  ids[kk]);
                                        list.dataNode().putShort( 0, row, (short) 0);
                                        list.dataNode().putFloat( 1, row, 0.0f);
                                        list.dataNode().putShort( 2, row, (short) sector);
                                        list.dataNode().putShort( 3, row, (short) 0);
                                        row++;
                                        list.dataNode().setRows(row);
                                        if(row>=list.dataNode().getMaxRows()-5) return;
                                    }
                                    //data.add(means);
                                }
                            }
                        }
                    }
                }
        }
    }    
    
    public static void main(String[] args){
        /*
        TrackConstructor tc = new TrackConstructor();
        
        tc.add(1, 6, 6,  0.35,0.45);        
        tc.add(1, 1, 1,  0.10,0.20);
        tc.add(1, 2, 2,  0.15,0.25);
        tc.add(1, 3, 3,  0.20,0.30);
        
        tc.add(1, 4, 4,  0.25,0.35);
        tc.add(1, 5, 5,  0.30,0.40);
        

        //tc.add(1, 6, 11, 0.2);
        
        tc.show();
        
        Tracks tkl = new Tracks();
        tc.sectors[0].create5(tkl,1,null);
        tkl.show();        
        */
        Combinatorics c = new Combinatorics();
        
        c.add(1   ,       1 , 66.9048 , 66.2619);
        c.add(1,  2   , 73.2000 , 73.2000);
        c.add(  2,3        ,  70.8571,  70.6429);
        c.add(2,4 ,  75.3000 , 75.3000);        
        c.add(3,5, 57.8571 , 56.6429);
        
        c.add(3, 6 , 70.7619 , 71.4048);
        c.add(4,7 ,  59.0952,  57.7381);
        c.add(4, 8 ,  72.9730 , 73.3784);
        
        c.add(5,9  ,  37.8750 , 35.8438);
        c.add(5, 10 ,  72.2381,  73.5952);
        c.add(6,11 , 38.0000 , 35.5000);
       c.add(6, 12 ,  74.7432 , 75.5541);
        //TrackConstructor.filter(tkl, new int[]{9});
        //System.out.println(" AFTER FILTER");
        //tkl.show();
    }
    
}
