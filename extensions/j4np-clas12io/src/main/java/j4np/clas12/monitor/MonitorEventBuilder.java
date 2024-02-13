/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.monitor;

import j4np.graphics.CanvasLayout;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.physics.PDGDatabase;
import j4np.physics.PDGParticle;
import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class MonitorEventBuilder extends MonitorWorker {
    Bank[] banks = null;
    public MonitorEventBuilder(){
         super("EventBuilder");
        banks = factory.getBanks(new String[]{"REC::Particle","REC::Track","REC::Scintillator"}, 32);
        init();
    }
    
    private void init(){
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kInvertedDarkBodyRadiator);
        CanvasLayout layout = new CanvasLayout();
        layout.addColumn(0, 0.25, new double[]{0.33,0.33,0.33});
        layout.addColumn(0.25, 0.25, CanvasLayout.uniform(6));
        layout.addColumn(0.50, 0.25, CanvasLayout.uniform(6));
        layout.addColumn(0.75, 0.25, CanvasLayout.uniform(6));
        DataGroup group = new DataGroup("EventBuilder",layout); 
        group.setRegionAttributes("mt=10,mb=50,fc=#FAF5ED");
        for(int i = 0; i < 3; i++) group.add(new H2F("",10,0.0,1.0,10,0.0,1.0), i, "F");
        for(int i = 0; i < 6; i++) 
            group.add(H1F.book("h", "n:start time:counts", "fc=10", 80,-2,2), i+3, "");
        for(int i = 0; i < 6; i++) 
            group.add(H1F.book("h", "n:Geant PID:counts", "fc=5", 16,-0.5,15.5), i+9, "");
        this.getGroups().add(group);
    }
    
    @Override
    void process(Event e) {
        double cc = 29.9792458;
       e.read(banks);       
       int nrows = banks[0].getRows();
       for(int i = 0; i < nrows; i++){
           int    pid = banks[0].getInt("pid", i);
           int     ch = banks[0].getInt("charge", i);
           int     st = Math.abs(banks[0].getInt("status", i));
           float  chi2 = Math.abs(banks[0].getFloat("chi2pid", i));
           double  vz = banks[0].getFloat("vz", i);
           if(pid!=0&&st>2000&&st<3000&&chi2<5&&ch!=0){
               PDGParticle p = PDGDatabase.getParticleById(pid);
               if(p!=null) {
                   int sector = getSector(banks[1],i);
                   int index = sector - 1 + 9;
                   ((H1F) this.getGroups().get(0).getData().get(index)).fill(p.gid());
                   
                   if(pid==11){
                       int row = findRow(banks[2],0);
                       //System.out.println( banks[2].getRows() + "  " + row);
                       if(row>=0){
                           double path = banks[2].getFloat("path", row);
                           double vt = banks[0].getFloat("vt", 0);
                           double time = banks[2].getFloat("time", row);
                           int sec = banks[2].getInt("sector",row);
                           double result = time - path/cc - vt;
                           //System.out.println(" result = " + result);
                           ((H1F) this.getGroups().get(0).getData().get(sec-1+3)).fill(result);
                       }
                   }
               }
               
           }
       }       
    }
    public int findRow(Bank b, int row){
        for(int i = 0; i < b.getRows(); i++){
            int layer = b.getInt("layer", i);
            if(b.getInt("pindex", i)==row&&layer==2) return i;
        }
        return -1;
    }
    public int getSector(Bank b, int order){
        for(int i = 0 ; i < b.getRows(); i++){
            if(b.getInt("pindex",i) == order ) return b.getInt("sector", i);
        }
        return -1;
    }
}
