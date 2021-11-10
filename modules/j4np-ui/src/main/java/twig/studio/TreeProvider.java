/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package twig.studio;

import javax.swing.tree.TreeModel;
import twig.graphics.TGDataCanvas;

/**
 * Interface for implementing different data providers
 * such as file, http histogram service and tuples.
 * @author gavalian
 */
public interface TreeProvider {
    public TreeModel getTreeModel();
    public void      draw(String path, TGDataCanvas c);
}
