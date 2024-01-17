/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.ccdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.ccdb.Assignment;
import org.jlab.ccdb.CCDB;
import org.jlab.ccdb.TypeTableColumn;

/**
 *
 * @author gavalian
 */
public class DatabaseProvider {
    
    Logger LOGGER = Logger.getLogger(DatabaseProvider.class.getName());
    
    private String   variation  = "default";
    private Integer  runNumber = 10;
    private Date     databaseDate   = new Date();
    //private String databaseAddress = "mysql://clas12reader@clasdb-farm.jlab.org/clas12";
    private String databaseAddress = "mysql://clas12reader@clasdb.jlab.org/clas12";
    
    private org.jlab.ccdb.JDBCProvider provider;
    
    
    public DatabaseProvider(String dbstring, int dbrun, String dbvar){
        variation = dbvar;
        runNumber = dbrun;
        this.databaseAddress = dbstring;
        this.initialize(this.databaseAddress);
    }
    
    public DatabaseProvider(int dbrun, String dbvar){
        variation = dbvar;
        runNumber = dbrun;
        this.initialize(this.databaseAddress);
    }
    
    
    protected final void initialize(String address){
        
        provider = CCDB.createProvider(address);

        /*LOGGER.log(Level.INFO, "[DB] --->  open connection with : " + address);
        LOGGER.log(Level.INFO, "[DB] --->  database variation   : " + this.variation);
        LOGGER.log(Level.INFO, "[DB] --->  database run number  : " + this.runNumber);
        LOGGER.log(Level.INFO, "[DB] --->  database time stamp  : " + databaseDate);
        */
        
        provider.connect();
        
        if(provider.isConnected()){
            LOGGER.log(Level.INFO,"[DB] --->  database connection  : success");
        } else {
            LOGGER.log(Level.SEVERE,"[DB] --->  database connection  : failed");
        }
        
        provider.setDefaultVariation(variation);
        provider.setDefaultDate(databaseDate);
        provider.setDefaultRun(this.runNumber);
    }
    
    public Map<Long,Long> getTranslation(int run, int type, String table){
        Map<Long,Long> map = new LinkedHashMap<>();
        Assignment asgmt = provider.getData(table);
        
        int ncolumns = asgmt.getColumnCount();
        Vector<TypeTableColumn> typecolumn = asgmt.getTypeTable().getColumns();
        List< Vector<String> >  tableRows = new ArrayList< Vector<String> >();
        
        //System.out.println(" typecolumn size = " + typecolumn.size());
        
        //System.out.println(" rows  size = " + tableRows.size());
        for(int loop = 0; loop < ncolumns; loop++){
            String name = typecolumn.get(loop).getName();
                Vector<String> column = asgmt.getColumnValuesString(name);
                tableRows.add(column);
                //System.out.printf(" loop = %d, column size = %d\n",loop,column.size());
        }
        int nrows = tableRows.get(0).size();
        
        for(int nr = 0 ; nr < nrows; nr++){
            String[] values = new String[ncolumns];
            for(int nc = 0; nc < ncolumns; nc++){
                values[nc] = tableRows.get(nc).get(nr);
            }
            long key = DetectorTools.hardwareEncoder(Long.parseLong(values[0]),
                    Integer.parseInt(values[1]),Integer.parseInt(values[2]));
            long value = DetectorTools.softwareEncoder(type,
                    Long.parseLong(values[3]),
                    Long.parseLong(values[4]),
                    Integer.parseInt(values[5]),
                    Integer.parseInt(values[6])
            );
            
            //int[] keys  = new int[3];
            //int[] soft  = new int[5];            
            /*
            DetectorTools.hardwareDecoder(key, keys);
            DetectorTools.softwareDecoder(value, soft);            
            System.out.println(Long.parseLong(values[0]) + " " + 
                    Integer.parseInt(values[1]) + " " + Integer.parseInt(values[2])
                    + "  *** " + Arrays.toString(keys)
            );
            
            System.out.println("\t" + Long.parseLong(values[3]) + " " + 
                    Integer.parseInt(values[4]) + " " + Integer.parseInt(values[5])
                    + " " + Integer.parseInt(values[6])
                    + "  *** " + Arrays.toString(soft)
            );*/
            
            //System.out.printf("%012X -> %012X\n", key, value);
            map.put(key, value);
            //table.addEntryFromString(values);
        }
        return map;
    }
    
    public Map<Long,Long> getFadc(int run, int type, String table){
        Map<Long,Long> map = new LinkedHashMap<>();
        Assignment asgmt = provider.getData(table);        
        int ncolumns = asgmt.getColumnCount();
        Vector<TypeTableColumn> typecolumn = asgmt.getTypeTable().getColumns();
        List< Vector<String> >  tableRows = new ArrayList< Vector<String> >();
        
        //System.out.println(" typecolumn size = " + typecolumn.size());
        
        //System.out.println(" rows  size = " + tableRows.size());
        for(int loop = 0; loop < ncolumns; loop++){
            String name = typecolumn.get(loop).getName();
                Vector<String> column = asgmt.getColumnValuesString(name);
                tableRows.add(column);
                //System.out.printf(" loop = %d, column size = %d\n",loop,column.size());
        }
        int nrows = tableRows.get(0).size();
        
        for(int nr = 0 ; nr < nrows; nr++){
            String[] values = new String[ncolumns];
            for(int nc = 0; nc < ncolumns; nc++){
                values[nc] = tableRows.get(nc).get(nr);
            }
            long key = DetectorTools.hardwareEncoder(Long.parseLong(values[0]),
                    Integer.parseInt(values[1]),Integer.parseInt(values[2]));
            
            long value = DetectorTools.fadcEncoder(type,
                    (long) (Float.parseFloat(values[3])*10),
                    Long.parseLong(values[4]),
                    Integer.parseInt(values[5]),
                    Integer.parseInt(values[6])
            );
            
            /*
            int[] keys  = new int[3];
            int[] soft  = new int[5];  
            DetectorTools.hardwareDecoder(key, keys);
            DetectorTools.fadcDecoder(value, soft); 
            
            System.out.println(Long.parseLong(values[0]) + " " + 
                    Integer.parseInt(values[1]) + " " + Integer.parseInt(values[2])
                    + "  *** " + Arrays.toString(keys)
            );
            
            System.out.println("\t" + Float.parseFloat(values[3]) + " " + 
                    Integer.parseInt(values[4]) + " " + Integer.parseInt(values[5])
                    + " " + Integer.parseInt(values[6])
                    + "  *** " + Arrays.toString(soft)
            );*/
            //System.out.printf("%012X -> %012X\n", key, value);
            map.put(key, value);
            //table.addEntryFromString(values);
        }
        return map;
    }
    
    public static void main(String[] args){
        
        int type = 0;
        if(type==1){
            
            DatabaseProvider p = new DatabaseProvider(10,"default");
            Map<Long,Long> map = p.getTranslation(10, 18,"/daq/tt/ec");
            List<Integer> crates = DetectorTools.getCreates(map);
            System.out.println(crates);
            int[] v = new int[5];
            int[] k = new int[4];
            for(Map.Entry<Long,Long> entry : map.entrySet()){
                
                DetectorTools.hardwareDecoder(entry.getKey(), k);
                DetectorTools.fadcDecoder(entry.getValue(), v);
                //System.out.println(Arrays.toString(k) + "  ===> " + Arrays.toString(v));
            }
        } else {
            DatabaseProvider p = new DatabaseProvider(10,"default");
            Map<Long,Long> map = p.getFadc(10, 18,"/daq/fadc/ec");
            
            List<Integer> crates = DetectorTools.getCreates(map);
            System.out.println(crates);
            int[] v = new int[5];
            int[] k = new int[4];
            for(Map.Entry<Long,Long> entry : map.entrySet()){
                
                DetectorTools.hardwareDecoder(entry.getKey(), k);
                DetectorTools.fadcDecoder(entry.getValue(), v);
                System.out.println(Arrays.toString(k) + "  ===> " + Arrays.toString(v));
            }
        }
    }
}
