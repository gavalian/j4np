/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.rich;

import j4np.geom.prim.Vector3D;

/**
 *
 * @author gavalian
 */
public class RichHit {
    Vector3D hitPosition = new Vector3D();
    public RichHit(){}
    public RichHit(double x, double y, double z){ hitPosition.setXYZ(x, y, z);}
    public Vector3D position(){return hitPosition;}
}
