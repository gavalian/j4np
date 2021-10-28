/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;



import j4np.graphics.settings.DataAttributes;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import twig.config.TDataAttributes;

/**
 *
 * @author gavalian
 */
public class GraphErrors implements DataSet {

    //public static final String[] MARKERNAME = {"Circle", "Square", "Triangle", "Inverted Triangle"};

    private final DataVector dataX = new DataVector();
    private final DataVector dataY = new DataVector();
    private final DataVector dataEX = new DataVector();
    private final DataVector dataEY = new DataVector();

    private String graphName = "graphErrors";
    private TDataAttributes graphAttr = new TDataAttributes();

    //private Func1D fitFunction = null;

    public GraphErrors() {
        initAttributes();
    }

    public GraphErrors(String name, DataVector grX, DataVector grY) {
        //setName(name);
        for (int i = 0; i < grX.getSize(); i++) {
            this.addPoint(grX.getValue(i), grY.getValue(i), 0.0, 0.0);
        }
        initAttributes();
    }

    public GraphErrors(String name, DataVector grX, DataVector grY, DataVector erX, DataVector erY) {
        //setName(name);
        for (int i = 0; i < grX.getSize(); i++) {
            double errX = 0.0;
            if (erX != null) errX = erX.getValue(i);
            this.addPoint(grX.getValue(i), grY.getValue(i), errX, erY.getValue(i));
        }
        initAttributes();
    }

    public GraphErrors(String name, DataVector grY) {
        //setName(name);
        for (int i = 0; i < grY.getSize(); i++) {
            this.addPoint((double) (i + 1), grY.getValue(i), 0.0, 0.0);
        }
        initAttributes();
    }

    public GraphErrors(String name, double[] x, double y[], double[] ex, double[] ey) {
        //setName(name);
        for (int i = 0; i < x.length; i++) {
            this.addPoint(x[i], y[i], ex[i], ey[i]);
        }
        initAttributes();
    }

    public GraphErrors(String name, double[] x, double y[], double[] ey) {
        //setName(name);
        for (int i = 0; i < x.length; i++) {
            this.addPoint(x[i], y[i], 0.0, ey[i]);
        }
        initAttributes();
    }

    public GraphErrors(String name, double[] x, double y[]) {
        //setName(name);
        for (int i = 0; i < x.length; i++) {
            this.addPoint(x[i], y[i], 0.0, 0.0);
        }
        initAttributes();
    }

    public GraphErrors(String name) {
        graphName = name;
        initAttributes();
    }

    private void initAttributes() {
        
    }

    
    public final void addPoint(double x, double y) {
        addPoint(x,y,0.0,0.0);
    }
    
    public final void addPoint(double x, double y, double ex, double ey) {
        dataX.add(x);
        dataY.add(y);
        dataEX.add(ex);
        dataEY.add(ey);
    }

    public void setPoint(int point, double x, double y) {
        dataX.set(point, x);
        dataY.set(point, y);
    }

    public void shiftX(double xshift){
        int npoints = this.dataX.getSize();
        for (int i = 0; i < npoints; i++){
            double xvalue = this.dataX.getValue(i);
            this.dataX.set(i, xvalue+xshift);
        }
    }
    
    public void setError(int point, double ex, double ey) {
        dataEX.set(point, ex);
        dataEY.set(point, ey);
    }

    public DataVector getVectorX() {
        return this.dataX;
    }

    public DataVector getVectorY() {
        return this.dataY;
    }

    public DataVector getVectorEX() {
        return this.dataEX;
    }

    public DataVector getVectorEY() {
        return this.dataEY;
    }
    
    



    

    public GraphErrors divide(double number){        
        StatNumber denom = new StatNumber(number,0.0);
        StatNumber   nom = new StatNumber(number,0.0);
        GraphErrors gr = new GraphErrors();
        
        for(int i = 0; i < this.dataY.size(); i++){
            double value = dataY.getValue(i);
            nom.set(value, dataEY.getValue(i));
            nom.divide(denom);
            //dataY.setValue(i, nom.number());
            //dataEY.setValue(i, nom.error());
            gr.addPoint(dataX.getValue(i), nom.number(), dataEX.getValue(i), nom.error());
        }
        return gr;
    }
    
    public GraphErrors  divide(GraphErrors gr){
            throw new UnsupportedOperationException("graph divide to graph not implemented yet");

       /* if(this.getDataSize(0)!=gr.getDataSize(0)){
            System.out.println("[graph:divide] error , graphs have different sizes");
            return new GraphErrors();
        }
        
        GraphErrors result = new GraphErrors();
        StatNumber nom = new StatNumber();
        StatNumber denom = new StatNumber();
        
        for(int i = 0; i < this.getDataSize(0); i++){
            
            nom.set(this.getDataY(i),this.getDataEY(i));
            denom.set(gr.getDataY(i),gr.getDataEY(i));
            nom.divide(denom);
            result.addPoint(this.getDataX(i), nom.number(), 0.0, nom.error());
        }
        return result;*/
    }
    
    
    public void statErrors(){
        int ndata = dataX.getSize();
        for(int i = 0; i < ndata; i++){
            double ye = this.dataY.getValue(i);
            this.dataEY.set(i, Math.sqrt(Math.abs(ye)));
        }
    }
    
    

    public void show(){
        System.out.println(">>> graph");
        for(int i = 0; i < dataX.getSize(); i++){
            System.out.printf("%9.5f %9.5f %9.5f %9.5f \n",
                    dataX.getValue(i),dataY.getValue(i),
                    dataEX.getValue(i),dataEY.getValue(i)
                    );
        }
    }

    @Override
    public String getName() {
        return graphName;
    }

    @Override
    public void setName(String name) {
        graphName = name;
    }

    @Override
    public int getSize(int dimention) {
        return this.dataX.getSize();
    }

    @Override
    public void getPoint(DataPoint point, int... coordinates) {
        int order = coordinates[0];
        if(order<0||order>=dataX.getSize()){
            System.out.printf("[GraphErrors] >> error: data point %d is out of range [%d-%d]\n",
                    order,0,dataX.getSize());
        } else {
            point.x = this.dataX.getValue(order);
            point.y = this.dataY.getValue(order);
            point.xerror = this.dataEX.getValue(order);
            point.yerror = this.dataEY.getValue(order);
        }
    }

    @Override
    public void getRange(DataRange range) {
        range.set(dataX.getValue(0), dataX.getValue(0),
                dataY.getValue(0), dataY.getValue(0)
        );
        for(int p = 0; p < dataX.size(); p++){
            range.grow(
                    dataX.getValue(p)-dataEX.getValue(p),
                    dataY.getValue(p)-dataEY.getValue(p)
                    );
            range.grow(
                    dataX.getValue(p)+dataEX.getValue(p),
                    dataY.getValue(p)-dataEY.getValue(p)
                    );
            range.grow(
                    dataX.getValue(p)+dataEX.getValue(p),
                    dataY.getValue(p)+dataEY.getValue(p)
                    );
            range.grow(
                    dataX.getValue(p)-dataEX.getValue(p),
                    dataY.getValue(p)+dataEY.getValue(p)
                    );
        }
    }

    @Override
    public TDataAttributes attr() {
        return this.graphAttr;
    }

    @Override
    public void reset() {
        dataX.clear(); dataY.clear();
        dataEX.clear(); dataEY.clear();
    }

    @Override
    public List<String> getStats(String options) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
