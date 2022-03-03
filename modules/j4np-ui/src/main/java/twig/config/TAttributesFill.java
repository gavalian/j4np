/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.config;

/**
 *
 * @author gavalian
 */
public class TAttributesFill {

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public int getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(int fillStyle) {
        this.fillStyle = fillStyle;
    }

    public int getFillColorFade() {
        return fillColorFade;
    }

    public void setFillColorFade(int fillColorFade) {
        this.fillColorFade = fillColorFade;
    }
    
    private int fillColor = -1;
    private int fillStyle =  0;    
    private int fillColorFade = 0;
    
}
