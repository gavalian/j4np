/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.utils;

import j4np.utils.io.OptionApplication;
import j4np.utils.io.OptionApplicationStore;
import j4np.utils.io.OptionStore;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class UtilsApplication extends OptionApplication {
    
    public UtilsApplication(){
        super("utils");
        getOptionStore().addCommand("-config", "write configuration file with new values.");
        getOptionStore().getOptionParser("-config").addRequired("-o", "output file name");
        getOptionStore().getOptionParser("-config").addRequired("-r", "raplacement variables");
    }

    @Override
    public String getDescription() {
        return "various utility applications";
    }

    @Override
    public boolean execute(String[] args) {
         OptionStore store = getOptionStore();
        
        store.parse(args);
        
        if(store.getCommand().compareTo("-config")==0){
            String input   = store.getOptionParser("-config").getInputList().get(0);
            String replace = store.getOptionParser("-config").getOption("-r").stringValue();
            String output  = store.getOptionParser("-config").getOption("-o").stringValue();
            
            Map<String,String> data = FileUtils.parseMap(replace);
            FileUtils.writeConfig(input,output, data);
        }
        return true;
    }
    
    public static void main(String[] args){
        Map<String,String> data = FileUtils.parseMap("input=text1.txt,output=k.txt,threads=340");
        FileUtils.writeConfig("template.txt", "template_var.txt", data);
        
        //String data = "command with ${a}";
        //String dr   = data.replace("${a}", "input.txt");
        //System.out.println(dr);
    }
}
