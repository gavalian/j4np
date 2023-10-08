/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.graphics.d3;

import java.awt.Color;
import java.awt.geom.Point2D;
import twig.config.TStyle;
import twig.data.H2F;
import twig.data.TDataFactory;

/**
 *
 * @author gavalian
 */
public class MeshMaker {
    
    H2F h2h = null;
    private double h2maximum = 0.0;
    
    private double    scaleX = 1.0;
    private double    scaleY = 1.0;
    private double    scaleZ = 1.0;

    public double rotationX = 0.0;
    public double rotationY = 0.0;
    public double rotationZ = 0.0;
    
    public CoordPair  move = new CoordPair(0.0,0.0,0.0);
    public Color defaultColor = Color.lightGray;
    
    public MeshMaker(){
        
    }
    
    public final void generate(int bins){
         h2h = TDataFactory.createH2F(5000, bins);
         h2h.normalize(h2h.getMaximum());
         h2maximum = h2h.getMaximum();
    }
    
    public final void generateEmpty(int bins){
         h2h = new H2F("h2h",bins,-1.0,1.0,bins,-1.0,1.0);
         h2h.normalize(h2h.getMaximum());
         h2maximum = h2h.getMaximum();
    }
    
    public void setRotation(double x, double y, double z){
        rotationX = Math.toRadians(x);
        rotationY = Math.toRadians(y);
        rotationZ = Math.toRadians(z);
    }
    
    public void setScale(double xs, double ys, double zs){
        scaleX = xs; scaleY = ys; scaleZ = zs;
    }
    
    public static class MeshEdge {
        CoordPair origin; CoordPair end;
        public MeshEdge(double x, double y, double z, double xe, double ye, double ze){
            origin.set(x, y,z);
            end.set(xe, ye,ze);
        }
    }
    
    public String rotationsString(){
        return String.format(" x = %6.2f, y = %6.2f z=%6.2f",
                Math.toDegrees(rotationX),Math.toDegrees(rotationY),
                Math.toDegrees(rotationZ)
                );
    }
    public static class MeshSurface {
        
        public CoordPair[] points = new CoordPair[]{ 
            new CoordPair(), new CoordPair(),
            new CoordPair(), new CoordPair() };
        public Color color = Color.BLACK;
        
        public MeshSurface(){}
        
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            for(int k = 0; k < points.length; k++) str.append(points[k]).append(" ");
            return str.toString();
        }
                
        public double getMidpointZ(){
            return (points[0].zcoord+points[1].zcoord+points[2].zcoord+points[3].zcoord)*0.25;
        }
    }
    
    public int getEdgeCount(){
        return 1;
    }
    
    public int getSurfaceCount(){
        return (h2h.getAxisX().getNBins() - 1)*(h2h.getAxisY().getNBins()-1);
    }
    
    public int getWidth(){ return h2h.getAxisX().getNBins()-1;}
    public int getHeight(){ return h2h.getAxisY().getNBins()-1;}
    
    public void getSurface(MeshSurface srf, int x, int y){
       
        srf.points[0].set(
                h2h.getAxisX().getBinCenter(x)*scaleX, 
                h2h.getAxisY().getBinCenter(y)*scaleY,
                h2h.getBinContent(x, y)*scaleZ
        );
        srf.points[1].set(
                h2h.getAxisX().getBinCenter(x+1)*scaleX, 
                h2h.getAxisY().getBinCenter(y)*scaleY,
                h2h.getBinContent(x+1, y)*scaleZ
        );
        srf.points[2].set(
                h2h.getAxisX().getBinCenter(x+1)*scaleX, 
                h2h.getAxisY().getBinCenter(y+1)*scaleY,
                h2h.getBinContent(x+1, y+1)*scaleZ
        );
        srf.points[3].set(
                h2h.getAxisX().getBinCenter(x)*scaleX, 
                h2h.getAxisY().getBinCenter(y+1)*scaleY,
                h2h.getBinContent(x, y+1)*scaleZ
        );
        for(CoordPair p : srf.points) {
            p.rotateX(rotationX);
            p.rotateY(rotationY);
            p.rotateZ(rotationZ);
            p.move(move);
        }
        srf.color = this.defaultColor;
        //double value = srf.getMidpointZ()/h2maximum;
        /*
        srf.color = TStyle.getInstance().getPalette().palette2d().getColor3D(value, 0, 1.0, false);
        if(srf.color.getRed()==255&&srf.color.getGreen()==255&&srf.color.getBlue()==255)
            srf.color = TStyle.getInstance().getPalette().palette2d().getColor(0);
        */
    }
    
    public void getEdge(MeshEdge edge, int index){
        
    }
    
    public void show(){
        int xsize = this.getWidth();
        int ysize = this.getHeight();
         MeshSurface surf = new MeshSurface();
         
         for(int x = 0; x < xsize; x++){
             for(int y = 0; y < ysize ; y++){
                 this.getSurface(surf, x, y);
                 System.out.printf("%5d , %5d : %s\n",x,y,surf.toString());
             }
         }
    }
    
    public Node getH2D(){
        int bins = 25;
        
        H2F h = TDataFactory.createH2F(3500, bins);
        Node node = new Node(0.0,0.0);
        for(int x = 0; x < bins; x++){
            for(int y = 0; y < bins; y++){
                
            }
        }
        return node;
    }
}
