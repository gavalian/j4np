/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.core;

import j4np.utils.dsl.DSLModuleManager;
import j4np.utils.io.TextFileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jline.reader.EndOfFileException;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 *
 * @author gavalian
 */
public class J4npCliMain {
    
    //private static Map<String,Integer> argMap = JawMainCLI.init();
    DSLModuleManager dslMain = new DSLModuleManager();
    LineReader reader = null;
    public J4npCliMain(){
    
    }
    
    private List<String> readModules(){
        String directory = System.getenv("J4NPDIR");
        if(directory==null) {
            System.out.println("[warning] environment variable J4NPDIR is not set.");
            return new ArrayList<String>();
        }
        
        File file = new File(directory+"/etc/modules.list");
        if(file.exists()==false) {
            
            return new ArrayList<String>();
        }
        return TextFileReader.readFile(directory+"/etc/modules.list");
    }
            
    public StringsCompleter getCompleter(){
       List<String> commands = this.dslMain.getCommandsList();
       String[] args = new String[commands.size()];
       for(int i = 0; i < args.length; i++){
           args[i] = commands.get(i);
       }
       return new StringsCompleter(args);
    }
    
    public void initialize() throws IOException{
        
        List<String> modules = readModules();
        for(String m : modules){
            dslMain.initModule(m);
        }
        
        System.out.println("\n");
        String prompt = "\033[33mj4np\033[0m> ";
        String rightPrompt = null;
        TerminalBuilder builder = TerminalBuilder.builder();
        
        Parser parser = new DefaultParser();
        Terminal terminal = builder.build();
        StringsCompleter completer = this.getCompleter();
        
        reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .parser(parser)
                    .build();
        
        reader.setVariable(LineReader.HISTORY_FILE, dslMain.historyFile());
    }
    
    
    public void run(){
        System.out.println("\n");
        String prompt = "\033[33mj4np\033[0m> ";
        String rightPrompt = null;
        while (true) {
            
            String line = null;
                try {
                    line = reader.readLine(prompt, rightPrompt, null, null);
                } catch (UserInterruptException e) {
                    // Ignore
                } catch (EndOfFileException e) {
                    return;
                }
                if (line == null) {
                    continue;
                }

                line = line.trim();
                
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit") || 
                        line.equalsIgnoreCase("bye")) {
                    
                    break;
                }
                                
                if(line.startsWith("exec")==true){
                    String[] tokens = line.replace("exec","").trim().split("\\s+");                    
                    //JawMainCLI.executeScript(cliMain, tokens);
                }
                
                if(line.equalsIgnoreCase("help")==true){
                    //cliMain.printHelp();
                }
                
                if(line.startsWith("help")==true){
                    String[] tokens = line.split("\\s+");
                    if(tokens.length>1){
                        //cliMain.printSystemHelp(tokens[1]);
                    }
                }
                
                dslMain.execute(line);
                //System.out.println(line);
                //cliMain.printMessageUnrecognizedCommand(line);
        }
        System.out.println("\n Bye-bye...\n");
        System.exit(0); 
    }
    
    public static void main(String[] args) throws IOException {
         
     }
}
