/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

/**
 *
 * @author gavalian
 */
public class Vector3 {
    
    double fX;
    double fY;
    double fZ;
    
    public Vector3(Vector3 v){
        this.fX = v.fX;
        this.fY = v.fY;
        this.fZ = v.fZ;
    }
    
    public Vector3()
    {
        fX = 0.0;
        fY = 0.0;
        fZ = 0.0;
    }
    public Vector3(double x, double y, double z){
        fX = x;
        fY = y;
        fZ = z;
    }
    
    public double x() { return fX;}
    public double y() { return fY;}
    public double z() { return fZ;}
    
    public void setXYZ(double x, double y, double z)
    {
        fX = x;
        fY = y;
        fZ = z;  
    }
    
    public static Vector3 from(Vector3 v){
        Vector3 vec = new Vector3(v.x(),v.y(),v.z());
        return vec;
    }
    
    public void rotateX(double angle)
    {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double yy = fY;
        fY = c*yy - s*fZ;
        fZ = s*yy + c*fZ;
    }
    
    public void rotateY(double angle)
    {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double zz = fZ;
        fZ = c*zz - s*fX;
        fX = s*zz + c*fX;
    }
    
    public void rotateZ(double angle)
    {
        //rotate vector around Z
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double xx = fX;
        fX = c*xx - s*fY;
        fY = s*xx + c*fY;
    }
    
    public double angle(Vector3 vec){
        double denom = vec.mag()*mag();
        if(denom<1e-9) return 0.0;
        return Math.acos( dot(vec)/denom);
    }
    
    public void setMagThetaPhi(double mag, double theta , double phi)
    {
        double amag = Math.abs(mag);
        fX = amag * Math.sin(theta) * Math.cos(phi);
        fY = amag * Math.sin(theta) * Math.sin(phi);
        fZ = amag * Math.cos(theta);
    }
    
    public double mag2()
    {
        return (fX*fX+fY*fY+fZ*fZ);
    }
    
    public double mag()
    {
        return Math.sqrt(this.mag2());
    }
    
    public double rho()
    {
        return Math.sqrt(fX*fX + fY*fY);
    }
    
    public double theta()
    {
        return Math.acos(fZ/this.mag());
    }
    
    public double phi()
    {
        return Math.atan2(fY, fX);
    }
    
    public Vector3 add(Vector3 vector)
    {
        fX = fX + vector.x();
        fY = fY + vector.y();
        fZ = fZ + vector.z();
        return this;
    }
    
    public Vector3 add(double x, double y, double z){
        fX = fX + x;
        fY = fY + y;
        fZ = fZ + z;
        return this;
    }
    
    public void negative()
    {
        fX = -fX;
        fY = -fY;
        fZ = -fZ;
    }
    
    public Vector3 sub(Vector3 vector)
    {
        fX = fX - vector.x();
        fY = fY - vector.y();
        fZ = fZ - vector.z();
        return this;
    }
    
    public Vector3 sub(double x, double y, double z){
       fX = fX - x;
       fY = fY - y;
       fZ = fZ - z;
       return this;
    }
    
    public double compare(Vector3 vect)
    {
        double quality = 0.0;
        quality += Math.abs(fX-vect.x())/Math.abs(fX);
        quality += Math.abs(fY-vect.y())/Math.abs(fY);
        quality += Math.abs(fZ-vect.z())/Math.abs(fZ);
        return quality;
    }
    
    public double dot(Vector3 vect)
    {
        return fX*vect.x()+fY*vect.y()+fZ*vect.z();
    }
    
    public Vector3 cross(Vector3 vect)
    {
        Vector3 vprod = new Vector3();
        vprod.setXYZ(fY*vect.z()-fZ*vect.y(), 
                fZ*vect.x()-fX*vect.z(), fX*vect.y()-fY*vect.x());
        return vprod;
    }
    
    public void copy(Vector3 vect)
    {
        fX = vect.x();
        fY = vect.y();
        fZ = vect.z();
    }
    
    public void unit()
    {
        
        if(this.mag()!=0)
        {
            double factor = 1.0/this.mag();
            fX = fX*factor;
            fY = fY*factor;
            fZ = fZ*factor;
        }
    }
    
    public void invert(){
       fX = -fX;
       fY = -fY;
       fZ = -fZ;
    }
    
    public Vector3 scale(double factor){
        fX = fX*factor;
        fY = fY*factor;
        fZ = fZ*factor;
        return this;
    }
    
    public double compareWeighted(Vector3 vect)
    {
        double quality = 0.0;
        double magFactor = mag();
        if(magFactor==0) magFactor = 0.000001;
        quality += Math.abs(fX/magFactor) * Math.abs(fX-vect.x())/Math.abs(fX);
        quality += Math.abs(fY/magFactor) * Math.abs(fY-vect.y())/Math.abs(fY);
        quality += Math.abs(fZ/magFactor) * Math.abs(fZ-vect.z())/Math.abs(fZ);
        return quality;
    }
    
    public String getXYZString()
    {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%e %e %e",fX,fY,fZ));
        return str.toString();
    }
    
    public String getMagThetaPhiString()
    {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%e %e %e",this.mag(),this.theta(),this.phi()));
        return str.toString();
    }
    
    @Override
    public String toString(){
        return this.getXYZString();
    }
}
