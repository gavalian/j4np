/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.studio;

import twig.studio.StudioFrame;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
        


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author gavalian
 */
public class StudioWindow extends JFrame implements ActionListener {
    
    StudioFrame sFrame = null;
    JMenuItem   inspectorMenuItem = null;
    JPanel panel4 = null;
    
    public StudioWindow(){
        super();
        
        initUI();
        initMenuBar();
        initToolBar();
    }
    
    private void initToolBar(){
        
        JToolBar toolBar = new JToolBar();
        
        URL imageURL = StudioWindow.class.getResource("bld_open.png");
        System.out.printf("path : %s\n",imageURL.getPath());
        JButton button = new JButton();
        button.setIcon(new ImageIcon(imageURL, "OPEN"));
        toolBar.add(button);
        toolBar.setBorder(BorderFactory.createEtchedBorder());
        add(toolBar, BorderLayout.PAGE_START);        
    }
    
     
    private void initMenuBar(){
        
        JMenuBar   menuBar = new JMenuBar();
        
        JMenu      menuEdit = new JMenu("Edit");
        inspectorMenuItem = new JMenuItem("Show Inspector");
        inspectorMenuItem.addActionListener(this);
        menuEdit.add(inspectorMenuItem);
        
        menuBar.add(menuEdit);
        
        JMenu  menuTheme = new JMenu("Theme");
        JMenuItem tOne   = new JMenuItem("Flat Look Ligth");
        JMenuItem tTwo   = new JMenuItem("Solarized Ligth");
        JMenuItem tThree = new JMenuItem("Solarized Dark");
        
        tOne.addActionListener(this);
        tTwo.addActionListener(this);
        tThree.addActionListener(this);
        
        menuTheme.add(tOne);
        menuTheme.add(tTwo);
        menuTheme.add(tThree);
        
        menuBar.add(menuTheme);
        this.setJMenuBar(menuBar);
    }
    private void initUI(){
        this.setLayout(new BorderLayout());
        
        sFrame = new StudioFrame();
        this.add(sFrame,BorderLayout.CENTER);
        JPanel panel = this.createInspector();
        //this.add(panel,BorderLayout.LINE_END);
        //this.pack();
        
        panel4 = createInspector();
        this.add(panel4,BorderLayout.LINE_END);
        panel4.setVisible(false);
        
    }
    
    public StudioFrame getStudioFrame(){ return this.sFrame;}
    
    
    public static void changeLook(String name){
        if(name.compareTo("Flat Light")==0){
            UIManager.put("Tree.paintLines", Boolean.TRUE);
            try {
                UIManager.setLookAndFeel( new FlatLightLaf() );
                //SwingUtilities.updateComponentTreeUI(this);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(StudioWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(name.compareTo("Arc Dark")==0){
            UIManager.put("Tree.paintLines", Boolean.TRUE);
            try {
                UIManager.setLookAndFeel( new FlatArcDarkOrangeIJTheme() );
                //SwingUtilities.updateComponentTreeUI(this);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(StudioWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void changeLook(){
        //com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme.install();
        UIManager.put("Tree.paintLines", Boolean.TRUE);
        //FlatLightLaf.install();            
        /*try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }*/
        
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );                
            //UIManager.setLookAndFeel( new  FlatSolarizedLightIJTheme());
            //UIManager.setLookAndFeel( new  FlatGradiantoNatureGreenIJTheme());
            //UIManager.setLookAndFeel( new  FlatHiberbeeDarkIJTheme());
            
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
    }
    
    public JPanel createInspector(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEtchedBorder());
        for(int i = 11; i < 25; i++){
            JButton button = new JButton("Inspector Button " + i);
            panel.add(button);
        }
        return panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        
        if(arg0.getActionCommand().compareTo("Flat Look Ligth")==0){
            try {
                UIManager.setLookAndFeel( new FlatLightLaf() );
                SwingUtilities.updateComponentTreeUI(this);
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
        }
        
        if(arg0.getActionCommand().compareTo("Solarized Ligth")==0){
            System.out.println("change to solarized");
            try {
                UIManager.setLookAndFeel( new FlatSolarizedLightIJTheme() );
                SwingUtilities.updateComponentTreeUI(this);
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
        }
        
        if(arg0.getActionCommand().compareTo("Solarized Dark")==0){
            System.out.println("change to solarized");
            try {
                UIManager.setLookAndFeel( new FlatSolarizedDarkIJTheme() );
                SwingUtilities.updateComponentTreeUI(this);
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
        }
        
        if(arg0.getActionCommand().compareTo("Show Inspector")==0){
            System.out.println("---> show inspector");
            /*panel4 = createInspector();
            this.add(panel4,BorderLayout.LINE_END);*/
            Dimension dim = this.getSize();
            this.repaint();
            this.pack();
            this.setSize(dim);
            //this.setSize(800, 500);*/
            panel4.setVisible(true);
            inspectorMenuItem.setText("Hide Inspector");
        }
        
        if(arg0.getActionCommand().compareTo("Hide Inspector")==0){
            System.out.println("---> hiding inspector");
            /*BorderLayout layout = (BorderLayout) this.getLayout();
            this.remove(panel4);;//layout.getLayoutComponent(BorderLayout.LINE_END));
            Dimension dim = this.getSize();
            this.repaint();
            this.pack();*/
            //this.setSize(800, 500);
            //this.setSize(dim);
            panel4.setVisible(false);
            inspectorMenuItem.setText("Show Inspector");
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args){
        
        StudioWindow.changeLook();
        
        StudioWindow frame = new StudioWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setVisible(true);
    }

    
}
