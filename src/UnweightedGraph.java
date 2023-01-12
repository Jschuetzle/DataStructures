import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
    This class is the implementation of an unweighted, undirected graph. An adjacency list is used to store all the neighbors
    connected by an edge of a single node. The adjacency list isn't order alphabetically/numerically because the class accepts generic
    types that don't have to extend the Comparable interface. Two traversal methods and two path algorithms are included with this graph class.
 */

public class UnweightedGraph<T> implements Graph<T> {

    ArrayList<GraphNode<T>> nodes;
    int numEdges;

    //Constructors
    public UnweightedGraph(){
        nodes = new ArrayList<>();
        numEdges = 0;
    }

    protected static class GraphEdge<T> implements Graph.Edge<T>, Comparable<GraphEdge<T>> {
        private GraphNode<T> location;
        private int cost;

        public GraphEdge(){ }

        public GraphEdge(GraphNode<T> to){
            location = to;
            cost = 1;
        }

        public GraphEdge(GraphNode<T> to, int cost){
            location = to;
            this.cost = cost;
        }

        @Override
        public GraphNode<T> getLocation() { return location; }

        @Override
        public int getCost() { return cost; }

        @Override
        public void setCost(int cost){ this.cost = cost;}

        @Override
        public int compareTo(@NotNull GraphEdge<T> o) {
            return cost - o.getCost();
        }
    }

    protected static class GraphNode<T> implements Graph.Node<T> {
        private T data;
        private PriorityQueue<GraphEdge<T>> edges;    //adjacency list

        boolean encountered;
        GraphNode<T> parent;
        Integer closestDistance;
        boolean finalized;

        public GraphNode(T data){
            this.data = data;
            edges = new PriorityQueue<>();

            encountered = false;
            parent = null;
            closestDistance = Integer.MAX_VALUE;
            finalized = false;
        }

        @Override
        public T getData() { return data; }

        @Override
        public PriorityQueue<GraphEdge<T>> getEdges() { return edges; }
    }

    //Adds a node to the graph if it isn't already present
    @Override
    public boolean addNode(@NotNull T data) {
        if(contains(data))
            return false;

        nodes.add(new GraphNode<T>(data));
        return true;
    }

    //Given a collection of elements, each is attempted to be added to the graph as a node.
    //Returns true if any of the elements were successfully added
    @Override
    public boolean addNodes(@NotNull Collection<? extends T> c) {
        boolean changed = false;
        for(T element : c){
            if(addNode(element)) changed = true;
        }
        return changed;
    }

    //Adds an edge between two distinct, already present nodes in the graph.
    //If the edge is already present between the nodes, it returns false.
    @Override
    public boolean addEdge(@NotNull T from, T to) {
        GraphNode<T> fromNode = get(from);
        GraphNode<T> toNode = get(to);

        //If the parameters are equal or if the nodes aren't in the graph, return false
        if(from.equals(to) || (fromNode == null || toNode == null))
            return false;

        //if the edge already exists, return false...look through the node with fewer edges
        GraphNode<T> temp = fromNode.edges.size() > toNode.edges.size() ? toNode : fromNode;
        for(GraphEdge<T> edge : temp.edges){
            if(from.equals(edge.location.data) || to.equals(edge.location.data))
                return false;
        }

        fromNode.edges.add(new GraphEdge<>(toNode));
        toNode.edges.add(new GraphEdge<>(fromNode));
        numEdges++;
        return true;
    }

    //Attempts to add an edge between the first parameter and every element in the collection, assuming
    //both are present in the graph. Returns true if any of the elements were added.
    @Override
    public boolean addEdges(@NotNull T from, Collection<? extends T> to) {
        boolean changed = false;
        for(T element : to){
            if(addEdge(from, element)) changed = true;
        }

        return changed;
    }

    //Performs a breadth-first traversal with a given node. Adjacent nodes are processed in the order
    //in which their edges were added into the graph.
    public Collection<T> breadthFirstTraversal(@NotNull T start) {
        Collection<T> traversal = new ArrayList<>();

        GraphNode<T> origin = get(start);
        if(origin == null) return traversal;

        Queue<GraphNode<T>> queue = new LinkedList<>();

        traversal.add(origin.data);
        queue.add(origin);
        origin.encountered = true;

        while(!queue.isEmpty()){
            GraphNode<T> temp = queue.remove();
            for(GraphEdge<T> neighborLink : temp.edges){
                GraphNode<T> neighborNode = neighborLink.location;

                if(!neighborNode.encountered){
                    neighborNode.encountered = true;

                    traversal.add(neighborNode.data);
                    queue.add(neighborNode);
                }
            }
        }

        resetNodeInformation();
        return traversal;
    }

    //Removes all the nodes and edges from the graph
    @Override
    public void clear(){
        nodes = new ArrayList<>();
        numEdges = 0;
    }

    //Returns true if the parameter is present inside the graph as a node.
    @Override
    public boolean contains(@NotNull T data){
        for(GraphNode<T> node : nodes){
            if(data.equals(node.data))  return true;
        }

        return false;
    }

    //Performs a depth-first traversal on a given node. Adjacent nodes are processed in the order in which
    //their edges were added into the graph.
    public Collection<T> depthFirstTraversal(@NotNull T startNode) {
        Collection<T> traversal = new ArrayList<>();

        GraphNode<T> origin = get(startNode);
        if(origin == null) return traversal;

        traversal.add(origin.data);
        origin.encountered = true;


        depthFirstTraversal(origin, traversal);
        resetNodeInformation();
        return traversal;
    }

    //Recursive helper class for the public class directly above
    private void depthFirstTraversal(@NotNull UnweightedGraph.GraphNode<T> startNode, Collection<T> trav){
        for(GraphEdge<T> edge : startNode.edges){
            GraphNode<T> neighborNode = edge.location;

            if(!neighborNode.encountered){
                neighborNode.encountered = true;
                trav.add(neighborNode.data);

                depthFirstTraversal(neighborNode, trav);
            }
        }
    }

    //Uses a breadth-first search to find the shortest path between two already present nodes in the graph.
    //If a path doesn't exist, an empty list is returned.
    public List<T> findShortestPath(@NotNull T start, T end) {

        List<T> shortestPath = new ArrayList<>();   //path which will be returned
        ArrayList<GraphNode<T>> traversal = new ArrayList<>();

        GraphNode<T> startNode = get(start);
        GraphNode<T> endNode = get(end);
        if(startNode == null || endNode == null) return shortestPath;

        Queue<GraphNode<T>> queue = new LinkedList<>();

        traversal.add(startNode);
        queue.add(startNode);
        startNode.encountered = true;

        while(!queue.isEmpty()){
            if(end.equals(queue.peek().data))     //perform breadth-first traversal...however stop if we've reached the end node
                break;

            GraphNode<T> temp = queue.remove();

            for(GraphEdge<T> neighborLink : temp.edges){
                GraphNode<T> neighborNode = neighborLink.location;

                if(!neighborNode.encountered){
                    neighborNode.encountered = true;
                    neighborNode.parent = temp;

                    traversal.add(neighborNode);
                    queue.add(neighborNode);
                }
            }
        }

        GraphNode<T> endOfTraversal = queue.peek();   //if the front of the queue is null that means there was no path between the nodes
        if(endOfTraversal == null) {
            resetNodeInformation();
            return shortestPath;
        }

        while(endOfTraversal != null){                  //use parent information to trace the path
            shortestPath.add(endOfTraversal.data);
            endOfTraversal = endOfTraversal.parent;
        }

        Collections.reverse(shortestPath);
        resetNodeInformation();
        return shortestPath;
    }

    //Returns the total number of edges in the graph
    @Override
    public int numEdges(){ return numEdges; }

    //Attempts to remove an edge between two distinct, already present nodes in the graph.
    //If the edge isn't present between the nodes, the method returns false.
    @Override
    public boolean removeEdge(@NotNull T from, T to){
        GraphNode<T> fromNode = get(from);
        GraphNode<T> toNode = get(to);

        //if the nodes are equal OR if either of the nodes don't exist in the graph, then return false
        if(from.equals(to) || !(fromNode != null && toNode != null))
            return false;


        GraphNode<T> temp;
        GraphNode<T> temp2;

        //determine which node has fewer neighbors, traverse through it first
        if(fromNode.edges.size() > toNode.edges.size()){
            temp = toNode;
            temp2 = fromNode;
        }
        else {
            temp = fromNode;
            temp2 = toNode;
        }

        boolean found = false;

        //if edge isn't found, found stays false and the method returns false
        //otherwise, edge information is changed for both nodes
        for(GraphEdge<T> edge : temp.edges){
            if(from.equals(edge.location.data) || to.equals(edge.location.data)){
                temp.edges.remove(edge);
                found = true;
            }
        }

        if(found){
            for(GraphEdge<T> edge : temp2.edges){
                if(from.equals(edge.location.data) || to.equals(edge.location.data)){
                    temp2.edges.remove(edge);
                }
            }
            numEdges--;
            return true;
        }
        else
            return false;
    }

    //Attempts to remove the edges connecting the nodes of the first parameter and each element in the collection.
    //Returns true if any of the edges are successfully removed.
    @Override
    public boolean removeEdges(@NotNull T from, Collection<? extends T> to){
        boolean changed = false;
        for(T element : to){
            if(removeEdge(from, element))   changed = true;
        }
        return changed;
    }

    //The given node will be removed from the graph if it's present and the data of the
    //associated node is returned. Returns null if the node isn't present in the graph.
    @Override
    public T removeNode(@NotNull T data) {
        GraphNode<T> toDelete = get(data);
        if(toDelete == null)
            return null;

        //delete information about the nodeToDelete in its neighbors edges
        for(GraphEdge<T> edge : toDelete.edges){
            GraphNode<T> neighbor = edge.location;

            for(GraphEdge<T> neighborEdge : neighbor.edges){
                if(data.equals(neighborEdge.location.data))
                    neighbor.edges.remove(neighborEdge);
            }
        }

        numEdges -= toDelete.edges.size();
        nodes.remove(toDelete);
        return data;
    }

    //Attempts to remove the nodes of each element from the collection.
    //If none of the nodes are present in the graph, the method returns false.
    @Override
    public boolean removeNodes(@NotNull Collection<? extends T> c) {
        boolean changed = false;
        for(T element : c){
            if(removeNode(element) != null) changed = true;
        }

        return changed;
    }

    //Uses breadth-first search to find the second-shortest path between two distinct, already present nodes in the graph.
    //Returns an empty list if there isn't a path or a second-shortest path between the nodes
    public List<T> secondShortestPath(@NotNull T start, T end) {
        /*
            1. Find the shortest path
            2. Remove one edge of the shortest path, and then find the shortest path of this new graph
            3. Put the edge back
            4. Do this for every edge in the actual shortest path
            5. The shortest path out of all of these new paths is the 2nd shortest
         */

        List<T> shortestPath = findShortestPath(start, end);

        if(shortestPath.size() < 2)     //either path doesn't exist or path is from the node to itself
            return new ArrayList<>();

        List<List<T>> newPaths = new ArrayList<>();

        for(int i=shortestPath.size()-1; i>0; i--){
            removeEdge(shortestPath.get(i), shortestPath.get(i-1));
            newPaths.add(findShortestPath(start, end));
            addEdge(shortestPath.get(i), shortestPath.get(i-1));
        }

        //find the shortestPath inside the newPaths list and return it
        List<T> toReturn = new ArrayList<>();
        int smallestListSize = Integer.MAX_VALUE;

        for (List<T> tempPath : newPaths) {
            if (tempPath.size() != 0 && tempPath.size() < smallestListSize) {
                smallestListSize = tempPath.size();
                toReturn = tempPath;
            }
        }

        return toReturn;
    }

    //Return the number of nodes in the graph
    @Override
    public int size(){ return nodes.size(); }

    //Prints the graph in its adjacency list form
    public void print(){
        for(GraphNode<T> node : nodes){
            System.out.print(node.data + ": ");
            for(GraphEdge<T> edge : node.edges)
                System.out.print(edge.location.data + " ");
            System.out.println();
        }
    }

    GraphNode<T> get(T data){
        for(GraphNode<T> node : nodes){
            if(data.equals(node.data))  return node;
        }

        return null;
    }

    //Is necessary if multiple traversals or path algorithms need to be executed in the same program
    void resetNodeInformation(){
        for(GraphNode<T> node : nodes){
            node.encountered = false;
            node.parent = null;
            node.closestDistance = Integer.MAX_VALUE;
            node.finalized = false;
        }
    }
}
