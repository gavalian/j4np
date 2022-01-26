/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.analysis.clas12;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.EventModifier;
import j4np.physics.PhysicsEvent;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;

/**
 *
 * @author gavalian
 */
public class EventTopologyFilter {

    PhysicsReaction reaction = null;//new PhysicsReaction();
    
    public EventTopologyFilter(String filter, double energy){
        reaction = new PhysicsReaction(filter,energy);
    }
    
    public PhysicsReaction getReaction(){ return reaction;}
    
    public static void filter(String filename){
        
        HipoReader r = new HipoReader(filename);
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().copy(r.getSchemaFactory());
        w.open(filename+".filtered.h5");
        EventTopologyFilter f = new EventTopologyFilter("11:211:-211:X+:X-:Xn",10.6);
        f.getReaction().addVector(f.getReaction().getVector(), "-[11]-[211]-[-211]");
        f.getReaction().addEntry("mxepipi", 0, VectorOperator.OperatorType.MASS);
        
        f.getReaction().addModifier(new EventModifier(){
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
        
        Event event = new Event();
        
        
        
        while(r.hasNext()==true){
            r.nextEvent(event);
            
        }
        
    }
    public static void main(String[] args){
        String filename = args[0];
        
    }
}
