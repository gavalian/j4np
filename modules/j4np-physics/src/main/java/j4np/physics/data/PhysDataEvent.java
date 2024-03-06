/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.data;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoReader;
import j4np.physics.PDGDatabase;
import j4np.physics.PDGParticle;
import j4np.physics.Particle;
import j4np.physics.PhysicsEvent;
import j4np.physics.Vector3;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class PhysDataEvent extends PhysicsEvent {

    protected Bank       dataBank = null;
    protected Bank     configBank = null;
    
    protected int  index_status = -1;
    protected int    index_chi2 = -1;
    protected int  index_charge = -1;
    
    protected PropertyList properties = new PropertyList();
    
    public PhysDataEvent(){}
    
    public PhysDataEvent(Bank b){
    
        Schema sc = b.getSchema().copy();
        dataBank  = new Bank(sc);
        if(sc.hasEntry("status")==true){
            index_status = sc.getEntryOrder("status");
        }
        if(sc.hasEntry("charge")==true){
            index_charge = sc.getEntryOrder("charge");
        }
        
        if(sc.hasEntry("chi2pid")==true){
            index_chi2 = sc.getEntryOrder("chi2pid");
        }
    }
    
    public void init(HipoReader r){
        properties.init(r);
        if(r.getSchemaFactory().hasSchema("REC::Event")){
            configBank = new Bank(r.getSchemaFactory().getSchema("REC::Event"));
        }
    }
    
    public void addLink(int order, String bank, String reference){
        properties.addLink(order, bank, reference);
    }
    
    public double property(int order, int row, String variable){
        return this.properties.getProperty(order, row, variable);
    }
    
    @Override
    public int count() {
        return dataBank.getRows();
    }

    public void read(Event event){
        event.read(dataBank);
        properties.read(event);
        if(configBank!=null) event.read(configBank);
        //System.out.printf(" bank ");
    }
    
    public int getHelicity(){
        if(configBank!=null){
            if(configBank.getRows()>0) return configBank.getInt(6, 0);
        }
        return 0;
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

    @Override
    public double chi2(int index) {
        if(index_status>=0)
            return dataBank.getFloat(index_chi2, index);
        return 0.0;
    }
    
    public static class PropertyList {
        Map<Integer, PropertyLink> map = new HashMap<>();
        
        public void addLink(int index, String bankname, String order){
            map.put(index, new PropertyLink(bankname,order));
        }
        
        public void init(HipoReader r){
            for(Map.Entry<Integer,PropertyLink> entry : map.entrySet()){
                System.out.printf("[PropertyList] >> initializing bank order = %d, name = %s  with [reference = %s]\n",
                        entry.getKey(),entry.getValue().bankName , entry.getValue().reference);
                entry.getValue().bank = r.getBank(entry.getValue().bankName);
            }
        }
        
        public double getProperty(int order, int row, String variable){
            PropertyLink link = map.get(order);
            int nrows = link.bank.getRows();
            System.out.printf(" bank %s , rows = %d\n",link.bankName,nrows);
            for(int i = 0; i < nrows; i++){
                int linkRow = link.bank.getInt(link.reference, i);
                if(linkRow==row) return link.bank.getValue(variable, i);
            }
            return 0.0;
        }
        
        public void read(Event e){
            for(Map.Entry<Integer,PropertyLink> entry : map.entrySet()){
                e.read(entry.getValue().bank);
            }
        }
    }
    
    public static class PropertyLink {
        String bankName  = "";
        String reference = "";
        Bank   bank      = null;
        public PropertyLink(String __bank, String __ref){
            bankName = __bank; reference = __ref;
        }
    }
}
