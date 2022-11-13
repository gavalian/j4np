/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.io;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.hipo5.data.Event;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class HipoChain implements DataSource {
    List<String>   files = new ArrayList<>();
    private    int  nFile = 0;
    private    int nEvent = 0;
    HipoReader   r     = new HipoReader();
    
    
    public HipoChain(List<String> list ){
        files.addAll(list); open(files.get(0));
    }
    
    public HipoChain(String file ){
        files.add(file); open(files.get(0));
    }
    
    public HipoReader getReader(){ return r;}
    
    @Override
    public void open(String url) {
        r.open(files.get(0)); nFile = 0;
    }

    @Override
    public boolean hasNext() {
        if(r.hasNext()==false){
            if(nFile>=files.size()-1) return false;
            nFile++; r = new HipoReader(files.get(nFile));
        }
        return true;
    }

    @Override
    public boolean next(DataEvent event) {
        nEvent++;
        return r.next(event);
    }

    @Override
    public int position() {
        return nEvent;
    }

    @Override
    public boolean position(int pos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int entries() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int nextFrame(DataFrame frame) {
        int frameSize = frame.getCount();
        int   counter = 0;
        
        for(int i = 0; i < frameSize; i++){
            Event event = (Event) frame.getEvent(i);
            if(this.hasNext()==true){
                this.next(event);
                counter++;
            } else {
                event.reset();
            }
        }
        return counter; 
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
