/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gavalian
 */
public class TwigTableFilter extends JDialog implements ActionListener {
    
    private final List<String>  tableData = new ArrayList<String>();
    private JPanel             tablePanel = null;
    private JTable                  table = null;
    private JScrollPane        scrollPane = null;
    private JNPTableFilterModel tableModel = null;
        
    private JPanel         filterPanel     = null;
    private JPanel         actionPanel     = null;

    private JTextField     filterTextField = null;
    private int            dialogStatus    = 0;
    
    public TwigTableFilter(JFrame parent, String title){
        super(parent,title,true);
        initUI();
    }
    
    public void setData(String[] data){
        setData(Arrays.asList(data));
    }
    
    public void  setData(List<String>  data){
        tableModel.setData(data);
    }
    
    private void initUI(){
        
        tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new JNPTableFilterModel();
        //tableModel.setData("pid:px:py:pz:vx:vy:vz:status:charge");
        
        tableModel.setData("pid (" + this.getClass().getName()+"):px ("
                + this.getClass().getName()+"):py (" 
                + this.getClass().getName() + "):pz ("
                + this.getClass().getName() + "):vx:vy:vz:status:charge");
        
        //tableModel = new JNPTableFilterModel();
        
        table = new JTable();
        table.setModel(tableModel);
                
        table.getColumnModel().getColumn(1).setMaxWidth(100);
        
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        tablePanel.add(scrollPane,BorderLayout.CENTER);               
        
        filterTextField = new JTextField();        
        //filterTextField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        filterTextField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               //System.out.println("adding filter " + e.getActionCommand());
               tableModel.setFilter(e.getActionCommand());
               table.updateUI();
            }            
        });
        
        tablePanel.add(filterTextField, BorderLayout.PAGE_START);
        
        
        actionPanel = new JPanel();
        
        JButton button_chkALL = new JButton("Check All");
        JButton button_unchkALL = new JButton("Uncheck All");
        JButton button_cancel = new JButton("Cancel");
        JButton button_ok = new JButton("OK");
        button_chkALL.addActionListener(this);
        button_unchkALL.addActionListener(this);
        button_cancel.addActionListener(this);
        button_ok.addActionListener(this);
        
        actionPanel.add(button_chkALL);
        actionPanel.add(button_unchkALL);
        actionPanel.add(button_cancel);
        actionPanel.add(button_ok);
        
        tablePanel.add(actionPanel,BorderLayout.PAGE_END);
        
        //this.setLayout(new BorderLayout());
       add(tablePanel);
        pack();
    }
    
    
    public void startDialog(){
        setVisible(true);
    }
    /*public void setData(String[] data){
        tableData.clear();
        for(String item : data){ tableData.add(item); }
    }*/

    
    public List<String> getFilteredList(){
        return this.tableModel.getSelectedList();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Check All")==0){
            this.tableModel.checkAll();
            this.table.updateUI();
        }
        
        if(e.getActionCommand().compareTo("Uncheck All")==0){
            this.tableModel.uncheckAll();
            this.table.updateUI();
        }
        
        if(e.getActionCommand().compareTo("OK")==0){
            this.dialogStatus = 1;
            this.setVisible(false);
        }
        if(e.getActionCommand().compareTo("Cancel")==0){
            this.dialogStatus = 0;
            this.setVisible(false);
        }
    }
    
    public int getStatus(){
        return dialogStatus;
    }
    
    public static class JNPTableFilterModel extends DefaultTableModel {
        
        private final List<String>    modelData       = new ArrayList<String>();
        private final List<Boolean>   modelDataStatus = new ArrayList<Boolean>();
        private final List<Integer>   visibleIndex    = new  ArrayList<Integer>();
        
        private final List<String>    modelRows = new ArrayList<String>();
        private final List<Boolean>  modelCheck = new ArrayList<Boolean>();
        
        private final String         modelFilter = "";
        
        public JNPTableFilterModel(){
            super();
            //super(new String[][]{{"a","b"},{"c","d"}},new String[]{"k","l"});
            //System.out.println("SIZE = " + modelRows.size());
        }
        
        public JNPTableFilterModel(String values){
            super();            
        }
        
        public List<String> getSelectedList(){
            int size = modelData.size();
            List<String> selected = new ArrayList<String>();
            for(int i = 0; i < size; i++){
                if(modelDataStatus.get(i)==true){
                    selected.add(modelData.get(i));
                }
            }
            return selected;
        }
        
        public void checkAll(){
            for(int i = 0; i < visibleIndex.size(); i++){
                this.modelDataStatus.set(visibleIndex.get(i), true);
            }
        }
        
        public void uncheckAll(){
            for(int i = 0; i < visibleIndex.size(); i++){
                this.modelDataStatus.set(visibleIndex.get(i), false);
            }
        }
        
        public void setData(List<String> data)
        {
            modelData.clear();
            modelDataStatus.clear();
            for(String item : data){
                modelData.add(item);
                modelDataStatus.add(false);
            }
        }
        
        public void setData(String values){
            String[] tokens = values.split(":");
            for(int i = 0; i < tokens.length; i++){
                
                modelData.add(tokens[i]);
                modelDataStatus.add(false);
                visibleIndex.add(i);
                //modelRows.add(tokens[i]);
                //modelCheck.add(false);
               // rows.add(tokens[i]); check.add(true);
               // System.out.println("adding = " + tokens[i]);
            }
        }
        
        public void setFilter(String filter){
            modelRows.clear();
            modelCheck.clear();
            visibleIndex.clear();
            int nData = modelData.size();
            for(int i = 0; i < nData; i++){
                if(filter.length()>0){
                    if(modelData.get(i).contains(filter)==true){
                        //modelRows.add(modelData.get(i));
                        //modelCheck.add(modelDataStatus.get(i));
                        visibleIndex.add(i);
                    }
                } else{
                    visibleIndex.add(i);
                }
            }
        }
        
        @Override
        public int getColumnCount(){
            //System.out.println(" cols = " + modelRows.size());
            return 2;
        }
        
        @Override
        public int getRowCount(){
            if(visibleIndex!=null) return visibleIndex.size();
            //System.out.println(" rows = " + modelRows.size());
            return 0;
            /*
            System.out.println(" rows = " + rows.size());
            return rows.size();*/
        }
    
        @Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            if(column==1) return true;
            return false;
        }
        
        @Override
        public void setValueAt(Object aValue,
                int row,
                int column){
           if(column==1){
               modelDataStatus.set(visibleIndex.get(row), (Boolean) aValue);
           } 
           
           if(column==0){
               modelData.set(row, (String) aValue);
           }
        }
        @Override
        public Object getValueAt(int row, int column){
            if(column==0){
                if(modelData!=null) return modelData.get(visibleIndex.get(row));
                return 0;//return rows.get(row);
            }
            if(column==1){
                if(modelDataStatus!=null) return modelDataStatus.get(visibleIndex.get(row));
                return 0;//return check.get(row);
            }
            return "unknown";
        }
        
        
        @Override
        public String getColumnName(int column){
            if(column==0) return "Item";
            return "Status";
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if(columnIndex==1) return Boolean.class;
            return String.class;
        }
        
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        //JNPTableFilterModel model = new JNPTableFilterModel();
        frame.setSize(300, 300);
        //JNPTreeView tree = new JNPTreeView();
        TwigTableFilter filter = new TwigTableFilter(frame,"Schema Filter");
        //filter.setVisible(true);
        filter.startDialog();
        
        System.out.println("STATUS = " + filter.getStatus());
        
        if(filter.getStatus()!=0){
            List<String> list = filter.getFilteredList();
            for(String item : list){
                System.out.println(" item = " + item);
            }
        } else {
            System.out.println("---->>>>> CANCELED");
        }
        
        //filter.dispose();
        //frame.add(tree);
        frame.setVisible(true);
    }
}
