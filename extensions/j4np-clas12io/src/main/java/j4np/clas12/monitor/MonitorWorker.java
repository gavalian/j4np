/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.monitor;

import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import java.util.ArrayList;
import java.util.List;
import twig.data.DataGroup;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public abstract class MonitorWorker {
    protected TDirectory dir = new TDirectory();
    protected String workerName = "generic";
    protected List<DataGroup> dataGroups = new ArrayList<>();
    protected SchemaFactory factory = new SchemaFactory();
    
    public MonitorWorker(String name){ 
        workerName = name;
        factory.initFromDirectory(System.getenv("CLAS12DIR")+"/etc/bankdefs/hipo4");
    }
    
    public List<DataGroup> getGroups(){ return this.dataGroups;}
    public String getName(){ return workerName;}
    
    abstract void process(Event e);
        
}
