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
import java.util.Random;

/**
 *
 * @author gavalian
 */
public class AsciiPlot {
    static int plotSizeX = 75;
    static int plotSizeY = 25;
    public static String[] H2D_SPRITES = new String[]{" ","\u2591","\u2592","\u2591","\u2588"};
    public static String[] H1D_SPRITES = new String[]{
        " ","\u2581","\u2582","\u2583","\u2584",
        "\u2585","\u2586","\u2587","\u2588"
    };
    
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
    public static void printTopLine(int count){
        System.out.print("\u250C");
        for(int i = 0; i < count-2;i++) System.out.print("\u2500");
        System.out.println("\u2510");
    }
    public static void printBottomLine(int count){
        System.out.print("\u2514");
        for(int i = 0; i < count-2;i++) System.out.print("\u2500");
        System.out.println("\u2518");
    }
    public static void drawh1box(H1F h){
        twig.data.Axis axis = new twig.data.Axis(AsciiPlot.H1D_SPRITES.length,0,h.getMax());
        System.out.print("\u2502");
        for(int x = 0; x < h.getAxisX().getNBins(); x++){
            int bin = axis.getBin(h.getBinContent(x));
            if(bin<0) bin = 0; if(bin>axis.getNBins()-1) bin = axis.getNBins()-1;
            System.out.print(AsciiPlot.H1D_SPRITES[bin]);
        }
        System.out.println();
        AsciiPlot.printBottomLine(h.getAxisX().getNBins()+2);
    }
    public static void drawh2(H2F h2){
        double max = h2.getMaximum();

        twig.data.Axis axis = new twig.data.Axis(5,0,max);
        AsciiPlot.printTopLine(h2.getAxisX().getNBins()+2);
        for(int y = 0 ; y < h2.getAxisY().getNBins(); y++){
            System.out.print("\u2502");
            for(int x = 0; x < h2.getAxisX().getNBins(); x++){
                double bc = h2.getBinContent(x, y);
                int bin = axis.getBin(bc);
                if(bin<0) bin = 0;
                if(bin>axis.getNBins()-1) bin = axis.getNBins()-1;
                System.out.print(AsciiPlot.H2D_SPRITES[bin]);
            }
            System.out.println("\u2502");
        }
        AsciiPlot.printBottomLine(h2.getAxisX().getNBins()+2);
    }
    
    public static void main(String[] args){
        H1F h = TDataFactory.createH1F(25000, 80, 0.0, 1.0, 0.5, 0.05);
        AsciiPlot.draw(h,120,25);
        
        GraphErrors gr = h.getGraph();
        
        AsciiPlot.draw(gr);
        
        H2F h2 = new H2F("h2",112,0.5,112.5,6,0.5,6.5);//TDataFactory.createH2F(1200, 50);
        Random r = new Random();
        for(int i = 0; i < 800; i++) h2.fill(r.nextDouble()*112, r.nextDouble()*6);
        AsciiPlot.drawh2(h2);
        
        //H2F h2c = TDataFactory.createH2F(120000, 50);
        //AsciiPlot.drawh2(h2c);
        H1F h1 = TDataFactory.createH1F(12000, 112, 0.0, 1.0, 0.6, 0.08);
        AsciiPlot.drawh1box(h1);
        
        H1F h1c = h2.projectionX();
        AsciiPlot.drawh1box(h1c);
    }
}
