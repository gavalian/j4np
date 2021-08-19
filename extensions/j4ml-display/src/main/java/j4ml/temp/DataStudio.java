/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.temp;

import java.util.HashMap;
import java.util.Map;
import org.jlab.groot.data.IDataSet;
import org.jlab.groot.data.TDirectory;
import org.jlab.jnp.groot.graphics.TDataCanvas;
import org.jlab.jnp.groot.settings.GRootColorPalette;

/**
 *
 * @author gavalian
 */
public class DataStudio {
    
    public static DataStudio dataStudio = new DataStudio();
    
    
    public static DataStudio getInstance(){ return dataStudio;}
    
    private Map<String,TDataCanvas> canvasMap = new HashMap<>();
    //private TDirectory        studioDirectory = new TDirectory();
    private DataDirectory<IDataSet>  studioDirectory = new DataDirectory<IDataSet>();
    
    public DataStudio(){
        studioDirectory.setName("studio");
        studioDirectory.addDirectory("graphs");
        studioDirectory.addDirectory("vectors");
        studioDirectory.addDirectory("histograms");
        GRootColorPalette.getInstance().setColorScheme("gold10");
    }

    public TDataCanvas getDefaultCanvas(){
        if(canvasMap.containsKey("default")==false){
            canvasMap.put("default", new TDataCanvas());
        }
        return canvasMap.get("default");
    }    
    
    public DataDirectory  getDirectory(){ return studioDirectory;}
    
}
