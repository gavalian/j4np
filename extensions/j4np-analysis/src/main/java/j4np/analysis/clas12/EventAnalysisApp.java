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
        getOptionStore().addCommand("-stats", "analyse file and print topology statistics");
        getOptionStore().getOptionParser("-stats").addOption("-b", "REC::Particle", "bank containing particles");
        
        getOptionStore().addCommand("-filter", "filter events according to physics reaction");
        getOptionStore().getOptionParser("-filter").addOption("-r", "e2pi", "filter events e-pi+pi-");
        getOptionStore().getOptionParser("-filter").addOption("-b", "rec::event", "bank with particle information");
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
    
    
    @Override
    public boolean execute(String[] args) {
        
        OptionStore store = getOptionStore();
        
        store.parse(args);
        
        if(store.getCommand().compareTo("-stats")==0){
            String bank = store.getOptionParser("-stats").getOption("-b").stringValue();
            this.printStatistics(store.getOptionParser("-stats").getInputList().get(0), bank);
        }
        
        if(store.getCommand().compareTo("-filter")==0){
            String reaction = store.getOptionParser("-filter").getOption("-r").stringValue();
            String     bank = store.getOptionParser("-filter").getOption("-b").stringValue();
            if(reaction.compareTo("e2pi")==0){
                EventTopologyFilter.filter(store.getOptionParser("-filter").getInputList().get(0),bank);
            }
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
