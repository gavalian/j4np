/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package j4np.utils;

/**
 *
 * @author gavalian
 */
public class BenchmarkTimer {
    
    private String timerName = "generic";
    
    private long   lastStartTime = 0;
    private long   totalTime     = 0;
    private long   timeAtResume  = 0;
    private int    numberOfCalls = 0;
    private long   dataPassedThrough = 0L;
    
    private Boolean isPaused = true;
    
    public BenchmarkTimer(){
        
    }
    
    public BenchmarkTimer(String name){
        timerName = name;
    }
    
    public String getName(){
        return timerName;
    }
    
    public void resume(){
        if(isPaused == true){
            timeAtResume = System.nanoTime();
            isPaused = false;
        }
    }
    
    public void addDataSize(long dataSize){
        this.dataPassedThrough += dataSize;
    }
    
    public void pause(){
        if(isPaused==false){
            long timeAtPause = System.nanoTime();
            totalTime += (timeAtPause - timeAtResume);
            numberOfCalls++;
            isPaused = true;
        }
    }
    
    public double getMiliseconds(){
        return totalTime/(1.0e6);
    }
    
    public double getSeconds(){
        return totalTime/(1.0e9);
    }
    
    public String benchmarkStringData(){
        StringBuilder str = new StringBuilder();
        double time = this.getSeconds();
        String timeString = Benchmark.msecString((long) (totalTime/10e6));
        String dataString = Benchmark.bytesString(this.dataPassedThrough);
        long   dataRate   = (long) (this.dataPassedThrough/time);
        String dataRateString = Benchmark.bytesString(dataRate);
        str.append(String.format("TIMER (%-12s) : time = %s , data = %s , processed = %s / sec", 
                getName(),timeString,dataString,dataRateString));
        return str.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        double timePerCall = 0.0;
        if(numberOfCalls!=0) timePerCall = this.getMiliseconds()/numberOfCalls;
        str.append(String.format("TIMER (%-12s) : N Calls %12d, Time  = %12.2f sec,  Unit Time = %12.3f msec",
                this.getName(),numberOfCalls,this.getSeconds(),timePerCall));
        return str.toString();
    }
}
