/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.data;

/**
 *
 * @author gavalian
 */
public class TrackCondition {
    public static boolean isValid(Tracks list,int row){
        double chi2 = list.bank.getDouble( 4, row);
        double   vz = list.bank.getDouble(10, row);
        return (chi2<10.0&&vz>-15&&vz<5);
    }
}
