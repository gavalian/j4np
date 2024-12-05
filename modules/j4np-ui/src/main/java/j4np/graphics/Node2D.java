/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author gavalian
 */
public class Node2D {
    
    public static int  ALIGN_INSETS   = 1;
    public static int  ALIGN_POSITION = 2;
    public static int  ALIGN_RELATIVE = 3;
    
    public static int  PIN_CORNER_NONE     = 0;
    public static int  PIN_CORNER_LEFT_TOP   = 1;
    public static int  PIN_CORNER_RIGTH_TOP = 2;
    public static int  PIN_CORNER_LEFT_BOTTOM = 3;
    public static int  PIN_CORNER_RIGTH_BOTTOM = 4;
    
    private boolean        canMove = true;
    private boolean    isDebugMode = false;//true;
    
    private int     ALIGN_MODE = Node2D.ALIGN_INSETS;
    private int       PIN_MODE = Node2D.PIN_CORNER_NONE;
    private int            UID = 0;
    
    private int  xPos = 0;
    private int  yPos = 0;
    private int    oW = 280;
    private int    oH = 50;
    
    protected Color        nodeBackground = null;//new Color(255,255,255,255);
    protected Color            nodeBorder = new Color(185,185,185,255);
    /*
    private Point2D           objectPosition = new Point2D.Double();    
    private Point2D    objectPositionClicked = new Point2D.Double();
    */
    
    private NodeRegion2D        nodeRegion = new NodeRegion2D();
    private NodeRegion2D    nodeRegionBind = new NodeRegion2D();
    
    private NodeInsets      nodeInsets = new NodeInsets();
    
    
    private Point2D         nodeClickedPosition = new Point2D.Double();
    private Point2D                 pinPosition = new Point2D.Double();
    //private Rectangle2D    nodeRegion = new Rectangle2D.Double();
    //private Rectangle2D    nodeInsets = new Rectangle2D.Double();
    
    private final List<Node2D>            nodeChildren = Collections.synchronizedList(new LinkedList<Node2D>());
    private final Rectangle2D    coordinateTranslation = new Rectangle2D.Double();
    
    private Node2D         nodeParent = null;
    private JPanel        superParent = null;
    private String          nodeName = "node";
    

        
    public Node2D(int x, int y){
        //xPos = x;
        //yPos = y;
        //objectPosition.setLocation(x, y);
        this.setPosition(x, y);
       // this.setBounds(x, y, 400,400);
       
    }
    
    public Node2D(int x, int y, boolean debug){
        //xPos = x;
        //yPos = y;
        //objectPosition.setLocation(x, y);
        this.setPosition(x, y);
        this.isDebugMode = debug;
       // this.setBounds(x, y, 400,400);
       
    }
    
    public Node2D(int x, int y, int w, int h){
        //xPos = x;
        //yPos = y;
        //objectPosition.setLocation(x, y);
        this.setPosition(x, y);
        this.setBounds(x, y, w, h);
    }
    
    public Node2D(int x, int y, int w, int h, boolean debug){
        //xPos = x;
        //yPos = y;
        //objectPosition.setLocation(x, y);
        this.setPosition(x, y);
        this.setBounds(x, y, w, h);
        this.isDebugMode = debug;
    }
     
  /*   public void setPopupProvider(PopupProvider pr){
         this.popupProvider = pr;
     }*/
     
    protected void setSuperParent(JPanel panel){ this.superParent = panel;}
    protected void repaint(){if(this.superParent!=null) this.superParent.repaint();}
    
    public void drawLayer(Graphics2D g2d, int layer){ 
        
        if(this.nodeBackground!=null){
            g2d.setColor(this.nodeBackground);            
        } /*else {
            g2d.setColor(Color.WHITE);
        }*/
        
        g2d.fillRoundRect((int) nodeRegion.getBounds().getX(), (int) nodeRegion.getBounds().getY() ,
                (int) nodeRegion.getBounds().getWidth(), (int) nodeRegion.getBounds().getBounds().getHeight() 
                ,0,0);
        
        if(this.isDebugMode == true){
            g2d.setColor(new Color(248,209,178));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect((int) nodeRegion.getBounds().getX(), 
                    (int) nodeRegion.getBounds().getY() ,
                    (int) nodeRegion.getBounds().getWidth(), 
                    (int) nodeRegion.getBounds().getBounds().getHeight() 
                ,0,0);
            
            
            g2d.setColor(new Color(200,214,193));
            g2d.fillRoundRect((int) ( nodeRegion.getBounds().getX() + nodeInsets.getLeft()), 
                    (int) (nodeRegion.getBounds().getY() + nodeInsets.getTop()),
                    (int) (nodeRegion.getBounds().getWidth() - nodeInsets.getLeft() - nodeInsets.getRight()), 
                    (int) (nodeRegion.getBounds().getBounds().getHeight() - nodeInsets.getTop()- nodeInsets.getBottom()),
                    0,0);
            g2d.setColor(new Color(194,237,254));
            
            g2d.drawRoundRect((int) ( nodeRegion.getBounds().getX() + nodeInsets.getLeft()), 
                    (int) (nodeRegion.getBounds().getY() + nodeInsets.getTop()),
                    (int) (nodeRegion.getBounds().getWidth() - nodeInsets.getLeft() - nodeInsets.getRight()), 
                    (int) (nodeRegion.getBounds().getBounds().getHeight() - nodeInsets.getTop()- nodeInsets.getBottom()),
                    0,0);             
        }
        
        this.drawChildren(g2d, layer);
        //g2d.setColor(Color.GRAY);        
        //g2d.drawRoundRect( (int) objectPosition.getX(), (int) objectPosition.getY(), oW, oH,0,0);        
    }
    
    public final Node2D setName(String name) { nodeName = name; return this;}
    
    public final String getName(){ return nodeName;}
    
    public final void addNode(Node2D go2d){
        this.nodeChildren.add(go2d);
        go2d.setParent(this);
    }
    
    public final void addNodes(Collection<? extends Node2D> nodes){
        for(Node2D n : nodes){
            this.nodeChildren.add(n);
            n.setParent(this);
        }
    }
    public int alignMode(){
        return this.ALIGN_MODE;
    }
    
    public void alignMode(int mode){
        this.ALIGN_MODE = mode;
    }
    
    public final void setPosition(double x, double y){
        this.nodeRegion.moveTo(x, y);
        //this.objectPosition.setLocation(x, y);
        //this.nodeRegion.set(x, y, nodeRegion.getWidth(),nodeRegion.getHeight());
    }
    
    public final void setPinPosition(double x, double y){
        this.pinPosition.setLocation(x, y);
        this.PIN_MODE = Node2D.PIN_CORNER_LEFT_TOP;
    }
    
    public final void setPinPosition(double x, double y, int pinMode){
        this.pinPosition.setLocation(x, y);
        this.PIN_MODE = pinMode;
    }
    
    public void setBounds(double x, double y, double w, double h){
        this.nodeRegion.set(x, y, w, h);
        this.coordinateTranslation.setRect(x, y, w, h);
    }
    
    public void setBoundsBind(double x, double y, double w, double h){
        this.nodeRegionBind.set(x, y, w, h);
        this.alignMode(Node2D.ALIGN_RELATIVE);
    }
    
    public Node2D setBackgroundColor(int r, int g, int b){
        this.nodeBackground = new Color(r,g,b);
        return this;
    }
    
    public Node2D setBackgroundColor(int r, int g, int b, int alpha){
        this.nodeBackground = new Color(r,g,b,alpha);
        return this;
    }
    
    public Node2D setBorderColor(int r, int g, int b){
        this.nodeBorder = new Color(r,g,b);
        return this;
    }
    
    public Node2D setBorderColor(int r, int g, int b, int alpha){
        this.nodeBorder = new Color(r,g,b,alpha);
        return this;
    }
    
    public Color getBackgroundColor(){ return this.nodeBackground;}
    public Color getBorderColor(){ return this.nodeBorder;}
    
    public void setTranslation(double x, double y, double w, double h){
        this.coordinateTranslation.setRect(x, y, w, h);        
    }    
    
    public Rectangle2D getTranslation(){ return this.coordinateTranslation;}
    
    public void setClickedPosition(double x, double y){        
        this.nodeClickedPosition.setLocation(this.getLocalX(x),this.getLocalY(y));
    }
    
    public final NodeRegion2D getBounds(){
        return this.nodeRegion;
    }
    
    public double getX(double x){
        return x + this.nodeRegion.getBounds().getX();
    }
    
    public double getY(double y){
        return y + this.nodeRegion.getBounds().getY();
    }
    
    public double transformX(double x){
        double startX = this.nodeInsets.getLeft();
        double      W = this.nodeRegion.getWidth() ;
               // - this.nodeInsets.getLeft() - this.nodeInsets.getRight();
        //double    startX = 
        double xf = (x - this.coordinateTranslation.getX())/
                coordinateTranslation.getWidth();
       /* System.out.printf("X = %12.5f %12.5f, %12.5f, %12.5f %12.5f %12.5f\n",x, xf, 
               coordinateTranslation.getX(), coordinateTranslation.getWidth(),
               nodeRegion.getWidth(),nodeRegion.getX() + xf*nodeRegion.getWidth()); 
        System.err.println("STARTX = " + startX + " WIDTH = " + W + "  INSET LEFT = " 
                + this.nodeInsets.getLeft());*/
        return startX + xf*W;
    }
    
    public double transformY(double y){
        double startY = this.nodeInsets.getTop();
        double      H = this.nodeRegion.getHeight();
// - this.nodeInsets.getTop() - this.nodeInsets.getBottom();
        double yf = (y - this.coordinateTranslation.getY())
                /coordinateTranslation.getHeight();
               /* System.out.printf("Y = %12.5f %12.5f, %12.5f, %12.5f %12.5f %12.5f\n", y, yf, 
               coordinateTranslation.getY(), coordinateTranslation.getHeight(),
               nodeRegion.getHeight(),nodeRegion.getY() + yf*nodeRegion.getHeight()
                );*/
        return startY + yf*H;
    }
    
    public double translateX(double x){
        double xcoord = 0.0;
        if(this.nodeParent!=null){
            xcoord = this.nodeParent.getBounds().getX();
        }
        return x + xcoord;
    }
    
    public double translateY(double y){
        double ycoord = 0.0;
        if(this.nodeParent!=null){
            ycoord = this.nodeParent.getBounds().getY();
        }
        return y + ycoord;
    }
    
    public double getScreenX(double x){
        return 0.0;
    }
    
    public double getLocalX(double screenX){ 
        NodeRegion2D region = getScreenBounds();
        //System.out.println(" SCREEN BOUNDS = " + region);
        //if(this.nodeParent==null) return screenX;
        //double x = nodeParent.getLocalX(screenX);
        return screenX - region.getX();
    }
    
    public double getLocalY(double screenY){ 
        NodeRegion2D region = getScreenBounds();
        //System.out.println(" SCREEN BOUNDS = " + region);
        //if(this.nodeParent==null) return screenX;
        //double x = nodeParent.getLocalX(screenX);
        return screenY - region.getY();
    }
    
    public NodeRegion2D getScreenBounds(){
        NodeRegion2D region = new NodeRegion2D();
        //System.out.println("called for " + getName() + "  number of children = " + this.nodeChildren.size() + " has parent " + (nodeParent!=null));
        if(this.nodeParent==null){
            region.set(nodeRegion.getX(),nodeRegion.getY(),nodeRegion.getWidth(),nodeRegion.getHeight());            
        } else {
            NodeRegion2D r = nodeParent.getScreenBounds();
            region.set(r.getX() + nodeRegion.getX(), r.getY() + nodeRegion.getY(), nodeRegion.getWidth(), nodeRegion.getHeight());
        }
        return region;
    }
    
    public void moveTo(double x, double y){
        
        double xm = x;
        double ym = y;
        
        if(xm<0) xm = 0.0;
        if(ym<0) ym = 0.0;
        
        if(nodeParent!=null){
            if(xm + getBounds().getWidth() > nodeParent.getBounds().getWidth()){
                xm = nodeParent.getBounds().getWidth() - getBounds().getWidth();
            }
            if(ym + getBounds().getHeight() > nodeParent.getBounds().getHeight()){
                ym = nodeParent.getBounds().getHeight() - getBounds().getHeight();
            }
        }
        
        if(xm<0) xm = 0.0;
        if(ym<0) ym = 0.0;
        this.nodeRegion.moveTo(xm, ym);
        
        //System.out.println(" AFTER MOVE " + nodeRegion);
    }
    
    public void canMove(boolean flag){
        this.canMove = flag;
    }
    
    public boolean canMove(){
        return this.canMove;
    }
    
    public NodeInsets  getInsets(){return this.nodeInsets;}
    
    public List<Node2D> getChildren(){return this.nodeChildren;}
    
    public Node2D  getClicked(int x, int y){
        
        //System.out.println(" get clicked " + getName() + "  " + x + " " + y);
        Node2D clickedNode = null;
        System.out.println("[Node 2D ] Examine root node : " + getName() + " --> " + getBounds());
        if(this.nodeChildren.isEmpty()==true){
            NodeRegion2D region = getScreenBounds();
            System.out.println(" X/Y = " + x + " / " + y + " -> " + region.toString());
            if(region.getBounds().contains(x, y)==true) return this;
        } else {
            for(Node2D node : this.nodeChildren){
                System.out.println("[Node 2D] examine child node = " + node.getName() + " ---> " + node.getBounds()
                + " contains = " + node.getClicked(x, y));
                Node2D clicked = node.getClicked(x, y);
                if(clicked!=null) return clicked;
            }
            NodeRegion2D region = getScreenBounds();
            if(region.getBounds().contains(x, y)==true) return this;
        }        
        return null;
    }
    
    public boolean isClicked(int x, int y){
        //this.objectPositionClicked.setLocation(x, y);
        return this.nodeRegion.getBounds().contains(x, y);
    }
    
    public void setMargins(double xmin, double ymin, double xmax, double ymax){
        
    }
    
    public void applyMouseDrag(int x, int y, int xmove, int ymove){
        
        //System.out.println("applying drag component " + getName());
        
        /*
        NodeRegion2D  region = this.getScreenBounds();
        
        double xpos = this.getLocalX(x);
        double ypos = this.getLocalY(y);
        System.out.println("DRAG : SCREEN BOUNDS : " + region);
        
        System.out.println(" X / Y " + x + " " + y + " //// " + xpos + " " + ypos);
        if(canMove()==true){
            this.moveTo(x, y);
        }
        
        System.out.println(" AFTER MOVE = " + this.nodeRegion);
        */
    }
    
    public void drawChildren(Graphics2D g2d, int layer){
        //System.out.println(" daring children of " + getName() +  " "  + getBounds() + "  SIZE = " + this.nodeChildren.size());
        for(Node2D object : nodeChildren){
            //System.out.println(" drawing child " + object.getName() + "  " + object.getBounds());
            //object.updateRegion(nodeRegion);
            object.drawLayer(g2d, layer);
        }
    }
    
    public void  setParent(Node2D parent){
        this.nodeParent = parent;
    }
    
    public Node2D  getParent(){
        return this.nodeParent;
    }
    
    public void updateDimensions(){
        
    }
    
    
    public void updateRegion( NodeRegion2D parentRegion){
        /*NodeInsets insets = getInsets();
        if(insets.relative()==false){
            this.nodeRegion.set( insets.getLeft(), insets.getTop(), 
                    parentRegion.getWidth() - insets.getLeft() - insets.getRight(), 
                    parentRegion.getHeight() - insets.getTop() - insets.getBottom());
        } */
        //System.out.printf("UPDATING REGION : %12s FROM -> %s (PARENT %s)\n",getName(),
        //        nodeRegion.toString(),parentRegion.toString());
        if(alignMode()==Node2D.ALIGN_INSETS){
            parentRegion.updateRegion(nodeRegion, nodeInsets);
            //System.out.println("INSETS");
        } else if (alignMode()==Node2D.ALIGN_RELATIVE){
            parentRegion.updateRegion(nodeRegion, nodeRegionBind, nodeInsets);
            //System.out.println(nodeRegion);
            //System.out.println(nodeRegionBind);
            //System.out.println(nodeInsets);
            //System.out.println("RELATIVE");
        } else if(alignMode()==Node2D.ALIGN_POSITION){
            this.setPosition(parentRegion.getX() + this.getBounds().getX(), parentRegion.getY() + this.getBounds().getY());
            // To-Do
        } else if(alignMode()==Node2D.PIN_CORNER_RIGTH_TOP){
            this.setPosition(parentRegion.getX() + parentRegion.getWidth() 
                    - this.getBounds().getWidth(), 
                    parentRegion.getY() + parentRegion.getHeight() - this.getBounds().getHeight());
        }
        
        //System.out.println(" PARENT REGION [" + this.getName() + "] = " + parentRegion.toString()
        //+ "  THIS = " + nodeRegion.toString());
        if(PIN_MODE==Node2D.PIN_CORNER_LEFT_TOP){
            //System.out.println("TOP LEFT PIN");
            this.nodeRegion.moveTo(pinPosition.getX(), pinPosition.getY());
        }
        
        if(PIN_MODE==Node2D.PIN_CORNER_RIGTH_TOP){
            //System.out.println("TOP RIGHT PIN");
            this.nodeRegion.moveTo(
                    parentRegion.getWidth() - pinPosition.getX() - nodeRegion.getWidth(),
                    pinPosition.getY());
        }
        
        for(Node2D node : this.nodeChildren){
            node.updateRegion(nodeRegion);
        }
        //System.out.printf("UPDATING REGION : %12s   TO -> %s (PARENT %s)\n",getName(),nodeRegion.toString(),
          //      parentRegion.toString());
    }
    
    public boolean mouseMoved(double X, double Y){
        return false;
    }
    
    public boolean mousePressed(double X, double Y){
        return false;
    }
    
    public void show(){
        String pname = "NONE";
        if(nodeParent!=null) pname = nodeParent.getName();
        System.out.println(String.format(" NODE = %12s , PARENT = %12s", getName(), pname));
        System.out.println("\tINSETS = " + this.nodeInsets.toString());
        System.out.println("\tREGION = " + this.nodeRegion);
        System.out.println("\tSCREEN = " + this.getScreenBounds());
        
        for(Node2D child : this.nodeChildren){
            child.show();
        }
    }
}
