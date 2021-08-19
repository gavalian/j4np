/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

import j4np.utils.io.OptionParser;
import j4np.utils.io.TextFileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class DSLModuleManager {
    
    List<String> completerCommands = new ArrayList<String>();
    Map<String,DSLClass>  commands = new LinkedHashMap<String,DSLClass>();
    List<String>          history  = new ArrayList<String>();
        
    public DSLModuleManager(){
        
    }
    
    public String historyFile(){
        String homeDir = System.getenv("HOME");
        StringBuilder str = new StringBuilder();
        str.append(homeDir).append("/").append(".jnp_history");
        return str.toString();
    }
    
    /*public History getHistory(){
        History h = new History();
        
    }*/
    public void loadHistory(){
        String historyFile = historyFile();
        File f = new File(historyFile);
        if(f.exists()==false){
            System.out.println("[CLI] history file does not exist : " + historyFile);
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(historyFile));
                String line = br.readLine();

                while(line!=null){
                    this.history.add(line);
                    line = br.readLine();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DSLModuleManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DSLModuleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Initializes the module with given class name.
     * @param clazz_name 
     */
    public void initModule(String clazz_name){        
        try {
            Class module = Class.forName(clazz_name);
            DSLClass clazz = new DSLClass();
            clazz.initWithClass(module);
            clazz.scanClass();
            if(clazz.isInitialized()==true){
                System.out.println("[\033[32mINFO\033[0m] ***> init successfull for system : " + clazz.getSystemName());
                commands.put(clazz.getSystemName(),clazz);
                List<String> list = clazz.getCommandList();
                this.completerCommands.addAll(list);
                /*for(String item : list){
                    System.out.println(" ---> " + item);
                }*/
            } else {
                System.out.println("[\033[32mINFO\033[0m] ---> \033[31merror\033[0m initializing class : " + clazz_name);
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DSLModuleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * String completer is used in JLine interface to tell terminal which
     * commands can be auto completed.
     * @return 
     */
/*    public StringsCompleter getCompleter(){
        String[] args = new String[this.completerCommands.size()];
        for(int i = 0; i < args.length; i++){
            args[i] = this.completerCommands.get(i);
        }
        return new StringsCompleter(args);
    }*/
    public List<String> getCommandsList(){ return this.completerCommands;}
    /**
     * returns a String containing only arguments from the command
     * @param commandLine
     * @return 
     */
    private String getArgumentList(String commandLine){
        String[] tokens = commandLine.trim().split("\\s+");
        if(tokens.length>1){
            StringBuilder str = new StringBuilder();
            for(int i = 1; i < tokens.length; i++){
                str.append(tokens[i]).append(" ");
            }
            return str.toString().trim();
        }
        return "";
    }
    
    public void executeFile(String filename, List<String> arguments){
        
        File f = new File(filename);
         if(f.exists()==false){
            System.out.println("[DSL] execute : error >>> file does not exist : " + filename);
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line = br.readLine();

                while(line!=null){
                    
                    String scriptLine = line;
                    for(int i = 0; i < arguments.size(); i++){
                        String replace = String.format("$%d",i+1);
                        scriptLine = scriptLine.replace(replace, arguments.get(i));
                    }                    
                    //execute(line);
                    execute(scriptLine);
                    line = br.readLine();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DSLModuleManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DSLModuleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void executeFile(String filename){                
        
        File f = new File(filename);
         if(f.exists()==false){
            System.out.println("[DSL] execute : error >>> file does not exist : " + filename);
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line = br.readLine();

                while(line!=null){
                    execute(line);
                    line = br.readLine();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DSLModuleManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DSLModuleManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean executeManager(String command){
        
        if(command.trim().compareTo("help")==0){
            System.out.println("\n\nhelp:\n");
            System.out.println("\t commands : print all available commands");
            System.out.println("\t  history : print the history of commands");
            System.out.println("\t     exec : execute a script");
            System.out.println("\n");
            return true;
        }
        if(command.trim().compareTo("commands")==0){
            this.showCommands();
        }
        
        if(command.trim().startsWith("exec")==true){
            String[] params = command.trim().split("\\s+");
            List<String> parameters = new ArrayList<>();
            if(params.length>2){
                for(int k = 2; k < params.length; k++){
                    parameters.add(params[k]);                    
                }
                executeFile(params[1],parameters);
            } else { 
                executeFile(params[1]);
            }
        }
        
        return false;
    }
    /**
     * executes given command line. First separate the system and class
     * and command line.
     * @param commandLine
     * @return 
     */
    public boolean execute(String commandLine){
        

        
        if(commandLine.startsWith("#")||commandLine.startsWith("*")) return false;
        boolean status = executeManager(commandLine);
        if(status==true) return true;
        
        String[] tokens = commandLine.trim().split("\\s+");
        if(tokens.length==1){
            //System.out.println(" HELP - " + tokens[0]);
            String[] pair = tokens[0].split("/");
            if(pair.length>=2){
                DSLClass clazz = this.commands.get(pair[0]);
                if(clazz.getNumberOfArguments(pair[1])!=0){
                    clazz.printHelp(pair[1]);
                    return true;
                }
            }
        }
        
        if(tokens.length>0){
            if(tokens[0].contains("/")==true){
                String[] pair = tokens[0].split("/");
                if(this.commands.containsKey(pair[0])==true){
                    DSLClass clazz = this.commands.get(pair[0]);
                    String    args = getArgumentList(commandLine);
                    clazz.execute(pair[1], args);
                }
            }
        }
        return true;
    }
    
    
    public void help(String system){
        if(this.commands.containsKey(system)==true){
            commands.get(system).help();
        }
    }
    /**
     * if command was not found in the dictionary, prints out the statement.
     * @param command 
     */
    public void printMessageUnrecognizedCommand(String command){
        System.out.println("\033[33m warning\033[0m: unrecognized command \"" + command + "\"");
    }
    
    public void showCommands(){
        for(Map.Entry<String,DSLClass> entry : this.commands.entrySet()){
            entry.getValue().showCommands();
        }
    }
    
    public static void main(String[] args){
        
        OptionParser parser = new OptionParser("j4ml-dsl");
        parser.addRequired("-modules", "the file with list of modules");
        parser.addOption("-list", "!", "list all available functions");
        parser.addOption("-exec", "!", "file name to execute");
        parser.addOption("-c", "!", "execute a semicolon separated command list");
        
        parser.parse(args);
        
        if(parser.getOption("-exec").stringValue().compareTo("!")==0&&
               parser.getOption("-list").stringValue().compareTo("!")!=0){
            String modulesFile = parser.getOption("-modules").stringValue();
            List<String> modules = TextFileReader.readFile(modulesFile);
            DSLModuleManager manager = new DSLModuleManager();
            for(String module : modules){
                manager.initModule(module);
            }
            System.out.println("");
            manager.showCommands();
            System.out.println("");
        }
        
        if(parser.getOption("-exec").stringValue().compareTo("!")!=0&&
               parser.getOption("-list").stringValue().compareTo("!")==0){
            List<String>  inputs = parser.getInputList();
            
            String modulesFile = parser.getOption("-modules").stringValue();
            String  executable = parser.getOption("-exec").stringValue();
            
            List<String> modules = TextFileReader.readFile(modulesFile);
            DSLModuleManager manager = new DSLModuleManager();
            
            for(String module : modules){
                manager.initModule(module);
            }            
            manager.executeFile(executable, inputs);

        }
                
        if(parser.getOption("-c").stringValue().compareTo("!")!=0){
            String command = parser.getOption("-c").stringValue();
            String modulesFile = parser.getOption("-modules").stringValue();
            DSLModuleManager manager = new DSLModuleManager();
            List<String> modules = TextFileReader.readFile(modulesFile);
            for(String module : modules){
                manager.initModule(module);
            }  
            System.out.println(">>> executing command : [" + command + "]");
            manager.execute(command);
        }
    }
}
