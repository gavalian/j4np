/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.data.base;

/**
 *
 * @author gavalian
 */
public abstract class DataActor<T extends DataSource,V extends DataEvent> {
    
    public DataActor(){}
    
    public abstract boolean init(T source);
    public abstract boolean configure(String config);
    public abstract boolean process(V event);
    
}
