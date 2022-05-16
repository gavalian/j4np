/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.cvt;

import j4np.hipo5.data.Bank;

/**
 *
 * @author gavalian
 */
public class CvtArrayIO {
    
    public static int getSensor(int sector, int layer){
        
        if(layer==1||layer==2)
            return (sector-1)+(layer-1)*10;
        
        if(layer==3||layer==4)
            return 20 + (sector-1)+(layer-3)*14;
        
        return 48 + (sector-1)+(layer-5)*18;
    }
    
    public static CvtArray2D createArray(Bank bst, Bank bmt, int track){        
        CvtArray2D a2d = new CvtArray2D();
        
        int rowsbst = bst.getRows();
        
        for(int i = 0; i < rowsbst; i++){
            int tid = bst.getInt("trkID", i);
            int layer = bst.getInt("layer", i);
            int sector = bst.getInt("sector", i);
            int strip = bst.getInt("strip", i);
            boolean write = true;
            if(track>0&&track!=tid) write = false;
            if(write){
                int y = CvtArrayIO.getSensor(sector, layer);
                a2d.set(strip-1, y, 1.0f);
            }
        }
        
        int rowsbmt = bmt.getRows();
        for(int i = 0; i < rowsbmt; i++){
            int    tid = bmt.getInt("trkID", i);
            int sector = bmt.getInt("sector", i);
            int  layer = bmt.getInt("layer", i);
            boolean write = true;
            if(track>0&&track!=tid) write = false;
            if(write){
                if(layer==2||layer==3||layer==5){
                    double xc = bmt.getFloat("x1",i);
                    double yc = bmt.getFloat("y1",i);
                    double phi = Math.atan2(yc, xc);
                    double fideg = Math.toDegrees(phi);
                    int coord = (int) (256*((fideg+180)/360));
                    if(layer==2) a2d.set(coord,84,1.0f);
                    if(layer==3) a2d.set(coord,85,1.0f);
                    if(layer==5) a2d.set(coord,86,1.0f);
                }
                
                if(layer==1||layer==4||layer==6){
                    int y = 87;
                    if(layer==4) y = 88;
                    if(layer==6) y = 89;
                    double    zc = bmt.getFloat("x1",i);
                    int    start = (sector-1)*85;
                    double shift = (zc/30.0)*85;
                    if(zc>0&&zc<30) a2d.set(start + (int) shift, y, 1.0f);
                }
            }
        }
        
        return a2d;
    }
}
