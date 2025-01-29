/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.geom.prim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class QuadMesh3D implements Transformable, Showable {
    
    List<Quad3D>  faces = new ArrayList<>();
    
    public void getQuad(Quad3D quad, int index){
        
    }
    
    
    public Quad3D getQuad(int index){
        return faces.get(index);
    }
    
    public void addQuad(Quad3D quad){ faces.add(quad);}
    
    public List<Quad3D> getFaces(){ return faces;}
            
    public Color getColor(int index){
        return Color.BLACK;
    }
    
    public int getCount(){
        return faces.size();
    }
    
    public static QuadMesh3D  box(double xsize, double ysize, double zsize){
        QuadMesh3D mesh = new QuadMesh3D();
        Quad3D q1 = Quad3D.rectXY(xsize*2.0, ysize*2.0);
        q1.translateXYZ(0.0,0.0, -zsize);
        Quad3D q2 = Quad3D.rectXY(xsize*2.0, ysize*2.0);
        q2.translateXYZ(0.0,0.0, zsize);
        
        Quad3D q3 = Quad3D.rectXZ(xsize*2.0, zsize*2.0);
        q3.translateXYZ(0.0,-ysize,0.0);        
        Quad3D q4 = Quad3D.rectXZ(xsize*2.0, zsize*2.0);
        q4.translateXYZ(0.0,ysize,0.0);
        
        Quad3D q5 = Quad3D.rectYZ(ysize*2.0, zsize*2.0);
        q5.translateXYZ(-xsize,0.0,0.0);        
        Quad3D q6 = Quad3D.rectYZ(ysize*2.0, zsize*2.0);
        q6.translateXYZ(xsize,0.0,0.0);
        
        mesh.getFaces().addAll(Arrays.asList(q1,q3,q2,q4,q5,q6));
        //mesh.addQuad(Quad3D.rectXY(xsize, ysize*2.0));
        //mesh.getFaces().get(0).translateXYZ(0.0,0.0, -zsize);
        
        return mesh;
    }
    
    @Override
    public void translateXYZ(double dx, double dy, double dz) {
        for(Quad3D q : this.faces) q.translateXYZ(dx, dy, dz);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void rotateX(double angle) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void rotateY(double angle) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void rotateZ(double angle) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void show() {
        System.out.println("MESH : ");
        //for(Point3D p : this.) System.out.println("\t" + p);
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
