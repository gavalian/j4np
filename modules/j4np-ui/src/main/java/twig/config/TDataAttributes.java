/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.config;

/**
 *
 * @author gavalian
 */
public class TDataAttributes {

    public String getDrawOptions() {
        return drawOptions;
    }

    public void setDrawOptions(String drawOptions) {
        this.drawOptions = drawOptions;
    }

    public String getStatOptions() {
        return statOptions;
    }

    public void setStatOptions(String statOptions) {
        this.statOptions = statOptions;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleX() {
        return titleX;
    }

    public void setTitleX(String titleX) {
        this.titleX = titleX;
    }

    public String getTitleY() {
        return titleY;
    }

    public void setTitleY(String titleY) {
        this.titleY = titleY;
    }

    public String getTitleZ() {
        return titleZ;
    }

    public void setTitleZ(String titleZ) {
        this.titleZ = titleZ;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(int lineStyle) {
        this.lineStyle = lineStyle;
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

    public int getMarkerStyle() {
        return markerStyle;
    }

    public void setMarkerStyle(int markerStyle) {
        this.markerStyle = markerStyle;
    }

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
    
    public TDataAttributes(){
        
    }
    
    
    private int    lineColor = 1;
    private int    lineWidth = 1;
    private int    lineStyle = 1;
    
    private int  markerColor = 1;
    private int   markerSize = 1;
    private int  markerStyle = 1;
    
    private int    fillColor = -1;
    private int    fillStyle = -1;
    
    private String  title  = "";
    private String  titleX = "";
    private String  titleY = "";
    private String  titleZ = "";
    
    private String  drawOptions = "PE";
    private String  statOptions = "";
    
}
