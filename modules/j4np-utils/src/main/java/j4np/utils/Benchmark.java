/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package j4np.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gavalian
 */
public class Benchmark {
    
    private final TreeMap<String,BenchmarkTimer> timerStore = new TreeMap<String,BenchmarkTimer>();
    private static final int KILO_BYTES = 1024;
    private static final int MEGA_BYTES = 1024*1024;
    private static final int GIGA_BYTES = 1024*1024*1024;    
    
    public Benchmark(){
        
    }
    
    public void addTimer(String name){
        if(timerStore.containsKey(name)==true){
            System.err.println("[Benchmark] -----> error. timer with name ("
            + name + ") already exists");
        } else {
            BenchmarkTimer timer = new BenchmarkTimer(name);
            timerStore.put(timer.getName(), timer);
        }
    }
    
    public void pause(String name){
        if(timerStore.containsKey(name)==false){
            System.err.println("[Benchmark] -----> error. no timer defined with name ("
            + name + ")");
        } else {
            timerStore.get(name).pause();
        }
    }
    
    public void resume(String name){
        if(timerStore.containsKey(name)==false){
            System.err.println("[Benchmark] -----> error. no timer defined with name ("
            + name + ")");
        } else {
            timerStore.get(name).resume();
        }
    }
    public BenchmarkTimer  getTimer(String name){
        if(timerStore.containsKey(name)==true){
            return timerStore.get(name);
        }
        return null;
    }
    
    public static String bytesString(long bytes){
        
        double value = 0.0;
        
        if(bytes<Benchmark.KILO_BYTES){
            value = ((double) bytes);
            return String.format("%.2f B", value);
        }
        
        if(bytes<Benchmark.MEGA_BYTES){
            value = ((double) bytes) /1024.0;
            return String.format("%.2f KB", value);
        }
        
        if(bytes<Benchmark.GIGA_BYTES){
            value = ((double) bytes)/1024.0/1024.0;
            return String.format("%.2f MB", value);
        }
        
        value = ((double) bytes)/1024.0/1024.0/1024.0;
        return String.format("%.2f GB", value);        
    }
    /**
     * Returns a string representing the time passed between startTime and
     * endTime, given in milli-seconds. If the startTime is larger than end time
     * they will be reversed.
     * @param startTime milliseconds of time given by System 
     * @param endTime milliseconds of time given by System
     * @return pretty string for printout
     */
    public static String msecString(long startTime, long endTime){
        if(endTime-startTime<0) return Benchmark.msecString(startTime-endTime);
        return Benchmark.msecString(endTime-startTime);
    }
    /**
     * Formats the milliseconds into a pretty string for printing. If the time
     * exceeds 1000 ms, the string is returning seconds, if time exceeds 60 
     * seconds minute:second string is returned.
     * @param ms elapsed time in milli-seconds
     * @return pretty string for printing.
     */
    public static String msecString(long ms){
        if(ms>1000&&ms<60000){
            double time = ms/1000.0;
            return String.format("%.2f sec", time);
        }
        
        if(ms>60000){
            double time = ms/1000.0;
            int minutes = (int) Math.floor(time/60.0);
            int seconds = (int) Math.floor(time - minutes*60);
            return String.format("%d:%d sec", minutes,seconds);
        }        
        return String.format("%d msec", ms);
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        ArrayList<String>  timerStrings = new ArrayList<String>();
        for(Map.Entry<String,BenchmarkTimer> timer : timerStore.entrySet()){
            timerStrings.add(timer.getValue().toString());
            //str.append(timer.getValue().toString());
            //str.append("\n");
        }
        
        if(timerStrings.size()>0){
            int len = timerStrings.get(0).length();
            char[]  asterix = new char[len+8];
            Arrays.fill(asterix,'*');
            String margins = new String(asterix);
            str.append(margins);
            str.append("\n");
            str.append("*     BENCHMARK  RESULTS \n");
            str.append(margins);
            str.append("\n");
            for(String lines : timerStrings){
                str.append("*   ");
                str.append(lines);
                str.append("   *\n");
            }
            str.append(margins);
            str.append("\n");
        }
        
        return str.toString();
    }
}
