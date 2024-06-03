/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

import j4np.physics.Vector3;
import java.util.Arrays;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class Swimmer {
    
    public  GraphErrors traj(double p, double phi){
    
        final double CHARGE = 1.6e-19; // Charge of the particle in Coulombs
        final double MASS = 9.11e-31; // Mass of the particle in kilograms
        final double MAGNETIC_FIELD = 1.0; // Magnetic field strength in Tesla (uniform along the axis)

        // Inputs
        System.out.print("Enter the velocity of the charged particle (m/s): ");
        double velocity = p*3e8;

        System.out.print("Enter the radius of the cylindrical magnetic field (m): ");
       

        // Calculate the radius of curvature
        double radiusOfCurvature = (MASS * velocity) / (CHARGE * MAGNETIC_FIELD);

        Vector3 v = new Vector3(velocity*Math.cos(phi),velocity*Math.sin(phi),0.0);
        
       
        // Calculate the angle of deflection (theta) in radians
        double radius = 0.0;
        GraphErrors g = new GraphErrors();
        while(radius<1){
        radius += 0.01;
        double theta = radius / radiusOfCurvature;
        
// Calculate the final trajectory (x, y)
        double x = radiusOfCurvature * Math.sin(theta); // Assuming initial position is at (0,0)
        double y = radiusOfCurvature * (1 - Math.cos(theta));

        // Output
        System.out.println("The trajectory of the charged particle is: ");
        System.out.println("x = " + x + " meters");
        System.out.println("y = " + y + " meters");
        g.addPoint(x, y);

        }
        return g;
    }
    
    public static void main(String[] args){
        Swimmer sw = new Swimmer();
        GraphErrors g1 = sw.traj(1,1);
        GraphErrors g2 = sw.traj(2,2);
        GraphErrors g3 = sw.traj(3,3);
        g1.attr().set("mc=2");
        g2.attr().set("mc=3");
        g3.attr().set("mc=4");
        TGCanvas c = new TGCanvas();
        c.region().draw(Arrays.asList(g1,g2,g3), "");
    }
}
