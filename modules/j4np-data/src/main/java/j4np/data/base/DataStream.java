/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author gavalian
 */
public class DataStream {
    /*
    private DataFrame<R,T>       frame = new DataFrame<>();
    private List<Consumer>   consumers = new ArrayList<>();
    private R               dataSource = null;
    private T               dataEvent  = null;
    private int         nDataFrameSize = 2;
    */
    private int     numberOfThreads = 1;
    private int       numberOfCores = 1;
        
    private Consumer       consumer = null;
    private DataSource       source = null;
    private DataFrame         frame = null;
    private int           maxEvents = -1;
    
    public DataStream(){
       
    }
    
    private final void initThreading(){
        numberOfCores = Runtime.getRuntime().availableProcessors();
        numberOfThreads = numberOfCores/2;
        if(numberOfThreads==0) numberOfThreads = 1;
    }
    
    /*
    protected void initFrame(String clazzName){
       
    } */   
    public DataStream consumer(Consumer c){
        consumer = c; return this;
    }
    
    public DataStream threads(int n){ numberOfThreads = n; return this;}
    public DataStream source(DataSource src){ source = src; return this;}
    public DataStream frame(DataFrame fr){ frame = fr; return this;}
    
    public void run(String filename){
        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());        
        System.out.printf("[stream] ( %s ) : system cores %4d, threads initialized %4d\n",
                formatter.format(date),numberOfCores,numberOfThreads);
        source.open(filename);
    }    
    
}
