/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.gui;

import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gavalian
 */
public class NodeTable extends JPanel implements ActionListener {
    
    private JScrollPane scrollPane;
    private JTable table;
    DefaultTableModel model = null;//new DefaultTableModel(columnNames, 0);
    
    private Leaf   leaf = null;
    
    public NodeTable(){}
    
    public NodeTable(int group, int item){
        leaf = new Leaf(group,item, "i", 1024*4); // allocate 16 Kbyte buffer
    }
    
    private void init(){
        this.setLayout(new BorderLayout());
        //scrollPane = new JScrollPane(table);
    }
    public NodeTable(Leaf node){
        this.setLayout(new BorderLayout());
        String[] names = this.getColumnNames(node);
        String[][] data = this.getColumnData(node);
        table = new JTable(data,names);
        table.getTableHeader().setBackground(Color.ORANGE);
        scrollPane = new JScrollPane(table);
        this.add(scrollPane,BorderLayout.CENTER);
    }
    
    public final String[] getColumnNames(Leaf node){
        String[] cn = new String[node.getEntries()];
        for(int i = 0; i < cn.length; i++) {
            Integer n = node.getEntryType(i);
            cn[i] = String.format("%d [t=%d]",i+1,node.getEntryType(i));
        }
        return cn;
    }
    
    public String[][] getColumnData(Leaf node){
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
            Event event = (Event) e.getSource();
            event.read(leaf);
            String[]   names = this.getColumnNames(leaf);
            String[][]  data = this.getColumnData(leaf);
            
        }
    }
    public static void main(String[] args){
        Leaf node = Leaf.random(24);
    
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
