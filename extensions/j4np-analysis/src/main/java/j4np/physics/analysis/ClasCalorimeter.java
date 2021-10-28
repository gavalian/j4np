/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics.analysis;

import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.jnp.hipo4.data.Event;
import org.jlab.jnp.hipo4.io.HipoChain;

/**
 *
 * @author gavalian
 */
public class ClasCalorimeter {
    
    BankStore store = new BankStore();
    private double[] minimum = new double[28];
    private double[] maximum = new double[]{
        10,
        1,1,1,450,450,450,650,650,650,
        1,1,1,450,450,450,650,650,650,
        1,1,1,450,450,450,650,650,650
    };
    /*private double[] maximum = new double[]{
        10,
        0.65,0.65,0.65,450,450,450,650,650,650,
        0.75,0.75,0.75,450,450,450,650,650,650,
        0.3,0.3,0.3,450,450,450,650,650,650
    };*/
    
    public ClasCalorimeter(){
        for(int i = 0; i < minimum.length; i++) minimum[i] = 0.0;
        store.add("REC::Calorimeter","ECAL::moments","ECAL::calib");
    }
    
    public static ClasCalorimeter with(HipoChain chain){
        ClasCalorimeter calo = new ClasCalorimeter();
        calo.store.init(chain);
        return calo;
    }
    
    public void read(Event event){
        store.read(event);
    }
    
    public int[] findResponse(int pindex){
        int[] index = new int[]{-1,-1,-1};
        Bank response = store.getBanks().get(0);
        int nrows = response.getRows();
        for(int i = 0; i < nrows; i++){
            int part = response.getInt("pindex", i);
            int layer = response.getInt("layer", i);
            if(part==pindex){
                if(layer==1) index[0] = i;
                if(layer==4) index[1] = i;
                if(layer==7) index[2] = i;
            }
        }
        return index;
    }
    
    public float[]  getHitPosition(int pindex){
        float[] pos = new float[]{0.0f,0.0f,0.0f};
        int[] index = this.findResponse(pindex);
        if(index[0]>=0){
            int k = index[0];
            pos[0] = store.getBanks().get(0).getFloat("hx", k);
            pos[1] = store.getBanks().get(0).getFloat("hy", k);
            pos[2] = store.getBanks().get(0).getFloat("hz", k);
        }
        return pos;
    }
    
    public double[] getResponse(int pindex){
        
        double[] r = new double[28];
        for(int k = 0; k < r.length; k++) r[k] = 0.0;
        
        //int nrows = store.getBanks().get(0).getRows();
        
        int[] index = this.findResponse(pindex);
        
        Bank mom = store.getBanks().get(1);
        Bank cal = store.getBanks().get(2);
        
        for(int i = 0; i < index.length; i++){
            int offset = i*9 + 1;
            if(index[i]>=0){
                int k = index[i];
                r[offset + 0] = cal.getFloat("recEU", k);
                r[offset + 1] = cal.getFloat("recEV", k);
                r[offset + 2] = cal.getFloat("recEW", k);
                
                r[offset + 3] = mom.getFloat("distU", k);
                r[offset + 4] = mom.getFloat("distV", k);
                r[offset + 5] = mom.getFloat("distW", k);
                
                r[offset + 6] = mom.getFloat("m2u", k);
                r[offset + 7] = mom.getFloat("m2v", k);
                r[offset + 8] = mom.getFloat("m2w", k);
                
            }
        }
        
        return r;
    }
    
    public double[] getResponse(double p, int pindex){
        double[] r = this.getResponse(pindex);
        r[0] = p;
        /*for(int k = 0; k < 3; k++) r[k +  1] = r[k +  1]/p;
        for(int k = 0; k < 3; k++) r[k + 10] = r[k + 10]/p;
        for(int k = 0; k < 3; k++) r[k + 19] = r[k + 19]/p;
        */
        for(int k = 0; k < minimum.length; k++){
            if(r[k]>minimum[k]&&r[k]<maximum[k]){
                double value = (r[k] - minimum[k])/(maximum[k]-minimum[k]);
                r[k] = value;
            } else r[k] = 0.0;
        }
        return r;
    }
    
    public float[] getResponseFloat(double p, int pindex){
        double[] r = this.getResponse(p,pindex);
        float [] f = new float[r.length];
        for(int i = 0; i < f.length; i++) f[i] = (float) r[i];
        return f;
    }
    //
}
