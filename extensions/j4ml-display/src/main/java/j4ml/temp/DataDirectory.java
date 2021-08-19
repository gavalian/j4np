/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class DataDirectory<T> {
    
    private String directoryName = "root";
    private List<DataDirectory<T>> dirChildren = new ArrayList<>();
    private Map<String,T>          dirItems = new HashMap<>();
    
    public DataDirectory(){
    }
    
    public DataDirectory(String name){
        directoryName = name;
    }
    
    public final String getName(){
        return directoryName;
    }
    
    public final void setName(String name){
        directoryName = name;
    }
    public void addDirectory(String name){
        dirChildren.add(new DataDirectory<T>(name));
    }
    
    public DataDirectory<T> getDirectory(String name){
        for(int i = 0 ; i < dirChildren.size(); i++){
            if(dirChildren.get(i).getName().compareTo(name)==0){
                return dirChildren.get(i);
            }
        }
        return null;        
    }
    
    public T getObject(String name){
        return dirItems.get(name);
    }
    
    public T putObject(String name, T object){
        return dirItems.put(name, object);
    }
    
    public void list(){
        System.out.printf("directory listing : %s\n",getName());
        
        /*for(Map.Entry<String,T> entry : dirItems.entrySet()){
            System.out.printf("\t%14s : %s\n",entry.getKey(),entry.getValue().getClass().getName());
        }*/
        for(Map.Entry<String,T> entry : dirItems.entrySet()){
            System.out.printf("\t%14s : %s\n",entry.getKey(),entry.getValue().getClass().getName());
        }
    }
}
