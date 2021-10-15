/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import j4np.physics.LorentzVector;
import j4np.physics.PhysicsReaction;
import j4np.physics.Vector3;
import j4np.physics.VectorOperator.OperatorType;
import j4np.utils.io.OptionParser;
import java.util.Arrays;
import java.util.List;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;

/**
 *
 * @author gavalian
 */
public class ClasExclusive {
    protected PhysicsReaction reaction = null;
    
    public ClasExclusive(String filter, double energy){
        reaction = new PhysicsReaction(filter,energy);
    }
    
    protected void configure(){
        
        reaction.addVector(reaction.getVector(), "-[11]-[211]-[-211]");
        reaction.addVector( "[11]");
        reaction.addVector( "[211]");
        reaction.addVector( "[-211]");
        reaction.addEntry(   "mx", 0, OperatorType.MASS);
        reaction.addEntry(  "eth", 1, OperatorType.THETA_DEG);
        reaction.addEntry( "ppth", 2, OperatorType.THETA_DEG);
        reaction.addEntry( "pmth", 3, OperatorType.THETA_DEG);

        reaction.summary();
        
    }
    
    public void process(List<String>  inputs){
        HipoChain chain = new HipoChain();
        chain.addFiles(inputs);
        chain.open();
        
        ClasEvent clas = ClasEvent.with(chain,new String[]{"mc::event"});
        Event event = new Event();
        int counter = 0;
        while(chain.hasNext()==true){
            chain.nextEvent(event);
            clas.read(event);
            //System.out.println(clas.toLundString());
            if(reaction.isValid(clas)==true){
                reaction.apply(clas);
                System.out.println(reaction);
                LorentzVector ve = new LorentzVector();                

                clas.maskAll();
                System.out.println(clas.toLundString());
                
                clas.unmask(11, 0).unmask(211, 0).unmask(-211, 0);
                System.out.println(clas.toLundString());
                /*clas.makeVector(ve, new int[]{11}, new double[]{0.0005},
                        new int[]{0}, new int[]{1});
                
                Vector3 v3 = new Vector3();
                clas.vector(v3, 11, 0);
                System.out.println("****************** " + ve.theta() + "   "
                + v3.theta()*57.29);*/
            }
        }
        
        reaction.stats();
    }
    
    public static void main(String[] args){
        
        OptionParser parser = new OptionParser("clasExclusive");
        parser.addRequired("-o", "output file name");
        
        parser.parse(args);
        List<String>  inputs = parser.getInputList();
        
        ClasExclusive excl = new ClasExclusive("11:211:-211:Xn:X+:X-",10.5);
        excl.configure();
        inputs = Arrays.asList("test.hipo");
        
        excl.process(inputs);
    }
}
