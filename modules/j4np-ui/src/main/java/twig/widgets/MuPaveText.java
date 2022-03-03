/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import j4np.graphics.NodeInsets;
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
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import twig.widgets.LatexText.TextAlign;
import twig.widgets.LatexText.TextRotate;


/**
 *
 * @author gavalian
 */
public class MuPaveText implements Widget { 
   
    public enum MuPaveTextStyle {
        MULTILINE, ONELINE, STATS_MULTILINE;
    }
    
    protected Font           textFont = new Font("Avenir", Font.PLAIN, 18);
    private Color           textColor = Color.BLACK;
    private Color         borderColor = new Color(200,200,200);
    private Color    headerBackground = new Color(12,255,255);
    private String         textHeader = "Info";
        
    private List<String>                textStrings = new ArrayList<>();
    private List<Rectangle2D.Double>  textPositions = new ArrayList<>();
    private Rectangle2D.Double         paveBoundary = new Rectangle2D.Double();
    
    private LatexText       latexText = new LatexText("a",0,0);
    
    private double        textSpacing = 0.0;
    
    protected Point2D          position = new Point2D.Double(0.0,0.0);
    protected Point2D          positionOffset = new Point2D.Double(0.0,0.0);
    protected NodeInsets       insets = new NodeInsets(5,5,5,5);
    
    /*
    private double          positionX = 0;
    private double          positionY = 0;
    
    private double          positionOffsetX = 0;
    private double          positionOffsetY = 0;
    */
    
    private int             roundRadius = 5;
    public  MuPaveTextStyle   paveStyle = MuPaveTextStyle.MULTILINE;
        
    public  boolean          attrCoordPDF = true;    
    public boolean            attrDrawBox = true;
    public boolean            attrFillBox = true;
    
    private TextAlign       xAlignment = TextAlign.LEFT;
    private TextAlign       yAlignment = TextAlign.TOP;
    
    private TextAlign       textAlignment = TextAlign.RIGHT;
    
    private TextRotate        rotation = TextRotate.NONE;    
    private TextAlign    paveAlignment = TextAlign.TOP_LEFT;
    
    
    public MuPaveText(String text, double x, double y, Boolean boxDraw, int fontSize){        
        this.position.setLocation(x, y);
        this.attrDrawBox = boxDraw;
        this.attrFillBox = boxDraw;
        textFont = new Font("Avenir", Font.PLAIN, fontSize);
        this.latexText.setFont(textFont);
        textStrings.add(text);
        textPositions.add(new Rectangle2D.Double(0.0,0.0,0.0,0.0));
    }
    
    public MuPaveText(List<String> text, double x, double y, Boolean boxDraw, int fontSize){        
        this.position.setLocation(x, y);
        this.attrDrawBox = boxDraw;
        this.attrFillBox = boxDraw;
        textFont = new Font("Avenir", Font.PLAIN, fontSize);
        this.latexText.setFont(textFont);
        this.addLines(text);
    }
    
    public MuPaveText(String text, double x, double y){
        this.position.setLocation(x, y);
        textStrings.add(text);
        textPositions.add(new Rectangle2D.Double(0.0,0.0,0.0,0.0));
    }
    
    public MuPaveText(List<String> texts, double x, double y){
        this.position.setLocation(x, y);
        for(String text : texts)
            this.addLine(text);
    }
    
    public MuPaveText(double x, double y){
        this.position.setLocation(x, y);
        latexText.setFont(textFont);
    }
    
    public MuPaveText(double x, double y, boolean boxDraw){
        position.setLocation(x, y);
        latexText.setFont(textFont);
        this.attrDrawBox = boxDraw;
        this.attrFillBox = boxDraw;
    }
    
    public MuPaveText setStyle(MuPaveTextStyle style){
        paveStyle = style; return this;
    }
    
    public void setMultiLine(boolean flag){
        if(flag==true){
            this.paveStyle = MuPaveTextStyle.MULTILINE;
        } else {
            this.paveStyle = MuPaveTextStyle.ONELINE;
        }
    }
    
    public MuPaveTextStyle getStyle(){
        return paveStyle;
    }
    
    public void setTextColor(Color col){
        this.textColor = col;
    }
    
    public final void setPosition(double x, double y){
        position.setLocation(x, y);
    }
    
    public final MuPaveText addLine(String line){
        textStrings.add(line); 
        textPositions.add(new Rectangle2D.Double(0.0,0.0,0.0,0.0));
        return this;
    }
    
    public void show(){
        System.out.println("number of lines = " + textStrings.size());
        for(String line : textStrings) System.out.println("\t--> " + line);
    }
    
    public MuPaveText setAlign(TextAlign xal, TextAlign yal){
        this.xAlignment = xal; this.yAlignment = yal;
        return this;
    }
    
    public MuPaveText setAlign(TextAlign pal){
        this.paveAlignment = pal;
        return this;
    }
    
    public MuPaveText setTexAlign(TextAlign pal){
        this.textAlignment = pal;
        return this;
    }
    
    public MuPaveText setPositionOffset(double xp, double yp){
        this.positionOffset.setLocation(xp, yp);
        return this;
    }
    
    public MuPaveText setRotate(TextRotate rot){
        rotation = rot;
        return this;
    }
    
    public MuPaveText setDrawBox(boolean flag){
        this.attrDrawBox = flag; return this;
    }
    
    public MuPaveText setFillBox(boolean flag){
        this.attrFillBox = flag; return this;
    }
    
    public MuPaveText addLines(String[] lines){
        for(String line : lines)
            this.addLine(line);
        return this;
    }
    
    public MuPaveText addLines(List<String> lines){
        for(String line : lines)
            this.addLine(line);
        return this;
    }
    
    public MuPaveText setNDF(boolean flag){ attrCoordPDF = flag;return this;}
    
    public NodeInsets getInsets(){return insets;}
            
    public MuPaveText setSpacing(double spacing){
        textSpacing = spacing; return this;
    }
    
    public void setFont(Font font){
        this.textFont = font;
        this.latexText.setFont(font);
    }
    
    public MuPaveText setBackgroundColor(Color color){
        headerBackground = color; return this;
    }
    
    public MuPaveText setBorderColor(Color color){
        borderColor = color; return this;
    }
    /*
    private void drawMultilineOLD(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        double height = getTextHeightWithSpacing(g2d, textSpacing);
        double  width = getTextWidthMax(g2d);
        
        double xPos = tr.getX(position.getX(),r);
        double yPos = r.getY() + r.getHeight() - tr.relativeY(position.getY(), r);
        
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect((int) xPos, (int) yPos, (int) width, (int) height );
        this.updateTextPositions(g2d, xPos, yPos);
        
        for(int i = 0; i < textStrings.size(); i++){
            Rectangle2D p = textPositions.get(i);
            double tpx = p.getX();
            if(this.textAlignment==TextAlign.RIGHT){
                tpx = (xPos+width) - p.getWidth();
            }
            
            if(this.textAlignment==TextAlign.CENTER){
                tpx = xPos + (width-p.getWidth())*0.5;
            }
            
            latexText.drawString(textStrings.get(i), g2d, 
                    (int) tpx,  (int) p.getY(),
                    xAlignment, yAlignment, 0);
        }
    }*/
    
    private void drawMultiline(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        //double height = getTextHeightWithSpacing(g2d, textSpacing);
        //double  width = getTextWidthMax(g2d);
        double   xPos = tr.getX(position.getX(),r);
        double   yPos = r.getY() + r.getHeight() - tr.relativeY(position.getY(), r);


        //g2d.drawRect((int) xPos, (int) yPos, (int) width, (int) height );
        
        this.updateTextPositions(g2d, xPos, yPos);
        this.drawPaveBackground(g2d, this.paveBoundary);
        this.drawTextsAtPosition(g2d, xPos,yPos, 
                paveBoundary.width,paveBoundary.height,
                0.0, 0.0);
    }
    
    public void drawPaveBackground(Graphics2D g2d, Rectangle2D r){
        if(attrFillBox==true){
            g2d.setColor(this.headerBackground);
            g2d.fillRoundRect((int) r.getX(), (int)r.getY(), (int) r.getWidth(),
                    (int)r.getHeight(), 5, 5);
        }
        if(attrDrawBox==true){
            g2d.setColor(this.borderColor);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect((int) r.getX(), (int)r.getY(), (int) r.getWidth(),
                    (int)r.getHeight(), 5, 5);
        }
    }
    
    public Rectangle2D getPaveBounds(){
        return paveBoundary;
    }
    
    private void rectGrow(Rectangle2D r, double x, double y){
        if(x>(r.getX()+r.getWidth())){
            r.setRect(r.getX(), r.getY(), r.getWidth() +  (x-r.getMaxX()), r.getHeight());
        }
        if(y>(r.getMaxY())){
            r.setRect(r.getX(), r.getY(), r.getWidth(), r.getHeight() + y - r.getMaxY());
        }
    }
    
    protected void drawTextsAtPosition(Graphics2D g2d, double x, double y,
            double width, double height,double xpos, double ypos){
        g2d.setColor(textColor);
        g2d.setFont(textFont);
        for(int i = 0; i < textStrings.size(); i++){
            Rectangle2D p = textPositions.get(i);
            double tpx = p.getX() + xpos;
            if(this.textAlignment==TextAlign.RIGHT){
                tpx = (xpos+p.getX()+width) - p.getWidth();
            }
            
            if(this.textAlignment==TextAlign.CENTER){
                tpx = x + xpos + (width-p.getWidth())*0.5;
            }
            
            latexText.drawString(textStrings.get(i), g2d, 
                    (int) tpx,  (int) (p.getY()+ypos),
                    xAlignment, yAlignment, 0);
        }
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        this.drawMultiline(g2d, r, tr);
        
        /*
        //System.out.println("style = " + paveStyle);
        //if(paveStyle == PaveTextStyle.MULTILINE) drawLayerMultiLine(g2d,r,tr);
        if(paveStyle == MuPaveTextStyle.MULTILINE || 
                paveStyle == MuPaveTextStyle.STATS_MULTILINE) ;
            //drawLayerMultiLineNuevo(g2d,r,tr);
        
        if(paveStyle == MuPaveTextStyle.ONELINE) ;
            //this.drawLayerOneLine(g2d, r, tr);*/
    }                
    
    protected List<Rectangle2D.Double> getTextPositions(){
        return this.textPositions;
    }
    
    
    protected void updateTextPositions(Graphics2D g2d, double xstart, double ystart){
        
        double xpos = xstart;
        double ypos = ystart;
        
        for(int i = 0; i < textStrings.size(); i++){
            
            latexText.setText(textStrings.get(i));
            Rectangle2D bounds = latexText.getBounds(g2d);
            this.textPositions.get(i).setRect(xpos,
                    ypos, bounds.getWidth(), bounds.getHeight());
            if(i==0) paveBoundary.setRect(xpos,ypos, 
                    bounds.getWidth(), bounds.getHeight());
            ypos += bounds.getHeight() + textSpacing*bounds.getHeight();

            if(i!=0)
                rectGrow(paveBoundary, xpos, ypos);            
        }
    }
    
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
        return attrCoordPDF;
    }
    
    @Override
    public void configure() {
        
        System.out.println("Oy, Configuring Pave Text");
        JTextField posX = new JTextField();
        JTextField posY = new JTextField();
               
        posX.setText(String.format("%.3f", this.position.getX()));
        posY.setText(String.format("%.3f", this.position.getY()));
        
        JCheckBox drawBoxCheck = new JCheckBox();
        drawBoxCheck.setSelected(this.attrDrawBox);
        
        JCheckBox fillBoxCheck = new JCheckBox();
        fillBoxCheck.setSelected(this.attrFillBox);
        
        Object[] message = {
            "Position X:", posX,
            "Position Y:", posY,
            "Draw Box:",drawBoxCheck,
            "Fill Box:",fillBoxCheck
            
        };
        
        int option = JOptionPane.showConfirmDialog(null, 
                
                message, "Pave Text", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            double x = Double.parseDouble(posX.getText());
            double y = Double.parseDouble(posY.getText());
            this.setPosition(x, y);
            this.attrDrawBox = drawBoxCheck.isSelected();
            this.attrFillBox = fillBoxCheck.isSelected();
        } else {
            System.out.println("Login canceled");
        }
    }
}
