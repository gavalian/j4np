/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics.debug;

import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author gavalian
 */
public class CanvasPlot {
    
    public static int  DC_WIDTH = 20;
    public static int  DC_OFFSET_X = 20;
    public static int  DC_OFFSET_Y = 20;
    public static int DC_LENGTH = 160;
    
    public static double[][] dcEventCombi = new double[][]{
        {   0.7500,  0.7300,  0.6700,  0.4500,  0.6500,  0.8500  },
        {    0.7500,  0.7300,  0.6700,  0.4500,  0.6500,  0.5500  },
        {    0.7500,  0.7300,  0.6700,  0.4500,  0.4000,  0.2200  },
        {    0.7500,  0.7300,  0.6700,  0.4500,  0.4000,  0.5500  },
        {    0.7500,  0.7300,  0.6700,  0.5800,  0.6500,  0.8500  },
        {    0.7500,  0.7300,  0.6700,  0.5800,  0.6500,  0.5500  },
        {    0.7500,  0.7300,  0.6700,  0.5800,  0.4000,  0.2200  },
        {    0.7500,  0.7300,  0.6700,  0.5800,  0.4000,  0.5500  },
        {    0.7500,  0.7300,  0.5500,  0.5800,  0.6500,  0.8500  },
        {    0.7500,  0.7300,  0.5500,  0.5800,  0.6500,  0.5500  },
        {    0.7500,  0.7300,  0.5500,  0.5800,  0.4000,  0.2200  },
        {    0.7500,  0.7300,  0.5500,  0.5800,  0.4000,  0.5500  },
        {    0.4500,  0.2500,  0.6700,  0.4500,  0.6500,  0.8500  },
        {    0.4500,  0.2500,  0.6700,  0.4500,  0.6500,  0.5500  },
        {    0.4500,  0.2500,  0.6700,  0.4500,  0.4000,  0.2200  },
        {    0.4500,  0.2500,  0.6700,  0.4500,  0.4000,  0.5500  },
        {    0.4500,  0.2500,  0.5500,  0.4500,  0.6500,  0.8500  },
        {    0.4500,  0.2500,  0.5500,  0.4500,  0.6500,  0.5500  },
        {    0.4500,  0.2500,  0.5500,  0.4500,  0.4000,  0.2200  },
        {    0.4500,  0.2500,  0.5500,  0.4500,  0.4000,  0.5500  },
        {    0.4500,  0.2500,  0.5500,  0.5800,  0.6500,  0.8500  },
        {    0.4500,  0.2500,  0.5500,  0.5800,  0.6500,  0.5500  },
        {    0.4500,  0.2500,  0.5500,  0.5800,  0.4000,  0.2200  },
        {    0.4500,  0.2500,  0.5500,  0.5800,  0.4000,  0.5500  },
        {    0.1500,  0.2500,  0.5500,  0.5800,  0.4000,  0.5500  }
    };
    public static double[][] dcEventCombi5 = new double[][]{
        {   0.0000,  0.2000,  0.5500,  0.4500,  0.6500,  0.8500},
        {   0.0000,  0.2000,  0.5500,  0.4500,  0.6500,  0.5500},
        {   0.0000,  0.2500,  0.5500,  0.4500,  0.6500,  0.8500},
        {   0.0000,  0.2500,  0.5500,  0.4500,  0.6500,  0.5500},
        {   0.1500,  0.0000,  0.5500,  0.4500,  0.6500,  0.8500},
        {   0.1500,  0.0000,  0.5500,  0.4500,  0.6500,  0.5500},
        {   0.4500,  0.0000,  0.5500,  0.4500,  0.6500,  0.8500},
        {   0.4500,  0.0000,  0.5500,  0.4500,  0.6500,  0.5500},
        {   0.1500,  0.2000,  0.0000,  0.4500,  0.6500,  0.8500},
        {   0.1500,  0.2000,  0.0000,  0.4500,  0.6500,  0.5500},
        {   0.1500,  0.2500,  0.0000,  0.4500,  0.6500,  0.8500},
        {   0.1500,  0.2500,  0.0000,  0.4500,  0.6500,  0.5500},
        {   0.4500,  0.2500,  0.0000,  0.4500,  0.6500,  0.8500},
        {   0.4500,  0.2500,  0.0000,  0.4500,  0.6500,  0.5500},
        {   0.1500,  0.2000,  0.5500,  0.0000,  0.6500,  0.8500},
        {   0.1500,  0.2000,  0.5500,  0.0000,  0.6500,  0.5500},
        {   0.1500,  0.2500,  0.5500,  0.0000,  0.6500,  0.8500},
        {   0.1500,  0.2500,  0.5500,  0.0000,  0.6500,  0.5500},
        {   0.4500,  0.2500,  0.5500,  0.0000,  0.6500,  0.8500},
        {   0.4500,  0.2500,  0.5500,  0.0000,  0.6500,  0.5500},
        {   0.1500,  0.2000,  0.5500,  0.4500,  0.0000,  0.8500},
        {   0.1500,  0.2000,  0.5500,  0.4500,  0.0000,  0.5500},
        {   0.1500,  0.2500,  0.5500,  0.4500,  0.0000,  0.8500},
        {   0.1500,  0.2500,  0.5500,  0.4500,  0.0000,  0.5500},
        {   0.4500,  0.2500,  0.5500,  0.4500,  0.0000,  0.8500},
        {   0.4500,  0.2500,  0.5500,  0.4500,  0.0000,  0.5500},
        {   0.1500,  0.2000,  0.5500,  0.4500,  0.6500,  0.0000},
        {   0.1500,  0.2500,  0.5500,  0.4500,  0.6500,  0.0000},
        {   0.4500,  0.2500,  0.5500,  0.4500,  0.6500,  0.0000}    
    };
    
    public static double[][] dcTracks = new double[][]{
        {0.52530 ,0.56696 ,0.45982 ,0.47449 ,0.29464 ,0.28571 },
        {0.75298, 0.73661, 0.67411, 0.64435, 0.53869, 0.51116},
        {0.33185, 0.32679, 0.34286,0.34375,0.43036,0.42857}
    };
    
    public static double[][] dcTracksPoints = new double[][]{
        {0.45 ,0.25 ,0.55 ,0.85 ,0.85 ,0.55 }
    };
    
    public static double[][] dcTracksSmooth = new double[][]{
        {0.15 ,0.20 ,0.3 ,0.45 ,0.65 ,0.85 },
        {0.75, 0.73, 0.67, 0.58, 0.40, 0.22},
        {0.75 ,0.20 ,0.3 ,0.45 ,0.40 ,0.85 },
        {0.75, 0.20, 0.67, 0.58, 0.40, 0.85},
         {    0.4500,  0.3500,  0.5500,  0.4500,  0.65,  0.5500  }
    };
    
    public static double[][] dcTracksSmooth5 = new double[][]{
        {0.15 ,0.20 ,0.3 ,0.45 ,0.65 ,0.85 },
        {0.75, 0.73, 0.67, 0.58, 0.40, 0.22},
        {0.75 ,0.20 ,0.3 ,0.45 ,0.40 ,0.85 },
        {0.75, 0.20, 0.67, 0.58, 0.40, 0.85}
    };
    public static double[][] dcTracksFale = new double[][]{
        { 0.33185,0.56696 ,0.45982 ,0.47449 ,0.29464 ,0.42857 },
        {0.33185, 0.56696 , 0.34286,0.34375,0.29464,0.42857},
        {0.52530 ,0.56696 ,0.45982 ,0.47449 ,0.29464 ,0.28571 },
        {0.33185, 0.32679, 0.34286,0.34375,0.43036,0.42857}
    };
    
    public static Node2D createTrack(String lsvm){
        return null;
    }
    
    public static List<Node2D> createRandomTracks(int count){
        List<Node2D> tracks = new ArrayList<>();
        Random r = new Random();

        for(int i = 0; i < count; i++){
            PolygoneNode2D box = new PolygoneNode2D(0,0,500,500);
                box.nodeSize = 12;
                box.lineSize = 1;
                /*box.lineColor = new Color(0,150,0);
                box.nodeColor = new Color(0,150,0);*/
                box.lineColor = new Color(240,0,0);
                box.nodeColor = new Color(240,0,0);
                box.fillPolygon = false;
                box.drawLine = true;
            for(int s = 0; s < 6; s++){
               
                double mean = r.nextDouble();
                int layer = s;
                int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*layer;
                int y = (int) (mean*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
                box.addPoint(x, y);
                tracks.add(box);
            }
        }
        return tracks;
    }
    
    
    public static List<PathNode2D> getTracks5(){
        List<PathNode2D> list = new ArrayList<>();
        for(int i = 0 ; i < 29; i++){
            PathNode2D path = new PathNode2D(0,0,500,500);
            for(int k = 0; k < 6; k++){
                double data = CanvasPlot.dcEventCombi5[i][k];
                if(data>0.00001){
                    int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*k;
                    int y = (int) (data*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
                    path.addPoint(x, y);
                } else {
                    if(k==0){
                        data = 0.5*(CanvasPlot.dcEventCombi5[i][k+1] + CanvasPlot.dcEventCombi5[i][k+2]);
                    }
                    if(k==5){
                        data = 0.5*(CanvasPlot.dcEventCombi5[i][k-1] + CanvasPlot.dcEventCombi5[i][k-2]);
                    }
                    
                    if(k!=0&&k!=5){
                        data = 0.5*(CanvasPlot.dcEventCombi5[i][k-1] + CanvasPlot.dcEventCombi5[i][k+1]);
                    }
                    
                    int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*k;
                    int y = (int) (data*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
                    path.addPoint(x, y);
                    path.skip.add(k);
                    path.skipDraw = true;
                    path.lineColor = Color.red;
                }                
                list.add(path);
            }
        }
        return list;
    }
    
    public static Node2D createRandom(int count){
        
        PolygoneNode2D box = new PolygoneNode2D(0,0,500,500);
        box.nodeSize = 12;
        box.lineSize = 3;
        box.fillPolygon = false;
        box.drawLine = false;
        Random r = new Random();

        for(int i = 0; i < count; i++){
            int   layer = r.nextInt(6);
            double mean = r.nextDouble();
            
            int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*layer;
            int y = (int) (mean*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
            box.addPoint(x, y);
        }
        return box;
    }
    
    public static Node2D createTrack(double[] means){
        
        PolygoneNode2D box = new PolygoneNode2D(0,0,500,500);
        box.nodeSize = 12;
        box.lineSize = 3;
        box.fillPolygon = false;
        
        for(int i = 0; i < means.length; i++){
            int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*i;
            int y = (int) (means[i]*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
            box.addPoint(x, y);
        }
        return box;
    }
    
    public static PathNode2D createTrackPath(double[] means){
        
        PathNode2D box = new PathNode2D(0,0,500,500);
        box.nodeSize = 12;
        box.lineSize = 3;
        box.fillPolygon = false;
        
        for(int i = 0; i < means.length; i++){
            int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*i;
            int y = (int) (means[i]*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
            box.addPoint(x, y);
        }
        return box;
    }
    public static PathNode2D createPath(double[] means){
        
        PathNode2D box = new PathNode2D(0,0,500,500);
        box.nodeSize = 12;
        box.lineSize = 3;
        box.fillPolygon = false;
        
        for(int i = 0; i < means.length; i++){
            int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*i;
            int y = (int) (means[i]*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
            box.addPoint(x, y);
        }
        return box;
    }
    
   
    
    public static List<Node2D> createChambers(){
        List<Node2D> dc = new ArrayList<>();
        Color[] colors = new Color[]{new Color(240,240,255),new Color(240,255,240)};
        for(int i = 0; i < 6; i++){
            PolygoneNode2D box = new PolygoneNode2D(0,0,500,500);
            box.addPoints(new double[]{20+i*20,20+(i+1)*20,20+(i+1)*20,
                20+i*20,}, 
                    new double[]{20,20,180,180});
            int index = i%2;
            box.fillColor = colors[index];
            box.fillPolygon = true;
            
            box.nodeSize = 1;
            box.lineSize = 1;
                    
            dc.add(box);
        }
        return dc;
    }
    
    
    public static Node2D example1(){
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        Node2D       tr = CanvasPlot.createTrack(CanvasPlot.dcTracks[0]);
        List<Node2D>  rt = CanvasPlot.createRandomTracks(5);
        for(Node2D n : dc) node.addNode(n);
        for(Node2D n : rt) node.addNode(n);
        node.addNode(tr);
        return node;
    }
    
    public static Node2D example2(){
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        Node2D       tr = CanvasPlot.createTrack(CanvasPlot.dcTracksSmooth[0]);
        Node2D       tr2 = CanvasPlot.createTrack(CanvasPlot.dcTracksSmooth[1]);
        List<Node2D>  rt = CanvasPlot.createRandomTracks(5);
        for(Node2D n : dc) node.addNode(n);
        //for(Node2D n : rt) node.addNode(n);
        node.addNode(tr);
        node.addNode(tr2);
        return node;
    }
    
    public static Node2D example22(int hide){
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        PathNode2D       tr = CanvasPlot.createTrackPath(CanvasPlot.dcTracksSmooth[0]);
        tr.skip.add(hide);
        for(Node2D n : dc) node.addNode(n);
        //for(Node2D n : rt) node.addNode(n);
        node.addNode(tr);

        return node;
    }
    
    public static Node2D example3(int which, Color col){
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        Node2D       tr = CanvasPlot.createTrack(CanvasPlot.dcTracksSmooth[which]);
        PolygoneNode2D pol = (PolygoneNode2D) tr;
        pol.lineColor = col;
        pol.nodeColor = col;
        //Node2D       tr2 = CanvasPlot.createTrack(CanvasPlot.dcTracks[2]);
        //List<Node2D>  rt = CanvasPlot.createRandomTracks(5);
        for(Node2D n : dc) node.addNode(n);
        //for(Node2D n : rt) node.addNode(n);
        node.addNode(tr);
        //node.addNode(tr2);
        return node;
    }
    
    public static Node2D example4(){
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        
        Node2D       tr = CanvasPlot.createTrack(new double[] {0.52530 ,0.56696  });
        
        List<Node2D>  rt = CanvasPlot.createRandomTracks(2);
        PolygoneNode2D box = new PolygoneNode2D(0,0,500,500);
        box.nodeSize = 12;
        box.lineSize = 3;
        box.fillPolygon = false;
        double[] means = new double[]{0.47449 ,0.29464 ,0.28571};
        for(int i = 0; i < means.length; i++){
            int x = CanvasPlot.DC_OFFSET_X + CanvasPlot.DC_WIDTH/2 + CanvasPlot.DC_WIDTH*(i+3);
            int y = (int) (means[i]*CanvasPlot.DC_LENGTH+CanvasPlot.DC_OFFSET_Y);
            box.addPoint(x, y);
        }
        for(Node2D n : dc) node.addNode(n);
        for(Node2D n : rt) node.addNode(n);
        node.addNode(tr);
        node.addNode(box);
        return node;
    }
    
    
    public static Node2D example5(){
        
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        for(Node2D n : dc) node.addNode(n);

        for(int i = 0; i < 25; i++){
            PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcEventCombi[i]);
            tr.lineSize = 1;
            tr.lineColor = Color.red;
            node.addNode(tr);
        }
        PathNode2D       trt = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[1]);
        node.addNode(trt);
        return node;
    }
    
    public static Node2D example6(){
        
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        for(Node2D n : dc) node.addNode(n);
        
        //PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcEventCombi5[1]);
        PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        tr.skip.add(2);
        tr.lineSize = 1;
        tr.lineColor = Color.red;
        tr.nodeColor = Color.red;
        tr.skipDraw = false;
        node.addNode(tr);
        
        PathNode2D       tr2 = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[4]);
        tr2.skip.add(1);
        tr2.lineSize = 1;
        tr2.lineColor = Color.red;
        tr2.nodeColor = Color.red;
        tr2.skipDraw = false;
        node.addNode(tr2);
        //PathNode2D       trt = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        //node.addNode(trt);
        return node;
    }
    
    public static Node2D example7(){
        
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        for(Node2D n : dc) node.addNode(n);
        
        //PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcEventCombi5[1]);
        PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        tr.skip.add(2);
        tr.lineSize = 1;
        tr.lineColor = Color.red;
        tr.nodeColor = Color.red;
        tr.skipDraw = true;
        node.addNode(tr);
        
        PathNode2D       tr2 = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[4]);
        tr2.skip.add(1);
        tr2.lineSize = 1;
        tr2.lineColor = Color.red;
        tr2.nodeColor = Color.red;
        tr2.skipDraw = true;
        node.addNode(tr2);
        //PathNode2D       trt = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        //node.addNode(trt);
        return node;
    }
    
    public static Node2D example8(){
        
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        for(Node2D n : dc) node.addNode(n);
        
        //PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcEventCombi5[1]);
       
        
        PathNode2D       tr2 = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[4]);
        tr2.skip.add(1);
        tr2.lineSize = 1;
        tr2.lineColor = Color.red;
        tr2.nodeColor = Color.red;
        tr2.skipDraw = true;
        node.addNode(tr2);

        PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        //tr.skip.add(2);
        tr.lineSize = 1;
        tr.lineColor = Color.BLACK;
        tr.nodeColor = Color.BLACK;
        tr.skipDraw = true;
        node.addNode(tr);
        //PathNode2D       trt = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        //node.addNode(trt);
        return node;
    }
    
     public static Node2D example9(){
        
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        for(Node2D n : dc) node.addNode(n);
        
        //PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcEventCombi5[1]);
       
        
        PathNode2D       tr2 = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[1]);
        //tr2.skip.add(1);
        tr2.lineSize = 1;
        tr2.lineColor = Color.black;
        tr2.nodeColor = Color.black;
        tr2.skipDraw = true;
        node.addNode(tr2);

        PathNode2D       tr = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        tr.skip.add(2);
        tr.lineSize = 1;
        tr.lineColor = Color.BLACK;
        tr.nodeColor = Color.BLACK;
        tr.skipDraw = true;
        node.addNode(tr);
        //PathNode2D       trt = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        //node.addNode(trt);
        return node;
    }
    public static Node2D example10(){
        
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        List<Node2D> dc = CanvasPlot.createChambers();
        for(Node2D n : dc) node.addNode(n);
        List<PathNode2D> tr = CanvasPlot.getTracks5();
        for(PathNode2D n : tr) node.addNode(n);;
        //PathNode2D       trt = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[1]);
        //node.addNode(trt);
        PathNode2D       trt = CanvasPlot.createPath(CanvasPlot.dcTracksSmooth[0]);
        node.addNode(trt);
        return node;
    }
    
    public static void main(String[] args){
    
        JFrame frame = new JFrame();
        Canvas2D canvas = Canvas2D.createFrame(frame, 600, 300);        
        /*
        Node2D  node = new Node2D(0,0,160,200);
        node.setBackgroundColor(255, 255, 255);
        
        System.out.println(node.getTranslation());
        
       
        List<Node2D> dc = CanvasPlot.createChambers();
        Node2D       tr = CanvasPlot.createTrack(
                new double[] {0.52530 ,0.56696 ,0.45982 ,0.47449 ,0.29464 ,0.28571 } );
        
        Node2D        rn = CanvasPlot.createRandom(16);
        List<Node2D>  rt = CanvasPlot.createRandomTracks(5);
        for(Node2D n : dc) node.addNode(n);
        for(Node2D n : rt) node.addNode(n);
        node.addNode(tr);
        //node.addNode(rn);
        //node.getChildren().addAll(dc);*/
        
        //Node2D node = CanvasPlot.example1();
        //Node2D node = CanvasPlot.example2();

        //Node2D node = CanvasPlot.example22(5);
        //Node2D node = CanvasPlot.example3(3,new Color(255,0,0));
        Node2D node = CanvasPlot.example5();
        
        //Node2D node = CanvasPlot.example7();
        //Node2D node = CanvasPlot.example4();
        
        
        canvas.addNode(node);
        
        canvas.repaint();
        canvas.save("example22_6.pdf");
    }
    
    
}
