/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import j4np.graphics.Translation2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JPanel;
import twig.graphics.TGCanvas;
import twig.widgets.LatexText;
import twig.widgets.Polygon;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class HipoFormat implements Widget {
    
    public void drawTable(Graphics2D g2d, int x, int y, int ncols, int nrows, Color colhead, Color colrow){
        LatexText text = new LatexText("Input Layer");
        text.setFont(new Font("Avenir",Font.BOLD,18));
        String[] columns = new String[]{"a","b","c","d","e","f"};
        int startX = x;
        int startY = y;
        int wordSpace = 50;
        int lineSpace = 30;
        int columnSize = 26;
        BasicStroke stroke = new BasicStroke(2);
        for(int i = 0; i < 3; i++){
            g2d.setColor(colhead);
            g2d.fillRect(startX+i*wordSpace-wordSpace/2, startY-lineSpace/2, wordSpace, lineSpace);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(stroke);
            g2d.drawRect(startX+i*wordSpace-wordSpace/2, startY-lineSpace/2, wordSpace, lineSpace);
            text.setText(columns[i]);    
            text.drawString(g2d, startX+wordSpace*i, startY, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        }
        
        for(int i = 0; i < 3; i++){
            for(int row = 0; row < nrows; row++){
                g2d.setColor(colrow);
                g2d.fillRect(startX+i*wordSpace-wordSpace/2, startY+(row+1)*lineSpace-lineSpace/2, wordSpace, lineSpace);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(stroke);
                g2d.drawRect(startX+i*wordSpace-wordSpace/2, startY+(row+1)*lineSpace-lineSpace/2, wordSpace, lineSpace);
                text.setText(String.format("%s%d",columns[i],row+1));    
                text.drawString(g2d, startX+wordSpace*i, startY + (row+1)*lineSpace, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
            }
        }
    }
    
    
    
    public void drawOne(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        LatexText text = new LatexText("Input Layer");
        text.setFont(new Font("Avenir",Font.BOLD,18));
        
        LatexText title = new LatexText("Input Layer");
        title.setFont(new Font("Avenir",Font.PLAIN,18));
        
        int startX = 100;
        int startY = 100;
        int wordSpace = 50;
        int lineSpace = 30;
        int columnSize = 26;
        int nrows = 5;
        String[] columns = new String[]{"a","b","c"};
        Color[]  headers = new Color[]{new Color(0xFF,0x90,0x00), 
            new Color(0x94,0xF9,0x00), new Color(0x1C,0xFA,0x90)};
        
        Color[]     rowc = new Color[]{new Color(0xFF,0xD0,0x6C), 
            new Color(0xD9,0xFA,0x72), new Color(0x79,0xFC,0xD4)
        };
        
        BasicStroke stroke = new BasicStroke(2);
        for(int i = 0; i < 3; i++){
            g2d.setColor(headers[i]);
            g2d.fillRect(startX+i*wordSpace-wordSpace/2, startY-lineSpace/2, wordSpace, lineSpace);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(stroke);
            g2d.drawRect(startX+i*wordSpace-wordSpace/2, startY-lineSpace/2, wordSpace, lineSpace);
        }
        
        for(int i = 0; i < 3; i++){
            for(int row = 0; row < nrows; row++){
                g2d.setColor(rowc[i]);
                g2d.fillRect(startX+i*wordSpace-wordSpace/2, startY+(row+1)*lineSpace-lineSpace/2, wordSpace, lineSpace);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(stroke);
                g2d.drawRect(startX+i*wordSpace-wordSpace/2, startY+(row+1)*lineSpace-lineSpace/2, wordSpace, lineSpace);
            }
        }
        //text.drawString(g2d, 100, 100, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        for(int i = 0; i < 3; i++){
            text.setText(columns[i]);    
            text.drawString(g2d, startX+wordSpace*i, startY, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        }
        
        for(int i = 0; i < 3; i++){
            for(int row = 0; row < nrows; row++){
                text.setText(String.format("%s%d",columns[i],row+1));    
                text.drawString(g2d, startX+wordSpace*i, startY + (row+1)*lineSpace, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
            }
        }
        
        title.setText("Logical Table");
        title.drawString(g2d, startX + (wordSpace/2)*2, startY-lineSpace, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        int offsetX = 180;
        int offsetY = 20;
        
        for(int row = 0; row < nrows; row++){            
            for(int i = 0; i < 3; i++){

                int x = offsetX+startX+(i+(row*3))*wordSpace;
                int y = offsetY+startY;
                g2d.setColor(rowc[i]);
                g2d.fillRect(x,y , wordSpace, lineSpace);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(stroke);
                g2d.drawRect(x,y , wordSpace, lineSpace);
                text.setText(String.format("%s%d", columns[i],row+1));
                text.drawString(g2d, x+wordSpace/2, y+lineSpace/2, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
            }
        }
        
        title.setText("Type 1 table Memory Layout");
        title.drawString(g2d, startX + offsetX+120, startY+offsetY-lineSpace/2, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        

         offsetY = 100;
         int x = offsetX + startX - wordSpace;        
         for(int i = 0; i < 3; i++){

             for(int row = 0; row < nrows; row++){ 
                x += wordSpace;
                int y = offsetY+startY;
                g2d.setColor(rowc[i]);
                g2d.fillRect(x,y , wordSpace, lineSpace);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(stroke);
                g2d.drawRect(x,y , wordSpace, lineSpace);
                text.setText(String.format("%s%d", columns[i],row+1));
                text.drawString(g2d, x+wordSpace/2, y+lineSpace/2, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
            }
        }
         title.setText("Type 2 table Memory Layout");
        title.drawString(g2d, startX + offsetX+120, startY+offsetY-lineSpace/2, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
    }
    public void drawLine(Graphics2D g2d, int x, int y, Color[] colors, String[] labels, int... type){
        int startX = x;
        int startY = y;
        int wordSpace = 50;
        int lineSpace = 30;
        int xpos = startX;
        LatexText text = new LatexText("Input Layer");
        text.setFont(new Font("Avenir",Font.BOLD,18));
        BasicStroke stroke = new BasicStroke(2);
        for(int row = 0; row < type.length; row++){
            x += wordSpace;                
            g2d.setColor(colors[type[row]]);
            g2d.fillRect(x,y , wordSpace, lineSpace);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(stroke);
            g2d.drawRect(x,y , wordSpace, lineSpace);
            text.setText(labels[type[row]]);
            text.drawString(g2d, x+wordSpace/2, y+lineSpace/2, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        }
    }
    
    public void drawTwo(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        
        LatexText title = new LatexText("X (nrows==2)");        
        title.setFont(new Font("Avenir",Font.BOLD,18));
        
        LatexText text = new LatexText("X (nrows==2)");        
        text.setFont(new Font("Avenir",Font.PLAIN,18));
        this.drawTable(g2d, 100, 100, 4, 2, new Color(0x18,0x9A,0xB4), new Color(0xEC,0xF8,0x7F));
        this.drawTable(g2d, 100, 230, 4, 3, new Color(0x75,0xE6,0xDA), new Color(0xEC,0xF8,0x7F));
        this.drawTable(g2d, 100, 390, 4, 5, new Color(0xD4,0xF1,0xF4), new Color(0xEC,0xF8,0x7F));
        
        title.drawString(g2d, 140, 190, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        title.setText("Y (nrows==3)");
        title.drawString(g2d, 140, 350, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        title.setText("Z (nrows>=5)");
        title.drawString(g2d, 140, 570, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        Color[] colors = new Color[]{
            new Color(0x18,0x9A,0xB4),new Color(0x75,0xE6,0xDA),new Color(0xD4,0xF1,0xF4)};
        String[] labels = new String[]{"X","Y","Z"};
        
        this.drawLine(g2d, 230, 80, colors,labels, 0,1,0,0,1,0,2,0,0,1,0,1,0,2); //4
        this.drawLine(g2d, 230, 140, colors,labels, 0,0,1,0,0,1,0,2,0,0,1,0,0,1);//5
        this.drawLine(g2d, 230, 200, colors,labels, 0,1,0,0,1,0,2,0,0,0,0,1,0,2);//4
        this.drawLine(g2d, 230, 260, colors,labels, 0,1,2,0,0,0,1,0,0,1,0,2,0,0);//3
        
        this.drawLine(g2d, 230, 360, colors,labels, 1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1);
        this.drawLine(g2d, 230, 420, colors,labels, 2,2,2,2,2,2,2,0,0,0,0,0,0,0);
        this.drawLine(g2d, 230, 480, colors,labels, 0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        this.drawLine(g2d, 230, 540, colors,labels, 0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        
        text.setText("record[0] (tag=0)");
        text.drawString(g2d, 280, 65, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[1] (tag=0)");
        text.drawString(g2d, 630, 65, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);        
        text.setText("record[2] (tag=0)");
        text.drawString(g2d, 280, 125, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);        
        text.setText("record[3] (tag=0)");
        text.drawString(g2d, 630, 125, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[4] (tag=0)");
        //text.setText("record (tag=1)");
        text.drawString(g2d, 280, 185, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[5] (tag=0)");
        text.drawString(g2d, 630, 185, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[6] (tag=0)");
        text.drawString(g2d, 280, 245, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[7] (tag=0)");
        text.drawString(g2d, 630, 245, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        
        
        text.setText("record[0] (tag=2)");
        text.drawString(g2d, 280, 345, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[1] (tag=2)");
        text.drawString(g2d, 630, 345, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);        
        text.setText("record[2] (tag=3)");
        text.drawString(g2d, 280, 405, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);        
        text.setText("record[3] (tag=1)");
        text.drawString(g2d, 630, 405, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[4] (tag=1)");
        text.drawString(g2d, 280, 465, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[5] (tag=1)");
        text.drawString(g2d, 630, 465, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[6] (tag=1)");
        text.drawString(g2d, 280, 525, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.setText("record[7] (tag=1)");
        text.drawString(g2d, 630, 525, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        
        //this.drawLine(g2d, 230, 140, colors,labels, 1,0,1,1,0,1,0,2,0,0,1,0,1,1);
        //this.drawLine(g2d, 230, 180, colors,labels, 0,1,1,0,1,0,2,0,0,1,0,1,1,2);
        //this.drawLine(g2d, 230, 220, colors,labels, 0,1,2,0,1,0,1,0,1,1,0,2,1,0);
        text.setText("Tagged File Layout");
        text.drawString(g2d, 1025, 470, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER, LatexText.TextRotate.RIGHT);
        text.setText("Untagged File Layout");
        text.drawString(g2d, 1025, 190, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER, LatexText.TextRotate.RIGHT);
    }
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        //this.drawOne(g2d, r, tr);
        //this.drawTwo(g2d, r, tr);
        this.drawThree(g2d, r, tr);
        /*
        LatexText text = new LatexText("Input Layer");
        text.setFont(new Font("Avenir",Font.BOLD,18));
        
        LatexText title = new LatexText("Input Layer");
        title.setFont(new Font("Avenir",Font.PLAIN,18));
        
        int startX = 100;
        int startY = 100;
        int wordSpace = 50;
        int lineSpace = 30;
        int columnSize = 26;
        int nrows = 5;
        String[] columns = new String[]{"a","b","c"};
        Color[]  headers = new Color[]{new Color(0xFF,0x90,0x00), 
            new Color(0x94,0xF9,0x00), new Color(0x1C,0xFA,0x90)};
        
        Color[]     rowc = new Color[]{new Color(0xFF,0xD0,0x6C), 
            new Color(0xD9,0xFA,0x72), new Color(0x79,0xFC,0xD4)
        };
        
        BasicStroke stroke = new BasicStroke(2);
        for(int i = 0; i < 3; i++){
            g2d.setColor(headers[i]);
            g2d.fillRect(startX+i*wordSpace-wordSpace/2, startY-lineSpace/2, wordSpace, lineSpace);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(stroke);
            g2d.drawRect(startX+i*wordSpace-wordSpace/2, startY-lineSpace/2, wordSpace, lineSpace);
        }
        
        for(int i = 0; i < 3; i++){
            for(int row = 0; row < nrows; row++){
                g2d.setColor(rowc[i]);
                g2d.fillRect(startX+i*wordSpace-wordSpace/2, startY+(row+1)*lineSpace-lineSpace/2, wordSpace, lineSpace);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(stroke);
                g2d.drawRect(startX+i*wordSpace-wordSpace/2, startY+(row+1)*lineSpace-lineSpace/2, wordSpace, lineSpace);
            }
        }
        //text.drawString(g2d, 100, 100, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        for(int i = 0; i < 3; i++){
            text.setText(columns[i]);    
            text.drawString(g2d, startX+wordSpace*i, startY, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        }
        
        for(int i = 0; i < 3; i++){
            for(int row = 0; row < nrows; row++){
                text.setText(String.format("%s%d",columns[i],row+1));    
                text.drawString(g2d, startX+wordSpace*i, startY + (row+1)*lineSpace, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
            }
        }
        
        title.setText("Logical Table");
        title.drawString(g2d, startX + (wordSpace/2)*2, startY-lineSpace, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);*/
    }
    public GeneralPath getPath(int x, int y, int w, int h){
        double depth = 10;
        Polygon p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x+w, y);
        p.addPoint(x+w, y+h);
        p.addPoint(x+w-w*0.1, y+h-depth);
        p.addPoint(x+w-0.45*w, y+h+depth*2);
        p.addPoint(x+w-0.55*w, y+h+depth*2-depth);
        p.addPoint(x+w-0.9*w, y+h+depth*3);
        p.addPoint(x, y+h+depth*3-depth);
        p.addPoint(x, y);
        return p.getPath();
    }
    
    public void boxText(Graphics2D g2d, String string, int x, int y, int w, int h, Color col){
        LatexText text = new LatexText(string);
        g2d.setColor(col);
        g2d.fillRect(x, y, w, h);
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.black);
        g2d.drawRect(x, y, w, h);
        text.drawString(g2d, x + w/2, y+h/2, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
    }
    
    public void boxText2(Graphics2D g2d, String string, int x, int y, int w, int h, Color col){
        LatexText text = new LatexText(string);
        g2d.setColor(col);
        g2d.fillRect(x, y, w, h);
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.black);
        g2d.drawRect(x, y, w, h);
        text.drawString(g2d, x + 5, y+5, LatexText.TextAlign.LEFT, LatexText.TextAlign.TOP);
    }
    
    public GeneralPath getPathRev(int x, int y, int w, int h){        
        double depth = 10;
        Polygon p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x+w, y);
        p.addPoint(x+w, y-h);
        p.addPoint(x+w-w*0.1, y-h-depth);
        p.addPoint(x+w-0.45*w, y-h+depth*2);
        p.addPoint(x+w-0.55*w, y-h+depth*2-depth);
        p.addPoint(x+w-0.9*w, y-h+depth*3);
        p.addPoint(x, y-h+depth*3-depth);
        p.addPoint(x, y);
        return p.getPath();
    }
    
    public GeneralPath getPathBoth(int x, int y, int w, int h){        
        double depth = 10;
        Polygon p = new Polygon();
        
        p.addPoint(x, y+depth);
        p.addPoint(x, y+h);
        p.addPoint(x+w-0.9*w, y+h+depth);
        p.addPoint(x+w-0.55*w, y+h-depth);
        p.addPoint(x+w-0.45*w, y+h);
        p.addPoint(x+w-0.1*w, y+h-3*depth);
        p.addPoint(x+w, y+h-2*depth);
        p.addPoint(x+w, y-depth);
        p.addPoint(x+w-0.1*w, y-2*depth);
        p.addPoint(x+w-0.45*w, y+depth);
        p.addPoint(x+w-0.55*w, y);
        p.addPoint(x+w-0.90*w, y+depth*2);
        p.addPoint(x, y+depth);
       // p.addPoint(x+w, y-h);
       // p.addPoint(x+w-w*0.1, y-h-depth);
       // p.addPoint(x+w-0.45*w, y-h+depth*2);
       // p.addPoint(x+w-0.55*w, y-h+depth*2-depth);
       // p.addPoint(x+w-0.9*w, y-h+depth*3);
       // p.addPoint(x, y-h+depth*3-depth);
       // p.addPoint(x, y);
        return p.getPath();
    }
    
    
    public void drawThree(Graphics2D g2d, Rectangle2D r, Translation2D tr){
        LatexText title = new LatexText("X (nrows==2)");        
        title.setFont(new Font("Avenir",Font.BOLD,18));
        
        LatexText text = new LatexText("X (nrows==2)");        
        //text.setFont(new Font("Avenir",Font.PLAIN,18));
        

        g2d.setColor(Color.red);
        GeneralPath p = this.getPath(100,100, 300, 325);
        g2d.setColor(new Color(180,180,180));
        g2d.fill(p);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(p);
        
//g2d.draw(p);
        
        
        //GeneralPath p2 = this.getPath(120,120, 160, 260);
        //g2d.draw(p2);
        title.setText("File Header");
        title.drawString(g2d, 105, 105, LatexText.TextAlign.LEFT, LatexText.TextAlign.TOP);
        this.boxText(g2d, "Magic Number (0xC0DA)", 115, 140, 270, 30, new Color(200,200,240));
        this.boxText2(g2d, "Header Data", 115,180,270, 150, new Color(200,200,200));
        this.boxText(g2d, "Object Distionaries", 130, 210, 240, 40, new Color(230,230,230));
        this.boxText(g2d, "User Metadata", 130, 260, 240, 40, new Color(230,230,230));
        this.boxText(g2d, "File Footer Position", 115, 340, 270, 30, new Color(200,200,240));
        
        
        GeneralPath dr = this.getPath(115,380, 270, 30);
        
        g2d.setColor(new Color(200,200,200));
        g2d.fill(dr);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(dr);
        text.setText("Record");
        text.drawString(g2d, 120, 385, LatexText.TextAlign.LEFT, LatexText.TextAlign.TOP);
        
        GeneralPath pr = this.getPathRev(100,690, 300, 250);
        g2d.setColor(new Color(180,180,180));
        g2d.fill(pr);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(pr);
        
        GeneralPath dr2 = this.getPathRev(115,510, 270, 50);
        g2d.setColor(new Color(200,200,200));
        g2d.fill(dr2);
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(dr2);
        text.drawString(g2d, 120, 490, LatexText.TextAlign.LEFT, LatexText.TextAlign.TOP);
        title.setText("File Footer");
        title.drawString(g2d, 105, 515, LatexText.TextAlign.LEFT, LatexText.TextAlign.TOP);
        
        this.boxText2(g2d, "Record Information", 115,540,270, 140, new Color(200,200,200));
        this.boxText(g2d, "Record Positions", 130, 565, 240, 30, new Color(230,230,230));
        this.boxText(g2d, "Record Lengths", 130, 600, 240, 30, new Color(230,230,230));
        this.boxText(g2d, "Record Tags", 130, 635, 240, 30, new Color(230,230,230));
        
        /*for(int i = 0; i < 5; i++){
            GeneralPath p3 = this.getPathBoth(550-i*10, 120+i*10, 300, 500);
            g2d.setColor(new Color(180,180,180));        
            g2d.fill(p3);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(p3);
        }*/
        
        int yoff = -25;
        GeneralPath p3 = this.getPathBoth(550, 120+yoff, 400, 580);
        g2d.setColor(new Color(180,180,180));        
        g2d.fill(p3);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(p3);
        
        GeneralPath p4 = this.getPathRev(560, 180+yoff, 380, 60);
        g2d.setColor(new Color(200,200,200));        
        g2d.fill(p4);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(p4);
        
        GeneralPath p5 = this.getPath(560, 630+yoff, 380, 40);
        g2d.setColor(new Color(200,200,200));        
        g2d.fill(p5);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(p5);
        
        this.boxText2(g2d, "Record", 560, 190+yoff, 380, 430, new Color(220,220,220));
        this.boxText(g2d,  "Magic Number (0xC0DA)", 575, 215+yoff, 350, 30, new Color(200,200,240));
        this.boxText(g2d,  "Record Length", 575, 245+yoff, 350, 30, new Color(240,240,240));
        this.boxText(g2d,  "Data Length", 575, 275+yoff, 350, 30, new Color(240,240,240));
        this.boxText(g2d,  "Data Length Compressed", 575, 305+yoff, 350, 30, new Color(240,240,240));
        this.boxText(g2d,  "Number of Events", 575, 335+yoff, 350, 30, new Color(240,240,240));
        this.boxText(g2d,  "Record Tag", 575, 365+yoff, 350, 30, new Color(240,240,240));
        this.boxText2(g2d, "Compressed Data", 575, 400+yoff, 350, 200, new Color(240,240,240));
        
        this.boxText(g2d, "Record Meta-Data", 590, 430+yoff, 320, 30, new Color(240,250,240));
        this.boxText(g2d, "Event Index Array", 590, 470+yoff, 320, 30, new Color(240,250,240));
        GeneralPath d1 = this.getPath(590, 510+yoff, 320, 25);
        g2d.setColor(new Color(240,250,240));        
        g2d.fill(d1);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(d1);
        
        GeneralPath d2 = this.getPathRev(590, 590+yoff, 320, 45);
        g2d.setColor(new Color(240,250,240));        
        g2d.fill(d2);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(d2);
        //text.setFont(new Font("Avenir",Font.PLAIN,18));
        text.setText("Event #1");
        text.drawString(g2d, 750, 525+yoff, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        text.setText("Event #N");
        text.drawString(g2d, 750, 578+yoff, LatexText.TextAlign.CENTER, LatexText.TextAlign.CENTER);
        
        text.setText("Record");
        text.drawString(g2d, 565, 645+yoff, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
        text.drawString(g2d, 565, 170+yoff, LatexText.TextAlign.LEFT, LatexText.TextAlign.CENTER);
    }
    
    @Override
    public boolean isNDF() {
        return true;
    }

    @Override
    public void configure(JComponent parent) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public static void main(String[] args){
        // - draw oneTGCanvas c = new TGCanvas("network_arch",1100,400);
        TGCanvas c = new TGCanvas("network_arch",1100,780);
        
        c.view().region().drawFrame(false);
        c.view().region().setDebugMode(true);
        HipoFormat f = new HipoFormat();
        c.view().region().draw(f);
        c.view().export("file_structure.pdf", "pdf");
    }
}
