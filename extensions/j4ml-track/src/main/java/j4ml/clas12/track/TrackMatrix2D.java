/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.track;

import j4np.hipo5.data.Bank;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TrackMatrix2D {
    
    private   List<String>  idString = null;
    private     float[]       matrix = null;    
    protected   int            xSize = 0;
    protected   int            ySize = 0;
    
    public TrackMatrix2D(int xsize, int ysize, String[] iddata){
        xSize = xsize; ySize = ysize;
        matrix = new float[xsize*ysize];
        idString = Arrays.asList(iddata);
    }
    
    public void reset(){
        for(int i = 0; i < matrix.length; i++) matrix[i] = 0.0f;
    }
    
    private int index(int x, int y){ return xSize*y+x;}
    
    public void set(int x, int y, float value){
       int index = index(x,y);
       if(index>=0&&index<matrix.length) {
           matrix[index] = value;
       } else
           System.out.println("error, x y is oput of bounds\n");
    }
    
    public float get(int x, int y){
       int index = index(x,y);
       if(index>=0&&index<matrix.length) return matrix[index];
       System.out.println("error, x y is oput of bounds\n");
       return 0.0f;
    }
    
    public void fill(Bank b, List<Integer> index){
        int[] ids = new int[idString.size()];
        for(int i = 0; i < index.size(); i++){
            int r = index.get(i);
            for(int k = 0; k < ids.length; k++)
                ids[k] = b.getInt(idString.get(k), r);
            int[] xy = toPoint(ids);
            set(xy[0],xy[1],1.0f);
            //System.out.println(i + Arrays.toString(ids)+"  ==>  " + Arrays.toString(xy));
        }
    }
    
    public int[] toPoint(int[] ids){
        System.out.println("error, not implements\n");
        return new int[]{0,0};
    }
    
    public static BufferedImage getImage(TrackMatrix2D m){
        BufferedImage bi = new BufferedImage(m.xSize, m.ySize, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < m.xSize; x++){
            for(int y = 0; y < m.ySize; y++){
                if(m.get(x, y)>0.5){
                    bi.setRGB(x, y, TrackMatrix2D.getRGB(255, 255, 255));
                } else  bi.setRGB(x, y, TrackMatrix2D.getRGB(0, 0, 0));
            }
        }
        //File outputfile = new File(file);
        //    ImageIO.write(bi, "png", outputfile);
        return bi;
    }
    
    public static int  getRGB(int r, int g, int b){ 
        return (255 << 24) | (r << 16) | (g << 8) | b;//(r << 16) | (g << 8) | b;
    }
    
    public void show(){
        for(int y = 0; y < ySize; y++){
            for(int x = 0; x < xSize; x++){
                //System.out.printf("%d, %d - %.1f\n",x,y,get(x,y));
                if(get(x,y)>0.5){ System.out.print("\u25A0");}
                else {System.out.print("-");}
            } System.out.println(" row " + y);
        }
    }
    
    public int count(){
        int counter = 0;
        for(int i =0; i < this.matrix.length; i++)
            if(matrix[i]>0.5) counter++;
        /*int oc = 0;
        for(int y = 0; y < ySize; y++)
            for(int x = 0 ; x < xSize; x++)
                if(get(x,y)>0.5) oc++;
        System.out.println(" non zero " + counter + "  other way = " + oc);*/
        return counter;
    }
}
