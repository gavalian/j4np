/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.VectorOperator.OperatorType;
import j4np.physics.data.PhotoDataEvent;
import j4np.physics.data.PhysDataEvent;
import j4np.physics.store.EventModifierStore.EventModifierForward;
import j4np.physics.store.ReactionConfiguration;
import java.util.ArrayList;
import java.util.List;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.graphics.TGCanvas;
import twig.tree.Tree;

/**
 * 
 * @author gavalian
 */
public class PhysicsReaction extends Tree {
    
    protected List<VectorOperator> vecOprators = new ArrayList<>();
    protected List<ReactionEntry>  operEntries = new ArrayList<>();
    protected List<EventModifier>    modifiers = new ArrayList<>();
    
    protected EventFilter          eventFilter = new EventFilter("X+:X-:Xn");
    protected LorentzVector         beamVector = new LorentzVector();
    protected LorentzVector       targetVector = new LorentzVector();
    
    protected PhysDataEvent       physicsEvent = null;
    protected HipoReader                reader = null;
    protected String        dataPrintoutFormat = "%8.5f";
    
    protected long                counterCalls = 0L;
    protected long               counterFilter = 0L;
    protected Event              reactionEvent = new Event();
    
    
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
    
    
    public static  EventModifier FORWARD_ONLY_CHI2PID = new EventModifier(){
        @Override
        public void modify(PhysicsEvent event) {
            int counter = event.count();
            for(int i = 0; i < counter ; i++){
                int status = event.status(i);
                if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                    event.status(i, 1);
                    double chi2 = event.chi2(i);
                    if(Math.abs(chi2)>5.0) event.status(i, -1);
                } else { event.status(i, -1); }
            }
        }
    };
    
    public static  EventModifier FORWARD_ONLY_MESONX = new EventModifier(){
        @Override
        public void modify(PhysicsEvent event) {
            int counter = event.count();
            
            int    pid0 = event.pid(0);
            if(pid0==11){
                for(int i = 0; i < counter ; i++){
                    event.status(i, -1);
                }
                return;
            }
            
            for(int i = 0; i < counter ; i++){
                int status = event.status(i);
                int    pid = event.pid(i);
                if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                    event.status(i, 1);
                } else { 
                    if(pid==11&&Math.abs(status)>=1000&&Math.abs(status)<2000){
                        event.status(i, 1);
                    } else {
                        event.status(i, -1);
                    }
                }
            }
        }
    };
    
    public static  EventModifier MC_FORWARD_ONLY = new EventModifier(){
        @Override
        public void modify(PhysicsEvent event) {
            int counter = event.count();
            Vector3 v = new Vector3();
            for(int i = 0; i < counter ; i++){
                event.vector(v, i);
                double theta = Math.toDegrees(v.theta());
                if(theta>5&&theta<35) event.status(i, 1); else event.status(i, -1);
            }
                
        }
    };
    
    public static  EventModifier FORWARD_CENTRAL = new EventModifier(){
        @Override
        public void modify(PhysicsEvent event) {
            int counter = event.count();
            
            for(int i = 0; i < counter ; i++){                
                    event.status(i, 1);               
            }
        }
    };
    
    public static  EventModifier EMPTY = new EventModifier(){
        @Override
        public void modify(PhysicsEvent event) {
            
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
    
    public void setEventClass(PhysDataEvent pde){
        this.physicsEvent = pde;
    }
    
    public PhysicsReaction setDataSource(HipoReader r, String bank){
        reader = r; 
        physicsEvent = new PhysDataEvent(reader.getBank(bank));
        return this;
    }
    
    
    public PhysicsReaction setDataSource(HipoReader r, PhysDataEvent physEvent){
        reader = r; 
        physicsEvent = physEvent;
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
    
    public LorentzVector getBeamVector(){ 
        LorentzVector vec = LorentzVector.from(beamVector);
        return vec;
    }
    
    public PhysicsReaction addVector(LorentzVector vec, String oper){
        VectorOperator vv = VectorOperator.parseOperator(oper);
        vv.setVector(vec);
        vv.show();
        vecOprators.add(vv);
        //vecOprators.add(new VectorOperator(vec,oper));
        return this;
        //vecOprators.add(new VectorOperator(vec,oper));
        //return this;
    }
    
    public PhysicsReaction addVector(String oper){
        VectorOperator vv = VectorOperator.parseOperator(oper);
        vv.setVector(LorentzVector.withPxPyPzM(0.0, 0.0, 0.0, 0.0));
        vv.show();
        vecOprators.add(vv);
        return this;
        //vecOprators.add(new VectorOperator(LorentzVector.withPxPyPzM(0.0, 0.0, 0.0, 0.0),oper));
        //return this;
    }
    
    public PhysicsReaction addVector(VectorOperator oper){
        vecOprators.add(oper);
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
    
    public PhysicsEvent event(){ return this.physicsEvent;}
    public Event dataEvent(){ return this.reactionEvent;}
    
    public boolean checkCuts(){
        for(ReactionEntry entry : operEntries){
            double value = this.vecOprators.get(entry.entryOrder).getValue(entry.entryType);
            if(entry.isValid(value)==false) return false;
        }
        return true;
    }
    
    public VectorOperator operator(int index){
        return this.vecOprators.get(index);
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
        if(eventFilter.isValid(physicsEvent)==true){
            apply(physicsEvent);
        }
        
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
    
    public DataGroup process(){
        return null;
    }
    public static DataGroup getParticle(String file, double beamEnergy, 
            EventModifier modifier,String filter,  String operator, String value, int bins, double min, double max){
        return PhysicsReaction.getParticle(file, beamEnergy, "REC::Particle",modifier, filter, operator, value, bins, min, max);
    }
    public static DataGroup getParticle(String file, double beamEnergy, String bank,
            EventModifier modifier,String filter,  String operator, String value, int bins, double min, double max){
        H1F h = new H1F("reaction",bins,min, max);
        
        h.attr().setTitleX(String.format("%s(%s)",value.toLowerCase(),operator));
        h.attr().setLegend(String.format("%s(%s)",value.toLowerCase(),operator));
        h.attr().setFillColor(3);
        
        DataGroup group = new DataGroup(1,1);
        group.add(h, 0, "");
        PhysicsReaction r = new PhysicsReaction(filter,beamEnergy);
        r.setDataSource(new HipoReader(file),bank);
        r.addModifier(modifier);
        if(operator.startsWith("[b]+[t]")==true){
           String op2 = operator.replace("[b]+[t]", "");
           r.addVector(r.getVector(),op2);
        } else {
            r.addVector(operator);
        }
        r.addEntry("value", 0, OperatorType.valueOf(value));
        while(r.next()==true){
            h.fill(r.getValue(0));
        }
        return group;
    }
    
    public static DataGroup statistics(String file, EventModifier modifier, String[] filters){        
        return PhysicsReaction.statistics(file, "REC::Particle", modifier, filters);
    }
    
    
    public static DataGroup statistics(String file, String bank){        
        return PhysicsReaction.statistics(file, bank, PhysicsReaction.FORWARD_ONLY_CHI2PID, 
                new String[]{
                    "11:X+:X-:Xn", 
                    "11:1c:X+:X-:Xn",
                    "11:2c:X+:X-:Xn",
                    "11:3c:X+:X-:Xn",
                    "2c:X+:X-Xn", "3c:X+:X-:Xn"
                } );
    }
    
    public static DataGroup statistics(String file, String bank, EventModifier modifier, String[] filters){
        EventFilter[] list = new EventFilter[filters.length];
        for(int i = 0; i < filters.length; i++){            
            list[i] = new EventFilter(filters[i]);
        }
        return PhysicsReaction.statistics(file, bank, modifier, list);
    }
    
    public static DataGroup statistics(String file, EventModifier modifier){
        EventFilter[] list = new EventFilter[]{
            new EventFilter("11:X+:X-:Xn"),
            new EventFilter("11:1+:X+:X-:Xn"),
            new EventFilter("11:1-:X+:X-:Xn")
        };
        return PhysicsReaction.statistics(file, modifier, list);
    }
    
    public static DataGroup statistics(String file, EventModifier modifier, EventFilter[] filters){         
       return PhysicsReaction.statistics(file, "REC::Particle", modifier, filters);
    }
    
     public static DataGroup statistics(String file, String bank,  EventModifier modifier, EventFilter[] filters){        
        
        DataGroup group = new DataGroup(1,1);
        //H1F[] topology = new H1F[filters.length];        
        //new H1F("topology_"+(i+1),filters.length,0.5,filters.length+0.5);
        H1F topology = new H1F("topology",filters.length,0.5,filters.length+0.5);
        topology.attr().setFillColor(3);
        group.add(topology, 0, "");
        
        PhysicsReaction r = new PhysicsReaction("X+:X-:Xn",10.5);
        r.setDataSource(new HipoReader(file),bank);
        r.addModifier(modifier);
        int counter = 0;
        while(r.next()==true){
            for(int i = 0; i < filters.length; i++){
                if(filters[i].isValid(r.getPhysicsEvent())==true) 
                    topology.incrementBinContent(i);
            }
            counter++;
        }
        
        System.out.printf(">>> statisctics (processed = %9d)\n",counter);
        for(int k = 0; k < filters.length; k++){
            System.out.printf("%2d >> %24s : %6d %8.5f\n",
                    k,filters[k].getFilterString(),(int) topology.getBinContent(k),topology.getBinContent(k)/counter);
        }
        System.out.println();
        return group;
    }
     
     public static void filter(List<String> files, EventModifier modifier, String[] filters){
         PhysicsReaction.filter(files, "filter_output", "REC::Particle", modifier, filters);
     }
     
     public static void filter(List<String> files, String[] filters){
         PhysicsReaction.filter(files, "filter_output", "REC::Particle", PhysicsReaction.FORWARD_CENTRAL, filters);
     }
     
    public static void filter(List<String> files, String pattern, String bank, EventModifier modifier, String[] filters){
        HipoReader r = new HipoReader(files.get(0));
        HipoWriter[]     w = new HipoWriter[filters.length];
        EventFilter[] list = new EventFilter[filters.length];
        
        for(int i = 0; i < filters.length; i++){            
            list[i] = new EventFilter(filters[i]);
            w[i] = HipoWriter.create(String.format("%s_%d.h5",pattern, i), r);
        }
        Event event = new Event();
        
        for(String file : files){
            
            HipoReader reader = new HipoReader(file);
            PhysDataEvent phys = new PhysDataEvent(reader.getBank(bank));
            
            PhysicsReaction fr = new PhysicsReaction("X+:X-:Xn",10.5);
            fr.setDataSource(new HipoReader(file),bank);
            fr.addModifier(modifier);
            int counter = 0;
            while(reader.hasNext()==true){
                reader.nextEvent(event);
                phys.read(event);
                modifier.modify(phys);
                for(int i = 0; i < list.length; i++){
                    if(list[i].isValid(phys)==true) w[i].add(event);
                }
            }                                                                     
            
        }
        for(HipoWriter item : w){item.close();}
    }
    
    public static void filter(List<String> files, String pattern, PhysDataEvent phys, EventModifier modifier, String[] filters){
        HipoReader r = new HipoReader(files.get(0));
        HipoWriter[]     w = new HipoWriter[filters.length];
        EventFilter[] list = new EventFilter[filters.length];
        
        for(int i = 0; i < filters.length; i++){            
            list[i] = new EventFilter(filters[i]);
            w[i] = HipoWriter.create(String.format("%s_%d.h5",pattern, i), r);
        }
        Event event = new Event();
        
        for(String file : files){
            
            try { 
                HipoReader reader = new HipoReader(file);
                
                PhysicsReaction fr = new PhysicsReaction("X+:X-:Xn",10.5);
                fr.setDataSource(new HipoReader(file),phys);
                fr.addModifier(modifier);
                int counter = 0;
                while(reader.hasNext()==true){
                    reader.nextEvent(event);
                    phys.read(event);
                    modifier.modify(phys);
                    for(int i = 0; i < list.length; i++){
                        if(list[i].isValid(phys)==true) w[i].add(event);
                    }
                }                                                                     
            } catch (Exception ex){
                System.out.println("???? something went wrong with file : " + file);
            }
        }
        for(HipoWriter item : w){item.close();}
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
