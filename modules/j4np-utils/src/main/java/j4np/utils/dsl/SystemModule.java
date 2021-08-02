/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
@DSLSystem (system="sys", info="execute system commands")
public class SystemModule {
    
    @DSLCommand(
            command="date",
            info="display date",
            defaults={"!"},
            descriptions={"display date for time given" }
    )
    public void date(String when){        
        try {
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            // Start a new process: UNIX command ls
            java.lang.Process p = rt.exec("date");
            java.io.InputStream is = p.getInputStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
            // And print each line
            String s = null;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(SystemModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @DSLCommand(
            command="list",
            info="list content of given directory",
            defaults={"1"},
            descriptions={"directory to list" }
    )
    public void list(String dir){        
        try {
            String command = "ls";
            if(dir.compareTo("!")!=0){ command = "ls " + dir; }
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            // Start a new process: UNIX command ls
            java.lang.Process p = rt.exec(command);
            java.io.InputStream is = p.getInputStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
            // And print each line
            String s = null;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(SystemModule.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
