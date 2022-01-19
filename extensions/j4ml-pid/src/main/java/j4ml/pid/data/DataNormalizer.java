/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.pid.data;

/**
 *
 * @author gavalian
 */
public class DataNormalizer {
    
    double[] mins = null;
    double[] maxs = null;
    
    double[] probeMin = null;
    double[] probeMax = null;
    
    
    public DataNormalizer(double[] __min, double[] __max){
        mins = __min; maxs = __max;
    }
    
    public DataNormalizer(){}
    
    public double[] normalize(double[] a){
        double[] b = new double[a.length];
        for(int i = 0; i < a.length; i++){
            if(a[i]>=mins[i]&&a[i]<=maxs[i]){
                b[i] = (a[i]-mins[i])/(maxs[i]-mins[i]);
            } else {
                b[i] = -10000.0;
            }
        }
        return b;
    }
    
    public float[] normalize(float[] a){
        float[] b = new float[a.length];
        for(int i = 0; i < a.length; i++){
            if(a[i]>=mins[i]&&a[i]<=maxs[i]){
                b[i] = (float) ((a[i]-mins[i])/(maxs[i]-mins[i]));
            } else {
                b[i] = (float) -10000.0;
            }
        }
        return b;
    }
    
    public boolean isValid(double[] b){
        for(int i = 0; i < b.length; i++) if(b[i]<0.0) return false;
        return true;
    }

    public void probe(double[] a){
        if(probeMin==null){
            probeMin = new double[a.length];
            probeMax = new double[a.length];
            for(int i = 0; i < a.length; i++){
                probeMin[i] = a[i];
                probeMax[i] = a[i];
            }
        } else {
            for(int i = 0; i < a.length; i++){
                if(a[i]<probeMin[i]) probeMin[i] = a[i];
                if(a[i]>probeMax[i]) probeMax[i] = a[i];
            }
        }
    }
    
    public String suggestion(){
        StringBuilder str = new StringBuilder();
        str.append("double[] dataMin = new double[] { \n");
        for(int i = 0; i < this.probeMin.length; i++){

            str.append(String.format("%12.5f",probeMin[i]));
            if(i!=probeMin.length-1) str.append(","); 
            if((i+1)%9==0) str.append("\n");
        }
        str.append("\n};\n");
        
        str.append("double[] dataMax = new double[] { \n");
        for(int i = 0; i < this.probeMax.length; i++){
            str.append(String.format("%12.5f",probeMax[i]));
            if(i!=probeMax.length-1) str.append(","); 
            if((i+1)%9==0) str.append("\n");
        }
        str.append("\n};\n");
        return str.toString();
    }
}
