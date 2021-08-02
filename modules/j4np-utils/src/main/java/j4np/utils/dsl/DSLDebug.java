/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

import java.util.Arrays;

/**
 *
 * @author gavalian
 */
public class DSLDebug {
    
    public static void main(String[] args){
        
        DSLModuleManager manager = new DSLModuleManager();
        manager.initModule("j4np.utils.dsl.DummyModule");
        
        manager.execute("math/add 1 2");
        manager.execute("math/add 5");
        manager.execute("math/mult 24 12");
        
        System.out.println(">>> executing file");
        manager.execute("exec math.kumac 45 67");
        
        manager.execute("help");
        manager.execute("commands");
        
        //manager.executeFile("math.kumac",Arrays.asList("45","65"));
        //manager.showCommands();
        //CliClass clazz = new CliClass();
        //clazz.initWithClass("j4np.utils.dsl.DummyModule");
        //clazz.initWithClass(DummyModule.class);
        //clazz.help();
        //clazz.execute("add", "1 2");
    }
}
