/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.analysis.clas12;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.base.DataStream;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.EventModifier;
import j4np.physics.PhysicsEvent;
import j4np.physics.data.PhysDataEvent;

/**
 *
 * @author gavalian
 */
public class EventTopologyStatistics extends DataWorker<HipoReader,Event> {

    private   Schema    schema = null;
    private   String    bankName = "REC::Particle";
    private   Bank      bank = null;
    
    private PhysDataEvent   physEvent = null;
    private int[]             counter = new int[16];
    private EventModifier     modifier = null;
    private String[]     counterNames = new String[]{"Events",
        "1-,1+","1-,2+","1-,3+",
        "2-,1+","2-,2+","2-,3+",        
        "e-, 1-,1+","e-, 1-,2+","e-, 1-,3+",
        "e-, 2-,1+","e-, 2-,2+","e-, 2-,3+",
        "e-","c-","c+"};
    /*
    @Override
    public boolean init(DataSource r) {
        HipoReader hr = (HipoReader) r;
        schema = hr.getSchemaFactory().getSchema(bankName);
        return true;
    }
    
    @Override
    public void execute(DataEvent t) {
        
    }*/
    
    public EventTopologyStatistics(String name){
        bankName = name;
    }
    
    @Override
    public boolean init(HipoReader r) {
        schema = r.getSchemaFactory().getSchema(bankName);
        bank   = new Bank(schema);
        physEvent = new PhysDataEvent(bank);
        
        modifier = new EventModifier(){
            @Override
            public void modify(PhysicsEvent pe) {
                int counter = pe.count();
                for(int i = 0; i < counter; i++){
                    int status = pe.status(i);
                    if(Math.abs(status)>=2000&&Math.abs(status)<3000){
                        pe.status(i, 1);
                    }     else {
                        pe.status(i, -4);
                    }
                }
            }
            
        };
        
        return true;
    }
    private double ratio(int a, int b){
        return ((double) a)/b;
    }
    
    @Override
    public void execute(Event t) {
        
        physEvent.read(t);
        
        modifier.modify(physEvent);
        
        int pid    = physEvent.pid(0);
        int status = physEvent.status(0);
        int flag   = 0;
        
        if(pid==11&&status>0) flag = 1;
        
        int npos = physEvent.countByCharge(+1);
        int nneg = physEvent.countByCharge(-1);
        counter[0]++;
        if(npos==1&&nneg==1) counter[1]++;
        if(npos==2&&nneg==1) counter[2]++;
        if(npos==3&&nneg==1) counter[3]++;
        if(npos==1&&nneg==2) counter[4]++;
        if(npos==2&&nneg==2) counter[5]++;
        if(npos==3&&nneg==2) counter[6]++; 

        if(npos==1&&nneg==1&&flag==1) counter[7]++;
        if(npos==2&&nneg==1&&flag==1) counter[8]++;
        if(npos==3&&nneg==1&&flag==1) counter[9]++;
        if(npos==1&&nneg==2&&flag==1) counter[10]++;
        if(npos==2&&nneg==2&&flag==1) counter[11]++;
        if(npos==3&&nneg==2&&flag==1) counter[12]++;
        if(flag==1) counter[13]++;
        
        counter[14] += nneg;
        counter[15] += npos;
        
    }
    
    public void summary(){
        System.out.println("*********\n");
        for(int i = 0; i < counter.length; i++){
            System.out.printf(" %12s : %.6f\n",counterNames[i],ratio(counter[i],counter[0]));
        }
    }
    
    public static void main(String[] args){
        
        String file = "/Users/gavalian/Work/dataspace/denoise/out.ev.10.150na.dn.rec.ai.filtered.hipo";
        String bank = "REC::Particle";
        
        if(args.length>0) file = args[0];
        if(args.length>1) bank = args[1];
        
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
}
