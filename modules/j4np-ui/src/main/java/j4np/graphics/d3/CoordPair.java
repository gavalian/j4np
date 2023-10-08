package j4np.graphics.d3;


public class CoordPair {
    
    double xcoord;
    double ycoord;
    double zcoord;

    public CoordPair(double x, double y, double z) {
        this.xcoord = x;
        this.ycoord = y;
        this.zcoord = z;
    }

    public CoordPair(int x, int y, int z) {
        this((double)x, (double)y, (double)z);
    }

    public CoordPair(int x, int y) {
        this(x, y, 0);
    }

    public CoordPair(double x, double y) {
        this(x, y, 0);
    }

    public CoordPair() {
        this(0, 0, 0);
    }

    @Override
    public String toString() {
        // return String.format("(%d, %d, %d)", (int)x, (int)y, (int)z);
        return String.format("(%8.5f, %8.5f, %8.5f)", xcoord, ycoord, zcoord);
    }

    public void rotateX(double angle){
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double yy = ycoord;
        ycoord = c*yy - s*zcoord;
        zcoord = s*yy + c*zcoord;
    }
    
    public void rotateY(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double zz = zcoord;
        zcoord = c*zz - s*xcoord;
        xcoord = s*zz + c*xcoord;
    }
   
    public void rotateZ(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double xx = xcoord;
        xcoord = c*xx - s*ycoord;
        ycoord = s*xx + c*ycoord;
    }
    
    public void set(double x, double y, double z){
        xcoord = x; ycoord = y; zcoord = z;
    }
    
    public void set(CoordPair cp){
        xcoord = cp.xcoord; ycoord = cp.ycoord; zcoord = cp.zcoord;
    }
    
    public void move(CoordPair cp){
        xcoord += cp.get_x(); ycoord += cp.get_y(); zcoord += cp.get_z();
    }
    
    public double get_x() {
        return this.xcoord;
    }

    public double get_y() {
        return this.ycoord;
    }

    public double get_z() {
        return this.zcoord;
    }
}
