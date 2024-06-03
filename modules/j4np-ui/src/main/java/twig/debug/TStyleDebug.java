/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import twig.config.TStyle;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class TStyleDebug {
    public static void main(String[] args){
        TStyle.setStyle(TStyle.TwigStyle.PRESENTATION);
        TGCanvas c = new TGCanvas("c",550,500);
        H1F h = TDataFactory.createH1F(1200);
        c.draw(h);
    }
}
