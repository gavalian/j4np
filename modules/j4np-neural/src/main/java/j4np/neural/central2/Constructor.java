/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central2;

import j4ml.data.DataList;
import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Query;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Constructor {
    
    public static List<Segment> getSegments(Bank b, int[] index){
        return getSegments(b,index,true);
    }
    
    public static List<Segment> getSegments(Bank b, int[] index, boolean check){
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
                   if(check==true){
                       if(t.isValid()) segments.add(t);
                   } else segments.add(t);
                }
            }
        }
        return segments;
    }
    
    public static List<Trajectory2> getTrajectories(Bank b){
        
        List<Segment> seg1 = Constructor.getSegments(b, new int[]{1,2});
        List<Segment> seg2 = Constructor.getSegments(b, new int[]{3,4});
        List<Segment> seg3 = Constructor.getSegments(b, new int[]{5,6});
        for(Segment s : seg1) s.update(b);
        for(Segment s : seg2) s.update(b);
        for(Segment s : seg3) s.update(b);
        
        List<Trajectory2> list = new ArrayList<>();
        for(int i1 = 0; i1 < seg1.size(); i1++){
            for(int i2 = 0; i2 < seg2.size(); i2++){
                Trajectory2 t = new Trajectory2();
                t.add(seg1.get(i1));
                t.add(seg2.get(i2));
                if(t.isValid()) list.add(t);
            }
        }
        
        for(int i1 = 0; i1 < seg1.size(); i1++){
            for(int i3 = 0; i3 < seg3.size(); i3++){
                Trajectory2 t = new Trajectory2();
                t.add(seg1.get(i1));
                t.add(seg3.get(i3));
                if(t.isValid()) list.add(t);
            }
        }
        
        for(int i2 = 0; i2 < seg2.size(); i2++){
            for(int i3 = 0; i3 < seg3.size(); i3++){
                Trajectory2 t = new Trajectory2();
                t.add(seg2.get(i2));
                t.add(seg3.get(i3));
                if(t.isValid()) list.add(t);
            }
        }
        
        return list;
    }
    
    public static void updateTrajectory(Bank b, List<Trajectory2> t){
        List<Trajectory2> ref = new ArrayList<>();
        List<Segment> bmt = new ArrayList<>();
        
        ref.addAll(t);
        
        t.clear();
        
        int[] layerC = new int[]{ 7, 10, 12};
        int[] layerZ = new int[]{ 8,  9, 11};
        for( int c = 0; c < layerC.length; c++)
            for(int z = 0; z < layerZ.length; z++){
                List<Segment> seg = Constructor.getSegments(b, new int[]{layerC[c],layerZ[z]},false);
                bmt.addAll(seg);
            }
        
        for(int cbst = 0; cbst < ref.size(); cbst++){
            for(int cbmt = 0; cbmt < bmt.size(); cbmt++){
                Trajectory2 trj = new Trajectory2();
                trj.add(ref.get(cbst).segments.get(0));
                trj.add(ref.get(cbst).segments.get(1));
                trj.index[0] = bmt.get(cbmt).reference[0];
                trj.index[1] = bmt.get(cbmt).reference[1];
                trj.update(b);
                t.add(trj);
            }
        }
        //System.out.println(" trajectory = " + t.size()+ " LZ Segements = " + bmt.size());
    }
    
    public static float[] getFeatures(Bank b, int index){
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
    
    public static List<Trajectory2>  crop(List<Trajectory2> list, int size){
        if(list.size()<size) return list;
        Collections.shuffle(list);
        Collections.shuffle(list);
        return list.subList(0, size);
    }
    
    public static List<Trajectory2>  filter(List<Trajectory2> list, int status){
        List<Trajectory2> reduced = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) if(list.get(i).status==status) reduced.add(list.get(i));
        return reduced;
    }
    
    public static List<float[]> getFeatures(Bank b, Trajectory2 traj){
        List<float[]> features = new ArrayList();
        features.add(Constructor.getFeatures(b, traj.segments.get(0).reference[0]));
        features.add(Constructor.getFeatures(b, traj.segments.get(0).reference[1]));
        features.add(Constructor.getFeatures(b, traj.segments.get(1).reference[0]));
        features.add(Constructor.getFeatures(b, traj.segments.get(1).reference[1]));
        features.add(Constructor.getFeatures(b, traj.index[0]));
        features.add(Constructor.getFeatures(b, traj.index[1]));
        
        //for(int i = 0; i < index.size(); i++) features.add(this.getFeatures(b, index.get(i)));
        return features;
    }
    
     public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-10.8/study/central/MLSample1.hipo";
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("cvtml::clusters");
        DataList data = new DataList();
        
        for(int i = 0; i < 50; i++){
            r.nextEvent(b);
            
            List<Trajectory2> traj = Constructor.getTrajectories(b[0]);
            System.out.println("************************ event No");
            //for(Trajectory2 t : traj){
            //    System.out.println(t);
                //List<float[]> fff = Constructor.getFeatures(b[0], t);
                //float[] ff = DataEntry.combine(fff);
                //System.out.println(Arrays.toString(ff));
            //}
            Constructor.updateTrajectory(b[0], traj);
            
            

            
            List<Trajectory2> traj2 = Constructor.filter(traj, 6);
            System.out.println("SIZE = " + traj2.size());
            for(Trajectory2 t : traj2){
                System.out.println(t);
                //List<float[]> fff = Constructor.getFeatures(b[0], t);
                //float[] ff = DataEntry.combine(fff);
                //System.out.println(Arrays.toString(ff));
            }
            /*
            List<Segment> seg1 = Constructor.getSegments(b[0], new int[]{1,2});
            List<Segment> seg2 = Constructor.getSegments(b[0], new int[]{3,4});
            List<Segment> seg3 = Constructor.getSegments(b[0], new int[]{5,6});
            System.out.println("------ event No");
            System.out.println("********************************************");
            System.out.println("********************************************");
            System.out.println("******************** 1&2 ");
            for(Segment s : seg1) if(s.isValid()) System.out.println(s);
            System.out.println("******************** 3&4 ");
            for(Segment s : seg2) if(s.isValid()) System.out.println(s);
            System.out.println("******************** 5&6 ");
            //for(Segment s : seg3) if(s.isValid()) System.out.println(s);
            for(Segment s : seg3)  System.out.println(s);*/
            
        }
    }
}
