/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.kmeans;

import j4np.utils.io.TextFileReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class EKMeans {
    
    public static double[][] readData(String file){
        TextFileReader r = new TextFileReader(file);
        List<double[]> data = new ArrayList<>();
        while(r.readNext()==true){
            String[] tokens = r.getString().split("\\s+");
            if(tokens.length==2){
                try {
                    data.add(new double[]{Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1])});                
                } catch (NumberFormatException e){}
            }
        }
        
        double[][] result = new double[data.size()][2];
        for(int k = 0; k < data.size(); k++){
            result[k][0] = data.get(k)[0];
            result[k][1] = data.get(k)[1];
        }
        return result;
    }
    public static List<GraphErrors> getGraph(double[][] p, int[] a, int n){
        List<GraphErrors> list = new ArrayList<>();
        for(int i = 0; i < n ; i++){
            GraphErrors g = new GraphErrors();
            g.attr().setMarkerColor(2+i);
            list.add(g);
        }
        for(int i = 0; i < a.length; i++){
            list.get(a[i]).addPoint(p[i][0],p[i][1]);
        }
        return list;
    }
    
    public static void main(String[] args){
        int n = 1200; // the number of data to cluster
        int k = 9; // the number of cluster
        Random random = new Random(System.currentTimeMillis());
        
        double[][] centroids = new double[k][2];
        // lets create random centroids between 0 and 100 (in the same space as our points)
        /*double[][] points = new double[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = Math.abs(random.nextInt() % 100);
            points[i][1] = Math.abs(random.nextInt() % 100);
        }*/
        
        double[][] points = EKMeans.readData("input3.txt");
        for (int i = 0; i < k; i++) {
            centroids[i][0] = Math.abs(random.nextInt() % 100);
            centroids[i][1] = Math.abs(random.nextInt() % 100);
        }
        int iter = 3600*2;
        long then = System.currentTimeMillis();
        for(int i = 0; i < iter; i++){
            DoubleEKmeans eKmeans = new DoubleEKmeans(centroids, points, false ,DoubleEKmeans.MANHATTAN_DISTANCE_FUNCTION,null);
            
            eKmeans.run();
        }
        long now = System.currentTimeMillis();
        
        DoubleEKmeans eKmeans = new DoubleEKmeans(centroids, points, false ,DoubleEKmeans.MANHATTAN_DISTANCE_FUNCTION,null);            
        eKmeans.run();
        System.out.printf("iterations : %d, time = %d msec\n", iter, now-then);
        
        int[] assignments = eKmeans.assignments;
        // here we just print the assignement to the console.
        for (int i = 0; i < assignments.length; i++) {
            System.out.println(MessageFormat.format("point {0} is assigned to cluster {1}", i, assignments[i]));
        }
        
        
        List<GraphErrors> g = EKMeans.getGraph(points, assignments, k);
        TGCanvas c = new TGCanvas();
        c.region().draw(g,"");
    }
}
