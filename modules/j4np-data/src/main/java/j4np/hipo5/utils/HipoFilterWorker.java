/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.utils;

import j4np.data.base.DataFrame;
import j4np.data.base.DataStream;
import j4np.data.base.DataWorker;

import j4np.hipo5.data.Event;
import j4np.hipo5.data.EventFilter;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;

/**
 *
 * @author gavalian
 */
public class HipoFilterWorker extends DataWorker<HipoReader,Event> {

    
    private EventFilter eventFilter = new EventFilter();
    
    public HipoFilterWorker(String filter){
        this.addBankList(filter);
    }
    
    public final void addBankList(String filter){
        eventFilter.addBankList(filter);
    }
    
    public final void addBankExistList(String filter){
        eventFilter.addBankExistList(filter);
    }
    
    @Override
    public boolean init(HipoReader src) {
        this.eventFilter.init(src.getSchemaFactory());
        //System.out.printf("***** ",);
        return true;
    }
    
    @Override
    public void execute(Event e) {
        
        //eventFilter.reduceEvent(e);
        //event.show();
    }
    
    public static void executeProgram(String input, String output){
        
        DataStream<HipoReader,HipoWriter,Event>  stream = new DataStream<>();
        HipoReader r = new HipoReader(input);
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().copy(r.getSchemaFactory());
        w.open(output);
        
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < 128; i++){ frame.addEvent(new Event());}
        
        HipoFilterWorker worker = new HipoFilterWorker("TimeBased.*");
        stream.source(r).withOutput(w).frame(frame).consumer(worker);
        
        stream.threads(1);
        stream.run();
        stream.show();
    }
}
