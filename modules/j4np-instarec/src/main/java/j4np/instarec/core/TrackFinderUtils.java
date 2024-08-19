/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

import j4np.hipo5.data.Bank;
import j4np.instarec.core.TrackConstructor.CombinationCuts;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TrackFinderUtils {
    
    public TrackFinderUtils(){
        
    }
    
    
    public static boolean isComplete(int[] cid){
        for(int k = 0; k < cid.length; k++) if(cid[k]<=0) return false;
        return true;
    }
    
    public static boolean isSame(int[] cida, int[] cidb){
        for(int k = 0; k < cida.length; k++) if(cida[k]!=cidb[k]) return false;
        return true;
    }
    
    public static void getSegmentBank(Bank segments, Bank hbclusters){
        int nrows = hbclusters.getRows();
        segments.setRows(nrows);
        for(int i = 0; i < nrows; i++){
            segments.putShort(       "id", i, hbclusters.getShort("id", i));
            segments.putByte(    "sector", i, hbclusters.getByte("sector", i));
            segments.putByte("superlayer", i, hbclusters.getByte("superlayer", i));
            segments.putFloat(     "mean", i, hbclusters.getFloat("avgWire", i));
            segments.putFloat(    "slope", i, hbclusters.getFloat("fitSlope", i));
            segments.putByte(    "status", i, (byte) 0);
        }
    }
    
    public static void getSector(TrackConstructor constructor, int sector, Tracks list){
        list.dataNode().setRows(0);        
        CombinationCuts cuts = new CombinationCuts() {
            @Override
            public boolean validate(double m1, double m2, double m3, double m4, double m5, double m6) {
                if(Math.abs(m1-m2)>25.0) return false;
                if(Math.abs(m3-m4)>25.0) return false;
                if(Math.abs(m5-m6)>25.0) return false;
                return true;
            }        
        };
        
        constructor.sectors[sector-1].create(list, sector,cuts);
        
        /*
        Tracks nList = new Tracks(4096);
        constructor.sectors[sector-1].create(nList, sector,cuts);
        System.out.printf(" track candidates without a cut = %8d, with cuts %8d\n",
                list.size(),nList.size());
        */
    }
    
    public static int which(float[] cid){
        for(int i = 0; i < cid.length; i++) if(cid[i]<0.0005) return i;
        return -1;
    }
    
    public static int maxBin(float[] cid){
        double max = cid[0]; int bin = 0;
        for(int i = 0; i < cid.length; i++) if(cid[i]>max){ max = cid[i]; bin = i;}
        return bin;
    }
    
    public static void copyFromTo(Tracks trkFrom, Tracks trkTo){
        for(int k = 0; k < trkFrom.dataNode().getRows();k++){
            if(trkFrom.status(k)>0&&trkFrom.probability(k)>0.5){
                int rows = trkTo.dataNode().getRows();
                trkTo.dataNode().setRows(rows+1);
                trkTo.dataNode().copyRow(trkFrom.dataNode(), k, rows);                
            }
        }
    }
    
    public static void fillConstructor(TrackConstructor tc, Bank bank){
        tc.reset();
        int nrows = bank.getRows();
        //bank.getSchema().show();
        for(int r = 0; r < nrows; r++){
            if(bank.getInt("status",r)>=0){
                tc.add(
                        bank.getInt("sector", r),
                        bank.getInt("superlayer", r),
                        bank.getInt("id", r),
                        bank.getFloat("wireL1", r),
                        bank.getFloat("wireL6", r)
                );
            }
        }
    }
    
    public static void fillConstructor(TrackConstructor tc, Bank bank, List<Integer> cids){
        tc.reset();
        int nrows = bank.getRows();
        //bank.getSchema().show();
        for(int r = 0; r < nrows; r++){
            int id = bank.getInt("id", r);
            if(cids.contains(id)==false){
                tc.add(
                        bank.getInt("sector", r),
                        bank.getInt("superlayer", r),
                        bank.getInt("id", r),
                        bank.getFloat("wireL1", r),
                        bank.getFloat("wireL6", r)
                );
                //System.out.println(" row value   " + r + " " + bank.getFloat("wireL6", r));
            }
        }
    }
    
    public static void evaluate(InstaRecNetworks net, Tracks tr){
        float[] f = new float[12];
        float[] r = new float[3];
        
        for(int i = 0; i < tr.getRows(); i++){
            tr.getInput12(f, i);
            net.getClassifier().feedForwardSoftmax(f, r);
            tr.applyOutput(r, i);
        }
    }
}
