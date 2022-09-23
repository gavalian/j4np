/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.geom.prim;

/**
 *
 * @author gavalian
 */
public class RotationMatrix {
    private final double[][] Rx = new double[3][3];
    private final double[][] Ry = new double[3][3];
    private final double[][] Rz = new double[3][3];
    private final double[][] R  = new double[3][3];
    
    //--- I know in all implementations setting 0.0
    // is not efficienc since their places won't change
    //--- I will fix it later.
    private RotationMatrix setRx(double angle){
        Rx[0][0] = 1.0;
        Rx[1][1] = Math.cos(angle);
        Rx[2][2] = Rx[1][1];
        Rx[2][1] = Math.sin(angle);
        Rx[1][2] = -Rx[2][1];
        Rx[0][1] = 0.0; Rx[0][2] = 0.0;
        Rx[1][0] = 0.0; Rx[2][0] = 0.0;
        return this;
    }
    
    private RotationMatrix setRy(double angle){
        Ry[0][0] = Math.cos(angle);
        Ry[1][1] = 1.0; Ry[2][2] = Ry[0][0];
        Ry[0][2] = Math.sin(angle); Ry[2][0] = -Ry[0][2];
        
        Ry[0][1] = 0.0; Ry[1][0] = 0.0; 
        Ry[1][2] = 0.0; Ry[2][1] = 0.0;
        
        return this;
    }
    private RotationMatrix setRz(double angle){
        Rz[0][0] = Math.cos(angle);
        Rz[1][1] = Rz[0][0]; Rz[2][2] = 1.0;
        Rz[1][0] = Math.sin(angle);
        Rz[0][1] = -Rz[1][0];        
        Rz[0][2] = 0.0; Rz[1][2] = 0.0;
        Rz[2][0] = 0.0; Rz[2][1] = 0.0;
        return this;
    }
    
    public void print( double[][] m){
        System.out.printf("%12.4f %12.4f %12.4f\n",m[0][0],m[0][1],m[0][2]);
        System.out.printf("%12.4f %12.4f %12.4f\n",m[1][0],m[1][1],m[1][2]);
        System.out.printf("%12.4f %12.4f %12.4f\n",m[2][0],m[2][1],m[2][2]);
    }
    
    public void show(){
        System.out.println("********** X MATRIX");
        print(Rx);
        System.out.println("********** Y MATRIX");
        print(Ry);
        System.out.println("********** Z MATRIX");
        print(Rz);
        System.out.println("********** ROTATION MATRIX");
        print(R);
    }
    
    private void mult(double[][] result, double[][] a, double[][] b){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                result[i][j] = 0.0;
                for(int k = 0; k < 3; k++){
                    result[i][j] += a[i][k]*b[k][j];
                }
            }
        }
    }
    public void setR(double[][] rm){
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++){
                R[i][j] = rm[j][i];
            }
    }
    
    public void mult(double[][] result, double[][] a, double[][] b, double[][] c){
        double[][] m = new double[3][3];
        mult(m,b,c);
        mult(result,a,m);
    }
    
    public void setXYZ(double rotX, double rotY, double rotZ){
        setRx(rotX).setRy(rotY).setRz(rotZ);
        double[][] matrix = new double[3][3];        
        //mult(R,Rz,Ry,Rx);
        mult(R,Rx,Ry,Rz);
    }
    
    public void setZYX(double rotZ, double rotY, double rotX){
        setRx(rotX).setRy(rotY).setRz(rotZ);
        mult(R,Rz,Ry,Rx);
    }
    
    public void setZXY(double rotZ, double rotX, double rotY){
        setRx(rotX).setRy(rotY).setRz(rotZ);
        mult(R,Rz,Rx,Ry);
    }
    
    public double[] getRotations(){
        
        double x,y,z;
        //const G4double cosb = std::sqrt(mat.xx()*mat.xx()+mat.yx()*mat.yx());
        double cosb = Math.sqrt(R[0][0]*R[0][0] + R[0][1]*R[0][1]);
        
        if (cosb > 0.0000000001)
        {
            //System.out.println("IF IS DOING");
            //x = atan(mat.zy(),mat.zz());
            x = Math.atan2(R[1][2], R[2][2]);
            //y = std::atan2(-mat.zx(),cosb);
            y = Math.atan2(-R[0][2], cosb);
            //z = std::atan2(mat.yx(),mat.xx());
            z = Math.atan2(R[0][1], R[0][0]);
        }
        else
        {
            //System.out.println("ELSE IS DOING");
            //x = std::atan2(-mat.yz(),mat.yy());
            x = Math.atan2(-R[1][2], R[1][1]);
            //y = std::atan2(-mat.zx(),cosb);
            y = Math.atan2(-R[0][2], cosb);
            z = 0.0;
        }        
        return new double[]{x,y,z};
    }
    
    
    public void showDegrees(double[] angles){
        System.out.printf("\n>>>> X = %12.5f , Y = %12.5f , Z = %12.5f\n",
                Math.toDegrees(angles[0]),
                Math.toDegrees(angles[1]),
                Math.toDegrees(angles[2])
                );
    }
    
    public static void main(String[] args){
        
        RotationMatrix r = new RotationMatrix();
        
        //r.setRz(Math.toRadians(25.0));
        //r.setRx(Math.toRadians(35.0));
        double rotX = Math.toRadians(30.0);
        double rotY = Math.toRadians(60.0);
        double rotZ = Math.toRadians(45.0);
        
        r.setXYZ(rotX,rotY,rotZ);
        r.show();
        
        double[] result = r.getRotations();
        r.showDegrees(result);
        
        System.out.println("--- VECTOR Operations:");
        Vector3D vec = new Vector3D(1.0,0.0,0.0);
        
        vec.rotateZ(rotZ);
        vec.rotateX(rotX);
        vec.rotateY(rotY);
        
        vec.show();
        
        r.setZXY(rotZ, rotX, rotY);
        double[] angles = r.getRotations();
        r.showDegrees(angles);
        
        Vector3D vecM = new Vector3D(1.0,0.0,0.0);
        
        vecM.rotateX(-angles[0]);
        vecM.rotateY(-angles[1]);
        vecM.rotateZ(-angles[2]);
        
        vecM.show();
        
        
        System.out.println("===>>>>> from the paper");
        r.setR(new double[][]{
            {0.5,-0.1464,0.8536},
            {0.5,0.8536, -0.1464},
            {-0.7071,0.5,0.5}});
        r.show();
        double[] solution = r.getRotations();
        
        r.showDegrees(solution);
        
        
        System.out.println("====> NOW THE SAME");
        
        double rX = Math.toRadians(45.0);
        
        r.setRx(rX).setRy(rX).setRz(rX);
        r.show();
        r.showDegrees(r.getRotations());
    }
}
