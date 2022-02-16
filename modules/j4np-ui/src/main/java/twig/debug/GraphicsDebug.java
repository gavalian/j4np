/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
    
    public static void main(String[] args){
        
        StudioWindow.changeLook();
       JFrame frame = new JFrame( "Canvas" );
       GraphicsDebug gd = new GraphicsDebug();
       
       frame.add(gd);
       frame.setSize(400, 400);
       frame.setVisible(true);
    }
}
