/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.level3.data;

import j4np.data.evio.EvioEvent;
import j4np.data.evio.EvioFile;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.coda.et.EtAttachment;
import org.jlab.coda.et.EtConstants;
import org.jlab.coda.et.EtEvent;
import org.jlab.coda.et.EtStation;
import org.jlab.coda.et.EtSystem;
import org.jlab.coda.et.EtSystemOpenConfig;
import org.jlab.coda.et.enums.Mode;
import org.jlab.coda.et.exception.EtBusyException;
import org.jlab.coda.et.exception.EtClosedException;
import org.jlab.coda.et.exception.EtDeadException;
import org.jlab.coda.et.exception.EtEmptyException;
import org.jlab.coda.et.exception.EtException;
import org.jlab.coda.et.exception.EtTimeoutException;
import org.jlab.coda.et.exception.EtWakeUpException;

/**
 *
 * @author gavalian
 */
public class Evio2Et {
    
    String etName = null, etHost = null, netInterface = null;
    int port = EtConstants.serverPort;
    int group = 1;
    int delay = 0;
    int size  = 120*1024;
    int chunk = 1;
    boolean verbose = false;
    boolean remote  = true;
    int[] con = null;
    EtSystem sys = null;
    EtAttachment att = null;
    long  totalEventsProduced = 0;
    long  totalTimeProduced = 0;
    
    String evioFile = "";
    
    public Evio2Et(String file, String host){
        etName = file; etHost = host; chunk = 20;
    }
    
    public void setEvioFile(String filename){
        this.evioFile = filename;
    }
        
    public void setDelay(int td){
        delay = td;
    }
    
    public void run(){
        
        EvioFile file = new EvioFile();
        file.open(evioFile);
        
        EvioEvent event = new EvioEvent();
        
        while(true){
            
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(Evio2Et.class.getName()).log(Level.SEVERE, null, ex);
            }
            long then = System.nanoTime();
            EtEvent[] events = produce(chunk);
            
            int nEvents = events.length;
            for(int e = 0; e < nEvents; e++){
                if(file.hasNext()==false){
                    System.out.println("[evio2et] >>> reopening file : " + evioFile);
                    file = new EvioFile();
                    file.open(evioFile);
                }
                
                file.next(event);
                events[e].setByteOrder(ByteOrder.LITTLE_ENDIAN);
                //System.out.println("event size = " + event.bufferLength() + " " + event.getBuffer().capacity());
                System.arraycopy(event.getBuffer().array(), 0, 
                        events[e].getDataBuffer().array(), 0, event.getBuffer().capacity());
                try {
                    events[e].setLength(event.getBuffer().capacity());
                } catch (Exception ex) { System.out.println("[evio2et] error setting event size....");}
            }
            try {
                sys.putEvents(att, events);
            } catch (IOException ex) {
                Logger.getLogger(Evio2Et.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtException ex) {
                Logger.getLogger(Evio2Et.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtDeadException ex) {
                Logger.getLogger(Evio2Et.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtClosedException ex) {
                Logger.getLogger(Evio2Et.class.getName()).log(Level.SEVERE, null, ex);
            }
            long now = System.nanoTime();
            totalEventsProduced += events.length;
            double time = now - then;
            totalTimeProduced += (now-then);
            double rate =  ((double) totalEventsProduced*1000000000)/totalTimeProduced;
            System.out.printf("[evio2et] events %8d, total %9d. time = %12.2f msec, rate = %8.2f evt/sec\n",
                    events.length,totalEventsProduced, ((double) (now-then))/1000000,
                    rate);
        }
    }
    
    public EtEvent[] produce(int chunk){
        EtEvent[] mevs = null;
        
        try {
            mevs = sys.newEvents(att, Mode.SLEEP, false, 0, chunk, size, group);
            
            // example of how to manipulate events
                if (false) {
                    for (int j = 0; j < mevs.length; j++) {
                        // put integer (j + startingVal) into data buffer
                        //int swappedData = Integer.reverseBytes(j + startingVal);
                        //mevs[j].getDataBuffer().putInt(swappedData);
                        // big endian by default
                        mevs[j].setByteOrder(ByteOrder.LITTLE_ENDIAN);
                        // set data length to be 4 bytes (1 integer)
                        mevs[j].setLength(4);
                        // set event's control array
                        mevs[j].setControl(con);
                    }
                    //startingVal += mevs.length;
                }
        } catch (Exception ex) {
            Logger.getLogger(Evio2Et.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return mevs;
    }
    
    public void connect(){
        try {
            // Make a direct connection to ET system's tcp server
            EtSystemOpenConfig config = new EtSystemOpenConfig(etName, etHost, port);
            config.setConnectRemotely(remote);
            if (netInterface != null) config.setNetworkInterface(netInterface);

            // create ET system object with verbose debugging output
            sys = new EtSystem(config);
            if (verbose) sys.setDebug(EtConstants.debugInfo);
            sys.open();

            // get GRAND_CENTRAL station object
            EtStation gc = sys.stationNameToObject("GRAND_CENTRAL");

            // attach to GRAND_CENTRAL
            att = sys.attach(gc);

            con = new int[EtConstants.stationSelectInts];
            for (int i=0; i < EtConstants.stationSelectInts; i++) {
                con[i] = i+1;
            }
        } catch (Exception ex) {
            System.out.println("Error using ET system as producer");
            ex.printStackTrace();
        }
            
    }
    
    
    public static void main(String[] args){
        
        String etfile = "/tmp/etlocal";
        String ethost = "localhost";
        
        if(args.length>1){
            etfile = args[0];
            ethost = args[1];
        }
        Evio2Et evio2et = new Evio2Et(etfile,ethost);
        //evio2et.setEvioFile("/Users/gavalian/Work/DataSpace/evio/clas_003852.evio.981");
        evio2et.setEvioFile("/Users/gavalian/Work/DataSpace/evio/clas_011878.evio.00001");
        evio2et.setDelay(500);
        evio2et.connect();
        
        evio2et.run();
    }
}
