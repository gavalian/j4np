/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author gavalian
 */
public class Canvas2DTab extends JPanel implements ActionListener  {
    
    private JTabbedPane   tabbedPane = null; 
    private JPanel       actionPanel = null;
    private Map<String,Canvas2D>  tabbedCanvases 
            = new LinkedHashMap<String,Canvas2D>();
    
    List<Canvas2D>  canvasList = new ArrayList<>();
    public Canvas2DTab(){
        init(new String[]{"canvas"});
    }
    
    public Canvas2DTab(String[] tabs){
        init(tabs);
    }
    
    private JButton makeButton(String text, String action, String tooltip){
        JButton b = new JButton(text);
        b.addActionListener(this);
        b.setToolTipText(tooltip);
        b.setActionCommand(action);
        //b.setMargin(new Insets(0, 0, 0, 0));
        //b.setSize(40, 40);
        b.setPreferredSize(new Dimension(25, 25));
        return b;
    }
    
    private void init(String[] tabs){
        this.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        this.add(tabbedPane,BorderLayout.CENTER);
        
        for(int i = 0; i < tabs.length; i++){
            addCanvas2D(tabs[i]);
        }
        
        tabbedPane.setSelectedIndex(0);
        /**
         * Action panel initialization
         */
        
        actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        //actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(actionPanel,BorderLayout.PAGE_END);
        
        actionPanel.add(makeButton("+","add_canvas","Add new canvas"));
        //actionPanel.add(makeButton("\u2795","add_canvas","Add new canvas"));
        actionPanel.add(makeButton("-","delete_canvas","Remove current canvas"));
        actionPanel.add(makeButton("\u229E","divide_grid","Divide canvas int grid"));
        actionPanel.add(makeButton("\u229F","divide_vertical","Divide canvas Vertical"));
        /**
         * Settings icon, can also be \u26EF
         */
        //((FlowLayout)actionPanel.getLayout()).setHgap(5);
        actionPanel.add(Box.createHorizontalStrut(30));
        actionPanel.add(makeButton("\u229C","edit_settings","Settings"));
        
        //actionPanel.setPreferredSize(new Dimension(300,30));
        
    }
    
    public final void addCanvas2D(String name){        
        Canvas2D canvas = new Canvas2D();
        Background2D back = Background2D.createBackground(255, 255, 255);
        canvas.setBackground(back);
        //canvas.setBackground(Color.white);
        this.tabbedCanvases.put(name, canvas);        
        tabbedPane.addTab(name, canvas);
        tabbedPane.setSelectedComponent(canvas);
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        System.out.printf("you pressed a button [%s], so what ? \n",event.getActionCommand());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Canvas2D getCanvas2D(String name){
        return this.tabbedCanvases.get(name);
    }
    
    public static Canvas2DTab createFrame(JFrame frame, int w,int h){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas2DTab canvas = new Canvas2DTab(new String[]{"canvas1", "canvas2", "canvas3"});
        frame.setSize(w, h);
        frame.add(canvas);
        frame.setVisible(true);
        return canvas;
    }
    
    public static Canvas2DTab createFrameDebug(JFrame frame, int w,int h){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas2DTab canvas = new Canvas2DTab(new String[]{"A", "B", "C"});
        for(int i = 0; i < 6; i++){
            Node2D node = new Node2D(0,0,100,100,true);
            node.getInsets().left(15).right(15).top(15).bottom(15);
            canvas.getCanvas2D("A").addNode(node);
        }
        canvas.getCanvas2D("A").divide(3, 2);
        
        
        for(int i = 0; i < 4; i++){
            Node2D node = new Node2D(0,0,100,100,true);
            node.getInsets().left(15).right(15).top(15).bottom(15);
            canvas.getCanvas2D("B").addNode(node);
        }
        canvas.getCanvas2D("B").divide(2, 2);
        
        for(int i = 0; i < 20; i++){
            Node2D node = new Node2D(0,0,100,100,true);
            node.getInsets().left(15).right(15).top(15).bottom(15);
            canvas.getCanvas2D("C").addNode(node);
        }
        canvas.getCanvas2D("C").divide(4, 5);
        
        frame.setSize(w, h);
        frame.add(canvas);
        frame.setVisible(true);
        return canvas;
    }
    
    public static void main(String[] args){
        //GraphicsThemes.setTheme("Solarized Ligth");
        GraphicsThemes.setTheme("Flat Look Ligth");
        JFrame frame = new JFrame();
        Canvas2DTab tab = Canvas2DTab.createFrameDebug(frame, 500, 500);
    }

   
}
