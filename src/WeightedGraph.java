import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeightedGraph<T> extends UnweightedGraph<T> implements Graph<T> {
    private int pathCost;   //only used for secondShortestPath method

    public WeightedGraph(){
        pathCost = 0;
    }

    //Same function as addEdge in UnweightedGraph, however
    // 1) we have to deal with cost 2) if edge already exists, then change the cost and return true
    public boolean addEdge(T from, T to, int cost) {
        /*
               1. if from and to are equal, return false
               2. if from or to aren't nodes present in the graph, return false
               3. if the edge already exists, just change the cost
               4. if the edge doesn't exist, add to the graph

         */
        if(cost <= 0)
            throw new RuntimeException("Edge weight has to be positive");

        GraphNode<T> fromNode = get(from);
        GraphNode<T> toNode = get(to);

        //If the parameters are equal or if the nodes aren't in the graph, return false
        if(from.equals(to) || !(fromNode != null && toNode != null))
            return false;

        //If edge exists, just change its previous cost to the input parameter
        GraphNode<T> temp = fromNode.getEdges().size() > toNode.getEdges().size() ? toNode : fromNode;
        for(GraphEdge<T> edge : temp.getEdges()){
            if(edge.getLocation().equals(from) || edge.getLocation().equals(to)){
                edge.setCost(cost);
                return true;
            }
        }

        //If edge wasn't previously present
        fromNode.getEdges().add(new GraphEdge<>(toNode, cost));
        toNode.getEdges().add(new GraphEdge<>(fromNode, cost));
        numEdges++;
        return true;
    }

    //Attempts to add an edge between the first parameter and each element in the collection.
    //Each edge will have a cost of the input parameter and the method returns true if any of the elements are added successfully.
    public boolean addEdges(T from, Collection<? extends T> to, int cost) {
        boolean changed = false;
        for(T element : to){
            if(addEdge(from, element, cost)) changed = true;
        }

        return changed;
    }

    //Attempts to add an edge between the first parameter and each element in the collection.
    //Each element in "to" has a corresponding weight associated with it in the cost collection.
    //If the collections don't match in size, an exception is thrown. Method returns true if any edges are successfully added
    public boolean addEdges(T from, Collection<? extends T> to, Collection<Integer> cost){
        if(to.size() != cost.size())
            throw new RuntimeException("Collection parameters aren't same size");

        boolean changed = false;
        Iterator<? extends T> iter1 = to.iterator();
        Iterator<Integer> iter2 = cost.iterator();

        while(iter1.hasNext()){
            if(addEdge(from, iter1.next(), iter2.next()))   changed = true;
        }
        return changed;
    }

    //Same purpose and rules as findShortestPath from UnweightedGraph, however Dijkstra's algorithm is used
    @Override
    public List<T> findShortestPath(@NotNull T start, T end){
        List<T> shortestPath = new ArrayList<>();

        ArrayList<GraphNode<T>> unfinalized = new ArrayList<>(nodes);

        GraphNode<T> startNode = get(start);
        GraphNode<T> endNode = get(end);

        if(start.equals(end) || (startNode == null || endNode == null))
            return shortestPath;

        //Make startNode finalized, update the closestDistance of its neighbors

        unfinalized.remove(startNode);
        startNode.finalized = true;

        for(GraphEdge<T> edge : startNode.getEdges()){
            GraphNode<T> neighbor = edge.getLocation();

            neighbor.parent = startNode;
            neighbor.closestDistance = edge.getCost();
        }

        //Main loop for Dijkstra algorithm
        while(unfinalized.size() > 0){

            //find the node in unfinalized with the smalled closestDistance
            GraphNode<T> closestNode = unfinalized.get(0);
            for(int i=1; i<unfinalized.size(); i++){
                GraphNode<T> temp = unfinalized.get(i);

                if(temp.closestDistance < closestNode.closestDistance)
                    closestNode = temp;
            }

            //make the node finalized
            unfinalized.remove(closestNode);
            closestNode.finalized = true;

            if(closestNode.equals(endNode))
                break;

            //update the closestDistances of unfinalized neighbors
            for(GraphEdge<T> edge : closestNode.getEdges()){
                GraphNode<T> neighbor = edge.getLocation();

                if(!neighbor.finalized){
                    int newDistance = closestNode.closestDistance + edge.getCost();

                    if(newDistance < neighbor.closestDistance){
                        neighbor.parent = closestNode;
                        neighbor.closestDistance = newDistance;
                    }
                }
            }
        }

        //store the cost of the entire path...used for secondShortestPath
        pathCost = endNode.closestDistance;

        //if path not found, return empty List
        if(pathCost == Integer.MAX_VALUE)
            return new ArrayList<T>();

        //use parent information to trace the path
        while(endNode != null){
            shortestPath.add(endNode.getData());
            endNode = endNode.parent;
        }

        Collections.reverse(shortestPath);
        resetNodeInformation();
        return shortestPath;
    }

    @Override
    public List<T> secondShortestPath(T start, T end){
        List<T> shortestPath = findShortestPath(start, end);

        List<List<T>> newPaths = new ArrayList<>();
        ArrayList<Integer> pathCosts = new ArrayList<>();       //will store the cost of each new path we calculate

        if(shortestPath.size() < 2)     //either path doesn't exist or path is from the node to itself
            return new ArrayList<>();

        for(int i=shortestPath.size()-1; i>0; i--){
            removeEdge(shortestPath.get(i), shortestPath.get(i-1));

            newPaths.add(findShortestPath(start, end));
            pathCosts.add(pathCost);

            addEdge(shortestPath.get(i), shortestPath.get(i-1));
        }

        //find and return the path with the lowest cost inside the newPaths list
        List<T> toReturn = new ArrayList<>();
        int smallestCost = Integer.MAX_VALUE;

        for(int i=0; i<newPaths.size(); i++){
            if(pathCosts.get(i) < smallestCost){
                smallestCost = pathCosts.get(i);
                toReturn = newPaths.get(i);
            }
        }

        return toReturn;
    }

    //Prints the graph in its adjacency list form, with the cost of each edge
    @Override
    public void print(){
        for(GraphNode<T> node : nodes){
            System.out.print(node.getData() + ": ");
            for(GraphEdge<T> edge : node.getEdges())
                System.out.print(edge.getLocation().getData() + " (" + edge.getCost() + ") ");
            System.out.println();
        }
    }
}
