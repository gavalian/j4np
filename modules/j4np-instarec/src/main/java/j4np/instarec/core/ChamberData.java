/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.core;

/**
 *
 * @author gavalian
 */
public class ChamberData {
    
    public long[] hits = new long[12*6*6];
    public int[]  scan = new int[112];
    public int[]  type = new int[112];
    public long[] temp = new long[6];
    
    public int[]  results = null;
    
    
    public int  wireWord( int wire){ return wire<=56?0:1;}
    
    public int  wireShift( int wire){
        int shift = wire<=56?56-wire:56-(wire-56);
        return shift;
    }
    
    public long get(int sector, int layer, int wire){
        int soffset = 12*6*(sector-1);
        int loffset = (layer-1)*2;
        int   word = wireWord(wire);
        int   wshift = wireShift(wire);
        long value = (hits[soffset+loffset+word]>>wshift)&0x1L;
        return value;
    }
    
    public void set(int sector, int layer, int wire){
        int soffset = 12*6*(sector-1);
        int loffset = (layer-1)*2;
        int   word = wireWord(wire);
        int   wshift = wireShift(wire);
        hits[soffset+loffset+word] = hits[soffset+loffset+word]|(1L<<wshift);
    }        
}
