/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.central;

import j4ml.data.DataEntry;
import j4ml.data.DataList;
import j4ml.ejml.EJMLModelRegression;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.utils.io.TextFileWriter;
import java.util.Arrays;

/**
 *
 * @author gavalian
 */
public class CentralRegression {
    
    public static double distance(float[] a, float[] b){
        double dist = 0.0;
        for(int i = 0; i < a.length; i++){
            dist += Math.sqrt( (a[i]-b[i])*(a[i]-b[i]));
        }
        return dist;
    }
    
    public static double distance2(float[] a, float[] b){
        double dist = 0.0;
        for(int i = 1; i < a.length; i++){
            dist += Math.sqrt( (a[i]-b[i])*(a[i]-b[i]));
        }
        return dist;
    }
    
    public static double mag(float[] a){
        double sum2 = 0;
        for(int i = 0; i < a.length; i++){
            sum2 += a[i]*a[i];
        }
        return Math.sqrt(sum2);
    }
    
    public static double dot(float[] a, float[] b){
       double sum = 0.0;
       for(int i = 0; i < a.length; i++){
           sum += a[i]*b[i];
       }
       double am = CentralRegression.mag(a);
       double bm = CentralRegression.mag(b);
       return sum/(am*bm);
    }
    
    public static double d(float[] a, float[] b, int as, int bs){
        double diff = 0.0;
        for(int i =0; i < 4; i++){
            diff += (a[as+i]-b[bs+i])*(a[as+i]-b[bs+i]);
        }
        return Math.sqrt(diff);
    }
    
    public static int similarity(float[] a, float[] b){
        //double[] d = new double[3];
        int count = 0;
        System.out.println(Arrays.toString(a) + " VS " + Arrays.toString(b));
        for(int ia = 0; ia<3; ia++){
            for(int ib = 0; ib<3; ib++){
                double d = d(a,b,ia*3,ib*3);
                System.out.printf(" %d, %d, distance = %f\n",ia,ib,d);
                if(ia!=ib&&d<0.00001) count++;
            }
        }
        return count;
    }
    
    public static void main(String[] args){
        CentralTrainer tr = new CentralTrainer();
        EJMLModelRegression rm = new EJMLModelRegression("regression2.network");
        
        
        HipoReader r = new HipoReader("cvt_output.h5");
        Event event = new Event();
        r.getEvent(event, 250000);
        TextFileWriter w = new TextFileWriter();
        w.open("inference.csv");
        
        for(int j = 0; j < 2000; j++){          
            r.next(event);
            
            DataList input = tr.read(event);
            System.out.println("----- new event");
            for(int i = 0; i < input.getList().size(); i++){
                float[] result = new float[4];
                float[] features = input.getList().get(i).features();
                float[] desired = input.getList().get(i).labels();
                rm.getModel().feedForwardTanhLinear(features,result);
                input.getList().get(i).setInfered(result);
                System.out.println(Arrays.toString(desired) + " => " + Arrays.toString(result));
            }
            System.out.println("======== dirances");
            DataEntry entry = input.getList().get(0);
            for(int i = 1; i < input.getList().size(); i++){
                float[] desired = input.getList().get(i).labels();
                
                int similarity = CentralRegression.similarity(entry.features(), input.getList().get(i).features());
                
                
                double distance = CentralRegression.distance(entry.getInfered(), input.getList().get(i).getInfered());
                double distance2 = CentralRegression.distance2(entry.getInfered(), input.getList().get(i).getInfered());
                double dot  = CentralRegression.dot(entry.getInfered(), input.getList().get(i).getInfered());
                System.out.println(Arrays.toString(desired ) + " ==> "  + Arrays.toString(input.getList().get(i).getInfered()) + " " + distance);
                int label = 0;
                if(desired[0]>0.005) label = 1;
                String value = String.format("%d,%d,%f,%f,%f", label,similarity,distance,distance2,dot);
                System.out.println(value);
                w.writeString(value);
            }
        }
        w.close();
    }
}
