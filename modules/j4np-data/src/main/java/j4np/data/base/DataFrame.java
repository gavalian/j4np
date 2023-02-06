/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author gavalian
 */
public class DataFrame<T extends DataEvent> {
    
    private int     numberOfThreads = 1;
    private int       numberOfCores = 1;
    private List<T>       eventList = new ArrayList<>();
    
    
    public DataFrame(){
        initThreading();
    }
    
    private final void initThreading(){
        numberOfCores = Runtime.getRuntime().availableProcessors();
        numberOfThreads = numberOfCores/2;
        if(numberOfThreads==0) numberOfThreads = 1;
    }
    
    public void reset(){this.eventList.clear();}
    public void addEvents(List<T> list){ this.eventList.addAll(list);}
    
    public void addEvent(T event){ eventList.add(event);}
    public int  getCount(){ return eventList.size();}
    
    public DataEvent getEvent(int index){ return eventList.get(index);}
    public Stream<T> getStream(){ return eventList.stream();};
    public Stream<T> getParallelStream(){ return eventList.parallelStream();};
    
    public void show(){
        int entries = eventList.size();
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < entries; i++){
            DataEvent ev = eventList.get(i);
            str.append(String.format("[%3d / %5d], ",i+1, 
                    ev.getBuffer().capacity()));
        }
        System.out.println(str.toString());
    }
    
    /*public void process(){
        
    }*/
}
