/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.decoder;

import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Node;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionStore;

/**
 *
 * @author gavalian
 */
public class Clas12DecoderApp extends OptionApplication {

    public Clas12DecoderApp(){
        super("c12");
        OptionStore parser = this.getOptionStore();
        parser.setName("c12data");
        parser.addCommand("-decode", " decode evio file into hipo");
        parser.getOptionParser("-decode")
                .addOption("-f", "32", "data frame size")
                .addOption("-t", "4", "number of threads to use")
                .addRequired("-o", "ouput file name")
                .addOption("-m", "1", "decoding mode ( 1 - write out only adc and tdc banks, 0 - everything)");
        
        
        parser.addCommand("-convert", " converts EVIO file into EVIO6/HIPO format");
        parser.getOptionParser("-convert")
                .addRequired("-o", "ouput file name");
        
    }
    @Override
    public String getDescription() {
        return "clas12 related data tools";
    }

    @Override
    public boolean execute(String[] strings) {
        OptionStore parser = this.getOptionStore();
        parser.parse(strings);
        
        
        if(parser.getCommand().compareTo("-decode")==0){
            Clas12Decoder decoder = new Clas12Decoder();
            Clas12DecoderService service = new Clas12DecoderService();
            service.initialize();
            decoder.numberOfThreads = parser.getOptionParser("-decode").getOption("-t").intValue();
            decoder.frameSize = parser.getOptionParser("-decode").getOption("-f").intValue();
            decoder.initThreadPool();
            decoder.consumer(service);
            //for(int k = 0; k < 500; k++)
            decoder.decodeFile(parser.getOptionParser("-decode").getInputList().get(0), 
                    parser.getOptionParser("-decode").getOption("-o").stringValue());
        }
        if(parser.getCommand().compareTo("-convert")==0){
            this.convert(parser.getOptionParser("-convert").getInputList().get(0),
                    parser.getOptionParser("-convert").getOption("-o").stringValue());
            
        }
        
        return true;
    }
    
    public void convert(String input, String output){
        EvioFile file = new EvioFile();
        file.open(input);
        HipoWriter w = new HipoWriter();
        w.open(output);
        Event e = new Event();
        EvioEvent evio = new EvioEvent();
        while(file.hasNext()){
            file.next(evio);
            e.reset();
            Node node = new Node(1,11,evio.getBuffer().array(),0,evio.getSize()*4+8);
            e.write(node);
            w.add(e);
        }
        w.close();
    }
}
