/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.base;

/**
 *
 * @author gavalian
 */
public interface DataNode extends DataBuffer {
    
    public int getType();
    public int count();
    public String format();
    
}
