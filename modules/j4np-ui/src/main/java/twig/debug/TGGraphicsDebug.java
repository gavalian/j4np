/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.debug;

import j4np.graphics.Canvas2D;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.JFrame;
import twig.config.TStyle;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGDataCanvas;
import twig.graphics.TGENode2D;
import twig.graphics.TGF1D;
import twig.graphics.TGH1F;
import twig.graphics.TGRegion;
import twig.math.DataFitter;
import twig.math.F1D;
import twig.widgets.Arc;
import twig.widgets.Line;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class TGGraphicsDebug {
    
    public static void example1(){
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 500, 500);
        TGRegion  region = new TGRegion(0,0,500,500);        
        region.getInsets().left(60).bottom(60).top(60).right(60);
        region.getAxisFrame().setLimits(0, 2.0, 0.0, 2.0);
        
        region.getAxisFrame().getAxisX().getAttributes().setAxisTitle("X^2 Axis_t");
        region.getAxisFrame().getAxisY().getAttributes().setAxisTitle("Y Axis");
        /*region.getAxisFrame().getAxisY().getAttributes().getAxisTicksPosition().add(0.0);
        region.getAxisFrame().getAxisY().getAttributes().getAxisTicksPosition().add(0.9);
        
        region.getAxisFrame().getAxisY().getAttributes().getAxisTicksString().add("0.0");
        region.getAxisFrame().getAxisY().getAttributes().getAxisTicksString().add("0.9");*/
        canvas.addNode(region);
        canvas.repaint();
    }
    
    
    public static void example2(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 500, 500);
        TGRegion  region = new TGRegion(0,0,500,500);        
        region.getInsets().left(100).bottom(60).top(60).right(60);        
        //region.getAxisFrame().setLimits(0, 2.0, 0.0, 2.0);
        
        PaveText text = new PaveText("Pave Text Example",0.5,0.5);
        text.setFont(new Font("Times",Font.BOLD,18));
        text.addLine("second line added").addLine("third line for pave");
        
        region.getAxisFrame().getAxisX().getAttributes().setAxisLabelFont(new Font("Times",Font.PLAIN,18));
        region.getAxisFrame().getAxisY().getAttributes().setAxisLabelFont(new Font("Avenir",Font.PLAIN,18));
        Line line = new Line(0.1,0.1,0.8,0.8);
        
        Arc  arc = new Arc(0.4,0.2,0.2,0.2,0,90);
        
        region.getAxisFrame().addWidget(text);
        region.getAxisFrame().addWidget(line);
        region.getAxisFrame().addWidget(arc);
        
        canvas.addNode(region);
        canvas.repaint();
    }
    
    public static void example3(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 500, 800);
        TGRegion  region = new TGRegion(0,0,500,500);        
        region.getInsets().left(75).bottom(75).top(30).right(30);        
        region.getAxisFrame().setLimits(0, 2.0, 0.0, 2.0);
        region.getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        region.getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        
        //region.getAxisFrame().getAxisX().getAttributes().setAxisLabelsDraw(Boolean.FALSE);
        int iter = 12;
        
        for(int g = 0; g < iter; g++){
            GraphErrors graph1 = TDataFactory.createGraph(g+1, g+1);
            //graph1.show();
            TGENode2D   gn1 = new TGENode2D(graph1,"F");
            region.getAxisFrame().addDataNode(gn1);
        }
                       
        canvas.addNode(region);
        canvas.repaint();
    }
    
    public static void example4(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 900, 600);
        
        canvas.divide(2, 2);
        //region.getAxisFrame().getAxisX().getAttributes().setAxisLabelsDraw(Boolean.FALSE);
        int iter = 4;
        
        for(int g = 0; g < iter; g++){
            GraphErrors graph1 = TDataFactory.createGraph(g+1, g+1);
            //graph1.show();
            canvas.region(g).getAxisFrame().addDataNode(new TGENode2D(graph1,"F"));
        }
        
        canvas.repaint();
    }
    
    public static void example5(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 900, 600);
        
        canvas.divide(3, 4);
        //region.getAxisFrame().getAxisX().getAttributes().setAxisLabelsDraw(Boolean.FALSE);
        int iter = 12;
        
        for(int g = 0; g < iter; g++){
            H1F h = TDataFactory.createH1F(2500);
            h.attr().setLineColor(g+2);
            h.attr().setFillColor(g+62);
            //graph1.show();
            canvas.region(g).getAxisFrame().addDataNode(new TGH1F(h,"F"));
        }
        
        canvas.repaint();
    }
    
    public static void example6(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 900, 600);
        

        //region.getAxisFrame().setLimits(0, 2.0, 0.0, 2.0);
        
        F1D func = new F1D("f1","[amp]*gaus(x,[mean],[error])",0.0,1.0);
        func.setParameters(new double[]{100,0.4,0.2});
        func.attr().setLineWidth(2);
        func.attr().setLineColor(3);
        canvas.region(0).getAxisFrame().addDataNode(new TGF1D(func));
        
        
        PaveText text = new PaveText("Pave Text Example",0.6,100.0);
        text.setDrawBox(false);
        text.setFont(new Font("Avenir",Font.BOLD,18));
        text.addLine("second line added").addLine("third line for pave");
        canvas.region(0).getAxisFrame().addWidget(text);

        
        //canvas.addNode(region);
        canvas.repaint();
    }
    
    public static void example7(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 900, 600);
        

        //region.getAxisFrame().setLimits(0, 2.0, 0.0, 2.0);
        GraphErrors graph = TDataFactory.createGraph(2, 3);
        
        F1D func = new F1D("f1","[base]*exp(x/[par])",0.8,5.2);        
        func.setParameters(new double[]{1,-0.4});
        func.attr().setLineWidth(2);
        func.attr().setLineColor(3);        

        canvas.region(0).getAxisFrame().addDataNode(new TGENode2D(graph));
        canvas.region(0).getAxisFrame().addDataNode(new TGF1D(func));
        DataFitter.fit(func, graph, "N");
        func.show();
        //canvas.addNode(region);
        canvas.repaint();
    }
    
    
    public static void example8(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 600, 600);
       
        //region.getAxisFrame().setLimits(0, 2.0, 0.0, 2.0);
        //GraphErrors graph = TDataFactory.createGraph(2, 3);
        H1F h = TDataFactory.createH1F(25000);
        H1F h2 = TDataFactory.createH1F(25000,100,0.0,1.0,0.7,0.1);
        F1D func = new F1D("f1","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0.0,1.0);
        func.setParameters(new double[]{10,10,100,0.4,0.2});
        DataFitter.fit(func, h, "N");
        h.attr().setFillColor(123);
        h.attr().setLineColor(3);
        h.attr().setLineWidth(2);
        
        h2.attr().setFillColor(52);
        h2.attr().setLineColor(2);
        h2.attr().setLineWidth(2);
        
        func.attr().setLineWidth(3);

        func.attr().setLineColor(12);
        
        canvas.region(0).getAxisFrame().getAxisX().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        canvas.region(0).getAxisFrame().getAxisY().getAttributes().setAxisBoxDraw(Boolean.TRUE);
        
        
        canvas.region(0).getAxisFrame().addDataNode(new TGH1F(h2));
        canvas.region(0).getAxisFrame().addDataNode(new TGH1F(h));

        canvas.region(0).getAxisFrame().addDataNode(new TGF1D(func));
        List<String> stats = func.getStats("FM");
        PaveText text = new PaveText(stats.get(0)
                .replace("mean", "#mu").replace("sigma", "#sigma")
                ,0.01,0.9);
        for(int j = 1; j < stats.size(); j++) text.addLine(
                stats.get(j).replace("mean", "#mu").replace("sigma", "#sigma")
        );
        text.setNDF(true);
        text.setTextColor(TStyle.getInstance().getPalette().getColor(3));
        text.setFont(new Font("Avenir",Font.PLAIN,12));
        System.out.println(func.getStats("").get(0));
        func.show();
        //text.setTextColor(Color.red);
        canvas.region(0).getAxisFrame().addWidget(text);
        //canvas.addNode(region);
        canvas.repaint();
    }
    
    
      public static void example9(){
        
        TGDataCanvas canvas = new TGDataCanvas();
        JFrame frame = Canvas2D.getFrame(canvas, 500, 800);
       
        canvas.divide(1, 2);
        //region.getAxisFrame().setLimits(0, 2.0, 0.0, 2.0);
        GraphErrors graph = TDataFactory.createGraph(2, 3);
        GraphErrors graph2 = TDataFactory.createGraph(3, 3);
        graph.attr().setMarkerSize(16);
        graph.attr().setLineWidth(2);
        graph.attr().setTitleX("Beam Current [nA]");
        graph.attr().setTitleY("Efficiency");
        
        graph2.attr().setMarkerSize(16);
        graph2.attr().setLineWidth(2);
        
        canvas.region(0).getAxisFrame().addDataNode(new TGENode2D(graph));
        canvas.region(0).getAxisFrame().getAxisX().getAttributes().setAxisTitleFont(
                new Font("Avenir",Font.PLAIN,18));
        canvas.region(0).getAxisFrame().getAxisY().getAttributes().setAxisTitleFont(
                new Font("Avenir",Font.PLAIN,18));
        canvas.region(1).getAxisFrame().addDataNode(new TGENode2D(graph2));
        //canvas.region(1).getAxisFrame().setFixedLimits(0.5,5.5, 0.0, 16.0);
        canvas.region(1).getAxisFrame().getAxisY().setFixedLimits(0.0, 16.0);
        
        //canvas.addNode(region);
        canvas.repaint();
    }

    public static void main(String[] args){
        TGGraphicsDebug.example9();
    }
}
