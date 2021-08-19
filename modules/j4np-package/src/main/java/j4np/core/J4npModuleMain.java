/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.core;

import j4np.utils.dsl.DSLModuleManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static void main(String[] args){
        
        if(args.length<1){
            J4npModuleMain.printWelcome();
            System.out.println();
            System.out.printf("environment : %s\n",System.getenv("J4NPDIR"));
            return;
        }
        
        if(args[0].startsWith("-cli")==true){
            J4npModuleMain.runCLI(args);
        } else {
            J4npModuleMain.runPrompt(args);
        }
    }
}
