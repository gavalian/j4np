/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import twig.config.TAttributesFill;
import twig.config.TAttributesLine;
import twig.config.TStyle;

/**
 *
 * @author gavalian
 */
public class MultiPaveText implements Widget {
    
    private List<PaveTextRow>  paveRows = new ArrayList<>();
    private Font               textFont = new Font("PT Serif", Font.PLAIN, 14);
    private LatexText         latexText = new LatexText("a",0,0);
    
    private Rectangle2D         paveBorder = new Rectangle2D.Double();    
    private PaveTextBorder    borderConfig = new PaveTextBorder();
    
    private double            positionX = 0;
    private double            positionY = 0;
    
    private double          spacingColumn = 10;
    private double          spacingRows   = 0;
    
    private int             shadowOffsetX = 5;
    private int             shadowOffsetY = 5;
    
    private LatexText.TextAlign       xAlignment = LatexText.TextAlign.LEFT;
    private LatexText.TextAlign       yAlignment = LatexText.TextAlign.TOP;
    

    private LatexText.TextRotate        rotation = LatexText.TextRotate.NONE;
    private String                      columnAlignment = "lllllll";
    List<LatexText.TextAlign>           alignments = new ArrayList<>();
    
    
    public MultiPaveText(double __x, double __y){
        positionX = __x; positionY = __y;
        this.setFont(textFont);
    }
    
    public void setFont(Font f){
        this.textFont = f;
        for(PaveTextRow r : this.paveRows) r.setFont(f);
    }
    
    public PaveTextBorder getBorder(){ return this.borderConfig;}
    
    public void setAlignments(String strAlign){
        alignments.clear();
        for(int i = 0; i < strAlign.length(); i++){
            char item = strAlign.charAt(i);
            switch(item){
                case 'l' : alignments.add(LatexText.TextAlign.LEFT); break;
                case 'c' : alignments.add(LatexText.TextAlign.CENTER); break;
                case 'r' : alignments.add(LatexText.TextAlign.RIGHT); break;
                default: alignments.add(LatexText.TextAlign.LEFT); break;
            }
        }
    }
    
    public void addText(String[] tokens){ 
        PaveTextRow row = new PaveTextRow(tokens);
        row.setFont(textFont);
        paveRows.add(row);
    }
    
    private int maxSize(){
        int max = 0;
        for(int i = 0; i < this.paveRows.size(); i++) 
            max = Math.max(max, paveRows.get(i).columns.size());
        return max;
    }
    
    private int getMaxLength(int column){
        int max = 0;
        for(int row = 0; row < this.paveRows.size();row++){
            if(paveRows.get(row).columns.size()>column) 
                max = Math.max(max, (int) paveRows.get(row).rect.get(column).getWidth());            
        }
        return max;
    }
    
    
    private List<Integer> getLengths(){
        List<Integer> list = new ArrayList<>();
        int maxColumns = this.maxSize();
        //System.out.println("number of columns = " + maxColumns);
        double  width = 0.0;
        double height = this.paveRows.get(0).rect.get(0).getHeight();
        for(int col = 0; col < maxColumns; col++){
            //System.out.printf("--> column = %5d, max length = %5d\n",col,this.getMaxLength(col));
            double maxLength = getMaxLength(col);
            list.add((int) maxLength);
            width += maxLength + this.spacingColumn;
        }
        paveBorder.setRect(0.0, 0.0, width-spacingColumn, 
                height*this.paveRows.size()+ this.spacingRows*(this.paveRows.size()-1)
        );
        
        return list;
    }
    
    
    private void setBorderRectangle(PaveTextBorder b, double xPos, double yPos){
        if(b.borderAlign==LatexText.TextAlign.BOTTOM_LEFT){
            paveBorder.setRect(xPos,
                    yPos - paveBorder.getHeight() - borderConfig.padding.getY()*2, 
                    paveBorder.getWidth() + borderConfig.padding.getX()*2, 
                    paveBorder.getHeight() + borderConfig.padding.getY()*2);
            return;
        }
        
        if(b.borderAlign==LatexText.TextAlign.BOTTOM_RIGHT){
            paveBorder.setRect(xPos - borderConfig.padding.getX()*2 - paveBorder.getWidth(),
                    yPos - paveBorder.getHeight() - borderConfig.padding.getY()*2, 
                    paveBorder.getWidth() + borderConfig.padding.getX()*2, 
                    paveBorder.getHeight() + borderConfig.padding.getY()*2);
            return;
        }
        if(b.borderAlign==LatexText.TextAlign.TOP_RIGHT){
            paveBorder.setRect(xPos - 
                    borderConfig.padding.getX()*2 - paveBorder.getWidth(),
                    yPos, 
                    paveBorder.getWidth() + borderConfig.padding.getX()*2, 
                    paveBorder.getHeight() + borderConfig.padding.getY()*2);
            return;
        }
        

        paveBorder.setRect(xPos,yPos, 
                paveBorder.getWidth() + borderConfig.padding.getX()*2, 
                paveBorder.getHeight() + borderConfig.padding.getY()*2);    
    }

    public void setPosition(double x, double y){positionX = x;positionY = y;}
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        
        TStyle  style = TStyle.getInstance();
        
        double xPos = tr.getX(positionX,r);
        double yPos = r.getY() + r.getHeight() - tr.relativeY(positionY, r);
    
        for(PaveTextRow row : this.paveRows) row.update(g2d, 10);
        
        /*int maxColumns = this.maxSize();
        System.out.println("number of columns = " + maxColumns);
        
        for(int col = 0; col < maxColumns; col++){
            System.out.printf("--> column = %5d, max length = %5d\n",col,this.getMaxLength(col));
        }*/
        
        List<Integer>  columnWidths = getLengths();
                    
        //paveBorder.setRect(xPos-borderConfig.padding.getX(), 
        //        yPos-borderConfig.padding.getY(), 
        //        paveBorder.getWidth() + borderConfig.padding.getX()*2, 
        //        paveBorder.getHeight() + borderConfig.padding.getY()*2);        
        //System.out.println(paveBorder);
        
        setBorderRectangle(this.borderConfig,xPos,yPos);
        
        if(this.borderConfig.attrFill.getFillColor()!=-1){
            if(shadowOffsetX>0&&shadowOffsetX>0){
                g2d.setColor(Color.black);
                g2d.fillRect((int) (paveBorder.getX() + shadowOffsetX), 
                        (int) (paveBorder.getY() + shadowOffsetY), 
                        (int) paveBorder.getWidth(), 
                        (int) paveBorder.getHeight());
            }
            g2d.setColor(style.getPalette().getColor(this.borderConfig.attrFill.getFillColor()));
            g2d.fill(paveBorder);
        }
        
        if(this.borderConfig.drawBorder == true){
            TAttributesLine aline = this.borderConfig.attrLine;
            g2d.setColor(style.getPalette().getColor(aline.getLineColor()));
            g2d.setStroke(style.getLineStroke(aline.getLineStyle(), aline.getLineWidth()));
            g2d.draw(paveBorder);
        }
        
        //for(Integer item : columnWidths) System.out.printf("-----> %d\n",item);
        xPos = paveBorder.getX()+ borderConfig.padding.getX();
        yPos = paveBorder.getY()+ borderConfig.padding.getY();
        
        for(int i = 0; i < paveRows.size(); i++){            
            //paveRows.get(i).update(g2d, 10);            
            PaveTextRow rn = paveRows.get(i);
            int offset   = 0;
            double rowWidth = 0;
            for(int row = 0 ; row < rn.columns.size(); row++){                
                //System.out.println(" drawing + " + rn.columns.get(row));
                if(row>0) offset += columnWidths.get(row-1) + this.spacingColumn;                
                //offset -= rn.rect.get(row).getX();                
                double xoffset = xPos + offset;
                rowWidth = Math.max(rowWidth, rn.rect.get(row).getHeight());
                if(this.alignments.size()>row){
                    double   width = columnWidths.get(row);
                    //System.out.println(" row alignment " + row + " = " + this.alignments.get(row));
                    if(this.alignments.get(row)==LatexText.TextAlign.RIGHT){
                        rn.columns.get(row).drawString(g2d,
                            (int) (xoffset+width),(int) (yPos), 
                            LatexText.TextAlign.RIGHT, yAlignment);
                        //System.out.println("\t\t ALIGNING RIGHT");
                    } else {
                        rn.columns.get(row).drawString(g2d,
                            (int) xoffset,(int) (yPos), 
                            xAlignment, yAlignment);
                    }
                } else {
                    rn.columns.get(row).drawString(g2d,
                            (int) xoffset,(int) (yPos), 
                            xAlignment, yAlignment);
                }
                    //latexText.setText(rn.columns.get(row));
                //latexText.drawString(g2d, (int) (xPos + rn.rect.get(row).getX()), (int) (yPos), this.xAlignment,this.yAlignment,0);
            }
            
            yPos += rowWidth + this.spacingRows;
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
  
    public static class PaveTextBorder {
        public boolean drawBorder = true;
        public Point2D position   = new Point2D.Double();
        public Point2D padding    = new Point2D.Double();
        public TAttributesLine attrLine = new TAttributesLine();
        public TAttributesFill attrFill = new TAttributesFill();
        public LatexText.TextAlign borderAlign = LatexText.TextAlign.TOP_LEFT; 
        
        public PaveTextBorder(){ attrFill.setFillColor(0);}
    }
  
    public static class PaveTextRow {
        
        List<LatexText>     columns = new ArrayList<>();
        List<Rectangle2D>      rect = new ArrayList<>();
        Rectangle2D         arrange = new Rectangle2D.Double();
        
        public PaveTextRow(String[] tokens){
            for(String t : tokens){ 
                columns.add(new LatexText(t)); rect.add(new Rectangle2D.Double());
            }
        }
        
        public void setFont(Font f){for(LatexText t : columns) t.setFont(f);}
        
        public void update(Graphics2D g2d, double spacing){
            double previousX = 0.0;            
            for(int i = 0; i < columns.size(); i++){
                //t.setText(columns.get(i));
                Rectangle2D bounds = columns.get(i).getBounds(g2d);
                rect.get(i).setRect(previousX+bounds.getX(), 
                        bounds.getY(), bounds.getWidth(), 
                        bounds.getHeight());
                //System.out.println(bounds);
                previousX += bounds.getWidth() + spacing;
            }
        }        
    }
    
}
