/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import twig.data.DataSet;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.editors.DataEditorUtils;
import twig.math.Func1D;
import twig.math.PeakFinder;
import twig.utils.SpringUtilities;

/**
 *
 * @author gavalian
 */
public class FitterPanel extends JDialog implements ActionListener {
    TGRegion region = null;
    
    List<DataSet> regionData = new ArrayList<>();
    
    
    public static final int OK_OPTION = 0;
    public static final int CANCEL_OPTION = 1;
    
    private int result = -1;
    
    JPanel controls;
    
    
    JComboBox cbDataset;
    JComboBox cbBackground;
    JComboBox cbPeaks;
    JTextField tfMin;
    JTextField tfMax;
    
    TTabDataCanvas c;
    
    public FitterPanel(List<DataSet> datalist, Frame parent){
        super(parent, false);
        regionData.addAll(datalist);
        initUI();
    }
    
    public JComboBox createComboBox(){
        String[] items = new String[regionData.size()];
        for(int i = 0; i < items.length;i++) items[i] = regionData.get(i).getName();
        return new JComboBox(items);
    }
    
    
    private void addWitLabel(JPanel p, String label, JComponent jc){
        JLabel l1 = new JLabel(label, JLabel.TRAILING);
        p.add(l1); l1.setLabelFor(jc);
        controls.add(jc);        
    }
    
    private void initUI(){
        setLayout(new BorderLayout());
        
        c = new TTabDataCanvas(new String[]{"data","genetic"});
        c.getCanvases().get(0).region().draw(this.regionData.get(0));
        
        this.add(c,BorderLayout.CENTER);
        JPanel layout = new JPanel(new BorderLayout());
        layout.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        controls = new JPanel(new SpringLayout());
        
        controls.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        int numPairs = 2;
        
       
        cbDataset = createComboBox();
        this.addWitLabel(controls, "Dataset:", cbDataset);
    
        cbBackground = new JComboBox(new String[]{"0","1","2","3"});
        cbBackground.setSelectedIndex(2);
        this.addWitLabel(controls, "Background:", cbBackground);
        
        cbPeaks = new JComboBox(new String[]{"1","2","3"});
        this.addWitLabel(controls, "Peaks:", cbPeaks);
        
        tfMin = new JTextField(10);
        tfMax = new JTextField(10);
        tfMin.setText("0.0"); tfMax.setText("1.0");
        
        this.addWitLabel(controls, "Minimum:", tfMin);
        this.addWitLabel(controls, "Maximum:", tfMax);
        
        JButton btnFit = new JButton("Fit");
        JButton btnClose = new JButton("Close");
        controls.add(btnFit);controls.add(btnClose);
        
        btnFit.addActionListener(this);
        //JSpinner spMin = DataEditorUtils.makeSpinnerDouble(0, 0, 100, 0.01);
        //JSpinner spMax = DataEditorUtils.makeSpinnerDouble(0, 0, 100, 0.01);
        
        //controls.add(spMin);
        //controls.add(spMax);
        /*for (int i = 0; i < numPairs; i++) {
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            controls.add(l);
            JTextField textField = new JTextField(10);
            l.setLabelFor(textField);
            controls.add(textField);
        }*/
        
        SpringUtilities.makeCompactGrid(controls,
                                        numPairs, 6, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);
        layout.add(controls,BorderLayout.CENTER);
        this.add(layout,BorderLayout.PAGE_END);
        /*JPanel gui = new JPanel(new BorderLayout(3, 3));
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        content = new JPanel(new BorderLayout());
        gui.add(content, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(4));
        gui.add(buttons, BorderLayout.SOUTH);
        
        JButton ok = new JButton("OK");
        buttons.add(ok);
        ok.addActionListener(e->{
            result = OK_OPTION;
            setVisible(false);
        });
        
        JButton cancel = new JButton("Cancel");
        buttons.add(cancel);
        cancel.addActionListener(e->{
            result = CANCEL_OPTION;
            setVisible(false);
        });
        setContentPane(gui);*/
    }
    
    public void showDialog(){

        this.setLocationRelativeTo(this.getParent());
        this.setTitle("Genetic Fitting Panel");
        this.pack();
        this.setSize(600, 550);
        this.setVisible(true);
        //int result = this.showConfirmDialog(c, "Genetic Fitter Panel");
        //System.out.println(" result = " + result);
    }
    
    public int showConfirmDialog(JComponent child, String title) {
        System.out.println("--- showing ---");
        setTitle(title);
        //content.removeAll();
        this.add(child, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
        return result;
    }
    
     @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getActionCommand().compareTo("Fit")==0){
           int background = Integer.parseInt((String) this.cbBackground.getSelectedItem());
           int npeaks = Integer.parseInt((String) this.cbPeaks.getSelectedItem());
           double min = Double.parseDouble(this.tfMin.getText());
           double max = Double.parseDouble(this.tfMax.getText());
           int selected = this.cbDataset.getSelectedIndex();
           System.out.println(">>> " + selected + "  size = " + this.regionData.size());
           H1F h = (H1F) this.regionData.get(selected);
           PeakFinder pf = new PeakFinder(h);
           
           pf.setRange(min, max);
           
           
           this.c.getCanvases().get(0).region().draw(h,"EP");
           pf.fit(2, 1);
           c.getCanvases().get(0).region().draw(pf.getFittedFunctions().get(0),"same");
           
           c.getCanvases().get(1).region().draw(pf.getDerived(),"EP");
           c.getCanvases().get(1).region().draw(pf.getPdf(),"same");
           //for(Func1D f : pf.getGeneticFunctions()) c.getCanvases().get(1).region().draw(f,"same");
       }
    }
    
    public static void main(String[] args){
        JFrame fr = new JFrame();
        fr.setVisible(true);
        H1F h = TDataFactory.createH1F(8200, 240, 0.0, 1.0, 0.25, 0.05);
        FitterPanel panel = new FitterPanel(Arrays.asList(h),fr);
        panel.showDialog();
    }

   
}
