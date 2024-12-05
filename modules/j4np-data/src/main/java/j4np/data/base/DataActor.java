/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.data.base;

import j4np.utils.ProgressPrintout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author gavalian
 */
public class DataActor extends Thread {
    
    private DataSource     dataSource = null;
    private DataSync         dataSync = null;
    
    private DataFrame<DataEvent>      dataFrame = null;
    private List<DataWorker>        dataWorkers = new ArrayList<>();
    
    private long[] workerTimes = null;
    private long[] workerStats = null;
    
    private long startTime = 0L;
    private long   endTime = 0L;
    
    private int  benchmark = 0;
    
    public DataActor(){}
    
    public DataActor setSource(DataSource src){ dataSource =  src; return this;}
    public DataActor setSync(DataSync sync){      dataSync = sync; return this;}
    public DataActor setDataFrame(DataFrame frame) { dataFrame = frame; return this;}
    public long      executionTime(){ return (endTime-startTime);}
    public DataActor setBenchmark(int flag){ benchmark = flag; return this;}
    public int       getBenchmark(){return benchmark;}
    
    @Override
    public void run(){
    
        if(benchmark!=0){
            int size = dataWorkers.size();
            workerTimes = new long[size+2];
            workerStats = new long[size+2];
            Arrays.fill(workerStats, 0L);
            Arrays.fill(workerTimes, 0L);
            int nframes = dataFrame.getCount();
            int nReceived = nframes;
            long now, then;
            while(nReceived==nframes){
                then = System.nanoTime();
                nReceived = dataSource.nextFrame(dataFrame);
                now = System.nanoTime();;
                workerTimes[0] += (now-then);
                workerStats[0] += nframes;
                for(int w = 0; w < size; w++){
                    then = System.nanoTime();
                    for(int f = 0; f < nframes; f++){
                        try{
                            dataWorkers.get(w).execute(dataFrame.getEvent(f));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    now = System.nanoTime();
                    workerTimes[w+1] += (now-then);
                    workerStats[w+1] += nframes;
                }
                if(this.dataSync!=null) {
                    then = System.nanoTime();
                    dataSync.addFrame(dataFrame);
                    now = System.nanoTime();
                    workerTimes[size+1] += (now-then);
                    workerStats[size+1] += nframes;
                }
            }
        } else {
        
            startTime = System.currentTimeMillis();
            int size = dataFrame.getCount();
            int nReceived = size;
            while(nReceived==size){
                nReceived = dataSource.nextFrame(dataFrame);
                for(int k = 0; k < nReceived; k++){
                    try{
                        this.accept(dataFrame.getEvent(k));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                if(this.dataSync!=null) dataSync.addFrame(dataFrame);
            }
            endTime = System.currentTimeMillis();
        }
    }
    
    public void setWorkes(List<DataWorker> wrks){
        this.dataWorkers.addAll(wrks);
    }
    
    public  void accept(DataEvent event){
        for(DataWorker w : dataWorkers){
            try {
                w.accept(event);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public String getBenchmarkString(){
        StringBuilder str = new StringBuilder();
        str.append("+").append("-".repeat(107)).append("+\n");
        String bformat = "| %48s | %14d | %14d msec | %12.4f Hz |\n";
        double r1 = ProgressPrintout.nanoTimeToHertz(workerStats[0], workerTimes[0]);
        str.append(String.format(bformat, this.dataSource==null?"N/A":this.dataSource.getClass().getName(),
                    workerStats[0],(long) (workerTimes[0]*1e-6),r1));
        for(int i = 0; i < workerTimes.length-2; i++){
            double rate = ProgressPrintout.nanoTimeToHertz(workerStats[i+1], workerTimes[i+1]);
            str.append(String.format(bformat, dataWorkers.get(i).getClass().getName(),
                    workerStats[i+1],(long) (workerTimes[i+1]*1e-6),rate));
        }
        double r2 = ProgressPrintout.nanoTimeToHertz(workerStats[workerStats.length-1], 
                workerTimes[workerStats.length-1]);
        str.append(String.format(bformat, this.dataSync==null?"N/A":this.dataSync.getClass().getCanonicalName(),
                    workerStats[workerStats.length-1],(long) (workerTimes[workerStats.length-1]*1e-6),r2));
        long nanoTime = 0L; for(int i = 0 ; i < workerTimes.length; i++) nanoTime += workerTimes[i];
        double r3 = ProgressPrintout.nanoTimeToHertz(workerStats[0],nanoTime);
        str.append("+").append("-".repeat(107)).append("+\n");
        str.append(String.format("processint rate %12.4f Hz\n", r3));
        return str.toString();
    }
    
    public void showBenchmark(){
        System.out.println("Data Actor Benchmark\n");
        System.out.println(this.getBenchmarkString());
        System.out.println("---\n");
    }
}
