/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.utils;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Schema;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoDoctor;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.FileUtils;
import j4np.utils.ProgressPrintout;
import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionParser;
import j4np.utils.io.OptionStore;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class HipoUtilities extends OptionApplication {
    
    public HipoUtilities(){
        
        super("h5utils");
        OptionStore parser = this.getOptionStore();
        parser.setName("h5utils");
        
        parser.addCommand("-filter", " filter banks from the input files");
        parser.addCommand("-filter2", " filter banks from the input files (improved version)");
        parser.addCommand("-info", " printout information about the file");
        parser.addCommand("-doctor", " fix corrupt hipo file");
        
        parser.getOptionParser("-filter").addRequired("-o", " output file name ")
                .addOption("-t", "0", " tag of the events to filter. other tags are written as is.")
                .addRequired("-b", " filter string separated by (,)")
                .addOption("-r", "DUMMY::DUMMY"," bank list (or expression) to remove (separated by),)")
                .addOption("-s","true", " true - will write schemas only for banks that are kept. false - all schemas")
                .addOption("-e","-", " check if the banks exists to write out event")
                .addOption("-c","2", " output compression level ( 0, 1 or 2)")
                .addOption("-n","-1", " maximum events in the output file");
     
        
        parser.getOptionParser("-filter2").addRequired("-o", " output file name ")
                .addOption("-t", "0", " tag of the events to filter. other tags are written as is.")
                .addRequired("-b", " nodes to save in the output")
                .addRequired("-nodes", "nodes to save in the output format \"12/1,32100/4,34/15\"")
                .addOption("-c","2", " output compression level ( 0, 1 or 2)")
                .addOption("-n","-1", " maximum events in the output file");

        
        parser.addCommand("-replicate", "create a larger file with replicated events from input file");
        parser.getOptionParser("-replicate").addRequired("-o","output file name")
                .addRequired("-r", "how many copies of events to create")
                .addOption("-s", "0", "starting sequential event in the input file")
                .addOption("-n", "number of events in from the input file to replicate");
        
        parser.addCommand("-merge", "merge input files into one big output file");
        
        
        parser.addCommand("-dump", " show content of the hipo file");
        parser.getOptionParser("-dump").addOption("-b","*", "show only banks give by the list")
                .addOption("-e", "*", "advance to events where given banks exist")
                .addOption("-tags", "-1", "tags from the file to read");
    }
    
    
    public static void filter2(List<String> inputFile, String outputFile, 
            String identifiers, String banks,
            int[] tagsToFilter, 
            boolean schemaFilter,
            int maxEvents, 
            int compression){
        
        
        HipoReader reader = new HipoReader();
        reader.setDebugMode(0);
        reader.open(inputFile.get(0));                
        
        
        
        List<int[]> nodes = new ArrayList<>();
        
        if(banks.length()>2) {
            String[] bankList = banks.split(",");
            nodes.addAll(reader.getSchemaFactory().getIdentifiers(Arrays.asList(bankList)));
        }
        
        String[]   tokens = identifiers.split(",");
        
        for(int t = 0; t < tokens.length; t++){
            String[] ids = tokens[t].split("/");
            if(ids.length==2){
                nodes.add(new int[]{
                    Integer.parseInt(ids[0]),
                    Integer.parseInt(ids[1])
                });
            } else {
                System.out.printf("---- error parsing entry [%s]\n",tokens[t]);
            }
        }
        
        HipoWriter writer = new HipoWriter();                        

        writer.setCompressionType(compression);        
        writer.getSchemaFactory().copy(reader.getSchemaFactory());
        writer.open(outputFile);
        reader.close();
        
        Event   inputEvent = new Event();
        Event   outEvent   = new Event();
        System.out.println("\n\n LIST of nodes to Save: \n" + nodes.size());
        System.out.printf("BANKS = %s\n",banks);
        System.out.printf("NODES = %s\n",identifiers);
        for(int[] items : nodes){
            System.out.printf(" saving >>>>>> %5d, %5d\n",items[0],items[1]);
        }
        ProgressPrintout progress = new ProgressPrintout();
        
        for(int i = 0; i < inputFile.size(); i++){
        
            HipoReader ir = new HipoReader(); ir.setDebugMode(0);
            ir.open(inputFile.get(i));
            System.out.println(String.format("****>>>> openning file : %5d/%5d",i+1,inputFile.size()));
            int counter = 0;
            System.out.println("--> number of events = " + ir.getEventCount());
            while(ir.hasNext()==true){
                outEvent.reset();
                ir.nextEvent(inputEvent);
            
                int tag = inputEvent.getEventTag();
                outEvent.setEventTag(tag);

                for(int j = 0; j < nodes.size();j++){
                    inputEvent.copyNode(outEvent, 
                            nodes.get(j)[0], 
                            nodes.get(j)[1]
                    );
                }
                
                if(outEvent.getEventBufferSize()>16){
                    writer.addEvent(outEvent,outEvent.getEventTag());
                }
                
                counter++;
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
    
    public static void filter(List<String> inputFile, String outputFile, 
            
            String regEx, String banksExist, String banksRemove,
            int tagToFilter, 
            boolean schemaFilter, 
            int maxEvents, 
            int compression){
        
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

    public static void fileInfo(String file){
        HipoReader r = new HipoReader(file);
        r.showInfo();
        //r.showRecords();
    }
    
    @Override
    public String getDescription() {
        return "Utilities to manipulated hipo files";
    }
    
    public static void reduce(List<String> files, String output){
        
        HipoReader reader = new HipoReader();
        reader.open(files.get(0));
        
        HipoWriter writer = HipoWriter.create(output, reader);
       
        Bank[] banks = reader.getBanks("REC::Particle","RECAI::Particle");
        Event e = new Event();
        for(int k = 0; k < files.size(); k++){
            reader = new HipoReader(files.get(k));
            while(reader.hasNext()==true){
                reader.next(e);
                e.read(banks);
                boolean writeEvent = false;
                for(int j = 0; j < banks.length; j++){
                    if(banks[j].getRows()>0){
                        int pid = banks[j].getInt("pid", 0);
                        int status = banks[j].getInt("status", 0);
                        if(pid==11&&Math.abs(status)>=2000&&Math.abs(status)<3000){
                            int ipos = 0; int ineg = 0;
                            for(int t = 1; t < banks[j].getRows();t++){
                                int charge = banks[j].getInt("charge", t);
                                int  pstat = banks[j].getInt("status", t);
                                if(Math.abs(pstat)>=2000&&Math.abs(pstat)<3000){
                                    if(charge>0) ipos++;
                                    if(charge<0) ineg++;
                                }
                            }
                            if(ipos>0&&ineg>0) writeEvent = true;
                        }
                    }
                }
                
                if(writeEvent==true) writer.addEvent(e);
            }
        }
        writer.close();
    }
    
    public static void reduceDir(String directory, String output){
        List<String> files = FileUtils.dir(directory,"*.hipo");
        HipoUtilities.reduce(files, output);
    }
    
    public static void reduceDir(String directory, String regex, String output){
        List<String> files = FileUtils.dir(directory,regex);
        HipoUtilities.reduce(files, output);
    }
    
    public static String waitForInput(){
        String line = "";
        Console c = System.console();
        if (c != null) {
            // printf-like arguments
            //c.format(message, args);
            c.format("\nChoose (n=next,p=previous, q=quit, h=help, s=show banks, r=show raw), Type Bank Name or id : ");
            line = c.readLine();
        }
        return line;
    }
    
    public static void printHelpScreen(){
        System.out.println("HELP\n");
        System.out.println("\t    n: next event and show bank content");
        System.out.println("\t    s: show banks for current event");
        System.out.println("\t    r: show raw node info for current event");
        System.out.println("\t    c: show user configurations of the file");
        System.out.println("\t    q: quit");
        System.out.println("\t    d: describe the bank [give bank name]");
        System.out.println("\t  g/i: show data for node group=g and item=i");
        
        System.out.println("\n\nthis is the way...\n");
        
        
    }
    
    public static void hipoDump(String file, String banksShow, String banksExist, List<Long> tagsList){
        
        
        long[] tags = new long[tagsList.size()];
        for(int i = 0; i < tags.length; i++) tags[i] = tagsList.get(i);
        
        HipoDump hd = new HipoDump(file,banksShow,banksExist, tags);
        
        boolean exitLoop = false;
        
        while(exitLoop==false){
            String response = "n";
            
            response = HipoUtilities.waitForInput();
            
            if(response.equals("q")||response.equals("Q")) {
                exitLoop=true; break;
            }
            
            if(response.equals("s")){
                hd.show();
            }
            
            if(response.equals("h")){
               HipoUtilities.printHelpScreen();
            }
            
            if(response.equals("n")){
                hd.advance();
                hd.show();
            }
            
            if(response.equals("r")){
                //hd.advance();
                System.out.println();
                hd.showRaw();
            }
            
            if(response.contains("/")==true){
                String[] tokens = response.split("/");
                if(tokens[0].startsWith("d")==true){
                    hd.describe(tokens[1].trim());
                } else {
                    hd.show(Integer.parseInt(tokens[0].trim()), Integer.parseInt(tokens[1].trim()));
                }
            } else {
            
                if(response.startsWith("goto")){
                    String[] tokens = response.split("\\s+");
                    Integer order = Integer.parseInt(tokens[1]);
                    hd.gotoEvent(order);
                }
                if(response.trim().matches("-?\\d+(\\.\\d+)?")==true){
                    hd.show(Integer.parseInt(response.trim())-1);
                } else {
                    if(response.length()>2&&response.startsWith("goto")==false){
                        String   bankName = response.trim();
                        hd.show(bankName);
                    }
                }
            }
        }
        
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
        
        
        if(parser.getCommand().compareTo("-filter2")==0){
            
            List<String>  inputFiles    = parser.getOptionParser("-filter2").getInputList();
            String        outputFile    = parser.getOptionParser("-filter2").getOption("-o").stringValue();
            String        regExpression = parser.getOptionParser("-filter2").getOption("-b").stringValue();
            String        nodes = parser.getOptionParser("-filter2").getOption("-nodes").stringValue();
            Integer       tagToFilter   = parser.getOptionParser("-filter2").getOption("-t").intValue();
            Integer       maxEvents    = parser.getOptionParser("-filter2").getOption("-n").intValue();
            Integer       compression    = parser.getOptionParser("-filter2").getOption("-c").intValue();
            boolean       schemaFilter  = true;
           // String    schemaFilterOption = parser.getOptionParser("-filter2").getOption("-s").stringValue();
           // if(schemaFilterOption.contains("false")==true) schemaFilter = false;
            
            HipoUtilities.filter2(inputFiles, outputFile, 
                    nodes,regExpression,new int[]{tagToFilter},schemaFilter,maxEvents,compression);            
        }
        
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
        
        if(parser.getCommand().compareTo("-dump")==0){
            OptionParser p = parser.getOptionParser("-dump");
            
            String tags = p.getOption("-tags").stringValue();
            List<Long>  filetags = new ArrayList<>();
            if(tags.compareTo("-1")!=0){
                String[] tokens = tags.split(":");
                for(int i = 0; i < tokens.length; i++) filetags.add(Long.parseLong(tokens[i]));
            }
            
            HipoUtilities.hipoDump(p.getInputList().get(0), p.getOption("-b").stringValue(),p.getOption("-e").stringValue(), filetags);
        }
        
        if(parser.getCommand().compareTo("-info")==0){
            OptionParser p = parser.getOptionParser("-info");
            
            HipoUtilities.fileInfo(p.getInputList().get(0));
        }
        
        if(parser.getCommand().compareTo("-doctor")==0){
            OptionParser p = parser.getOptionParser("-doctor");
            HipoDoctor doctor = new HipoDoctor();
            doctor.scanCure(p.getInputList().get(0));
        }
        return true;
    }
    
    public static void main(String[] args){
        //HipoFilterWorker.executeProgram("/Users/gavalian/Work/dataspace/denoise/out.ev.bg.hipo_rec.hipo", "test.h5");
    }
}
