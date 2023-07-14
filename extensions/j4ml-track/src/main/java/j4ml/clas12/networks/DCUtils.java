/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.networks;

/**
 *
 * @author gavalian
 */
public class DCUtils {
    public static double degree2cosine(double theta){
        double csine = 0.2 - (Math.cos(theta)-0.8);
        return csine;
    }
    
    public static double consine2degree(double csine){
        double ct = 1.0-csine;
        return Math.acos(ct);
    }
    
    public static void main(String[] args){
        double angle = 24.5/57.29;
        
        double ct = DCUtils.degree2cosine(angle);
        double theta = DCUtils.consine2degree(ct);
        
        System.out.printf("angle = %f, ct = %f, theta = %f", angle,ct,theta);
    }
}
