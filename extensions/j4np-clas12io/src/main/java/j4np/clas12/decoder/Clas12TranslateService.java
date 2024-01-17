/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.ccdb.DatabaseManager;
import j4np.clas12.ccdb.DetectorTools;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class Clas12TranslateService extends DataWorker<HipoReader,Event> {
    DatabaseManager manager = new DatabaseManager();

    @Override
    public boolean init(HipoReader src) {
        return true;
    }

    @Override
    public void execute(Event e) {
        DataBankStore store = new DataBankStore();
        e.read(store.header, 31,20);
        store.tdcNode.setRows(0);
        this.translateTDC(e, store);
        
        e.reset();
        e.write(store.header);
        e.write(store.tdcNode);
    }
    
    public void translateTDC(Event event , DataBankStore store){
        int run = store.header.getInt(0, 0);
        if(run<10) {
            System.out.println("[decoder] >> warning unknown run number " + run);
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
                
    }
    
}
