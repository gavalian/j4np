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

    public TAttributesFill setFillColor(int fillColor) {
        this.fillColor = fillColor; return this;
    }

    public int getFillStyle() {
        return fillStyle;
    }

    public TAttributesFill setFillStyle(int fillStyle) {
        this.fillStyle = fillStyle; return this;
    }

    public int getFillColorFade() {
        return fillColorFade;
    }

    public TAttributesFill setFillColorFade(int fillColorFade) {
        this.fillColorFade = fillColorFade; return this;
    }
    
    private int fillColor =  0;
    private int fillStyle =  0;    
    private int fillColorFade = 0;
    
}
