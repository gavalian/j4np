/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public abstract class PhysicsEvent {
    
    public PhysicsEvent(){
        
    }    
    
    abstract public int   count();
    abstract public int   charge(int index);
    abstract public int   pid(int index);        
    abstract public int   status(int index);
    abstract public void  status(int index, int value);
    abstract public void  vector(Vector3 v, int index);
    abstract public void  vertex(Vector3 v, int index);    
    
    
    
    public void vector(Vector3 v, int pid, int skip){
        int index = getOrderByPid(pid,skip);
        if(index<0){ v.setXYZ(0, 0, 0); return; }
        vector(v,index);
    }
    
    public void vertex(Vector3 v, int pid, int skip){
        int index = getOrderByPid(pid,skip);
        if(index<0){ v.setXYZ(0, 0, 0); return; }
        vertex(v,index);
    }
    
    public void vector(LorentzVector v, double mass,  int pid, int skip){
        int index = getOrderByPid(pid,skip);
        if(index<0){ v.setPxPyPzM(0, 0, 0, 0); return; }
        vector(v.vect(),index);
        v.setE(Math.sqrt(v.vect().mag2() + mass*mass));
    }
    
    public int getOrderByPid(int pid, int skip){
        int order = 0;
        int index = 0;
        for(int i = 0; i < count(); i++){
            if(status(i)>0&&pid(i)==pid){
                if(order==skip){
                    return i;
                } else {
                    order++;
                }
            }
        }
        return -1;
    }
    
    private int getOrderByPidRaw(int pid, int skip){
        int order = 0;
        int index = 0;
        for(int i = 0; i < count(); i++){
            if(pid(i)==pid){
                if(order==skip){
                    return i;
                } else {
                    order++;
                }
            }
        }
        return -1;
    }
    
    public int getOrderByCharge(int charge, int skip){
        int order = 0;
        int index = 0;
        for(int i = 0; i < count(); i++){
            if(status(i)>0&&charge(i)==charge){
                if(order==skip){
                    return i;
                } else {
                    order++;
                }
            }
        }
        return -1;
    }
            
    
    
    public int countByPid(int pid){
        int counter = 0;
        for(int i = 0; i < count(); i++){
            if(status(i)>0&&pid(i)==pid) counter++;
        }
        return counter;
    }
    
    public int countByCharge(int pcharge){
        int counter = 0;
        for(int i = 0; i < count(); i++){
            if(status(i)>0&&charge(i)==pcharge) counter++;
        }
        return counter;
    }
    
    private String particleLundString(int pid, Vector3 vec, Vector3 vrt){
        PDGParticle part = PDGDatabase.getParticleById(pid);

        if(part==null) return null;
        LorentzVector lv = LorentzVector.withPxPyPzM(vec.x(), vec.y(), vec.z(), part.mass());
        StringBuilder str = new StringBuilder();
        str.append(String.format("%3.0f. %4d %6d %2d %2d %9.4f %9.4f %9.4f ", (float) part.charge(), 
                (int) 1, pid, (int) 0, (int) 0,
                vec.x(), vec.y(), vec.z()));
        
        str.append(String.format("%9.4f %9.4f %11.4f %9.4f %9.4f", lv.e(), lv.mass(), vrt.x(), vrt.y(),
                vrt.z()));
        return str.toString();
    }
    
    
    public PhysicsEvent maskAll(){
        int size = count();
        for(int i = 0; i < size; i++){ this.status(i, -900);}
        return this;
    }
    
    public PhysicsEvent unmask(int pid, int skip){
        int index = this.getOrderByPidRaw(pid, skip);
        if(index>=0) status(index,1);
        return this;
    }
    
    public void makeVector(LorentzVector v, int[] pid, double[] mass, int[] skip, int[] sign){
        v.reset(); 
        Vector3 temp = new Vector3();
        
        for(int p = 0; p < pid.length; p++){
            vector(temp,pid[p],skip[p]);
            
                
            
            if(sign[p]>0){
                v.add(temp.x(), temp.y(), temp.z(), mass[p]);
            } else {
                v.sub(temp.x(), temp.y(), temp.z(), mass[p]);
            }
            /*if(pid.length==1){
            System.out.printf(" THETA %8.5f, new VECTOR %8.5f\n",
                        Math.toDegrees(temp.theta()),
                        Math.toDegrees(v.theta()));
            }*/
        }
    }
    
    public String toLundString(){
        Vector3 vec = new Vector3();
        Vector3 vrt = new Vector3();
        StringBuilder str = new StringBuilder();
        List<Integer> index = new ArrayList<>();
        for(int i = 0; i < this.count(); i++){
            if(status(i)>=0) index.add(i);
        }
        
        int count = index.size();
                        
        str.append(String.format("%12d %2d. %2d. %2d %2d %5.3f %7.3f %7.3f %7.3f %7.3f\n", 
                count, (int) 1, (int) 1, (int) 1,
                (int) 1, (float) 0.0, (float) 0.0, 10.5,
                (float) 0.0, (float) 0.0));
        
        for (int loop = 0; loop < count; loop++) {
            this.vector(vec, index.get(loop));
            this.vertex(vrt, index.get(loop));
            
            str.append(String.format("%5d", loop + 1));
            String pLund = this.particleLundString(this.pid(index.get(loop)), vec, vrt);
            if(pLund!=null) str.append(pLund);
            if(loop!=count-1)str.append("\n");
            //str.append("\n");
        }

        return str.toString();
    }
}
