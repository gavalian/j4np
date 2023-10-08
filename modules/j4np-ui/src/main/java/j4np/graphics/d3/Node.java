package j4np.graphics.d3;


import java.util.ArrayList;

public class Node {
    private CoordPair pos;
    private ArrayList<Node> connected = new ArrayList<Node>();

    /**
     * Constructor for making node at given position
     * will make a new CoordPair object
     * @param x the x position
     * @param y the y position
     * @param z the z position
     */
    public Node(double x, double y, double z) {
        this.pos = new CoordPair(x, y, z);
    }

    public Node(int x, int y, int z) {
        this((double)x, (double)y, (double)z);
    }

    public Node(double x, double y) {
        this(x, y, 0);
    }

    public Node(int x, int y) {
        this(x, y, 0);
    }

    /**
     * Constructor for making node with position of given coordinate pair
     * will use the given CoordPair object
     * @param pos the CoordPair object to use for the position of the new node
     */
    public Node(CoordPair pos) {
        this.pos = pos;
    }

    public Node() {
        this(0, 0, 0);
    }

    /**
     * Adds another node to the arraylist of connected nodes
     * @param n the node to add to the connected list
     * @param fill true: add this node to n's connected list; false: no change to n's list
     */
    public void add_connection(Node n, boolean fill) {
        connected.add(n);
        if (fill) {
            n.add_connection(this, false);    
        }
    }

    public void add_connection(Node n) {
        add_connection(n, true);
    }

    public CoordPair get_pos() {
        return this.pos;
    }

    public double get_angle() {
        return 0.0;//this.directionAngle;
    }

    public void set_pos(CoordPair newPos) {
        if (newPos == null) {
            throw new IllegalArgumentException("###ERROR: Null position passed to the Node's set_pos() function");
        }
        this.pos = newPos;
    }

    public ArrayList<Node> get_connected() {
        return this.connected;
    }
}