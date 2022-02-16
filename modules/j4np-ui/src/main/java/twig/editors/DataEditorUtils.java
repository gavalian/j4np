/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.editors;

import java.awt.Dimension;
import javax.swing.JSpinner;
import javax.swing.JTextField;
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
    
    public static JTextField makeTextField(String value, int length){
        JTextField tf = new JTextField(length);
        tf.setText(value);
        return tf;
    }
    
}
