/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.particle;

import j4np.hipo5.data.Bank;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class ClasParticle {

    public double getInferedProb() {
        return inferedProb;
    }

    public void setInferedProb(double inferedProb) {
        this.inferedProb = inferedProb;
    }

   


    
    private Vector3    pvec = new Vector3();
    private Vector3    pvrt = new Vector3();      
    private int       order = 0;    
    private int         pid = 0;
    private int  inferedPid = 0;
    private double  inferedProb = 0.0;
    private int      charge = 0;
    private double     nphe = 0.0;
    
        
    private ClasCalorimeter calo = new ClasCalorimeter();
    
    public ClasParticle(){}
    
    public Vector3 vector(){ return pvec;}
    public Vector3 vertex(){ return pvrt;}
        public int getOrder() {
        return order;
    }
        
    public void setOrder(int order) { this.order = order;}
    public int getPid() {return pid;}
    public void setPid(int pid) { this.pid = pid;}
    public int getCharge() { return charge;}
    public void setCharge(int charge) { this.charge = charge;}
    public double getNphe() {return nphe;}
    public void setNphe(double nphe) {this.nphe = nphe;}
            
     public int getInferedPid() {
        return inferedPid;
    }

    public void setInferedPid(int inferedPid) {
        this.inferedPid = inferedPid;
    }
    
    public ClasCalorimeter calorimeter(){ return calo; }
            
    public double[] getFeatures(){
        double[] f = calo.getResponsesCopy();
        for(int i = 0; i < 9; i++) f[i] = f[i]/pvec.mag();
        return f;
    }
    
    public int featureSize(){
        int count = 0;
        for(int i = 0; i < 9; i++){
            if(calo.responses[i]>0.00001) count++;
        }
        return count;
    }
    
    public static ClasParticle initFromBanks(Bank bPART,Bank bCALO,
            Bank bCALI,Bank bCHER, int pindex){
        ClasParticle cp = new ClasParticle();
        cp.setOrder(pindex);
        cp.setCharge(bPART.getInt("charge", pindex));
        cp.setPid(bPART.getInt("pid", pindex));
        cp.vector().setXYZ(bPART.getFloat("px", pindex), 
                bPART.getFloat("py", pindex),bPART.getFloat("pz", pindex)
                );
        
        List<Integer>  ecidx = ClasParticle.getIndexEC(bCALO, pindex);
        cp.calo.reset();
        
        for(int i = 0; i < ecidx.size(); i++){
               cp.calo.responses[0+i] = bCALI.getFloat("recEU", ecidx.get(i));
               cp.calo.responses[3+i] = bCALI.getFloat("recEV", ecidx.get(i));
               cp.calo.responses[6+i] = bCALI.getFloat("recEW", ecidx.get(i));

               cp.calo.responses[9+i]  = bCALO.getFloat("lu", ecidx.get(i));
               cp.calo.responses[12+i] = bCALO.getFloat("lv", ecidx.get(i));
               cp.calo.responses[15+i] = bCALO.getFloat("lw", ecidx.get(i));

               cp.calo.responses[18+i] = bCALO.getFloat("m2u", ecidx.get(i));
               cp.calo.responses[21+i] = bCALO.getFloat("m2v", ecidx.get(i));
               cp.calo.responses[24+i] = bCALO.getFloat("m2w", ecidx.get(i));
               
               int layer = bCALO.getInt("layer", i);
               if(layer==1) 
                   cp.calo.position.setXYZ(
                           bCALO.getFloat("x", i),
                           bCALO.getFloat("y", i),
                           bCALO.getFloat("z", i)
                   );
        }
        
        for(int i = 0; i < bCHER.getRows(); i++){
            int pi = bCHER.getInt("pindex", i);
            if(pi==pindex) cp.nphe = bCHER.getFloat("nphe",i);
        }
        return cp;
    }
    
    private static List<Integer> getIndexEC(Bank bCALO, int pindex){
        List<Integer> idx = new ArrayList<>();
        for(int i = 0; i < bCALO.getRows(); i++)
            if(bCALO.getInt("pindex", i)==pindex) idx.add(i);
        return idx;
    }
    
    @Override
    public String toString(){
        return String.format("%3d %4d %5d %5d %6.4f %8.2f (%8.5f %8.5f %8.5f) %s", 
                order,charge,pid,inferedPid,inferedProb,nphe,pvec.x(),pvec.y(),pvec.z(),calo.toString());
    }
    
    public static class ClasCalorimeter {
        
        double[] responses = new double[27];
        int        regions = 0;
        Vector3   position = new Vector3();
        
        public ClasCalorimeter(){
            
        }
        
        public void reset(){ 
            for(int i = 0; i < responses.length;i++) responses[i]=0.0;
        }
        
        public double[] getResponsesCopy(){
            double[] a = new double[responses.length];
            System.arraycopy(responses, 0, a, 0, responses.length);
            return a;
        }
        
        public double[] getResponseShort(){
            double[] a = new double[9];
            return a;
        }
        
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append(String.format("[%8.3f %8.3f %8.3f] ",
                    position.x(),position.y(),position.z()));
            for(int i = 0; i < 9; i++) 
                str.append(String.format("%8.6f ",responses[i]));
            for(int i = 0; i < 9; i++) 
                str.append(String.format("%8.2f ",responses[i+9]));
            for(int i = 0; i < 9; i++) 
                str.append(String.format("%8.3f ",responses[i+18]));
            return str.toString();
        }
    }
    
}
