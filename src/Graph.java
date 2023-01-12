import java.util.Collection;
import java.util.List;

public interface Graph<T> {
    boolean addNode(T data);

    boolean addNodes(Collection<? extends T> c);

    boolean addEdge(T from, T to);

    boolean addEdges(T from, Collection<? extends T> to);

    void clear();

    boolean contains(T data);

    int numEdges();

    boolean removeEdge(T from, T to);

    boolean removeEdges(T from, Collection<? extends T> to);

    T removeNode(T data);

    boolean removeNodes(Collection<? extends T> c);

    int size();

    static interface Edge<T> {
        Graph.Node<T> getLocation();
        int getCost();
        void setCost(int cost);
    }

    static interface Node<T> {
        T getData();
        PriorityQueue<? extends Edge<T>> getEdges();
    }
}
