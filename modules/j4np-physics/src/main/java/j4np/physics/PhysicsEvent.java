/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

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
}
