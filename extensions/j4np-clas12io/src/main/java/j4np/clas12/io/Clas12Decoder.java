/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.io;

import j4np.clas12.decoder.GenericDecoder;
import j4np.clas12.decoder.EvioNodeDecoder;
import j4np.clas12.ccdb.DatabaseProvider;
import j4np.clas12.decoder.DataBankStore;
import j4np.data.base.DataEvent;
import j4np.data.base.DataNodeCallback;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.data.evio.EvioNode;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.DataType;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class Clas12Decoder {
    
    protected EvioNodeCallback callback = new EvioNodeCallback();   
    protected EvioNodeCallbackStore callbackstore = new EvioNodeCallbackStore();
    
    
    protected int debugMode = 1;
    protected Map<Long,Long> translation = new HashMap<>();
    
    public Clas12Decoder(){
        
    }
    
    public void decode(EvioEvent event, Event hipo){
        DataBankStore store = new DataBankStore();
        event.setCallback(callbackstore);
        callbackstore.store = store;
        callbackstore.hipoEvent = hipo;
        event.scan();
    }

    public void initialize(){
         List<EvioNodeDecoder>  decoders = callback.getDecoders();
         DatabaseProvider p = new DatabaseProvider(10,"default");
         for(EvioNodeDecoder decoder : decoders){
             decoder.initDecoder(p);             
         }
         
         for(EvioNodeDecoder decoder : decoders){
             decoder.show();
         }
         
         //System.out.printf("LOADED trsnaltion map with entries = %d\n",translation.size());
         
         //translation = Collections.unmodifiableMap(translation);
    }
    
    public static class EvioNodeCallbackStore implements DataNodeCallback {
        
        public List<EvioNodeDecoder>  decoders = new ArrayList<>();
        
        EvioNode node = null;
        
        public DataBankStore     store = null;
        public Event         hipoEvent = null;
        
        public EvioNodeCallbackStore(){
            node = new EvioNode(85*1024);
        }
        
        public List<EvioNodeDecoder> getDecoders(){ return decoders;}
        public void reset(){ 
            for(EvioNodeDecoder d : decoders) d.reset();
        }
        
        
        @Override
        public void apply(DataEvent event, int position, int[] identification) {
            event.getAt(node, position);
            for(EvioNodeDecoder d : decoders){
                try{ d.decode(node, identification, store,hipoEvent); } catch (Exception e) { }
            }
        } 
    }
    
    public static class EvioNodeCallback implements DataNodeCallback {
        public List<EvioNodeDecoder>  decoders = new ArrayList<>();
        EvioNode node = null;
        
        public EvioNodeCallback(){
            node = new EvioNode(85*1024);
        }
        
        public List<EvioNodeDecoder> getDecoders(){ return decoders;}
        public void reset(){ 
            for(EvioNodeDecoder d : decoders) d.reset();
        }
        
        
        @Override
        public void apply(DataEvent event, int position, int[] identification) {
            event.getAt(node, position);
            for(EvioNodeDecoder d : decoders){
                try{ d.decode(node, identification); } catch (Exception e) { }
            }
        } 
    }
    
    public void addDecoder(EvioNodeDecoder decoder){
        callback.getDecoders().add(decoder);
    }
    
    public void addStoreDecoder(EvioNodeDecoder decoder){
        callbackstore.getDecoders().add(decoder);
    }
    
    public void decode(String file , String output, int limit){
        this.decode(Arrays.asList(file),output, limit);
    }
    
    public void evio2hipo(EvioEvent evioEvent, Event hipoEvent){
        int size = evioEvent.getSize()*4;
        Node node = new Node(1,11,DataType.BYTE, size + 8);
        node.putByte(evioEvent.getBuffer().array());
        //hipoEvent.write(node);
    
        if(callback.getDecoders().size()>0){
            callback.reset();
            evioEvent.setCallback(callback);
            evioEvent.scan();
            
            for(EvioNodeDecoder nd : callback.getDecoders()){ 
                //nd.translate(translation);
                nd.finilize();
                for(CompositeNode cn : nd.getNodes()) {
                    hipoEvent.write(cn);
                }
            }
        }
    }
    
    public void decode(List<String> files, String output, int limit){
        HipoWriter w = new HipoWriter();
        w.setCompressionType(1);
        w.open(output);
        EvioEvent evioEvent = new EvioEvent();
        Event event = new Event();
        int counter = 0;
        long then = System.currentTimeMillis();
        for(int i = 0; i < files.size(); i++){
            EvioFile reader = new EvioFile();
            reader.open(files.get(i));    
            while(reader.hasNext()){
                
                
                counter++;
                if(counter>=limit) break;
                reader.next(evioEvent);
                event.reset();
                //event.scanShow();
                //this.evio2hipo(evioEvent, event);
                //event.scanShow();
                this.decode(evioEvent, event);
                //event.scanShow();
                w.addEvent(event);
            }
        }
        long now = System.currentTimeMillis();
        w.close();
        System.out.printf("processed %d events in %d msec\n",counter, now - then);
    }
    
    public static void main(String[] args){
        
        //String file = "/Users/gavalian/Work/DataSpace/evio/clas_018777.evio.00049";
        String file = "/Users/gavalian/Work/DataSpace/evio/clas_018640.evio.00000";
        Clas12Decoder decoder = new Clas12Decoder();
        
        /*decoder.addDecoder(new TriggerCrateDecoder());
        decoder.addDecoder(new GenericDecoder());
        decoder.addDecoder(new ECALDecoder());
        */
        /*decoder.addDecoder(new ECALDecoder());
        decoder.addDecoder(new DriftChamberDecoder());
        decoder.addDecoder(new TimeOfFlightDecoder());
        decoder.initialize();*/
        decoder.addStoreDecoder(new GenericDecoder());        
        //for(int k = 0; k < 150; k++){
                
        decoder.decode(file, "output_decoded_2.h5",150000);
        //}
        //System.out.println("\n\n\n\n========================");
        //decoder.decode(file, "output_decoded2.h5",150000);
        
    }
}
