/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
public class DSLClass {
    
    private Object cliClazz = null;    
    private Map<String,DSLCommandDescriptor> descriptors = new LinkedHashMap<String,DSLCommandDescriptor>();
    private String systemName = "";
    private String systemInfo = "";
    private Boolean initialized = false;
    
    public DSLClass(){
        
    }
    
    public void initWithClass(String className){
        
    }
    
    public void initWithClass(Class clazz){        
        try {
            cliClazz = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DSLClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public String getSystemName(){return systemName;}
    public String getSystemInfo(){return systemInfo;}
    public Boolean isInitialized(){return this.initialized;}
    
    public void printHelp(String command){
        descriptors.get(command).showHelp();
    }
    
    public List<String> getCommandList(){
        List<String> commandList = new ArrayList<String>();
        for(Map.Entry<String,DSLCommandDescriptor> entry : this.descriptors.entrySet()){
            commandList.add(this.getSystemName() + "/" + entry.getValue().getCommand());
        }
        return commandList;
    }
    
    public void showCommands(){
        List<String>  commands = this.getCommandList();
        for(String command : commands){
            String[] tokens = command.split("/");
            
            DSLCommandDescriptor desc = descriptors.get(tokens[1]);
            System.out.printf("%24s : %s\n",command,desc.getDescription());
        }
    }
    
    public int getNumberOfArguments(String command){
        return descriptors.get(command).getNumberOfArguments();
    }
    
    public void scanClass(){
        
        this.initialized = false;
        DSLSystem clazzInfo = (DSLSystem) cliClazz.getClass().getAnnotation(DSLSystem.class);
        if(clazzInfo==null){
            System.out.println(">>>> invalid class : " + cliClazz.getClass().getName());
            return;
        }
        
        systemName = clazzInfo.system();
        systemInfo = clazzInfo.info();
        this.descriptors.clear();
        
        Method[] methods = cliClazz.getClass().getMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(DSLCommand.class)==true){
                //System.out.println("parsin - > " + method.getName());
                Type[] types = method.getParameterTypes();
                Annotation[] annotations = method.getDeclaredAnnotations();
                DSLCommand ann = (DSLCommand) annotations[0];
                String system = systemName;
                String command    = ann.command();
                String info    = ann.info();
                String[] args     = ann.descriptions();
                String[] defaults = ann.defaults();
                DSLCommandDescriptor desc = new DSLCommandDescriptor(systemName,command);
                desc.setMethod(method.getName()).setDescription(info);
                desc.setInputDescriptions(args);
                desc.setInputDefaults(defaults);
                for(int k = 0; k < types.length; k++){
                    //System.out.println(">>> type name = [" + types[k].getTypeName() + "]");
                    if(types[k].getTypeName().compareToIgnoreCase("int")==0){
                        //desc.addConverter(new BasicInputConverter<Integer>("a",args[k],Integer.parseInt(defaults[k])));
                        desc.addIntConverter(Integer.parseInt(defaults[k]));
                    }
                    if(types[k].getTypeName().compareToIgnoreCase("double")==0){
                        //desc.addConverter(new BasicInputConverter<Integer>("a",args[k],Integer.parseInt(defaults[k])));
                        desc.addDoubleConverter(Double.parseDouble(defaults[k]));
                    }
                    if(types[k].getTypeName().compareToIgnoreCase("java.lang.String")==0){
                        //desc.addConverter(new BasicInputConverter<Integer>("a",args[k],Integer.parseInt(defaults[k])));
                        desc.addStringConverter(defaults[k]);
                    }
                }
                this.descriptors.put(desc.getCommand(),desc);
                //System.out.println(" SIZE TYPES = " + types.length + " " + args.length + " " + defaults.length);
                
            }
        }
        if(this.descriptors.size()>0) {
            initialized = true;
        } else {
            System.out.println(" no decalred methods");
        }
    }
    
    
    public void help(){
        for(Map.Entry<String,DSLCommandDescriptor> desc : this.descriptors.entrySet()){
            System.out.println(desc.getValue().help());
        }
    }

    public void execute(String command, String arguments){
        
        DSLCommandDescriptor desc = this.descriptors.get(command);
        
        Object[] array   = desc.getInputs(arguments);
        Class[]  args    = desc.getMethodInputs();
        
        String argumentLine = desc.getExcutionString(array);
        //System.out.println("EXECUTE : " + argumentLine);
        //System.out.println(" OBJECTS = " + array.length);
        //System.out.println(" LENGTH = " + args.length);
        try {
            Method method = this.cliClazz.getClass().getDeclaredMethod(command, args);
            method.invoke(cliClazz, array);
        } catch (NoSuchMethodException | SecurityException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DSLClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
