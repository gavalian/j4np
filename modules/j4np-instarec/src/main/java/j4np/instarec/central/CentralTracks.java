/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.central;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;

/**
 *
 * @author gavalian
 */
public class CentralTracks {
    
    CompositeNode node = null;
    /**
     * The Central detector patterns
     * 0,1,2,3,4,5 - is SVT 
     * 6
     */
    
    public static int[][] patterns = new int[][]{
        {0,1,2,3,   4,  5}, {0,1,2,3,   6,  8}, {0,1,2,3,   6, 10},
        {0,1,2,3,   6,  7}, {0,1,2,3,   6,  8}, {0,1,2,3,   6, 10},     
        //{0,1,2,3,   6,  9}, {0,1,2,3,   8,  9}, {0,1,2,3,   9, 10},        
        {0,1,2,3,   7, 11}, {0,1,2,3,   8, 11}, {0,1,2,3,  10, 11},        
        //-------------
        {0,1,4,5,   6,  7}, {0,1,4,5,   6,  8}, {0,1,4,5,   6, 10},        
        {0,1,4,5,   6,  9}, {0,1,4,5,   8,  9}, {0,1,4,5,   9, 10},        
        {0,1,4,5,   7, 11}, {0,1,4,5,   8, 11}, {0,1,4,5,  10, 11},
        //-------------
        {2,3,4,5,   6,  7}, {2,3,4,5,   6,  8}, {2,3,4,5,   6, 10},        
        {2,3,4,5,   6,  9}, {2,3,4,5,   8,  9}, {2,3,4,5,   9, 10},        
        {2,3,4,5,   7, 11}, {2,3,4,5,   8, 11}, {2,3,4,5,  10, 11},        
    };
    
    public CentralTracks(){
        node = new CompositeNode(33000,1,"6i6i",256);
        node.setRows(0);
    }
    
    public void fill(int[] array){
        int row = node.getRows();
        for(int i = 0; i < 12; i++){
            if(array[i]>0) node.putInt(i, row, array[i]-1); else {node.putInt(i, row, -1);}
        }
        node.setRows(row+1);
    }
    
    public CompositeNode node(){return node;}
    public void show(){node.print();}
    
    public void getSubTrack(int row, int[] cid, int order){
        for(int i = 0; i < patterns[order].length; i++){
            cid[i] = node.getInt(patterns[order][i], row);
        }
    }
    
    public int status(int[] cid, Bank b){
        for(int i = 0; i < cid.length; i++)
            if(b.getInt("status", cid[i])<1) return 0;
        return 1;
    }
    
    public boolean isComplete(int[] cid){
        for(int i = 0; i < cid.length; i++) if(cid[i]<0) return false;
        return true;
    }
    
    public float[] getFeatures(int[] cid, Bank b){
        float[] features = new float[24];
        for(int i = 0; i < 6; i++){
            float[] pf = CentralUtils.getFeatures(b, cid[i]);
            int start = i*4;
            for(int k = 0; k < 4; k++){
                features[k+start] = pf[k];
            }
        }
        return features;
    }
    
}
