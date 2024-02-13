/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.monitor;

import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class Clas12DecoderMonitor extends DataWorker<HipoReader,Event> {

    @Override
    public boolean init(HipoReader src) {
        return true;
    }

    @Override
    public void execute(Event e) {
        e.scanShow();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Clas12DecoderMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
