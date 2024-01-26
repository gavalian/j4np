/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.chain;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;

/**
 *
 * @author gavalian
 */
public class Evio2HipoSource implements DataSource {
    EvioFile reader = new EvioFile();
    EvioEvent event = new EvioEvent();
    
    int position = 0;
    @Override
    public void open(String url) {
        reader.open(url);        
    }

    @Override
    public boolean hasNext() {
        return reader.hasNext();
    }

    @Override
    public boolean next(DataEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public boolean position(int pos) {
        return true;
    }

    @Override
    public int entries() {
        return 1;
    }

    @Override
    public int nextFrame(DataFrame frame) {
        int counter = 0;
        for(int i = 0; i < frame.getCount(); i++){
            Event hipo = (Event) frame.getEvent(i);
            hipo.require(200*1024);
            hipo.reset();
            if(reader.hasNext()==true){
                reader.next(event);
                Node node_evio = new Node(1,11,event.getBuffer().array(),0,event.bufferLength()*4+8);
                //System.out.println(" size = " + node_evio.getBufferSize());
                hipo.write(node_evio);
                counter++;
            }
        }
        return counter;
    }
    
}
