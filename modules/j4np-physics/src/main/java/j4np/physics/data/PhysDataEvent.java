/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.physics.PhysicsEvent;
import j4np.physics.Vector3;

/**
 *
 * @author gavalian
 */
public class PhysDataEvent extends PhysicsEvent {

    private Bank dataBank = null;
        
    public PhysDataEvent(Bank b){
        Schema sc = b.getSchema().copy();
        dataBank  = new Bank(sc);
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
        return dataBank.getInt("charge", index);
    }

    @Override
    public int pid(int index) {
        return dataBank.getInt("pid", index);
    }

    @Override
    public int status(int index) {
        return dataBank.getInt("status", index);
    }

    @Override
    public void status(int index, int value) {
        dataBank.putShort("status", index,(short) value);        
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
