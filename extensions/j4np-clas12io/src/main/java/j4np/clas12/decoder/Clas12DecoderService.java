/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.ccdb.DatabaseManager;
import j4np.clas12.ccdb.DatabaseProvider;
import j4np.clas12.ccdb.DetectorTools;
import j4np.clas12.ccdb.DatabaseManager.DecoderDatabase;
import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataNodeCallback;
import j4np.data.base.DataWorker;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class Clas12DecoderService extends DataWorker<HipoReader,Event> {
    
    public boolean keepEvio = false;
    //protected EvioNodeCallbackStore callbackstore = new EvioNodeCallbackStore();
    /*
    Map<Long,Long> crateMap = new HashMap<>();
    Map<Long,Long>  fadcMap = new HashMap<>();
    
    Map<Integer,String> detectorTables = new HashMap<>();
    
    Map<Integer,String> fadcTables = new HashMap<>();
    //String[] mapTables = new String[]{"/daq/tt/ec","/daq/tt/ftof","/daq/tt/dc"};
    int run = 10;
    */
    
    DatabaseManager manager = new DatabaseManager();
    
    public Clas12DecoderService(){
        //callbackstore.getDecoders().add(new GenericDecoder());
       
    }
    
    public void setKeepEvio(boolean flag){keepEvio = flag;}
    
    @Override
    public boolean init(HipoReader r) {
        System.out.println(":::: emtpy initalizer"); return true;
    }

    public void initialize(){
         
    }
    
    @Override
    public void execute(Event t) {
        //System.out.println("-- running an event with size = " + t.getEventBufferSize());
        int position = t.scan(1, 11);
        if(position<8) return;
        //System.out.println("-------- decoding an event ----");
        Node n = t.read(1, 11);
        byte[] data = n.getByte();
        EvioEvent event = new EvioEvent(data);
        EvioNodeCallbackStore callbackstore = new EvioNodeCallbackStore();
        callbackstore.getDecoders().add(new GenericDecoder());
        //System.out.println(" EVIO EVENT LENGTH = " + event.bufferLength());
        if(keepEvio==false) t.reset();
        DataBankStore store = DataBankStore.createDecoder();
        event.setCallback(callbackstore);
        callbackstore.store = store;
        callbackstore.hipoEvent = t;
        callbackstore.store.timeStamp.setRows(0);
        event.scan();
        
        t.write(store.header);
        t.write(store.timeStamp);
    }
    
    @Override
    public void execute(DataFrame<Event> tf) {
        //System.out.println(" processing data frame with size = " + tf.getList().size());  
        DataBankStore store = DataBankStore.createDecoder();
        EvioNodeCallbackStore callbackstore = new EvioNodeCallbackStore();
        callbackstore.getDecoders().add(new GenericDecoder());
        for(int i = 0; i < tf.getCount(); i++){
            Event t = (Event) tf.getEvent(i);
            int position = t.scan(1, 11);
            if(position>8){
                store.reset();
                Node n = t.read(1, 11);
                byte[] data = n.getByte();
                EvioEvent event = new EvioEvent(data);
                event.setCallback(callbackstore);
                callbackstore.store = store;
                callbackstore.hipoEvent = t;
                callbackstore.store.timeStamp.setRows(0);
                if(keepEvio==false) t.reset();
                event.scan();
                t.write(store.header);
                t.write(store.timeStamp);
            }
        }
    }
    
    public static class EvioNodeCallbackStore implements DataNodeCallback {
        
        public List<EvioNodeDecoder>  decoders = new ArrayList<>();
        EvioNode node = null;
        
        public DataBankStore     store = null;
        public Event         hipoEvent = null;
        
        public EvioNodeCallbackStore(){
            node = new EvioNode(128*1024);
        }
        
        public List<EvioNodeDecoder> getDecoders(){ return decoders;}
        public void reset(){ 
            for(EvioNodeDecoder d : decoders) d.reset();
        }
        
        
        @Override
        public void apply(DataEvent event, int position, int[] identification) {
            event.getAt(node, position);
            for(EvioNodeDecoder d : decoders){
                try{ d.decode(node, identification, store, hipoEvent); } catch (Exception e) { }
            }
        } 
    }        
}
