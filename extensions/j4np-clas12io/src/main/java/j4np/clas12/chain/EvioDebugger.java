/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.chain;

import j4np.data.base.DataEvent;
import j4np.data.base.DataNodeCallback;
import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import java.util.Arrays;

/**
 *
 * @author gavalian
 */
public class EvioDebugger {
    EvioFile reader = null;
    EvioEvent event = new EvioEvent();
    public EvioDebugger(String file){
        reader = new EvioFile();
        reader.open(file);
    }
    public void advance(int count){
        for(int i = 0; i < count; i++){
            reader.next(event);
        }
    }
    
    public void show(){
       DataNodeCallback callback = new  DataNodeCallback (){
           @Override
           public void apply(DataEvent event, int position, int[] identification) {
               System.out.printf(" position %d , ids : %s\n",position, Arrays.toString(identification));
           } 
       };
       event.setCallback(callback);
       event.scan();       
    }
    
    public static void main(String[] args){
        EvioDebugger debug = new EvioDebugger("clas_019436.evio.00049");
        debug.advance(125);
        debug.show();
    }
}
