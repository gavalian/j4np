/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.studio;

import j4ml.temp.DataStudio;
import j4np.utils.dsl.DSLCommand;
import j4np.utils.dsl.DSLSystem;
import org.jlab.groot.data.GraphErrors;

/**
 *
 * @author gavalian
 */
@DSLSystem (system="graph", info="graph operations module")

public class DSLGraphOperations {
    @DSLCommand(
            command="create",
            info="create a graph errors object from given string",
            defaults={"10","1:1,2:3,3:5,4:8"},
            descriptions={"id of the graph",
                "string with data (example 0.5:1.2,0.6:2.3)"}
    ) 
    public void create(int id, String pairs){
        GraphErrors gr = new GraphErrors();
        String[] tokens = pairs.split(",");
        for(int i =0; i < tokens.length; i++){
            String[] pair = tokens[i].split(":");
            if(pair.length==2){
                gr.addPoint(Double.parseDouble(pair[0]), 
                        Double.parseDouble(pair[1]), 0.0, 0.0);
            }
            if(pair.length==3){
                gr.addPoint(Double.parseDouble(pair[0]), 
                        Double.parseDouble(pair[1]), 0.0, Double.parseDouble(pair[2]));
            }
        }
        
        String name = String.format("%d", id);
        DataStudio.getInstance().getDirectory().getDirectory("graphs").putObject(name, gr);
        DataStudio.getInstance().getDirectory().getDirectory("graphs").list();

    }
    
     @DSLCommand(
             command="plot",
             info="plots graph with id on default canvas",
             defaults={"10","!"},
             descriptions={"id of the graph", "plotting options (use 'same' for overlaying graphs)"
            }
    ) 
    public void plot(int id, String options){
        String name = String.format("%d", id);
        Object obj = DataStudio.getInstance().getDirectory().getDirectory("graphs").getObject(name);
        if(obj==null){
            System.out.println("(error) object " + name + " was not found");
        } else {
            DataStudio.getInstance().getDefaultCanvas().draw((GraphErrors) obj,options);
            DataStudio.getInstance().getDefaultCanvas().repaint();
        }
    }
    
    @DSLCommand(
            command="flip",
            info="flip the x and y of the graph",
            defaults={"10","11"},
            descriptions={"source graph",
                "flipped new graph"}
    ) 
    public void flip(int ids, int idf){
        String name_s = String.format("%d", ids);
        String name_f = String.format("%d", idf);
        Object obj = DataStudio.getInstance().getDirectory().getDirectory("graphs").getObject(name_s);
        if(obj!=null){
            System.out.println("found object : " + name_s);
            GraphErrors gr_source = (GraphErrors) obj;
            GraphErrors gr_dest   = new GraphErrors();
            for(int p = 0; p < gr_source.getDataSize(0); p++){
                gr_dest.addPoint(
                        gr_source.getDataY(p),
                        gr_source.getDataX(p),
                        gr_source.getDataEY(p),
                        gr_source.getDataEX(p)
                );
            }
            DataStudio.getInstance().getDirectory().getDirectory("graphs").putObject(name_f, gr_dest);
            DataStudio.getInstance().getDirectory().getDirectory("graphs").list();
        } else {
            System.out.println("(error) no object with name " + name_s + " exists in directory");
        }
               

    }
    
    @DSLCommand(
            command="set",
            info="set graph parameters : \n" + ""
                    + "\tlw - line with \n"
                    + "\tlc - line color "
                    + "\tls - line style "
                    + "\tms - marker size "
                    + "\tmc - marker color "
                    + "\tmt - marker type "
            ,            
            defaults={"10","!"},
            descriptions={"graph id",
                "options coma separated (example: lc=2,lw=3,ms=8)"}
    ) 
    public void set(int ids, String options){
        String name_s = String.format("%d", ids);
        Object obj = DataStudio.getInstance().getDirectory().getDirectory("graphs").getObject(name_s);
        if(obj!=null){
            GraphErrors gr = (GraphErrors) obj;
            String[] tokens = options.split(",");
            for(int i = 0; i < tokens.length; i++){
                String[] pair = tokens[i].split("=");
                if(pair[0].compareTo("lw")==0){
                    gr.setLineThickness(Integer.parseInt(pair[1]));
                }
                
                if(pair[0].compareTo("lc")==0){
                    gr.setLineColor(Integer.parseInt(pair[1]));
                }
                
                if(pair[0].compareTo("mc")==0){
                    gr.setMarkerColor(Integer.parseInt(pair[1]));
                }
                
                if(pair[0].compareTo("mt")==0){
                    gr.setMarkerStyle(Integer.parseInt(pair[1]));
                }
                
                if(pair[0].compareTo("ms")==0){
                    gr.setMarkerSize(Integer.parseInt(pair[1]));
                }
            }
        } else {
            System.out.println("(error) no object with name " + name_s + " exists in directory");
        }
    }
}
