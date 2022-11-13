/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.utils;

import j4np.data.base.DataFrame;
import j4np.data.base.DataStream;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;

import j4np.hipo5.data.Event;
import j4np.hipo5.data.EventFilter;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoChain;
import j4np.hipo5.io.HipoDataStream;
import j4np.hipo5.io.HipoDataWorker;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class HipoFilterWorker extends HipoDataWorker {
       
    private Schema[]      keep = null;
    private String[]   schemas = null;
    private String  bankExpression = null;

    public HipoFilterWorker(String expression){
        this.bankExpression = expression;
    }
    
    public static void executeProgram(String input, String output){
        
        DataStream<HipoReader,HipoWriter,Event>  stream = new DataStream<>();
        HipoReader r = new HipoReader(input);
        HipoWriter w = new HipoWriter();
        w.getSchemaFactory().copy(r.getSchemaFactory());
        w.open(output);
        
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < 128; i++){ frame.addEvent(new Event());}
        
       // HipoFilterWorker worker = new HipoFilterWorker("TimeBased.*");
        //stream.source(r).withOutput(w).frame(frame).consumer(worker);
        
        stream.threads(1);
        stream.run();
        stream.show();
    }

    @Override
    public boolean init(HipoChain src) {        
        SchemaFactory factory = src.getReader().getSchemaFactory().reduce(bankExpression);
        List<Schema> list = factory.getSchemaList();
        this.keep = new Schema[list.size()];
        for(int i = 0; i < keep.length; i++) this.keep[i] = list.get(i);
        System.out.println("\n\n***** filtering:");
        System.out.println("***** number of schemas : " + src.getReader().getSchemaFactory().getSchemaList().size());
        System.out.println("***** filtered  schemas : " + keep.length);
        return true;
    }

    @Override
    public void execute(Event e) {
        Bank[] banks = e.read(keep);
        e.reset();
        for(Bank b : banks) e.write(b);
    }
    
    public static void main(String[] args){
       System.out.println("---start"); 
       String input = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        String output = "output.hipo";
        HipoDataStream stream = new HipoDataStream(input,output,64);
        HipoFilterWorker c = new HipoFilterWorker("REC::Particle");
        stream.threads(8).consumer(c);
        stream.run();
        stream.show();
    }
    /*public static void main(String[] args){
        String input = "/Users/gavalian/Work/DataSpace/rga/rec_005988.00005.00009.hipo";
        String output = "output.hipo";
        HipoDataStream stream = new HipoDataStream(input,output,64);
        HipoFilterWorker c = new HipoFilterWorker("TimeBasedTrkg::*");
        stream.threads(1).consumer(c);
        stream.run();
    }*/
}
