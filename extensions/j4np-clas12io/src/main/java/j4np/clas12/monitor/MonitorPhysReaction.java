/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.monitor;

import j4np.graphics.CanvasLayout;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.physics.EventFilter;
import j4np.physics.EventModifier;
import j4np.physics.LorentzVector;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;
import j4np.physics.data.PhysDataEvent;
import j4np.physics.PhysicsEvent;
import java.util.ArrayList;
import java.util.List;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class MonitorPhysReaction  extends MonitorWorker {
    
    PhysDataEvent physEvent = null;
    EventModifier modifier = PhysicsReaction.FORWARD_ONLY;
    Bank[]    banks = null;
    
    List<VectorOperator> vops = new ArrayList<>();
    List<EventFilter> filters = new ArrayList<>();
    
    public MonitorPhysReaction(){
        super("Physics Reaction");
        banks = factory.getBanks(new String[]{"REC::Particle","REC::Track"}, 32);
        init();
    }
    
    private void init(){
        CanvasLayout layout = new CanvasLayout();
        layout.addColumn(0.0, 0.4, 3);
        layout.addColumn(0.4, 0.3, 6);
        layout.addColumn(0.7, 0.3, 6);
        
        DataGroup group = new DataGroup("Physics",layout);        
        group.setRegionAttributes("mt=10,mb=50,fc=#FAF5ED");
        H2F hwf  = new H2F( "hWf",120,0.6,1.8,60,-3.14,3.14);
        H2F hq2f = new H2F("hQ2f",120,0.6,1.8,80,0.0,4.0);
        group.add(hwf, 0, "F");
        group.add(hq2f, 1, "F");
        group.add(hwf, 2, "F");
        
        for(int i = 3; i < 15; i++){ 
            group.add( H1F.book(
                    String.format("vertex_%d",i+1) ,
                    String.format("v:vertex (sector %d):counts",(i)%6+1),
                    "fc=72",120,0.4,1.8)
                    , i, "");
        }
        this.dataGroups.add(group);
        LorentzVector b = LorentzVector.withPxPyPzM(0, 0, 6.4, 0.0005);
        LorentzVector t = LorentzVector.withPxPyPzM(0, 0, 0.0, 0.938);
        LorentzVector cm = LorentzVector.from(b).add(t);
        
        VectorOperator v1 = new VectorOperator(cm,"-[11]-[211]");
        VectorOperator v2 = new VectorOperator(cm,"-[11]-[211]-[-211]");
        VectorOperator v3 = new VectorOperator(cm,"-[11]");
        VectorOperator v4 = new VectorOperator(new LorentzVector(), "[11]");
        VectorOperator v5 = new VectorOperator(b, "-[11]");
        vops.add(v2); vops.add(v1); vops.add(v3); vops.add(v4); vops.add(v5);
        
        filters.add(new EventFilter("11:211:X+:X-:Xn"));
        filters.add(new EventFilter("11:211:-211:X+:X-:Xn"));
        filters.add(new EventFilter("11:X+:X-:Xn"));
    }
    
    @Override
    void process(Event e) {
        e.read(banks);
        if(physEvent==null) physEvent = new PhysDataEvent(banks[0]);
        
        int sector = getSector(banks[0],banks[1]);
        if(sector>0){
            physEvent.read(e);
            //System.out.println(" doing analysis sector = " + sector);
            modifier.modify(physEvent);
            if(filters.get(0).isValid(physEvent)==true){
                //System.out.println("\t filter 0 is true ");
                vops.get(0).apply(physEvent);
                //System.out.println(vops.get(0).vector().mass());
                ((H1F) getGroups().get(0).getData().get(sector-1+3)).fill(vops.get(0).vector().mass());
            }
            if(filters.get(1).isValid(physEvent)==true){
                //System.out.println("\t filter 1 is true ");
                vops.get(1).apply(physEvent);
                //System.out.println("operator 1 is : " + vops.get(0).vector().mass());
                ((H1F) getGroups().get(0).getData().get(sector-1+9)).fill(vops.get(1).vector().mass());
            }
            
            if(filters.get(2).isValid(physEvent)==true){
                //System.out.println("\t filter 1 is true ");
                vops.get(2).apply(physEvent);
                vops.get(3).apply(physEvent);
                vops.get(4).apply(physEvent);
                //System.out.println("operator 1 is : " + vops.get(2).vector().mass() + " " + vops.get(3).vector().phi());
                ((H2F) getGroups().get(0).getData().get(0)).fill(vops.get(2).vector().mass(),vops.get(3).vector().phi());
                ((H2F) getGroups().get(0).getData().get(1)).fill(vops.get(2).vector().mass(),-vops.get(4).vector().mass());
            } 
        }
    }
    
    public int getSector(Bank particle, Bank track){
        int pid = particle.getInt(0, 0);
        int  st = particle.getInt("status", 0);
        st = Math.abs(st);
        if(pid==11&&st>2000&&st<3000) return getSector(track,0);
        return -1;
    }
    
    public int getSector(Bank b, int order){
        for(int i = 0 ; i < b.getRows(); i++){
            if(b.getInt("pindex",i) == order ) return b.getInt("sector", i);
        }
        return -1;
    }
}
