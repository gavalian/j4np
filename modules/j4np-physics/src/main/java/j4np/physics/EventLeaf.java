/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics;

import j4np.hipo5.data.Leaf;

/**
 *
 * @author gavalian
 */
public class EventLeaf extends PhysicsEvent {
    private Leaf dataLeaf = null;
    
    private int     index_pid = 0;
    private int  index_charge = 3;
    private int  index_px     = 5;
    private int  index_py     = 6;
    private int  index_pz     = 7;
    private int  index_vx     = 8;
    private int  index_vy     = 9;
    private int  index_vz     = 10;
    
    public EventLeaf setLeaf(Leaf l){ dataLeaf = l; return this;}
    
    @Override
    public int count() {
        return dataLeaf.getRows();
    }

    @Override
    public int charge(int index) {
        return dataLeaf.getInt(index_charge, index);
    }

    @Override
    public int pid(int index) {
        return dataLeaf.getInt(index_pid, index);
    }

    public Leaf getLeaf(){
        return dataLeaf;
    }
    
    @Override
    public int status(int index) {
        return 1;
    }

    @Override
    public double chi2(int index) {
        return 0.0;
    }

    @Override
    public void status(int index, int value) {
        
    }

    @Override
    public void vector(Vector3 v, int index) {
        v.setXYZ(dataLeaf.getDouble(index_px, index), 
                dataLeaf.getDouble(index_py, index),
                dataLeaf.getDouble(index_pz, index));
    }

    @Override
    public void vertex(Vector3 v, int index) {
        v.setXYZ(dataLeaf.getDouble(index_vx, index), 
                dataLeaf.getDouble(index_vy, index),
                dataLeaf.getDouble(index_vz, index));
    }
    
}
