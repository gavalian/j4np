/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DSLCommandDescriptor {
    
    String  system = "";
    String command = "";
    String description = "";
    String method = "";
    
    
    List<BasicInputConverter>  converters = new ArrayList<BasicInputConverter>();
    List<Class>                inputTypes = new ArrayList<Class>();
    List<String>               inputDescriptions = new ArrayList<String>();
    List<String>               inputDefaults = new ArrayList<String>();
    
    public DSLCommandDescriptor(String _system, String _command){
        system  = _system;
        command = _command;
    }
    
    public String getSystem(){ return system;}
    public String getCommand(){ return command;}
    public String getDescription(){ return description;}
    public String getMethod(){ return method;}
    public int    getNumberOfArguments(){return inputTypes.size();}
    
    public DSLCommandDescriptor setSystem(String _system){ system = _system; return this;}
    public DSLCommandDescriptor setCommand(String _command){ command = _command; return this;}
    public DSLCommandDescriptor setDescription(String _desc){ description = _desc; return this;}
    public DSLCommandDescriptor setMethod(String _method){ method = _method; return this;}
    
    public DSLCommandDescriptor setInputDescriptions(String[] desc){
        inputDescriptions.clear();
        for(int i = 0; i < desc.length; i++) inputDescriptions.add(desc[i]);
        return this;
    }
    
    public DSLCommandDescriptor setInputDefaults(String[] defs){
        inputDefaults.clear();
        for(int i = 0; i < defs.length; i++) inputDefaults.add(defs[i]);
        return this;
    }
    public int getMaxLength(List<String> list){
        int maxLength = list.get(0).length();
        for(String item : list){
            if(item.length()>maxLength) maxLength = item.length();
        }
        return maxLength;
    }
    
    public String getHelpFormat(int length, int defLength){
        StringBuilder str = new StringBuilder();
        str.append("    %3d : %-").append(length).append("s : (default = %");
        str.append(defLength).append("s)");
        return str.toString();
    }
    public String getHelpString(){
        StringBuilder  str = new StringBuilder();
        int maxLengthDesc = getMaxLength(inputDescriptions);
        int maxLengthDefs = getMaxLength(inputDefaults);
        String     format = getHelpFormat(maxLengthDesc,maxLengthDefs);
        
        str.append("COMMAND: ").append(system).append("/").append(command).append("\n\n");
        str.append("info : \n");
        str.append(description).append("\n\n");
        str.append("arguments : \n\n");
        for(int i = 0; i < inputDescriptions.size(); i++){
            str.append(String.format(format,i+1, 
                    inputDescriptions.get(i),inputDefaults.get(i))).append("\n");
        }
        return str.toString();
    }
    
    public void showHelp(){
        System.out.println(getHelpString());
    }
    
    public void addIntConverter(int _defaultValue){
        this.converters.add(new BasicInputConverter<Integer>(_defaultValue));
        this.inputTypes.add(Integer.TYPE);
    }
    
    public void addDoubleConverter(double _defaultValue){
        this.converters.add(new BasicInputConverter<Double>(_defaultValue));
        this.inputTypes.add(Double.TYPE);
    }
    
    public void addStringConverter(String _defaultValue){
        this.converters.add(new BasicInputConverter<String>(_defaultValue));
        this.inputTypes.add(String.class);
    }
    
    public void addConverter(BasicInputConverter converter){
        this.converters.add(converter);
    }
    
    public Class[]  getMethodInputs(){
        /*
        Class[] args = new Class[this.converters.size()];
        System.out.println(" class names = " + this.command + "  converters = " + this.converters.size());
        for(int i = 0; i < args.length; i++){
            String name = this.converters.get(i).getClass().getGenericSuperclass().getTypeName();
            System.out.println( i + " = " + name);
        }
        return args;*/
        Class[] inputTypesClass = new Class[this.inputTypes.size()];
        for(int i = 0; i < inputTypes.size();i++) inputTypesClass[i] = this.inputTypes.get(i);
        return  inputTypesClass;
    }
    
    public Object[] getInputs(String commandString){
        //System.out.println("[DEBUG] String passed = [" + commandString + "]");
        //String[] tokens = commandString.split("\\s+"); // COMMENTED TO INTRODUCE COMMAS
        String[] tokens = this.getStringArray(commandString);
        Object[] objArray = new Object[converters.size()];
        //System.out.println("[DEBUG] Tokens Length = [" + tokens.length + "]");
//        List<String> arguments = Arrays.asList(tokens);
        //arguments.remove(0);
        List<String> arguments = new ArrayList<String>();
        if(tokens.length==1&&tokens[0].length()<1){
            
        } else {
            for(int i = 0; i < tokens.length; i++) arguments.add(tokens[i]);
        }
        
        while(arguments.size()<this.converters.size()){
            arguments.add("!");
        }
                
        for(int i = 0; i < objArray.length;i++){
            objArray[i] = converters.get(i).valueOf(arguments.get(i));
        }
                
        return objArray;
    }
    
    
    public String getExcutionString(Object[] array){
       StringBuilder str = new StringBuilder();
       for(int i = 0; i < array.length; i++){
           if(i!=0) str.append(" ");
           str.append(array[i]);
       }
       return str.toString();
    }
    
    public void execute(Object clazz, String line){
        
    }
    
    public String help(){
        StringBuilder  str = new StringBuilder();
        String cmd = String.format("%12s/%s", system,command);
        str.append(String.format(" %18s : %s", cmd,description));
        return str.toString();
    }
    
    public String usage(){
        StringBuilder  str = new StringBuilder();
        
        return str.toString();
    }
    
    public String[]     getStringArray(String command){
        List<String> itemsList= getStringList(command);
        String[] tokens = new String[itemsList.size()];
        for(int i = 0; i < tokens.length; i++) tokens[i] = itemsList.get(i);
        return tokens;
    }
    
    public List<String> getStringList(String command){
        List<String> tokens = new ArrayList<String>();
        String trimmed = command.trim()+" ";
        int     length = trimmed.length();
        int   position = 0;
        while(position<length){
            if(trimmed.charAt(position)==' '){
              position++;  
            } else {
                if(trimmed.charAt(position)=='\''){
                    int index = trimmed.indexOf('\'', position+1);
                    //System.out.println("found a string at " + position + " ends at " + index);
                    String item = trimmed.substring(position+1, index);
                    tokens.add(item);
                    position = index+1;
                } else {
                    int index = trimmed.indexOf(' ', position);
                    //System.out.println("found a regular at " + position + " ends at " + index);
                    String item = trimmed.substring(position, index);
                    tokens.add(item);
                    position = index;
                }
            }
        }
        return tokens;
    }
    
    public static void main(String[] args){
        
        
        DSLCommandDescriptor desc = new DSLCommandDescriptor("math","add");
        
        desc.addIntConverter(10);
        desc.addIntConverter(560);
        desc.addStringConverter("!");
        
        //desc.addStringConverter("!");
        
        Object[] array = desc.getInputs("20 40 'group task'");
        System.out.println("LENGTH = " + array.length);
        
        for(int i = 0; i < array.length; i++){
            System.out.println( i + " : " + array[i]);
        }
        
        List<String>  list = desc.getStringList("40 50 60 80 'Momentum trouble'");
        for(String item : list){
            System.out.println("-> " + item);
        }
    }
}
