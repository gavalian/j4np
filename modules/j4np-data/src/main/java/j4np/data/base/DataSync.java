/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package j4np.data.base;

/**
 * This is an interface to implement data output classes.
 * @author gavalian
 */
public interface DataSync {
    public boolean open(String filename);
    public boolean add(DataEvent event);
    public void    addFrame(DataFrame frame);
    public void    close();
}
