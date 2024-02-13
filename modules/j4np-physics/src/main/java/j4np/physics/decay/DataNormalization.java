/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.decay;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.physics.LorentzVector;
import j4np.physics.Vector3;
import j4np.physics.data.PhysDataEvent;
import java.util.Random;
import twig.data.H1F;
import twig.graphics.TGCanvas;
import twig.math.F1D;
import twig.math.RandomFunc;

/**
 *
 * @author gavalian
 */
public class DataNormalization {
    
    public static H1F getWeight(double slope){
        F1D f = new F1D("f","[p0]+[a]*exp((x-[b])*[c])",0,2);
        f.setParameters(2000,1.697,-2,3.0);
        
        F1D f2 = new F1D("f","[p0]+[a]*exp((x-[b])*[c])",0,2);
        f2.setParameters(2000,1.697,-2,slope);
        
        
        RandomFunc rf = new RandomFunc(f);
        RandomFunc rf2 = new RandomFunc(f2);
        
        H1F h = new H1F("h",120,0.0,2.0);
        H1F h2 = new H1F("h2",120,0.0,2.0);
        h.attr().set("lc=2,lw=2");
        h2.attr().set("lc=3,lw=2");
        for(int i = 0; i < 1000000; i++){
            h.fill(rf.random());
            h2.fill(rf2.random());
        }
        
        h.unit();
        h2.unit();
        
        H1F h3 = H1F.divide(h2,h);
        return h3;
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/pentaquark/HipoFiles/Theta_Klp.h5";
        //String file = "output_4p5.h5";
        //String file = "/Users/gavalian/Work/DataSpace/pentaquark/HipoFiles/Sigma1660_Klp.h5";

        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("MC::particle","EVENT::particle","TAGGER::tagr");

        PhysDataEvent event = new PhysDataEvent(b[0]);
        
        Event e = new Event();
        LorentzVector t = new LorentzVector(0.,0.0,0.,0.938);
        LorentzVector pp = new LorentzVector(0.,0.0,0.,0.938);
        LorentzVector pm = new LorentzVector(0.,0.0,0.,0.938);
        
        H1F weight = DataNormalization.getWeight(48.0);
        
        H1F h = new H1F("h",120,0,2);
        TGCanvas c = new TGCanvas();
        c.view().divide(1,2);
        c.cd(0).draw(weight);
        c.cd(1).draw(h);
        HipoWriter w = HipoWriter.create("output_48p0.h5", r);
        //HipoWriter w = HipoWriter.create("o.h5", r);
        Random rnd = new Random();
        
        while(r.next(e)==true){
            
            event.read(e);
            e.read(b[2]);
            LorentzVector g = LorentzVector.withPxPyPzM(0, 0, b[2].getFloat("energy", 0),0);
            //System.out.println(" energy = " + g.e());
            //System.out.println(event.toLundString());
            event.vector(pp, 0.139,  211, 0);
            event.vector(pm, 0.139, -211, 0);
            
            pp.add(pm);
            g.add(t);
            Vector3 boost = g.boostVector();
            boost.invert();
            //System.out.println(" vector k " + pp);
            //System.out.println(" boost vector k " + boost);
            pp.boost(boost);
            //System.out.println(" vector k after boost " + pp);
            //System.out.println("cos = " + Math.cos(pp.theta()));
            double cos = 1 + Math.cos(pp.theta());
            int bin = weight.getAxisX().getBin(cos);
            double value = weight.getBinContent(bin);
            double dice  = rnd.nextDouble();
            if(dice<value) w.add(e);
            h.fill(1+Math.cos(pp.theta()));
        }
        
        System.out.println("done");
        w.close();
    }
}
