/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.structure;

/**
 *
 * @author gavalian
 */
public class DataStructureUtils {
    
    public static void importData(String file){
        
    } 
    
    public static void print(DataStructure struct){
        DataStructureUtils.print(struct, new int[0]);
    }
    
    public static void print(DataStructure struct, int[] mask){
        int    nrows = struct.getRows();
        int nentries = struct.getEntries();
        System.out.printf("structure : rows = %5d, row length = %5d, entries = %5d\n",
                nrows,struct.dataDescriptor.getStructureLength(),nentries);
        for(int e = 0; e < nentries; e++){
            
            boolean showEntry = false;
            

            for(int m = 0; m < mask.length; m++)
                if(e==mask[m]) showEntry = true;
            
            if(mask.length<1) showEntry = true;
                
            if(showEntry==true){
                System.out.printf("%8d : ", e);
                int type = struct.getEntryType(e);
                for(int r = 0; r < nrows; r++){
                    
                    switch(type){
                        case 1: System.out.printf("%8d ",struct.getInt(r, e)); break;
                        case 2: System.out.printf("%8d ",struct.getInt(r, e)); break;
                        case 3: System.out.printf("%8d ",struct.getInt(r, e)); break;
                        case 4: System.out.printf("%8.5f ",struct.getDouble(r, e)); break;
                        case 5: System.out.printf("%8.5f ",struct.getDouble(r, e)); break;
                        default: break;
                    }
                }
                System.out.println();
            }
        }
    }
}
