/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author gavalian
 */
public class UITools {
    
    public static JPanel  withPanelEmpty(JComponent c, int margin){
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(margin,margin,margin,margin));
        p.setLayout(new BorderLayout());
        p.add(c,BorderLayout.CENTER);
        return p;
    }
    
    public static JPanel  withPanelEtched(JComponent c, int margin){        
        c.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel p = UITools.withPanelEmpty(c,margin);
        return p;
    }
    
     public static JPanel  withPanelTitled(JComponent c, String title, int margin){        
        c.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel tp = new JPanel();
        tp.setLayout(new BorderLayout());
        tp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(margin, margin, margin, margin), title));
        //JPanel p = UITools.withPanelEmpty(c,margin);
        tp.add(c,BorderLayout.CENTER);
        return tp;
    }
     
     public static JPanel  withPanelTitled2(JComponent c, String title, int margin){        
        c.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel tp = new JPanel();
        tp.setLayout(new BorderLayout());
        tp.setBorder(BorderFactory.createTitledBorder(title));
        //JPanel p = UITools.withPanelEmpty(c,margin);
        tp.add(c,BorderLayout.CENTER);
        return UITools.withPanelEmpty(tp, margin);
    }
    
    public static Icon getColorIcon(Color color, int width, int height){
        int padding = 2;
         BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
         Graphics2D g2d = image.createGraphics();
         g2d.setColor(color);
         g2d.fillRect(padding, padding, width - 2*padding, height-2*padding);
         Icon icon = new ImageIcon(image);
         return icon;
    }
    
    public static JComboBox getColorComboBox(){
        Color[] colors = new Color[]{
            Color.BLACK,Color.BLUE,Color.CYAN, Color.GREEN,
            Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.RED
        };
        Vector model = new Vector();

        for(int j = 0; j < colors.length; j++){
            Icon icon = UITools.getColorIcon(colors[j], 60, 12);
            IconItem item = new IconItem(icon,"");
            model.addElement(item);
        }
        
        JComboBox box = new JComboBox(model);
        box.setRenderer(new ItemRenderer());
        return box;
    }
    
    public static class ItemRenderer extends BasicComboBoxRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected,
                    cellHasFocus);
            
            IconItem item = (IconItem) value;
            
            //    if (index == -1) {
            //  setText(item.getText());
            //  setIcon(null);
            //} else {
            setText(item.getText());
            setIcon(item.getIcon());
            //}
            return this;
        }
    }

    public  static class IconItem {
        private Icon icon;
        private String text;
        public IconItem(Icon icon, String text){this.icon = icon; this.text = text;}
        public Icon   getIcon(){ return this.icon;}
        public String getText(){ return this.text;}
    }
    
    //public 
    
    public static void main(String[] args){
        JFrame fr = new JFrame();
        JComboBox cb = UITools.getColorComboBox();
        fr.add(cb);
        fr.pack();
        fr.setVisible(true);
    }
}
