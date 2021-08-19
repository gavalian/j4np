/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4ml.clas12.track;

import j4np.utils.io.DataArrayUtils;

/**
 *
 * @author gavalian
 */
public class DCSector {
    
    private int[][] data = new int[36][112];
    
    public DCSector(){
        
    }
    
    public double getSuperLayerSlope(int superlayer){
        int start = superlayer*6;
        double mx = meanY(superlayer);
        double my = meanX(superlayer);
        double xmxs = 0.0;
        double xmxymy = 0.0;
        
        for(int l = 0; l < 6; l++){
            int index = start + l;
            for(int w = 0; w < 112; w++){
                if(getWire(index,w)>0){
                    xmxs +=  ( (l+1)-mx )*( (l+1)-mx );
                    xmxymy += ((l+1)-mx) * (w+1 - my);
                }
            }
        }
        if(xmxs<0.000000000000001) return 0.0;
        return xmxymy/xmxs;
    }
    
    public double meanX(int superlayer){
        int start_layer = superlayer*6;
        int  summ = 0;
        int count = 0;
        for(int l = 0; l < 6; l++){
            int index = l + start_layer;
            for(int w = 0; w < 112; w++){
                if(getWire(index,w)>0){
                    summ += (w+1);
                    count++;
                }
            }
        }
        if(count==0) return 0;
        return (double) (((double) summ)/count);
    }
    
    public double meanY(int superlayer){
        int start_layer = superlayer*6;
        int  summ = 0;
        int count = 0;
        for(int l = 0; l < 6; l++){
            int index = l + start_layer;
            for(int w = 0; w < 112; w++){
                if(getWire(index,w)>0){
                    summ += (l+1);
                    count++;
                }
            }
        }
        if(count==0) return 0;
        return (double) (((double) summ)/count);
    }
    
    public double[] getFeatures6(){
        double[] means = new double[6];
        for(int i = 0; i < 6; i++) means[i] = this.getSuperLayerFeature(i)/112.0;
        return means;
    }
    
    public double[] getFeatures12(){

        double[] means = new double[12];
        for(int i = 0; i < 6; i++) means[i] = this.getSuperLayerFeature(i)/112.0;
        for(int i = 0; i < 6; i++) means[i+6] = (this.getSuperLayerSlope(i)+2)/4.0;
        return means;
    }
    
    public double[] getFeatures36(){
        double[] features = new double[36];
        StringBuilder str = new StringBuilder();
        int next = 0;
        for(int s = 0 ; s < 6; s++){
            double meanX = meanX(s);
            double meanY = meanY(s);
            double slope = getSuperLayerSlope(s);
            double ydist  = meanY - 1;
            double xdist  = slope*ydist;
            double start  = meanX - xdist;
            //System.out.println(" distance X = " + xdist + " start = " 
            //        + start + " y mean = " + meanY + " mean x = " + meanX);

            for(int l = 0; l < 6; l++){
                double x = start + (l+0.5)*slope;
                features[next] = x/112.0; next++;
                //str.append(",");
                //str.append(String.format("%.3f", x));
                //System.out.printf("%3d %8.5f\n",l+1,x);
                //double w = 
            }
        }
        return features;
    }
    
    public double  getSuperLayerFeature(int superlayer){
        int start_layer = superlayer*6;
        int  summ = 0;
        int count = 0;
        for(int l = 0; l < 6; l++){
            int index = l + start_layer;
            for(int w = 0; w < 112; w++){
                if(getWire(index,w)>0){
                    summ += (w+1);
                    count++;
                }
            }
        }
        if(count==0) return 0;
        return (double) (((double) summ)/count);
    }
    
    public double[] getFeatures(){
        double[] means = new double[6];
        for(int i = 0; i < 6; i++) means[i] = this.getSuperLayerFeature(i);
        return means;
    }
    
    public String getFeaturesString(){
        double[] features = this.getFeatures();
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < features.length; i++)
            str.append(String.format("%d:%.4f ", i+1,features[i]));
        return str.toString();
    }
    
    public String getFeaturesStringExtended(){
        StringBuilder str = new StringBuilder();
        for(int l = 0; l < 36; l++){
            for(int w = 0; w < 112; w++){
                if(getWire(l,w)>0){
                    int index = l*112 + w + 1;
                    str.append(String.format("%d:1 ", index));                    
                }
            }
        }        
        return str.toString();
    }
    
    public DCSector setWire(int layer, int wire, int value){
        data[layer][wire] = value; return this;
    }
    
    public DCSector setWire(int superlayer, int layer, int wire, int value){
        int localLayer = superlayer*6+layer;
        data[localLayer][wire] = value; return this;
    }
    
    public int getWire(int layer, int wire){
        return data[layer][wire];
    }
    
    
    public void show(){
        for(int l = 0; l < 36; l++){
            for(int w = 0; w < 112; w++){
                if(getWire(l,w)>0){
                    System.out.print("X");
                } else {
                    System.out.print("-");
                }
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args){
        
        DCSector sector = new DCSector();
        sector.setWire(0, 30, 1);
        sector.setWire(1, 29, 1);
        sector.setWire(2, 28, 1);
        sector.setWire(3, 27, 1);
        sector.setWire(4, 26, 1);
        sector.setWire(5, 25, 1);
        
        sector.setWire(6, 50, 1);
        sector.setWire(7, 50, 1);
        sector.setWire(8, 50, 1);
        sector.setWire(9, 50, 1);
        sector.setWire(10, 50, 1);
        sector.setWire(11, 50, 1);
        System.out.println("slope = " + sector.getSuperLayerSlope(0));
        
        sector.show();
        
        System.out.println(sector.getFeatures36());
        System.out.println(DataArrayUtils.doubleToString(sector.getFeatures6(), ","));
        System.out.println(DataArrayUtils.doubleToString(sector.getFeatures12(), ","));
        System.out.println(DataArrayUtils.doubleToString(sector.getFeatures36(), ","));
        
    }
}
