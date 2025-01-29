/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.filters;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author gavalian
 */
public class GaussianDemo extends JPanel {
    private BufferedImage image;
    private BufferedImage imageRed;
    private BufferedImage imageBlur;
    private BufferedImage imageBlurRed;
    private GaussianFilter filter = new GaussianFilter(7);
    private Random r = new Random();
    private Timer            updateTimer = null;
    List<GraphNode>    nodes = new ArrayList<>();
    List<TrackNodes>   tracks = null;
    
    long    updateCounter = 0L;
    
    public GaussianDemo(){
        image = getImage(30,30,10,Color.ORANGE);
        imageRed = getImage(30,30,10,Color.RED);
        imageBlur = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        imageBlurRed = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                 repaint();
                /*for(int i = 0; i < canvasPads.size();i++){
     Timer               System.out.println("PAD = " + i);
                    canvasPads.get(i).show();
                }*/
            }
        };
        updateTimer = new Timer("EmbeddeCanvasTimer");
        updateTimer.scheduleAtFixedRate(timerTask, 30, 10);
    
    }
    
    
    public void makeBlur(){        
        filter.filter(image, imageBlur);
        filter.filter(imageRed, imageBlurRed);
    }
    
    public static BufferedImage getImage(int width, int height, int radius, Color col){
         BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clear the image by filling it with a fully transparent color
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);

        // Set the composite mode back to source-over for drawing
        g2d.setComposite(AlphaComposite.SrcOver);

        // Set the color to black
        g2d.setColor(col);

        // Calculate the circle's position and size
        int circleDiameter = radius;
        int circleX = (width - circleDiameter) / 2;
        int circleY = (height - circleDiameter) / 2;

        // Draw the filled black circle
        g2d.fillOval(circleX, circleY, circleDiameter, circleDiameter);

        // Dispose of the Graphics2D object to release resources
        g2d.dispose();
        return image;
    }
   
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = this.getWidth();
        int h = this.getHeight();
        
        Rectangle2D boundary = new Rectangle2D.Double(0,0,w,h);
        if(nodes.isEmpty()){
            for(int i = 0; i < 420; i++){
                int x = r.nextInt(w-40)+10;
                int y = r.nextInt(h-40)+10;
                nodes.add(new GraphNode(x,y,r.nextDouble()*2-1,r.nextDouble()*2-1));
            }
            this.tracks = TrackNodes.generate(3, r, nodes.size());
        }
        
        updateCounter++;
        
        if(updateCounter%1500==0){
            int n = r.nextInt(3)+2;
            this.tracks = TrackNodes.generate(n, r, nodes.size());
            //System.out.println(" tracs size = " + tracks.size());
        }
        g2d.setColor(new Color(42,56,72));
        g2d.fillRect(0, 0, w, h);
        g2d.setStroke(new BasicStroke(0.1f));
        //System.out.printf("%d %d\n",w,h);
        g2d.setColor(Color.ORANGE);
        if(imageBlur!=null){
            int ox = 0;
            int oy = 0;
            for(int i = 0; i < nodes.size(); i++){
                int x = (int) nodes.get(i).position.getX();
                int y = (int) nodes.get(i).position.getY();
                if(i!=0) g2d.drawLine(x+15, y+15, ox+15, oy+15);
                g.drawImage(imageBlur, (int) nodes.get(i).position.getX(), 
                        (int) nodes.get(i).position.getY(), this);
                ox = x;
                oy = y;
                
            }
            /*
            for(int i = 0; i < 120; i++){
                int x = r.nextInt(w-40)+10;
                int y = r.nextInt(h-40)+10;
                if(i!=0) g2d.drawLine(x+15, y+15, ox+15, oy+15);
                g.drawImage(imageBlur, x, y, this);
                ox = x;
                oy = y;
            }*/
        }

        for(int i = 0; i < tracks.size(); i++){
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(0.4f));
            for(int j = 1; j < 6; j++){
                GraphNode np = nodes.get(tracks.get(i).index[j-1]);
                GraphNode nn = nodes.get(tracks.get(i).index[j]);
                g2d.drawLine((int) (np.position.getX()+15),(int) (np.position.getY()+15),
                        (int)(nn.position.getX()+15),(int)(nn.position.getY()+15));
                g.drawImage(imageBlurRed, (int) nn.position.getX(), 
                        (int) nn.position.getY(), this);
                
            }
        }
        for(GraphNode node : nodes) node.update(boundary);
        //if (image != null) {
        //    g.drawImage(image, 0, 0, this);
        //}
    }
    
    public static class TrackNodes {
        int[] index = new int[6];
        public TrackNodes(Random r, int count){
            for(int i = 0; i < 6; i++){
                index[i] = r.nextInt(count);
            }
        }
        
        public static List<TrackNodes> generate(int n, Random r, int count){
            List<TrackNodes> list = new ArrayList<>();
            for(int i = 0; i < n; i++) list.add(new TrackNodes(r,count));
            return list;
        }
        
    }
    public static class GraphNode {
        Point2D position = new Point2D.Double();
        Point2D velocity = new Point2D.Double();
        public GraphNode(double x, double y, double vx, double vy){
            position.setLocation(x, y);
            velocity.setLocation(vx,vy);
        }
        
        public void update(Rectangle2D boundary){
            double newX = position.getX() + velocity.getX();
            double newY = position.getY() + velocity.getY();
            if(newX<0||newX>boundary.getWidth()-30){ velocity.setLocation(-velocity.getX(), velocity.getY());}
            if(newY<0||newY>boundary.getHeight()-30){ velocity.setLocation(velocity.getX(), -velocity.getY());}
            position.setLocation(newX, newY);
        }
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Transparent Circle Image");
        GaussianDemo panel = new GaussianDemo();
        long then = System.currentTimeMillis();
        for(int j = 0; j < 100; j++){
            panel.makeBlur();
        }
        long now = System.currentTimeMillis();
        System.out.printf("time = %d -> %f\n",now-then, ((double) (now-then))/100.0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 100);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
