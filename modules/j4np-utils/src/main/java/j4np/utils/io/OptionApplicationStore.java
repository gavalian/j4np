/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class OptionApplicationStore {
    
    Map<String,OptionApplication> optionsApps = new HashMap<>();
    OptionStore optionsStore = null; //new OptionStore();
    
    public OptionApplicationStore(String name){
        optionsStore = new OptionStore(name);
    }
    
    public OptionApplicationStore addApplication(OptionApplication app){
        optionsApps.put(app.getAppName(), app); return this;
    }
    
    public void init(){
        for(Map.Entry<String,OptionApplication> entry : optionsApps.entrySet()){
            optionsStore.addCommand(entry.getValue().getAppName(), entry.getValue().getDescription());
            OptionParser parser = optionsStore.getOptionParser(entry.getKey());
            //entry.getValue().init(parser);
        }
    }
    
    public void run(String[] args){
        optionsStore.parse(args);
        String command = optionsStore.getCommand();
        if(optionsApps.containsKey(command)==true){
            OptionApplication app = optionsApps.get(command);
            //app.run(optionsStore.getOptionParser(command));
        } else {
            System.out.println("\n>>> application error : unknown command ["+command+"]");
        }
    }
}
