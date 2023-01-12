import java.util.Collection;
import java.util.Set;

public interface Map<K, V> {

    void clear();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    Set<Map.Entry<K, V>> entrySet();

    V get(Object key);

    boolean isEmpty();

    Set<K> keySet();

    V put(K key, V value);

    void putAll(Map<? extends K, ? extends V> m);

    V remove(Object key);

    int size();

    String toString();

    Collection<V> values();

    static interface Entry<K, V>{
        K getKey();
        V getValue();
        V setValue(V value);
    }
}
