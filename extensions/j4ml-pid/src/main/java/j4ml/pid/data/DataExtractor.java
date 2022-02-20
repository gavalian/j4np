/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.pid.data;

import j4np.data.base.DataFrame;
import j4np.data.base.DataStream;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.Vector3;
import j4np.physics.data.PhysDataEvent;

/**
 *
 * @author gavalian
 */
public class DataExtractor extends DataWorker<HipoReader,Event>{
    
    PhysDataEvent  mcEvent = null;
    PhysDataEvent recEvent = null;
    Bank            mcBank = null;
    Bank            ecBank = null;
    int             mcElec = 0;
    int             mcElecMatched = 0;
    int             mcElecMiss = 0;
    
    int             mcElecMiss3ecal = 0;
    int             mcElecMatched3ecal = 0;
    
    int             mcElecMiss2ecal = 0;
    int             mcElecMatched2ecal = 0;
    
    public int getECALCount(int index, Bank ec){
        int nrows = ec.getRows();
        int count = 0;
        for(int i = 0; i < nrows; i++){
            if(ec.getInt("pindex", i)==index) count++;
        }
        return count;
    }
    
    public int getIndex(int pid, int skip){        
        Vector3  v = new Vector3();
        Vector3 vf = new Vector3();
        //mcBank.show();
        int    index = -1;
        double   min = 5.0;
        
        if(mcBank.getInt(0, 0)==11){
            v.setXYZ(mcBank.getFloat(1, 0), 
                    mcBank.getFloat(2, 0),
                    mcBank.getFloat(3, 0));
            
            mcElec++;
            //mcEvent.vector(v, pid, skip);        
            int nrows = recEvent.count();
            
            for(int i = 0; i < nrows; i++){
                recEvent.vector(vf, i);
                double w = vf.compareWeighted(v);
                if(w<1.0){
                    if(w<min){
                        min = w; index = i;
                    }
                }
                //System.out.printf("%8.4f : %s\n",w,vf.toString());
            }
        }
        
        if(index>=0){ 
            
            int ecount = this.getECALCount(index, ecBank);
            
            if(recEvent.pid(index)==11){
                mcElecMatched++;
                if(ecount==3) mcElecMatched3ecal++;
                if(ecount==2) mcElecMatched2ecal++;
           } else { 
                mcElecMiss++;
                if(ecount==3) mcElecMiss3ecal++;
                if(ecount==2) mcElecMiss2ecal++;
            } 
        }
        //System.out.println(recEvent.toLundString());
        return index;
    }
    
    @Override
    public boolean init(HipoReader src) {
        mcBank = src.getBank("MC::Particle");
        ecBank = src.getBank("REC::Calorimeter");
        mcEvent  = new PhysDataEvent(src.getBank("MC::Particle"));
        recEvent = new PhysDataEvent(src.getBank("REC::Particle"));
        return true;
    }

    @Override
    public void execute(Event e) {
        //System.out.println("========= processing event ==========");
        //mcEvent.read(e);
        recEvent.read(e);
        for(int r = 0; r < recEvent.count(); r++) recEvent.status(r, 1);
        e.read(mcBank); e.read(ecBank);
        
        int index = getIndex(11,0);
        //mcEvent.read(e);
        //recEvent.read(e);
    }
    
    public void show(){
        System.out.printf("\n\n MC E- : %12d, \n REC MATCHED E- %12d\n MISS E- : %12d\n",
                mcElec,mcElecMatched, mcElecMiss);
        
        System.out.printf("REC MATCHED 3 ecal : %12d\n",mcElecMatched3ecal);
        System.out.printf("REC MATCHED 2 ecal : %12d\n",mcElecMatched2ecal);
        System.out.printf("REC MISSED  3 ecal : %12d\n",mcElecMiss3ecal);
        System.out.printf("REC MISSED  2 ecal : %12d\n",mcElecMiss2ecal);
    }
    
    public static void main(String[] args){
        
        String input = "/Users/gavalian/Work/dataspace/pid/rec_epipi_0001_000nA.hipo.filtered.hipo";
        String output = "temp.h5";
        DataStream<HipoReader,HipoWriter,Event>  stream = new DataStream<>();
        HipoReader r = new HipoReader(input);
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().copy(r.getSchemaFactory());
        w.open(output);
        
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < 12; i++){ frame.addEvent(new Event());}
        
        DataExtractor worker = new DataExtractor();
        stream.source(r).frame(frame).consumer(worker);
        
        stream.threads(1);
        stream.run();
        stream.show();
        worker.show();
    }
}
