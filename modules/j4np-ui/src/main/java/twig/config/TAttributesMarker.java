/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.config;

/**
 *
 * @author gavalian
 */
public class TAttributesMarker {

    public int getMarkerStyle() {
        return markerStyle;
    }

    public void setMarkerStyle(int markerStyle) {
        this.markerStyle = markerStyle;
    }

    public int getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(int markerColor) {
        this.markerColor = markerColor;
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(int markerSize) {
        this.markerSize = markerSize;
    }

    public int getMarkerOutlineColor() {
        return markerOutlineColor;
    }

    public void setMarkerOutlineColor(int markerOutlineColor) {
        this.markerOutlineColor = markerOutlineColor;
    }

    public int getMarkerOutlineWidth() {
        return markerOutlineWidth;
    }

    public void setMarkerOutlineWidth(int markerOutlineWidth) {
        this.markerOutlineWidth = markerOutlineWidth;
    }

    public int getMarkerOutlineStyle() {
        return markerOutlineStyle;
    }

    public void setMarkerOutlineStyle(int markerOutlineStyle) {
        this.markerOutlineStyle = markerOutlineStyle;
    }
    
    private int markerStyle = 1;
    private int markerColor = 1;
    private int  markerSize = 1;
    
    private int markerOutlineColor = 0;
    private int markerOutlineWidth = 0;
    private int markerOutlineStyle = 0;
}
