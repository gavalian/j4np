/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class TrackinDiagrams {
    
    
    public static List<GraphErrors> getGraphs(){
        List<GraphErrors> list = new ArrayList<>();
        double[] y  = new double[]{1,2,3,4,5,6};
        double[] x1 = new double[]{6,8,12,14,20,23};
        double[] ym  = new double[]{1,2,4,5,6};
        double[] x2 = new double[]{22,20,11,8,4};
        list.add(new GraphErrors("x1",x1,y));    
        list.add(new GraphErrors("x2",x2,ym));
        return list;
    }
    public static List<GraphErrors> getNoise(int count){
        GraphErrors g = new GraphErrors();
        Random r = new Random();
        
        for(int i = 0; i < count; i++){
            int x = r.nextInt(24) + 1;
            int y = r.nextInt(6) + 1;
            g.addPoint(x, y);
        }
        g.attr().set("mo=1,mc=31,lc=1,ms=8,mw=1");
        return Arrays.asList(g);
    }
    
    public static void drawDenoising(){
        TGCanvas c = new TGCanvas(800,500); 
        
        List<GraphErrors> g80 = TrackinDiagrams.getNoise(80);
        List<GraphErrors> g20 = TrackinDiagrams.getNoise(15);
        List<GraphErrors> gl = TrackinDiagrams.getGraphs();
        List<GraphErrors> gl2 = TrackinDiagrams.getGraphs();

        c.view().divide(3,1);
        
        c.cd(0).draw(g80.get(0),"PL");
        
        for(GraphErrors gr : gl){
            gr.attr().set("mo=1,mc=31,lc=1,lw=2,ms=8,mw=1");
            c.draw(gr,"Psame");
        }
        
        c.cd(1).draw(g20.get(0));
        for(GraphErrors gr : gl){
            gr.attr().set("mo=1,mc=31,lc=1,lw=2,ms=8,mw=1");
            c.draw(gr,"Psame");
        }
        
        c.cd(2).draw(g20.get(0));
        for(GraphErrors gr : gl2){
            gr.attr().set("mo=5,mc=35,lc=5,lw=2,ms=8,mw=1");
            c.draw(gr,"PLsame");
        }
        
        
        
    }
    
    public static void main(String[] args){
        TrackinDiagrams.drawDenoising();
    }
}
