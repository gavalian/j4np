/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.level3.data;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.evio.EvioFile;

/**
 *
 * @author gavalian
 */
public class EvioDataSource implements DataSource {
    
    EvioFile evioReader = new EvioFile();
    
    @Override
    public void open(String url) {
        evioReader.open(url);
    }

    @Override
    public boolean hasNext() {
        return evioReader.hasNext();
    }

    @Override
    public boolean next(DataEvent event) {
        if(hasNext()==false) return false;        
        evioReader.next(((DecoderEvent) event).getEvioEvent());
        return true;
    }

    @Override
    public int position() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean position(int pos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int entries() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int nextFrame(DataFrame frame) {
        int size = frame.getCount();
        int counter = 0;
        for(int i = 0; i < size; i++){
            boolean status = this.next(frame.getEvent(i));
            if(status==true){ counter++;}
        }
        return counter;
    }
    
}
