/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author gavalian
 */
public class TDataFactory {
    
    public static GraphErrors createGraph(int color, int level){
        GraphErrors graph = new GraphErrors();
        graph.addPoint(1.0, 3.0 + level*2.2 , 0.0, 1.6);
        graph.addPoint(2.0, 2.0 + level*2.2 , 0.0, 1.2);
        graph.addPoint(3.0, 1.5 + level*2.2 , 0.0, 1.);
        graph.addPoint(4.0, 1.2 + level*2.2 , 0.0, 1.2);
        graph.addPoint(5.0, 1.0 + level*2.2 , 0.0, 1.8);
        graph.attr().setMarkerColor(color);
        graph.attr().setMarkerStyle(level);
        graph.attr().setLineColor(color);
        graph.attr().setLineWidth(2);
        graph.attr().setFillColor(120+color);
        return graph;
    }
    
    public static int pointBrightness(int color){
        int red = (color>>16)&0x000000FF;;
        int green =  (color>>8)&0x000000FF;;
        int blue =  (color)&0x000000FF;;
        int br = red+green+blue;
        return br/3;
    }
    
    
    public static GraphErrors fromImage(String filename){
        BufferedImage img = null;
        GraphErrors graph = new GraphErrors();
        //graph.setTitleX("wire");
        //graph.setTitleX("layer");
        
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
        }
        
        int width = img.getWidth();
        int height = img.getHeight();
        
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                int value = img.getRGB(x, y);
             
                if(TDataFactory.pointBrightness(value)>150){
                    graph.addPoint(x+1, y+1, 0.0,0.0);
                }
                //System.out.printf("x = %4d, y = %4d , color = %4d, %4d, %4d, brightness = %5d\n",
                //        x,y, getRed(value),getGreen(value),getBlue(value), brightness(value));
            }
            
        }
        
        graph.attr().setMarkerColor(42);
        graph.attr().setMarkerSize(4);
        graph.attr().setMarkerStyle(1);
        graph.attr().setLineColor(2);
        //graph.attr().setLineThickness(1);
        return graph;
    
    }
    
    public static List<GraphErrors> createGraphColors(int size){
        List<GraphErrors> graphs = new ArrayList<>();
        for(int i = 0; i < size; i++){
            GraphErrors gr = TDataFactory.createGraph(i, i+1);
            gr.attr().setMarkerColor(i+1);
            gr.attr().setMarkerStyle(i+1);
            gr.attr().setLineColor(i+1);
            gr.attr().setMarkerSize(10);            
        }
        return graphs;
    }
    
    public static H1F createH1F(int count, int bins, double min, double max, double mean, double sigma){
        Random r = new Random();
        H1F h = new H1F("h",bins,min,max);
        for(int loop = 0; loop < count; loop++){
            double g = r.nextGaussian()*sigma + mean;
            h.fill(g);
            h.fill(r.nextDouble()*(max-min)+min);
        }
        return h;
    }
    
    public static H1F createH1F(int count){
        return TDataFactory.createH1F(count, 100, 0.0, 1.0, 0.4, 0.2);
    }
    
    
}