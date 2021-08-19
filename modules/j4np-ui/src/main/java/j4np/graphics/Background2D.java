/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author gavalian
 */
public class Background2D {
    
    public static int BACKGROUND_POPULATION = 80;
    public Color      BACKGROUND_COLOR      = new Color(255,255,255);
    
    public static List<Color>  getColorPalette(int index){
        List<Color>  colorPalette = new ArrayList<Color>();
        //colorPalette.add(new Color(220,125,38));
        colorPalette.add(new Color(242,157,51));
        
        return colorPalette;
    }
    public void setColor(Color c){
        this.BACKGROUND_COLOR = c;
    }
    
    public static Background2D createBackground(int red, int green, int blue){
        Background2D bkg = new Background2D();
        bkg.setColor(new Color(red,green,blue));
        return bkg;
    }
    
    public void drawBackground(Graphics2D g2d, int x , int y, int w, int h){
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(x, y, w, h);
    }
    
    public static BufferedImage createBackground(int w, int h, int squareSize, Color back, List<Color> squares){
        
        BufferedImage image = 
                new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(back);
        
        g2d.fillRect(0, 0, w, h);
        int nboxesX = (int) (w/squareSize);
        int nboxesY = (int) (h/squareSize);
        int nsquares = (int) ( (nboxesX*nboxesY)*(BACKGROUND_POPULATION/100.0) );
        for(int i = 0; i < nsquares; i++){
            int x =  (int) (Math.random()*(nboxesX+1));
            int y =  (int) (Math.random()*(nboxesY+1));
            int color = ((int) (Math.random()*Integer.MAX_VALUE)%squares.size());
            
            //System.out.println(" x = " + x + " y = " + y);
            g2d.setColor(squares.get(color));
            g2d.fillRect(x*squareSize + 1 , y*squareSize + 1, squareSize - 2, squareSize - 2);
        }
        return image;    
    }
    
    public static void setRenderingQuality(Graphics2D g2d){
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}
