/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.io;

import j4np.hipo5.data.CompositeNode;

/**
 *
 * @author gavalian
 */
public class Clas12NodeUtils {
    
    public static CompositeNode createNodeTDC(int group, int item, int maxRows){
        return new CompositeNode(group,item,"bbbsbil",maxRows);
    }
    /*
    "entries": [
            {"name":"run",          "type":"I", "info":"RUN number from CODA or GEMC"},
            {"name":"event",        "type":"I", "info":"Event number"},
            {"name":"unixtime",     "type":"I", "info":"Unix time (seconds)"},
            {"name":"trigger",      "type":"L", "info":"trigger bits"},
            {"name":"timestamp",    "type":"L", "info":"time stamp from Trigger Interface (TI) board (4 nanoseconds)"},
            {"name":"type",         "type":"B", "info":"type of the run"},
            {"name":"mode",         "type":"B", "info":"run mode"},
            {"name":"torus",        "type":"F", "info":"torus setting relative value(-1.0 to 1.0)"},
            {"name":"solenoid",     "type":"F", "info":"solenoid field setting (-1.0 to 1.0)"}
        ]
    */
    public static CompositeNode createHeaderNode(int group, int item, int maxRows){
        return new CompositeNode(group,item,"iiillbbff",maxRows);
    }
    
    public static CompositeNode createNodeADC(int group, int item, int maxRows){
        return new CompositeNode(group,item,"bbbsbifs",maxRows);
    }
    
    public static CompositeNode createNodeADCPulse(int group, int item, int maxRows){
        return new CompositeNode(group,item,"s",maxRows);
    }
    
    public static CompositeNode createIndexNode(int group, int item, int maxRows){
        return new CompositeNode(group,item,"iii",maxRows);
    }
}
