/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.data.base;

import java.util.List;

/**
 *
 * @author gavalian
 * @param <T> type of event class
 */
public abstract class FrameWorker<T extends DataEvent> {

    public abstract void    execute(List<T> e);
    
}
