/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.data.tests;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class HipoFileDebug {
     public static void main(String[] args){
        String file = "/Users/gavalian/Work/temp/output.h5";
        HipoReader r = new HipoReader();
        r.open(file);
        
        Event e = new Event();
        Bank b = r.getBank("DC::tdc");
        
        for(int i = 0; i < 200; i++){
            r.nextEvent(e);
            e.show();
            e.scan();
        }
    }
}
