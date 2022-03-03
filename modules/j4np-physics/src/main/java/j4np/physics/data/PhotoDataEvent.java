/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.physics.LorentzVector;
import j4np.physics.PDGDatabase;
import j4np.physics.PDGParticle;
import j4np.physics.PhysicsEvent;
import j4np.physics.Vector3;

/**
 *
 * @author gavalian
 */
public class PhotoDataEvent extends PhysDataEvent {
    
    private Bank     tagrBank = null;

    private int  index_energy_tagr = -1;
    
    public PhotoDataEvent(Bank b, Bank tagr){
        super(b);   
        Schema sct = tagr.getSchema().copy();          
        tagrBank  = new Bank(tagr.getSchema());
        if(sct.hasEntry("energy")==true){
            index_energy_tagr = sct.getEntryOrder("energy");
            System.out.printf("[]  order for energy = %d\n",index_energy_tagr);
        } else {
            System.out.printf("ERROR: tagger bank [%s] does not have entry \"energy\"\n",sct.getName());
            sct.show();
        }
    }    
    
    @Override
    public int count() {
        return dataBank.getRows();
    }

    @Override
    public void read(Event event){
        event.read(dataBank);
        event.read(tagrBank);
        //System.out.printf(" bank ");
    }
    
    @Override
    public int charge(int index) {
        if(index_charge>=0)
            return dataBank.getInt(index_charge, index);
        PDGParticle p = PDGDatabase.getParticleById(pid(index));
        if(p==null) return 0;
        return p.charge();
    }

    @Override
    public int pid(int index) {
        if(index<0){ 
            switch(index){
                case -1: return 2212;
                case -2: return 22;
                default: return 22;
            }
        }
        return dataBank.getInt("pid", index);
    }

    
    @Override
    public void vector(Vector3 v, int pid, int skip){
        int index = getOrderByPid(pid,skip);
        if(index<0){ 
            if(index==-1){
                v.setXYZ(0, 0, 0);
            } else {
                v.setXYZ(0.0,0.0,tagrBank.getFloat(index_energy_tagr, 0));
            }
            return; 
        }
        vector(v,index);
    }
    
    @Override
    public void vertex(Vector3 v, int pid, int skip){
        int index = getOrderByPid(pid,skip);
        if(index<0){ 
            v.setXYZ(0, 0, -50.0); 
            return; 
        }
        vertex(v,index);
    }
    
    public void vector(LorentzVector v, double mass,  int pid, int skip){
        int index = getOrderByPid(pid,skip);
        if(index<0){ 
            if(index==-5000){
                v.setPxPyPzM(0, 0, 0, 0.938); 
            } else {
                v.setPxPyPzM(0, 0, tagrBank.getFloat(index_energy_tagr, 0), 0.0);
            }
            
            return; 
        }
        vector(v.vect(),index);
        v.setPxPyPzE(v.vect().x(),v.vect().y(),v.vect().z(), Math.sqrt(v.vect().mag2() + mass*mass));
    }
    
    @Override
    public int getOrderByPid(int pid, int skip){
        
        if(pid==5000) return -1;
        if(pid==5001) return -2;            
        
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
    
    @Override
    public int status(int index) {
        if(index<0) return 1;
        if(index_status>=0)
            return dataBank.getInt(index_status, index);
        return 1;
    }

    @Override
    public void status(int index, int value) {
        if(index>=0)
            if(index_status>=0)
                dataBank.putInt("status", index, value); 
    }

    @Override
    public void vector(Vector3 v, int index) {
        if(index<0){
            if(index==-1){
                v.setXYZ(0.0, 0.0, 0.0);
            } else {
                v.setXYZ(0.0, 0.0, tagrBank.getFloat(index_energy_tagr, 0));
            }
        } else {
            v.setXYZ(
                    dataBank.getFloat("px", index), dataBank.getFloat("py", index), 
                    dataBank.getFloat("pz", index)
                );
        }
    }

    @Override
    public void vertex(Vector3 v, int index) {
        if(index<0){
            v.setXYZ(0.0, 0.0, -50);
        } else {
            v.setXYZ(
                    dataBank.getFloat("vx", index), dataBank.getFloat("vy", index), 
                    dataBank.getFloat("vz", index)
                );
        }
    }
}
