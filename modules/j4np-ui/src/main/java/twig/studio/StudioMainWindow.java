/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import twig.data.TDirectory;
import twig.graphics.TGDataCanvas;
import twig.graphics.TTabDataCanvas;

/**
 *
 * @author gavalian
 */
public class StudioMainWindow extends JFrame implements ActionListener {

    private StudioComponent      leftPane = null;
    private StudioComponent    bottomPane = null;
    
    private TTabDataCanvas studioCanvas = null;
    private JPanel            rightPane = null;
    private JToolBar            toolBar = null;
    private JMenu            studioMenu = null;
    
    private int Xsize = 900;
    private int Ysize = 700;        
    
    private TDirectory studioDir = new TDirectory();
    
    public StudioMainWindow(){
    
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {            
            @Override
            public void windowClosing(WindowEvent e) {confirmAndExit();}   
        });        
        StudioWindow.changeLook();
    }
    
    void confirmAndExit() {
        if (JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to quit?",
                "Please confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION
                ) {
            System.exit(0);
        }
    }
    
    public TDirectory     getDirectory(){ return studioDir;}
    public TGDataCanvas   canvas(){ return this.studioCanvas.activeCanvas();}
    public TTabDataCanvas view(){ return this.studioCanvas;}
    
    public void setLeftPane(StudioComponent lp){
        this.leftPane = lp;
        this.leftPane.addActionListener(this);
    }
    
    public void setBottomPane(StudioComponent bp){
        this.bottomPane = bp;
        //this.leftPane.addActionListener(this);
    }
    
    public void initialize(){
        this.setLayout(new BorderLayout());
        studioCanvas = new TTabDataCanvas(new String[]{"canvas"});
        this.add(studioCanvas,BorderLayout.CENTER);
        if(this.leftPane!=null){
            System.out.println(" main window : adding left panel...");
            this.add(leftPane,BorderLayout.LINE_START);
        }
        
        if(this.bottomPane!=null){
            System.out.println(" main window : adding bottom panel...");
            this.add(bottomPane,BorderLayout.PAGE_END);
        }
        this.pack();
        this.setSize(Xsize,Ysize);
    }
    
    public void addMenu(JMenu[] menu){
        JMenuBar menubar = new JMenuBar();
        for(int j = 0; j < menu.length; j++)  menubar.add(menu[j]);
        this.setJMenuBar(menubar);
    }
    
    public void showWindow(){
        this.initialize();
        this.setVisible(true);
    }
    
    
    protected void showDirectory(){
        StudioTreeView view = new StudioTreeView(studioDir);
        view.addActionListener(this);
        Xsize = this.getWidth();
        Ysize = this.getHeight();
        this.add(view,BorderLayout.LINE_START);
        this.pack();
        int width = view.getWidth();
        System.out.println("Width = " + width);
        this.setSize(Xsize+width, Ysize);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        System.out.println(" action was performed : " + e.getActionCommand() + "  id = "); 
        
        if(e.getSource() instanceof StudioTreeView){
            StudioTreeView view = (StudioTreeView) e.getSource();
            view.getTreeProvider().draw(e.getActionCommand(), this.studioCanvas.activeCanvas());
            this.studioCanvas.activeCanvas().repaint();
        }
        
        if(e.getActionCommand().compareTo("edit_showdirectory")==0){
            this.showDirectory();
        }
        
        if(e.getActionCommand().compareTo("edit_hidedirectory")==0){
            System.out.println("executing hyde");
            Component[] comps = this.getContentPane().getComponents();
            for(Component c : comps){ 
                System.out.println("class - " + c.getClass().getName());
                if(c instanceof StudioTreeView){
                    System.out.println("found it ...");
                    this.remove(c);
                }
            }
            this.pack();
            this.setSize(Xsize, Ysize);
        }
        
        if(e.getActionCommand().compareTo("file_open")==0){
            this.openFile();
        }
    }
    
    public void openFile(){
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Open file : " + file.getPath());
            this.studioDir.read(file.getPath());
            this.showDirectory();
        } else {
            System.out.println("Open command cancelled by user.");
        }
   
    }
    
    public static void main(String[] args){
        
        
        StudioMainWindow mw = new StudioMainWindow();
        
        JMenu[] menus = new JMenu[2];
        
        menus[0] = StudioTools.createMenu("File", mw, 
                new String[]{"New","Open","Save","-----","Quit"}, 
                new String[]{"file_new","file_open","file_save","-----","file_quit"});
        
        menus[1] = StudioTools.createMenu("Edit", mw, 
                new String[]{"Resize","Clear","-----","Show Directory","Hide Directory"}, 
                new String[]{"edit_resize","edit_clear","-----",
                    "edit_showdirectory","edit_hidedirectory"});
        
        mw.addMenu(menus);
        mw.initialize();
        mw.setVisible(true);
    }
}
