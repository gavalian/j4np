/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.data.base.DataEvent;
import j4np.data.base.DataSource;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.data.Structure;

/**
 *
 * @author gavalian
 */
public class Hipo2Hipo4Worker extends DataWorker {

    @Override
    public boolean init(DataSource src) {
        return true;
    }

    @Override
    public void execute(DataEvent e) {
        Event ev = (Event) e;
        
        Structure struct = new Structure(32000,120,12,2*1024);
        
        ev.move(struct, 32101, 10);
        ev.move(struct, 32000, 1);
        ev.move(struct, 32000, 21);
        ev.move(struct, 32200, 1);
        ev.move(struct, 32200, 2);
        ev.move(struct, 32200, 3);
        
        Node  evio = ev.read(1, 11);
        ev.reset();
        
        ev.write(evio);
        ev.write(struct);
    }
    
}
