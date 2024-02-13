/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.decay;

import j4np.physics.DecayKinematics;
import j4np.physics.DecayKinematics.Frame;
import j4np.physics.LorentzVector;
import j4np.physics.Particle;
import j4np.physics.ParticleList;
import j4np.physics.VectorOperator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.data.H1F;
import twig.graphics.TGCanvas;
import twig.math.F1D;
import twig.math.RandomFunc;

/**
 *
 * @author gavalian
 */
public class HyperonDecay {
    
    F1D  funcCosine = null;
    F1D hyperonMass = null;
    
    RandomFunc randCosine = null;
    RandomFunc   randMass = null;
    Random r = new Random();
    
    
    PhotonBeam  photon = new PhotonBeam(1.2,3.2);
    
    public HyperonDecay(double slope){
        funcCosine = new F1D("fc","[p]*exp(x*[b])",0.0,2.0);
        funcCosine.setParameters(5,slope);
        randCosine = new RandomFunc(funcCosine);
    }
    
    public void setHyperon(double mass, double width){
        hyperonMass = new F1D("hyp","[amp]*gaus(x,[mean],[sigma])",mass-2*width,mass+3*width);
        hyperonMass.setParameters(20,mass,width);
        randMass = new RandomFunc(hyperonMass);
    }
    
    public H1F debugCosine(){
        H1F h = new H1F("",120,-1,1);
        for(int i = 0; i < 1000; i++){
            double cos = randCosine.random() - 1;
            h.fill(cos);
        }
        return h;
    }
    public List<LorentzVector> getDecay(LorentzVector cm){
        double fi = r.nextDouble()*Math.PI*2;
        double cos = randCosine.random() - 1;
        double mass = randMass.random();
        //System.out.println("mass = " + mass + "  cosine = " + cos);
        List<LorentzVector> res = DecayKinematics.decay(cm, new double[]{mass,0.497}, Math.acos(cos), fi, Frame.REST);        
        return res;
    }
    
    public List<LorentzVector> createEvent(){
        List<LorentzVector> list = new ArrayList<>();
        LorentzVector beam = new LorentzVector();
        LorentzVector targ = new LorentzVector(0.0,0.0,0.0,0.938);
        photon.getBeam(beam);
        beam.add(targ);
        List<LorentzVector> decay = getDecay(beam);
        
        beam.sub(targ);
        list.add(beam); list.add(targ);
        list.addAll(decay);
        return list;
    }
    
    public void createEvent(ParticleList event){
        event.reset();
        
        //List<LorentzVector> list = new ArrayList<>();
        LorentzVector beam = new LorentzVector();
        LorentzVector targ = new LorentzVector(0.0,0.0,0.0,0.938);
        photon.getBeam(beam);
        beam.add(targ);
        List<LorentzVector> decay = getDecay(beam);        
        beam.sub(targ);
        
        
        event.addParticle(Particle.withPid(22, beam.px(), beam.py(), beam.pz()));
        event.addParticle(Particle.withPid(2212, targ.px(), targ.py(), targ.pz()));
        double cos = r.nextDouble()*2-1;
        double fi  = r.nextDouble()*2*Math.PI;
        System.out.println(" KAON = " + decay.get(1));
        List<LorentzVector> pions = DecayKinematics.decay(decay.get(1), 
                new double[]{0.139,0.139}, Math.acos(cos), fi, Frame.LAB);
        
        event.addParticle(Particle.withPid(211, pions.get(0).vect()));
        event.addParticle(Particle.withPid(-211, pions.get(1).vect()));
        //event.addParticle(Particle.withPid(2212, targ.px(), targ.py(), targ.pz()));
        
        //list.add(beam); list.add(targ);
        //list.addAll(decay);
        //return list;
    }
    
    public void print(List<LorentzVector> vec){
        for(LorentzVector v : vec) System.out.println(v);
    }
    
    public static void main(String[] args){
        
        HyperonDecay decay = new HyperonDecay(0.2);        
        decay.setHyperon(1.680, 0.002);
        
        TGCanvas c = new TGCanvas();
        H1F h = decay.debugCosine();
        ParticleList event = new ParticleList();
        VectorOperator vop = new VectorOperator(new LorentzVector(),"[22]+[2212]-[211]-[-211]");
        c.draw(h);
        for(int i = 0 ; i< 100; i++){
            
            System.out.println("------------ event");
            decay.createEvent(event);
            System.out.println(event.toLundString());
            vop.apply(event);
            System.out.println(" --- mass = " + vop.vector().mass());
            //System.out.println("-- event");
            //decay.print(vec);
            //LorentzVector g = LorentzVector.from(vec.get(0));
            //g.sub(vec.get(2));
            //System.out.println(" t = " + g.mass2());
           /* 
            
            System.out.println(" g = " + g);
            g.sub(vec.get(0));
            System.out.println(vec.size() + " " + vec.get(2).mass() + " t = " + g.mass2() );
            */
        }
        
        /*PhotonBeam fb = new PhotonBeam(1.2,3.2);
        LorentzVector   beam = new LorentzVector();
        LorentzVector target = new LorentzVector(0.0,0.0,0.0,0.938);
        DecayKinematics decay = new DecayKinematics();
        TGCanvas c = new TGCanvas();
        H1F h = new H1F("h",120,1.,4.);
        H1F h2 = new H1F("h2",120,1.2,2.4);
        Random r = new Random();
        c.view().divide(2,2);
        c.cd(0).draw(h).cd(1).draw(h2);
        for(int i = 0; i < 1000000; i++){
            fb.getBeam(beam);
            h.fill(beam.e());
            beam.add(target);
            List<LorentzVector> products = DecayKinematics.decay(beam, new double[]{1.54, 0.495},r.nextDouble()*Math.PI , 
                    r.nextDouble()*Math.PI*2,Frame.LAB);
            
            beam.sub(products.get(1));
            h2.fill(beam.mass());
        }*/
        
    }
}
