/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class FileSystemScan {
    
    String property = "java.io.tmpdir";
    
    public FileSystemScan(){
        
    }
    
    public FileSystemScan(String prop){
        property = prop;
    }
    
    public final void setProperty(String prop){
        property = prop;
    }
    
    public void scan(String... dirs){
    
        for(String dir : dirs){
            boolean status = scanDirectory(dir);
            System.out.printf("() directory scan : %s , status = %s\n",dir,status);
            if(status==true){
                System.setProperty(property, dir);
                System.out.printf(" setting property : %s to %s\n",property,dir);
                break;
            }
        }
    }
    
    public boolean scanDirectory(String dir){
        File f = new File(dir);
        if(f.canWrite()==false) return false;
        String exec = String.format("%s/list-%d.sh", dir,ProcessHandle.current().pid());
        //System.out.println(" openning file : " + exec);
        boolean status = this.writeFile(exec);        
        if(status == false) return false;        
        File fe = new File(exec);       
        fe.setExecutable(true, false);
        boolean canExecute = fe.canExecute();
        fe.delete();
        return canExecute;
    }
    
    public boolean writeFile(String file){
        try {
            FileWriter w = new FileWriter(file);
            w.write("#!/bin/sh\nls -l \n");
            w.close();
        } catch (IOException ex) {
            System.out.println("() error writing file: " + file);
            return false;
        }
        return true;
    }
    public static void main(String[] args){
        FileSystemScan scan = new FileSystemScan();
        scan.scanDirectory("/tmp");
        scan.scan("/Library","/tmp","/Users/gavalian/Work","/Users/gavalian/Work/DataSpace");
    }
}
