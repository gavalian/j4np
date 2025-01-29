/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.debug;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.EventModifier;
import j4np.physics.LorentzVector;
import j4np.physics.PhysicsReaction;
import j4np.physics.Vector3;
import j4np.physics.data.PhysDataEvent;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class KaonStarDebug {
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-11.0/study/kstar/data/filter_output_0_w.h5";
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("REC::Particle");
        PhysDataEvent physics = new PhysDataEvent(b[0]);
        
        physics.init(r);
        EventModifier modifier = PhysicsReaction.FORWARD_ONLY_CHI2PID;
        Event e = new Event();
        Vector3 vbeam = new Vector3(0,0,10.6);
        Vector3    ve = new Vector3();
        Vector3    kp = new Vector3();
        Vector3    pi = new Vector3();
        
        LorentzVector lvKp = new LorentzVector();
        LorentzVector lvPi = new LorentzVector();
        
        TGCanvas c = new TGCanvas();
        c.view().divide(2,1);
        H1F h = new H1F("h",120,-3.14,3.14);
        H2F h2 = new H2F("h",25,-3.14,3.14,120,0.65,1.35);
        
        c.cd(0).draw(h);
        c.cd(1).draw(h2);
        c.view().initTimer(400);
        
        while(r.next(e)){
            physics.read(e);
            modifier.modify(physics);
            //System.out.println(physics.toLundString());
            physics.vector(ve, 11, 0);
            physics.vector(pi, -211, 0);
            physics.vector(kp, 321, 0);
            
            physics.vector(lvPi, 0.139, -211, 0);
            physics.vector(lvKp, 0.497,  321, 0);
            
            lvKp.add(lvPi);
            
            Vector3 ey = vbeam.cross(ve);
            Vector3 ez = Vector3.from(ve).sub(vbeam);
            
            ey.unit(); ez.unit();
            Vector3 ex = ey.cross(ez);
            
            double kpx = kp.dot(ex);
            double kpy = kp.dot(ey);
            
            double ksx = lvKp.vect().dot(ex);
            double ksy = lvKp.vect().dot(ey);
            
            double phi  = Math.atan2(kpy, kpx);
            double phis = Math.atan2(ksy, ksx);
            
            System.out.printf(" %8.5f %8.5f %8.5f %8.5f\n",kpx,kpy, phi,phis);
            if(lvKp.mass()>0.8&&lvKp.mass()<1.0) h.fill(phis);
            h2.fill(phis, lvKp.mass());
            //System.out.println(ve.toString());
        }
    }
}
