/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import twig.config.TwigFontLoader;
import twig.graphics.GraphicsAxis;
import twig.widgets.LatexText;
import twig.widgets.LatexText.TextAlign;

/**
 * 
 * @author gavalian
 */
public class GraphicsAxisDebug extends JPanel {
    
    public GraphicsAxisDebug(){
        super();
        TwigFontLoader.getInstance().register();
    }
    
    
    public void drawAxisFonts(Graphics2D g2d){
        int w = this.getSize().width;
        int h = this.getSize().height;
        LatexText lt = new LatexText("text");
        List<String> fonts = TwigFontLoader.getInstance().getFontList();
        for(int k = 0; k < fonts.size(); k++){
            Font f = TwigFontLoader.getInstance().getFontFromFile(Integer.parseInt(fonts.get(k)),Font.BOLD,18);
            GraphicsAxis ga = new GraphicsAxis();
            ga.setFont(f);
            //System.out.println(f);
            ga.drawAxis(g2d, 80, 80+k*50, w-80, 80+k*50, 0.0 , 1.0, 
                Arrays.asList(0.1,0.25,0.5,0.6,0.8,0.9), 
                Arrays.asList("0.1","0.25","0.5","0.6","0.8","0.9"),
                Arrays.asList(0.125,0.3,0.55,0.7,0.85,0.95)
            );
            lt.setText(fonts.get(k));
            lt.setFont(f);
            lt.drawString(g2d, 80, 80+k*50, TextAlign.LEFT, TextAlign.BOTTOM);
        }
    }
    
    public void drawText(Graphics2D g2d){
        
        LatexText latex = new LatexText("10^3^1");
        int ff = 2;
        Font f1 = TwigFontLoader.getInstance().getFont(ff,18);
        g2d.setFont(f1);
        g2d.drawString("DejaVu Fonts 1234567890", 200, 100);
        latex.setFont(f1);
        latex.drawString(g2d, 200, 140, TextAlign.LEFT, TextAlign.CENTER);
        
        Font f2 = TwigFontLoader.getInstance().getFont(100+ff,18);
        g2d.setFont(f2);
        System.out.println(" f2 = " + f2.getStyle());
        g2d.drawString("DejaVu Fonts 1234567890", 200, 200);
        latex.setFont(f2);
        latex.drawString(g2d, 200, 240, TextAlign.LEFT, TextAlign.CENTER);
        
        Font f3 = TwigFontLoader.getInstance().getFont(200+ff,18);
        g2d.setFont(f3);
        System.out.println(" f3 = " + f3.getStyle());
        g2d.drawString("DejaVu Fonts 1234567890", 200, 300);
        latex.setFont(f3);
        latex.drawString(g2d, 200, 340, TextAlign.LEFT, TextAlign.CENTER);
        
        Font f4 = TwigFontLoader.getInstance().getFont(300+ff,18);
        g2d.setFont(f4);
        g2d.drawString("DejaVu Fonts 1234567890", 200, 400);
        latex.setFont(f1);
        latex.drawString(g2d, 200, 440, TextAlign.LEFT, TextAlign.CENTER);
    }
    
    
    @Override
    public void paint(Graphics g){
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //Background2D.setRenderingQuality(g2d);
        
        int w = this.getSize().width;
        int h = this.getSize().height;
                
        g2d.drawRect(20, 20, w-40, h-40);
        
        //this.drawAxisFonts(g2d);
        this.drawText(g2d);
        
        /*
        Font f1 = TwigFontLoader.getInstance().getFont(101, 28);
        Font f2 = TwigFontLoader.getInstance().getFont(101, 18);
        
        GraphicsAxis axisX = new GraphicsAxis(" M^x(#pi^+,#pi^-) [GeV]");
        GraphicsAxis axisY = new GraphicsAxis(" M^x(#pi^+,#pi^-) [GeV]");
        System.out.println(f1);
        axisX.setFont(f1); axisY.setFont(f2);
        //axisX.setGridLineHeight(220);
        axisX.setProperty("axis.ticks.size", "12");
        
        int size = 2;
        
        double y1 = 300;
        double y2 = 380;
        double x1 = 40;
        double x2 = 600;
        
        g2d.fillOval( (int) (x1-size), (int) (y1-size), 
                2*size, 2*size);
        g2d.fillOval( (int) (x2-size), (int) (y2-size), 
                2*size, 2*size);
        
        int iter = 1;
        long then = System.currentTimeMillis();
        //for(int i = 0; i < iter; i++)
        axisX.drawAxis(g2d, 80, h-80, w-80, h-80, 0.0 , 1.0, 
                Arrays.asList(0.1,0.25,0.5,0.6,0.8,0.9), 
                Arrays.asList("0.1","0.25","0.5","0.6","0.8","0.9"),
                Arrays.asList(0.125,0.3,0.55,0.7,0.85,0.95)
        );
        
        long now = System.currentTimeMillis();
        axisY.setType(GraphicsAxis.AxisType.VERTICAL);
        axisY.drawAxis(g2d, 80, 80, 80, h-80, 0.0 , 1.0, 
                    Arrays.asList(0.1,0.25,0.5,0.6,0.8,0.9), 
                    Arrays.asList("0.1","0.25","0.5","0.6","0.8","0.9"),
                    Arrays.asList(0.125,0.3,0.55,0.7,0.85,0.95)
            );
        
        
        
        
        //if(f==null) System.out.println("---- font is NULL");
        g2d.setFont(f1);
        g2d.setColor(Color.ORANGE);
        
        g2d.drawString("DeJaVu Font 1234567890", 400, 400);*/
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame( "Attributes" );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicsAxisDebug gax = new GraphicsAxisDebug();
        frame.add(gax);
        frame.pack();
        frame.setSize( 800, 600 );
        frame.setVisible(true);
                
        System.out.println("  BOLD = " + Font.PLAIN + "  " + Font.BOLD + "  " 
                + " " + Font.ITALIC + "  " +  (Font.BOLD|Font.ITALIC));
        
    }
}
