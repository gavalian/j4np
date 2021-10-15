/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package j4np.physics.io;

import j4np.physics.PDGDatabase;
import j4np.physics.PDGParticle;
import j4np.physics.PhysicsEvent;
import j4np.utils.io.OptionParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.data.Node;
import org.jlab.jnp.hipo4.data.Schema;
import org.jlab.jnp.hipo4.data.Schema.SchemaBuilder;
import org.jlab.jnp.hipo4.io.HipoWriter;
import org.jlab.jnp.hipo4.operations.BankIterator;
import org.jlab.jnp.hipo4.operations.BankSelector;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;
import org.jlab.jnp.utils.file.FileUtils;


/**
 *
 * @author gavalian
 */
public class LundReader {
    
    
    private final ArrayList<String> inputFiles = new ArrayList<String>();
    private BufferedReader reader = null;
    
    private List<Integer>  particleStatusTable = new ArrayList<Integer>();
    
    public LundReader() {
        
    }
    
    public LundReader(String file) {
        this.addFile(file);
        this.open();
    }
    
    public void addFile(String file) {
        inputFiles.add(file);
    }
    
    public LundReader acceptStatus(int... flags){
        this.particleStatusTable.clear();
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < flags.length; i++){
            str.append(String.format("%8d ", flags[i]));
            this.particleStatusTable.add(flags[i]);
        }
        System.out.println("changed accepted status : " + str.toString());
        return this;
    }
    
    public void open() {
        this.openFile(0);
    }
    
    private Boolean openFile(int counter) {
        try {
            File file = new File(inputFiles.get(counter));
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LundReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
   /* public PhysicsEvent getEvent() {
        return physEvent;
    }*/
    
    public Boolean next() {
        try {

            String header = reader.readLine();
            if (header == null)
                return false;
            String[] tokens = header.trim().split("\\s+");
            // System.err.println("TOKENS size = " + tokens.length);
            // for(int loop = 0; loop < tokens.length; loop++){
            // System.err.println(" token " + loop + " = " + tokens[loop]);
            // }
            if (tokens.length < 10){                
                return false;
            }
            Integer nrows = Integer.parseInt(tokens[0]);
            //System.out.println(" nrows = " + nrows);
            if (nrows < 1)
                return false;
            
            for (int loop = 0; loop < nrows; loop++) {
                String particleLine = reader.readLine();
                if (particleLine != null) {
                    String[] params = particleLine.trim().split("\\s+");
                    // System.err.println("PARAMS LENGTH = " + params.length);
                    if (params.length == 14) {
                        
                        int pid = Integer.parseInt(params[3]);
                        // System.err.println("PID = " + pid);
                        int status = Integer.parseInt(params[2]);
                        double px = Double.parseDouble(params[6]);
                        double py = Double.parseDouble(params[7]);
                        double pz = Double.parseDouble(params[8]);
                        double vx = Double.parseDouble(params[11]);
                        double vy = Double.parseDouble(params[12]);
                        double vz = Double.parseDouble(params[13]);
                        //if (status == 1) {
                        //physEvent.addParticle(new Particle(pid, px, py, pz, vx, vy, vz));
                        //}
                    }
                }
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(LundReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
   
    public Boolean next(Bank evBank) {
        try {

            String header = reader.readLine();
            if (header == null)
                return false;
            String[] tokens = header.trim().split("\\s+");
            // System.err.println("TOKENS size = " + tokens.length);
            // for(int loop = 0; loop < tokens.length; loop++){
            // System.err.println(" token " + loop + " = " + tokens[loop]);
            // }
            if (tokens.length < 10){
                evBank.setRows(0);
                return false;
            }
            
            Integer nrows = Integer.parseInt(tokens[0]);
            //System.out.println(" nrows = " + nrows);
            if (nrows < 1){
                evBank.setRows(0);
                return false;
            }
            evBank.setRows(nrows);
            
            for (int loop = 0; loop < nrows; loop++) {
                String particleLine = reader.readLine();
                if (particleLine != null) {
                    String[] params = particleLine.trim().split("\\s+");
                    // System.err.println("PARAMS LENGTH = " + params.length);
                    if (params.length == 14) {
                        
                        int pid = Integer.parseInt(params[3]);
                        // System.err.println("PID = " + pid);
                        int charge = (int) Double.parseDouble(params[1]);
                        int status = Integer.parseInt(params[2]);
                        double px = Double.parseDouble(params[6]);
                        double py = Double.parseDouble(params[7]);
                        double pz = Double.parseDouble(params[8]);
                        double vx = Double.parseDouble(params[11]);
                        double vy = Double.parseDouble(params[12]);
                        double vz = Double.parseDouble(params[13]);
                        evBank.putInt("pid", loop, pid);
                        evBank.putFloat("px", loop, (float) px);
                        evBank.putFloat("py", loop, (float) py);
                        evBank.putFloat("pz", loop, (float) pz);
                        evBank.putFloat("vx", loop, (float) vx);
                        evBank.putFloat("vy", loop, (float) vy);
                        evBank.putFloat("vz", loop, (float) vz);
                        evBank.putInt("charge", loop, charge);
                        if(status!=1){
                            evBank.putInt("status", loop, -Math.abs(status));
                        } else {
                            evBank.putInt("status", loop, 1);
                        }
                        //if (status == 1) {
                        //physEvent.addParticle(new Particle(pid, px, py, pz, vx, vy, vz));
                        //}
                    }
                }
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(LundReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static void lund2hipo(String[] arguments){
        OptionParser parser = new OptionParser();
        parser.addOption("-o", "out.hipo", "output file name"); 
        parser.parse(arguments);
        
        List<String>  fileList = parser.getInputList();
        
        SchemaBuilder schemaBuilder = new SchemaBuilder("mc::event",22001,1);
        schemaBuilder.addEntry("pid", "I", "");
        schemaBuilder.addEntry("px", "F", "");
        schemaBuilder.addEntry("py", "F", "");
        schemaBuilder.addEntry("pz", "F", "");
        schemaBuilder.addEntry("vx", "F", "");
        schemaBuilder.addEntry("vy", "F", "");
        schemaBuilder.addEntry("vz", "F", "");
        schemaBuilder.addEntry("charge", "I", "");
        schemaBuilder.addEntry("beta", "F", "");
        schemaBuilder.addEntry("chi2", "F", "");
        schemaBuilder.addEntry("status", "I", "");

        Schema schema = schemaBuilder.build();
        
        Event hipoEvent = new Event();
        BankSelector  selector = new BankSelector(schema);
        selector.add("status==1");
        
        ProgressPrintout progress = new ProgressPrintout();
        
        HipoWriter writer = new HipoWriter();
        writer.getSchemaFactory().addSchema(schema);        
        
        writer.setMaxSize(16*1024*1024).setMaxEvents(1000000);
        writer.setCompressionType(2);
        
        String outputFileName = parser.getOption("-o").stringValue();
        
        writer.open(outputFileName);
        
        int totalEvents = 0;
        
        BankIterator iter = new BankIterator();
        
        for(String file : fileList) {            
            System.out.println("adding file ----> " + file);
            LundReader reader = new LundReader();
            reader.addFile(file);
            
            reader.open();
            Bank eventBank = new Bank(schema);
            int eventCounter = 0;
            while( reader.next(eventBank)){
                //reader.nextEvent(event);
                progress.updateStatus();
                hipoEvent.reset();
                hipoEvent.write(eventBank);
                
                selector.getIterator(hipoEvent, iter);
                Bank eventBankReduced = selector.reduceBank(iter);
                //eventBank.show();
                //eventBankReduced.show();
                hipoEvent.reset();
                hipoEvent.write(eventBankReduced);
                
                writer.addEvent(hipoEvent);
                eventCounter++;
                totalEvents++;
            }            
        }
        System.out.printf("\nprocessed %d files, total of %d events\n",
                fileList.size(),totalEvents);
        writer.close();
    }
    

    public static void main(String[] args){
        
        String directory = "/Users/gavalian/Work/temp/clasdis/eventfiles/0000.dat";
        
        List<String> fileList = FileUtils.getFileListInDir(directory);
                
        if(args.length>0){
            directory = args[0];
        }
        
        fileList.clear();fileList.add(directory);
        
        SchemaBuilder schemaBuilder = new SchemaBuilder("mc::event",22001,1);
        schemaBuilder.addEntry("pid", "I", "");
        schemaBuilder.addEntry("px", "F", "");
        schemaBuilder.addEntry("py", "F", "");
        schemaBuilder.addEntry("pz", "F", "");
        schemaBuilder.addEntry("vx", "F", "");
        schemaBuilder.addEntry("vy", "F", "");
        schemaBuilder.addEntry("vz", "F", "");
        schemaBuilder.addEntry("charge", "I", "");
        schemaBuilder.addEntry("beta", "F", "");
        schemaBuilder.addEntry("chi2", "F", "");
        schemaBuilder.addEntry("status", "I", "");

        Schema schema = schemaBuilder.build();
        
        //schema.parse("I6FI2FI");
        //schema.setNames("pid:px:py:pz:vx:vy:vz:charge:beta:chi2pid:status");
        
        
        Event hipoEvent = new Event();
        ProgressPrintout progress = new ProgressPrintout();
        
        HipoWriter writer = new HipoWriter();
        writer.getSchemaFactory().addSchema(schema);
        
        writer.setCompressionType(2);
        
        writer.setMaxSize(16*1024*1024).setMaxEvents(1000000);
        writer.setCompressionType(2);
        
        writer.open("sidis_mc_data.hipo");
        
        int counter = 0;
        int eventCounter = 0;
        Bank eventBank = new Bank(schema);
        
        for(String file : fileList) {
            
            System.out.println("adding file ----> " + file);
            LundReader reader = new LundReader();
            reader.addFile(file);
            
            reader.open();

            int eventCounterFile = 0;
            
            while( reader.next(eventBank)){
                //reader.nextEvent(event);
                progress.updateStatus();
                eventCounter++;
                eventCounterFile++;

                hipoEvent.reset();
                hipoEvent.write(eventBank);
                //hipoEvent.printEventBuffer();
                //hipoEvent.show();                
                /*Event ev = new Event();                
                //ByteBuffer buffer = hipoEvent.getEventBuffer();
                ByteBuffer buffer = ev.getEventBuffer();
                System.out.print(" BUFFER : ");
                for(int i = 0; i < 16 ; i ++){
                    System.out.print(String.format("%02X ", buffer.get(i)));
                }
                System.out.println();*/
                writer.addEvent(hipoEvent);
            }
            counter++;
            System.out.println(" number of event processed = " + eventCounterFile + "  total = " + eventCounter);
            //if(eventCounter>1500000) break;
            //if(counter>5) break;
        }
        writer.close();
        System.out.println(progress.getUpdateString());
    }
}
