/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.clas12.ccdb.DatabaseManager;
import j4np.clas12.ccdb.DatabaseManager.DecoderDatabase;
import j4np.clas12.ccdb.DatabaseManager.ParameterDatabase;
import j4np.clas12.ccdb.DetectorTools;
import j4np.clas12.decoder.PulseFitter.PulseFitterConfig;
import j4np.clas12.decoder.PulseFitter.PulseFitterParams;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class Clas12FitterService extends DataWorker<HipoReader,Event> {

    
    DatabaseManager manager = new DatabaseManager();

    @Override
    public boolean init(HipoReader src) {
        return true;
    }

    @Override
    public void execute(Event e) {
        
        DataBankStore store = DataBankStore.createFitter();
        
        e.read(store.header, 42,1);
        
        int run = store.header.getInt(0, 0);
        if(run<10) {
            //System.out.println("[decoder] >> warning unknown run number " + run);
            return;
        }
        
        e.scanLeafs(store.index);
        
        //store.adcNode.setRows(0);
        store.cache.setRows(0);
        DecoderDatabase pdb = manager.get(run);
        
        PulseFitterConfig config = new PulseFitterConfig();
        PulseFitterParams params = new PulseFitterParams();
        SinglePulseFitter fitter = new SinglePulseFitter();
        int[]   pars = new int[5];
        
        //System.out.println("--------------------");
        //event.read(node, run, run);
        for(int i = 0; i < store.index.getRows(); i++){
            int group = store.index.getInt(0, i);
            int  item = store.index.getInt(1, i);
            int   pos = store.index.getInt(2, i);
            int[] address = new int[5];

                if(item==23&&pos>=16){ 
                 try {
                store.cache.setRows(0);
                e.read(store.pulse, pos); 
                
                //System.out.printf("%d %d %d length = %d\n",group,item,pos,pos);
                int nrows = store.pulse.getRows();
                
                int position = 0;
                while(position<nrows){
                    int nsamples = store.pulse.getInt(0, position);
                    int crate = store.pulse.getInt(0, position+1);
                    int slot = store.pulse.getInt(0, position+2);
                    int chann = store.pulse.getInt(0, position+3);                        
                    long key = DetectorTools.hardwareEncoder(crate, slot, chann);
                    //System.out.printf("\t key = %d (%x)\n",key,key);
                    if(pdb.fadcMap.containsKey(key)==true){
                        long value = pdb.fadcMap.get(key);
                        //System.out.printf("\t VALUE = %d (%x)\n",value,value);
                        DetectorTools.fadcDecoder(value, pars);
                        //config.NSA = pars[2];
                        //config.NSB = pars[3];
                        
                        config.NSA = pars[3];
                        config.NSB = pars[2];
                        
                        config.TET = pars[4];
                        config.pedestal = 0.0;//((double) pars[1])/10.0;
                        //System.out.println(config);
                        fitter.fit(params, config, store.pulse, position+4, nsamples);
                        //System.out.println(params);
                        
                        //if(params.position>0&&params.time>0.0){
                        if(params.pedestal>0.0){
                            int crows = store.cache.getRows();
                            store.cache.putByte(  1, crows, (byte) crate);
                            store.cache.putByte(  2, crows, (byte) slot);
                            store.cache.putShort( 3, crows, (short) chann);
                            store.cache.putInt(   5, crows,  params.ADC);
                            store.cache.putFloat( 6, crows, (float) params.time);
                            store.cache.putShort( 7, crows, (short) params.pedestal);
                            store.cache.setRows(crows+1);
                        }
                    }
                                                        
                    position += 4+nsamples;
                                   
                }
                store.cache.setGroup(group);
                store.cache.setItem(22);
                e.write(store.cache);
                 }     catch (Exception ex) {System.out.println(" exception ");
                     System.out.printf("--- fitting node %d, %d ,%d rows = %d\n",group,item,pos, pos);
                     e.scanShow();
                }                                          
            }      
        }
    }
    
}
