import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HashMap<K, V> implements Map<K, V> {

    public static class Entry<K, V> implements Map.Entry<K, V> {       //LOOK INTO IF PRIVATE STATIC IS SUITABLE HERE
        private K key;
        private V value;

        public Entry(K key, V value){
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() { return key; }

        @Override
        public V getValue() { return value; }

        @Override
        public V setValue(V value) {
            V toReturn = this.value;
            this.value = value;
            return toReturn;
        }

        public String toString(){
            return key + "=" + value;
        }
    }


    //INSTEAD OF USING LINKEDLISTS FOR EACH BUCKET, JAVA NOW USES AVL TREES...WE CAN INCORPORATE THIS LATER

    private LinkedList<Entry<K, V>>[] hashMap;      //linkedList is for separate chaining
    private float maxLoadFactor;
    private int capacity;
    private int items;

    public HashMap(){               //default constructor
        maxLoadFactor = 0.75F;
        capacity = 16;
        items = 0;

        hashMap = new LinkedList[capacity];
    }

    public HashMap(int initialCapacity){        //alternative constructors below
        maxLoadFactor = 0.75F;
        capacity = initialCapacity;
        items = 0;

        hashMap = new LinkedList[capacity];
    }

    public HashMap(int initialCapacity, float loadFactor){
        maxLoadFactor = loadFactor;
        capacity = initialCapacity;
        items = 0;

        hashMap = new LinkedList[capacity];
    }

    //THE LAST CONSTRUCTOR IMPLEMENTATION IS ONE WITH AN INPUT MAP

    @Override
    public V put(K key, V value){
        Entry<K, V> entryToAdd = new Entry<>(key, value);

        int hashCode = Math.abs(entryToAdd.key.hashCode()) % capacity;

        items++;

        if(hashMap[hashCode] == null){                              //if bucket is null, initialize new LinkedList
            hashMap[hashCode] = new LinkedList<>();
            hashMap[hashCode].add(entryToAdd);

            if((items / (float) capacity) >= maxLoadFactor)
                rehash();
        }
        else {
            LinkedList<Entry<K, V>> bucket = hashMap[hashCode];

            //have to check if key is already present...if so, just change its value
            for(Entry<K, V> entry : bucket){
                if(key.equals(entry.key)){
                    V toReturn = entry.value;
                    entry.value = value;

                    return toReturn;
                }
            }

            //if key not present, just add the entry

            bucket.add(entryToAdd);
            if((items / (float) capacity) > maxLoadFactor)
                rehash();

        }
        return null;
    }


    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if(m != null){
            for(Map.Entry<? extends K, ? extends V> entry : m.entrySet()){
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void clear() {
        for(int i=0; i<capacity; i++){
            if(hashMap[i] != null){
                hashMap[i].clear();
                hashMap[i] = null;
            }
        }

        items = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();

        for(int i=0; i<capacity; i++){
            if(hashMap[i] != null){
                for(Entry<K, V> entry : hashMap[i])
                    keys.add(entry.key);
            }
        }

        return keys;
    }

    @Override
    public Collection<V> values() {
        //instantiate a Collection list to store the values in
        //traverse over hashMap and add each value to the Collection

        Collection<V> values = new ArrayList<>();

        for(int i=0; i<capacity; i++){
            if(hashMap[i] != null){
                for(Entry<K, V> entry : hashMap[i])
                    values.add(entry.value);
            }
        }

        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = new HashSet<>();

        for(int i=0; i<capacity; i++){
            if(hashMap[i] != null){
                for(Entry<K, V> entry : hashMap[i]){
                    entrySet.add(entry);
                }
            }
        }

        return entrySet;
    }

    @Override
    public V remove(Object key){
        int hashCode = Math.abs(key.hashCode()) % capacity;     //map the input key with hash function
        LinkedList<Entry<K, V>> bucket = hashMap[hashCode];

        if(bucket == null)         //if bucket is null, we would get an error if we try to traverse it
            return null;

        for(Entry<K, V> entry : bucket){
            if(key.equals(entry.key)){
                V toReturn = entry.value;
                bucket.remove(entry);

                items--;

                if(bucket.size() == 0)          //just a preference...if a bucket has no items in it, I want it to be null
                    hashMap[hashCode] = null;

                return toReturn;
            }
        }

        return null;
    }

    @Override
    public int size(){ return items; }

    @Override
    public boolean isEmpty() { return items == 0; }

    @Override
    public boolean containsKey(Object key) {
        int hashCode = Math.abs(key.hashCode()) % capacity;

        if(hashMap[hashCode] == null)
            return false;
        else {

            //loop through each entry in the bucket and see if the key exists
            for(Entry<K, V> entry : hashMap[hashCode]){
                if(key.equals(entry.key))
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        //we can't just search up with a hashed key
        //so we have to traverse the entire hashMap

        for(int i=0; i<capacity; i++){
            if(hashMap[i] != null){
                for(Entry<K, V> entry : hashMap[i]){
                    if(value.equals(entry.value))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        int hashCode = Math.abs(key.hashCode()) % capacity;

        if(hashMap[hashCode] != null){
            for(Entry<K, V> entry : hashMap[hashCode]){
                if(key.equals(entry.key))
                    return entry.value;

            }
        }

        return null;
    }

    @Override
    public String toString(){
        String toReturn = "{";

        Set<Map.Entry<K, V>> entries = entrySet();
        for(Map.Entry<K, V> entry : entries){
            toReturn += entry.toString() + ", ";
        }

        return toReturn.substring(0, toReturn.length()-2) + "}";
    }

    private void rehash(){
        LinkedList<Entry<K, V>>[] oldMap = hashMap;    //store pre-rehashing state of hashmap

        capacity = 2*capacity + 1;              //update variables
        hashMap = new LinkedList[capacity];
        items = 0;

        //add all entries from oldMap onto the new one that was created
        for(LinkedList<Entry<K, V>> bucket : oldMap){

            //if there exists a linkedlist at a bucket, add each of its elements to new hashMap
            if(bucket != null){
                for(Entry<K, V> entry : bucket){
                    put(entry.key, entry.value);
                }
            }
        }
    }
}