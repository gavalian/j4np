/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.data.PhysDataEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class Reaction {
    
    protected List<VectorOperator> vecOprators = new ArrayList<>();
    protected List<PhysicsReaction.ReactionEntry> operEntries = new ArrayList<>();
    
    String              eventBank = "REC::Particle";
    Bank                 dataBank = null;
    PhysDataEvent    physicsEvent = null;
    
    protected EventFilter          eventFilter = new EventFilter("X+:X-:Xn");
    
    public Reaction(){}
    
    public Reaction(String filter){
        eventFilter = new EventFilter(filter);
    }
    
    public Reaction(HipoReader r){
        dataBank = r.getBank(eventBank);
        physicsEvent = new PhysDataEvent(dataBank);
    }
    
    public Reaction(HipoReader r, String name){
        eventBank = name;
        dataBank = r.getBank(eventBank);
        physicsEvent = new PhysDataEvent(dataBank);
    }
    
    public Reaction(HipoReader r, PhysDataEvent pde, double energy){
        eventBank = "undefined";
        //dataBank = r.getBank(eventBank);
        physicsEvent = pde;
    }
    
    public Reaction setDataBank(String db){ this.eventBank = db; return this;}
    public Reaction setSource(HipoReader r){ 
        dataBank = r.getBank(eventBank);
        physicsEvent = new PhysDataEvent(dataBank); 
        return this;
    }
    
    public Reaction addVector(String operator){
        vecOprators.add(new VectorOperator(LorentzVector.withPxPyPzM(0.0, 0.0, 0.0, 0.0),operator));
        return this;
    }
    
    public Reaction addEntry(String name, int order, VectorOperator.OperatorType type, double min, double max){
        this.operEntries.add(new PhysicsReaction.ReactionEntry(name,order,type,min,max)); return this;
    }
    
    public void process(Event event){
        physicsEvent.read(event);
        for(VectorOperator op : vecOprators){
            op.apply(physicsEvent);
        }
    }
    
    public boolean checkCuts(){
        if(this.eventFilter.isValid(physicsEvent)==false) return false;
        for(PhysicsReaction.ReactionEntry entry : operEntries){
            double value = this.vecOprators.get(entry.entryOrder).getValue(entry.entryType);
            if(entry.isValid(value)==false) return false;
        }
        return true;
    }
    //public void 
    
}
