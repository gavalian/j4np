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
    
    public int     getType();
    public int     count();
    public String  format();
    
    public double  getDouble( int index);
    public int     getInt(    int index);
    
    public double  getDouble( int order, int index);
    public int     getInt(    int order, int index);
        
}
