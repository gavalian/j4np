/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.gui;

import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author gavalian
 */
public class FrameView extends JPanel implements ActionListener {
    DataSource  dataSource;
    DataFrame<Event> dataFrame = new DataFrame();
    
    List<Event>  dataEvents = new ArrayList<>();
    JTree objectTree;
    JTree tree = new JTree();
    NodeTable nodePane;
    JSplitPane pane;
    JPanel actionPanel;
    
    public FrameView(int size){
        for(int k = 0; k < size; k++) dataEvents.add(new Event());
        //initUI();
    }
    
    public FrameView  setDataSource(DataSource src){
        dataSource = src; return this;
    }
    
    public JButton createButton(String name, String action){
        JButton button = new JButton(name);
        button.setActionCommand(action);
        button.addActionListener(this);
        return button;
    }
    protected void initUI(){
        this.setLayout(new BorderLayout());
        pane = new JSplitPane();
        objectTree = new JTree();
        TreeModel model =  this.getTreeModel();
        objectTree.setModel(model);
        objectTree.addMouseListener(new MouseAdapter() 
        {
             @Override
             public void mouseClicked(MouseEvent me) {
                 doMouseClicked(me);
             }
             
        });
        nodePane = new NodeTable();
        JScrollPane treePane = new JScrollPane(objectTree);
        pane.setLeftComponent(treePane);
        pane.setRightComponent(nodePane);
        
        this.add(pane,BorderLayout.CENTER);
        
        actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());
        
        actionPanel.add(this.createButton("Next", "next_events"));
        actionPanel.add(this.createButton("Connect", "connect_server"));
        
        this.add(actionPanel,BorderLayout.PAGE_END);
        
    }
    
    public void doMouseClicked(MouseEvent me){
        if(me.getClickCount()==2){
            TreePath tp = objectTree.getPathForLocation(me.getX(), me.getY());
            if (tp != null){
                System.out.println(" path -> : " + tp.toString());
                StringBuilder str = new StringBuilder();
                int nelements = tp.getPathCount();
                if(nelements==3){
                    for(int i = 1; i < nelements; i++){
                        if(i!=1)str.append(",");
                        str.append( tp.getPathComponent(i).toString());
                    }
                    String objectPath = str.toString();
                    String objectIds = objectPath.replaceAll("[^0-9,/]", "");
                    
                    System.out.println(objectIds);
                    String[] tokens = objectIds.split(",");
                    int type = Integer.parseInt(tokens[3]);
                    if(type==10){
                        Leaf node = new Leaf(1024);
                        /*dataEvents.get(Integer.parseInt(tokens[0])-1).read(node, 
                                Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));*/
                        //node.show();
                        //node.print();
                        NodeTable table = new NodeTable(node);
                        pane.setRightComponent(table);
                    } else {
                        System.out.println("\n\nerror>>>> this is not a composite bank\n");
                    }
                }
            }
        }
    }
    
    public   TreeModel  getTreeModel(){
    
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        for(int i = 0; i < dataFrame.getCount(); i++){
            DefaultMutableTreeNode event = new DefaultMutableTreeNode(String.format("event # %d", i+1));
            List<String> leafs = ( (Event) dataFrame.getEvent(i)).scanLeafs();
            for(int l = 0; l < leafs.size(); l++) event.add(new DefaultMutableTreeNode(leafs.get(l)));
            root.add(event);
        }
        /*for(int i = 0; i < dataEvents.size(); i++){
            DefaultMutableTreeNode event = new DefaultMutableTreeNode(String.format("event # %d", i+1));
            List<String> leafs = this.dataEvents.get(i).scanLeafs();
            for(int l = 0; l < leafs.size(); l++) event.add(new DefaultMutableTreeNode(leafs.get(l)));
            root.add(event);
        }*/
        return new DefaultTreeModel(root);
    }
    
    public void updateTreeModel(){
        TreeModel model = getTreeModel();
        this.objectTree.setModel(model);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("next_events")==0){
            dataSource.nextFrame(dataFrame);
            updateTreeModel();
        }
    }
    
    public static void main(String[] args){
        
        FrameView view = new FrameView(25);
        Random r = new Random();
        for(int k = 0; k < view.dataEvents.size(); k++)
            view.dataEvents.get(k).write(CompositeNode.random(r.nextInt(25)+4));
        
        //CompositeNode node = CompositeNode.random(12);
        view.initUI();
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.add(view);
        frame.pack();
        frame.setSize(800, 500);
        frame.setVisible(true);
    }

}
