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
import j4np.hipo5.data.Leaf;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DriftChamberWorker extends DataWorker {
    
    DriftChamber drift = new DriftChamber();
    DriftChamber3 drift3 = new DriftChamber3();

    public boolean useDenoising = true;
    
    @Override
    public boolean init(DataSource src) {
        return true;
    }

    @Override
    public void execute(DataEvent event) {
        Event ev = (Event) event;
        //System.out.println(" ----- EVECUTING DC WORKER ----");
        int position = ev.scan(42, 11);
        //ev.scanShow();
        if(position>0){
            
            ChamberData data = new ChamberData();
            int length = ev.scanLengthAt(42, 11, position);
            Leaf tdc = new Leaf(length+128);
            ev.readAt(tdc, 42, 11, position);
            int nrows = tdc.getRows();
            
            for(int i = 0; i < nrows; i++){
                int type = tdc.getInt(0, i);
                if(type==6){
                    if(useDenoising==false){
                        data.set(tdc.getInt(1, i),tdc.getInt(2,i),tdc.getInt(3, i));
                    } else {
                        int order = tdc.getInt(4, i);
                        if(order==0||order==40||order==50)
                            data.set(tdc.getInt(1, i),tdc.getInt(2,i),tdc.getInt(3, i));
                    }
                }
            }
            
            Leaf leaf = new Leaf(32101,10,"2s2b2f3b",1024);
            drift3.segmentFinder(data, leaf);
            
            ev.write(leaf);
            System.out.println("----- current LEAF");
            leaf.print();
            //ev.scanShow();
            //drift.processEventRaw((Event) event);
        }
    }
}
