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
@DSLSystem (system="vector", info="vector")

public class DSLVectorOperations {
    @DSLCommand(
            command="read",
            info="read vector fromthe file",
            defaults={"a","file.txt","0","-1"},
            descriptions={"the name of the vector",
                "file name (use csv extension if your data is comma separated)",
                "default column","maximum rows to read"}
    ) 
    public void read(String names, String file, String columns){
        
        String delim = "\\s+";
        if(file.endsWith(".csv")==true){
            delim = ",";
        }
        
        TextFileReader reader = new TextFileReader();
        reader.open(file);
        
        String[]    vecNames = names.split(":");
        String[] fileColumns = columns.split(":");
        int[]     vecColumns = new int[fileColumns.length];
        List<DataVector> vectors = new ArrayList<>();
        for(int i = 0; i < vecNames.length; i++){
            vecColumns[i] = Integer.parseInt(fileColumns[i]);
            vectors.add(new DataVector());
        }
        int counter = 0;
        while(reader.readNext()==true){
            String[] tokens = reader.getString().split(delim);
            
            for(int j = 0; j < vecColumns.length; j++){
                double value = Double.parseDouble(tokens[vecColumns[j]]);
                vectors.get(j).add(value);
            }
            counter++;
        }
        System.out.printf("[vector:read] processed lines = %d\n",counter);
        //String name = String.format("%d", id);
        //DataStudio.getInstance().getDirectory().getDirectory("graphs").putObject(name, gr);
        for(int j = 0; j < vecNames.length; j++)
            DataStudio.getInstance().getVectorDirectory().put(vecNames[j], vectors.get(j));
    }
    
    @DSLCommand(
            command="list",
            info="list vectors",
            defaults={"!"},
            descriptions={"the name of the vector"}
    ) 
    public void list(String names){
        
        Map<String,DataVector> map = DataStudio.getInstance().getVectorDirectory();
        for(Map.Entry<String,DataVector> entry : map.entrySet()){
            System.out.printf("\t%12s : size = %8d\n",entry.getKey(),entry.getValue().getSize());
        }

    }
    
    @DSLCommand(
            command="fill",
            info="list vectors",
            defaults={"!","100"},
            descriptions={"the name of the vector", "histogram string (100 or 100,120,0.0,0.1)"}
    ) 
    public void fill(String name, String histogram){
        
        if(DataStudio.getInstance().getVectorDirectory().containsKey(name)==true){
            DataVector vec = DataStudio.getInstance().getVectorDirectory().get(name);
            H1F h = null;
            if(histogram.contains(",")==false){
                h = H1F.create(histogram, 100, vec);
            } else {
                String[] tokens = histogram.split(",");
                h = H1F.create(tokens[0], Integer.parseInt(tokens[1]), vec,
                        Double.parseDouble(tokens[2]),Double.parseDouble(tokens[3]));
            }
            DataStudio.getInstance().getDirectory()
                    .getDirectory("histograms").putObject(h.getName(), h);
        } else {
            System.out.printf("[vector/fill] error, can't find vector with name [%s]\n",name);
        }
    }
}
