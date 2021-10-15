/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

import j4np.physics.VectorOperator.OperatorType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class PhysicsReaction {
    
    protected List<VectorOperator> vecOprators = new ArrayList<>();
    protected EventFilter          eventFilter = new EventFilter("X+:X-:Xn");
    protected LorentzVector         beamVector = new LorentzVector();
    protected LorentzVector       targetVector = new LorentzVector();
    protected String        dataPrintoutFormat = "%8.5f";
    protected List<ReactionEntry>  operEntries = new ArrayList<>();
    protected long                counterCalls = 0L;
    protected long               counterFilter = 0L;
    
    public  PhysicsReaction(String filter){
        eventFilter = new EventFilter(filter);
    }
    
    public  PhysicsReaction(String filter, double beamEnergy){
        eventFilter = new EventFilter(filter);
        beamVector.setPxPyPzM(0, 0, beamEnergy, 0.0005);
        targetVector.setPxPyPzM(0.0, 0.0, 0.0, 0.938);
    }
    
    public PhysicsReaction setFilter(String filter){ 
        eventFilter = new EventFilter(filter); return this;
    }
    
    public PhysicsReaction addEntry(String name, int order, OperatorType type){
        this.operEntries.add(new ReactionEntry(name,order,type)); return this;
    }
    
    public PhysicsReaction addEntry(String name, int order, OperatorType type, double min, double max){
        this.operEntries.add(new ReactionEntry(name,order,type,min,max)); return this;
    }
    
    public LorentzVector getVector(){ 
        LorentzVector vec = LorentzVector.from(beamVector).add(targetVector);
        return vec;
    }
    
    public PhysicsReaction addVector(LorentzVector vec, String oper){
        vecOprators.add(new VectorOperator(vec,oper));
        return this;
    }
    public PhysicsReaction addVector(String oper){
        vecOprators.add(new VectorOperator(LorentzVector.withPxPyPzM(0.0, 0.0, 0.0, 0.0),oper));
        return this;
    }
    public List<VectorOperator> operators(){ return this.vecOprators;}
    
    public boolean isValid(PhysicsEvent event){ 
        counterCalls++;
        boolean status = eventFilter.isValid(event); 
        if(status==true) counterFilter++;
        return status;
    }
    
    public boolean checkCuts(){
        for(ReactionEntry entry : operEntries){
            double value = this.vecOprators.get(entry.entryOrder).getValue(entry.entryType);
            if(entry.isValid(value)==false) return false;
        }
        return true;
    }
    
    public void    apply(PhysicsEvent event){
        for(VectorOperator op : vecOprators){
            op.apply(event);
        }
    }
    
    public void stats(){
        System.out.printf("----- calls %9d, filter passed %9d\n",counterCalls,counterFilter);
    }
    
    public void summary(){
        for(VectorOperator op  : vecOprators){
            System.out.println(op);
        }
        
        for(ReactionEntry e  : this.operEntries){
            System.out.println(e);
        }
    }
    
    @Override
    public String toString(){
        
        StringBuilder str = new StringBuilder();
        boolean status = checkCuts();
        if(status==true) {
            str.append("1");
        } else {
            str.append("0");
        }
        
        for(int i = 0; i < this.operEntries.size(); i++){
            ReactionEntry entry = this.operEntries.get(i);
            //if(i!=0) str.append(",");
            str.append(String.format(dataPrintoutFormat, 
                    this.vecOprators.get(entry.entryOrder).getValue(entry.entryType)));
        }
        return str.toString();
    }    
    
    public static class ReactionEntry {
        
        String    entryName = "u";
        int      entryOrder =  0;        
        double     minValue = -1000.0;
        double     maxValue =  1000.0;
        boolean    isCutSet = false;
        
        OperatorType entryType = OperatorType.MASS;
        
        public ReactionEntry(String name, int order, OperatorType type){
            entryName = name; entryOrder = order; entryType = type;
        }
        
        public ReactionEntry(String name, int order, OperatorType type, double min, double max){
            entryName = name; entryOrder = order; entryType = type;
            this.setCut(min, max);
        }
        
        public final ReactionEntry setCut(double min, double max){
            minValue = min; maxValue = max; isCutSet = true; return this;
        }
        
        public boolean isValid(double value){
            if(this.isCutSet==true){
               return (value>=minValue&&value<=maxValue);
            }
            return true;
        }
        @Override
        public String toString(){
            return String.format("->>> %14s , %8d, type = %s", entryName,entryOrder,entryType);
        }
    }
    
    
}
