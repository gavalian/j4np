/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.cvt;

import j4np.hipo5.data.Bank;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

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
    /*
    public static int getSensor(int sector, int layer){        
        if(layer==1||layer==2)
            return (sector-1)*2+(layer-1);
        
        if(layer==3||layer==4)
            return 20 + (sector-1)*2+(layer-3);
        
        return 48 + (sector-1)*2+(layer-5);
    }*/
    
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
    
    public static int  getRGB(int r, int g, int b){ 
        return (255 << 24) | (r << 16) | (g << 8) | b;//(r << 16) | (g << 8) | b;
    }
    
    public static void saveImage(CvtArray2D t, String file){
        try {
            BufferedImage bi = new BufferedImage(256, 90, BufferedImage.TYPE_INT_ARGB);
            for(int x = 0; x < 256; x++){
                for(int y = 0; y < 90; y++){
                    if(t.get(x, y)>0.5){
                        bi.setRGB(x, y, CvtArrayIO.getRGB(255, 255, 255));
                    } else  bi.setRGB(x, y, CvtArrayIO.getRGB(0, 0, 0));
                }
            }
            File outputfile = new File(file);
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(CvtArrayIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String arrayToLSVM(float[] array, double threshold){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.length; i++){
            if(array[i]>threshold) str.append(String.format(" %d:1", i+1));
        }
        return str.toString();
    }
    public static float[] csvToArray(String csv){
        String[] tokens = csv.trim().split(",");
        float[]  data = new float[tokens.length];
        for(int i = 0 ; i < data.length; i++) data[i] = Float.parseFloat(tokens[i].trim());
        return data;
    }
    public static float[] lsvmToArray(String lsvm, int length){
        float[]   array = new float[length];
        String[] tokens = lsvm.trim().split("\\s+");
        for(int k = 0; k < tokens.length; k++){
            if(tokens[k].contains(":")==true){
                String[] pair = tokens[k].split(":");
                try {
                    int   index = Integer.parseInt(pair[0]);
                    if(index>0&&index<length){
                        array[index-1] = Float.parseFloat(pair[1]);                    
                    }
                } catch (Exception e){
                    System.out.println(">>> [lsvm] error : can't parse string ["+tokens[k]+"]");
                }                
            }
        }
        return array;
    }
}

