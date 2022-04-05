/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

import com.indvd00m.ascii.render.Region;
import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IElement;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.plot.AxisLabels;
import com.indvd00m.ascii.render.elements.plot.Axis;
import com.indvd00m.ascii.render.elements.plot.Plot;
import com.indvd00m.ascii.render.elements.plot.api.IPlotPoint;
import com.indvd00m.ascii.render.elements.plot.misc.PlotPoint;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class AsciiPlot {
    static int plotSizeX = 75;
    static int plotSizeY = 25;
    
    public static void setSize(int width, int height){ 
        plotSizeX = width; plotSizeY = height;
    }
    
    public static void drawh1(H1F h, int sizeX, int sizeY){
        List<IPlotPoint> points = new ArrayList<IPlotPoint>();
        for(int i = 0 ; i < h.getxAxis().getNBins(); i++){
            points.add(new PlotPoint(h.getxAxis().getBinCenter(i), h.getBinContent(i)));
        }
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(sizeX).height(sizeY);
        //builder.element( new Rectangle(0, 0, sizeX, sizeY));
        builder.layer(new Region(1, 1, sizeX-2, sizeY-2));
        builder.element(new Axis(points, new Region(0, 0, sizeX-2, sizeY-2)));
        builder.element(new AxisLabels(points, new Region(0, 0, sizeX-2, sizeY-2)));
        builder.element(new Plot(points, new Region(0, 0, sizeX-2, sizeY-2)));
        ICanvas canvas = render.render(builder.build());
        String s = canvas.getText();
        System.out.println(s);
    }
    
    public static void draw(DataSet data){
       AsciiPlot.draw(data, plotSizeX, plotSizeY);
    }
    
    public static void draw(DataSet data, int sizeX, int sizeY){
        List<IPlotPoint> points = null;
        String dataDesc = "";
        
        if(data instanceof H1F){
            points = AsciiPlot.h1f2points((H1F) data);
            H1F h = (H1F) data;
            dataDesc = String.format("H1F : mean = %.5f, rms = %.5f", 
                    h.getMean(), h.getRMS());
        }
        
        if(data instanceof GraphErrors) points = AsciiPlot.graph2points((GraphErrors) data);
        if(points!=null){
            IRender render = new Render();
            IContextBuilder builder = render.newBuilder();
            builder.width(sizeX).height(sizeY);
            //builder.element( new Rectangle(0, 0, sizeX, sizeY));
            builder.layer(new Region(1, 1, sizeX-2, sizeY-2));
            builder.element(new Axis(points, new Region(0, 0, sizeX-2, sizeY-2)));
            builder.element(new AxisLabels(points, new Region(0, 0, sizeX-2, sizeY-2)));
            builder.element(new Plot(points, new Region(0, 0, sizeX-2, sizeY-2)));
            ICanvas canvas = render.render(builder.build());
            String s = canvas.getText();
            System.out.println("\n\n ::: %s " + dataDesc + "\n");
            System.out.println(s);
        } else {
            System.out.println("[ASCII] ::: plot not supported for class : " + data.getClass().getName());
        }
    }
    
    public static  List<IPlotPoint> h1f2points(H1F h){
        List<IPlotPoint> points = new ArrayList<IPlotPoint>();
        for(int i = 0 ; i < h.getxAxis().getNBins(); i++){
            points.add(new PlotPoint(h.getxAxis().getBinCenter(i), h.getBinContent(i)));
        }
        return points;
    }
    public static List<IPlotPoint> graph2points(GraphErrors gr){
        List<IPlotPoint> points = new ArrayList<IPlotPoint>();
        for(int i = 0 ; i < gr.getVectorX().getSize(); i++){
            points.add(new PlotPoint(gr.getVectorX().getValue(i),
                    gr.getVectorY().getValue(i)
            ));
        }
        return points;
    }
    
    public static void main(String[] args){
        H1F h = TDataFactory.createH1F(25000, 80, 0.0, 1.0, 0.5, 0.05);
        AsciiPlot.draw(h,120,25);
        
        GraphErrors gr = h.getGraph();
        
        AsciiPlot.draw(gr);
        
    }
}
