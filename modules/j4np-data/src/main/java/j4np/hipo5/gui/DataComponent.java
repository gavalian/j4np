/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.gui;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataComponent {
    
    List<ActionListener> listeners = new ArrayList<>();
    HipoReader r = null;
    Event      currentEvent = new Event();
    
    public DataComponent(){
        
    }
    
    public void addListener(ActionListener l){ this.listeners.add(l);}
    
    public void openFile(String file){
        r = new HipoReader(file);
        ActionEvent evt = new ActionEvent(r,21,"hipo::open");
        for(ActionListener l : listeners){ l.actionPerformed(evt);}
    }
    
    public void nextEvent(){
        r.nextEvent(currentEvent);
        ActionEvent evt = new ActionEvent(r,22,"hipo::next");
        for(ActionListener l : listeners){ l.actionPerformed(evt);}
    }
}
