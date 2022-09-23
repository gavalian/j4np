/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import javax.swing.JFrame;
import twig.graphics.TGAxisFrame;
import twig.graphics.TGDataCanvas;
import twig.graphics.TGRegion;

/**
 *
 * @author gavalian
 */
public class AxisDebug {
    
    public static void logarithmic(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TGDataCanvas dc = new TGDataCanvas();
        
        TGRegion af = new TGRegion(true);
        
        af.getAxisFrame().getAxisY().isLogarithmic = true;
        af.getAxisFrame().setLogY(true);
        
        dc.addNode(af);
        frame.add(dc);

        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
    
    public static void main(String[] args){
        AxisDebug.logarithmic();
    }
}
