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
import org.jlab.jnp.hipo4.data.Schema;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoReader;
import org.jlab.jnp.hipo4.operations.BankExpression;
import org.jlab.jnp.hipo4.operations.BankIterator;

/**
 *
 * @author gavalian
 */
public class ClasEvent extends PhysicsEvent {
    
    public enum EventType {
        FWD_TRIGGER,TAG_TRIGGER, UNKNOWN_TRIGGER, SIMULATION, LUND
    }
        
    private int index_px = -1;
    private int index_py = -1;
    private int index_pz = -1;
    
    private int index_vx = -1;
    private int index_vy = -1;
    private int index_vz = -1;
        
    private int index_pid = -1;
    private int index_charge = -1;
    private int index_status = -1;
    
    BankStore store = new BankStore();
    
    private EventType clasEventType = EventType.UNKNOWN_TRIGGER;
    
    
    public ClasEvent(){
       store.add("REC::Particle","REC::Calorimeter");
    }
    
    public ClasEvent(String[] banks){
        //for(int i = 0; i < banks.length;i++)
        store.add(banks);
    }
    
    public ClasEvent setEventType(EventType type){
        clasEventType = type; return this;
    }
    
    public static ClasEvent with(HipoChain r, String[] banks){
        ClasEvent event = new ClasEvent(banks); event.store.init(r); 
        event.initIndices();
        return event;
    }
    
    public static ClasEvent with(HipoChain r){
        ClasEvent event = new ClasEvent(); event.store.init(r); 
        event.initIndices();
        return event;
    }
    
    private void initIndices(){
        Bank part = store.getBanks().get(0);
        Schema schema = part.getSchema();
        for(int row = 0; row < schema.getEntryList().size(); row++){
            String item = schema.getEntryList().get(row);
            if(item.compareTo("pid")==0) index_pid = row;
            if(item.compareTo("px")==0 ) index_px  = row;
            if(item.compareTo("py")==0 ) index_py  = row;
            if(item.compareTo("pz")==0 ) index_pz  = row;
            
            if(item.compareTo("vx")==0 ) index_vx  = row;
            if(item.compareTo("vy")==0 ) index_vy  = row;
            if(item.compareTo("vz")==0 ) index_vz  = row;
            if(item.compareTo("status")==0 ) index_status  = row;
            if(item.compareTo("charge")==0 ) index_charge  = row;
        }
    }
    
    public void read(Event event){        
        store.read(event);
        
        if(clasEventType==EventType.LUND){
            int count = count();
            for(int i = 0; i < count; i++){
                int status = status(i);
                if(status!=1){ status(i,-900);}
            }
        }
        
        if(clasEventType==EventType.FWD_TRIGGER){
            int count = count();
            if(count>0){
                int status = status(0);
                if(Math.abs(status)>2000&&Math.abs(status)<3000){
                    status(0,1);
                }
                for(int i = 1; i < count; i++){
                    status = status(i);
                    if(status>2000&&status<3000){
                        status(i,1);
                    }
                }
                //int status = status(i);
                //if(status!=1){ status(i,-900);}
            }
        }
        //clasEventType = EventType.UNKNOWN_TRIGGER;
        
        /*
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
        }*/
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
        if(index_charge>=0)
            return store.getBanks().get(0).getInt(index_charge, index);
        return 0;
    }
    
    @Override
    public int pid(int index) {
        if(index_pid>=0)
            return store.getBanks().get(0).getInt(index_pid, index);
        return 0;
    }

    @Override
    public int status(int index) {
        if(index_status>=0)
            return store.getBanks().get(0).getInt(index_status, index);
        return 1;
    }
    
    @Override
    public void status(int index, int value) {
        if(index_status>=0)
            store.getBanks().get(0).putInt(index_status, index,value);
    }
    
    @Override
    public void vector(Vector3 v, int index) {
        if(index_px>=0&&index_py>=0&&index_pz>=0){
            Bank b = store.getBanks().get(0);
            v.setXYZ(
                    b.getFloat(index_px, index),
                    b.getFloat(index_py, index),
                    b.getFloat(index_pz, index));
        }
    }

    @Override
    public void vertex(Vector3 v, int index) {
        if(index_vx>=0&&index_vy>=0&&index_vz>=0){
        Bank b = store.getBanks().get(0);
        v.setXYZ(
                b.getFloat(4, index),
                b.getFloat(5, index),
                b.getFloat(6, index));
        }
    }
    
    
    public void show(){
        //store.show();
        store.getBanks().get(0).show();
    }
    
    public void createBank(){
        Bank bank = store.getBanks().get(0);
        BankExpression  exp = new BankExpression("status>0",bank.getSchema());
        BankIterator   iter = new BankIterator(100);        
        exp.getIterator(bank, iter);
    }
}
