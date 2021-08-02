/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

/**
 *
 * @author gavalian
 */
public class Particle {
    
    private String name = "default";
    
    private LorentzVector       pVector = new LorentzVector();
    private LorentzVector pVectorHelper = new LorentzVector();
    
    private int[] particleIDs   = null;
    private int[] particleOrder = null;
    private int[] particleSign  = null;
    private double[] particleMass = null;   
    public Particle(){
        
    }
    
    protected void initialize(int[] ids, int[] order, int[] sign){
        
        particleIDs   = new int[ids.length];
        particleOrder = new int[ids.length];
        particleSign  = new int[ids.length];
        particleMass  = new double[ids.length];
                
        for(int i = 0; i < ids.length; i++){
            
            PDGParticle  p = PDGDatabase.getParticleById(ids[i]);
            if(p==null){
                particleMass[i] = 0.0;
            } else {
                particleMass[i] = p.mass();
            }
            particleIDs  [i]  = ids[i];
            particleOrder[i]  = order[i];
            particleSign [i]  = sign[i];
        }
    }
    
    public void calculate(PhysicsEvent event){
        pVector.setPxPyPzM(0, 0, 0, 0);
        for(int i = 0; i < particleIDs.length; i++){
            event.vector(pVectorHelper, particleMass[i], particleIDs[i], particleOrder[i]);
            if(particleSign[i]>0){
                pVector.add(pVectorHelper);
            } else{
                pVector.sub(pVectorHelper);
            }
        }
    }
    
    public LorentzVector vector(){ return pVector;}
    
    
}
