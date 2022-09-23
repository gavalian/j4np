/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataGroup {
    
    private List<DataSet>      groupData = new ArrayList<>();
    private List<Double>   axisTickMarks = new ArrayList<>();
    private List<String>  axisTickLabels = new ArrayList<>();
    
    public DataGroup(){
        
    }
    
    public List<DataSet> getData(){ return groupData;}
    public List<Double> getAxisTickMarks(){return this.axisTickMarks;}
    public List<String> getAxisTickLabels(){return this.axisTickLabels;}
}
