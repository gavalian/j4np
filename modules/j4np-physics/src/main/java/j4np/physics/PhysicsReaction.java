/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.VectorOperator.OperatorType;
import j4np.physics.data.PhotoDataEvent;
import j4np.physics.data.PhysDataEvent;
import j4np.physics.store.EventModifierStore.EventModifierForward;
import j4np.physics.store.ReactionConfiguration;
import java.util.ArrayList;
import java.util.List;
import twig.data.H1F;
import twig.graphics.TGCanvas;
import twig.tree.Tree;

/**
 * 
 * @author gavalian
 */
public class PhysicsReaction extends Tree {
    
    protected List<VectorOperator> vecOprators = new ArrayList<>();
    protected EventFilter          eventFilter = new EventFilter("X+:X-:Xn");
    protected LorentzVector         beamVector = new LorentzVector();
    protected LorentzVector       targetVector = new LorentzVector();
    protected PhysDataEvent       physicsEvent = null;
    protected HipoReader                reader = null;
    protected String        dataPrintoutFormat = "%8.5f";
    protected List<ReactionEntry>  operEntries = new ArrayList<>();
    protected long                counterCalls = 0L;
    protected long               counterFilter = 0L;
    protected Event              reactionEvent = new Event();
    protected List<EventModifier>    modifiers = new ArrayList<>();
    
    public static  EventModifier FORWARD_ONLY = new EventModifier(){
        @Override
        public void modify(PhysicsEvent event) {
            int counter = event.count();
            for(int i = 0; i < counter ; i++){
                int status = event.status(i);
                if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                    event.status(i, 1);
                } else { event.status(i, -1);}
            }
        }
    };
    
    public  PhysicsReaction(String filter){
        eventFilter = new EventFilter(filter);
    }
    
    public  PhysicsReaction(){
        eventFilter = new EventFilter("X+:X-:Xn");
        beamVector.setPxPyPzM(   0.0, 0.0, 10.6, 0.0005);
        targetVector.setPxPyPzM( 0.0, 0.0,  0.0, 0.9380);
        addModifier(new EventModifierForward());
        addVector(getVector(),"-[11]");
        addVector("[11]");
        this.addEntry("w2",   0, OperatorType.MASS);
        this.addEntry("e_p",  1, OperatorType.P);
        this.addEntry("e_th", 1, OperatorType.THETA);
        this.addEntry("e_phi", 1, OperatorType.PHI);        
    }
    
    public  PhysicsReaction(String filter, double beamEnergy){
        eventFilter = new EventFilter(filter);
        beamVector.setPxPyPzM(0, 0, beamEnergy, 0.0005);
        targetVector.setPxPyPzM(0.0, 0.0, 0.0, 0.938);
    }
    
    public PhysicsReaction setDataSource(HipoReader r){
        return setDataSource(r,"mc::event");
    }
    
    public final PhysicsReaction addModifier(EventModifier m){
        this.modifiers.add(m); return this;
    }
    
    
    public PhysicsReaction setDataSource(HipoReader r, String bank){
        reader = r; 
        physicsEvent = new PhysDataEvent(reader.getBank(bank));
        return this;
    }
    
    public PhysicsReaction setDataSourcePhoto(HipoReader r, String bank, String tagr){
        reader = r; 
        physicsEvent = new PhotoDataEvent(reader.getBank(bank),reader.getBank(tagr));
        return this;
    }
    
    public PhysicsReaction setFilter(String filter){ 
        eventFilter = new EventFilter(filter); return this;
    }
    
    public PhysicsReaction addEntry(String name, int order, OperatorType type){
        this.operEntries.add(new ReactionEntry(name,order,type)); return this;
    }
    
    protected PhysicsReaction addEntry(ReactionEntry entry){
        this.operEntries.add(entry); return this;
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
    
    public PhysicsEvent getPhysicsEvent(){ return this.physicsEvent;}
    
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
    
    
    public Event getDataEvent(){ return this.reactionEvent;}
    
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

    @Override
    public double getValue(int order) { 
        if(this.isValid(physicsEvent)==false) return -100000.0;        
        ReactionEntry re = operEntries.get(order);
        int index = re.entryOrder;
        return this.vecOprators.get(index).getValue(re.entryType);
    }

    @Override
    public double getValue(String branch) {
        int order = this.getBranchOrder(branch);
        return getValue(order);
    }

    @Override
    public List<String> getBranches() {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < this.operEntries.size();i++){
            list.add(operEntries.get(i).entryName);
        }
        return list;
    }

    @Override
    public int getBranchOrder(String name) {
        for(int i = 0; i < this.operEntries.size();i++){
            if(operEntries.get(i).entryName.compareTo(name)==0) return i;
        }
        return -1;        
    }

    @Override
    public void reset() {
        reader.rewind();
    }

    public void read(Event event){
        physicsEvent.read(event);
        for(EventModifier m : modifiers) m.modify(physicsEvent);        
        apply(physicsEvent);        
    }
    
    @Override
    public boolean next() {
        if(reader.hasNext()==false) return false;
        
        reader.nextEvent(reactionEvent);
        physicsEvent.read(reactionEvent);
        
        for(EventModifier m : modifiers) m.modify(physicsEvent);
        
        apply(physicsEvent);
        
        return true;
    }

    @Override
    public void configure() {
        ReactionConfiguration config = new ReactionConfiguration(null,this);
        config.show();
        
        this.setDataSource(config.reader, config.getBankName());
        this.modifiers.clear();
        this.addModifier(config.getEventModifier());
        double energy = config.getBeamEnergy();
        this.beamVector.setPxPyPzM(0.0, 0.0, energy, 0.0005);        
        System.out.println("energy = " + energy);
        //System.out.println();
        configChange();
    }
    
    public void configChange(){
        System.out.println("reconfiguring");
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
    
    public static void main(String[] args){
        
        // This is a documentation on how to use PhysicsReaction class
        
        //LorentzVector vcm = LorentzVector.withPxPyPzM(0, 0, 10.6, 0.0005).add(0, 0, 0, 0.938);
        
        //HipoReader r = new HipoReader("/Users/gavalian/Work/temp/dis_fmc_1.hipo");  
        //HipoReader r = new HipoReader("/Users/gavalian/Work/dataspace/denoise/out.ev.hipo_rec.hipo"); 
        HipoReader r = new HipoReader("/Users/gavalian/Work/dataspace/denoise/rec_output_filtered.hipo");
        PhysicsReaction react = new PhysicsReaction("11:211:-211",10.6);
        
        react.addVector(react.getVector(), "-[11]-[211]-[-211]")
                .addEntry("mxepipi", 0, OperatorType.MASS);
        
        //react.setDataSource(r, "rec::event");        
        react.setDataSource(r, "REC::Particle");
        
        react.addModifier(new EventModifier(){
            @Override
            public void modify(PhysicsEvent event) {
                int counter = event.count();
                for(int i = 0; i < counter ; i++){
                    int status = event.status(i);
                    if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                        event.status(i, 1);
                    } else { event.status(i, -1);}
                }
            }
        });
        
        H1F h = react.geth("mxepipi", "", 80, 0.6, 1.8);
        
        TGCanvas c = new TGCanvas();
        c.view().region().draw(h);
    }
}
