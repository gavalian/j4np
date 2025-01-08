/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.data.base;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class DataActorStream {
    
    protected List<DataActor> actors = new ArrayList<>();
    protected DataSource dataSource = null;
    protected DataSync    dataSync = null;
    
    public DataActorStream addActor(DataActor actor){ actors.add(actor);return this;}
    public DataActorStream addActor(List<DataActor> actor){ actors.addAll(actor);return this;}
    
    public DataActorStream setSource(DataSource src){
        this.dataSource = src; return this;
    }
    
    public DataActorStream setSync(DataSync sync){
        this.dataSync = sync; return this;
    }
    
    public void run(){
        
        for(int t = 0; t < actors.size(); t++) { 
            actors.get(t).setSource(dataSource);
            actors.get(t).setSync(dataSync);
            actors.get(t).start();
        }
        
        int active = 0;
        boolean keep = true;
        long startTime = System.currentTimeMillis();
        while(keep){
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataActorStream.class.getName()).log(Level.SEVERE, null, ex);
            }

            active = 0;
            for(int t = 0; t < actors.size(); t++) 
                if(actors.get(t).isAlive()==true){ 
                    active++;
                } else {
                    System.out.printf("actor-stream: actor # %d , processed %12d, finished time = %d\n",t+1,actors.get(t).eventsProcessed,actors.get(t).executionTime());
                }

            if(active==0) keep = false;
        }
        long endTime = System.currentTimeMillis();
        long total = 0L;
        if(this.dataSync!=null) dataSync.close();
        for(int i = 0; i < this.actors.size(); i++){
            total += actors.get(i).eventsProcessed;
            if(this.actors.get(i).getBenchmark()>0)
                this.actors.get(i).showBenchmark();
        }
        double time = (endTime-startTime);
        time = time/1000.0;
        System.out.printf("stream:: processed = %d, actors = %d, rate = %4f\n",total,actors.size(),((double)total)/time);
    }
}
