/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.utils;

import j4np.utils.io.TextFileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class ExternalProcess  {
    
    protected String sysCommandTemplate = "";
    protected int sleepTimer = 20000;
    
    public ExternalProcess(String command){
        sysCommandTemplate = command;
    }
    
    public void show(List<ProcessConfiguration> list){
        for(int i = 0; i < list.size(); i++){            
            String threadCommand = list.get(i).apply(sysCommandTemplate);            
            System.out.printf("%5d : %s\n",i+1,threadCommand);
        }
    }
    
    public void startProcess(List<ProcessConfiguration> list){
        List<Thread>  threads = new ArrayList<>();
        
        for(int i = 0; i < list.size(); i++){            
            String threadCommand = list.get(i).apply(sysCommandTemplate);            
            ProcessDescriptor process = new ProcessDescriptor(i+1,threadCommand);            
            Thread th = new Thread(process);
            threads.add(th);
        }
        
        for(Thread th : threads){ th.start();}
        boolean keepRun = true;
        while(keepRun==true){
            int count = 0;            
            try {
                Thread.sleep(sleepTimer);
            } catch (InterruptedException ex) {
                Logger.getLogger(ExternalProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            for(int i = 0; i < threads.size(); i++){
                if(threads.get(i).isAlive()==true) count++;
            }
            if(count==0) keepRun = false;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
            //Date date = new Date();
            LocalDateTime now = LocalDateTime.now();  
            System.out.printf(">>>> [%s] threads running #%7d/%7d\n", dtf.format(now), count,threads.size());
            
        }
        System.out.println("\n>>> exiting.... ");
        
    }
    
    public static class ProcessConfiguration {
        
        protected Map<String,String> replaceMap = new HashMap<>();
        
        public ProcessConfiguration(){}
        
        public ProcessConfiguration(String config){
            parse(config);
        }
        
        public final void parse(String inputs){
            String[] tokens = inputs.split(":");
            for(int i = 0; i < tokens.length; i+=2){
                replaceMap.put(tokens[i], tokens[i+1]);
            }
        }
        
        public String apply(String temp){
            Set<String>  keys = replaceMap.keySet();
            String       tres = temp;
            for(String key : keys){
                String value = "${"+key+"}";
                boolean found = true;
                while(found==true){
                    int index = tres.indexOf(value);
                    if(index>=0){
                        tres = tres.substring(0, index) + replaceMap.get(key) + tres.substring(index + value.length(), tres.length());
                    } else {
                        found = false;
                    }
                }
                //tres = tres.replaceAll(value, replaceMap.get(key));
            }
            return tres;
        }
        
        public static List<ProcessConfiguration> load(String file){
            List<ProcessConfiguration> list = new ArrayList<>();
            TextFileReader r = new TextFileReader();
            while(r.readNext()==true){
                ProcessConfiguration config = new ProcessConfiguration(r.getString());
                list.add(config);
            }
            return list;
        }
        
        public static List<ProcessConfiguration> fromDir(String file){
            List<String>  files = FileUtils.getFileListInDir(file);
            List<ProcessConfiguration> list = new ArrayList<>();
            for(int i = 0; i < files.size(); i++){
                String params = "input:"+files.get(i)+":output:"+files.get(i)+"_output";
                list.add(new ProcessConfiguration(params));
            }
            return list;
        }
                 
    }
    
    public static class ProcessDescriptor implements Runnable {
        
        protected String sysCommand = "";
        protected int      threadNumber = 1;
        public ProcessDescriptor(String command){
            sysCommand = command;
        }
        
        public ProcessDescriptor(int nt, String command){
            sysCommand = command;
            threadNumber = nt;
        }
        
        @Override
        public void run() {
            System.out.printf(">>>> [thread # %5d] starting\n",threadNumber);
            System.out.printf(">>>> [thread # %5d] :: %s\n",threadNumber,this.sysCommand);

            try
            {
                BufferedReader is;  // reader for output of process
                String line;
                // Command to create an external process            
                // Running the above command
                Runtime run  = Runtime.getRuntime();
                Process proc = run.exec(sysCommand);
                is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                
                while ((line = is.readLine()) != null){
                    //System.out.println(line);
                }
                
                proc.waitFor();
            }  
            catch (IOException e)
            {
                e.printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(ProcessDescriptor.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.printf(">>>> finished thread # %5d\n",threadNumber);
        }
    }
    
    
    public static void main(String[] args){
        
        
        /*String data = "we want to parse ${in} from ${out}";
        
        int index = data.indexOf("${in}");
        System.out.println("index = " + index);
        String data2 = data.replaceAll("${", " dictionary ");
        
        System.out.printf("after : %s\n",data2);
        */
        //ProcessConfiguration p = new ProcessConfiguration("input:data_0001.bos:output:reco_0001.bos:ffread:clasg11.ffred");
        //String result = p.apply("/group/gemc -i ${input} -o ${output} -ffread ${ffread}");
        //System.out.println(">>>> " + result);
        
        ExternalProcess proc = new ExternalProcess("/group/gemc -i ${input} -o ${output} -ffread ${ffread}");
        
        List<ProcessConfiguration>  config = new ArrayList<>();
        
        config.add(new ProcessConfiguration("input:data_0001.bos:output:reco_0001.bos:ffread:clasg11.ffred"));
        config.add(new ProcessConfiguration("input:data_0002.bos:output:reco_0002.bos:ffread:clasg11.ffred"));
        config.add(new ProcessConfiguration("input:data_0003.bos:output:reco_0003.bos:ffread:clasg11.ffred"));
        config.add(new ProcessConfiguration("input:data_0004.bos:output:reco_0004.bos:ffread:clasg11.ffred"));
        config.add(new ProcessConfiguration("input:data_0005.bos:output:reco_0005.bos:ffread:clasg11.ffred"));        
        
        proc.show(config);
        proc.startProcess(config);
    }
}
