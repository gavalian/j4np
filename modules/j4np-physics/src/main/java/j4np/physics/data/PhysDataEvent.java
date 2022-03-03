/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.physics.PDGDatabase;
import j4np.physics.PDGParticle;
import j4np.physics.Particle;
import j4np.physics.PhysicsEvent;
import j4np.physics.Vector3;

/**
 *
 * @author gavalian
 */
public class PhysDataEvent extends PhysicsEvent {

    protected Bank     dataBank = null;
    protected int  index_status = -1;
    protected int  index_charge = -1;
    
    public PhysDataEvent(Bank b){
        Schema sc = b.getSchema().copy();
        dataBank  = new Bank(sc);
        if(sc.hasEntry("status")==true){
            index_status = sc.getEntryOrder("status");
        }
        if(sc.hasEntry("charge")==true){
            index_charge = sc.getEntryOrder("charge");
        }
    }    
    
    @Override
    public int count() {
        return dataBank.getRows();
    }

    public void read(Event event){
        event.read(dataBank);
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
        return dataBank.getInt("pid", index);
    }

    @Override
    public int status(int index) {
        if(index_status>=0)
            return dataBank.getInt(index_status, index);
        return 1;
    }

    @Override
    public void status(int index, int value) {
        if(index_status>=0)
            dataBank.putInt("status", index, value); 
    }

    @Override
    public void vector(Vector3 v, int index) {
        v.setXYZ(
                dataBank.getFloat("px", index), dataBank.getFloat("py", index), 
                dataBank.getFloat("pz", index)
                );
    }

    @Override
    public void vertex(Vector3 v, int index) {
        v.setXYZ(
                dataBank.getFloat("vx", index), dataBank.getFloat("vy", index), 
                dataBank.getFloat("vz", index)
                );
    }
    
}
