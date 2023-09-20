/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.config;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TAxisAttributes implements Cloneable {

    public int getAxisGridLineColor() {
        return axisGridLineColor;
    }

    public void setAxisGridLineColor(int axisGridLineColor) {
        this.axisGridLineColor = axisGridLineColor;
    }

    public int getAxisGridLineStyle() {
        return axisGridLineStyle;
    }

    public void setAxisGridLineStyle(int axisGridLineStyle) {
        this.axisGridLineStyle = axisGridLineStyle;
    }

    public int getAxisGridLineWidth() {
        return axisGridLineWidth;
    }

    public void setAxisGridLineWidth(int axisGridLineWidth) {
        this.axisGridLineWidth = axisGridLineWidth;
    }

    public Boolean getAxisGridDraw() {
        return axisGridDraw;
    }

    public void setAxisGridDraw(Boolean axisGridDraw) {
        this.axisGridDraw = axisGridDraw;
    }

    
    public TAxisAttributes(){
        TStyle style = TStyle.getInstance();
        if(style!=null){
            this.axisLineColor  = TStyle.getInstance().getAxisLineColor();
            this.axisLabelColor = TStyle.getInstance().getAxisLabelColor();
            this.axisTitleColor = TStyle.getInstance().getAxisTitleColor();
            
            this.axisGridLineColor = style.getDefaultGridLineColor();
            this.axisGridLineStyle = style.getDefaultGridLineStyle();
            this.axisGridLineWidth = style.getDefaultGridLineWidth();
        }
    }
    
    
    @Override
    public TAxisAttributes clone() throws CloneNotSupportedException {
        return (TAxisAttributes) super.clone();
    }
    
    public int getAxisTickMarkCount() {
        return axisTickMarkCount;
    }

    public void setAxisTickMarkCount(int axisTickMarkCount) {
        this.axisTickMarkCount = axisTickMarkCount;
    }

    public int getAxisTicksLineWidth() {
        return axisTicksLineWidth;
    }

    public void setAxisTicksLineWidth(int axisTicksLineWidth) {
        this.axisTicksLineWidth = axisTicksLineWidth;
    }
    
    public Boolean getAxisLineDraw() {
        return axisLineDraw;
    }

    public void setAxisLineDraw(Boolean axisLineDraw) {
        this.axisLineDraw = axisLineDraw;
    }

    public int getAxisLineColor() {
        return axisLineColor;
    }

    public void setAxisLineColor(int axisLineColor) {
        this.axisLineColor = axisLineColor;
    }

    public int getAxisLabelColor() {
        return axisLabelColor;
    }

    public void setAxisLabelColor(int axisLabelColor) {
        this.axisLabelColor = axisLabelColor;
    }

    public int getAxisTitleColor() {
        return axisTitleColor;
    }

    public void setAxisTitleColor(int axisTitleColor) {
        this.axisTitleColor = axisTitleColor;
    }

    public String getAxisTitle() {
        return axisTitle;
    }

    public void setAxisTitle(String axisTitle) {
        this.axisTitle = axisTitle;
    }

    public int getAxisLineWidth() {
        return axisLineWidth;
    }

    public void setAxisLineWidth(int aixsLineWidth) {
        this.axisLineWidth = aixsLineWidth;
    }

    public AxisType getAxisType() {
        return axisType;
    }

    public void setAxisType(AxisType axisType) {
        this.axisType = axisType;
    }

    public AxisLocation getAxisLocation() {
        return axisLocation;
    }

    public void setAxisLocation(AxisLocation axisLocation) {
        this.axisLocation = axisLocation;
    }

    public List<Double> getAxisTicksPosition() {
        return axisTicksPosition;
    }

    public void setAxisTicksPosition(List<Double> axisTicksPosition) {
        this.axisTicksPosition = axisTicksPosition;
    }

    public List<String> getAxisTicksString() {
        return axisTicksString;
    }

    public void setAxisTicksString(List<String> axisTicksString) {
        this.axisTicksString = axisTicksString;
    }

    public int getAxisTickMarkSize() {
        return axisTickMarkSize;
    }
    
    public void setAxisTickMarkSize(int axisTickMarkSize) {
        this.axisTickMarkSize = axisTickMarkSize;
    }

    public int getAxisMinorTickMarkSize() {
        return axisMinorTickMarkSize;
    }

    public void setAxisMinorTickMarkSize(int axisMinorTickMarkSize) {
        this.axisMinorTickMarkSize = axisMinorTickMarkSize;
    }

    public int getAxisLabelOffset() {
        return axisLabelOffset;
    }

    public void setAxisLabelOffset(int axisLabelOffset) {
        this.axisLabelOffset = axisLabelOffset;
    }

    public int getAxisTitleOffset() {
        return axisTitleOffset;
    }

    public void setAxisTitleOffset(int axisTitleOffset) {
        this.axisTitleOffset = axisTitleOffset;
    }

    public Font getAxisLabelFont() {
        return axisLabelFont;
    }

    public void setAxisLabelFont(Font axisLabelFont) {
        this.axisLabelFont = axisLabelFont;
    }

    public Font getAxisTitleFont() {
        return axisTitleFont;
    }

    public void setAxisTitleFont(Font axisTitleFont) {
        this.axisTitleFont = axisTitleFont;
    }

    public Boolean getAxisBoxDraw() {
        return axisBoxDraw;
    }

    public void setAxisBoxDraw(Boolean axisBoxDraw) {
        this.axisBoxDraw = axisBoxDraw;
    }

    public Boolean getAxisTicksDraw() {
        return axisTicksDraw;
    }

    public void setAxisTicksDraw(Boolean axisTicksDraw) {
        this.axisTicksDraw = axisTicksDraw;
    }

    public Boolean getAxisLabelsDraw() {
        return axisLabelsDraw;
    }

    public void setAxisLabelsDraw(Boolean axisLabelsDraw) {
        this.axisLabelsDraw = axisLabelsDraw;
    }
    
    public Boolean getAxisTitlesDraw() {
        return axisTitlesDraw;
    }

    public void setAxisTitlesDraw(Boolean axisTitlesDraw) {
        this.axisTitlesDraw = axisTitlesDraw;
    }
    
    public enum AxisType {
        AXIS_X, AXIS_Y, AXIS_Z
    };
    
    public enum AxisLocation {
        AXIS_NORMAL, AXIS_REVERSED
    };
    /**
     * Axis type definitions.
     */
    private AxisType            axisType = AxisType.AXIS_X;
    private AxisLocation    axisLocation = AxisLocation.AXIS_NORMAL;
    /**
     * Axis ticks positions, if the arrays are empty the tick
     * position is generated using NiceAxis helper class.
     */
    private List<Double>   axisTicksPosition = new ArrayList<>();
    private List<String>     axisTicksString = new ArrayList<>();
    private String                 axisTitle = "";
    /**
     * Basic offsets for markers, labels and titles.
     */    
    private int      axisTickMarkSize  =  6;
    private int      axisTickMarkCount = 10;
    private int axisMinorTickMarkSize  =  0;
    private int       axisLabelOffset  =  4;
    private int       axisTitleOffset  =  0;
    private int         axisLineWidth  =  1;
    private int         axisTicksLineWidth =  1;
    
    private int         axisLineColor = 1;
    private int        axisLabelColor = 1;
    private int        axisTitleColor = 1;
    
    private int        axisGridLineColor = 41;
    private int        axisGridLineStyle = 8;
    private int        axisGridLineWidth = 1;
    /**
     * fonts for drawing axis Labels and titles.
     */
    private Font         axisLabelFont = new Font("Avenir",Font.PLAIN,14);
    private Font         axisTitleFont = new Font("Avenir",Font.PLAIN,14);
    
    /**
     * Various boolean fields for enabling and disabling 
     * drawing of some of the components.
     */
    private Boolean           axisBoxDraw = true;
    private Boolean         axisTicksDraw = true;
    private Boolean          axisLineDraw = true;
    private Boolean          axisGridDraw = false;
    
    private Boolean        axisLabelsDraw = true;
    private Boolean        axisTitlesDraw = true;
    private Boolean        axisEndPointSupress = true;

    public Boolean getAxisEndPointSupress() {
        return axisEndPointSupress;
    }

    public void setAxisEndPointSupress(Boolean axisEndPointSupress) {
        this.axisEndPointSupress = axisEndPointSupress;
    }
}
