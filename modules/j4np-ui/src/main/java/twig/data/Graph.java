/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

/**
 *
 * @author gavalian
 */
public class Graph extends GraphErrors {
    public Graph() {
        initAttributes();
    }

    public Graph(String name, DataVector grX, DataVector grY) {
        //setName(name);
        for (int i = 0; i < grX.getSize(); i++) {
            this.addPoint(grX.getValue(i), grY.getValue(i), 0.0, 0.0);
        }
        graphName = name;
        initAttributes();
    }

    public Graph(String name, DataVector grX, DataVector grY, DataVector erX, DataVector erY) {
        //setName(name);
        graphName = name;
        for (int i = 0; i < grX.getSize(); i++) {
            double errX = 0.0;
            if (erX != null) errX = erX.getValue(i);
            this.addPoint(grX.getValue(i), grY.getValue(i), errX, erY.getValue(i));
        }
        initAttributes();
    }

    public Graph(String name, DataVector grX, DataVector grY, DataVector erY) {
        //setName(name);
        graphName = name;
        for (int i = 0; i < grX.getSize(); i++) {
            double errX = 0.0;
            //if (erX != null) errX = erX.getValue(i);
            this.addPoint(grX.getValue(i), grY.getValue(i), 0.0, erY.getValue(i));
        }
        initAttributes();
    }
    
    public Graph(String name, DataVector grY) {
        //setName(name);
        for (int i = 0; i < grY.getSize(); i++) {
            this.addPoint((double) (i + 1), grY.getValue(i), 0.0, 0.0);
        }
        initAttributes();
    }

    public Graph(String name, double[] x, double y[], double[] ex, double[] ey) {
        //setName(name);
        for (int i = 0; i < x.length; i++) {
            this.addPoint(x[i], y[i], ex[i], ey[i]);
        }
        initAttributes();
    }

    public Graph(String name, double[] x, double y[], double[] ey) {
        //setName(name);
        for (int i = 0; i < x.length; i++) {
            this.addPoint(x[i], y[i], 0.0, ey[i]);
        }
        initAttributes();
    }

    public Graph(String name, double[] x, double y[]) {
        //setName(name);
        for (int i = 0; i < x.length; i++) {
            this.addPoint(x[i], y[i], 0.0, 0.0);
        }
        initAttributes();
    }

    public Graph(String name) {
        graphName = name;
        initAttributes();
    }
}
