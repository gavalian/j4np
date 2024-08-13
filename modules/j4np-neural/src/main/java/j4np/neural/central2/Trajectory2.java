/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central2;

import j4ml.data.DataList;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Query;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Trajectory2 {
    
    public List<Segment> segments = new ArrayList<>();
    public double     probability = 0.0; 
    public int[]            index = new int[2];
    public int             status = 0;
    
    public Trajectory2(){
        
    }
    
    public void add(Segment seg) { segments.add(seg);}
    
    public boolean isValid(){
        if(Math.abs(segments.get(0).theta()-segments.get(1).theta())>Math.toRadians(35)) return false;
        if(Math.abs(segments.get(0).phi()-segments.get(1).phi())>Math.toRadians(25)) return false;
        return true;
    }
    
    public void update(Bank b){
        status = 0;
        status += b.getInt("status", segments.get(0).reference[0])>0?1:0;
        status += b.getInt("status", segments.get(0).reference[1])>0?1:0;
        status += b.getInt("status", segments.get(1).reference[0])>0?1:0;
        status += b.getInt("status", segments.get(1).reference[1])>0?1:0;
        status += b.getInt("status", index[0])>0?1:0;
        status += b.getInt("status", index[1])>0?1:0;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        int statusBST = 1;
        if(segments.get(0).status==0||segments.get(1).status==0) statusBST = 0;
        str.append(String.format("(%d) (%d) (%8.5f) ", statusBST, status,probability));
        for(Segment s : segments) str.append(s.toString());
        return str.toString();
    }
    /*
    public static List<Segment> getSegments(Bank b, int[] index){
        Query[] q = new Query[2];
        for(int i = 0; i < 2; i++){
            q[i] = new Query(b,String.format("layer==%d", index[i]));
        }
        List<Integer> iter1 = q[0].getIterator(b);
        List<Integer> iter2 = q[1].getIterator(b);
        List<Segment> segments = new ArrayList<>();
        for(int i1 = 0; i1 < iter1.size(); i1++){
            for(int i2 = 0; i2 < iter2.size(); i2++){
                int l1 = b.getInt("sector", i1);
                int l2 = b.getInt("sector", i2);
                if(l1==l2){
                   Segment t = new Segment(iter1.get(i1),iter2.get(i2));
                   t.update(b);
                   segments.add(t);
                }
            }
        }
        return segments;
    }*/
    
    public static void main(String[] args){
        
        
    }
}
