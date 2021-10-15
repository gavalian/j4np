/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author gavalian
 */
public class PaveText implements Widget { 

     public enum PaveTextStyle {
        MULTILINE, ONELINE;
    }
     
    protected Font             textFont = new Font("Avenir", Font.PLAIN, 14);
    private Color           textColor = Color.BLACK;
    private Color         borderColor = new Color(150,150,150);
    private Color    headerBackground = new Color(250,255,250);
    private String         textHeader = "Info";
    private List<String>  textStrings = new ArrayList<>();
    private List<Point2D.Double>  textPositions = new ArrayList<>();
    
    private LatexText       latexText = new LatexText("a",0,0);
    private double        textSpacing = 0.0;
    
    private double          positionX = 0;
    private double          positionY = 0;
    
    private int           paddingLeft = 10;
    private int          paddingRight = 10;
    private int            paddingTop = 3;
    private int         paddingBottom = 6;
    
    private int           roundRadius = 5;
    public  PaveTextStyle   paveStyle = PaveTextStyle.MULTILINE;
    
    public  boolean  paveNDF = true;
    
    public boolean            drawBox = true;
    public boolean            fillBox = true;
    
    public PaveText(String text, double x, double y){
        //super(x,y);
        //setBackgroundColor(240,240,240);
        this.positionX = x;
        this.positionY = y;
        //setName("pave_text");
        textStrings.add(text);
        textPositions.add(new Point2D.Double(0.0,0.0));
    }
    
    public PaveText(List<String> texts, double x, double y){
        //super(x,y);
        //setBackgroundColor(240,240,240);
        this.positionX = x;
        this.positionY = y;
        //setName("pave_text");
        for(String text : texts)
            this.addLine(text);
        //textPositions.add(new Point2D.Double(0.0,0.0));
    }
    
    public PaveText(double x, double y){
        //super(x,y);
        //setBackgroundColor(240,240,240);
        this.positionX = x;
        this.positionY = y;
        //setName("pave_text");
        latexText.setFont(textFont);
    }
    
    public PaveText(double x, double y, boolean boxDraw){
        //super(x,y);
        //setBackgroundColor(240,240,240);
        this.positionX = x;
        this.positionY = y;
        //setName("pave_text");
        latexText.setFont(textFont);
        this.drawBox = boxDraw;
    }
    
    public PaveText setStyle(PaveTextStyle style){
        paveStyle = style; return this;
    }
    
    public PaveTextStyle getStyle(){
        return paveStyle;
    }
    
    public void setTextColor(Color col){
        this.textColor = col;
    }
    
    public final PaveText addLine(String line){
        textStrings.add(line); 
        textPositions.add(new Point2D.Double(0.0,0.0));
        return this;
    }
    
    public PaveText setDrawBox(boolean flag){
        this.drawBox = flag; return this;
    }
    
    public PaveText setFillBox(boolean flag){
        this.fillBox = flag; return this;
    }
    
    public PaveText addLines(String[] lines){
        for(String line : lines)
            this.addLine(line);
        return this;
    }
    
    public void setNDF(boolean flag){ paveNDF = flag;}
    
    public int left(){ return paddingLeft;}
    public int right(){ return paddingRight;}
    public int top(){ return paddingTop;}
    public int buttom(){ return paddingBottom;}
    
    public final PaveText left(int p){
        paddingLeft = p; return this;
    }
    public final PaveText right(int p){
        paddingRight = p; return this;
    }
    public final PaveText top(int p){
        paddingTop = p; return this;
    }
    
    public final PaveText bottom(int p){
        paddingBottom = p; return this;
    }
    
    public PaveText setSpacing(double spacing){
        textSpacing = spacing; return this;
    }
    
    public void setFont(Font font){
        this.textFont = font;
        this.latexText.setFont(font);
    }
    
    public PaveText setBackgroundColor(Color color){
        headerBackground = color; return this;
    }
    
    public PaveText setBorderColor(Color color){
        borderColor = color; return this;
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        if(paveStyle == PaveTextStyle.MULTILINE) drawLayerMultiLine(g2d,r,tr);
       if(paveStyle == PaveTextStyle.ONELINE) 
           this.drawLayerOneLine(g2d, r, tr);
    }
    
    protected void drawLayerMultiLine(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        //NodeRegion2D bounds = getParent().getBounds();
        //System.out.println("[Pave Text] ---> " + bounds);
        
        FontMetrics metrics = g2d.getFontMetrics(textFont);
        
        double textHeight = getTextHeightWithSpacing(g2d, textSpacing);
        double textWidth  = getTextWidthMax(g2d);
        

        
        double xPos = tr.getX(positionX,r);
        double yPos = r.getY() + r.getHeight() - tr.relativeY(positionY, r);//r.getY() + r.getHeight() - tr.getY(positionY,r);
        
        //System.out.printf("X pos = %d, Y pos = %d\n",(int) xPos, (int) yPos);
        //System.out.println(r);
        //tr.show();
        
        if(this.fillBox==true){
            g2d.setColor(this.headerBackground);
             g2d.fillRoundRect((int) (xPos), 
                    (int) (yPos-paddingTop), 
                    (int) (textWidth + paddingLeft + paddingRight), 
                    (int) (textHeight + paddingTop + paddingBottom), 
                    roundRadius,roundRadius);
        }
        
        if(this.drawBox==true){
            g2d.setColor(this.borderColor);
        
            g2d.drawRoundRect((int) (xPos), 
                    (int) (yPos-paddingTop), 
                    (int) (textWidth + paddingLeft + paddingRight), 
                    (int) (textHeight + paddingTop + paddingBottom), 
                    roundRadius,roundRadius);
        }
        
        xPos += paddingLeft;
        yPos += paddingTop;
        
        for(int i = 0; i < textStrings.size(); i++){
            latexText.setText(textStrings.get(i));
            Rectangle2D tb = latexText.getBounds(g2d);
            g2d.setColor(textColor);
            latexText.setColor(textColor);
            double xPosMarker = xPos - ((double)paddingLeft)/2.0;
            this.textPositions.get(i).x = xPosMarker;
            this.textPositions.get(i).y = yPos + (tb.getHeight() + textSpacing*tb.getHeight())*0.5;
            latexText.drawString(g2d, (int) xPos, (int) yPos,  LatexText.ALIGN_LEFT,
                    LatexText.ALIGN_TOP);
            yPos += tb.getHeight() + textSpacing*tb.getHeight();
        }
    }
    
    protected List<Point2D.Double> getTextPositions(){
        return this.textPositions;
    }
    
    protected void drawLayerOneLine(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        //NodeRegion2D bounds = getParent().getBounds();
        //System.out.println("[Pave Text] ---> " + bounds);
        
        FontMetrics metrics = g2d.getFontMetrics(textFont);
        
        double textHeight = this.getTextHeightMax(g2d);
        double textWidth  = this.getTextWidthWithSpacing(g2d, 0.0);
        
        
        
        double xPos = tr.getX(positionX,r);
        double yPos = tr.getX(positionX,r);
        
        if(this.fillBox==true){
            g2d.setColor(this.headerBackground);
            g2d.fillRoundRect((int) (xPos), 
                (int) (yPos-paddingTop), 
                (int) (textWidth + paddingLeft*textStrings.size() 
                        + paddingRight*textStrings.size()), 
                (int) (textHeight + paddingTop + paddingBottom), 
                roundRadius,roundRadius);
        }
        if(this.drawBox==true){
            g2d.setColor(this.borderColor);
            
            g2d.drawRoundRect((int) (xPos), 
                    (int) (yPos-paddingTop), 
                    (int) (textWidth + paddingLeft*textStrings.size() 
                            + paddingRight*textStrings.size()), 
                    (int) (textHeight + paddingTop + paddingBottom), 
                    roundRadius,roundRadius);
            
            xPos += paddingLeft;
            yPos += paddingTop+textHeight/2.0;
            
        }
        
        for(int i = 0; i < textStrings.size(); i++){
            latexText.setText(textStrings.get(i));
            Rectangle2D tb = latexText.getBounds(g2d);
            g2d.setColor(textColor);
            latexText.setColor(textColor);
            latexText.drawString(g2d, (int) xPos, (int) yPos,  LatexText.ALIGN_LEFT,
                    LatexText.ALIGN_CENTER);

            double xPosMarker = xPos - ((double)paddingLeft)/2.0;
            this.textPositions.get(i).x = xPosMarker;
            this.textPositions.get(i).y = yPos;
            
            /*g2d.drawLine((int) (xPosMarker-10), (int) yPos, (int) (xPosMarker+10), (int) yPos);
            g2d.drawLine((int) xPosMarker, (int) (yPos-10), (int) (xPosMarker), (int) (yPos+10));
            */

            xPos += tb.getWidth() + paddingLeft + paddingRight;
            //yPos += tb.getHeight() + textSpacing*tb.getHeight();
        }
    }
    
   /* @Override
    public void drawLayer(Graphics2D g2d, int layer){
        
       if(paveStyle == PaveTextStyle.MULTILINE) drawLayerMultiLine(g2d);
       if(paveStyle == PaveTextStyle.ONELINE) drawLayerOneLine(g2d);
       
    }*/
    
    public double getTextHeightWithSpacing(Graphics2D g2d,double spacing){
        double height = 0;
        for(int i = 0; i < textStrings.size(); i++){
            latexText.setText(textStrings.get(i));
            Rectangle2D bounds = latexText.getBounds(g2d);
            height += bounds.getHeight();
            if(i!=0) height += spacing*bounds.getHeight();
        }
        return height;
    }
    
    public double getTextWidthWithSpacing(Graphics2D g2d, double spacing){
        double width = 0;
        for(int i = 0; i < textStrings.size(); i++){
            latexText.setText(textStrings.get(i));
            Rectangle2D bounds = latexText.getBounds(g2d);
            width += bounds.getWidth();
            if(i!=0) width += spacing;
        }
        return width;
    }
    
    public double getTextWidthMax(Graphics2D g2d){
        double widthMax = 0;
        for(int i = 0; i < textStrings.size(); i++){
            latexText.setText(textStrings.get(i));
            Rectangle2D bounds = latexText.getBounds(g2d);
            if(bounds.getWidth()>widthMax) widthMax = bounds.getWidth();
        }
        return widthMax;
    }
    
    public double getTextHeightMax(Graphics2D g2d){
        double heightMax = 0;
        for(int i = 0; i < textStrings.size(); i++){
            latexText.setText(textStrings.get(i));
            Rectangle2D bounds = latexText.getBounds(g2d);
            if(bounds.getHeight()>heightMax) heightMax = bounds.getHeight();
        }
        return heightMax;
    }
    public int getTextHeight(FontMetrics fm, String text){
        int descend = fm.getDescent();
        int ascend  = fm.getAscent();
        int height  = fm.getHeight();
        //System.out.println(" for text : " + text + String.format(" desc = %5d, ascd = %5d, heigth = %5d \n",
        //        descend,ascend,height));
        return 0;
    }
    
     @Override
    public boolean isNDF() {
        return paveNDF;
    }
}
