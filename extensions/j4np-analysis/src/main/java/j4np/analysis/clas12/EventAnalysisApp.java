/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.analysis.clas12;

import j4np.data.base.DataFrame;
import j4np.data.base.DataStream;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionStore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class EventAnalysisApp extends OptionApplication {

    public EventAnalysisApp(){
        
        super("analysis");
        
        getOptionStore().addCommand("-ml", "compare ai tracks with conventional tracks");
        getOptionStore().addCommand("-tracks", "print tracking statistics");
        
        getOptionStore().addCommand("-stats", "analyse file and print topology statistics");
        getOptionStore().getOptionParser("-stats").addOption("-b", "REC::Particle", "bank containing particles");
        
        getOptionStore().addCommand("-filter", "filter events according to physics reaction");
        getOptionStore().getOptionParser("-filter").addOption("-r", "e2pi", "filter events e-pi+pi-");
        getOptionStore().getOptionParser("-filter").addOption("-b", "rec::event", "bank with particle information");
        
        getOptionStore().addCommand("-electron", "filter events according to physics reaction");
        getOptionStore().getOptionParser("-electron").addRequired("-o",  "output file name");
        getOptionStore().getOptionParser("-electron").addOption("-b", "REC::Particle", "bank with particle information");
    }
    
    @Override
    public String getDescription() {
        return "Various reconstruction file analysis tools ";
    }

    public void printStatistics(String file, String bank){
        DataStream<HipoReader,HipoWriter,Event> str = new DataStream();
        str.show();
        
        DataFrame<Event>  frame = new DataFrame<>();
        HipoReader       source = new HipoReader();
        EventTopologyStatistics worker = new EventTopologyStatistics(bank);
        
        source.open(file);
        for(int i = 0; i < 8; i++){ frame.addEvent(new Event());}
        str.threads(1);
        str.withSource(source).withFrame(frame).consumer(worker).run();        
        str.show();        
        worker.summary();
    }
    
    public void printTrackStatistics(String file){
        DataStream<HipoReader,HipoWriter,Event> str = new DataStream();
        str.show();
        
        DataFrame<Event>  frame = new DataFrame<>();
        HipoReader       source = new HipoReader();
        TrackingStatistics worker = new TrackingStatistics();
        
        source.open(file);
        for(int i = 0; i < 8; i++){ frame.addEvent(new Event());}
        str.threads(1);
        str.withSource(source).withFrame(frame).consumer(worker).run();        
        str.show();        
        worker.summary();
    }
    
    
    public void printStatisticsAi(String file){
        DataStream<HipoReader,HipoWriter,Event> str = new DataStream();
        str.show();
        
        DataFrame<Event>  frame = new DataFrame<>();
        HipoReader       source = new HipoReader();
        TrackClassifierStats worker = new TrackClassifierStats();
        
        source.open(file);
        for(int i = 0; i < 8; i++){ frame.addEvent(new Event());}
        str.threads(1);
        str.withSource(source).withFrame(frame).consumer(worker).run();        
        str.show();
    }
    
    @Override
    public boolean execute(String[] args) {
        
        OptionStore store = getOptionStore();
        
        store.parse(args);
        
        
        if(store.getCommand().compareTo("-ml")==0){
            //String bank = store.getOptionParser("-ml").getOption("-b").stringValue();
            this.printStatisticsAi(store.getOptionParser("-ml").getInputList().get(0));
        }
        
        if(store.getCommand().compareTo("-stats")==0){
            String bank = store.getOptionParser("-stats").getOption("-b").stringValue();
            this.printStatistics(store.getOptionParser("-stats").getInputList().get(0), bank);
        }
        
        if(store.getCommand().compareTo("-tracks")==0){
            //String bank = store.getOptionParser("-tracks").getOption("-b").stringValue();
            this.printTrackStatistics(store.getOptionParser("-tracks").getInputList().get(0));
        }
        
        if(store.getCommand().compareTo("-filter")==0){
            String reaction = store.getOptionParser("-filter").getOption("-r").stringValue();
            String     bank = store.getOptionParser("-filter").getOption("-b").stringValue();
            if(reaction.compareTo("e2pi")==0){
                EventTopologyFilter.filter(store.getOptionParser("-filter").getInputList().get(0),bank);
            }
        }
        
        if(store.getCommand().compareTo("-electron")==0){
            String   output = store.getOptionParser("-electron").getOption("-o").stringValue();
            String     bank = store.getOptionParser("-electron").getOption("-b").stringValue();
            
            EventTopologyFilter.filterForwardElectron(
                    store.getOptionParser("-electron").getInputList(),output,bank);
            
        }
        
        return true;
    }
    
    
    public static void main(String[] args) {
        
        try {
            Class clazz = Class.forName("j4np.analysis.clas12.EventAnalysisApp");
            
            System.out.println("is instance = " + clazz.isInstance(EventAnalysisApp.class));
            System.out.println("is instance = " + OptionApplication.class.isAssignableFrom(clazz));
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EventAnalysisApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
