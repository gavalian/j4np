/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.JFrame;
import twig.data.DataSet;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDataFactory;
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
    
    private long   twigDataSetCounter = 1000L;
    private List<TGCanvas>       studioCanvas = new ArrayList<>();    
    private TDirectory       defaultDirectory = new TDirectory();
    
    private List<DataSet>       copiedDataset = new ArrayList<>();
    
    private Map<String,TreeProvider> providerStore = 
            new HashMap<>();
    
    public TwigStudio(){
        
    }
    
    public long getNextUniqueId(){
        twigDataSetCounter += 1;
        return twigDataSetCounter;
    }
    
    public void addDataSet(long uid, DataSet ds){
        if(dataSetStore.containsKey(uid)){
            System.out.println("[twig-studio] >>>> replacing data set with uid = " + uid);
        }
        dataSetStore.put(uid, ds);
    }
    
    public void register(String name, TreeProvider tp){
        if(providerStore.containsKey(name)==true){
            System.out.printf(
                    "[TwigStudio] register >>> removing exisint tree provider [%s]\n",
                    name);
        }
        providerStore.put(name, tp);
    }
    
    public Map<String,TreeProvider> getRegistered(){return providerStore;}
    
    public List<DataSet>  getCopyBuffer(){
        return this.copiedDataset;
    }
    
    public TDirectory dir(){ return this.defaultDirectory;}
    
    public TGCanvas getCanvas(){
        if(studioCanvas.size()==0){ studioCanvas.add(new TGCanvas(650,550));}
        return studioCanvas.get(0);
    }
    
    public DataSet getDataSet(long uid){
        return dataSetStore.get(uid);
    }
        
    public static void browser(TreeProvider tp){
        StudioWindow.changeLook();
        
        StudioWindow window = new StudioWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setSize(800, 500);
        window.setVisible(true);
        
        window.getStudioFrame().setTreeProvider(tp);
    }
    
    public static void browser(String filename){
        
        StudioWindow.changeLook();
        
        StudioWindow window = new StudioWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setSize(800, 500);
        window.setVisible(true);
        
        TDirectory dir = new TDirectory();
        dir.read(filename);
        
        window.getStudioFrame().setTreeProvider(dir);
    }
    
    public static TwigStudio getInstance(){ return TwigStudio.studioInstance;}
    
    public static TDirectory getDemoDirectory(){
       TDirectory dir = new TDirectory();
       H1F h1d = TDataFactory.createH1Fs( 5500, 100, 0.0, 1.0, 0.65, 0.1);
       H2F h2d = TDataFactory.createH2F(  45000, 50);
       H1F h1d2 = TDataFactory.createH1Fs(7500, 100, 0.0, 1.0, 0.45, 0.12);
       
       h1d.attr().setTitleX("random gaus");
       h1d.attr().setTitleY("counts");
       
       h1d2.attr().setTitleX("random gaus");
       h1d2.attr().setTitleY("counts");
       
       h2d.attr().setTitleX("random gaus X");
       h2d.attr().setTitleY("random gaus Y");
       
       h1d.setName("gaus1d");
       h2d.setName("gaus2d");
       h1d2.setName("gaus1df");
       h1d2.attr().set("fc=3,fs=2");
       h1d.attr().set("fc=32,lc=2");
       
       dir.add("/demo", h1d);
       dir.add("/demo", h2d);
       dir.add("/demo", h1d2);
       return dir;
    }
    public static void main(String[] args){
        StudioWindow.changeLook();        
        StudioWindow frame = new StudioWindow();
        frame.getStudioFrame().setTreeProvider(TwigStudio.getDemoDirectory());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setVisible(true);
    }
}
