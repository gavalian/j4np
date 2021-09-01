/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;

/**
 *
 * @author gavalian
 */
public class ClasDebug {
    public static void main(String[] args){
        
        HipoChain reader = new HipoChain();
        
        //reader.addFile("/Users/gavalian/Work/DataSpace/ml/rec_clas_005038.evio.00055-00059.hipo");
        reader.addDir("/Users/gavalian/Work/DataSpace/ml","*rec_clas*00055*");
        reader.open();
        Event event = new Event();
        
        int counter = 0;
        int counter_remove = 0 ;
        //Bank b = reader.getBank("REC::Particle");
        BankStore store = new BankStore();
        //store.add("REC::Particle");
        store.add("REC::Particle","REC::Calorimeter",
                "REC::Scintillator","REC::Traj");
        
        store.init(reader);
        
        long nanoTimeRemove = 0L;
        long nanoTimeRead = 0L;
        
        while(reader.hasNext()==true){
            counter++;
            reader.nextEvent(event);
            
            long then = System.nanoTime();
            for(int i = 0; i < store.getBanks().size(); i++){
                event.remove(store.getBanks().get(i).getSchema());
                counter_remove++;
            }
            long now = System.nanoTime();
            nanoTimeRemove += now - then;
            counter++;
        }
        double miliseconds = nanoTimeRemove*1e-6;
        System.out.printf(" events %8d, remove = %8d, time = %12.6f ms, per remove = %12.6f\n",
                counter,counter_remove, miliseconds, miliseconds/counter_remove);
    }
}
