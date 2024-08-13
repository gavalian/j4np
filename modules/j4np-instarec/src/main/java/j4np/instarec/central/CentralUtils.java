/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.central;

import j4np.geom.prim.Vector3D;
import j4np.hipo5.data.Bank;
import j4np.utils.io.DataArrayUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CentralUtils {
    
    public static List<float[]> getFeatures(Bank b, int[] index){
        List<float[]> features = new ArrayList<>();
        for(int k = 0; k < index.length; k++){
            if(index[k]>0){
                float[] f = getFeatures(b,index[k]-1);
                features.add(f);
            }
        }
        return features;
    }
    
    public static float[] getFeatures(Bank b, int index){
        int layer = b.getInt("layer", index);
        int status = b.getInt("status", index);
        System.out.println(" status = " + status);
        float[] f = new float[4];
        if(layer>=1&&layer<=6){
            Vector3D v = new Vector3D();
            v.setXYZ(b.getFloat("xo",index),b.getFloat("yo",index),b.getFloat("zo",index));
            f[0] = (float) ((v.rho()-60)/80);
            f[1] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
            v.setXYZ(b.getFloat("xe",index),b.getFloat("ye",index),b.getFloat("ze",index));
            f[2] = (float) ((v.rho()-60)/80);
            f[3] = (float )((v.phi()+Math.PI)/(2.0*Math.PI));
        }
        
        if(layer==7||layer==10||layer==12){
            Vector3D v = new Vector3D();
            v.setXYZ(b.getFloat("xo",index),b.getFloat("yo",index),b.getFloat("zo",index));
            f[0] = (float) ((v.z()+150)/400.0);
            f[1] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
            v.setXYZ(b.getFloat("xe",index),b.getFloat("ye",index),b.getFloat("ze",index));
            f[2] = (float) ((v.z()+150)/400.0);
            f[3] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
        }
        
        if(layer==8||layer==9||layer==11){
            Vector3D v = new Vector3D();
            v.setXYZ(b.getFloat("xo",index),b.getFloat("yo",index),b.getFloat("zo",index));
            f[0] = (float) ((v.rho()-160)/50.0);
            f[1] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
            v.setXYZ(b.getFloat("xe",index),b.getFloat("ye",index),b.getFloat("ze",index));
            f[2] = (float) ((v.rho()-160)/50.0);
            f[3] = (float) ((v.phi()+Math.PI)/(2.0*Math.PI));
        }
        
        return f;
    }
    
    public static float[] parse(String data){
        String[] tokens = data.split(",");
        float[]  f = new float[24];
        for(int i = 0; i < 24; i++) f[i] = Float.parseFloat(tokens[i]);
        return f;
    }
    public static float[] getFeaturesArray(Bank b, int[] index){
        List<float[]>  fl = CentralUtils.getFeatures(b, index);
        String         fs = CentralUtils.array2string(fl);
        float[]  features = CentralUtils.parse(fs);
        return features;
    }
    public static String getFeaturesString(Bank b, int[] index){
        List<float[]>  fl = CentralUtils.getFeatures(b, index);
        String         fs = CentralUtils.array2string(fl);
        return fs;
    }
    public static String array2string(List<float[]> array){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.size(); i++)
            str.append(DataArrayUtils.floatToString(array.get(i), ","));
        return str.toString();
    }
}
