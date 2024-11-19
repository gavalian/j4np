/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author gavalian
 */
public class AppWindow extends JFrame implements ActionListener {

    JSplitPane splitPane = null;
    
    
    public AppWindow(){
        this.initUI();
    }
    
    private void initUI(){
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200); 
        
        // Set the resize weight (0.0 to 1.0)
        splitPane.setResizeWeight(0.5); // Equal resizing for both sides

        // Enable one-touch expanding/collapsing
        splitPane.setOneTouchExpandable(true);
        this.add(this.splitPane);
    }
    
    public void addLeft(JPanel panel){
        this.splitPane.setLeftComponent(panel);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    public static void main(String[] args){
        StudioWindow.changeLook("DeepOcean");        
        JPanel panel = new JPanel();        
        AppWindow frame = new AppWindow();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setVisible(true);
        
        frame.addLeft(panel);
    }
}
