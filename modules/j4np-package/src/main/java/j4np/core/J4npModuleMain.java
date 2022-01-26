/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.core;

import j4np.utils.dsl.DSLModuleManager;
import j4np.utils.io.OptionExecutor;
import j4np.utils.io.OptionStore;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
/**
 *
 * @author gavalian
 */
public class J4npModuleMain {
    
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
    public static void scan(){

        List<String> clazzList = OptionStore.scanClasses();
        AnsiConsole.systemInstall();
        Ansi a = new Ansi();

        AnsiConsole.out().print(a.eraseScreen());

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
        
        
        
    }
    
    public static void show(){
        
    }
    
    
    public static void execute( String[] args){
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
        }
        
    }
    public static void main(String[] args){
        if(args.length<1){
            J4npModuleMain.scan(); return;
            //J4npModuleMain.test();return;            
        } 
        
        J4npModuleMain.execute(args);
        
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
