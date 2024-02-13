/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.et;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.hipo5.base.RecordFrame;
import j4np.hipo5.data.Event;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
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
public class DataSourceEt implements DataSource {
    
    List<Event> etEvents = new ArrayList<>();
    
    private Boolean  connectionOK = false;
    private String   etRingHost   = "localhost";
    private Integer  etRingPort   = 11111;
    private String   etStation    = "h5_frame";
    
    private EtSystem sys = null;
    private EtAttachment  myAttachment = null;
    private Boolean       remoteConnection = false;
    private Integer       MAX_NEVENTS = 1;
    private int           currentEventPosition = 0;
    private RecordFrame   EtFrame = new RecordFrame();
    
    public DataSourceEt(String host){
        this.etRingHost = host;
        this.etRingPort = EtConstants.serverPort;
        this.remoteConnection = true;
    }
    
    @Override
    public void open(String url) {
        System.out.println("[DataSourceEt] -->>> connecting to host : [" +
                this.etRingHost + "]  FILE [" + url + "]  PORT [" + 
                this.etRingPort + "]");
        try {
            this.connectionOK = true;
            String etFile = url;
            
            EtSystemOpenConfig config = new EtSystemOpenConfig( etFile,this.etRingHost,this.etRingPort);
            if(this.remoteConnection==true){
                config.setConnectRemotely(true);
            }
            //config.setConnectRemotely(true);
            //System.out.println("-------------->>>>> CONNECTING REMOTELY");            
            //System.out.println("-------------->>>>> CONNECTING LOCALY");            
            sys = new EtSystem(config);
            sys.setDebug(EtConstants.debugInfo);
            sys.open();
            
            EtStationConfig statConfig = new EtStationConfig();
            //statConfig.setBlockMode(EtConstants.stationBlocking);
            statConfig.setBlockMode(EtConstants.stationNonBlocking);
            
            statConfig.setUserMode(EtConstants.stationUserMulti);
            statConfig.setRestoreMode(EtConstants.stationRestoreOut);
            //EtStation station = sys.createStation(statConfig, "GRAND_CENTRAL");
            EtStation station = sys.createStation(statConfig, this.etStation);
            
            myAttachment = sys.attach(station);
            
            //this.loadEvents();
            //sys.detach(myAttachment);
            //System.out.println("[ET-RING] ----> opened a stream with events # = " + this.readerEvents.size());
            
        } catch (EtException ex) {
            this.connectionOK = false;
            ex.printStackTrace();
        } catch (IOException | EtTooManyException ex) {
            this.connectionOK = false;
            Logger.getLogger(DataSourceEt.class.getName()).log(Level.SEVERE, null, ex);            
        } catch (EtDeadException | EtClosedException | EtExistsException ex) {
            Logger.getLogger(DataSourceEt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.printf("[DataSourceEt] -->>> connection status %s\n", sys.alive());
    }

    @Override
    public boolean hasNext() {
        return true;
    }
    
    private void takeEventsFromRing(){
        if(this.connectionOK == false){
            System.out.println("[EvioETSource] ---->  connection was not estabilished...");
        }
        if(sys.alive()==true){
           
            try {
                
                EtEvent[] events = sys.getEvents(myAttachment, Mode.SLEEP, null, 0, this.MAX_NEVENTS);
                System.out.printf("[et-ring] >>> recived %d events\n",events.length );
                ByteBuffer buffer = events[0].getDataBuffer();
                EtFrame.read(buffer.array(), 0, events[0].getLength());
                EtFrame.show();
                
            } catch (EtException | EtDeadException | EtClosedException | 
                    EtEmptyException | EtBusyException | EtTimeoutException | 
                    EtWakeUpException | IOException ex) {
                Logger.getLogger(DataSourceEt.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    @Override
    public boolean next(DataEvent event) {
        this.takeEventsFromRing(); return true;
    }

    @Override
    public int position() {
        return 1;
    }

    @Override
    public boolean position(int pos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int entries() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int nextFrame(DataFrame frame) {
        this.takeEventsFromRing();
        EtFrame.getEvents(etEvents);
        frame.reset();frame.addEvents(etEvents);
        return frame.getCount();
    }
    
}
