/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class TTabCanvas extends JFrame implements ActionListener  {
    
    protected TTabDataCanvas dataCanvas = null;
    private JPanel     canvasPane = null;
    private int CANVAS_DEFAULT_WIDTH  = 600;
    private int CANVAS_DEFAULT_HEIGHT = 500;
    
    public TTabCanvas(){
        StudioWindow.changeLook();
        this.initUI(false);
    }
    
    public TTabCanvas(int xsize, int ysize){
        StudioWindow.changeLook();
        this.CANVAS_DEFAULT_HEIGHT = ysize;
        this.CANVAS_DEFAULT_WIDTH  = xsize;
        this.initUI(false);
    }
    
    public TTabDataCanvas getDataCanvas(){return this.dataCanvas;}
    
    private void initUI(boolean closeOnExit){
        
        if(closeOnExit==true) setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //JMenuBar menuBar = this.createMenuBar();
        //setJMenuBar(menuBar);
         canvasPane = new JPanel();
        canvasPane.setLayout(new BorderLayout());
        dataCanvas = new TTabDataCanvas();
        dataCanvas.setSize(this.CANVAS_DEFAULT_WIDTH, this.CANVAS_DEFAULT_HEIGHT);
        
        
        canvasPane.add(dataCanvas,BorderLayout.CENTER);
        
        this.add(canvasPane);
        
        setSize(this.CANVAS_DEFAULT_WIDTH, this.CANVAS_DEFAULT_HEIGHT);
        
        this.pack();
        setSize(this.CANVAS_DEFAULT_WIDTH, this.CANVAS_DEFAULT_HEIGHT);
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
}
