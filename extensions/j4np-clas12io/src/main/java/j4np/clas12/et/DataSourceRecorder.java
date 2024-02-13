/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.et;

import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.base.FrameWorker;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.hipo5.io.HipoWriter;
import j4np.utils.io.OptionParser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataSourceRecorder {
    
    private DataSource  dataSource = null;
    private HipoWriter      writer = null;
    DataFrame<Event>     srcEvents = new DataFrame<>();
    
    private List<FrameWorker>  workers = new ArrayList<>();
    private int compressionType = 0;
    
    public DataSourceRecorder(){
        for(int i = 0; i < 120; i++) srcEvents.addEvent(new Event());
    }
    
    public DataSourceRecorder compression(int comp){compressionType = comp; return this;}
    public void open(String file){
        writer = new HipoWriter();
        writer.setCompressionType(compressionType);
        writer.open(file);
    }
    
    public DataSourceRecorder setSource(DataSource src){
        this.dataSource = src; return this;
    }
    
    public HipoWriter getWriter(){ return writer;}
    public DataSourceRecorder addWorker(FrameWorker fw){
        this.workers.add(fw); return this;
    }
    
    public void execute(){
        
        while(dataSource.hasNext()){
            dataSource.nextFrame(srcEvents);
            for(int k = 0; k < this.workers.size(); k++){
                workers.get(k).execute(srcEvents.getList());
            }
            for(int e = 0; e < srcEvents.getCount(); e++){
                writer.add(srcEvents.getEvent(e));
            }
        }
        writer.close();
    }
    
   
    public static void main(String[] args){
        
        OptionParser parser = new OptionParser();
        parser.addOption("-f", "N/A","file name to process, otherwise -et should be specified");
        parser.addOption("-et", "/tmp/et_local_hipo","et file name to connect to");
        parser.addOption("-host", "localhost", "host running the et ring");
        parser.addOption("-port", "11111", "the port of et ring");
        parser.addOption("-o", "recorded_output.h5", "output file name");
        
        parser.parse(args);
        //String file = "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/output.h5";
        String file = parser.getOption("-f").stringValue();
        if(file.compareTo("N/A")!=0){
            HipoReader r = new HipoReader(file);
            
            DataSourceRecorder rec = new DataSourceRecorder();
            rec.setSource(r);
            rec.addWorker(new DataBankCounter());
            rec.compression(0).open(parser.getOption("-o").stringValue());
            rec.getWriter().setSplitSize(1024*1024*1024*8);
            //rec.getWriter().setSplitSize()
            //rec.getWriter().setCompressionType(1);
            rec.execute();
        } else {        

            String   url = parser.getOption("-et").stringValue();
            String  host = parser.getOption("-host").stringValue();
            int    posrt = parser.getOption("-port").intValue();

            DataSourceEt et = new DataSourceEt(host);
            et.open(url);
            
            DataSourceRecorder rec = new DataSourceRecorder();
            rec.setSource(et);
            
            rec.addWorker(new DataBankCounter());
            rec.compression(0).open(parser.getOption("-o").stringValue());
            rec.getWriter().setSplitSize(1024*1024*1024*8);
            //rec.getWriter().setSplitSize()
            //rec.getWriter().setCompressionType(1);
            rec.execute();
        }
    }
}
