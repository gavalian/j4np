/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.physics.EventModifier;
import j4np.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 */
public class EventModifierStore {
    
    
    
    public static class EventModifierSimulation implements EventModifier {
        @Override
        public void modify(PhysicsEvent event) {
            
        }        
    }
    
    public static class EventModifierForward implements EventModifier {
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
    }
    
    public static class EventModifierForwardCentral implements EventModifier {
        @Override
        public void modify(PhysicsEvent event) {
             int counter = event.count();
                for(int i = 0; i < counter ; i++){
                    int status = event.status(i);
                    if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                        event.status(i, 1);
                    } else { 
                        if(Math.abs(status)>=3000&&Math.abs(status)<4000){
                            
                        } else {
                            event.status(i, -1);
                        }
                    }                                        
                }
        }
    }
}
