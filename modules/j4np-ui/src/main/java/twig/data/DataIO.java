/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import j4np.utils.io.TextFileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataIO {
    
    public static List<DataVector> load(String file, int... columns){
        return DataIO.load(file, "\\s+", columns);
    }
    
    public static List<DataVector> load(String file, String delim, int... columns){
        List<DataVector> vectors = new ArrayList<>();
        for(int i = 0; i < columns.length; i++) vectors.add(new DataVector());
        
        TextFileReader r = new TextFileReader(file);
        while(r.readNext()==true){
            String[] tokens = r.getString().split(delim);
            for(int c = 0; c < columns.length; c++)
                vectors.get(c).add(Double.parseDouble(tokens[columns[c]]));
        }
        return vectors;
    }
    
    
    
}
