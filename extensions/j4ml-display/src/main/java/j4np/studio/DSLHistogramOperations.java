/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.studio;

import j4ml.temp.DataStudio;
import j4np.utils.dsl.DSLCommand;
import j4np.utils.dsl.DSLSystem;
import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;

/**
 *
 * @author gavalian
 */
@DSLSystem (system="hist", info="vector")

public class DSLHistogramOperations {
    @DSLCommand(
            command="list",
            info="read vector fromthe file",
            defaults={"!"},
            descriptions={"directory to list"
            }
    ) 
    public void list(String dir){
        if(dir.compareTo("!")==0||dir.compareTo("/")==0){
            DataStudio.getInstance().getDirectory().getDirectory("histograms").list();
        } else {
            DataStudio.getInstance().getDirectory().getDirectory(dir).list();
        }
    }
    
    @DSLCommand(
            command="plot",
            info="list vectors",
            defaults={"!","!"},
            descriptions={"histogram name","draw options"}
    ) 
    public void plot(String name, String options){
        Object obj = DataStudio.getInstance().getDirectory().getDirectory("histograms").getObject(name);
        if(obj==null){
            System.out.printf("[hist/draw] error : histogram [%s] is not found..", name);
        } else {
            H1F h = (H1F) obj;
            DataStudio.getInstance().getDefaultCanvas().draw((H1F) obj,options);
            DataStudio.getInstance().getDefaultCanvas().repaint();
        }
    }
   
}
