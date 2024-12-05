/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author gavalian
 */
public class AppWindow extends JFrame implements ActionListener {

    JSplitPane splitPane = null;
    JSplitPane splitVertical  = null;
    
    
    public AppWindow(){
        this.initUI();
    }
    
    private void initUI(){
        splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setDividerLocation(500);
        splitVertical.setResizeWeight(0.8); // Equal resizing for both sides
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(60); 
        

        // Enable one-touch expanding/collapsing
        //splitPane.setOneTouchExpandable(true);
        // Set the resize weight (0.0 to 1.0)
        splitPane.setResizeWeight(0.1); // Equal resizing for both sides

        // Enable one-touch expanding/collapsing
        //splitPane.setOneTouchExpandable(true);
        
        splitVertical.setTopComponent(this.splitPane);
        this.add(this.splitVertical);
    }
    
    public Component getLeft(){
        return this.splitPane.getLeftComponent();
    }
    public Component getRight(){
        return this.splitPane.getRightComponent();
    }
    
    public void addLeft(JPanel panel){
        this.splitPane.setLeftComponent(panel);
    }
    
    public void addBottom(JPanel panel){
        this.splitVertical.setBottomComponent(panel);
    }
    public void addRight(JPanel panel){
        this.splitPane.setRightComponent(panel);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    public static void main(String[] args){
        //StudioWindow.changeLook("DeepOcean");        
        StudioWindow.changeLook();        
        JPanel panel = new JPanel();        
        AppWindow frame = new AppWindow();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setVisible(true);
        
        frame.addLeft(panel);
    }
}
