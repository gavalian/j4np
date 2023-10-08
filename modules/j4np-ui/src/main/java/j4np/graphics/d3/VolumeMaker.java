/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.graphics.d3;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class VolumeMaker {
    List<VolumeLine> lines = new ArrayList<>();
    public double rotationX = 0.0;
    public double rotationY = 0.0;
    public double rotationZ = 0.0;
    
    public VolumeMaker(){
        
    }
    
    public VolumeLine getLine(int index){
        VolumeLine line = new VolumeLine();
        line.points[0].set(lines.get(index).points[0]);
        line.points[1].set(lines.get(index).points[1]);
        for(int k = 0; k < line.points.length; k++){
            line.points[k].rotateX(rotationX);
            line.points[k].rotateY(rotationY);
            line.points[k].rotateZ(rotationZ);
        }
        return line;
    }
    
     public void setRotation(double x, double y, double z){
        rotationX = Math.toRadians(x);
        rotationY = Math.toRadians(y);
        rotationZ = Math.toRadians(z);
    }
     
    public static VolumeMaker  halfBox(){
       VolumeMaker vm = new VolumeMaker();

       vm.lines.add(new VolumeLine(-1,-1,-1, 1,-1,-1));
       vm.lines.add(new VolumeLine(-1,-1,-1,-1, 1,-1));
       vm.lines.add(new VolumeLine(-1,-1,-1,-1,-1, 1));
       return vm;
    }
    
    public int getEdgeCount(){
        return lines.size();
    }
    
    public static class VolumeLine {
        public CoordPair points[] = new CoordPair[]{new CoordPair(),new CoordPair()};
        public VolumeLine(){
            
        }
        public VolumeLine(double x1, double y1, double z1, double x2, double y2, double z2){
            points[0].set(x1, y1, z1);
            points[1].set(x2, y2, z2);
        }
    }
}
