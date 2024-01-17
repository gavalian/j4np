/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import java.util.List;
import twig.config.TDataAttributes;

/**
 *
 * @author gavalian
 */
public class Graph3D implements DataSet {
    
    private final DataVector dataX = new DataVector();
    private final DataVector dataY = new DataVector();
    private final DataVector dataZ = new DataVector();
    
    private TDataAttributes graphAttr = new TDataAttributes();
    
    private String name = "graph3d";
    
    public Graph3D(){
        
    }
    
    public Graph3D addPoint(double x, double y, double z){
        dataX.add(x);dataY.add(y);dataZ.add(z);
        return this;
    }
    
    public DataVector getVextorX(){ return dataX;}
    public DataVector getVextorY(){ return dataY;}
    public DataVector getVextorZ(){ return dataZ;}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getSize(int dimention) {
        return 3;
    }

    @Override
    public void getPoint(DataPoint point, int... coordinates) {
        
    }

    @Override
    public void getRange(DataRange range) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TDataAttributes attr() {
        return this.graphAttr;
    }

    @Override
    public List<String> getStats(String options) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void reset() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
