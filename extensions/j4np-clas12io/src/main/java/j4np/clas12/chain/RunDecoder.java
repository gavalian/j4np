/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.chain;

import j4np.clas12.decoder.Clas12DecoderService;
import j4np.clas12.decoder.Clas12FitterService;
import j4np.clas12.decoder.Clas12TranslateService;
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
public class RunDecoder {
    public static DataFrame createFrames(int count){
        DataFrame<Event>  frame = new DataFrame<>();        
        for(int i = 0; i < count; i++) frame.addEvent(new Event());
        return frame;
    }
    
    public static List<DataActor>  createActors(int nactors, int nframes, List<DataWorker> workers){
        List<DataActor> actors = new ArrayList<>();
        for(int a = 0; a < nactors; a++){
            DataActor actor = new DataActor();
            DataFrame frame = RunDecoder.createFrames(nframes);
            actor.setWorkes(workers);
            actor.setDataFrame(frame);
            actors.add(actor);
        }
        return actors;
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/INSTAREC_LDRD/evio/clas_005197.evio.00000.h5";
        DataActorStream stream = new DataActorStream();
        HipoReader r = new HipoReader(file);
        HipoWriter w = new HipoWriter();
        w.open("output.h5");
        
        Clas12DecoderService decoder = new Clas12DecoderService();
        Clas12FitterService   fitter = new Clas12FitterService();
        Clas12TranslateService trans = new Clas12TranslateService();
        stream.setSource(r).setSync(w);
        
        List<DataActor> actors = RunDecoder.createActors(8, 32, Arrays.asList(decoder,fitter, trans));
        
        stream.addActor(actors);
        for(DataActor a : actors) a.setBenchmark(1);
        stream.run();
        
    }
}
