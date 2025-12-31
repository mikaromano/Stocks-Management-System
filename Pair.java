public class Pair<K extends Comparable<K>, V extends Comparable<V>> implements Comparable<Pair<K, V>> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int compareTo(Pair<K, V> other) {
        // First compare by key, then by value
        int keyComparison = this.key.compareTo(other.key);

        if (keyComparison != 0) { // If keys are not equal, return the comparison result
            return keyComparison;
        } else if(this.value != null && other.value != null){  // keys are equal, then compare values
            return this.value.compareTo(other.value);
        } else{
            return keyComparison;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return (key == null ? other.key == null : key.equals(other.key)) &&
                (value == null ? other.value == null : value.equals(other.value));
    }

    @Override
    public int hashCode() {
        int result = 17; // arbitrary non-zero constant
        result = 31 * result + (key == null ? 0 : key.hashCode());
        result = 31 * result + (value == null ? 0 : value.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}