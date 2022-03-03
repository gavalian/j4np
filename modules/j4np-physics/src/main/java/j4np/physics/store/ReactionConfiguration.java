/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.physics.store;

import j4np.hipo5.io.HipoReader;
import j4np.physics.EventModifier;
import j4np.physics.PhysicsReaction;
import j4np.physics.store.EventModifierStore.EventModifierForward;
import j4np.physics.store.EventModifierStore.EventModifierForwardCentral;
import j4np.physics.store.EventModifierStore.EventModifierSimulation;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;
import twig.utils.TwigDialog;

/**
 *
 * @author gavalian
 */
public class ReactionConfiguration implements ActionListener {
    
    TwigDialog         dialog = null;
    PhysicsReaction  reaction = null;
    
    JSpinner         beamEnergy = null;
    JComboBox        comboBanks = null;
    JComboBox        comboBanksSecond = null;
    JComboBox        comboModifier = null;
    
    private boolean hasSecondBox = false;
    
    public String           bankName = "";
    public HipoReader         reader = new HipoReader();
    public EventModifier    modifier = null;
    
    public ReactionConfiguration(Frame frame, PhysicsReaction r){
        initUI(frame);
        reaction = r;
    }
    
    public ReactionConfiguration(Frame frame, PhysicsReaction r, boolean secondBox){        
        hasSecondBox = secondBox;
        reaction = r;
        initUI(frame);
    }
    
    private JButton createButton(String title, String callback){
        JButton button = new JButton(title);
        button.setActionCommand(callback);
        button.addActionListener(this);
        return button;
    }
    
    public String getBankName(){
       return (String) this.comboBanks.getSelectedItem();
    }
    
    public String getSecondBankName(){
        if(this.hasSecondBox==true&&comboBanksSecond!=null)
            return (String) this.comboBanksSecond.getSelectedItem();
        
        return "";
    }
    public EventModifier getEventModifier(){
       String mname = (String) this.comboModifier.getSelectedItem();
       switch(mname){
          case "EventModifierSimulation": return new EventModifierSimulation();
          case "EventModifierForward": return new EventModifierForward();
          case "EventModifierForwardCentral": return new EventModifierForwardCentral();               
          default: return new EventModifierSimulation();
       }
    }
    
    public double getBeamEnergy(){
        return (double) beamEnergy.getValue();
    }
    
    private void initUI(Frame frame){
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Reaction Configuration"));
        panel.setLayout(new MigLayout("","[]50[]20[]20[]20[]","[]20[]"));
        
        
        panel.add(new JLabel("Beam Energy:"));
        SpinnerNumberModel model = new SpinnerNumberModel(
                10.6, 0.0, 12.1, 0.05);
        beamEnergy = new JSpinner(model);
        panel.add(beamEnergy,"wrap");
        
        panel.add(new JLabel("Choose File:"));
        panel.add(createButton("...","Choose File"),"wrap");
        
        panel.add(new JLabel("Particle Bank:"));
        comboBanks = new JComboBox();
        panel.add( comboBanks,"wrap");
        
        if(this.hasSecondBox==true){
            panel.add(new JLabel("Tagger Bank:"));
            comboBanksSecond = new JComboBox();
            panel.add( comboBanksSecond,"wrap");
        }
        
        panel.add(new JLabel("Event Modifier:"));
        comboModifier = new JComboBox(new String[]
        {"EventModifierSimulation","EventModifierForward",
            "EventModifierForwardCentral"
        });
        panel.add( comboModifier
                ,"wrap");
        
        dialog = new TwigDialog(frame,panel);
    }
    
    public void show(){
        dialog.showDialog();        
    }

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
                reader.open(file.getAbsolutePath());
                List<String> banks = reader.getSchemaFactory().getSchemaKeys();
                Collections.sort(banks);
                comboBanks.removeAllItems();
                if(comboBanksSecond!=null) comboBanksSecond.removeAllItems();
                
                for(String b : banks){
                    comboBanks.addItem(b);
                }
                
                for(String b : banks){
                    if(comboBanksSecond!=null) comboBanksSecond.addItem(b);
                }
                if(comboBanksSecond!=null){
                    comboBanks.setSelectedItem("MC::particle");
                    comboBanksSecond.setSelectedItem("TAGGER::tagr");
                }
                
            }
        }
    }
    
    public static void main(String[] args){
        PhysicsReaction r = new PhysicsReaction();
        ReactionConfiguration rc = new ReactionConfiguration(null,r,true);
        rc.show();
        System.out.println("first = " + rc.getBankName() 
                + " \nsecond = " + rc.getSecondBankName());
    }
}
