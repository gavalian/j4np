/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.network;

import j4np.hipo5.data.Bank;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.core.InstaRecNetworks;
import j4np.instarec.core.Tracks;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class Debug {
    public static boolean checkLayers(int[] layers){
        for(int i = 0; i < layers.length; i++) if(layers[i]!=(i+1)) return false;
        return true;
    }
    
    public static float[] get6(float[] f){
        float[] t = new float[6];
        for(int i = 0; i < 6; i++){
            t[i] = ((f[2*i]+f[2*i+1])/2.0f)/112.f;
        }
        return t;
    }
    
    public static float[] get12(float[] f){
        float[] t = new float[12];
        for(int i = 0; i < 12; i++) t[i] = (f[i])/112.0f;
        return t;
    }
    
    public static float[] getCorrupt(float[] data, int which){
        float[] corrupt = new float[12];
        for(int i = 0; i < data.length; i++) corrupt[i] = data[i];
        corrupt[2*which] = 0.0f; corrupt[2*which+1] = 0.0f;
        return  corrupt;
    }
    
    public static void normalize(float[] data, float factor){
        for(int k = 0; k < data.length; k++) data[k] = data[k]*factor;
    }
    
    public static void check5(){
        List<float[]> data = Arrays.asList(
                new float[]{
                    90.7619f,91.4048f, 
                    92.0952f,92.7381f,
                    87.1216f ,87.5270f,
                    87.7619f, 88.4048f,
                    82.8000f,84.3000f,
                    84.1429f,85.3571f
                }, new float[]{
                    90.7619f,91.4048f, 
                    92.0952f,92.7381f,
                    87.1216f ,87.5270f,
                    87.7619f, 88.4048f,
                    82.8000f,84.3000f,
                    82.0000f,84.5000f
                }
                );
        
        InstaRecNetworks net = new InstaRecNetworks();
        net.init("etc/networks/clas12default.network", 15);
        net.show();
        
        float[] output = new float[3];
        float[] fixed  = new float[12];
        double[] prob = new double[2];
        
        for(int i = 0; i < data.size(); i++){
            System.out.println("------------------------------\n\n\n");
            float[] input = get6(data.get(i));
            float[] input12 = get12(data.get(i));
            
            net.getClassifier6().feedForwardSoftmax(input, output);            

            System.out.println(Arrays.toString(output)+ " \n");
            System.out.println(Arrays.toString(input12));
            double summ = 0;
            for(int w = 0; w < 6; w++){
                float[] corrupt = getCorrupt(input12,w);
                System.out.printf(" %3d / %3d %s\n", i, w, Arrays.toString(corrupt));
                net.getFixer().feedForwardReLULinear(corrupt, fixed);
                System.out.println(" fixed = " + Arrays.toString(fixed));
                corrupt[w*2] = fixed[w*2]; corrupt[w*2+1] = fixed[w*2+1];
                normalize(corrupt,112.0f);
                float[] fi = get6(corrupt);
                net.getClassifier6().feedForwardSoftmax(fi,output);
                System.out.println(Arrays.toString(output) + " ==> " + Arrays.toString(corrupt));
                System.out.println("\n\n");
                summ += output[2];
            }
            prob[i] = summ/6.0;
        }
        
        System.out.println("\n\n PROB = " + Arrays.toString(prob));
    }
    public static void main(String[] args){
        
        Debug.check5();
        /*
       String file = "rec_clas_005342.evio.00000.hipo";
       
       HipoReader r = new HipoReader(file);
       Bank [] b = r.getBanks("TimeBasedTrkg::TBTracks","HitBasedTrkg::Clusters","RUN::config");
       
       Tracks t = new Tracks(100);
       int[] cid = new int[6];
       
       for(int i = 0; i < 400; i++){
           r.nextEvent(b);
           if(b[0].getRows()>0){
               System.out.println(" Event # " + i);
               b[2].show();
               b[0].show();
               b[1].show();
               DataExtractor.getTracks(t, b[0], b[1]);
               t.show();

               Map<Integer,Integer> map = b[1].getMap("id");
               for(int k = 0; k < t.getRows(); k++){
                   t.getClusters(cid, k);
                   
                   int[] layers = new int[6];
                   
                   for(int c = 0; c < cid.length;c++){
                       if(map.containsKey(cid[c]))
                           layers[c] = b[1].getInt("superlayer", map.get(cid[c]));
                   }
                   System.out.println(Arrays.toString(cid) +  "  " + Arrays.toString(layers));
               }
               
               for(int k = 0; k < b[0].getRows(); k++){
                   int[] array = b[0].getIntArray(6, "Cluster1_ID", k);
                   
                   System.out.println(Arrays.toString(array));
               }

           }
       }*/
    }
}
