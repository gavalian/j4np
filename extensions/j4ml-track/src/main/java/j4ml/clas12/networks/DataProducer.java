/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.networks;

import j4ml.data.DataEntry;
import j4ml.data.DataList;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class DataProducer {
    int   width = 36;
    int  height = 36;
    
    Random r = new Random();
    
    public DataProducer(){
        
        //r.setSeed();
    }
    
    public DataProducer(int w, int h){
        width = w; height = h;
    }
    
    public BufferedImage random(){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int j = 0 ; j < height; j++){
            for(int i = 0; i < width; i++){
                image.setRGB(i, j, 0);
            }
        }
        
        Graphics2D g2d = (Graphics2D) image.getGraphics();        
        g2d.setColor(Color.white);
        int top = r.nextInt(width-4)+2;
        int bottom = r.nextInt(width-4)+2;
        g2d.drawLine(top, 0, bottom, height);
        return image;
    }
    
    public String imageString(BufferedImage img){
        int w = img.getWidth();
        int h = img.getHeight();
        StringBuilder str = new StringBuilder();
        for(int j = 0 ; j < h; j++){
            for(int i = 0; i < w; i++){
                int rgb = img.getRGB(i, j)&0x00FFFFFF;
                if(rgb==0) str.append("-"); else str.append("X");
            }
            str.append("\n");
        }
        return str.toString();
    }
    
    public void showImage(BufferedImage img){
        String str = imageString(img);
        System.out.println(str);
    }
    
    public double[] data(BufferedImage img){
        int w = img.getWidth();
        int h = img.getHeight();
        double[] buffer = new double[w*h];
        int counter = 0;
        for(int j = 0 ; j < h; j++){
            for(int i = 0; i < w; i++){
                int rgb = img.getRGB(i, j)&0x00FFFFFF;
                if(rgb==0) buffer[counter]=0.5; else buffer[counter] = 1.0;
                counter++;
            }
        }
        return buffer;
    }
    
    public void addNoise(BufferedImage img, double level){
        int w = img.getWidth();
        int h = img.getHeight();
        int count = (int) ((w*h)*level);
        Graphics2D g2d = (Graphics2D) img.getGraphics();        
        g2d.setColor(Color.white);
        for(int i = 0; i < count; i++){
            int x = r.nextInt(w);
            int y = r.nextInt(h);
            g2d.drawLine(x, y, x, y);
        }
    }
    public void print(double[] array, double threshold){
        System.out.println("\n########");
        for(int i = 0; i < array.length;i++){
            if(array[i]<threshold){
                System.out.print("-");
            } else System.out.print("X");
            if((i+1)%(width)==0) System.out.println();
        }
    }
    public int noiseLevel(double[] desired, double[] infered, double threshold){
        int counter = 0;
        for(int i = 0; i < desired.length; i++){
            if(desired[i]<threshold&&infered[i]>threshold) counter++;
        }
        return counter;
    }
    
    public int signalLevel(double[] desired, double[] infered, double threshold){
        int counter = 0;
        for(int i = 0; i < desired.length; i++){
            if(desired[i]>threshold&&infered[i]>threshold) counter++;
        }
        return counter;
    }
    
    public DataList createDataList(int count, double level){
        DataList list = new DataList();
        for(int i = 0; i < count; i++){
            BufferedImage bi = this.random();
            double[] output = this.data(bi);
            this.addNoise(bi, level);
            double[] input = this.data(bi);
            list.add(new DataEntry(input,output));
        }
        return list;
    }
}
