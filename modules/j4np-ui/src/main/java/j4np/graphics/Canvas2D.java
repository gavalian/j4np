/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.eps.EPSProcessor;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.pdf.PDFProcessor;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author gavalian
 */
public class Canvas2D extends JPanel implements MouseInputListener {    

    //private List<Node2D>   graphicsComponents = new ArrayList<Node2D>();
    private List<Node2D>   graphicsComponents = Collections.synchronizedList(new ArrayList<Node2D>());
    
    private Point2D             mousePosition = new Point2D.Double(0,0);
    private Point2D              mousePressed = new Point2D.Double(0,0);
    
    private int        currentAcviteComponent = -1;
    private Timer                 updateTimer = null;
    private boolean       isUpdateTimerPaused = false;
    private Node2D                 activeNode = null;
    private int             canvasDiagnostics = 0;
    private Background2D           background = null; 
    private PopupProvider       popupProvider = null;
    private Color             backgroundColor = null;
    
    

    public Canvas2D(){
        
        /*this.graphicsComponents.add(new GraphicsObject2D(100,100));
        
        PaveTextObject2D pave = new PaveTextObject2D();
        pave.addText("Testing objects 2d");
        pave.addText("This is a pave object");
        pave.addText("with bounds defined by text");
        
        this.graphicsComponents.add(pave);
        */
        //GraphicsDataObject2D dataObject = new GraphicsDataObject2D(20,20);
        //this.graphicsComponents.add(dataObject);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }
    
    public void setBackground(Background2D back){
        background = back;
    }
    
    public void setBackgroundColor(Color col){
        backgroundColor = col;
    }
    
    public void setDebug(int level){ this.canvasDiagnostics = level;}
    
    public List<Node2D> getGraphicsComponents(){
        return this.graphicsComponents;
    }
    
    @Override
    public void paint(Graphics g){
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //Background2D.setRenderingQuality(g2d);
        
        int w = this.getSize().width;
        int h = this.getSize().height;
        List<Color> colors = Background2D.getColorPalette(0);
        
        Background2D.BACKGROUND_POPULATION = 80;
        //BufferedImage backImage = Background2D.createBackground(w, h, 15, new Color(240,168,51), colors);
        //g2d.drawImage(backImage, 0, 0, this);
        //g2d.setColor(Color.LIGHT_GRAY);
        //g2d.fillRect(0, 0, w, h);
        
        if(background!=null){
            background.drawBackground(g2d, 0, 0, w, h);
        }
        
        if(backgroundColor!=null){
            g2d.fillRect(0, 0, w, h);
        }
        
        NodeRegion2D region = new NodeRegion2D();
        region.set(0, 0, w, h);
        long paintStart = System.currentTimeMillis();
        for(int i = 0; i < this.graphicsComponents.size(); i++){
            //System.out.println(" updating the node = " + i);
            this.graphicsComponents.get(i).updateRegion(region);
            this.graphicsComponents.get(i).drawLayer(g2d, 0);
        }
        long paintEnd = System.currentTimeMillis();
        if(this.canvasDiagnostics>0){
            double time = paintEnd-paintStart;
            System.out.printf("[Canvas2D:diag] >>>> paint time = %8.1f ms\n",time);
        }
    }
        
    public void setPopupProvider(PopupProvider pr){
        this.popupProvider = pr;
    }
    
    public void setTimerStatus(boolean isPaused){ this.isUpdateTimerPaused = isPaused;}
    
    public void initTimer(int interval) {
        System.out.println("[EmbeddedCanvas] ---->  starting an update timer.");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(isUpdateTimerPaused==false) repaint();
                /*for(int i = 0; i < canvasPads.size();i++){
     Timer               System.out.println("PAD = " + i);
                    canvasPads.get(i).show();
                }*/
            }
        };
        updateTimer = new Timer("EmbeddeCanvasTimer");
        updateTimer.scheduleAtFixedRate(timerTask, 30, interval);
    }
    
    public void addNode(Node2D node){
        this.graphicsComponents.add(node);
    }
    
    public void addNodes(Collection<? extends Node2D> nodes){
        this.graphicsComponents.addAll(nodes);
    }
    
    public void arrange(CanvasLayout layout){
        for(int i = 0; i < layout.size(); i++){
            if(i<graphicsComponents.size()){
                Rectangle2D rect = layout.getBounds(i);
                graphicsComponents.get(i).setBoundsBind(
                        rect.getX(),rect.getY(),
                        rect.getWidth(),rect.getHeight()
                );
                graphicsComponents.get(i).alignMode(Node2D.ALIGN_RELATIVE);
            }
        }
    }
    public void arrange(double[][] fractions){
        int    nColumns = fractions.length;
        //System.out.println("COLUMNS:: ARRANGE = " + nColumns);
        double xStep    = 1.0/nColumns;
        int    counter  = 0;
        for(int x = 0; x < nColumns; x++){
            double xPosition = x*xStep;
            double yPosition = 0.0;
            for(int y = 0; y < fractions[x].length; y++){
                double yStep = fractions[x][y];
                if(counter<graphicsComponents.size()){
                    graphicsComponents.get(counter).setBoundsBind(xPosition, yPosition, xStep, yStep);
                    graphicsComponents.get(counter).alignMode(Node2D.ALIGN_RELATIVE);
                }
                yPosition += yStep;
                counter++;
            }
        }
    }
    
    public void arrange(int[] rows){
        double   cStep = 1.0/rows.length;
        int    counter = 0;
        for(int c = 0; c < rows.length; c++){
            double rStep = 1.0/rows[c];
            for(int r = 0; r < rows[c]; r++){
                double xPosition = c*cStep;
                double yPosition  = r*rStep;
                if(counter<graphicsComponents.size()){
                    graphicsComponents.get(counter).setBoundsBind(xPosition, yPosition, cStep, rStep);
                    graphicsComponents.get(counter).alignMode(Node2D.ALIGN_RELATIVE);
                }
                counter++;
            }
        }
    }
    
    public void arrange(int xDivisions, int yDivisions){
        double xStep = 1.0/xDivisions;
        double  yStep = 1.0/yDivisions;
        int  counter = 0;
        for(int y = 0; y < yDivisions; y++){
            for(int x = 0; x < xDivisions; x++){
                double xPosition = x*xStep;
                double yPosition = y*yStep;
                //System.out.println(" counter = ");
                if(counter<graphicsComponents.size()){
                    graphicsComponents.get(counter).setBoundsBind(xPosition, yPosition, xStep, yStep);
                    graphicsComponents.get(counter).alignMode(Node2D.ALIGN_RELATIVE);
                }
                counter++;
            }
        }
    }
    
    protected void arrangeWithGap(double left, double bottom, int xDivisions, int yDivisions){
        double xStep = (1.0-left)/xDivisions;
        double yStep = (1.0-bottom)/yDivisions;        
        int  counter = 0;
        for(int y = 0; y < yDivisions; y++){
            for(int x = 0; x < xDivisions; x++){
                double xPosition = left + x*xStep;
                double yPosition = y*yStep;
                //System.out.println(" counter = ");
                if(counter<graphicsComponents.size()){
                    graphicsComponents.get(counter).setBoundsBind(xPosition, yPosition, xStep, yStep);
                    graphicsComponents.get(counter).alignMode(Node2D.ALIGN_RELATIVE);
                }
                counter++;
            }
        }
    }
    
    public void divide(int xDivisions, int yDivisions){
        
        if(xDivisions*yDivisions!=graphicsComponents.size()){
            System.out.println("**** error **** : canvas contains " + graphicsComponents.size());
        }
        
        double xStep = 1.0/xDivisions;
        double yStep = 1.0/yDivisions;
        int  counter = 0;
        for(int x = 0; x < xDivisions; x++){
            for(int y = 0; y < yDivisions; y++){
                double xPosition = x*xStep;
                double yPosition = y*yStep;
                //System.out.println(" counter = ");
                if(counter<graphicsComponents.size()){
                    graphicsComponents.get(counter).setBoundsBind(xPosition, yPosition, xStep, yStep);
                    graphicsComponents.get(counter).alignMode(Node2D.ALIGN_RELATIVE);
                }
                counter++;
            }
        }
    }
    
    public void divide(int[] rows){
        double   cStep = 1.0/rows.length;
        int    counter = 0;
        for(int c = 0; c < rows.length; c++){
            double rStep = 1.0/rows[c];
            for(int r = 0; r < rows[c]; r++){
                double xPosition = c*cStep;
                double yPosition  = r*rStep;
                if(counter<graphicsComponents.size()){
                    graphicsComponents.get(counter).setBoundsBind(xPosition, yPosition, cStep, rStep);
                    graphicsComponents.get(counter).alignMode(Node2D.ALIGN_RELATIVE);
                }
                counter++;
            }
        }
    }
    
    public void addComponent(){
        this.graphicsComponents.add(new Node2D(100,100));
    }
    
    private int getObjectByPosition(int x, int y){
        int icounter = 0;
        int    index = -1;
        for(Node2D object : this.graphicsComponents){
            if(object.isClicked(x, y)==true){
                index = icounter;
            }
            icounter++;
        }
        return index;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("mouse clicked : for popup ? = " 
        //        + e.isPopupTrigger() + " button = " + e.getButton());
        if(e.getButton()==3){
            int xc = e.getX();
            int yc = e.getY();
            int objectOrder = this.getObjectByPosition(xc, yc);
            //System.out.printf("clicked object is %d out of %d obejcts \n", objectOrder,
             //       this.graphicsComponents.size());
             
             //System.out.println(" object Order by click = " + objectOrder);
             JPopupMenu menu = null;
            if(this.popupProvider!=null){
                if(objectOrder>=0&&objectOrder<this.graphicsComponents.size()){
                    menu = 
                            popupProvider.createMenu(graphicsComponents.get(objectOrder));
                } else {
                    menu = 
                            popupProvider.createMenu(null);
                }
                menu.show(this, e.getX(), e.getY());
                //System.out.println(">>>> showing popup");
            }
        }
        if(e.getClickCount()==2){
            
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.mousePressed.setLocation(e.getX(), e.getY());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println(" mouse released...");
        activeNode = null;
        for(Node2D node : this.graphicsComponents){
            if(node.getBounds().contains(e.getX(), e.getY())){
            //if(node.mousePressed(e.getX(), e.getY())==true){
                this.activeNode = node;
                //System.out.printf(" mouse released : well found an active node");
            }
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("dragging detected");
        if(this.activeNode!=null){
            //System.out.println("dragging component " + activeNode.getName());
            this.activeNode.applyMouseDrag((int) mousePosition.getX(), (int) mousePosition.getY(), e.getX(), e.getY());
            //System.out.printf(" mouse dragged (some active) : %5d %5d -> %5d %5d\n",
             //      (int) mousePressed.getX(),(int) mousePressed.getY(),e.getX(),e.getY());
            this.repaint();
        } else {
            //System.out.printf(" mouse dragged (no active) : %5d %5d -> %5d %5d\n",
            //       (int) mousePressed.getX(),(int) mousePressed.getY(),e.getX(),e.getY());
        }
        //System.out.println("Dragged = " + e.getX() + " " + e.getY() + " " + mousePosition.toString());        
        //mousePosition.setLocation(e.getX() ,e.getY()  );        
        //System.out.println(" Distance = " + mousePosition.distance(e.getX(),e.getY()));
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition.setLocation(e.getX(), e.getY());
       /* this.mousePosition.setLocation(e.getX(), e.getY());
        boolean rePaint = false;
        for(Node2D node : this.graphicsComponents){
            if(node.mouseMoved(e.getX(), e.getY())==true)
                rePaint = true;
        }*/
        //if(rePaint == true) this.repaint();
        //System.out.println(" XY = " + mousePosition.toString());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } 
    
    public void saveEPS(String filename){
        long paintStart = System.currentTimeMillis();
        Graphics2D vg2d = new VectorGraphics2D();
        this.paint(vg2d);
        long paintEnd = System.currentTimeMillis();
        long commandsStart = System.currentTimeMillis();
        CommandSequence commands = ((VectorGraphics2D) vg2d).getCommands();

        long commandsEnd = System.currentTimeMillis();
        
        
        int  width = getWidth();
        int height = getHeight();
        //System.out.println("[PDF] --> opening document size = " + width + "x" + height);
        long procStart = System.currentTimeMillis();
        EPSProcessor    processor = new EPSProcessor();
        Document  doc = processor.getDocument(commands, new PageSize(0,0,width,height));
        long procEnd = System.currentTimeMillis();
        try {
            long writeStart = System.currentTimeMillis();
            doc.writeTo(new FileOutputStream(filename));
            long writeEnd = System.currentTimeMillis();
            //PDFGraphics2D g = new PDFGraphics2D(0.0, 0.0, 210.0, 297.0);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Canvas2D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Canvas2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        double ptime = (double) (paintEnd-paintStart);
        double ctime = (double) (commandsEnd-commandsStart);
        double wtime = (double) (commandsEnd-commandsStart);
        double dtime = (double) (procEnd-procStart);
        System.out.println(String.format("[EPS] (%5dx%5d) --> time (%4.2f,%4.2f,%4.2f,%4.2f) : file saved = %s" ,
                width,height,ptime/1000.0,ctime/1000.0,wtime/1000.0,dtime/1000.0,filename));
    }
    
    public void saveSVG(String filename){
        long paintStart = System.currentTimeMillis();
        Graphics2D vg2d = new VectorGraphics2D();
        this.paint(vg2d);
        long paintEnd = System.currentTimeMillis();
        long commandsStart = System.currentTimeMillis();
        CommandSequence commands = ((VectorGraphics2D) vg2d).getCommands();

        long commandsEnd = System.currentTimeMillis();
        
        
        int  width = getWidth();
        int height = getHeight();
        //System.out.println("[PDF] --> opening document size = " + width + "x" + height);
        long procStart = System.currentTimeMillis();
        SVGProcessor    processor = new SVGProcessor();
        Document  doc = processor.getDocument(commands, new PageSize(0,0,width,height));
        long procEnd = System.currentTimeMillis();
        try {
            long writeStart = System.currentTimeMillis();
            doc.writeTo(new FileOutputStream(filename));
            long writeEnd = System.currentTimeMillis();
            //PDFGraphics2D g = new PDFGraphics2D(0.0, 0.0, 210.0, 297.0);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Canvas2D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Canvas2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        double ptime = (double) (paintEnd-paintStart);
        double ctime = (double) (commandsEnd-commandsStart);
        double wtime = (double) (commandsEnd-commandsStart);
        double dtime = (double) (procEnd-procStart);
        System.out.println(String.format("[SVG] (%5dx%5d) --> time (%4.2f,%4.2f,%4.2f,%4.2f) : file saved = %s" ,
                width,height,ptime/1000.0,ctime/1000.0,wtime/1000.0,dtime/1000.0,filename));
    }
    
    public void save(String filename) {
        long paintStart = System.currentTimeMillis();
        Graphics2D vg2d = new VectorGraphics2D();
        this.paint(vg2d);
        long paintEnd = System.currentTimeMillis();
        long commandsStart = System.currentTimeMillis();
        CommandSequence commands = ((VectorGraphics2D) vg2d).getCommands();

        long commandsEnd = System.currentTimeMillis();
        
        
        int  width = getWidth();
        int height = getHeight();
        //System.out.println("[PDF] --> opening document size = " + width + "x" + height);

        PDFProcessor    processor = new PDFProcessor(true);
        long procStart = System.currentTimeMillis();
        Document  doc = processor.getDocument(commands, new PageSize(0,0,width,height));
        long procEnd = System.currentTimeMillis();
        try {
            long writeStart = System.currentTimeMillis();
            doc.writeTo(new FileOutputStream(filename));
            long writeEnd = System.currentTimeMillis();
            //PDFGraphics2D g = new PDFGraphics2D(0.0, 0.0, 210.0, 297.0);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Canvas2D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Canvas2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        double ptime = (double) (paintEnd-paintStart);
        double ctime = (double) (commandsEnd-commandsStart);
        double wtime = (double) (commandsEnd-commandsStart);
        double dtime = (double) (procEnd-procStart);
        System.out.println(String.format("[PDF] (%5dx%5d) --> time (%4.2f,%4.2f,%4.2f,%4.2f) : file saved = %s" ,
                width,height,ptime/1000.0,ctime/1000.0,wtime/1000.0,dtime/1000.0,filename));
    }
    
    public static Canvas2D createFrame(JFrame frame, int w,int h){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas2D canvas = new Canvas2D();
        frame.setSize(w, h);
        frame.add(canvas);
        frame.setVisible(true);
        return canvas;
    }
    
    public static JFrame getFrame(Canvas2D canvas, int w,int h){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(w, h);
        frame.add(canvas);
        frame.setVisible(true);
        return frame;
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        Canvas2D  canvas = Canvas2D.createFrame(frame, 500, 500);
        List<Node2D> nodes = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            Node2D node = new Node2D(0,0,100,100,true);
            node.getInsets().left(15).right(15).top(15).bottom(15);
            canvas.addNode(node);
        }
        canvas.divide(3, 2);
        
    }
}

