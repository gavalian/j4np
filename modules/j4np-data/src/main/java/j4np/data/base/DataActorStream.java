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
        while(keep){
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataActorStream.class.getName()).log(Level.SEVERE, null, ex);
            }

            for(int t = 0; t < actors.size(); t++) 
                if(actors.get(t).isAlive()==true){ 
                    active++;
                } else {
                    System.out.printf("actor-stream: actor # %d finished time = %d\n",t+1,actors.get(t).executionTime());
                }
            if(active==0) keep = false;
        }
    }
}
