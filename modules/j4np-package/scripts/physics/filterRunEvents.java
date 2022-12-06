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
public class FilterRunEvent extends HipoDataWorker {
    
    Schema[]       schemas = null;
    EventFilter     filter = null;
    EventModifier modifier = PhysicsReaction.FORWARD_ONLY;
    Map<Integer,List<Integer>> map;

    public FilterRunEvent(String filterString){
        //filter = new EventFilter(filterString);
	// read from text and populate run
    }
    
    @Override
    public boolean init(HipoChain src) {
        schemas = src.getReader().getSchemas("RUN::config");
        return true;
    }

    @Override
    public void execute(Event e) {
	
        Bank b = e.read(schemas)[0];
	int run = b.getInt("run",0);
	int runevent = b.getInt("event",0);

	if(map.containsKey(run) == true){
	    if(map.get(run).contains(runevent)) return;
	}
	e.reset();
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

