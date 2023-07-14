/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author gavalian
 */
public class DataEditorUtils {
    
    public static JSpinner makeSpinner(int value, int min, int max){
        SpinnerModel model =
                new SpinnerNumberModel(value,min,max,1);

        JSpinner spinner = new JSpinner(model);
        int h = spinner.getHeight();
        spinner.setMaximumSize(new Dimension(90, 25));
        spinner.setMinimumSize(new Dimension(90, 25));
        return spinner;
    }
    
    public static JSpinner makeSpinnerWide(int value, int min, int max){
        SpinnerModel model =
                new SpinnerNumberModel(value,min,max,1);

        JSpinner spinner = new JSpinner(model);
        int h = spinner.getHeight();
        //spinner.setMaximumSize(new Dimension(90, 25));
        //spinner.setMinimumSize(new Dimension(90, 25));
        return spinner;
    }
    
    public static JSpinner makeSpinnerDouble(double value, double min, double max, double step){
        SpinnerModel model =
                new  SpinnerNumberModel(value, min, max, step);

        JSpinner spinner = new JSpinner(model);
        int h = spinner.getHeight();
        spinner.setMaximumSize(new Dimension(90, 25));
        spinner.setMinimumSize(new Dimension(90, 25));
        return spinner;
    }
    
    public static JTextField makeTextField(String value, int length){
        JTextField tf = new JTextField(length);
        tf.setText(value);
        return tf;
    }
    
    public static JComboBox comboLineStyles(){
        String[] data = new String[]{"color 1","color 2", "color 3","color 4"};
        JComboBox box = new JComboBox(data);
        box.setMinimumSize(new Dimension(180,30));
        box.setPreferredSize(new Dimension(180,30));
        
        ColorCellRenderer r = new ColorCellRenderer();
        box.setRenderer(r);
        return box;
    }
    
    
    public static class ColorCellRenderer implements ListCellRenderer {
        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
            
            Icon theIcon = new ColorIcon();
            Font theFont = new Font("Avenir",Font.PLAIN,12);

            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            
            renderer.setFont(theFont);
            renderer.setText(" A ");

            renderer.setIcon(theIcon);
            
            return null;
        }
        
        class ColorIcon implements Icon {

            public ColorIcon() {
            }
            
            public int getIconHeight() {
                return 20;
            }
            
            public int getIconWidth() {
                return 20;
            }
            
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(Color.RED);
                g.drawRect(0, 0, 25, 25);
            }
        }
    }
    
    public static void main(String[] args){
       /* JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComboBox cb = DataEditorUtils.comboLineStyles();
        jf.add(cb);
        jf.pack();
        jf.setVisible(true);*/
       Object elements[][] = {
        { new Font("Helvetica", Font.PLAIN, 20), Color.RED, new MyIcon(), "A" },
        { new Font("TimesRoman", Font.BOLD, 14), Color.BLUE, new MyIcon(), "A" },
        { new Font("Courier", Font.ITALIC, 18), Color.GREEN, new MyIcon(), "A" },
        { new Font("Helvetica", Font.BOLD | Font.ITALIC, 12), Color.GRAY, new MyIcon(), "A" },
        { new Font("TimesRoman", Font.PLAIN, 32), Color.PINK, new MyIcon(), "A" },
        { new Font("Courier", Font.BOLD, 16), Color.YELLOW, new MyIcon(), "A" },
        { new Font("Helvetica", Font.ITALIC, 8), Color.DARK_GRAY, new MyIcon(), "A" } };

    JFrame frame = new JFrame("Complex Renderer");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    ListCellRenderer renderer = new ComplexCellRenderer();
    JComboBox comboBox = new JComboBox(elements);
    comboBox.setRenderer(renderer);
    frame.add(comboBox, BorderLayout.NORTH);
    
    frame.setSize(300, 200);
    frame.setVisible(true);
    }
    
    public static class MyIcon implements Icon {

  public MyIcon() {
  }

  public int getIconHeight() {
    return 20;
  }

  @Override
  public int getIconWidth() {
    return 20;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
      Rectangle2D r = c.getBounds();
      
      g.setColor(Color.RED);    
      g.fillRect(12, 2, 55, 15);
      g.setColor(Color.BLACK);
      g.drawRect(12, 2, 55, 15);
      System.out.println("bounds = " + r);
  }
}
    
    public static class ComplexCellRenderer implements ListCellRenderer {
  protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

  public Component getListCellRendererComponent(JList list, Object value, int index,
      boolean isSelected, boolean cellHasFocus) {
    Font theFont = null;
    Color theForeground = null;
    Icon theIcon = null;
    String theText = null;

    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
        isSelected, cellHasFocus);

    if (value instanceof Object[]) {
      Object values[] = (Object[]) value;
      theFont = (Font) values[0];
      theForeground = (Color) values[1];
      theIcon = (Icon) values[2];
      theText = "";//(String) values[3];
    } else {
      theFont = list.getFont();
      theForeground = list.getForeground();
      theText = "";
    }
    if (!isSelected) {
      renderer.setForeground(theForeground);
    }
    if (theIcon != null) {
      renderer.setIcon(theIcon);
    }
    renderer.setText(theText);
    renderer.setFont(theFont);
    return renderer;
  }
}
}
