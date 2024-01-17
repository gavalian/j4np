/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.ccdb;

import j4np.clas12.ccdb.DatabaseProvider;
import j4np.clas12.ccdb.RCDBConstants;
import j4np.clas12.ccdb.RCDBProvider;
import j4np.clas12.decoder.DetectorType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class DatabaseManager {
    
    Map<Integer,DecoderDatabase>     database = new LinkedHashMap<>();
    Map<Integer,ParameterDatabase> parameters = new LinkedHashMap<>();
    
    public DecoderDatabase get(int run){
        if(database.containsKey(run)==false) load_ccdb(run);
        return database.get(run);
    }
    
    public ParameterDatabase parameter(int run){
        if(parameters.containsKey(run)==false) load_rcdb(run);
        return parameters.get(run);
    }
    
    protected synchronized void load_rcdb(int run){
        if(parameters.containsKey(run)) return;
        ParameterDatabase  db = new ParameterDatabase();
        RCDBProvider provider = new RCDBProvider();
        RCDBConstants    data = provider.getConstants(run);
        //data.show();
        db.torus = data.getDouble("torus_scale");
        db.solenoid = data.getDouble("solenoid_scale");
        provider.disconnect();
        parameters.put(run, db);
    }
    
    protected synchronized void load_ccdb(int run){
        DecoderDatabase db = new DecoderDatabase();
        if(database.containsKey(run)) return;
        DatabaseProvider p = new DatabaseProvider(run,"default");         
         for(Map.Entry<Integer,String> entry : db.detectorTables.entrySet()){
             Map<Long,Long> map = p.getTranslation(run, entry.getKey(),entry.getValue());
             System.out.printf("decoder-service: map (type = %4d) (size = %9d) : initialization: %s\n",
                     entry.getKey(),map.size(),entry.getValue());
             db.crateMap.putAll(map);
         }
         System.out.printf("decoder-service: map initialized size = %d\n",db.crateMap.size());
         
         for(Map.Entry<Integer,String> entry : db.fadcTables.entrySet()){
             Map<Long,Long> map = p.getFadc(run, entry.getKey(),entry.getValue());
             System.out.printf("decoder-service: map  (type = %4d) (size = %9d) : initialization: %s\n",
                     entry.getKey(),map.size(),entry.getValue());
             db.fadcMap.putAll(map);
         }
         System.out.printf("decoder-service: fadc initialized size = %d\n",db.fadcMap.size());
         database.put(run, db);
    }
    
    public static class ParameterDatabase {
        public double    torus = 0.0;
        public double solenoid = 0.0;        
    }
    
    public static class DecoderDatabase {
        
        public Map<Long,Long> crateMap = new HashMap<>();
        public Map<Long,Long>  fadcMap = new HashMap<>();  
        
        protected Map<Integer,String> detectorTables = new HashMap<>();    
        protected Map<Integer,String> fadcTables = new HashMap<>();
        
        public DecoderDatabase(){
             detectorTables.put(DetectorType.DC.getDetectorId(),   "/daq/tt/dc");
             detectorTables.put(DetectorType.ECAL.getDetectorId(), "/daq/tt/ec");
             detectorTables.put(DetectorType.RF.getDetectorId(), "/daq/tt/rf");
             detectorTables.put(DetectorType.FTOF.getDetectorId(), "/daq/tt/ftof");
             detectorTables.put(DetectorType.HTCC.getDetectorId(), "/daq/tt/htcc");        
             fadcTables.put(DetectorType.ECAL.getDetectorId(),   "/daq/fadc/ec");
             fadcTables.put(DetectorType.FTOF.getDetectorId(),   "/daq/fadc/ftof");
             fadcTables.put(DetectorType.HTCC.getDetectorId(),   "/daq/fadc/htcc");
        }
        
        public void load(DatabaseProvider p ){
            
        }
    }
}
