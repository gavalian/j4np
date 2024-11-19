/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.data.base.DataActor;
import j4np.data.base.DataActorStream;
import j4np.data.base.DataFrame;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class RunInstaRec {
    public static DataFrame createFrames(int count){
        DataFrame<Event>  frame = new DataFrame<>();        
        for(int i = 0; i < count; i++) frame.addEvent(new Event());
        return frame;
    }
    
    public static List<DataActor>  createActors(int nactors, int nframes, List<DataWorker> workers){
        List<DataActor> actors = new ArrayList<>();
        for(int a = 0; a < nactors; a++){
            DataActor actor = new DataActor();
            DataFrame frame = RunInstaRec.createFrames(nframes);
            actor.setWorkes(workers);
            actor.setDataFrame(frame);
            actors.add(actor);
        }
        return actors;
    }
    
    public static void main(String[] args){
        //String file = "/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629_DC.hipo";
        String file = "outfile.hipo";

        if(args.length>0) file = args[0];
        HipoReader r = new HipoReader(file);
        
        HipoWriter w = HipoWriter.create("w.h5", r);
        
        DataActorStream stream = new DataActorStream();
        
        stream.setSource(r).setSync(w);
        
                
        ConverterWorker   convert = new ConverterWorker();
        DriftChamberWorker  dcwrk = new DriftChamberWorker();
        TrackFinderWorker  finder = new TrackFinderWorker();
        
        finder.initNetworks();
        
       // List<DataWorker>  workers = Arrays.asList(convert,dcwrk,finder);
        List<DataWorker>  workers = Arrays.asList(convert,dcwrk,finder);
        
        List<DataActor>   actors = RunInstaRec.createActors(8, 16, workers);
        
        stream.addActor(actors);//.addActor(convert2);//.addActor(convert3).addActor(convert4);
                
        stream.run();
        
    }
}
