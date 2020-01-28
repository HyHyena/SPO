package Collections;

public class HashSet {
    private LinkedList[] buckets;
    private int size;

    public HashSet(int capacity) {
        buckets = new LinkedList[capacity];
        init();
        size = 0;
    }

    public boolean add(Object o) {
        int index = hashFunction(o.hashCode());
        LinkedList currentBucket = buckets[index];

        if (currentBucket.contains(o) != -1) {
            return false;
        }

        currentBucket.add(o);
        size++;
        return true;
    }

    public boolean remove(Object o) {
        int index = hashFunction(o.hashCode());
        LinkedList currentBucket = buckets[index];
        if (currentBucket.contains(o) != -1) {
            currentBucket.remove(o);
            size--;
        }
        return false;
    }

    public boolean contains(Object o) {
        int index = hashFunction(o.hashCode());
        LinkedList currentBucket = buckets[index];
        return currentBucket.contains(o) != -1;
    }


    private int hashFunction(int hashCode) {
        int index = hashCode;
        if (index < 0) {
            index = -index;
        }
        return index % buckets.length;
    }

    private void init() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList();
        }
    }

    public String toString() {
        int i;
        StringBuilder s = new StringBuilder("[");
        for (i = 0; i < buckets.length - 1; i++) {
            s.append(buckets[i].toString()).append(", ");
        }
        s.append(buckets[i].toString()).append("]");
        return s.toString();
    }

    public int size() {
        return size;
    }
}