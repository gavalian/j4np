/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.core;


import j4np.utils.dsl.DSLModuleManager;
import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionExecutor;
import j4np.utils.io.OptionStore;
import j4np.utils.io.TextFileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import j4np.utils.asciitable.Table;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
/**
 *
 * @author gavalian
 */
public class J4npModuleMain {
    
    
    //private Map<String,OptionApplication>  appMap = new HashMap<>();
    
    
    public static void runPrompt(String[] args){
        J4npModuleMain.printWelcome();
        System.out.println("> starting prompt version of DSL utility..");
    }
    
    public static void runCLI(String[] args){
        J4npModuleMain.printWelcome();
        System.out.println("> starting CLI version of DSL utility..");
        J4npCliMain cli = new J4npCliMain();
        try {
            cli.initialize();
        } catch (IOException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        cli.run();
    }
    
    public static void printWelcome(){
        
        System.out.println("");
        System.out.println("       ██ ██   ██ ███    ██ ██████    Java Libraries for Physics");
        System.out.println("       ██ ██   ██ ████   ██ ██   ██   Jefferson National Lab (2021)");
        System.out.println("       ██ ███████ ██ ██  ██ ██████    Interactive Session by : JLine3");
        System.out.println("  ██   ██      ██ ██  ██ ██ ██        " ); 
        System.out.println("   █████       ██ ██   ████ ██        ");
        System.out.println("");
    }
    
    
    public static void test(){
    
        AnsiConsole.systemInstall();
        Ansi a = new Ansi();
        while(true){
            AnsiConsole.out().print(a.eraseScreen(Ansi.Erase.BACKWARD));
            //AnsiConsole.out().print(a.eraseScreen());
            AnsiConsole.out().print(a.cursorUp(12));
            
            AnsiConsole.out().println("*******************");
            AnsiConsole.out().println(" type = " + AnsiConsole.out().getType());        
            System.out.println(" width = " + AnsiConsole.out().getTerminalWidth());
            System.out.println("  mode = " + AnsiConsole.out().getMode());
            System.out.println("*******************");
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static List<String>  getClassConfig(){
        String dir = System.getenv("J4NPDIR");
        if(dir==null){
            System.out.println("\n\n no configuration is provided");
            return new ArrayList<>();
        }
        
        String file = dir + "/etc/applications.clist";
        List<String> data = TextFileReader.readFile(file);
        return data;
    }
    
    public static Map<String,OptionApplication> scan(){

        List<String> clazzList = OptionStore.scanClasses();
        
        /*
        AnsiConsole.systemInstall();
        Ansi a = new Ansi();

        //AnsiConsole.out().print(a.eraseScreen());

        J4npModuleMain.printWelcome();
        System.out.println("*******************");
        System.out.println(" type = " + AnsiConsole.out().getType());        
        System.out.println(" width = " + AnsiConsole.out().getTerminalWidth());
        System.out.println("  mode = " + AnsiConsole.out().getMode());
        System.out.println("*******************");
        
        for(String clazz : clazzList){
            AnsiConsole.out().println(a.fg(Ansi.Color.BLUE).a(" -->  ").fg(Ansi.Color.RED).a(clazz).a("\n"));
        }
        AnsiConsole.out().println(a.fgDefault());
        */
        Map<String,OptionApplication>  appMap = new HashMap<>();
        
        
        //List<String>  configList = J4npModuleMain.getClassConfig();
        
        
        for(String clazzName : clazzList){
            try {
                Class clazz = Class.forName(clazzName);
                OptionApplication app = (OptionApplication) clazz.newInstance();
                //System.out.println(":: "  );
                //System.out.println("---> " + clazz);
                //System.out.printf("\n%s : %s \n" , app.getAppName(), app.getDescription());              
                appMap.put(app.getAppName(), app);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        List<String>  configList = J4npModuleMain.getClassConfig();
        
        for(String clazzName : configList){
            
            try {
                System.out.println("looking for : " + clazzName);
                Class clazz = Class.forName(clazzName);
                OptionApplication app = (OptionApplication) clazz.newInstance();
                //System.out.println(":: "  );
                //System.out.println("---> " + clazz);
                           
                appMap.put(app.getAppName(), app);
                System.out.println("success");
            } catch (ClassNotFoundException ex) {
                //Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                //Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                //Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        

   
        return appMap;
    }
    
    public static void show(Map<String,OptionApplication> appMap){
        String[] header = new String[]{"module","decsription"};

        String[][] data = new String[appMap.size()][2]; 
        
        //IRender render = new Render();
        //IContextBuilder builder = render.newBuilder();
        //builder.width(62).height(7);
        //Table table = new Table(2, appMap.size()+1);
        int counter = 0;
        
        for(Map.Entry<String,OptionApplication> entry : appMap.entrySet()){
            data[counter][0] = entry.getKey();
            data[counter][1] = entry.getValue().getDescription();
            //System.out.printf(" element %d %d = %s | %s\n",1, counter, entry.getKey(),entry.getValue().getDescription());
            counter++;
        }
        
        String table = Table.getTable(header, data);
        System.out.println(table);
    }
    
    
    public static void execute(Map<String,OptionApplication> map, String app, String[] args){
        
        String[] modified = new String[args.length-1];
        for(int i = 0; i < modified.length; i++) modified[i] = args[i+1];
        map.get(app).execute(modified);
        /*
        String[] p = new String[args.length-1];
        for(int i = 0; i < p.length;i++) p[i] = args[i+1];
        
        String clazzName = args[0];
        
        Class clazz;
        try {
            clazz = Class.forName(clazzName);
            Constructor<?> ctor = clazz.getConstructor();
            OptionExecutor exec = (OptionExecutor) ctor.newInstance();
            exec.execute(p);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(J4npModuleMain.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
    }
    public static void main(String[] args){
        Map<String,OptionApplication> appMap = J4npModuleMain.scan();
        
        if(args.length>0){
            J4npModuleMain.execute(appMap, args[0], args);
            //J4npModuleMain.test();return;      
        } else {
            J4npModuleMain.printWelcome();
            J4npModuleMain.show(appMap);
        }
        
        //J4npModuleMain.execute(args);
        
        /*
        if(args.length<1){
            J4npModuleMain.printWelcome();
            System.out.println();
            System.out.printf("environment : %s\n",System.getenv("J4NPDIR"));
            System.out.println("run the command with flag -cli to start interactive mode");
            return;
        }
        
        if(args[0].startsWith("-cli")==true){
            J4npModuleMain.runCLI(args);
        } else {
            J4npModuleMain.runPrompt(args);
        }*/
    }
}
