/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.demo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import twig.graphics.TGDataCanvas;
import twig.studio.StudioWindow;

/**
 * Demo class for showcasing different types of plots that can be made 
 * with Twig data visualization software. 
 * @author gavalian
 */

public class TwigDemoCanvas extends JFrame {
    public List<TwigDemo> demos = new ArrayList<>();
    
    public TwigDemoCanvas(){
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    JTabbedPane tabbedPane = null; //new JTabbedPane();
    public TwigDemoCanvas addDemo(TwigDemo d){
        this.demos.add(d); return this;
    }
    
    public void showFrame(){
        pack();
        setSize(1200, 800);
        setVisible(true);
    }
    
    public void initUI(){
        tabbedPane = new JTabbedPane();
        for(int i = 0; i < this.demos.size(); i++){
            JTabbedPane demoPane = new JTabbedPane();
            TGDataCanvas c = new TGDataCanvas();
            demoPane.add("canvas", c);
            demos.get(i).drawOnCanvas(c);
            tabbedPane.add(demos.get(i).getName(),demoPane);
            JTextArea textArea = new JTextArea();
            textArea.setText(demos.get(i).getCode());
            JScrollPane scrollPane = new JScrollPane(textArea); 
            textArea.setEditable(false);
            demoPane.add("code", scrollPane);
        }
        this.add(tabbedPane);
    }
    
    public static void main(String[] args){
        StudioWindow.changeLook();
        TwigDemoCanvas td = new TwigDemoCanvas();
        td.addDemo(new HistogramDemo())
                .addDemo(new GraphErrorsDemo())
                .addDemo(new FittingExample());
        td.initUI();
        td.showFrame();
    }
}
