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
    
    public static boolean contains(int n, int[] array){
        for(int i = 0; i < array.length; i++) if(n==array[i]) return true;
        return false;
    }
    
    public static DataVector axisVector(DataVector v){
        DataVector a = new DataVector();
        for(int i = 0; i < v.getSize(); i++){ a.add(i);}
        return a;
    }
    
    public static List<DataVector> readRows(String file, String delim, int... rows){
        List<DataVector> vectors = new ArrayList<>();
        int count = 0;
        int   row = 0;
        TextFileReader r = new TextFileReader(file);
        while(r.readNext()==true&&count<rows.length){
            if(DataIO.contains(row, rows)==true){
                String[] tokens = r.getString().split(delim);
                DataVector v = new DataVector();
                for(int k = 0; k < tokens.length; k++ ){
                    v.add(Double.parseDouble(tokens[k]));
                }
                count++;
                vectors.add(v);
            }
            row++;
        }
        return vectors;
    }
    
    
}
