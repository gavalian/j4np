/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.graphics.TGCanvas;
import twig.widgets.Circle;
import twig.widgets.Line;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class HipoDiagrams {

    public static List<Widget> createTopology(double x, double y, int count, double length, int color){
        List<Widget> list = new ArrayList<>();
        double[] positions = new double[]{45,190,270,155};        
        for(int i = 0 ; i < count; i++){
            Line l = new Line(x,y, 
                    x+length*Math.cos(Math.toRadians(positions[i])),
                    y+length*Math.sin(Math.toRadians(positions[i]))
            );
            
            l.setLineColor(color);
            l.setWidth(2);
            l.setNDF(true);
            list.add(l);
        }
        double size = 0.04;
        for(int i = 0 ; i < count; i++){
            Circle c = new Circle();
            c.dim2d.setRect(
                    x+length*Math.cos(Math.toRadians(positions[i])),
                    y-length*Math.sin(Math.toRadians(positions[i])),size,size
                    );
            c.attrFill.setFillColor(color);
            c.attrLine.setLineColor(color);
            list.add(c);
        }
        
        return list;
    }
    
    public static int[][] createRecord(int size, int[] fractions, int[] colors, boolean sorted){
        int[][] rec = new int[size][size];
        int color = 0;
        int order = 0;

        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                
                if(color>=fractions.length) break;
                if(order>=fractions[color]){
                    color++; order = 0;
                }                                
                //System.out.printf(" order = %d , color = %d\n",order,color);
                if(color<fractions.length){
                    rec[x][y] = colors[color];               
                    order++;
                }
            }
        }
        
        if(sorted==false){
            Random r = new Random();
            for(int i = 0; i < 10000; i++){
                int xd = r.nextInt(size);
                int yd = r.nextInt(size);
                int xs = r.nextInt(size);
                int ys = r.nextInt(size);
                int temp = rec[xd][yd];
                rec[xd][yd] = rec[xs][ys];
                rec[xs][ys] = temp;
            }
        }
        return rec;
    }
    
    public static List<Circle> createRecords(int size, int[][] rec){
        List<Circle> list = new ArrayList<>();
        double step = 1.0/size;
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                Circle c = new Circle();
                c.dim2d.setRect(x*step, y*step, 0.03, 0.03);
                int color = 31;
                if(rec[x][y]>0) color = rec[x][y];
                c.attrFill.setFillColor(color);
                c.attrLine.setLineColor(color);
                list.add(c);
            }
        }
        
        return list;
    }
    public static List<Circle> createRecords(int size, boolean sorted){
        List<Circle> list = new ArrayList<>();
        int[][] rec = new int[size][size];
        Random r = new Random();
        double step = 1.0/size;
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                rec[x][y] = r.nextInt(5);
            }
        }
        
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                Circle c = new Circle();
                c.dim2d.setRect(x*step, y*step, 0.03, 0.03);
                int color = 31;
                if(rec[x][y]>0) color = rec[x][y]+1;
                c.attrFill.setFillColor(color);
                c.attrLine.setLineColor(color);
                list.add(c);
            }
        }
        
        return list;
    }
    
    public static void draw2(){
        TGCanvas c = new TGCanvas(800,800);
        c.view().region().axisLimitsX(0, 1);
        c.view().region().axisLimitsY(0, 1);
        c.view().region().setDebugMode(true);
        
        //List<Circle> record = HipoDiagrams.createRecords(24,true);
        double total = 24*24*5;
        
        int[] fractions = new int[]{ (int) (total*0.026), (int) (total*0.04), 
            (int) (total*(0.00750+0.00017+0.01472)), 
            (int) (total*(0.00426+0.00034))
        }; 
        
        System.out.println(Arrays.toString(fractions));
        //int[][] rec = HipoDiagrams.createRecord(24, new int[]{120,40,20,5}, new int[]{2,3,4,5}, true);
        int[][] rec = HipoDiagrams.createRecord(24, fractions, new int[]{2,3,4,5}, false);
        List<Circle> record = HipoDiagrams.createRecords(24, rec);
        c.region().draw(record);
        c.repaint();
    }
    
    public static void draw1(){
        TGCanvas c = new TGCanvas(500,500);
        c.view().region().axisLimitsX(0, 1);
        c.view().region().axisLimitsY(0, 1);
        c.view().region().setDebugMode(true);
        Line l = new Line(0,0,1,1);
        l.setNDF(true);
        Circle cb = new Circle();
        cb.attrFill.setFillColor(2);
        //c.region().draw(l).draw(cb); 
        for(int i = 0; i < 4; i++){
            List<Widget> topology = HipoDiagrams.createTopology(0.+i*0.3,0.5,i+1, 0.1, 2+i);
            c.region().draw(topology);
        }
    }
    
    public static void main(String[] args){
        HipoDiagrams.draw1();
        HipoDiagrams.draw2();
    }
}
