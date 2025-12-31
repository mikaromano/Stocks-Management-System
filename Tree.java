public class Tree<T extends Comparable<T>, V> {
    public Node<T,V> root;

    public static final Long LONG_SENTINEL_MAX = Long.MAX_VALUE;
    public static final Long LONG_SENTINEL_MIN = Long.MIN_VALUE;
    public static final String STRING_SENTINEL_MAX = "\uFFFF"; //the value is the maximum ASCII (unicode) value
    public static final String STRING_SENTINEL_MIN = "\u0000"; //the value is the minimum ASCII (unicode) value
    public static final Float FLOAT_SENTINEL_MAX = Float.POSITIVE_INFINITY;
    public static final Float FLOAT_SENTINEL_MIN = Float.NEGATIVE_INFINITY;


    public Tree(){
        this.root = null;
    }

    public void init(T sentinelMin, T sentinelMax){
        // Creates 3 new nodes with sentinels
        Node<T, V> x = new Node<>(sentinelMax, null);  // Root node with max sentinel
        Node<T, V> l = new Node<>(sentinelMin, null);  // Left child with min sentinel
        Node<T, V> m = new Node<>(sentinelMax, null);  // Right child with max sentinel
        // Set node l, m parent
        x.setChildInit(l, m);
        // Send to Node class to set node x childes
        l.setParentInit(x);
        m.setParentInit(x);
        // Init the root of the tree
        this.root = x;
    }

    public T getSentinelMax() {
        T key = root.getKey();
        if (key instanceof Long) {
            return (T) LONG_SENTINEL_MAX;
        } else if (key instanceof String) {
            return (T) STRING_SENTINEL_MAX; // Maximum Unicode value
        } else if (key instanceof Float) {
            return (T) Float.valueOf(FLOAT_SENTINEL_MAX);
        } else if (key instanceof Pair<?, ?>) {
            return (T) new Pair<>(FLOAT_SENTINEL_MAX, STRING_SENTINEL_MAX);
        }
        return null;
    }

    public void insert(Node<T,V> stock) {   //stock variable is stockID
        Node<T,V> y = this.root;
        while(y.left != null){ //check if y is not a leaf
            if(stock.compareTo(y.left) < 0){    // compare with the left children of y
                y = y.left;
            } else if(stock.compareTo(y.middle) < 0){   // compare with the middle children of y
                y = y.middle;
            } else {
                y = y.right;
            }
        }
        Node<T,V> x = y.parent;
        stock = insertAndSplit(x, stock); // we want to add stock as children of x
        while (x != this.root) {
            x = x.parent;
            if(stock != null){
                stock = insertAndSplit(x, stock);
            } else {
                x.updateKey();
            }
        }
        if(stock != null){ // case that we create new root and connect the two nodes to it
            Node<T,V> w = new Node<T,V>(null, null);
            w.setChildren(w,x, stock, null);
            this.root = w;
            stock.setSister();
            stock.left.setSister();
        }
    }

    public Node<T,V> insertAndSplit(Node<T,V> x, Node<T,V> stock) {
        Node<T,V> l = x.left;
        Node<T,V> m = x.middle;
        Node<T,V> r = x.right;
        if(r == null){
            if(stock.compareTo(l) < 0){
                x.setChildren(x,stock, l, m);
                x.left.setSister(); // In case stock is left Children update stock sister
                x.middle.setSister(); // update sister of the middle children of x after adding stock as left child
            } else if (stock.compareTo(m) < 0){
                x.setChildren(x,l, stock, m);
                x.middle.setSister(); // In case stock is middle Children update stock sister
                x.right.setSister(); // update sister of the right children of x after adding stock as middle child
            } else {
                x.setChildren(x,l, m, stock);
                x.right.setSister(); // In case stock is right Children update stock sister
                // Stock is x.right, then send to successor and find sister that should update
                stock.left.setSister();
            }
            return null;
        }
        Node<T,V> y = new Node<T,V>(null,null);
        //split the 4 children of x to be 2 children of x and 2 children of y
        if (stock.compareTo(l) < 0) {
            x.setChildren(x,stock, l, null);
            y.setChildren(y,m, r, null);
            x.middle.setSister();
            x.left.setSister();
        } else if (stock.compareTo(m) < 0) {
            x.middle.sister = stock;
            x.setChildren(x,l, stock, null);
            y.setChildren(y,m, r, null);
            x.middle.setSister();
        } else if (stock.compareTo(r) < 0) {
            x.setChildren(x,l, m, null);
            y.setChildren(y,stock, r, null);
            y.left.setSister();   // !!!!!!!!!!!!!!!!!!!!
            y.middle.setSister();
        } else {
            x.setChildren(x,l, m, null);
            y.setChildren(y,r, stock, null);
            y.middle.setSister();
            y.sister = x;
        }
        return y;
    }

    public Node<T,V> search(Node<T,V> x,T key) { // x is the root and key is of the leaf we want to find
        if(x.left == null){
            if (x.key.compareTo(key) == 0){
                return x;
            }
            return null;
        }
        if((key.compareTo(x.left.key)) <= 0){
            return search(x.left, key);
        } else if(key.compareTo(x.middle.key) <= 0){
            return search(x.middle, key);
        } else{
            return search(x.right, key); //we saw in lecture that the max sentinel ensures existence of right child
        }
    }

    public Node<T,V> minimum() {    // Find the leaf with the smallest key in the tree
        Node <T,V> x = this.root;
        while(x.left != null){
            x = x.left;
        }
        x = x.parent.middle;
        if(x.key.compareTo(getSentinelMax()) != 0){  //check if the key is not +inf, if the condition is false - we know that x.key smaller than inf
            return x;
        }
        return null;    // tree is empty - we'll never get here
    }

    public Node<T,V> successor(Node<T,V> x) {   // find the leaf y with the smallest key among those with y.key > x.key
        Node<T,V> z = x.parent;
        Node<T,V> y = null;
        while(x == z.right || (z.right == null && x == z.middle)){
            x = z;
            z = z.parent;
        }
        if (x == z.left){
            y = z.middle;
        } else{
            y = z.right;
        }
        while (y.left != null) {
            y = y.left;
        }
        if (y.key.compareTo(getSentinelMax()) < 0) {
            return y;
        } else {
            return null;
        }
    }

    public Node<T, V> successorNotOnlyForLeaves(Node<T, V> x) {
        Node<T, V> z = x.parent;
        Node<T, V> y = null;
        int levelUp = 0; // Track how many levels we moved up
        // Traverse up until we find a valid parent where x is NOT the rightmost child
        while (z != null && (x == z.right || (z.right == null && x == z.middle))) {
            x = z;
            z = z.parent;
            levelUp++; // Keep track of how many levels we've moved up
        }
        // If x was not the rightmost child, find the next node in the same level
        if (z != null) {
            if (x == z.left) {
                y = z.middle;
            } else if (x == z.middle) {
                y = z.right;
            }
        }
        // Descend back down to the original height
        while (y != null && levelUp > 0) {
            y = y.left; // Always move left to stay within the correct subtree level
            levelUp--;
        }
        // Return the valid successor at the correct height
        return y;
    }

    public void delete(Node<T,V> x) {
        Node<T,V> y = x.parent;
        if(x == y.left){
            y.middle.sister = y.left.sister; // update y.middle sister to point on the update sister
            y.setChildren(y,y.middle, y.right, null); // delete y.left
        } else if(x == y.middle){
            Node<T,V> rightSister = successor(x); /// added now
            if (rightSister != null) {
                rightSister.sister = x.sister; // added now
            }
            y.setChildren(y,y.left, y.right, null);
            if (y.middle != null){ // in case there is 2 children to y after deleting
                y.middle.sister = y.left;
            }
        } else { // x is y.right
            Node<T,V> sister = successor(y.right);
            sister.sister = y.middle;
            y.setChildren(y,y.left, y.middle, null);
        }
        // In this point, we disconnected x, then the garbage collector will delete it

        while(y != null) {
            if(y.middle != null) {
                y.updateKey();
                y = y.parent;
            } else { // In case y has 1 child
                if(y != root){
                    y = borrowOrMerge(y);
                } else{
                    root = y.left;
                    y.left.parent = null;
                    return;
                    // garbage collector will delete y
                }
            }
        }
    }

    public Node<T, V> borrowOrMerge(Node<T,V> y) {
        Node<T,V> z = y.parent;
        if(y == z.left){
            Node<T,V> x = z.middle;
            if(x.right != null){ // x has 3 children
                y.setChildren(y,y.left, x.left, null);
                x.setChildren(x,x.middle, x.right, null);
            } else { // x has 2 children
                x.setChildren(x,y.left, x.left, x.middle);
                y.left = null; // then garbage collector will delete y
                z.setChildren(z,x, z.right, null);
                x.setSister(); // after deleting y, we want x to point on its new sister
            }
            return z;
        }
        if(y == z.middle){
            Node<T,V> x = z.left;
            if(x.right != null){ // x has 3 children
                y.setChildren(y,x.right, y.left, null);
                x.setChildren(x,x.left, x.middle, null);
            } else { // x has 2 children
                if (z.right == null){
                    Node<T,V> sister = successorNotOnlyForLeaves(y);
                    if(sister != null){
                        sister.sister = x;
                    }
                }
                x.setChildren(x,x.left, x.middle, y.left);
                y.left = null; // then garbage collector will delete y
                y.sister = null;
                z.setChildren(z,x, z.right, null);
                if(z.middle != null) {
                    z.middle.sister = x;
                }
            }
            return z;
        }
        Node <T,V> x = z.middle;
        if(x.right != null) { // x has 3 children
            y.setChildren(y,x.right, y.left, null);
            x.setChildren(x,x.left, x.middle, null);
        } else { // x has 2 children
            x.setChildren(x,x.left, x.middle, y.left);
            y.left = null;
            z.setChildren(z,z.left, x, null);
            Node<T,V> sister = successorNotOnlyForLeaves(x);
            if(sister != null){
                sister.setSister();
            }
        }
        return z;
    }

    public int rank(Node<T,V> x) {  // returns the position of x.key in the linear order of the leaves
        int rank = 1;
        Node<T,V> y = x.parent;
        while(y != null) {
            if(x == y.middle) {
                rank += y.left.size;
            } else if(x == y.right) {
                rank = rank + y.left.size + y.middle.size;
            }
            x = y;
            y = y.parent;
        }
        return rank;
    }
}