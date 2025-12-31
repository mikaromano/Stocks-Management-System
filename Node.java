public class Node<T extends Comparable<T>, V> implements Comparable<Node<T,V>> {
    protected T key;
    protected Node<T,V> left;
    protected Node<T,V> middle;
    protected Node<T,V> right;
    protected Node<T,V> parent;
    protected Node<T,V> sister;
    protected V value;
    protected int size;


    public static final Long LONG_SENTINEL_MAX = Long.MAX_VALUE;
    public static final Long LONG_SENTINEL_MIN = Long.MIN_VALUE;
    public static final String STRING_SENTINEL_MAX = "\uFFFF"; //the value is the maximum ASCII (unicode) value
    public static final String STRING_SENTINEL_MIN = "\u0000"; //the value is the minimum ASCII (unicode) value
    public static final Float FLOAT_SENTINEL_MAX = Float.POSITIVE_INFINITY;
    public static final Float FLOAT_SENTINEL_MIN = Float.NEGATIVE_INFINITY;


    public Node (T key, V value){
        this.key = key;
        this.left = null;
        this.middle = null;
        this.right = null;
        this.parent = null;
        this.sister = null;
        this.value = value;
        if (checkIfSentinel(key)){ // check if the node is sentinel
            this.size = 0;
        } else { // node has a key that is not sentinel
            this.size = 1;
        }
    }

    public boolean checkIfSentinel(T key) {
        if (key instanceof Long && (key.equals(LONG_SENTINEL_MAX) || key.equals(LONG_SENTINEL_MIN))) {
            return true;
        } else if (key instanceof String && (key.equals(STRING_SENTINEL_MAX) || key.equals(STRING_SENTINEL_MIN))) {
            return true;
        } else if (key instanceof Float && (key.equals(FLOAT_SENTINEL_MAX) || key.equals(FLOAT_SENTINEL_MIN))) {
            return true;
        } else if (key instanceof Pair<?, ?>) {
            Pair<?, ?> pairKey = (Pair<?, ?>) key; // Safely cast key to Pair
            Pair<Float, String> sentinelPairMax = new Pair<>(FLOAT_SENTINEL_MAX, STRING_SENTINEL_MAX);
            Pair<Float, String> sentinelPairMin = new Pair<>(FLOAT_SENTINEL_MIN, STRING_SENTINEL_MIN);
            if (pairKey.equals(sentinelPairMax) || pairKey.equals(sentinelPairMin)){
                return true;
            }
        }
        return false;
    }


    // The function find and set the sister of node
    public void setSister(){
        Node<T,V> parent = this.parent;
        // If node is the right child, then sister is the middle child
        if (this == parent.right) {
            if (parent.middle != null) {
                this.sister = parent.middle;
                return;
            }
        }
        // If node is the middle child, then sister is the left child
        if (this == parent.middle){
            if (parent.left != null){
                this.sister = parent.left;
                return;
            }
        }
        // If node is the left child, then we need to find the sister from other parent
        Node<T, V> nextParent = parent.sister; // So we need all nodes will contain sister
        if (nextParent != null) {
            if (nextParent.right != null) {
                this.sister = nextParent.right; // Set sister to right child of parent sister
                return;
            }
            if (nextParent.middle != null) {
                this.sister = nextParent.middle; // Set sister to middle child of parent sister
            }
        }
    }

    // Set the children - left, middle, right of x
    public void setChildren(Node<T,V> x, Node <T,V> left, Node <T,V> middle, Node <T,V> right) {
        // Set this children
        this.left = left; // x.left
        this.middle = middle; // x.middle
        this.right = right; // x.right
        left.parent = x;
        if (middle != null) {
            middle.parent = x;
        }
        if (right != null) {
            right.parent = x;
        }
        this.updateKey();
        this.updateSize();               // update the size in the subtree that the node is its root
        this.updateSizeAfterSplit();     // After setting children and updating size, ensure the parent also updates its size
    }

    public void updateSize() {
        int numOfLeftChilds = 0, numOfMiddleChilds = 0, numOfRightChilds = 0;
        if(left != null) {
            numOfLeftChilds = left.size;
        }
        if(middle != null) {
            numOfMiddleChilds = middle.size;
        }
        if(right != null) {
            numOfRightChilds = right.size;
        }
        this.size = numOfLeftChilds + numOfMiddleChilds + numOfRightChilds;
    }
    // Update the key of x to be the maximum key in its subtree
    public void updateKey() {
        this.key = this.left.key;
        if (this.middle != null) {
            this.key = this.middle.key;
        }
        if (this.right != null) {
            this.key = this.right.key;
        }
    }

    public void setParentInit(Node <T,V> parent){
        this.parent = parent;
    }

    public void setChildInit(Node<T,V> left ,Node <T,V> middle){
        this.left = left;
        this.middle = middle;
    }

    @Override
    public int compareTo(Node<T,V> other) {
        if (this.getKey() == null || other.getKey() == null) {
            throw new IllegalArgumentException();
        }
        int x = this.key.compareTo(other.key);
        return x;
    }

    public T getKey() {
        return this.key;  // Return the key stored in this node
    }

    // Method to update the size of the node and propagate it upwards
    public void updateSizeAfterSplit() {
        if (this.parent != null) {
            this.parent.updateSize();  // Update the size of the parent node
            this.parent.updateSizeAfterSplit(); // Recursively call the updateSizeAfterSplit on the parent to propagate changes upwards
        }
    }
}
