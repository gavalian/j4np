/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public abstract class TwigDemo {
    public String   getName(){return "generic";}
    public abstract void drawOnCanvas(TGDataCanvas c);
    public abstract String getCode();
    
}
