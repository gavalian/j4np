/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

/**
 *
 * @author gavalian
 */
public interface OptionApplication {
    public String getKey();
    public String getDescription();
    public void   init(OptionParser parser);
    public void   run(OptionParser parser);
}
