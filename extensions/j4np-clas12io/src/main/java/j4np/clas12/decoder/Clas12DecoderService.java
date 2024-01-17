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
    
    @Override
    public boolean init(HipoReader r) {
        System.out.println(":::: emtpy initalizer"); return true;
    }

    public void initialize(){
         
    }
    
    @Override
    public void execute(Event t) {
        int position = t.scan(1, 11);
        if(position<8) return;
        
        Node n = t.read(1, 11);
        byte[] data = n.getByte();
        EvioEvent event = new EvioEvent(data);
        EvioNodeCallbackStore callbackstore = new EvioNodeCallbackStore();
        callbackstore.getDecoders().add(new GenericDecoder());
        //System.out.println(" EVIO EVENT LENGTH = " + event.bufferLength());
        t.reset();
        DataBankStore store = new DataBankStore();
        event.setCallback(callbackstore);
        callbackstore.store = store;
        callbackstore.hipoEvent = t;
        callbackstore.store.timeStamp.setRows(0);
        event.scan();
        
        t.write(store.header);
        //translate(t,store);
        //translate22(t,store);        
        //t.scanShow();
        //t.reset();        
        //t.write(store.tdcNode);
        //t.write(store.timeStamp);
        //store.timeStamp.print();
        //t.write(store.adcNode);
        //t.write(store.header);
        //t.write(store.);
    }
    
    public void translate(Event event , DataBankStore store){
        int run = store.header.getInt(0, 0);
        if(run<10) {
            System.out.println("[decoder] >> warning unknown run number " + run);
            return;
        }
        DecoderDatabase db = manager.get(run);
        event.scanLeafs(store.index);
        store.tdcNode.setRows(0);
        //event.read(node, run, run);
        for(int i = 0; i < store.index.getRows(); i++){
            int group = store.index.getInt(0, i);
            int  item = store.index.getInt(1, i);
            int   pos = store.index.getInt(2, i);
            int[] address = new int[5];
            if(item==21){ 
                event.read(store.tdcCache, pos);
                int rows = store.tdcCache.getRows();
                for(int r = 0; r < rows; r++){
                    int crate = store.tdcCache.getInt(1, r);
                    int  slot = store.tdcCache.getInt(2, r);
                    int chann = store.tdcCache.getInt(3, r);
                    long key = DetectorTools.hardwareEncoder(crate, slot, chann);
                    if(db.crateMap.containsKey(key)==true){
                        long value = db.crateMap.get(key);
                        DetectorTools.softwareDecoder(value, address);
                        
                        store.tdcCache.putByte(  0, r, (byte) address[0]);
                        store.tdcCache.putByte(  1, r, (byte) address[1]);
                        store.tdcCache.putByte(  2, r, (byte) address[2]);
                        store.tdcCache.putShort( 3, r, (byte) address[3]);
                        store.tdcCache.putByte(  4, r, (byte) (address[4]+1));
                    }
                }
                
                //event.remove(group, item);
                store.tdcCache.setGroup(group);
                store.tdcCache.setItem(31);
                store.tdcNode.copyRows(store.tdcCache,0,store.tdcCache.getRows());
                //event.replace(group, 21, store.tdcCache);
                //event.write(store.tdcCache);
            }
            
        }
        
        //event.write(store.tdcNode);
        //store.index.print();
    }
    
    public void translate22(Event event , DataBankStore store){
        int run = store.header.getInt(0, 0);
        if(run<10) {
            System.out.println("[decoder] >> warning unknown run number " + run);
            return;
        }
        DecoderDatabase db = manager.get(run);
        event.scanLeafs(store.index);
        store.adcNode.setRows(0);
        //event.read(node, run, run);
        for(int i = 0; i < store.index.getRows(); i++){
            int group = store.index.getInt(0, i);
            int  item = store.index.getInt(1, i);
            int   pos = store.index.getInt(2, i);
            int[] params = new int[5];

            if(item==23){ 
                System.out.println(" DECODING ADC " + group);
                event.read(store.adcCachePulse, pos);
                int rows = store.adcCachePulse.getRows();
                
                int npos = 0;
                while(npos<rows){
                    int count = store.adcCachePulse.getInt(0, npos);
                    int crate = store.adcCachePulse.getInt(0,npos+1);
                    int slot  = store.adcCachePulse.getInt(0,npos+2);
                    int chan  = store.adcCachePulse.getInt(0,npos+3);
                    npos += 4;
                    System.out.printf("%d:%d:%d (%d) \n",crate,slot,chan, count);
                    long key = DetectorTools.hardwareEncoder(crate, slot, chan);
                    if(db.fadcMap.containsKey(key)==true){
                        long value = db.fadcMap.get(key);
                        DetectorTools.fadcDecoder(value, params);
                        System.out.printf("\n found parameters %s\n",Arrays.toString(params));
                    }
                    npos += count;
                }
                                //event.replace(group, 21, store.tdcCache);
                //event.write(store.tdcCache);
            }
            
        }
        
        //event.write(store.tdcNode);
        //store.index.print();
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
