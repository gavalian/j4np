/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import twig.graphics.TTabDataCanvas;

/**
 *
 * @author gavalian
 */
public class StudioMainWindow extends JFrame implements ActionListener {

    private StudioComponent   leftPane = null;
    private JPanel  rightPane = null;
    private JToolBar  toolBar = null;
    private TTabDataCanvas studioCanvas = null;
    
    public StudioMainWindow(){
        StudioWindow.changeLook();
    }
    
    public void setLeftPane(StudioComponent lp){
        this.leftPane = lp;
        this.leftPane.addActionListener(this);
    }
    
    public void initialize(){
        this.setLayout(new BorderLayout());
        studioCanvas = new TTabDataCanvas(new String[]{"canvas"});
        this.add(studioCanvas,BorderLayout.CENTER);
        if(this.leftPane!=null){
            System.out.println(" main window : adding left panel...");
            this.add(leftPane,BorderLayout.LINE_START);
        }
        this.pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(" action was performed : " + e.getActionCommand());
        
        if(e.getSource() instanceof StudioTreeView){
            StudioTreeView view = (StudioTreeView) e.getSource();
            view.getTreeProvider().draw(e.getActionCommand(), this.studioCanvas.activeCanvas());
            this.studioCanvas.activeCanvas().repaint();
        }

    }
    
}
