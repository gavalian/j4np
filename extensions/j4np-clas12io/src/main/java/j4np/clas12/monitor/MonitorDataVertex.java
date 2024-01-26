/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.monitor;

import j4np.graphics.CanvasLayout;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.physics.Vector3;
import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class MonitorDataVertex extends MonitorWorker {
    Bank[] banks = null;
    public MonitorDataVertex(){
        super("Vertex");
        banks = factory.getBanks(new String[]{"REC::Particle","REC::Track"}, 32);
        init();
    }
    
    private void init(){
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kInvertedDarkBodyRadiator);
        CanvasLayout layout = new CanvasLayout();
        layout.addColumn(0, 0.25, new double[]{0.33,0.33,0.33});
        layout.addColumn(0.25, 0.25, CanvasLayout.uniform(6));
        layout.addColumn(0.50, 0.25, CanvasLayout.uniform(6));
        layout.addColumn(0.75, 0.25, CanvasLayout.uniform(6));
        
        DataGroup group = new DataGroup("Vertex",layout); 
        group.setRegionAttributes("mt=10,mb=50,fc=#FAF5ED");
        
        for(int i = 0; i < 3; i ++) group.add(
                new H2F("h2",120,-20,10,80,-3.14,3.14), i, "F");
        
        for(int i = 0; i < 18; i++){ 
            group.add( H1F.book(
                    String.format("vertex_%d",i+1) ,
                    String.format("v:vertex (sector %d):counts",(i)%6+1),
                    "fc=72",120,-20,10)
                    , i + 3, "");
        }
        
        this.getGroups().add(group);
        DataGroup groupm = new DataGroup(3,6); groupm.setName("Momentum");
        groupm.setRegionAttributes("mt=10,mb=50,fc=#FAF5ED");
        for(int i = 0; i < 18; i++){ 
            groupm.add(H1F.book(
                    String.format("momentum_%d",i+1) ,
                    String.format("v:momentum (sector %d):counts",(i)%6+1),
                    "fc=79",120,0.0,10.5), i, "");
        }
        this.getGroups().add(groupm);
    }
    
    @Override
    void process(Event e) {
        e.read(banks);
        int nrows = banks[0].getRows();
        for(int i = 0; i < nrows; i++){
            int    pid = banks[0].getInt("pid", i);
            int     ch = banks[0].getInt("charge", i);
            int     st = banks[0].getInt("status", i);
            double  vz = banks[0].getFloat("vz", i);
            int offset = -1;
            int pad = -1;
            if(ch>0&&Math.abs(st)>2000&&Math.abs(st)<3000){ offset = 12; pad = 2;}
            if(ch<0&&Math.abs(st)>2000&&Math.abs(st)<3000) { offset = 6; pad = 1;}
            if(offset==6&&pid==11) offset = 0;
            if(pad>0&&pid==11) pad = 0;
            /*if(pid==11){
                System.out.printf(" %d %d %d\n", pid,st, offset);
            }*/
            Vector3 vec = new Vector3(banks[0].getFloat("px", i),
                    banks[0].getFloat("py", i),banks[0].getFloat("pz", i));
            offset += 3;
            if(offset>=0){
                int sector = getSector(banks[1],i);
                if(sector>0) {
                    ((H1F) getGroups().get(0).getData().get(offset+(sector-1))).fill(vz);
                    //double px = banks[0].getFloat("px", i);
                    //double py = banks[0].getFloat("py", i);
                    //double pz = banks[0].getFloat("pz", i);
                    ((H1F) getGroups().get(1).getData().get(offset - 3 +(sector-1))).fill(vec.mag());
                }
            }
            
            if(pad>=0){
                ((H2F) getGroups().get(0).getData().get(pad)).fill(vz,vec.phi());
            }
        }
    }
    
    public int getSector(Bank b, int order){
        for(int i = 0 ; i < b.getRows(); i++){
            if(b.getInt("pindex",i) == order ) return b.getInt("sector", i);
        }
        return -1;
    }
}
