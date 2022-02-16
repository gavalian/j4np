/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.analysis.clas12;

import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.Vector3;
import java.util.Arrays;
import twig.data.H1F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class TrackClassifierStats extends DataWorker<HipoReader,Event> {

    protected String trackBankName = "TimeBasedTrkg::TBTracks";
    protected String  partBankName = "TimeBasedTrkg::TBTracks";
    protected String  aiClassifier = "ai::tracks";
    
    protected Bank       trackBank = null;
    protected Bank          aiBank = null;
    
    protected H1F   hConvTracks = null;
    protected H1F     hAiTracks = null;
    
    protected int          bins = 25;
    
    
    protected int      orderCL = 0;
    protected int      orderVZ = 0;
    protected int      orderPX = 0;
    protected int      orderQ  = 0;
    protected int    orderCHI2 = 0;

    protected int     clusterOrderAi   = 0;
    
    protected TDirectory       dir = new TDirectory();
    
    protected String    outputFile = "trackClassifierOutput.twig";
    
    
    protected ParticleHistos hPosConv = null;
    protected ParticleHistos hNegConv = null;
    
    protected ParticleHistos hPosConvV = null;
    protected ParticleHistos hNegConvV = null;
    
    protected ParticleHistos hPosAi = null;
    protected ParticleHistos hNegAi = null;
    
    protected ParticleHistos hPosAi5 = null;
    protected ParticleHistos hNegAi5 = null;
    
    protected ParticleHistos hPosAiV = null;
    protected ParticleHistos hNegAiV = null;
    
    public TrackClassifierStats(){
        
        hConvTracks = new H1F("hConvTracks",bins,0.0,11.0);
        hAiTracks   = new H1F(  "hAiTracks",bins,0.0,11.0);
        
        hPosConv = new ParticleHistos("hconv","pos");
        hNegConv = new ParticleHistos("hconv","neg");        
        hPosConvV = new ParticleHistos("hconv_vz","pos");
        hNegConvV = new ParticleHistos("hconv_vz","neg");
        
        hPosAi  = new ParticleHistos("hai","pos");
        hNegAi  = new ParticleHistos("hai","neg");  
        hPosAi5 = new ParticleHistos("hai5","pos");
        hNegAi5 = new ParticleHistos("hai5","neg");
        hPosAiV = new ParticleHistos("hai_vz","pos");
        hNegAiV = new ParticleHistos("hai_vz","neg");
        
        hPosConv.toDir(dir, "/particle");
        hPosConvV.toDir(dir, "/particle");
        hNegConv.toDir(dir, "/particle");
        hNegConvV.toDir(dir, "/particle");
        
        hPosAi.toDir(dir, "/particle");
        hPosAiV.toDir(dir, "/particle");
        hNegAi.toDir(dir, "/particle");
        hNegAiV.toDir(dir, "/particle");
        
        dir.add("/compare", hConvTracks);
        dir.add("/compare", hAiTracks);
    }
    
    @Override
    public boolean init(HipoReader src) {
        trackBank = src.getBank(trackBankName);
        aiBank    = src.getBank(aiClassifier);
        
        orderCL = trackBank.getSchema().getEntryOrder("Cluster1_ID");
        orderVZ = trackBank.getSchema().getEntryOrder("Vtx0_z");
        orderQ  = trackBank.getSchema().getEntryOrder("q");
        orderPX = trackBank.getSchema().getEntryOrder("p0_x");
        orderCHI2 = trackBank.getSchema().getEntryOrder("chi2");
        
        clusterOrderAi = aiBank.getSchema().getEntryOrder("c1");
        return true;
    }

    @Override
    public void execute(Event e) {
        e.read(aiBank);
        e.read(trackBank);
        
        int rows = trackBank.getRows();
        for(int i = 0; i < rows; i++){
            Vector3   v = getTrackVector(trackBank, i);
            double   vz = trackBank.getFloat(orderVZ, i);
            int charge = trackBank.getInt(orderQ, i);
            boolean isValid = trackCheck(trackBank,i);
            if(v.mag()>1.0){
                if(charge<0){
                    hNegConvV.hP.fill(v.mag());
                    hNegConvV.hFi.fill(Math.toDegrees(v.phi()));
                    hNegConvV.hTh.fill(Math.toDegrees(v.theta()));
                    hNegConvV.hVz.fill(vz);
                } else {
                    hPosConvV.hP.fill(v.mag());
                    hPosConvV.hFi.fill(Math.toDegrees(v.phi()));
                    hPosConvV.hTh.fill(Math.toDegrees(v.theta()));
                    hPosConvV.hVz.fill(vz);
                }
            }
            
            
            int[] c1 = this.getTrackClusters(trackBank, i);
            if(charge!=0&&v.mag()>0.5&&isValid&&c1[0]>0){
                //int[] c1 = this.getTrackClusters(trackBank, i);
                hConvTracks.fill(v.mag());
                if(charge<0){
                    hNegConv.hP.fill(v.mag());
                    hNegConv.hFi.fill(Math.toDegrees(v.phi()));
                    hNegConv.hTh.fill(Math.toDegrees(v.theta()));
                    hNegConv.hVz.fill(vz);
                } else {
                    hPosConv.hP.fill(v.mag());
                    hPosConv.hFi.fill(Math.toDegrees(v.phi()));
                    hPosConv.hTh.fill(Math.toDegrees(v.theta()));
                    hPosConv.hVz.fill(vz);
                }
                
                int airows = aiBank.getRows();
                
                boolean flag = false;
                boolean flag5 = false;
                System.out.println("---- " + i + "  charge = " + charge );
                for(int k = 0; k < airows; k++){
                    int[] c2 = this.getAiClusters(aiBank, k);
                    System.out.println(Arrays.toString(c1) 
                            + " ==> " + Arrays.toString(c2) 
                            + " result = " + this.compare(c1, c2));
                    
                    if(this.compare(c1, c2)==true) flag = true;
                    if(this.match(c1, c2)>=4) flag5 = true;
                }
                
                System.out.println("result = " + flag);
                if(flag==true){
                    if(charge<0){
                        hNegAi.hP.fill(v.mag());
                        hNegAi.hFi.fill(Math.toDegrees(v.phi()));
                        hNegAi.hTh.fill(Math.toDegrees(v.theta()));
                        hNegAi.hVz.fill(vz);
                    } else {
                        hPosAi.hP.fill(v.mag());
                        hPosAi.hFi.fill(Math.toDegrees(v.phi()));
                    hPosAi.hTh.fill(Math.toDegrees(v.theta()));
                    hPosAi.hVz.fill(vz);
                    }
                }
                if(flag5==true){
                    if(charge<0){
                        hNegAi5.hP.fill(v.mag());
                        hNegAi5.hFi.fill(Math.toDegrees(v.phi()));
                        hNegAi5.hTh.fill(Math.toDegrees(v.theta()));
                        hNegAi5.hVz.fill(vz);
                    } else {
                        hPosAi5.hP.fill(v.mag());
                        hPosAi5.hFi.fill(Math.toDegrees(v.phi()));
                        hPosAi5.hTh.fill(Math.toDegrees(v.theta()));
                        hPosAi5.hVz.fill(vz);
                    }           
                    //hAiTracks.fill(v.mag());
                }
            }
            
        }
    }
    
    private boolean trackCheck(Bank b, int row){
        
        double chi2 = b.getFloat(orderCHI2, row);
        double   vz = b.getFloat(orderVZ, row);
        Vector3 vec = this.getTrackVector(b, row);
        double th = Math.toDegrees(vec.theta()); 
        double fi = Math.toDegrees(vec.phi()); 
        if(vec.mag()<1.0)   return false;
        if(th<12.0||th>25.0) return false;
        if(fi<-20.0||fi>20.0) return false;
        if(chi2>300.0)      return false;
        if(vz<-7||vz>-2)    return false;
        
        return true;
    }
    
    
    
    private Vector3 getTrackVector(Bank b, int row){
        return new Vector3(b.getFloat(orderPX, row),
                b.getFloat(orderPX+1, row),
                b.getFloat(orderPX+2, row));
    }
    
    private int match(int[] c1, int c2[]){
        int count = 0;
        for(int i = 0; i < c1.length; i++)
            if(c1[i]>0&&c1[i]==c2[i]) count++;                
        return count;
    }
    
    private boolean compare(int[] c1, int c2[]){
        for(int i = 0; i < c1.length; i++) 
            if(c1[i]!=c2[i]) return false;
        return true;
    }
    
    private int[] getAiClusters(Bank b, int row){
        int[] cl = new int[6];
        for(int i = 0; i < 6; i++) 
            cl[i] = b.getInt(clusterOrderAi+i, row);
        for(int i = 0; i < 6; i++)
            if(cl[i]<=0) cl[0] = -1;
        return cl;
    }
    
    private int[] getTrackClusters(Bank b, int row){
        int[] cl = new int[6];
        for(int i = 0; i < 6; i++) 
            cl[i] = b.getInt(orderCL+i, row); 

        for(int i = 0; i < 6; i++)
            if(cl[i]<=0) cl[0] = -1;
        return cl;
    }
    
    @Override
    public boolean finilize(){
        
        H1F h1 = H1F.divide(hAiTracks, hConvTracks);        
        h1.setName("hRatioNeg");
        H1F[] h = new H1F[16];
        
        h[0] = H1F.divide(hNegAi.hP, hNegConv.hP);
        h[0].setName("ratio_neg_p");
        
        h[1] = H1F.divide(hNegAi.hTh, hNegConv.hTh);
        h[1].setName("ratio_neg_th");
        
        h[2] = H1F.divide(hNegAi.hFi, hNegConv.hFi);
        h[2].setName("ratio_neg_fi");
        
        h[3] = H1F.divide(hNegAi.hVz, hNegConv.hVz);
        h[3].setName("ratio_neg_vz");
        
        h[4] = H1F.divide(hPosAi.hP, hPosConv.hP);
        h[4].setName("ratio_pos_p");
        
        h[5] = H1F.divide(hPosAi.hTh, hPosConv.hTh);
        h[5].setName("ratio_pos_th");
        
        h[6] = H1F.divide(hPosAi.hFi, hPosConv.hFi);
        h[6].setName("ratio_pos_fi");
        
        h[7] = H1F.divide(hPosAi.hVz, hPosConv.hVz);
        h[7].setName("ratio_pos_vz");
        
        
        
        h[8] = H1F.divide(hNegAi5.hP, hNegConv.hP);
        h[8].setName("ratio5_neg_p");
        
        h[9] = H1F.divide(hNegAi5.hTh, hNegConv.hTh);
        h[9].setName("ratio5_neg_th");
        
        h[10] = H1F.divide(hNegAi5.hFi, hNegConv.hFi);
        h[10].setName("ratio5_neg_fi");
        
        h[11] = H1F.divide(hNegAi5.hVz, hNegConv.hVz);
        h[11].setName("ratio5_neg_vz");
        
        h[12] = H1F.divide(hPosAi5.hP, hPosConv.hP);
        h[12].setName("ratio5_pos_p");
        
        h[13] = H1F.divide(hPosAi5.hTh, hPosConv.hTh);
        h[13].setName("ratio5_pos_th");
        
        h[14] = H1F.divide(hPosAi5.hFi, hPosConv.hFi);
        h[14].setName("ratio5_pos_fi");
        
        h[15] = H1F.divide(hPosAi5.hVz, hPosConv.hVz);
        h[15].setName("ratio5_pos_vz");
        
        for(int i = 0; i < h.length; i++)
            dir.add("/ratios", h[i]);
        
        dir.write(this.outputFile);
        return true;
    }
    
    
    public class ParticleHistos {
        public H1F   hP = null;
        public H1F  hTh = null;
        public H1F  hFi = null;
        public H1F  hVz = null;
        
        public ParticleHistos(String prefix, String postfix){           
             hP = new H1F(prefix + "_p_"  + postfix,160,0.0,10.0);
            hTh = new H1F(prefix + "_th_" + postfix,120,0.0,60.0);
            hFi = new H1F(prefix + "_fi_" + postfix,180,-180.0,180.0);
            hVz = new H1F(prefix + "_vz_" + postfix,240,-30.0,30.0);
        }
        
        public void toDir(TDirectory dir, String directory){
            dir.add(directory, hTh).add(directory, hP)
                    .add(directory, hFi).add(directory, hVz);
        }
    }
}
