/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.data.base.DataActor;
import j4np.data.base.DataEvent;
import j4np.data.base.DataSource;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;

/**
 *
 * @author gavalian
 */
public class DriftChamberWorker extends DataWorker {
    
    DriftChamber drift = new DriftChamber();


    @Override
    public boolean init(DataSource src) {
        return true;
    }

    @Override
    public void execute(DataEvent event) {
        drift.processEventRaw((Event) event);
    }
    
}
