/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.monitor;

import j4np.data.base.DataEvent;
import j4np.data.base.DataSource;
import j4np.data.base.DataWorker;
import j4np.graphics.Background2D;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.EventFilter;
import j4np.physics.EventLeaf;
import j4np.physics.LorentzVector;
import j4np.physics.Vector3;
import j4np.physics.VectorOperator;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class TrackMonitor extends DataWorker{
    
    DataGroup group = null;
    Leaf       leaf = new Leaf(2048);
    EventLeaf  phys = new EventLeaf();
    
    public TrackMonitor(){
        
        group = new DataGroup(2,3);
        
        H1F[] h100s = H1F.duplicate(2, "HP", 120,0.5, 11.0);
        H1F[] h200s = H1F.duplicate(2, "HF", 40,-3.14,3.14);
        H1F[] h300s = H1F.duplicate(2, "HF", 120,Math.toRadians(5),Math.toRadians(45.0));
        
        group.add(h100s[0], 0, "").add(h100s[1], 1, "")
                .add(h200s[0], 2, "").add(h200s[1], 3, "")
                .add(h300s[0], 4, "").add(h300s[1], 5, "");
        h100s[0].attr().set("fc=#FFE31A");
        h100s[1].attr().set("fc=#FFE31A");
        h200s[0].attr().set("fc=#ABBA7C");
        h200s[1].attr().set("fc=#ABBA7C");
        h300s[0].attr().set("fc=#F09319");
        h300s[1].attr().set("fc=#F09319");
        group.setCanvasAttributes("bc=#EEEEEE");
        group.setRegionAttributes("fc=#EFEFEF");
        phys.setLeaf(leaf);
    }
    
    public static void filter(String file){
        HipoReader r = new HipoReader(file);
        HipoWriter w = new HipoWriter("");
    }

    @Override
    public boolean init(DataSource src) {
        return true;
    }

    @Override
    public void execute(DataEvent e) {
        ((Event) e).read(phys.getLeaf(),32000,1);
        Vector3 v3 = new Vector3();
        double[] values = new double[6];
        for(int j = 0 ; j < phys.count(); j++){
            phys.vector(v3, j);
            //System.out.println(v3);
            int charge = phys.charge(j);
            if(charge<0) values[0] = v3.mag(); else values[1] = v3.mag();
            if(charge<0) values[2]=v3.phi();   else values[3]=v3.phi();
            if(charge<0) values[4]=v3.theta(); else values[5]=v3.theta();
            if(Math.abs(v3.phi())<0.0001) System.out.println(v3.phi());
            //values[2+charge<0?0:1] = v3.phi();
            //values[4+charge<0?0:1] = v3.theta();
            //System.out.println("charge "  +  charge + " " + v3.mag() + " " + v3.phi());
            for(int i = 0; i <6; i++){
                //group.fill(values);
                ((H1F) group.getData().get(i)).fill(values[i]);
            }
        }
    }
    

    public static void main(String[] args){
        //HipoReader r = new HipoReader("irec_clas_003841.evio.00000_denoised.h5");
        HipoReader r = new HipoReader("chain_output_nov_20.h5");
        Leaf leaf = new Leaf(32000,1,"ii",1024);
        EventLeaf phys = new EventLeaf();
        phys.setLeaf(leaf);
        EventFilter f = new EventFilter("2-:1+:X+:X-:Xn");
        VectorOperator op  = VectorOperator.create(new LorentzVector(),"[1,0,0.139]+[2,0,0.139]");
        VectorOperator  w =  VectorOperator.create(VectorOperator.createEP(10.2),"-[1,1,0.0005]-[1,0,0.139]-[2,0,0.139]");
        
        Vector3 v = new Vector3();
        
        TrackMonitor monitor = new TrackMonitor();
        
        Event e = new Event();
        int counter = 0;
        H1F h = new H1F("massp",120,0.,2);
        H1F hc = new H1F("massp",60,0.,2);
        H1F hw = new H1F("massw",120,0.,4.2);
        H1F hwc = new H1F("massw",120,0.,4.2);
        TGCanvas c = new TGCanvas(700,900);
        //c.view().setBackground( Background2D.createBackground(250, 250, 150));
        monitor.group.draw(c.view(), true);
        
        hc.attr().set("fc=165");
        hwc.attr().set("fc=163");
        
        c.view().initTimer(1500);
        
        /*c.view().divide(2, 2);
        c.cd(0).draw(h).cd(2).draw(hc,"same");
        c.cd(1).draw(hw).cd(3).draw(hwc,"same");
        double[] values = new double[6];*/
        
        while(r.hasNext()){
            r.next(e);
            //e.read(phys.getLeaf());
            //
            monitor.execute(e);
            //System.out.println("--");
            //leaf.print();
            counter++;
            //System.out.println(phys.toString());
            //phys.vector(v, 0);
            //System.out.println(v + "  " + f.isValid(phys));
            /*
            if(f.isValid(phys)==true){
                op.apply(phys);
                w.apply(phys);
                
                //System.out.println(op.getValue(VectorOperator.OperatorType.MASS));

                double rm = op.getValue(VectorOperator.OperatorType.MASS);
                double wm = w.getValue(VectorOperator.OperatorType.MASS);
                hw.fill(wm);
                h.fill(rm);
                if(wm<1.2) hc.fill(op.getValue(VectorOperator.OperatorType.MASS));
                if(rm>0.7&&rm<0.85) hwc.fill(wm);
            }
            */
        }
        System.out.println(" counter = " + counter);
        
    }
}
