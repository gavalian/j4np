/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import twig.data.H1F;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class GraphicsDebug extends JPanel {
    
    public GraphicsDebug(){
        super();
        init();
    }
    
    public void init(){
        JButton button = new JButton("Marker Configuration");
        //button.setBorderPainted(false);
        //button.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.yellow));
                //BorderFactory.createStrokeBorder(new BasicStroke(1)));
        //        button.setBounds(15, 5, 5, 5);
        button.setText("Round");
        button.putClientProperty("JButton.buttonType", "roundRect");
        this.setLayout(new BorderLayout());
        this.add(button,BorderLayout.PAGE_START);
    }
    
    
    public static void timerDebug(){
        JFrame frame = new JFrame( "Canvas" );
        TGDataCanvas c = new TGDataCanvas();
        
        frame.add(c);
        frame.setSize(600, 600);
        frame.setVisible(true);
        
        c.initTimer(600);        
        H1F h = new H1F("h",100,-3.0,3.0);
        
        c.region().draw(h);
        Random r = new Random();
        while(true){
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(GraphicsDebug.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(int k =0; k < 10; k++) h.fill(r.nextGaussian());            
        }
    }
    
    public static void main(String[] args){
        GraphicsDebug.timerDebug();
        /*
        StudioWindow.changeLook();
       JFrame frame = new JFrame( "Canvas" );
       GraphicsDebug gd = new GraphicsDebug();
       
       frame.add(gd);
       frame.setSize(400, 400);
       frame.setVisible(true);
        */
    }
}
