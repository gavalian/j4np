/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.io.HipoReader;
import j4np.physics.LorentzVector;
import j4np.physics.PhysicsEvent;
import j4np.physics.PhysicsReaction;
import twig.data.DataGroup;
import twig.data.H1F;
import twig.data.H2F;

/**
 *
 * @author gavalian
 */
public class ReactionPi0 extends PhysicsReaction {
    public ReactionPi0(String file){
        super("11:X-:X+:Xn",10.5);
        this.setDataSource(new HipoReader(file), "REC::Particle");
        this.addModifier(FORWARD_ONLY);
    }
    
    @Override
    public DataGroup process(){
        
        DataGroup group = new DataGroup(3,2);
        
        H2F h2   = new H2F(  "pi02d",20,0.5,10.5,120,0.005,0.705);
        H2F h22  = new H2F( "cos02d",20,0.5,10.5,120,0.895,1.005);        
        H1F h2p  = new H1F(    "cos",120,0.895,1.005);
        
        H1F h1  = new H1F("pi0",120,0.005,0.705);
        H1F h11 = new H1F("elec",80,0.00,9.00);
        H1F h12 = new H1F("photons",20,-0.5,19.5);
        H1F h13 = new H1F("neutrals",20,-0.5,19.5);
        
        group.add(h1,  0, "");
        group.add(h2,  1, "");
        group.add(h11, 2, "");
        group.add(h12, 3, "");
        group.add(h13, 3, "");
        
        group.add(h22,4,"");
        group.add(h2p,5,"");
        
        
        
        h11.attr().setTitleX("P_e [GeV]");
        h1.attr().setTitleX("M(#gamma#gamma) [GeV]");
        h12.attr().setTitleX("N");
        h13.attr().setTitleX("N");
        
        h12.attr().set("lc=2,lw=2");
        h13.attr().set("lc=3,lw=2");
        
        LorentzVector  first = new LorentzVector();
        LorentzVector  second = new LorentzVector();
        LorentzVector  elec   = new LorentzVector();
        
        while(this.next()==true){
            
            PhysicsEvent event = this.getPhysicsEvent();
            if(this.eventFilter.isValid(event)==true){
                int nPhotons  = event.countByPid(22);
                int nNeutrals = event.countByCharge(0);
                event.vector(elec, 0.0005, 11, 0);
                h11.fill(elec.p());
                h12.fill(nPhotons);
                h13.fill(nNeutrals);            
                for(int n = 0; n < nPhotons-1; n++){
                    for(int t = n+1; t < nPhotons; t++){                        
                        event.vector(  first, 0.0, 22, n);
                        event.vector( second, 0.0, 22, t);
                        if(first.vect().mag()>0.2&&second.vect().mag()>0.2){
                            double costh = first.vect().dot(second.vect())/
                                    (first.vect().mag()*second.vect().mag());                            
                            first.add(second);
                            //if(costh<0.995){
                                h2.fill(first.e(), first.mass());
                                h1.fill(first.mass());
                            //}
                            h22.fill(first.e(), costh);
                            h2p.fill( costh);
                        }
                    }
                }
            }
        }
        return group;
    }
}
