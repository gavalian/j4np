/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.hipo5.data;

import j4np.hipo5.io.HipoReader;



/**
 *
 * @author gavalian
 */
public interface Worker {
    public void     init(HipoReader reader);
    public boolean  processEvent(Event event);
    public long     clasifyEvent(Event event);
}
