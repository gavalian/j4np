/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.io;

import j4np.data.base.DataFrame;
import j4np.data.base.DataStream;
import j4np.hipo5.data.Event;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class HipoDataStream extends DataStream<HipoChain,HipoWriter,Event> {
    
    public HipoDataStream(String fileinput, String fileoutput){
        super();
        HipoChain r = new HipoChain(fileinput);
        HipoWriter w = HipoWriter.create(fileoutput, r.getReader());
        this.withSource(r).withOutput(w);
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < 48; i++) frame.addEvent(new Event());
        this.withFrame(frame);
    }
    
    public HipoDataStream(List<String> fileinput, String fileoutput){
        super();
        HipoChain r = new HipoChain(fileinput);
        r.open(fileinput.get(0));
        HipoWriter w = HipoWriter.create(fileoutput, r.getReader());
        this.withSource(r).withOutput(w);
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < 48; i++) frame.addEvent(new Event());
        this.withFrame(frame);
    }
    
    public HipoDataStream(String fileinput, String fileoutput, int frameSize){
        super();
        HipoChain r = new HipoChain(fileinput);
        HipoWriter w = HipoWriter.create(fileoutput, r.getReader());
        this.withSource(r).withOutput(w);
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < frameSize; i++) frame.addEvent(new Event());
        this.withFrame(frame);
    }
    
    public HipoDataStream(List<String> fileinput, String fileoutput, int frameSize){
        super();
        HipoChain r = new HipoChain(fileinput);
        HipoWriter w = HipoWriter.create(fileoutput, r.getReader());
        this.withSource(r).withOutput(w);
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < frameSize; i++) frame.addEvent(new Event());
        this.withFrame(frame);
    }
        
    public HipoDataStream(String fileinput, int frameSize){
        super();
        HipoChain r = new HipoChain(fileinput);
        //HipoWriter w = HipoWriter.create(fileoutput, r);
        this.withSource(r);//.withOutput(w);
        DataFrame<Event> frame = new DataFrame<>();
        for(int i = 0; i < 48; i++) frame.addEvent(new Event());
        this.withFrame(frame);
    }
    
}
