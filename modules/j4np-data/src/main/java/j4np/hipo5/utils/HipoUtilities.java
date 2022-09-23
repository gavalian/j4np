/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.utils;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.ProgressPrintout;
import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionParser;
import j4np.utils.io.OptionStore;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class HipoUtilities extends OptionApplication {
    
    public HipoUtilities(){
        
        super("hipoutils");
        OptionStore parser = this.getOptionStore();
        parser.setName("hipoUtils");
        
        parser.addCommand("-filter", " filter banks from the input files");
        
        parser.getOptionParser("-filter").addRequired("-o", " output file name ")
                .addOption("-t", "0", " tag of the events to filter. other tags are written as is.")
                .addRequired("-b", " filter string separated by (,)")
                .addOption("-r", "DUMMY::DUMMY"," bank list (or expression) to remove (separated by),)")
                .addOption("-s","true", " true - will write schemas only for banks that are kept. false - all schemas")
                .addOption("-e","-", " check if the banks exists to write out event")
                .addOption("-c","2", " output compression level ( 0, 1 or 2)")
                .addOption("-n","-1", " maximum events in the output file");
     
        parser.addCommand("-replicate", "create a larger file with replicated events from input file");
        parser.getOptionParser("-replicate").addRequired("-o","output file name")
                .addRequired("-r", "how many copies of events to create")
                .addOption("-s", "0", "starting sequential event in the input file")
                .addOption("-n", "number of events in from the input file to replicate");
        
        parser.addCommand("-merge", "merge input files into one big output file");
        
    }

    public static void filter(List<String> inputFile, String outputFile, String regEx, 
            String banksExist, String banksRemove,int tagToFilter, 
            boolean schemaFilter, int maxEvents, int compression){
        
        HipoReader reader = new HipoReader();
        reader.open(inputFile.get(0));
        
        SchemaFactory factory4 = reader.getSchemaFactory().reduce(regEx);
        SchemaFactory factory = factory4.remove(banksRemove);
        List<Schema> schemaExistList = new ArrayList<Schema>();
        
        if(banksExist.compareTo("-")!=0){
            SchemaFactory  schemaExists = reader.getSchemaFactory().reduce(banksExist);            
            schemaExistList = schemaExists.getSchemaList();
        }
        
        factory.show();
        
        HipoWriter writer = new HipoWriter();
        
        List<Schema>   schemaList   = factory.getSchemaList();
        List<Bank>     schemaBanks  = new ArrayList<Bank>();
        
        
        for(Schema schema : schemaList){
            //writer.getSchemaFactory().addSchema(schema);
            schemaBanks.add(new Bank(schema));
        }
        if(schemaFilter==true){
            for(Schema schema : schemaList){
                writer.getSchemaFactory().addSchema(schema);
            }
        } else {
            List<Schema> schemaListFull = reader.getSchemaFactory().getSchemaList();
            for(Schema schema : schemaListFull){
                writer.getSchemaFactory().addSchema(schema);
            }
        }
        writer.setCompressionType(compression);        
        writer.open(outputFile);
        reader.close();
        Event   inputEvent = new Event();
        Event   outEvent   = new Event();
        ProgressPrintout progress = new ProgressPrintout();
        
        for(int i = 0; i < inputFile.size(); i++){
            HipoReader ir = new HipoReader();
            ir.open(inputFile.get(i));
            System.out.println(String.format("****>>>> openning file : %5d/%5d",i+1,inputFile.size()));
            int counter = 0;
            System.out.println("--> number of events = " + ir.getEventCount());
            while(ir.hasNext()==true){
                outEvent.reset();
                ir.nextEvent(inputEvent);
                int tag = inputEvent.getEventTag();
                outEvent.setEventTag(tag);
                
                for(int b = 0; b < schemaBanks.size(); b++){
                    inputEvent.read(schemaBanks.get(b));
                    if(schemaBanks.get(b).getRows()>0){
                        outEvent.write(schemaBanks.get(b));
                    }
                }
                
                counter++;
                //outEvent.show();
                if(tag==tagToFilter){
                    if(outEvent.getEventBufferSize()>16){
                        if(schemaExistList.isEmpty()==true){
                            writer.addEvent(outEvent,outEvent.getEventTag());
                        } else {
                            if(inputEvent.hasBanks(schemaExistList)==true){
                                writer.addEvent(outEvent,outEvent.getEventTag());
                            }
                        }
                    }
                } else {
                    writer.addEvent(inputEvent,inputEvent.getEventTag());
                }
                progress.updateStatus();
                if(maxEvents>0){
                    if(counter>maxEvents){
                        writer.close();
                        return;
                    }
                }
            }
            System.out.println("****>>>> processed events : " + counter);
        }
        writer.close();
    }

    
    @Override
    public String getDescription() {
        return "Utilities to manipulated hipo files";
    }
    
    public static void replicate(String input, String output, int start, int nEvents, int nExpand){
        HipoReader r = new HipoReader(input);
        HipoWriter w = HipoWriter.create(output, r);
        Event e = new Event();
        for(int i = 0; i <  nExpand; i++){
            r.getEvent(e, start);
            for(int k = 0; k < nEvents; k++){
                w.addEvent(e);
                if(r.hasNext()) r.nextEvent(e);
            }
        }
        w.close();
    }
    
    @Override
    public boolean execute(String[] args) {
        OptionStore parser = this.getOptionStore();
        parser.parse(args);
        
        if(parser.getCommand().compareTo("-filter")==0){
            
            List<String>  inputFiles    = parser.getOptionParser("-filter").getInputList();
            String        outputFile    = parser.getOptionParser("-filter").getOption("-o").stringValue();
            String        regExpression = parser.getOptionParser("-filter").getOption("-b").stringValue();
            String        banksRemove = parser.getOptionParser("-filter").getOption("-r").stringValue();
            String        bankExistsFilter = parser.getOptionParser("-filter").getOption("-e").stringValue();
            Integer       tagToFilter   = parser.getOptionParser("-filter").getOption("-t").intValue();
            Integer       maxEvents    = parser.getOptionParser("-filter").getOption("-n").intValue();
            Integer       compression    = parser.getOptionParser("-filter").getOption("-c").intValue();
            boolean       schemaFilter  = true;
            String    schemaFilterOption = parser.getOptionParser("-filter").getOption("-s").stringValue();
            if(schemaFilterOption.contains("false")==true) schemaFilter = false;
            HipoUtilities.filter(inputFiles, outputFile, regExpression, 
                    bankExistsFilter,banksRemove,tagToFilter,schemaFilter,maxEvents,compression);            
        }
        if(parser.getCommand().compareTo("-replicate")==0){
            OptionParser p = parser.getOptionParser("-replicate");
            String output = p.getOption("-o").stringValue();
            int     start = p.getOption("-s").intValue();
            int      nevt = p.getOption("-n").intValue();
            int      nrep = p.getOption("-r").intValue();
            HipoUtilities.replicate(p.getInputList().get(0), output, start, nevt, nrep);
        }
        return true;
    }
    
    public static void main(String[] args){
        //HipoFilterWorker.executeProgram("/Users/gavalian/Work/dataspace/denoise/out.ev.bg.hipo_rec.hipo", "test.h5");
    }
}
