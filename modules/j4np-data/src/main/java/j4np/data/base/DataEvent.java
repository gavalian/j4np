/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.base;

import java.nio.ByteBuffer;

/**
 *
 * @author gavalian
 */
public interface DataEvent extends DataBuffer {
    public void        getAt(DataNode node, int position);
    public String      showString();
}
