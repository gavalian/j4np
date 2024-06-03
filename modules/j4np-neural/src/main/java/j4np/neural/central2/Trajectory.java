/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central2;

import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author gavalian
 */
public class Trajectory {
    
    public List<Integer> index = new ArrayList<>();
    public int  status = 0;
    public double probability = 0.0;
    
    public Trajectory(){
        
    }
    
    public void setStatus(Bank b){
        status = 1;
        for(int i = 0; i < index.size(); i++){
            if(b.getInt("status",index.get(i))==0) { status = 0; return;}
        }
    }
    
    public float[] getFeatures(Bank b, int index){
        int layer = b.getInt("layer", index);
        float[] f = new float[4];
        if(layer>=1&&layer<=6){
            Vector3D v = new Vector3D();
            v.setXYZ(b.getFloat("xo",index),b.getFloat("yo",index),b.getFloat("zo",index));
            f[0] = (float) ((v.rho()-60)/80);
            f[1] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
            v.setXYZ(b.getFloat("xe",index),b.getFloat("ye",index),b.getFloat("ze",index));
            f[2] = (float) ((v.rho()-60)/80);
            f[3] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
        }
        
        if(layer==7||layer==10||layer==12){
            Vector3D v = new Vector3D();
            v.setXYZ(b.getFloat("xo",index),b.getFloat("yo",index),b.getFloat("zo",index));
            f[0] = (float) ((v.z()+150)/400.0);
            f[1] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
            v.setXYZ(b.getFloat("xe",index),b.getFloat("ye",index),b.getFloat("ze",index));
            f[2] = (float) ((v.z()+150)/400.0);
            f[3] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
        }
        
        if(layer==8||layer==9||layer==11){
            Vector3D v = new Vector3D();
            v.setXYZ(b.getFloat("xo",index),b.getFloat("yo",index),b.getFloat("zo",index));
            f[0] = (float) ((v.rho()-160)/50.0);
            f[1] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
            v.setXYZ(b.getFloat("xe",index),b.getFloat("ye",index),b.getFloat("ze",index));
            f[2] = (float) ((v.rho()-160)/50.0);
            f[3] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
        }
        
        return f;
    }
    
    public List<float[]> getFeatures(Bank b){
        List<float[]> features = new ArrayList();
        for(int i = 0; i < index.size(); i++) features.add(this.getFeatures(b, index.get(i)));
        return features;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("trj status = %4d probability = %8.5f [", status, probability));
        for(int i = 0; i < index.size(); i++) str.append(String.format("%4d ", index.get(i)));
        str.append("]");
        return str.toString();
    }
    
    public static int getHighest(List<Trajectory> list){
        if(list.size()==0) return -1;
        int          index = 0;
        double probability = list.get(0).probability;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).probability>probability){
                probability = list.get(i).probability; index = i;
            }
        }
        return index;
    }
    
    public static List<Trajectory> reduce(List<Trajectory> list, Trajectory ref){
        List<Trajectory> reduced = new ArrayList<>();
        for(Trajectory t : list){
            if(ref.overlap(t)==0) reduced.add(t);
        }
        return reduced;
    }
    
    public static Trajectory combine(Trajectory t1, Trajectory t2){
        Trajectory t = new Trajectory();
        for(Integer i : t1.index) t.index.add(i);
        for(Integer i : t2.index) t.index.add(i);
        return t;
    }
    
    public static List<Trajectory> combine(List<Trajectory> t1, List<Trajectory> t2){
        List<Trajectory> list = new ArrayList<>();
        for(int i1 = 0; i1 < t1.size(); i1++){
            for(int i2 = 0; i2 < t2.size(); i2++){
                list.add(Trajectory.combine(t1.get(i1), t2.get(i2)));
            }
        }
        return list;
    }
    
    public int overlap(Trajectory t){
       if(t.index.size()!=this.index.size()) return 0;
       int c = 0;
       for(int i = 0; i < t.index.size(); i++) if(Objects.equals(t.index.get(i), this.index.get(i))) c++;
       return c;
    }
    
    public static List<Trajectory> filter(List<Trajectory> traj, Trajectory ref, int overlap){
        List<Trajectory> list = new ArrayList<>();
        for(Trajectory t: traj) if(t.overlap(ref)==overlap) list.add(t);
        return list;
    }
}
