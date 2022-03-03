/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import j4np.hipo5.io.HipoReader;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;
import twig.utils.TwigDialog;

/**
 *
 * @author gavalian
 */
public class OpenHipoDialog implements ActionListener {
    
    TwigDialog dialog;
    JPanel     pane;
    JComboBox  comboBanks;
    HipoReader r = null;
    String filename = "";
    
    public OpenHipoDialog(Frame fr){
        
        initUI(fr);
    }
    
    private void initUI(Frame fr){
        pane = new JPanel();
        pane.setLayout(new MigLayout("","[]50[]20[]20[]20[]","[]20[]"));

        pane.add(new JLabel("Choose File:"));
        pane.add(TwigDialog.createButton("...","Choose File",this),"wrap");
        
        pane.add(new JLabel("Choose Bank:"));
        comboBanks = new JComboBox();
        pane.add(comboBanks,"wrap");
        
        dialog = new TwigDialog(fr,pane);
    }

    public void show(){ dialog.showDialog();}

    public String getFileName(){return filename;}
    public String getBankName(){return (String) comboBanks.getSelectedItem();}
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Choose File")==0){
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Twig Files (.twig)","twig"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Hipo Files (.h5)","h5"));
            
            File workingDirectory = new File(System.getProperty("user.dir"));
            fc.setCurrentDirectory(workingDirectory);

            int returnVal = fc.showOpenDialog(dialog);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                filename = file.getAbsolutePath();
                r = new HipoReader();
                r.open(file.getAbsolutePath());
                List<String> banks = r.getSchemaFactory().getSchemaKeys();
                Collections.sort(banks);
                comboBanks.removeAllItems();
                for(String b : banks)
                    comboBanks.addItem(b);
                
            }
        }
    }
}
