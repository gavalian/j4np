/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.graphics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import twig.config.TStyle;
import twig.data.DataSet;
import twig.studio.StudioWindow;
import twig.studio.TwigStudio;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class TGCanvas extends JFrame implements ActionListener {
    
    protected TGDataCanvas dataCanvas = null;
    
    private int CANVAS_DEFAULT_WIDTH  = 600;
    private int CANVAS_DEFAULT_HEIGHT = 500;
    
    private JPanel     canvasPane = null;

    private StatusPane      statusPane = null;
    private String     dataCanvasTitle = "canvas";

    public TGCanvas(){
        StudioWindow.changeLook();
        dataCanvasTitle = "canvas1";
        initUI(true);
    }

    public TGCanvas(int xsize, int ysize){
        StudioWindow.changeLook();
        dataCanvasTitle = "canvas1";
        CANVAS_DEFAULT_WIDTH  = xsize;
        CANVAS_DEFAULT_HEIGHT = ysize;
        initUI(true);
    }
    
    public TGCanvas(String name, int xsize, int ysize){
        StudioWindow.changeLook();
        CANVAS_DEFAULT_WIDTH  = xsize;
        CANVAS_DEFAULT_HEIGHT = ysize;  
        dataCanvasTitle = name;
        this.setTitle(name);
        initUI(true);
    }
    
    public TGCanvas(String name, int xsize, int ysize, boolean exitOnClose){
        StudioWindow.changeLook();
        CANVAS_DEFAULT_WIDTH  = xsize;
        CANVAS_DEFAULT_HEIGHT = ysize;  
        dataCanvasTitle = name;
        this.setTitle(name);
        initUI(exitOnClose);
    }
    
    private void initUI(boolean closeOnExit){
        
        if(closeOnExit==true) setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar menuBar = this.createMenuBar();
        setJMenuBar(menuBar);
        
        canvasPane = new JPanel();
        canvasPane.setLayout(new BorderLayout());
        dataCanvas = new TGDataCanvas();
        dataCanvas.setSize(this.CANVAS_DEFAULT_WIDTH, this.CANVAS_DEFAULT_HEIGHT);
        dataCanvas.divide(1, 1);
        statusPane = new StatusPane();
        canvasPane.add(dataCanvas,BorderLayout.CENTER);
        canvasPane.add(statusPane,BorderLayout.PAGE_END);
        
        this.add(canvasPane);
        
        setSize(this.CANVAS_DEFAULT_WIDTH, this.CANVAS_DEFAULT_HEIGHT);
        
        this.pack();
        setSize(this.CANVAS_DEFAULT_WIDTH, this.CANVAS_DEFAULT_HEIGHT);
        this.setVisible(true);
    }
    
    public TGDataCanvas view(){ return dataCanvas;}

    public TGRegion region(int index) { return dataCanvas.region(index);}
    
     
    
    private JMenu createMenu(String menuName, String[] items){
        JMenu uMenu = new JMenu(menuName);
        for(int i = 0; i < items.length; i++){
            JMenuItem item = new JMenuItem(items[i]);
            item.addActionListener(this);
            uMenu.add(item);
        }
        return uMenu;
    }
    
    private JMenu createMenu(String menuName, String[] items, String[] commands){
        JMenu uMenu = new JMenu(menuName);
        for(int i = 0; i < items.length; i++){
            if(items[i].compareTo("-")==0){
                uMenu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(items[i]);
                item.setActionCommand(commands[i]);
                item.addActionListener(this);
                uMenu.add(item);
            }
        }
        return uMenu;
    }
    
    private JMenuBar createMenuBar(){
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu  fileMenu = new JMenu("File");
        JMenu themeMenu = new JMenu("Theme");
        JMenu  editMenu = new JMenu("Edit");
        JMenu  fileHelp = new JMenu("Help");
        
        JMenu saveMenu = new JMenu("Save...");
        
        JMenuItem savePDF = new JMenuItem("Export PDF");
        JMenuItem saveEPS = new JMenuItem("Export EPS");
        JMenuItem saveSVG = new JMenuItem("Export SVG");
        
        JMenuItem savePDFFree = new JMenuItem("Export PDF free Hep");
        
        
        JMenuItem theme_SL = new JMenuItem("Solarized Light");
        JMenuItem theme_SD = new JMenuItem("Solarized Dark");
        JMenuItem theme_DL = new JMenuItem("Default Light");
        theme_SL.addActionListener(this);
        theme_DL.addActionListener(this);
        theme_SD.addActionListener(this);
        themeMenu.add(theme_DL);
        themeMenu.add(theme_SL);
        themeMenu.add(theme_SD);
        JMenu paletteMenu = this.createMenu("Color Palette", 
                new String[]{"Gold","Tab","Set","Bright"},new String[]{"gold10","tab10", "set1", "bright10"}
        );
        
        themeMenu.addSeparator();
        themeMenu.add(paletteMenu);
        
        JMenu resizeMenu = this.createMenu("Resize", 
                new String[]{"600x500","800x400","500x900","Custom"}
        );
        
        JMenu divideMenu = this.createMenu("Divide",
                new String[]{"1x1","2x2","3x3","3x2","2x3" , "-","Custom"},
                new String[]{"divide_1x1","divide_2x2","divide_3x3",
                    "divide_3x2","divide_2x3","-","divide_Custom"}
        );
        
        
       
        
        editMenu.add(resizeMenu);
        editMenu.add(divideMenu);
        
        JMenuItem colAndLine = new JMenuItem("Colors and Lines");
        JMenuItem colAndLineDark = new JMenuItem("Dark Mode");
        
        colAndLine.addActionListener(this);
        colAndLineDark.addActionListener(this);
        fileHelp.add(colAndLine);
        fileHelp.add(colAndLineDark);
        
        saveMenu.add(savePDF);
        saveMenu.add(saveEPS);
        saveMenu.add(saveSVG);
        saveMenu.add(savePDFFree);
        savePDF.addActionListener(this);
        savePDFFree.addActionListener(this);
        saveEPS.addActionListener(this);
        saveSVG.addActionListener(this);
        fileMenu.add(saveMenu);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(themeMenu);
        menuBar.add(fileHelp);
        return menuBar;
    }
    public TGCanvas cd(int pad){ this.view().cd(pad); return this;}
    
    public TGCanvas draw(String data){ return this.draw(data, "");}
    
    public TGCanvas draw(String data, String option){
        if(data.contains("/")==false){
            DataSet ds = TwigStudio.getInstance().dir().get("/studio",data);
            this.draw(ds, option);
        }
        return this;
    }
    public TGCanvas draw(DataSet ds,String option){ 
        this.view().region().draw(ds, option); this.repaint();
        return this;
    }
    
    public TGCanvas draw(DataSet ds){ return this.draw(ds, "");}
    
    public TGRegion region(){return this.view().region();}
    
    public TGCanvas draw(Widget w){
        this.view().region().draw(w);
        return this;
    }
    
    @Override
    public void actionPerformed(ActionEvent aev) {
        String ac = aev.getActionCommand();
        
        if(aev.getActionCommand().compareTo("600x500")==0){
            this.setSize(600, 500);
            //this.pack();
        }        
        if(aev.getActionCommand().compareTo("800x400")==0){
            this.setSize(800, 400);
            //this.pack();
        }
        if(aev.getActionCommand().compareTo("500x900")==0){
            this.setSize(500, 900);
            //this.pack();
        }
        
        if(aev.getActionCommand().compareTo("Custom")==0){
            JTextField sizeX = new JTextField("600");
            JTextField sizeY = new JTextField("500");
            Object[] message = {
                        "Width :", sizeX,
                        "Height :", sizeY
                    };
                    
                    int option = JOptionPane.showConfirmDialog(null, 
                            message, "Canvas Size", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        this.setSize(Integer.parseInt(sizeX.getText())
                                , Integer.parseInt(sizeY.getText()));                        
                    }
            //this.pack();
        }
        /**
         * divide canvas
         */
        if(ac.startsWith("divide_")==true){
            if(ac.startsWith("divide_1x1")) dataCanvas.divide(1, 1);
            if(ac.startsWith("divide_2x2")) dataCanvas.divide(2, 2);
            if(ac.startsWith("divide_3x3")) dataCanvas.divide(3, 3);
            if(ac.startsWith("divide_2x3")) dataCanvas.divide(2, 3);
            if(ac.startsWith("divide_3x2")) dataCanvas.divide(3, 2);            
        }
        if(ac.compareTo("Export PDF")==0){
            //dataCanvas.save(this.dataCanvasTitle+".pdf");
            dataCanvas.export(this.dataCanvasTitle+".pdf","PDF");
        }
        
        if(ac.compareTo("Colors and Lines")==0){
            TGCanvas c = new TGCanvas("canvasStyles",900,700,false);
            TGStyleFactory.markersAndColors(c.view());
        }
        
        if(ac.compareTo("Dark Mode")==0){
            TGCanvas c = new TGCanvas("canvasStyles",900,700,false);
            TGStyleFactory.darkModeShow(c.view());
        }
        
        if(ac.compareTo("tab10")==0){
            TStyle.getInstance().getPalette().init("tab10");            
        }
        if(ac.compareTo("gold10")==0){
            TStyle.getInstance().getPalette().init("gold10");            
        }
        if(ac.compareTo("bright10")==0){
            TStyle.getInstance().getPalette().init("bright10");
        }
        if(ac.compareTo("set1")==0){
            TStyle.getInstance().getPalette().init("set1");
        }
    }
    
    public static void main(String[] args){
        TGCanvas c = new TGCanvas();
    }
}
