/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.monitor;

import j4np.data.base.DataEvent;
import j4np.data.base.DataSource;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import twig.graphics.TTabCanvas;

/**
 *
 * @author gavalian
 */
public class Clas12RecoMonitor extends DataWorker<HipoReader,Event> {
    
    TTabCanvas tabCanvas = null;
    private JPanel controls = null;
    private List<MonitorWorker> monitors = new ArrayList<>();
    private SchemaFactory factory = new SchemaFactory();
    
    public Clas12RecoMonitor addMonitor(MonitorWorker w){ this.monitors.add(w);return this;}
    
    @Override
    public boolean init(HipoReader src) {
        tabCanvas = new TTabCanvas(controls,1400,800);
        
        for(MonitorWorker w : monitors){
            String name = w.getName();
            for(int n = 0; n < w.getGroups().size(); n++){
                tabCanvas.getDataCanvas().addCanvas(w.getGroups().get(n).getName(), true);
                w.getGroups().get(n).draw(tabCanvas.getDataCanvas().activeCanvas(),true);
            }
        }
        
        tabCanvas.getDataCanvas().initTimers(3000);
        return true;
    }

    @Override
    public void execute(Event e) {
        try {
            for(MonitorWorker w : monitors) { w.process(e);}
        } catch (Exception ex) { System.out.println("exception happened");ex.printStackTrace();}
    }
    
    public static void main(String[] args){
        Clas12RecoMonitor mon = new Clas12RecoMonitor();
        mon.addMonitor(new MonitorDataVertex());
        mon.addMonitor(new MonitorPhysReaction());
        mon.init(null);
        
    }
}
