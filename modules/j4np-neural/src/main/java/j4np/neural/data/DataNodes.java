/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

import j4np.hipo5.data.CompositeNode;

/**
 *
 * @author gavalian
 */
public class DataNodes {
    
    public static CompositeNode getNodeDC(){
        
        return new CompositeNode(12,1,"bbsbil",4060);
    }
    
    public static CompositeNode getNodeEC(){        
        return new CompositeNode(11,2,"bbsbil",4060);
    }
    
    public static CompositeNode getNodeClusters(){
        // 1 - cluster id
        // 2 - sector 
        // 3 - layer
        // 4 - mean
        // 5 - slope
        return new CompositeNode(32100,1,"3b2f",512);
    }
    
    public static CompositeNode getNodeTracks(){
        // 0 - track id
        // 1 - track sector
        // 2 - track charge 
        // 3 - ai assigned probability
        // 4 - 9 cluster id's from cluster bank
        // 10 - 15 cluster mean positions
       return new CompositeNode(32100,2,"3sf6s6f",128);
    }
    
    public static CompositeNode getNodeParticles(){
        // 0 - id (s),
        // 1 - charge
        // 2 - sector 
        // 3 - particle id
        // 4,5,6 - px,py,pz
        // 7,8,9 - vx,vy,vz
        // 10 - chi2pid
        // 11 - status
       return new CompositeNode(32100,3,"3si7fs",128);
    }
}
