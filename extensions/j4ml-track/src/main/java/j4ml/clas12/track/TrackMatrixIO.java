/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.clas12.track;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author gavalian
 */
public class TrackMatrixIO {
    
    private Color emptyColor = new Color(204,210,198);
    private Color highlightColor = new Color(58,78,81);
    private Color gridColor = new Color(230,230,230);
    private int   gridWidth = 1;
    
    private int   xPixel = 12;
    private int   yPixel = 12;
    
    public TrackMatrixIO(){
        
    }
    
    public TrackMatrixIO setGridWidth(int width){ gridWidth = width; return this;}
    
    
    public void saveImage(TrackMatrix2D m,String file){
        try {
            BufferedImage bi = this.createImage(m);
            File outputfile = new File(file);
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(TrackMatrixIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public BufferedImage createImage(TrackMatrix2D m){
        BufferedImage bi = new BufferedImage(m.xSize*xPixel, 
                m.ySize*yPixel, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        for(int x = 0; x < m.xSize; x++){
            for(int y = 0; y < m.ySize; y++){
                int cellX = x*xPixel;
                int cellY = y*yPixel;
                if(m.get(x, y)>0.5){
                    g2d.setColor(highlightColor);                    
                    g2d.fillRect(cellX, cellY, xPixel, yPixel);                    
                } else  {
                    g2d.setColor(emptyColor);                    
                    g2d.fillRect(cellX, cellY, xPixel, yPixel); 
                    //bi.setRGB(x, y, TrackMatrix2D.getRGB(0, 0, 0));
                }
            }
        }
        
        if(gridWidth>0){
            g2d.setColor(gridColor);
            g2d.setStroke(new BasicStroke(gridWidth));
            for(int x = 0; x < m.xSize; x++) {
                int xcoord = x*xPixel;
                g2d.drawLine(xcoord, 0, xcoord, m.ySize*yPixel);
            }
            
            for(int y = 0; y < m.xSize; y++) {
                int ycoord = y*yPixel;
                g2d.drawLine(0, ycoord, m.xSize*xPixel,ycoord);
            }
            
        }
        //File outputfile = new File(file);
        //    ImageIO.write(bi, "png", outputfile);
        return bi;
    }
        
}
