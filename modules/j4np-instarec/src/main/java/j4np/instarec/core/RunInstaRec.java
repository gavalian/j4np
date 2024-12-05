/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.data.base.DataActor;
import j4np.data.base.DataActorStream;
import j4np.data.base.DataFrame;
import j4np.utils.FileUtils;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoChain;
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
    public static void benchmark(String file, DataWorker worker){
        HipoReader r = new HipoReader(file);
        Event event = new Event();
        long then = System.currentTimeMillis();
        while(r.next(event)==true){
            worker.accept(event);
        }
        long now = System.currentTimeMillis();
        System.out.printf("time = %d\n",now-then);
    }
    
    
    public static void run(List<String> inputs, String output){
        
        HipoChain chain = new HipoChain(inputs); 
        
        chain.setMaxEvents(2500);
        HipoWriter w = HipoWriter.create(output, chain.getReader());

        DataActorStream stream = new DataActorStream();
        
        stream.setSource(chain).setSync(w);
        
        ConverterWorker   convert = new ConverterWorker();
        DriftChamberWorker  dcwrk = new DriftChamberWorker();
        TrackFinderWorker  finder = new TrackFinderWorker();

        
        finder.initNetworks();

        //RunInstaRec.benchmark("w.h5", finder);
        
       // List<DataWorker>  workers = Arrays.asList(convert,dcwrk,finder);
        //List<DataWorker>  workers = Arrays.asList(convert,dcwrk, finder);
        List<DataWorker>  workers = Arrays.asList(convert,dcwrk, finder);
        
        List<DataActor>   actors = RunInstaRec.createActors(1,32, workers);
        //for(DataActor a : actors) a.setBenchmark(1);
        actors.get(0).setBenchmark(1);
        stream.addActor(actors);//.addActor(convert2);//.addActor(convert3).addActor(convert4);                
        stream.run();
        
    }
    public static void main(String[] args){
        
//        List<String> files = FileUtils.dir("/Users/gavalian/Work/DataSpace/decoded/006677/","*hipo");
       
        //List<String> files = Arrays.asList("output3.hipo");
        List<String> files = Arrays.asList("wd.h5");
        for(String file : files) System.out.println(file);
        
        
        RunInstaRec.run(files, "chain_output.h5");

        //List<String> files = Arrays.asList("/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629.hipo");
        //RunInstaRec.run(files, "chain_output_006595.h5");
       /* String file = "/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629_DC.hipo";
        //String file = "/Users/gavalian/Work/DataSpace/decoded/003841/clas_003841.evio.00000_denoised.hipo";
        //String file = "outfile.hipo";

        if(args.length>0) file = args[0];
        HipoReader r = new HipoReader(file);
        
        HipoWriter w = HipoWriter.create("irec_clas_003841.evio.00000_denoised.h5", r);
        
        DataActorStream stream = new DataActorStream();
        
        stream.setSource(r).setSync(w);
        
                
        ConverterWorker   convert = new ConverterWorker();
        DriftChamberWorker  dcwrk = new DriftChamberWorker();
        TrackFinderWorker  finder = new TrackFinderWorker();

        
        finder.initNetworks();

        //RunInstaRec.benchmark("w.h5", finder);
        
       // List<DataWorker>  workers = Arrays.asList(convert,dcwrk,finder);
        List<DataWorker>  workers = Arrays.asList(convert,dcwrk, finder);
        
        List<DataActor>   actors = RunInstaRec.createActors(8,32, workers);
        for(DataActor a : actors) a.setBenchmark(1);
        actors.get(0).setBenchmark(1);
        stream.addActor(actors);//.addActor(convert2);//.addActor(convert3).addActor(convert4);
                
        stream.run();
        */
    }
}
