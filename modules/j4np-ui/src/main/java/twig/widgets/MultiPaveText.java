/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author gavalian
 */
public class MultiPaveText implements Widget {
    
    private List<PaveTextRow>  paveRows = new ArrayList<>();
    private Font               textFont = new Font("Avenir", Font.PLAIN, 18);
    private LatexText         latexText = new LatexText("a",0,0);
    
    private double          positionX = 0;
    private double          positionY = 0;
    
    private LatexText.TextAlign       xAlignment = LatexText.TextAlign.LEFT;
    private LatexText.TextAlign       yAlignment = LatexText.TextAlign.TOP;
    private LatexText.TextRotate        rotation = LatexText.TextRotate.NONE;
    private String                      columnAlignment = "lllllll";
    
    public MultiPaveText(double __x, double __y){
        positionX = __x; positionY = __y;
    }
    
    public void addText(String[] tokens){ paveRows.add(new PaveTextRow(tokens));}
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        double xPos = tr.getX(positionX,r);
        double yPos = r.getY() + r.getHeight() - tr.relativeY(positionY, r);
        for(int i = 0; i < paveRows.size(); i++){
            paveRows.get(i).update(latexText, g2d, 10);
            
            PaveTextRow rn = paveRows.get(i);
            for(int row = 0 ; row < rn.columns.size(); row++){
                System.out.println(" drawing + " + rn.columns.get(row));
                latexText.setText(rn.columns.get(row));
                latexText.drawString(g2d, (int) (xPos + rn.rect.get(row).getX()), (int) (yPos), this.xAlignment,this.yAlignment,0);
            }
            
            yPos+=25;
        }
    }

    @Override
    public boolean isNDF() {
        return true;
    }

    @Override
    public void configure(JComponent parent) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public void setFont(Font font){
        this.textFont = font;
        this.latexText.setFont(font);
    }
    
    public static class PaveTextRow {
        
        List<String>     columns = new ArrayList<>();
        List<Rectangle2D>   rect = new ArrayList<>();
        
        public PaveTextRow(String[] tokens){ for(String t : tokens){ columns.add(t); rect.add(new Rectangle2D.Double());}}
        
        public void update(LatexText t, Graphics2D g2d, double spacing){
            double previousX = 0.0;            
            for(int i = 0; i < columns.size(); i++){
                t.setText(columns.get(i));
                Rectangle2D bounds = t.getBounds(g2d);
                rect.get(i).setRect(previousX+bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
                System.out.println(bounds);
                previousX += bounds.getWidth() + spacing;
            }
        }
        
    }
    
}
