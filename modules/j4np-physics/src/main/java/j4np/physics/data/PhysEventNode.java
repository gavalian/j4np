/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.data;

import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.physics.PDGDatabase;
import j4np.physics.PDGParticle;
import j4np.physics.Vector3;

/**
 *
 * @author gavalian
 */
public class PhysEventNode extends PhysDataEvent {
    CompositeNode dataNode = new CompositeNode(32100,1,"",64);
    
    public PhysEventNode(){
        
    }
    
    @Override
    public void init(HipoReader r){
        
    }
    
    @Override
    public int count() {
        return dataNode.getRows();
    }

    @Override
    public void read(Event event){
        event.read(dataNode,32100,3);
        //properties.read(event);
        //System.out.printf(" bank ");
    }
    
    @Override
    public int charge(int index) {       
        return dataNode.getInt(1, index);        
    }

    @Override
    public int pid(int index) {
        return dataNode.getInt(3, index);
    }

    @Override
    public int status(int index) {
        
        return dataNode.getInt(11, index);
        
    }

    @Override
    public void status(int index, int value) {        
        dataNode.putShort(11, index, (short) value); 
    }

    @Override
    public void vector(Vector3 v, int index) {
        v.setXYZ(
                dataNode.getDouble(4, index), dataNode.getDouble(5, index), 
                dataNode.getDouble(6, index)
                );
    }

    @Override
    public void vertex(Vector3 v, int index) {
        v.setXYZ(
                dataNode.getDouble(7, index), dataNode.getDouble(8, index), 
                dataNode.getDouble(9, index)
                );
    }
}
