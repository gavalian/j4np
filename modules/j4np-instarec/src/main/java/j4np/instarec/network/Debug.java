/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.network;

import j4np.hipo5.data.Bank;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.core.Tracks;
import java.util.Arrays;
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
    
    public static void main(String[] args){
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
       }
    }
}
