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
public interface DataBuffer {    
    public ByteBuffer  getBuffer();
    public int         bufferLength();
    public boolean     allocate(int size); // size is given in bytes
    public int         identifier();
    public boolean     verify();
}
