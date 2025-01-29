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
public class Clas12ServiceChainDebug {
    
    public static DataFrame createFrames(int count){
        DataFrame<Event>  frame = new DataFrame<>();        
        for(int i = 0; i < count; i++) frame.addEvent(new Event(500*1024));
        return frame;
    }
    
    public static List<DataActor>  createActors(int nactors, int nframes, List<DataWorker> workers){
        List<DataActor> actors = new ArrayList<>();
        for(int a = 0; a < nactors; a++){
            DataActor actor = new DataActor();
            DataFrame frame = Clas12ServiceChainDebug.createFrames(nframes);
            actor.setWorkes(workers);
            actor.setDataFrame(frame);
            actors.add(actor);
        }
        return actors;
    }
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/DataSpace/evio/clas_005197/clas_005197.evio.00043.h5";
        
        HipoReader r = new HipoReader(file);
        HipoWriter w = new HipoWriter();
        w.open("output.h5");
        
        DataActorStream stream = new DataActorStream();
        
        Clas12DecoderService decoder = new Clas12DecoderService();
        Clas12FitterService  fitter = new Clas12FitterService();
        Clas12TranslateService translate = new Clas12TranslateService();
        
        decoder.setKeepEvio(true);
        translate.setKeepEvio(true);
        stream.setSource(r).setSync(w);
        List<DataWorker>  workers = Arrays.asList(decoder,fitter,translate);
        List<DataActor>   actors = Clas12ServiceChainDebug.createActors(4,128, workers);
        actors.get(0).setBenchmark(1);
        //for(DataActor act : actors) act.setRunWithFrames(true);
        //for(DataActor a : actors) a.setBenchmark(1);
        //actors.get(0).setBenchmark(1);
        //actors.get(1).setBenchmark(1);
        //actors.get(0).setBenchmark(1);
        stream.addActor(actors);//.addActor(convert2);//.addActor(convert3).addActor(convert4);

        stream.run();
        //actors.get(0).showBenchmark();
    }
}
