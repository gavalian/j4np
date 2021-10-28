/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import j4np.physics.PhysicsReaction;
import j4np.physics.VectorOperator;
import j4np.physics.io.LundReader;
import j4np.utils.io.OptionParser;
import j4np.utils.io.OptionStore;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;
import org.jlab.jnp.hipo4.io.HipoWriter;
import twig.data.DataSetSerializer;
import twig.data.H1F;

/**
 *
 * @author gavalian
 */
public class ClasSimulation {
    
    
    public static void hipoAnalysis(String[] arguments){
        OptionParser parser = new OptionParser();
        
        parser.addRequired("-o", "output file name");
        
        parser.parse(arguments);
        
        List<String> inputs = parser.getInputList();
        
        PhysicsReaction reaction = new PhysicsReaction("11:211:-211:X+:X-:Xn",10.5);
        reaction.addVector(reaction.getVector(), "-[11]-[211]-[-211]");
        reaction.addVector("[11]");
        reaction.addVector("[211]");
        reaction.addVector("[-211]");
        reaction.addEntry("mxepipi", 0, VectorOperator.OperatorType.MASS, 0.5, 2.0);
        
        HipoChain chain = new HipoChain();
        chain.addFiles(inputs);
        chain.open();
        
        H1F h = new H1F("h100",120,0.4,1.5);
        
        ClasEvent clas = ClasEvent.with(chain, new String[]{"REC::Particle"});
        //ClasEvent clas = ClasEvent.with(chain, new String[]{"MC::Particle"});
        clas.setEventType(ClasEvent.EventType.FWD_TRIGGER);
        //clas.setEventType(ClasEvent.EventType.SIMULATION);
        Bank bank = chain.getBank("MC::Particle");
        
        Event event = new Event();
        
        while(chain.hasNext()==true){
            chain.nextEvent(event);
            event.read(bank);
            //bank.show();
            clas.read(event);
            //System.out.println(clas.toLundString());
            //System.out.println("VALID = " + reaction.isValid(clas) 
            //        + "  " + clas.getOrderByPid(211, 0)
            //+"  " + clas.countByPid(211) 
            //                    +"  " + clas.countByPid(-211) 
            //                    +"  " + clas.countByPid(11) 
            //        );
            if(reaction.isValid(clas)==true){
                reaction.apply(clas);
                //System.out.println("---- event found ");
                //System.out.println(clas.toLundString());
                System.out.println(reaction.operators().get(0).getValue(VectorOperator.OperatorType.MASS));
                if(reaction.checkCuts()==true){
                    h.fill(reaction.operators().get(0).getValue(VectorOperator.OperatorType.MASS));
                }
                System.out.println(reaction.toString());
            }            
        }
        
        DataSetSerializer.export(h, 
                parser.getOption("-o").stringValue(), "mc/analysis");
        
    }
    
    public static void hipoStats(String[] arguments){
                        
        OptionParser parser = new OptionParser();
        
        //parser.addRequired("-o", "output file name");
        
        parser.parse(arguments);
        
        List<String> inputs = parser.getInputList();
        
        
        HipoChain chain = new HipoChain();
        chain.addFiles(inputs);
        chain.open();
        
        Bank trk = chain.getBank("TimeBasedTrkg::TBTracks");
        
        Event event = new Event();
        
        int[]  trkRows = new int[6];
        int counter = 0;
        while(chain.hasNext()==true){
            chain.nextEvent(event);
            event.read(trk);
            int nrows = trk.getRows();
            if(nrows<6) trkRows[nrows]++;
            counter++;
        }
        System.out.printf("# processed events count %8d\n",counter);
        for(int i = 0; i < trkRows.length; i++){
            double fraction = ((double) trkRows[i])/counter;
            System.out.printf("# track %5d : count = %8d\n",i,trkRows[i]);
        }
        System.out.println();
    }
    
    public static void hipo2hipo(String[] arguments){
                        
        OptionParser parser = new OptionParser();
        
        parser.addRequired("-o", "output file name");
        
        parser.parse(arguments);
        
        List<String> inputs = parser.getInputList();
        
        PhysicsReaction reaction = new PhysicsReaction("11:211:-211:X+:X-:Xn",10.5);
        reaction.addVector(reaction.getVector(), "-[11]-[211]-[-211]");
        reaction.addVector("[11]");
        reaction.addVector("[211]");
        reaction.addVector("[-211]");
        reaction.addEntry("mxepipi", 0, VectorOperator.OperatorType.MASS, 0.5, 1.4);
        reaction.addEntry("th_e",    1, VectorOperator.OperatorType.THETA_DEG, 4.0, 40.0);
        reaction.addEntry("th_pip",  2, VectorOperator.OperatorType.THETA_DEG, 4.0, 40.0);
        reaction.addEntry("th_pim",  3, VectorOperator.OperatorType.THETA_DEG, 4.0, 40.0);
        
        HipoChain chain = new HipoChain();
        chain.addFiles(inputs);
        chain.open();
        
        HipoWriter writer = new HipoWriter(chain.getSchemaFactory());
        writer.open(parser.getOption("-o").stringValue());
        
        ClasEvent clas = ClasEvent.with(chain, new String[]{"mc::event"});
        clas.setEventType(ClasEvent.EventType.SIMULATION);
        
        Event event = new Event();
        
        while(chain.hasNext()==true){
            chain.nextEvent(event);
            clas.read(event);            
            if(reaction.isValid(clas)==true){
                reaction.apply(clas);
                if(reaction.checkCuts()==true){
                    writer.addEvent(event);
                }
                //System.out.println(reaction.toString());
            }            
        }
        writer.close();
    }
    
    public static void hipo2lund(String[] arguments){
        OptionParser parser = new OptionParser();
        
        parser.addRequired("-skip", "number of events to skip");
        parser.addRequired("-n", "number of events to output");
        parser.addOption("-mask","*", "particles to remove");
        parser.addOption("-keep","*", "particle list to keep in the output");
        parser.addOption("-o", "out.lund", "output file name");
        
        parser.parse(arguments);
        
        List<String> inputs = parser.getInputList();
        
        HipoChain chain = new HipoChain();
        chain.addFiles(inputs);
        chain.open();
        
        List<Integer>  mask = new ArrayList<Integer>();
        List<Integer> keeps = new ArrayList<Integer>();
        
        String maskString = parser.getOption("-mask").stringValue();
        String keepString = parser.getOption("-keep").stringValue();
        
        if(maskString.compareTo("*")!=0){
            String[] tokens = maskString.split(":");
            for(int i = 0; i < tokens.length; i++) 
                mask.add(Integer.parseInt(tokens[i]));
        }
        
        if(keepString.compareTo("*")!=0){
            String[] tokens = keepString.split(":");
            for(int i = 0; i < tokens.length; i++) 
                keeps.add(Integer.parseInt(tokens[i]));
        }
                
        ClasEvent clas = ClasEvent.with(chain, new String[]{"mc::event"});
        clas.setEventType(ClasEvent.EventType.SIMULATION);
        
        String outputFileName = parser.getOption("-o").stringValue();
        int    skip = parser.getOption("-skip").intValue();
        int    nevents = parser.getOption("-n").intValue();
        TextFileWriter writer = new TextFileWriter();
        writer.open(outputFileName);
        Event event = new Event();
        for(int i = 0; i < skip; i++) chain.nextEvent(event);
        
        int counter = 0;
        
        while(chain.hasNext()==true&&counter<nevents){
            chain.nextEvent(event);
            clas.read(event);
            
            if(keeps.size()>0){
                for(int k = 0; k < clas.count(); k++){
                    clas.status(k, -900);
                }                
                for(int j = 0; j < keeps.size(); j++){
                    for(int k = 0; k < clas.count(); k++){
                        int pid = clas.pid(k);
                        if(pid==keeps.get(j)) clas.status(k, 1);
                    }
                }
            }
            //String data = StringUtils.clas.toLundString();
            writer.writeString(clas.toLundString());
            counter++;
        }
        
        writer.close();
    }
    
    public static String[] getReducedArguments(String[] args){
        String[] arguments = new String[args.length-1];
        for(int i = 0; i < arguments.length; i++) arguments[i] = args[i+1];
        return arguments;
    }
    
    
    
    public static void main(String[] args){
        
        OptionStore store = new OptionStore();
        
        store.addCommand("-lund2hipo", "convert lund file to hipo file");
        store.addCommand("-stats", "print number of tracks stats from hipo file");
        store.addCommand("-analysis", "analyze mc file");
        store.addCommand("-hipo2lund", "convert hipo file to lund file");        
        store.addCommand("-hipo2hipo", "filter hipo file for given reaction");
        
        store.parse(args);
        
        if(store.getCommand().compareTo("-lund2hipo")==0){
            LundReader.lund2hipo(ClasSimulation.getReducedArguments(args));
        }
        
        
        if(store.getCommand().compareTo("-stats")==0){
            ClasSimulation.hipoStats(ClasSimulation.getReducedArguments(args));
        }
        
        if(store.getCommand().compareTo("-analysis")==0){
            ClasSimulation.hipoAnalysis(ClasSimulation.getReducedArguments(args));
        }
        
        if(store.getCommand().compareTo("-hipo2lund")==0){
            ClasSimulation.hipo2lund(ClasSimulation.getReducedArguments(args));
        }
        
        if(store.getCommand().compareTo("-hipo2hipo")==0){
            ClasSimulation.hipo2hipo(ClasSimulation.getReducedArguments(args));
        }
    }
}
