/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics.settings;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author gavalian
 */
public class DataAttributes {
    
    public int lineColor = 1;
    public int lineWidth = 1;
    public int lineStyle = 1;
    
    public int  markerSize = 8;
    public int markerColor = 1;
    public int markerStyle = 1;
    
    public int markerOutlineColor = 1;
    public int markerOutlineWidth = 1;
    
    public int fillColor = 0;
    public int fillStyle = 0;
    
    public String   dataName = "unknown";
    public String  dataTitle = "";
    public String dataTitleX = "";
    public String dataTitleY = "";
    public String dataTitleZ = "";
    
    
    public static class DataAttributesPanel  extends JPanel {
        DataAttributes attr = null;
        
        public DataAttributesPanel(DataAttributes nAttr){
            super();
            attr = nAttr;
            initUI();
        }
        
        private void initUI(){
            
            setLayout(new MigLayout("","[left][right][right]",""));
        
            JSeparator separator = new JSeparator();
            JLabel lineLabel = new JLabel("Line:");
            //labelLabel.setText("JLabel:");
            add(lineLabel, "cell 0 0");
            //add(separator, "cell 1 0 2 1,w 80!");
            //add(new JSeparator(SwingConstants.HORIZONTAL),"cell 0 1");
            
            JLabel     labelLineColor = new JLabel("Color:");
            JSpinner spinnerLineColor = new JSpinner();
            
            spinnerLineColor.setValue(attr.lineColor);
            
            spinnerLineColor.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent ev) {
                    System.out.println( ((JSpinner) ev.getSource()).getValue().toString());
                }
            });
            
            add(labelLineColor,"cell 1 1");
            add(spinnerLineColor,"cell 2 1");
        
            JLabel     labelLineWidth = new JLabel("Width:");
            JSpinner spinnerLineWidth = new JSpinner();
            
            add(labelLineWidth,"cell 1 2");
            add(spinnerLineWidth,"cell 2 2");
            
            JLabel     labelLineStyle = new JLabel("Style:");
            JSpinner spinnerLineStyle = new JSpinner();
            
            add(labelLineStyle,"cell 1 3");
            add(spinnerLineStyle,"cell 2 3");
            
            JLabel markerLabel = new JLabel("Marker:");
            //labelLabel.setText("JLabel:");
            add(markerLabel, "cell 0 4");
            
            JLabel     labelMarkerColor = new JLabel("Color:");
            JSpinner spinnerMarkerColor = new JSpinner();
            
            add(labelMarkerColor,"cell 1 5");
            add(spinnerMarkerColor,"cell 2 5");
        
            JLabel     labelMarkerWidth = new JLabel("Size:");
            JSpinner spinnerMarkerWidth = new JSpinner();
            
            add(labelMarkerWidth,"cell 1 6");
            add(spinnerMarkerWidth,"cell 2 6");
            
            JLabel     labelMarkerStyle = new JLabel("Style:");
            JSpinner spinnerMarkerStyle = new JSpinner();
            
            add(labelMarkerStyle,"cell 1 7");
            add(spinnerMarkerStyle,"cell 2 7");
            
            JLabel fillLabel = new JLabel("Fill:");
            //labelLabel.setText("JLabel:");
            add(fillLabel, "cell 0 8");
            
            JLabel     labelFillColor = new JLabel("Color:");
            JSpinner spinnerFillColor = new JSpinner();
            
            add(labelFillColor,"cell 1 9");
            add(spinnerFillColor,"cell 2 9");
            
            JLabel     labelFillStyle = new JLabel("Style:");
            JSpinner spinnerFillStyle = new JSpinner();
            
            add(labelFillStyle,"cell 1 10");
            add(spinnerFillStyle,"cell 2 10");
            
            
            JLabel     labelTitles = new JLabel("Titles:");
            add(labelTitles, "cell 0 11");
            
            JLabel     labelDataTitle = new JLabel("Title:");
            JTextField textDataTitle = new JTextField();

            add(labelDataTitle, "cell 0 12");
            //add(textDataTitle , "cell 1 12,width 120");
            add(textDataTitle , "cell 1 12 2 1,w 120!");
            /*
            JLabel     labelDataTitleX = new JLabel("X Title:");
            JTextField textDataTitleX = new JTextField();

            add(labelDataTitleX, "cell 0 13");
            add(textDataTitleX, "cell 1 13 2 1,width 80!");
            
            JLabel     labelDataTitleY = new JLabel("Y Title:");
            JTextField textDataTitleY = new JTextField();

            add(labelDataTitleY, "cell 0 14");
            add(textDataTitleY, "cell 1 14 2 1,width 80!");
            
            JLabel     labelOptions = new JLabel("Options:");
            add(labelOptions, "cell 0 15");
            
            JLabel     labelDataOptions = new JLabel("Draw Option:");
            JTextField textDataOptions = new JTextField();

            add(labelDataOptions, "cell 0 16");
            add(textDataOptions,  "cell 1 16 2 1,width 80!");
            
            JLabel     labelStatOptions = new JLabel("Stat Option:");
            JTextField textStatOptions = new JTextField();

            add(labelStatOptions, "cell 0 17");
            add(textStatOptions, "cell 1 17 2 1,width 80!");
            
            */
        }
        
        
    }
}
