/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.ccdb.DatabaseManager;
import j4np.clas12.ccdb.DetectorTools;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class Clas12TranslateService extends DataWorker<HipoReader,Event> {
    
    DatabaseManager manager = new DatabaseManager();
    private boolean keepEvio = false;
    
    @Override
    public boolean init(HipoReader src) {
        return true;
    }

    public void setKeepEvio(boolean flag){keepEvio = flag;}
    
    @Override
    public void execute(Event e) {
        DataBankStore store = DataBankStore.createTranslate();
        e.read(store.header, 42,1);
        e.read(store.timeStamp, 42,2);
        store.tdcNode.setRows(0);
        store.tdcNode.setRows(0);
        store.adcNode.setRows(0);
        //e.scanShow();
        this.translateTDC(e, store);
        this.translateADC(e, store);
        if(this.keepEvio==true){
            
            int position = e.scan(1, 11);
            //e.scanShow();
            //System.out.println(" position = " + position);
            if(position>8) {
                Node n = e.read(1, 11);
                e.reset();
                //System.out.println("writing evio");
                e.write(n);
                //e.scanShow();
            } else e.reset();
        } else {
            e.reset();
        }
        e.write(store.header);
        e.write(store.timeStamp);
        e.write(store.tdcNode);
        e.write(store.adcNode);
    }
    
    public void translateTDC(Event event , DataBankStore store){
        int run = store.header.getInt(0, 0);
        if(run<10) {
            //System.out.println("[decoder] >> warning unknown run number " + run);
            return;
        }
        DatabaseManager.DecoderDatabase db = manager.get(run);
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
                        store.tdcCache.putByte(  4, r, (byte) (address[4])); // was +1 before
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
                
    }
    
    public void translateADC(Event event , DataBankStore store){
        int run = store.header.getInt(0, 0);
        if(run<10) {
            //System.out.println("[decoder] >> warning unknown run number " + run);
            return;
        }
        DatabaseManager.DecoderDatabase db = manager.get(run);
        event.scanLeafs(store.index);
        store.adcNode.setRows(0);
        //event.read(node, run, run);
        for(int i = 0; i < store.index.getRows(); i++){
            int group = store.index.getInt(0, i);
            int  item = store.index.getInt(1, i);
            int   pos = store.index.getInt(2, i);
            int[] address = new int[5];
            if(item==22){ 
                event.read(store.adcCache, pos);
                int rows = store.adcCache.getRows();
                for(int r = 0; r < rows; r++){
                    int crate = store.adcCache.getInt(1, r);
                    int  slot = store.adcCache.getInt(2, r);
                    int chann = store.adcCache.getInt(3, r);
                    long key = DetectorTools.hardwareEncoder(crate, slot, chann);
                    if(db.crateMap.containsKey(key)==true){
                        long value = db.crateMap.get(key);
                        DetectorTools.softwareDecoder(value, address);
                        
                        store.adcCache.putByte(  0, r, (byte) address[0]);
                        store.adcCache.putByte(  1, r, (byte) address[1]);
                        store.adcCache.putByte(  2, r, (byte) address[2]);
                        store.adcCache.putShort( 3, r, (byte) address[3]);
                        store.adcCache.putByte(  4, r, (byte) (address[4]));
                    }
                }                
                //event.remove(group, item);
                store.adcCache.setGroup(group);
                store.adcCache.setItem(32);
                store.adcNode.copyRows(store.adcCache,0,store.adcCache.getRows());
                //event.replace(group, 21, store.tdcCache);
                //event.write(store.tdcCache);
            }
            
        }
                
    }
    
}
