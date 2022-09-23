/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.track;

/**
 *
 * @author gavalian
 */
public class DcMatrix2D extends TrackMatrix2D {
    
    public DcMatrix2D(){
        super(112,36,new String[]{"sector","superlayer","layer","wire"});
    }
    
    @Override
    public int[] toPoint(int[] ids){
        int y = (ids[1]-1)*6 + ids[2]-1;
        int x = ids[3]-1;
        return new int[]{x,y};
    }
}
