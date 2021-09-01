/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import j4np.physics.PhysicsEvent;
import j4np.physics.Vector3;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class ClasEvent extends PhysicsEvent {
    
    public enum EventType {
        FWD_TRIGGER,TAG_TRIGGER, UNKNOWN_TRIGGER
    }
    
    BankStore store = new BankStore();

    private EventType clasEventType = EventType.UNKNOWN_TRIGGER;
    
    public ClasEvent(){
       store.add("REC::Particle","REC::Calorimeter");
    }
    
    public static ClasEvent with(HipoChain r){
        ClasEvent event = new ClasEvent(); event.store.init(r); return event;
    }
        
    public void read(Event event){
        store.read(event);
        
        clasEventType = EventType.UNKNOWN_TRIGGER;
        
        int    pid = store.getBanks().get(0).getInt(0, 0);
        int status = store.getBanks().get(0).getInt(11, 0);
        if(status<-2000&&status>-3000&&pid==11){
            clasEventType = EventType.FWD_TRIGGER;
            store.getBanks().get(0).putShort(11, 0, (short) -status);
        }
        
        for(int i = 1; i < store.getBanks().get(0).getRows(); i++){
            int stat = store.getBanks().get(0).getInt(11, i);
            if(stat<2000||stat>2999){
                //System.out.printf("change from %d to %d\n",stat,-stat);
                store.getBanks().get(0).putShort(11, i, (short) -stat);
            }
        }
    }
    
    public EventType getEventType(){
        return clasEventType;
    }
    
    @Override
    public int count() {
        return store.getBanks().get(0).getRows();
    }

    @Override
    public int charge(int index) {
        return store.getBanks().get(0).getInt(8, index);
    }

    @Override
    public int pid(int index) {
        return store.getBanks().get(0).getInt(0, index);
    }

    @Override
    public int status(int index) {
        int status = store.getBanks().get(0).getInt(11, index);
        return status;
    }

    @Override
    public void vector(Vector3 v, int index) {
        Bank b = store.getBanks().get(0);
        v.setXYZ(
                b.getFloat(1, index),
                b.getFloat(2, index),
                b.getFloat(3, index));
    }

    @Override
    public void vertex(Vector3 v, int index) {
        Bank b = store.getBanks().get(0);
        v.setXYZ(
                b.getFloat(4, index),
                b.getFloat(5, index),
                b.getFloat(6, index));
    }
    
    
    public void show(){
        //store.show();
        store.getBanks().get(0).show();
    }
    
}
