/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.temp;

import java.util.HashMap;
import java.util.Map;
import org.jlab.groot.data.TDirectory;
import org.jlab.jnp.groot.graphics.TDataCanvas;

/**
 *
 * @author gavalian
 */
public class DataStudio {
    
    public static DataStudio dataStudio = new DataStudio();
    
    
    public static DataStudio getInstance(){ return dataStudio;}
    
    private Map<String,TDataCanvas> canvasMap = new HashMap<>();
    private TDirectory        studioDirectory = new TDirectory();
    
    public DataStudio(){
        studioDirectory.mkdir("studio");
        studioDirectory.mkdir("studio/vectors");
        studioDirectory.mkdir("studio/graphs");
        studioDirectory.mkdir("studio/histograms");        
    }

    public TDataCanvas getDefaultCanvas(){
        if(canvasMap.containsKey("defualt")==false){
            canvasMap.put("default", new TDataCanvas());
        }
        return canvasMap.get("default");
    }    
    
    public TDirectory  getDirectory(){ return studioDirectory;}
    
}
