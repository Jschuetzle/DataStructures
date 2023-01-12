import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Queue<T>, Iterable<T> {

    private Node<T> head;
    private int size;


    //CONSIDER THERE'S MAYBE MORE THAN ONE CONSTRUCTOR...
    public LinkedList(){     //constructor
        head = null;
        size = 0;
    }

    //abstract method from Iterable to override
    @Override
    public LinkedListIterator<T> iterator() {
        return new LinkedListIterator<T>(head);
    }

    //Class for the iterator of this class
    private static class LinkedListIterator<T> implements Iterator<T> {
        private Node<T> node;

        public LinkedListIterator(Node<T> head){
            node = head;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public T next() {
            if(!hasNext())
                throw new NoSuchElementException();
            T value = node.data;
            node = node.next;
            return value;
        }
    }

    //Node class
    private static class Node<T> {
        private T data;
        private Node<T> next;

        public Node(T data){
            this.data = data;
            next = null;
        }
    }


    /*
        PUBLIC METHODS OF THE LINKED LIST
     */



    //appends the element to the end of the linkedlist
    @Override
    public void add(T data){
        Node<T> toAdd = new Node<T>(data);

        if(head == null)
            head = toAdd;
        else{
            Node<T> node = findElementAtIndex(size-1);
            node.next = toAdd;
        }
        size++;
    }

    //appends the element to the specified index of the LinkedList
    public boolean add(int index, T data){
        isIndexInBounds(index, size);

        Node<T> toAdd = new Node<>(data);
        ArrayList<Node<T>> nodes = findElementAndParentAtIndex(index);

        if(nodes.get(1) == null)
            head = toAdd;
        else
            nodes.get(1).next = toAdd;

        toAdd.next = nodes.get(0);
        size++;
        return true;
    }

    //Removes all elements from the linkedlist
    @Override
    public void clear(){
        for(int i=0; i<size; i++){
            remove();
            size--;
        }
    }

    //returns true if element is contained in the linkedlist, false otherwise
    @Override
    public boolean contains(T element){
        Node<T> node = head;
        for(int i=0; i<size; i++){
            if(node.data.equals(element))
                return true;
            node = node.next;
        }
        return false;
    }

    @Override
    public T element(){ return peek(); }

    //Returns the element at a specified index
    public T get(int index){
        isIndexInBounds(index, size-1);
        return findElementAtIndex(index).data;
    }

    @Override
    public boolean isEmpty(){ return size == 0; }

    //Returns the first index of a specified element. Returns -1 if element isn't present
    public int indexOf(T element){
        Node<T> temp = head;
        for(int i=0; i<size; i++){
            if(temp.data.equals(element))
                return i;
            temp = temp.next;
        }

        return -1;
    }

    //returns the last index of a specified element. Returns -1 if the element isn't present
    public int lastIndexOf(int element){
        for(int i=size-1; i>=0; i--){
            Node<T> node = findElementAtIndex(i);

            if(node.data.equals(element))
                return i;
        }

        return -1;
    }

    //Returns but does not remove the first item in the linkedlist
    @Override
    public T peek(){
        return head == null ? null : head.data;
    }

    //Returns but does not remove the last item in the linkedlist
    public T peekLast(){
        Node<T> node = findElementAtIndex(size-1);
        return node == null ? null : node.data;
    }

    @Override
    public T poll(){ return remove(); }

    //Removes and returns first item in the linkedlist
    @Override
    public T remove(){
        Node<T> temp = head;
        if(head == null)
            throw new NoSuchElementException();
        head = temp.next;

        size--;
        return temp.data;
    }

    //Returns and removes item at the specified index of the linkedlist
    public T remove(int index){
        isIndexInBounds(index, size-1);

        if(index == 0)
            return remove();

        ArrayList<Node<T>> forDeletion = findElementAndParentAtIndex(index);
        forDeletion.get(1).next = forDeletion.get(0).next;

        size--;
        return forDeletion.get(0).data;
    }

    //returns true if element is found and removed, false otherwise
    @Override
    public boolean remove(T element){
        ArrayList<Node<T>> nodes = findElementAndParent(element);
        if(nodes == null)
            return false;
        else if(nodes.get(1) == null)
            head = nodes.get(0).next;
        else {
            nodes.get(1).next = nodes.get(0).next;
        }

        size--;
        return true;
    }

    //Changes the element at some specified index to a value given by the user, returns the previous value in that index
    public T set(int index, T data){
        isIndexInBounds(index, size-1);

        Node<T> node = findElementAtIndex(index);
        T oldData = node.data;

        node.data = data;
        return oldData;
    }

    //getter method for size
    @Override
    public int size(){ return size; }

    //Returns a nice looking String of the linkedlist
    public String toString(){
        if(size == 0)
            return "[]";

        String finalStr = "[";

        Node temp = head;
        //Starting from the head, traverse through the list, while printing the data of each node
        while(temp.next != null){
            finalStr += (temp.data + ", ");
            temp = temp.next;
        }

        finalStr += temp.data + "]";
        return finalStr;
    }

    //Searches for a specified element...returns the element and its parent in an ArrayList
    //The order of the ArrayList is {node, parent}
    private ArrayList<Node<T>> findElementAndParent(T element){
        ArrayList<Node<T>> nodes = new ArrayList<>();

        Node<T> temp = head;
        Node<T> parent = null;

        for(int i=0; i<size; i++){
            if(temp.data.equals(element)){
                nodes.add(temp);
                nodes.add(parent);
                return nodes;
            }

            parent = temp;
            temp = temp.next;
        }
        return null;
    }

    //returns the element at a specified index
    private Node<T> findElementAtIndex(int index){
        Node<T> temp = head;
        for(int i=0; i<index; i++){
            temp = temp.next;
        }

        return temp;
    }

    //Given an index, it returns an ArrayList containing the node at that index and its parent
    //The ArrayList order is the same as the findElementAndParent(T element) method
    private ArrayList<Node<T>> findElementAndParentAtIndex(int index){
        ArrayList<Node<T>> nodes = new ArrayList<>();
        Node<T> temp = head;
        Node<T> parent = null;

        for(int i=0; i<index; i++){
            parent = temp;
            temp = temp.next;
        }

        nodes.add(temp);
        nodes.add(parent);
        return nodes;
    }

    //determines if an exception needs to be thrown for a specified index
    private void isIndexInBounds(int index, int max){
        if(index < 0 || index > max)
            throw new IndexOutOfBoundsException("Custom");
    }
}