/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.config;

/**
 *
 * @author gavalian
 */
public class TAttributesLine {

    public TAttributesLine(){}
    
    public TAttributesLine(TAttributesLine line){
        lineStyle = line.lineStyle;
        lineWidth = line.lineWidth;
        lineColor = line.lineColor;
    }
    
    public int getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(int lineStyle) {
        this.lineStyle = lineStyle;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }
    
    private int lineStyle = 0;
    private int lineWidth = 1;
    private int lineColor = 1;
}
