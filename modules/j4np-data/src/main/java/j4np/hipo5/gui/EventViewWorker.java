/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.gui;

import j4np.data.base.DataEvent;
import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.data.SchemaFactory;
import j4np.hipo5.io.HipoReader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gavalian
 */
public class EventViewWorker extends DataWorker {
    
    private Event dataEvent = null;
    private SchemaFactory factory = new SchemaFactory();
    JPanel  workerView = new JPanel();
     private JScrollPane scrollPane;
    private JTable table;
    DefaultTableModel model = null;//new DefaultTableModel(columnNames, 0);
    String[] columnNames = new String[]{"name", "group","item","type", "position","format","size","rows"};

    List<NodeTable>   nodeTables = new ArrayList<>();

    public EventViewWorker(){
        initUI();
    }
    
    public final void initUI(){
        workerView.setLayout(new BorderLayout());
        //String[] names = new String[]{"group","item","type", "position","format","size","rows"};
        String[][] data = new String[0][0];
        
        //model = new DefaultTableModel(data, columnNames);
        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        table = new JTable(model);
        //table.getTableHeader().setBackground(new Color(255,210,124));
        table.getTableHeader().setBackground(new Color(0xD6,0xCF,0XE2));
        scrollPane = new JScrollPane(table);
        this.workerView.add(scrollPane,BorderLayout.CENTER);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click detected
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        processClick(selectedRow);
                        StringBuilder rowData = new StringBuilder("Selected Row Data: ");
                        for (int col = 0; col < table.getColumnCount(); col++) {
                            rowData.append(table.getValueAt(selectedRow, col)).append(" ");
                        }
                        System.out.println(rowData.toString().trim());
                    }
                }
            }
        });
    }
    
    public JPanel getPanel(){ return workerView;}
    
    protected void processClick(int row){
        int type = Integer.parseInt((String) table.getValueAt(row, 3));
        System.out.println(" type = " + type);
        if(type==10){
            int group = Integer.parseInt((String) table.getValueAt(row, 1));
            int  item = Integer.parseInt((String) table.getValueAt(row, 2));
            
            int position = dataEvent.scan(group, item);
            int   length = dataEvent.scanLengthAt(group, item, position);
            Leaf    leaf = new Leaf(length+8+24);
            dataEvent.read(leaf, group, item);
            //leaf.print();
            NodeTable tbl = new NodeTable(leaf);
            this.nodeTables.add(tbl);
            JFrame frame = new JFrame();
            frame.setTitle(String.format("LEAF [%d%d]", group,item));
            frame.add(tbl);
            frame.pack();
            frame.setSize(500, 500);
            frame.setVisible(true);
        } 
        if(type==11){
            int group = Integer.parseInt((String) table.getValueAt(row, 1));
            int  item = Integer.parseInt((String) table.getValueAt(row, 2));
            if(factory.hasSchema(group, item)==true){
                Bank b = new Bank(factory.getSchema(group, item),128);
                dataEvent.read(b);
                NodeTable tbl = new NodeTable(b);
                this.nodeTables.add(tbl);
                JFrame frame = new JFrame();
                frame.setTitle(b.getSchema().getName());
                frame.add(tbl);
                frame.pack();
                frame.setSize(500, 500);
                frame.setVisible(true);
            }
        }
    }
    
    @Override
    public boolean init(DataSource src) {
        if(src instanceof HipoReader){
            HipoReader r = (HipoReader) src;
            factory.copy(r.getSchemaFactory());
            System.out.println("showing factory");
            if(!factory.getSchemaList().isEmpty())
                factory.show();
        }
            
        
        return true;
    }

    @Override
    public void execute(DataEvent e) {

    }
    
    public static List<String> splitStringByNewLines(String input) {
        return new ArrayList<>(Arrays.asList(input.split("\\n")));
    }
    
    @Override
    public void execute(DataFrame f){
        Event e = (Event) f.getEvent(0);
        this.dataEvent = e;
        //System.out.println(e.showString());
        String dataString = e.showString();
        
        List<String>  list = EventViewWorker.splitStringByNewLines(dataString);
        
        String[][] data = new String[list.size()-1][columnNames.length];
        
        for(int j = 1; j < list.size(); j++){
            String[] tokens = list.get(j).split(",");
            //System.out.printf("%d %d %d\n",tokens.length, columnNames.length, list.size());
            int type = Integer.parseInt(tokens[2].trim());
            int group = Integer.parseInt(tokens[0].trim());
            int item = Integer.parseInt(tokens[1].trim());
            data[j-1][0] = "Unknown";
            switch(type){
                case 1: data[j-1][0] = "Byte Array"; break;
                case 2: data[j-1][0] = "Short Array"; break;
                case 3: data[j-1][0] = "Int Array"; break;
                case 4: data[j-1][0] = "Float Array"; break;
                case 5: data[j-1][0] = "Double Array"; break;
                case 8: data[j-1][0] = "Long Array"; break;
                case 10: data[j-1][0] = "Composite"; break;
                case 11: { data[j-1][0] = factory.hasSchema(group,item)?factory.getSchema(group, item).getName():"Undeclared"; } break;
                default: data[j-1][0] = "Unknown"; break;                
            }
            for(int c = 0; c < columnNames.length-1;c++) {
                data[j-1][c+1] = tokens[c].trim();
            }
            //data[0][j] = tokens[0].trim();
            //data[1][j] = tokens[1].trim();            
        }
        /*Object[][] newData = {
            {"NewRow1-Col1", "NewRow1-Col2", "NewRow1-Col3", "NewRow1-Col4"},
            {"NewRow2-Col1", "NewRow2-Col2", "NewRow2-Col3", "NewRow2-Col4"},
            {"NewRow3-Col1", "NewRow3-Col2", "NewRow3-Col3", "NewRow3-Col4"},
            {"NewRow4-Col1", "NewRow4-Col2", "NewRow4-Col3", "NewRow4-Col4"}
        };*/
        
        model.setDataVector(data, columnNames);
        
        ActionEvent event = new ActionEvent(dataEvent,1,"hipo::next");
        for(NodeTable t : this.nodeTables) t.actionPerformed(event);
    }   
}
