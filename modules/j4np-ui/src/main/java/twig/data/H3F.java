/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.config.TDataAttributes;
import twig.math.MultiIndex;


/**
 * @author Gagik Gavalian
 * 
 */
public class H3F implements DataSet {

    private final Axis xAxis = new Axis();
    private final Axis yAxis = new Axis();
    private final Axis zAxis = new Axis();
    
    private MultiIndex offset = new MultiIndex();
    protected double[] hBuffer;
    
    private String hName = "h3f";
    protected long uniqueID = 0L;
    protected int nEntries = 0;
    
    
    protected TDataAttributes dataAttr = new TDataAttributes();
    
    public H3F(int xbins, double xmin, double xmax, int ybins, double ymin, double ymax,
            int zbins, double zmin, double zmax){
        this.initData(xbins, xmin, xmax, ybins, ymin, ymax, zbins, zmin, zmax);
    }
    
    
    public H3F(double[] xLimits, double[] yLimits, double[] zLimits){
        initData(xLimits,yLimits,zLimits);
    }
    
    
    
    @Override
    public String getName(){ return this.hName;}
    public long getUniqueID(){return this.uniqueID;}
    public int  getEntries(){ return this.nEntries;}
    
     private void initData(double[] xLimits, double[] yLimits, double[] zLimits){
        xAxis.set(xLimits);
        yAxis.set(yLimits);
        zAxis.set(zLimits);
        offset.setDimensions(xAxis.getNBins(),yAxis.getNBins(),zAxis.getNBins());
        hBuffer = new double[offset.getArraySize()];
    }
     
    private void initData(int xbins, double xmin, double xmax, int ybins, double ymin, double ymax,
            int zbins, double zmin, double zmax){
        xAxis.set(xbins, xmin, xmax);
        yAxis.set(ybins, ymin, ymax);
        zAxis.set(zbins, zmin, zmax);
        offset.setDimensions(xbins,ybins,zbins);
        hBuffer = new double[offset.getArraySize()];
    }
    
    //public Axis getAxisX(){return this.xAxis;}
    //public Axis getAxisY(){return this.yAxis;}
    //public Axis getAxisZ(){return this.zAxis;}

    private boolean isValidBins(int bx, int by, int bz){
        if ((bx >= 0) && (bx <= xAxis.getNBins()) && (by >= 0)
                && (by <= yAxis.getNBins()) && (bz>=0) && 
                (bz<zAxis.getNBins())) {
            return true;
        }
        return false;
    }
    
    public void reset(){
        for(int i = 0; i < hBuffer.length; i++){
            hBuffer[i] = (float) 0.0;
        }
    }
    public int findBin(double x, double y, double z){
        int bx = xAxis.getBin(x);
        int by = yAxis.getBin(y);
        int bz = zAxis.getBin(z);
        if (isValidBins(bx, by, bz)) {
            return (offset.getArrayIndex(bx, by, bz));
        }
        return -1;
    }
    
    public void fill(double x, double y, double z) {
        int bin = this.findBin(x, y, z);
        if (bin >= 0) {
            this.addBinContent(bin);
        }
    }
    
    public void fill(double x, double y, double z, double w) {
        int bin = this.findBin(x, y, z);
        if (bin >= 0) {
            this.addBinContent(bin, w);
        }
    }
    
    private void addBinContent(int bin, double w) {
        hBuffer[bin] = (float) (hBuffer[bin] + w);
    }
    
    private void addBinContent(int bin) {
        addBinContent(bin,1.0);
    }
    
    public Axis getAxisX(){ return xAxis;}
    public Axis getAxisY(){ return yAxis;}
    public Axis getAxisZ(){ return zAxis;}
    
    public void setBinContent(int bx, int by, int bz, double value){
        if (isValidBins(bx, by, bz)) {
            int buff = offset.getArrayIndex(bx, by, bz);
            hBuffer[buff] = (float) value;
        }
    }
    
    public double getBinContent(int bx, int by, int bz){
        if (isValidBins(bx, by, bz)) {
            int buff = offset.getArrayIndex(bx, by, bz);
            return hBuffer[buff];
        }
        return 0.0;
    }
    
    
    public int   getDataBufferSize(){
        return this.hBuffer.length;
    }
    
    public double getDataBufferBin(int bin){
        return hBuffer[bin]; 
    }
    
    
    public void export(String filename){
        
        TextFileWriter writer = new TextFileWriter();
        writer.open(filename);
        writer.writeString("NRRD0001");
        writer.writeString("type: float");
        writer.writeString("dimension: 3");
        writer.writeString(String.format("sizes: %d %d %d", 
                xAxis.getNBins(),yAxis.getNBins(),zAxis.getNBins()
                ));
        writer.writeString("encoding: ascii\n");
        for(int x = 0; x < xAxis.getNBins(); x++){
            for(int y = 0; y < yAxis.getNBins(); y++){
                for(int z = 0; z < zAxis.getNBins(); z++){
                    writer.writeString(String.format("%.6f", getBinContent(x,y,z)));
                }
            }
        }
        writer.close();
    }
    
    public static H1F getH1F(H3F h3){
        H1F h1 = new H1F("H3_to_H1",h3.getDataBufferSize(),0.0,h3.getDataBufferSize());
        for(int i = 0; i < h3.getDataBufferSize(); i++){
            h1.setBinContent(i, h3.getDataBufferBin(i));
        }
        return h1;
    }
    
    
    
    public H2F sliceY(int yBin){
        
        return null;
    }
    
    public H2F sliceZ(int zBin){
        
        H2F h2 = new H2F("SLICE of " + zBin + " Z bin",
                xAxis.getNBins(),xAxis.min(), xAxis.max(),
                yAxis.getNBins(),yAxis.min(), yAxis.max()
        );
        double value = 0.0;
        for(int xb = 0; xb < xAxis.getNBins(); xb++){
            for(int yb = 0; yb < yAxis.getNBins(); yb++){
                value = getBinContent(xb,yb,zBin);
                h2.setBinContent(xb, yb, value);
            }
        }
        return h2;
    }
    
    public List<H2F> getSlicesZ(){
        List<H2F> slicesZ = new ArrayList<H2F>();
        for(int zb = 0; zb < zAxis.getNBins(); zb++){
            H2F h2 = sliceZ(zb);
            slicesZ.add(h2);
        }
        return slicesZ;
    }

    @Override
    public void setName(String name) {
        hName = name;
    }

    @Override
    public int getSize(int dimention) {
        switch(dimention){
            case 0: return this.xAxis.getNBins();
            case 1: return this.yAxis.getNBins();
            case 2: return this.zAxis.getNBins();
            default: break;
        }
        return 0;
    }

    @Override
    public void getPoint(DataPoint point, int... coordinates) {
        point.x = this.xAxis.getBinCenter(coordinates[0]);
        point.xerror = this.xAxis.getBinWidth(coordinates[0]);
        
        point.y = this.yAxis.getBinCenter(coordinates[1]);
        point.yerror = this.yAxis.getBinWidth(coordinates[1]);
        
        
        point.z = this.zAxis.getBinCenter(coordinates[2]);
        point.zerror = this.zAxis.getBinWidth(coordinates[2]); 
    }

    @Override
    public void getRange(DataRange range) {
        range.set(
                this.xAxis.min(),  this.xAxis.max(),
                this.yAxis.min(),  this.yAxis.max()
        );
        range.z    = this.xAxis.min();
        range.dept = this.xAxis.max()-this.xAxis.min();        
    }

    @Override
    public TDataAttributes attr() {
       return  dataAttr;
    }

    @Override
    public List<String> getStats(String options) {
        return Arrays.asList("x","y","z");
    }
    
    
}
