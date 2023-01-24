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
import j4np.utils.io.OptionStore;
        


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import twig.data.TDirectory;
import twig.data.TGroupDirectory;
import twig.tree.HipoTree;

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
    
     
    private JMenu getMenu(String name, String[] names, String[] actions){
        JMenu menu = new JMenu(name);
        for(int i = 0; i < names.length; i++){
            if(names[i].compareTo("-")==0){
                menu.add(new JSeparator());
            } else {
                JMenuItem item = new JMenuItem(names[i]);
                item.setActionCommand(actions[i]);
                item.addActionListener(this);    
                menu.add(item);
            }
        }
        return menu;
    }
    
    private JMenu getMenu(String name, String[] names){
        return getMenu(name, names, names);
    }
    
    private void initMenuBar(){
        
        JMenuBar   menuBar = new JMenuBar();
        
        JMenu menuFile = this.getMenu("File", 
                new String[]{"Open...","-","Open Twig",
                "Open Hipo","Open Group", "-","Import Text","Import h5"});
        
        JMenu menuView = this.getMenu("View", 
                new String[]{"New Canvas"});
        
        JMenu menuEdit = this.getMenu("Edit", 
                new String[]{"Configure","-","Edit Cuts","Set Bins"});
                
        
        JMenu menuPlugins = this.getMenu("Plugins", 
                new String[]{"H(e,e-)X",
                    "H(e,e-,\u03C0+)X", 
                    "H(e,e-\u03C0+\u03C0-)X","H(\u03B3,\u03C0+,\u03C0-p)X"},
                new String[]{"inclusive_e", "inclusive_epip",
                "inclusive_epippim" ,"inclusive_photo"});
        /*
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
        
        menuBar.add(menuTheme);*/
        
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuView);
        menuBar.add(menuPlugins);
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
        
        if(arg0.getActionCommand().compareTo("Open Hipo")==0){
            HipoTree ht = new HipoTree();
            ht.configure();
            this.sFrame.setTreeProvider(ht);
        }
        
        if(arg0.getActionCommand().compareTo("New Canvas")==0){            
            this.sFrame.addCanvas();
        }
        
        if(arg0.getActionCommand().compareTo("Open Twig")==0){
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Twig Files (.twig)","twig"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Hipo Files (.h5)","h5"));
            
            File workingDirectory = new File(System.getProperty("user.dir"));
            fc.setCurrentDirectory(workingDirectory);

            int returnVal = fc.showOpenDialog(this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("file -> " + file.getAbsolutePath());
                TDirectory dir = new TDirectory();
                dir.read(file.getAbsolutePath());
                sFrame.setTreeProvider(dir);
            } else {
                
            }
 
        }
        
        if(arg0.getActionCommand().compareTo("Open Group")==0){
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Twig Files (.twig)","twig"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Hipo Files (.h5)","h5"));
            
            File workingDirectory = new File(System.getProperty("user.dir"));
            fc.setCurrentDirectory(workingDirectory);

            int returnVal = fc.showOpenDialog(this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("file -> " + file.getAbsolutePath());
                TGroupDirectory dir = new TGroupDirectory();
                dir.read(file.getAbsolutePath());
                sFrame.setTreeProvider(dir);
            } else {
                
            }
 
        }
        
        if(arg0.getActionCommand().compareTo("Configure")==0){
            sFrame.getTreeProvider().configure();
        }
        
        if(arg0.getActionCommand().compareTo("Edit Cuts")==0){
            String cutString = JOptionPane.showInputDialog(this, "What's your name?");
            String      name = "default";
            // get the user's input. note that if they press Cancel, 'name' will be null
            System.out.printf("The user's name is '%s'.\n", cutString);
            this.sFrame.getTreeProvider().execute("addcut/" + name +"/"+cutString);
            this.sFrame.getStatusPane().setTextCenter(cutString);
            
        }
        
        if(arg0.getActionCommand().compareTo("Set Bins")==0){
            String cutString = JOptionPane.showInputDialog(this, "What should default bins be ?");
            // get the user's input. note that if they press Cancel, 'name' will be null
            System.out.printf("The user's name is '%s'.\n", cutString);
            this.sFrame.getTreeProvider().execute("defaultbins/" + cutString);
            this.sFrame.getStatusPane().setTextLeft(cutString);
            
        }
        if(arg0.getActionCommand().compareTo("inclusive_e")==0){
            //sFrame.getTreeProvider().configure();
            sFrame.setTreeProvider("j4np.physics.PhysicsReaction");
        }
        if(arg0.getActionCommand().compareTo("inclusive_epip")==0){
            //sFrame.getTreeProvider().configure();
            sFrame.setTreeProvider("j4np.physics.store.InclusiveE1pion");
        }
        if(arg0.getActionCommand().compareTo("inclusive_epippim")==0){
            //sFrame.getTreeProvider().configure();
            sFrame.setTreeProvider("j4np.physics.store.InclusiveE2pions");
        }
        if(arg0.getActionCommand().compareTo("inclusive_photo")==0){
            //sFrame.getTreeProvider().configure();
            sFrame.setTreeProvider("j4np.physics.store.PhotoProductionClas6");
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args){
        StudioWindow.changeLook();        
        StudioWindow frame = new StudioWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setVisible(true);
    }
}
