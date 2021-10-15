/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import j4np.physics.EventFilter;
import j4np.physics.LorentzVector;
import j4np.physics.PhysicsReaction;
import j4np.physics.Vector3;
import j4np.physics.VectorOperator;
import java.util.Arrays;
import java.util.List;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;
import twig.data.H1F;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class ClasReaction extends PhysicsReaction {
    
    //private String filterString = "11:X+:X-:Xn";
    private EventFilter eventFilter = null;

    public ClasReaction(String filter){
       super(filter);
    }        
    
    public final ClasReaction withFilter(String filter){
        
        return this;
    }
    
    public void process(List<String> files){
        
        HipoChain chain = new HipoChain();
        chain.addFiles(files);
        
        chain.open();
        LorentzVector vcm = LorentzVector.withPxPyPzM(0, 0, 10.5, 0.0005).add(0,0,0,0.938);
        ClasEvent clas =  ClasEvent.with(chain,new String[]{"mc::event"});
        
        VectorOperator op = new VectorOperator(vcm,
                new int[]{11,211,-211},new int[]{0,0,0},new int[]{-1,-1,-1});
        
        Event event = new Event();
        H1F h = new H1F("h1",120,0.5,1.4);
        H1F h2 = new H1F("h1",120,0.0,90);
        H1F h3 = new H1F("h1",120,0.0,90);
        
        Vector3 ve = new Vector3();
        Vector3 vpp = new Vector3();
        Vector3 vpm = new Vector3();
        int counter = 0;
        while(chain.hasNext()==true){
            chain.nextEvent(event);
            clas.read(event);
            

            if(eventFilter.isValid(clas)==true){
                apply(clas);
                //System.out.println("true that");
                //System.out.println(clas.toLundString());
                op.apply(clas);
                //System.out.printf("m = %8.5f\n",op.vector().mass());
                h.fill(op.vector().mass());
                clas.vector(ve,   11, 0);
                clas.vector(vpp,  211, 0);
                clas.vector(vpm, -211, 0);
                h2.fill(ve.theta()*57.29);
                h3.fill(vpp.theta()*57.29);                
                double th1 = ve.theta()*57.29;
                double th2 = vpp.theta()*57.29;
                double th3 = vpm.theta()*57.29;
                if(th1>5&&th1<40&&th2>5&&th2<40&&th3>5&&th3<40){
                    counter++;
                }
            }
            
            //System.out.println(clas.toLundString());
            //System.out.println("count = " + clas.count() + " " + clas.pid(0));
        }
        TGCanvas c = new TGCanvas(800,400);
        c.view().divide(3,1);
        c.view().cd(0).region().draw(h);
        c.view().cd(1).region().draw(h2);
        c.view().cd(2).region().draw(h3);
        System.out.printf("counter = %d\n", counter);
    }
    
    public static void main(String[] args){
        String file = "test.hipo";
        ClasReaction reac = new ClasReaction("11:-211:211:Xn:X+:X-");
        reac.process(Arrays.asList(file));
    }
}
