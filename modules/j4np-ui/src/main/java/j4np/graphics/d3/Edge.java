package j4np.graphics.d3;



import java.util.HashSet;

public class Edge {
    // Node x;
    // Node y;
    HashSet<Node> nodes = new HashSet<Node>();

    public Edge(Node x, Node y) {
        if (x == null) {
            throw new IllegalArgumentException("x passed to edge constructor was null");
        } else if (y == null) {
            throw new IllegalArgumentException("y passed to edge constructor was null");
        }
        // this.x = x;
        // this.y = y;
        nodes.add(x);
        nodes.add(y);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Edge)) {
            return false;
        }
        Edge o = (Edge)other;
        if (this.nodes.size() != o.nodes.size()) {
            return false;
        }
        for (Node n : this.nodes) {
            if (!o.nodes.contains(n)) {
                return false;
            }
        }
        return true;
        // return (this.nodes == other.nodes);
    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
    }
}