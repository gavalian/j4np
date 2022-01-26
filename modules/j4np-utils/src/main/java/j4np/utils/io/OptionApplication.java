/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

/**
 *
 * @author gavalian
 */
public abstract class OptionApplication {
    
    private OptionStore store = new OptionStore();
    private String    appName = "unknown";
    
    public OptionApplication(String name){
        appName = name;        
    }
    
    public String      getAppName(){
        return appName;
    }
    
    public OptionStore getOptionStore(){ return store;}
    
    public abstract String   getDescription();
    public abstract boolean  execute(String[] args);
}
