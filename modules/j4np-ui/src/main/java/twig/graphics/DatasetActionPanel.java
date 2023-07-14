/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author gavalian
 */
public class DatasetActionPanel extends JDialog {
    
    TTabDataCanvas tc = null;
    
    JPanel         actionComponent = null;
    Frame          parentFrame = null;
    public   int    X_SIZE = 500;
    public int      Y_SIZE = 600;
    public  String dialogTitle = "dataset-action";
    
    public DatasetActionPanel(Frame parent){
        super(parent,false);
        parentFrame = parent;
    }
    protected void setActionComponent(JPanel panel){
        this.actionComponent = panel;
    }
    
    protected TTabDataCanvas getCanvas(){ return this.tc;}

    protected void initUI(String[] canvases){
         setLayout(new BorderLayout());
        
        tc = new TTabDataCanvas(canvases);
        this.add(tc,BorderLayout.CENTER);
        if(actionComponent!=null) {
            JPanel contextPane = new JPanel();
            contextPane.setLayout(new BorderLayout());
            contextPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            contextPane.add(actionComponent,BorderLayout.CENTER);
            this.add(contextPane,BorderLayout.PAGE_END);
        }
    }
    
    
    
    public void showDialog(){
        this.setLocationRelativeTo(this.getParent());
        this.setTitle(dialogTitle);
        this.pack();
        this.setSize(X_SIZE, Y_SIZE);
        this.setVisible(true);
    }
}
