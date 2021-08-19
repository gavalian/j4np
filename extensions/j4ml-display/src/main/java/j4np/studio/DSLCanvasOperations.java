/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.studio;

import j4ml.temp.DataStudio;
import j4np.utils.dsl.DSLCommand;
import j4np.utils.dsl.DSLSystem;
import org.jlab.groot.data.GraphErrors;

/**
 *
 * @author gavalian
 */

@DSLSystem (system="canvas", info="graph operations module")
public class DSLCanvasOperations {
    
    @DSLCommand(
            command="divide",
            info="divides canvs into x columns and y row",
            defaults={"2","2"},
            descriptions={"x - number of columns",
                "y - number of rows"}
    ) 
    public void divide(int x, int y){
        DataStudio.getInstance().getDefaultCanvas().divide(x, y);
    }
    
    @DSLCommand(
            command="cd",
            info="changes active data region of canvas",
            defaults={"0"},
            descriptions={"region number to change it to"}
    ) 
    public void cd(int region){
        DataStudio.getInstance().getDefaultCanvas().cd(region);
    }
    
    @DSLCommand(
            command="axis",
            info="changes axis limits for given data region",
            defaults={"0","0.0","1.0","0.0","1.0"},
            descriptions={"region number ",
                "x axis minimum", "x axis maximum", "y axis minimum",
            "y axis maximum"}
    ) 
    public void axis(int region,double xmin, double xmax, double ymin, double ymax){
        DataStudio.getInstance().getDefaultCanvas().getDataCanvas()
                .getRegion(region).getGraphicsAxis()
                .setAxisLimits(xmin, xmax, ymin, ymax);
        DataStudio.getInstance().getDefaultCanvas().repaint();
    }
    
    @DSLCommand(
            command="divisions",
            info="changes axis divisions",
            defaults={"0","10","10"},
            descriptions={"region number ",
                "x axis divisions", "y axis divisions"}
    ) 
    public void divisions(int region,int nDivisionsX, int nDivisionsY){
        DataStudio.getInstance().getDefaultCanvas().getDataCanvas()
                .getRegion(region).getGraphicsAxis()
                .getAxisX().setAxisTicks(nDivisionsX);
        
        DataStudio.getInstance().getDefaultCanvas().getDataCanvas()
                .getRegion(region).getGraphicsAxis()
                .getAxisY().setAxisTicks(nDivisionsY);
        DataStudio.getInstance().getDefaultCanvas().repaint();
    }
}
