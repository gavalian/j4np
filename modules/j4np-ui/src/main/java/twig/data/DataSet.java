/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

import java.awt.geom.Rectangle2D;
import twig.config.TDataAttributes;

/**
 *
 * @author gavalian
 */
public interface DataSet {
    
    public String getName();
    public void   setName(String name);
    public int    getSize(int dimention);
    public void   getPoint(DataPoint point, int... coordinates);
    public void   getRange(DataRange range);
    /**
     * return data plotting attributes.
     * @return 
     */
    public TDataAttributes attr();
}
