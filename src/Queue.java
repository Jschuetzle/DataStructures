public interface Queue<T> {
    void add(T data);

    //put a toAdd(Collection<T> c) here eventually

    void clear();

    boolean contains(T element);

    T element();

    boolean isEmpty();

    T peek();

    T poll();

    T remove();

    boolean remove(T element);

    int size();

    String toString();
}
