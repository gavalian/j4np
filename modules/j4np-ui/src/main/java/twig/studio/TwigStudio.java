/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.util.HashMap;
import java.util.Map;
import twig.data.DataSet;

/**
 *
 * @author gavalian
 */
public class TwigStudio {
    
    private static TwigStudio  studioInstance = new TwigStudio();    
    private Map<Long,DataSet>    dataSetStore = new HashMap<>();
    private StudioWindow         studioWindow = null;
    
    private long   twigDataSetCounter = 0L;
    
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
    
    
    public DataSet getDataSet(long uid){
        return dataSetStore.get(uid);
    }
            
    public static TwigStudio getInstance(){ return TwigStudio.studioInstance;}
    
    
}
