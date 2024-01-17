/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import twig.data.H1F;
import twig.data.H2F;
import twig.utils.SpringUtilities;

/**
 *
 * @author gavalian
 */
public class HistogramOperations extends DatasetActionPanel implements ActionListener {
    List<H1F> dataList = new ArrayList<>();
    JComboBox cbOne = null;
    JComboBox cbTwo = null;
    JPanel controls;
    
    public HistogramOperations(List<H1F> h, Frame parent){
       super(parent);
       dataList.addAll(h);
       this.init();
       this.setSize(750, 650);
    }
    
    public JComboBox createComboBox(List<H1F> list){
        String[] names = new String[list.size()];
        for(int j = 0; j < names.length; j++) names[j] = list.get(j).getName();
        return new JComboBox(names);
    }
    
    private void addWitLabel(JPanel p, String label, JComponent jc){
        JLabel l1 = new JLabel(label, JLabel.TRAILING);
        p.add(l1); l1.setLabelFor(jc);
        p.add(jc);        
    }
    
    public JButton createButton(String label){
        JButton btn = new JButton(label);
        btn.addActionListener(this);
        return btn;
    }
    
    private void init(){
        
        cbOne = this.createComboBox(dataList);
        cbTwo = this.createComboBox(dataList);
        
        controls = new JPanel(new SpringLayout());
        
        JButton btnSub = this.createButton("Subtract");
        JButton btnAdd = this.createButton("Add");
        JButton btnDiv = this.createButton("Divide");
        JButton btnMlt = this.createButton("Multiply");
        JButton btnAsym = this.createButton("Assymmetry");
        
        JButton btnDummy = this.createButton("Dummy");
        
        this.addWitLabel(controls, "First:", cbOne);
        controls.add(btnSub);
        controls.add(btnAdd);
        controls.add(btnAsym);
        this.addWitLabel(controls, "Second:", cbTwo);
        controls.add(btnDiv);
        controls.add(btnMlt);
        controls.add(btnDummy);
        SpringUtilities.makeCompactGrid(controls,
                                        2, 5, //rows, cols
                                        8, 8,        //initX, initY
                                        8, 8);
        
        setActionComponent(controls);
        
        initUI(new String[]{"operations-1D"});
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Subtract")==0){
            
            H1F one = this.dataList.get(cbOne.getSelectedIndex());
            H1F two = this.dataList.get(cbTwo.getSelectedIndex());
            
            
            H1F hsub = one.copy();
            hsub.sub(two);
            this.getCanvas().activeCanvas().region().draw(hsub);
            this.getCanvas().activeCanvas().repaint();
        }
        
        if(e.getActionCommand().compareTo("Add")==0){
            H1F one = this.dataList.get(cbOne.getSelectedIndex());
            H1F two = this.dataList.get(cbTwo.getSelectedIndex());
            
            H1F hsub = one.copy();
            hsub.add(two);
            this.getCanvas().activeCanvas().region().draw(hsub);
            this.getCanvas().activeCanvas().repaint();
        }
        
        if(e.getActionCommand().compareTo("Divide")==0){
            H1F one = this.dataList.get(cbOne.getSelectedIndex());
            H1F two = this.dataList.get(cbTwo.getSelectedIndex());
            System.out.println("DEBUG:: selected index 1 = " 
                    + cbOne.getSelectedIndex() + " 2 = " + cbTwo.getSelectedIndex());
            
            System.out.println("DEBUG:: integral index 1 = "  + one.integral() + " 2 = " 
                    + two.getIntegral());
            
            H1F hsub = H1F.divide(one, two);
            this.getCanvas().activeCanvas().region().draw(hsub);
            this.getCanvas().activeCanvas().repaint();
        }
        
        if(e.getActionCommand().compareTo("Assymmetry")==0){
            H1F one = this.dataList.get(cbOne.getSelectedIndex());
            H1F two = this.dataList.get(cbTwo.getSelectedIndex());
            
            System.out.println("DEBUG:: selected index 1 = " 
                    + cbOne.getSelectedIndex() + " 2 = " + cbTwo.getSelectedIndex());
            
            System.out.println("DEBUG:: integral index 1 = "  + one.integral() + " 2 = " 
                    + two.getIntegral());
            
            H1F hsumm = H1F.add(one, two);
            H1F hdiff = H1F.sub(one, two);
            
            H1F hsub = H1F.divide(hdiff, hsumm);
            this.getCanvas().activeCanvas().region().draw(hsub);
            this.getCanvas().activeCanvas().repaint();
        }
        
        if(e.getActionCommand().compareTo("Multiply")==0){
            H1F one = this.dataList.get(cbOne.getSelectedIndex());
            H1F two = this.dataList.get(cbTwo.getSelectedIndex());
            H1F hsub = one.copy();
            this.getCanvas().activeCanvas().region().draw(hsub);
            this.getCanvas().activeCanvas().repaint();
        }
        
    }
    
}
