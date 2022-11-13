/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.data;


import j4np.utils.FileUtils;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.io.HipoChain;
import j4np.hipo5.io.HipoDataStream;
import j4np.hipo5.io.HipoDataWorker;
import j4np.hipo5.io.HipoReader;
import j4np.physics.EventFilter;
import j4np.physics.EventModifier;
import j4np.physics.LorentzVector;
import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ReactionConsumer extends HipoDataWorker {
    
    Schema[]       schemas = null;
    EventFilter     filter = null;
    EventModifier modifier = PhysicsReaction.FORWARD_ONLY;
    
    
    public ReactionConsumer(String filterString){
        filter = new EventFilter(filterString);
    }
    
    @Override
    public boolean init(HipoChain src) {
        schemas = src.getReader().getSchemas("REC::Particle");
        return true;
    }

    @Override
    public void execute(Event e) {
        
        //e.show();
        PhysDataEvent physEvent = new PhysDataEvent(e.read(schemas)[0]);
        physEvent.read(e);
        modifier.modify(physEvent);
        
        
        if(filter.isValid(physEvent)==false){
            e.reset();
        } else {
            //System.out.println("-- found one");
            //e.show();
            //System.out.println(" ");
        }
        
        /*else {
            LorentzVector ve = new LorentzVector();
            LorentzVector vp = new LorentzVector();
            physEvent.vector(ve, 0.0005,  11, 0);
            physEvent.vector(vp, 0.0005, -11, 0);
            ve.add(vp);
            if(ve.mass()<2.5) e.reset();
        }*/
    }
    
    public static void runFile(String dir, String output){
        List<String> files = FileUtils.dir("/Users/gavalian/Work/DataSpace/rga/","*.hipo");
        System.out.println(" n files = " + files.size());
        HipoDataStream stream = new HipoDataStream(files,output,64);        
        stream.consumer(new ReactionConsumer("11:-11:2212:Xn")).threads(8);
        stream.run();
        stream.show();
    }
    
    public static void main(String[] args){
        ReactionConsumer.runFile("input.hipo", "output.hipo");
        
        /*String file = "input.hipo";
        HipoDataStream stream = new HipoDataStream(file,"output.hipo",64);
        
        stream.consumer(new ReactionConsumer("11:-11:2212:Xn")).threads(8);"
        stream.run();
        stream.show();*/
    }
}
