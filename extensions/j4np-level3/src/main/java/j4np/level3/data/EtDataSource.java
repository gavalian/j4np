/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.level3.data;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.evio.EvioEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.coda.et.EtAttachment;
import org.jlab.coda.et.EtConstants;
import org.jlab.coda.et.EtEvent;
import org.jlab.coda.et.EtStation;
import org.jlab.coda.et.EtStationConfig;
import org.jlab.coda.et.EtSystem;
import org.jlab.coda.et.EtSystemOpenConfig;
import org.jlab.coda.et.enums.Mode;
import org.jlab.coda.et.exception.EtBusyException;
import org.jlab.coda.et.exception.EtClosedException;
import org.jlab.coda.et.exception.EtDeadException;
import org.jlab.coda.et.exception.EtEmptyException;
import org.jlab.coda.et.exception.EtException;
import org.jlab.coda.et.exception.EtExistsException;
import org.jlab.coda.et.exception.EtTimeoutException;
import org.jlab.coda.et.exception.EtTooManyException;
import org.jlab.coda.et.exception.EtWakeUpException;

/**
 *
 * @author gavalian
 */
public class EtDataSource implements DataSource {
    private Boolean  connectionOK = false;
    private String   etRingHost   = "localhost";
    private Integer  etRingPort   = 11111;
    private String   etStation    = "reader_station";
    private int      etChunkSize  = 10;
    
    private EtSystem               sys = null;
    private EtAttachment  myAttachment = null;
    private Boolean       remoteConnection = true;
    
    public EtDataSource(int chunk){
        etChunkSize = chunk;
    }
    
    @Override
    public void open(String url) {
        try {
            String[] tokens = url.split(":");
            etRingHost = tokens[1];
            if(tokens.length>2) etRingPort = Integer.parseInt(tokens[2]);
            String etFile = tokens[0];
            System.out.printf("et:connect >> connecting to %s , at %s, port %s\n",
                    etFile,this.etRingHost,this.etRingPort);
            
            EtSystemOpenConfig config = new EtSystemOpenConfig( etFile,this.etRingHost,this.etRingPort);
            if(this.remoteConnection==true){
                config.setConnectRemotely(true);
            }
            
            sys = new EtSystem(config);
            sys.setDebug(EtConstants.debugInfo);
            sys.open();
            
            EtStationConfig statConfig = new EtStationConfig();
            //statConfig.setBlockMode(EtConstants.stationBlocking);
            statConfig.setBlockMode(EtConstants.stationNonBlocking);
            
            statConfig.setUserMode(EtConstants.stationUserMulti);
            statConfig.setRestoreMode(EtConstants.stationRestoreOut);
            //EtStation station = sys.createStation(statConfig, "GRAND_CENTRAL");
            //System.out.println("cue = " + statConfig.getCue());
            statConfig.setCue(etChunkSize);
            
            EtStation station = sys.createStation(statConfig, this.etStation);            
            
            myAttachment = sys.attach(station);
            
        } catch (EtException ex) {
            Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EtTooManyException ex) {
            Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EtDeadException ex) {
            Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EtClosedException ex) {
            Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EtExistsException ex) {
            Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public boolean next(DataEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int position() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean position(int pos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int entries() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int nextFrame(DataFrame frame) {
        
        int nEventsMax = frame.getCount();
        int nEventsLoaded = 0;
        if(sys.alive()==true){
            System.out.printf("et::system >> status = alive , attempting fetch, count = %d\n",
                    nEventsMax);                
            try {
                //EtEvent[] events = sys.getEvents(myAttachment, Mode.SLEEP, null, 0, nEventsMax);
                EtEvent[] events = sys.getEvents(myAttachment, Mode.ASYNC, null, 0, nEventsMax);
                if(events!=null){
                    System.out.println("et::info >> loaded events count = " + events.length);
                }
                for(int ev = 0; ev < events.length; ev++){
                    int length = events[ev].getDataBuffer().capacity();
                    DataEvent event = frame.getEvent(ev);
                    event.allocate(length);
                    System.arraycopy(events[ev].getDataBuffer().array(), 0, event.getBuffer().array()
                        , 0, length);
                }
                
                /*int length = events[0].getDataBuffer().capacity();
                EvioEvent event = new EvioEvent();
                event.allocate(length);
                System.arraycopy(events[0].getDataBuffer().array(), 0, event.getBuffer().array()
                        , 0, length);
                System.out.println(">>>>>>>>> scan :");
                event.scan();
                */
            } catch (EtException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtDeadException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtClosedException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtEmptyException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtBusyException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtTimeoutException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EtWakeUpException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EtDataSource.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return nEventsLoaded;
    }
    
}
