/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.gui;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gavalian
 */
public class NodeTable extends JPanel implements ActionListener {
    
    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel model = null;//new DefaultTableModel(columnNames, 0);    
    private Leaf   leaf = null;
    private Bank   bank = null;
    
    private String[] names = null;
    public NodeTable(){}
    
    public NodeTable(int group, int item){
        leaf = new Leaf(group,item, "i", 1024*4); // allocate 16 Kbyte buffer
    }
    
    
    private Icon getIcon(){
        try {
            BufferedImage image = ImageIO.read(getClass().getResource("/glyphs/hippo_19ptx.png"));
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgba = image.getRGB(x, y);
                    Color color = new Color(rgba, true);
                    //Color invertedColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue(), color.getAlpha());
                    //System.out.printf("%5d %5d %5d\n",color.getRed(),color.getGreen(),color.getBlue());
                    Color invertedColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue(), color.getAlpha());
                    image.setRGB(x, y, invertedColor.getRGB());
                }
            }
            Icon icon = new ImageIcon(image);
            return icon;
        } catch (IOException ex) {
            Logger.getLogger(NodeTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private void init(){
        this.setLayout(new BorderLayout());
        JPanel bottom = new JPanel();
        //Icon icon = new ImageIcon(getClass().getResource("/glyphs/hippo_19ptx.png"));
        Icon icon = getIcon();
        JLabel label = new JLabel(icon);
        label.setMaximumSize(new Dimension(60,20));
        label.setMinimumSize(new Dimension(60,20));
        bottom.setLayout(new BorderLayout());
        bottom.add(label,BorderLayout.LINE_START);        
        bottom.setBackground(new Color(0x70,0x56,0x97));
        this.add(bottom,BorderLayout.PAGE_END);
        //scrollPane = new JScrollPane(table);
    }
    
    public NodeTable(Leaf node){
        leaf = node;
        //this.setLayout(new BorderLayout());
        init();
        names = this.getColumnNames(node);
        String[][] data = getColumnData(node);
        
         model = new DefaultTableModel(data, names) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        table = new JTable(model);
        table.setSelectionBackground(new Color(0x70,0x56,0x97));
        table.setSelectionForeground(new Color(220,220,220));
        //table.getTableHeader().setBackground(new Color(255,210,124));
        table.getTableHeader().setBackground(new Color(0xD6,0xCF,0XE2));
        scrollPane = new JScrollPane(table);
        this.add(scrollPane,BorderLayout.CENTER);
        
        table.setDefaultRenderer(Object.class, createRenderer());
    }
    public DefaultTableCellRenderer createRenderer(){
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row, int column) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    //if(row%2==0){
                    //        component.setBackground(new Color(0xD6,0xCF,0XE2));
                    //        component.setForeground(Color.BLACK);
                    //}
                    if (isSelected) {
                        component.setBackground(new Color(0x70,0x56,0x97));
                        component.setForeground(new Color(220,220,220));
                    } else {
                        if(row%2==0){
                            component.setBackground(new Color(0xD6,0xCF,0XE2));
                            component.setForeground(Color.BLACK);
                        } else {
                            component.setBackground(Color.WHITE);
                            component.setForeground(Color.BLACK);
                        }
                    }
                    return component;
                }
            };
        return renderer;
    }
    public NodeTable(Bank node){
        
        bank = node;
        //this.setLayout(new BorderLayout());
        init();
        names = bank.getSchema().getEntryArray();
        String[][] data = getColumnDataBank(bank);
        
         model = new DefaultTableModel(data, names) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        table = new JTable(model);
        table.setSelectionBackground(new Color(0x70,0x56,0x97));
        table.setSelectionForeground(new Color(220,220,220));
        //table.getTableHeader().setBackground(new Color(255,210,124));
        table.getTableHeader().setBackground(new Color(0xD6,0xCF,0XE2));
        scrollPane = new JScrollPane(table);
        this.add(scrollPane,BorderLayout.CENTER);
        table.setDefaultRenderer(Object.class, createRenderer());
    }
    
    public final String[] getColumnNames(Leaf node){
        String[] cn = new String[node.getEntries()];
        for(int i = 0; i < cn.length; i++) {
            Integer n = node.getEntryType(i);
            cn[i] = String.format("%d [t=%d]",i+1,node.getEntryType(i));
        }
        return cn;
    }
    public final String[][] getColumnDataBank(Bank node){
        String[][] cn = new String[node.getRows()][node.getSchema().getElements()];
        //System.out.println();
        for(int r = 0; r < node.getRows(); r++) {
            for(int e = 0; e < node.getSchema().getElements(); e++){
                int type = node.getSchema().getType(e);//.getEntryType(e);
                switch(type){
                    case 1: Integer b1 = node.getInt(e,r); cn[r][e] = b1.toString(); break;
                    case 2: Integer b2 = node.getInt(e,r); cn[r][e] = b2.toString(); break;
                    case 3: Integer b3 = node.getInt(e,r); cn[r][e] = b3.toString(); break;
                    case 4: Float f4 = (float) node.getDouble(e,r); cn[r][e] = f4.toString(); break;
                    case 5: Double d8 =  node.getDouble(e,r); cn[r][e] = d8.toString(); break;
                    case 8: Long l1 = node.getLong(e,r); cn[r][e] = l1.toString(); break;
                    default: cn[r][e] = "n/a"; break;
                }
            }
        }
        return cn;
    }
    
    public final String[][] getColumnData(Leaf node){
        String[][] cn = new String[node.getRows()][node.getEntries()];
        //System.out.println();
        for(int r = 0; r < node.getRows(); r++) {
            for(int e = 0; e < node.getEntries(); e++){
                int type = node.getEntryType(e);
                switch(type){
                    case 1: Integer b1 = node.getInt(e,r); cn[r][e] = b1.toString(); break;
                    case 2: Integer b2 = node.getInt(e,r); cn[r][e] = b2.toString(); break;
                    case 3: Integer b3 = node.getInt(e,r); cn[r][e] = b3.toString(); break;
                    case 4: Float f4 = (float) node.getDouble(e,r); cn[r][e] = f4.toString(); break;
                    case 5: Double d8 =  node.getDouble(e,r); cn[r][e] = d8.toString(); break;
                    case 8: Long l1 = node.getLong(e,r); cn[r][e] = l1.toString(); break;
                    default: cn[r][e] = "n/a"; break;
                }
            }
        }
        return cn;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getActionCommand().compareTo("hipo::next")==0){
            if(leaf!=null&&bank==null){
                System.out.println(" recevied event hipo::next");
                Event event = (Event) e.getSource();
                event.read(leaf);
                String[]   names = this.getColumnNames(leaf);
                String[][]  data = this.getColumnData(leaf);
                model.setDataVector(data, names);
            }
        
            if(leaf==null&&bank!=null){
                Event event = (Event) e.getSource();
                event.read(bank);
                String[]   names = bank.getSchema().getEntryArray();
                String[][]  data = this.getColumnDataBank(bank);
                model.setDataVector(data, names);
            }
        }
    }
    public static void main(String[] args){
        Leaf node = Leaf.random(24,"8i5f");
    
        node.show();
        node.print();
        
        JFrame frame = new JFrame();
        NodeTable tbl = new NodeTable(node);
        
        
        frame.add(tbl);
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
        
    }

}
