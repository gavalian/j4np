/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.common;

import j4np.hipo5.data.Bank;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DriftDataUtils {
    
    public static List<DriftMatrix> getInput(Bank dctdc){
        List<DriftMatrix> sectors = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            sectors.add(new DriftMatrix());
        }
        int rows = dctdc.getRows();
        for(int r = 0; r < rows; r++){
            int sector = dctdc.getInt("sector", r);
            int  layer = dctdc.getInt("layer", r);
            int   wire = dctdc.getInt("component", r);
            sectors.get(sector).fill(layer, wire);
        }
        return sectors;
    }
    
    public static List<Integer> applyOutput(DriftMatrix m, Bank dctdc, int sector, double threshold){
        List<Integer> index = new ArrayList<>();
        int rows = dctdc.getRows();
        for(int r = 0; r < rows; r++){
            int sec = dctdc.getInt("sector", r);
            if(sec==sector){
                int layer = dctdc.getInt("layer",r);                
                int wire = dctdc.getInt("component",r);
                int idx = m.getIndex(layer, wire);
                if(m.getValue(idx)>threshold) index.add(r);
            }
        }
        return index;
    }
    
    public static Bank applyOutput(List<DriftMatrix> mat, Bank dctdc, double threshold){
        List<Integer> selection = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            List<Integer> sindex = DriftDataUtils.applyOutput(mat.get(i), dctdc, i+1, threshold);
            selection.addAll(sindex);
        }
        return dctdc.reduce(selection);
    }
    
}
