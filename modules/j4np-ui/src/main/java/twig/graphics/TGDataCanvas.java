/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import j4np.graphics.Background2D;
import j4np.graphics.Canvas2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.Page;
import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public class TGDataCanvas extends Canvas2D {
    
    private int activeRegion = 0;
    
    public TGDataCanvas(){
        Color color = TStyle.getInstance().getPalette().getColor(30004);
        Background2D back = Background2D.createBackground(color.getRed(),color.getGreen(),color.getBlue());
        setBackground(back);
        divide(1,1);
    }
    
    @Override
    public void divide(int cols, int rows){
        this.getGraphicsComponents().clear();
        for(int i = 0; i < cols*rows; i++){
            TGRegion pad = new TGRegion();
            this.addNode(pad);
        }
        this.arrange(cols, rows);
        this.repaint();
        this.activeRegion = 0;
    }
    
    public TGRegion region(){ 
        return (TGRegion) getGraphicsComponents().get(activeRegion);
    }
    
    public TGDataCanvas cd(int index){
        if(index < 0) { activeRegion = 0; return this;}
        if(index >= this.getGraphicsComponents().size()){
            activeRegion = this.getGraphicsComponents().size() - 1;
        } 
        activeRegion = index;
        return this;
    }
    
    public TGRegion region(int index){ 
        return (TGRegion) getGraphicsComponents().get(index);         
    }
    
    
    
    public void export(String filename, String type){
        if(type.compareTo("PDF")==0||type.compareTo("pdf")==0){
            PDFDocument pdfDoc = new PDFDocument();
            Page page = pdfDoc.createPage(new Rectangle(this.getSize().width, this.getSize().height));
            PDFGraphics2D g2 = page.getGraphics2D();
            this.paint(g2);
            pdfDoc.writeToFile(new File(filename));
            
        }
    }
    
}

