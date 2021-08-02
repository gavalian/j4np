/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

/**
 *
 * @author gavalian
 */
public class DSLClassScanner {
    
    private  String       classPattern = "j4np";
    private  Reflections   reflections = null;
    private  List<String>    classList = new ArrayList<>();
    
    public DSLClassScanner(){
        
    }
    
    public DSLClassScanner(String pattern){
        classPattern = pattern;
    }
    
    public void scan(){
        classList.clear();
        reflections = new Reflections(classPattern);        
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(DSLSystem.class);
        for(Class clazz : annotated){
            classList.add(clazz.getName());
            System.out.printf(" :: %s\n",clazz.getName());
        }
    }
    
    public static void main(String[] args){
       DSLClassScanner scanner = new  DSLClassScanner();
       scanner.scan();
    }
}
