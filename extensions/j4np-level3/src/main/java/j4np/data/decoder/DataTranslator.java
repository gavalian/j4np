/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.decoder;

import j4np.data.base.DataUtils;
import j4np.utils.io.TextFileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author gavalian
 */
public class DataTranslator {
    
    private Map<Long,Long> table = new HashMap<>();
    
    public DataTranslator(){
        
    }
    
    public static long getHash(int... ids){
        long hash = 0L;
        hash = DataUtils.writeToLong(hash, (short) ids[0], 48);
        hash = DataUtils.writeToLong(hash, (short) ids[1], 32);
        hash = DataUtils.writeToLong(hash, (short) ids[2], 16);
        hash = DataUtils.writeToLong(hash, (short) ids[3], 0);
        return hash;
    }
    
    public static void decodeHash(long hash, int[] ids){
        ids[0] = (int) ((hash>>48)&0xFFFFFFFF); 
        ids[1] = (int) ((hash>>32)&0xFFFFFFFF); 
        ids[2] = (int) ((hash>>16)&0xFFFFFFFF); 
        ids[3] = (int) ((hash)&0xFFFFFFFF); 
    }
    
    public Map<Long,Long>  getMap(){ return this.table;}
    
    public void read(String filename){
        table.clear();
        TextFileReader reader = new TextFileReader();
        reader.open(filename);
        int line = 0;
        while(reader.readNext()==true){
            String[] tokens = reader.getString().split("\\s+");
            if(tokens.length==7){
                int[]  input = new int[4];
                int[] output = new int[4];
                input[0] = Integer.parseInt(tokens[0]);
                input[1] = Integer.parseInt(tokens[1]);
                input[2] = Integer.parseInt(tokens[2]);
                input[3] = 0;
                output[0] = Integer.parseInt(tokens[3]);
                output[1] = Integer.parseInt(tokens[4]);
                output[2] = Integer.parseInt(tokens[5]);
                output[3] = Integer.parseInt(tokens[6]);

                long   key = DataTranslator.getHash(input);
                long value = DataTranslator.getHash(output);
                
                table.put(key, value);
            } else {
                System.out.println("error: the tt file line " + line +
                        " does not have 7 entries as expected");
            }
            line++;
        }
        
        System.out.println("tt: lines loaded = " + line);
    }
    
    public static void main(String[] args){
        long hash = DataTranslator.getHash(45,1,1,1);
        System.out.printf("%016x\n",hash);
        DataTranslator tt = new DataTranslator();
        tt.read("etc/db/dc_tt.txt");
        
    }
}
