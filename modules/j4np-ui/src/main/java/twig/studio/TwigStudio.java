/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import twig.data.DataSet;
import twig.data.TDirectory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class TwigStudio {
    
    private static TwigStudio  studioInstance = new TwigStudio();    
    
    private Map<Long,DataSet>    dataSetStore = new HashMap<>();
    private StudioWindow         studioWindow = null;
    
    private long   twigDataSetCounter = 0L;
    private List<TGCanvas>       studioCanvas = new ArrayList<>();    
    private TDirectory       defaultDirectory = new TDirectory();
    
    public TwigStudio(){
        
    }
    
    public long getNextUniqueId(){
        twigDataSetCounter += 100;
        return twigDataSetCounter;
    }
    
    public void addDataSet(long uid, DataSet ds){
        if(dataSetStore.containsKey(uid)){
            System.out.println("[twig-studio] >>>> replacing data set with uid = " + uid);
        }
        dataSetStore.put(uid, ds);
    }
    
    public TDirectory dir(){ return this.defaultDirectory;}
    
    public TGCanvas getCanvas(){
        if(studioCanvas.size()==0){ studioCanvas.add(new TGCanvas(650,550));}
        return studioCanvas.get(0);
    }
    
    public DataSet getDataSet(long uid){
        return dataSetStore.get(uid);
    }
            
    public static TwigStudio getInstance(){ return TwigStudio.studioInstance;}
    
    
}
