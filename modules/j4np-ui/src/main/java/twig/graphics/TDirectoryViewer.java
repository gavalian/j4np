/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import j4np.graphics.UITools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import twig.data.DataSet;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class TDirectoryViewer extends JFrame {
    
    private JPanel mainPanel = null;
    private JPanel infoPanel = null;
    private JList    dirList = null;
    
    private TDirectory   dir = null;
    
    public TDirectoryViewer(String file){
        super();
        this.setTitle("Directory Viewer");
        
        dir = new TDirectory(file);
        this.initUI();
        this.setSize(600, 600);
        
    }
    
    private String[] list2array(List<String> list){
        String[] array = new String[list.size()];
        for(int j = 0; j < list.size(); j++) array[j] = list.get(j);
        return array;
    }
    
    private void initUI(){
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        List<String> objectList = dir.getDirectoryList();
        dirList = new JList(this.list2array(objectList));
        dirList.setCellRenderer(new AlternateColorListCell());
        /*mainPanel.add(
                UITools.withPanelEtched(dirList,10),
                BorderLayout.CENTER);
        */
        mainPanel.add(
                UITools.withPanelTitled2(dirList,"directories",10),
                BorderLayout.CENTER);
        
        this.add(mainPanel);
    }
    
    public void  addData(String directory, DataSet ds){
        dir.add(directory, ds);
        this.updateModel();
    }
    
    private void updateModel(){
        //ListModel lm = dirList.getModel();
        DefaultListModel model = new DefaultListModel();
        List<String> objectList = dir.getDirectoryList();
        for(String str : objectList) model.addElement(str);
        dirList.setModel(model);
    }
    
    
    public class AlternateColorListCell extends JLabel implements ListCellRenderer {
        Color  colorOdd = new Color(255,245,245);
        Color colorEven = new Color(255,255,255);
        Color  colorSelected = new Color(255,210,200);
        
        public AlternateColorListCell() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Assumes the stuff in the list has a pretty toString
            setText(value.toString());
            
            // based on the index you set the color.  This produces the every other effect.

            if (index % 2 == 0) setBackground(colorEven);
            else setBackground(colorOdd);            
            if(isSelected) setBackground(colorSelected);
            return this;
        }
    }
    
    public static void main(String[] args){
        TDirectoryViewer viewer = new TDirectoryViewer("clas12rga.network");
        viewer.setVisible(true);
    }
}
