/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.track;

import j4np.physics.Vector3;
import twig.data.Range;

/**
 *
 * @author gavalian
 */
public class TrackConstrain {
    
    public Range momentum = new Range(0.3,10.5);
    public Range vertex = new Range(-15,5);
    public Range chiSquare = new Range(0,10);
    
    public Range rTheta = new Range(Math.toRadians(5),Math.toRadians(35));
    public Range   rPhi = new Range(Math.toRadians(40),Math.toRadians(80.0));
    public Range   rMag = new Range(0.5,10.5);
    
    
    
    public boolean isRegression(Vector3 v){
        if(rTheta.contains(v.theta())==false) return false;
        if(rPhi.contains(v.phi())==false) return false;
        return rMag.contains(v.mag());        
    }
    
    public void show(){
        System.out.println("\n\n--\n");
        System.out.printf("%-14s : %12.3f - %12.3f\n","momentum",momentum.min(),momentum.max());
        System.out.printf("%-14s : %12.3f - %12.3f\n","vertex",vertex.min(),vertex.max());
        System.out.printf("%-14s : %12.3f - %12.3f\n","chi2",chiSquare.min(),chiSquare.max());
        System.out.println("--\n\n");
    }
}
