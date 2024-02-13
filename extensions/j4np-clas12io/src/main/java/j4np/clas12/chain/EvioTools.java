/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.chain;

import j4np.clas12.decoder.Clas12ConvertService;
import j4np.clas12.decoder.Clas12DecoderService;
import j4np.clas12.decoder.Clas12FitterService;
import j4np.clas12.decoder.Clas12TranslateService;
import j4np.data.base.DataFrame;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class EvioTools {
    
    public static void convert(List<String> inputs, String output){
        HipoWriter w = new HipoWriter();
        w.open(output);
        Collections.sort(inputs);
        DataFrame<Event> frame = new DataFrame<>();
        for(int j = 0; j < 24; j++) frame.addEvent(new Event(1024*1024));
        int counter = 0;
        for(String input : inputs){
            counter++;
            System.out.printf(">>>> processing file %5d/%5d : %s\n",counter,inputs.size(), input);
            Evio2HipoSource source = new Evio2HipoSource();
            source.open(input);
            boolean keepGoing = true;
            while(keepGoing){
                int nread = source.nextFrame(frame);
                for(Event e : frame.getList()) w.addEvent(e);
                if(nread!=frame.getCount()) keepGoing = false;
            }
        }
        w.close();
    }
    
    public static void decode(String input, String output, String schemaDir, int mode, int threads, int frames){
        Clas12ServiceChain chain = new Clas12ServiceChain();
        HipoReader        reader = new HipoReader();
        HipoWriter        writer = new HipoWriter();
        SchemaFactory         sf = new SchemaFactory();
        
        sf.initFromDirectory(schemaDir);
        writer.getSchemaFactory().copy(sf);
        writer.open(output);
        reader.setProgressPrint(false);
        reader.setDebugMode(0);
        reader.open(input);
        
        Clas12DecoderService     decoder = new Clas12DecoderService();
        Clas12FitterService       fitter = new Clas12FitterService();
        Clas12TranslateService translate = new Clas12TranslateService();
        Clas12ConvertService     convert = new Clas12ConvertService(schemaDir);
        
        convert.init(reader);
        
        if(mode>0){
            System.out.printf("::::::::::: service added >> convert evio to hipo5\n");
            chain.addWorker(decoder);
        }
        if(mode>1) {
            System.out.printf("::::::::::: service added >> fit fadc pulses\n");
            chain.addWorker(fitter);
        }
        if(mode>2){
            System.out.printf("::::::::::: service added >> translate detector components\n");
            chain.addWorker(translate);
        }
        if(mode>3){
            System.out.printf("::::::::::: service added >> convert hipo5 to hipo4\n");
            chain.addWorker(convert);
        }
        
        chain.setSource(reader).setSync(writer);
        chain.frameSize = frames;
        chain.numberOfThreads = threads;
        
        chain.process();
    }
    
    public static void main(String[] args){
        String input = "/Users/gavalian/Work/Software/project-10.8/distribution/coatjava/clas_018775_0000_00005.ev6";
        EvioTools.decode(input, "test.h5", "/Users/gavalian/Work/Software/project-10.8/distribution/coatjava/etc/bankdefs/hipo4", 4, 1, 32);
    }
}
