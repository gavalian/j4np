/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import j4np.hipo5.data.Bank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class DataClusters {
    Map<Integer, List<Cluster>> map = new HashMap<>();
    
    public static class Cluster {
        public Cluster(int __sector, int __layer, int __one, int __two){
            sector = __sector;
            layer = __layer;
            one = __one;
            two = __two;
        }
        
        public int sector = 0;
        public int  layer = 0;
        public int    one = 0;
        public int    two = 0;
        
        @Override
        public String toString(){ return String.format("S/L [%3d,%3d] %4d %4d", sector,layer,one,two);}
    } 
    
    public static class Segment {
        public int[] index = null;
        public double probability = 0.0;
        
        public Segment(int i1, int i2, int i3, int i4){
            index = new int[]{i1,i2,i3,i4};
        }
        
        public String toString(){
        return String.format("SEG [%4d %4d %4d %4d] %9.6f", index[0],index[1],index[2],index[3],probability);}
    }
    
    public void add(int sector,int layer, int indx1, int indx2){
        if(map.containsKey(layer)==false) map.put(layer, new ArrayList<>());
        map.get(layer).add(new Cluster(sector,layer,indx1,indx2));
    }
    
    public void show(){
        for(Map.Entry<Integer,List<Cluster> > entry : map.entrySet()){
            System.out.println("CLUSTER LAYER " + entry.getKey());
            for(Cluster c : entry.getValue()) System.out.println("\t" + c);
        }
    }
    
    public void analyze(Bank b){
        for(int i = 0; i < b.getRows(); i++){
            int sector = b.getInt("sector", i);
            int  layer = b.getInt("layer", i);
            for(int j = i+1; j < b.getRows(); j++){
                int sectorj = b.getInt("sector", j);
                int  layerj = b.getInt("layer", j);
                if(sector==sectorj){
                    if(layer==1&&layerj==2) add(sector,1,i,j);
                    if(layer==2&&layerj==1) add(sector,1,i,j);
                    
                    if(layer==3&&layerj==4) add(sector,2,i,j);
                    if(layer==4&&layerj==3) add(sector,2,i,j);
                    
                    if(layer==5&&layerj==6) add(sector,3,i,j);
                    if(layer==6&&layerj==5) add(sector,3,i,j);
                                        
                }
            }
        } 
    }
    
    public List<Segment> getSegments(){
        List<Segment> segments = new ArrayList<>();
        List<Cluster> c1 = map.get(1);
        List<Cluster> c2 = map.get(2);
        List<Cluster> c3 = map.get(3);
        for(int i1 = 0; i1 < c1.size(); i1++){
            for(int i2 = 0; i2 < c2.size(); i2++){
                segments.add(new Segment(c1.get(i1).one, c1.get(i1).two, c2.get(i2).one, c2.get(i2).two));
            }
        }
        
        for(int i1 = 0; i1 < c1.size(); i1++){
            for(int i3 = 0; i3 < c3.size(); i3++){
                segments.add(new Segment(c1.get(i1).one, c1.get(i1).two, c3.get(i3).one, c3.get(i3).two));
            }
        }

        for(int i2 = 0; i2 < c2.size(); i2++){
            for(int i3 = 0; i3 < c3.size(); i3++){
                segments.add(new Segment(c2.get(i2).one, c2.get(i2).two, c3.get(i3).one, c3.get(i3).two));
            }
        }
        
        return segments;
    }
}
