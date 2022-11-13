/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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
 * This is an example on how to use the multithreaded data event workers
 * @author gavalian
 */
public class FilterPhysicsEvent extends HipoDataWorker {
    
    Schema[]       schemas = null;
    EventFilter     filter = null;
    EventModifier modifier = PhysicsReaction.FORWARD_ONLY;
    
    
    public FilterPhysicsEvent(String filterString){
        filter = new EventFilter(filterString);
    }
    
    @Override
    public boolean init(HipoChain src) {
        schemas = src.getReader().getSchemas("REC::Particle");
        return true;
    }

    @Override
    public void execute(Event e) {
        PhysDataEvent physEvent = new PhysDataEvent(e.read(schemas)[0]);
        physEvent.read(e);
        modifier.modify(physEvent);
        if(filter.isValid(physEvent)==false){
            e.reset();
        } else { 
			// this part is purely for the J/psi filtering
			// the rest of the code is pretty generic
            LorentzVector ve = new LorentzVector();
            LorentzVector vp = new LorentzVector();
            physEvent.vector(ve, 0.0005,  11, 0);
            physEvent.vector(vp, 0.0005, -11, 0);
            ve.add(vp);
            if(ve.mass()<2.5) e.reset();
        }
    }
    
    public static void runFile(String directory, String output){
        List<String> files = FileUtils.dir(directory,"*.hipo");
        System.out.println(" n files = " + files.size());
        HipoDataStream stream = new HipoDataStream(files,output,64);        
        stream.consumer(new FilterPhysicsEvent("11:-11:2212:Xn")).threads(8);
        stream.run();
        stream.show();
    }
}

