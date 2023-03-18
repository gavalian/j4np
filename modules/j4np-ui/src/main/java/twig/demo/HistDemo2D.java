/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import twig.data.H2F;
import twig.data.TDataFactory;
import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public class HistDemo2D extends TwigDemo {

    @Override
    public String getName(){return "2D Histogram Demo";}
    
    @Override
    public void drawOnCanvas(TGDataCanvas c) {
        c.divide(3, 2);
        H2F h2_1 = TDataFactory.createH2F(360000, 60);
        c.cd(0).region().draw(h2_1);
    }

    @Override
    public String getCode() {
        return "H2D create";
    }
    
}
