/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import j4np.graphics.CanvasLayout;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class LayoutDebug {
    public static void divideWithLayout(){
        TGCanvas c = new TGCanvas();        
        
        CanvasLayout l = new CanvasLayout();
        l.addRow(0., 0.25, new double[]{0.5,0.5})
                .addRow(0.25, 0.5, new double[]{0.25,0.5,0.25})
                .addRow(0.75, 0.25, new double[]{0.2,0.2,0.4,0.2});
        
        l.show();
        //c.view().divide(3, 3);
        c.view().divide(l);
        for(int i = 0; i < 9; i++){
            H1F h = TDataFactory.createH1F(3000);
            c.cd(i).draw(h);
        }
    }
    
    public static void main(String[] args){
        LayoutDebug.divideWithLayout();
    }
}
