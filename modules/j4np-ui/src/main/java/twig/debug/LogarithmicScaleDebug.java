/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class LogarithmicScaleDebug {
    public static void main(String[] args){
        TGCanvas c = new TGCanvas();
        H1F h = TDataFactory.createH1F(230000,120,0.0,1.0,0.65,0.02);
        c.view().region().getAxisFrame().getAxisY().getAttributes().setAxisTickMarkCount(5);
        c.view().region().getAxisFrame().setLogY(true);
        c.draw(h);
        //c.view().region().getAxisFrame().setLogY(true);
        c.repaint();
    }
}
