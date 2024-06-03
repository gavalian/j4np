/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.segments;

import j4np.hipo5.data.CompositeNode;

/**
 *
 * @author gavalian
 */
public class WireStore {
    
    private int         BSIZE = 6*112*36;
    private int    wireTDCLow = 65;
    private int   wireTDCHigh = 950;
    
    int[] wires = new int[6*112*36];
    int[] restore = new int[6*112*36];    
    
    public WireStore(){
        for(int i = 0; i < restore.length; i++){
            restore[i] = 0;
        }
    }
    
    public void reset(){
        System.arraycopy(restore, 0, wires, 0, restore.length);
    }
    
    public int index(int sector, int layer, int wire){
        int indx = (sector-1)*112*36 + (layer-1)*112 + (wire-1);
        return indx;
    }
    
    public void fill(CompositeNode node){
        int nrows = node.getRows();
        for(int r = 0; r < nrows; r++){
            int index = index(node.getInt(0, r), node.getInt(1,r),node.getInt(2, r));
            int tdc = node.getInt(4, r);
            if(index>=0&&index<BSIZE&&tdc>wireTDCLow&&tdc<wireTDCHigh) wires[index] = tdc;
        }
    }
    
    public void getData(int sector, int superlayer, int[] array){
        int offset = (sector-1)*112*36 + (superlayer-1)*6*112;
        System.arraycopy(wires, offset, array, 0, array.length);
    }
    
    public static void main(String[] args){
        
    }
}
