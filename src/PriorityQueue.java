import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PriorityQueue<T extends Comparable<T>> implements Queue<T>, Iterable<T> {

    private ArrayList<T> heapList;      //heap implementation of PriorityQueue
    private boolean reverse;            //if true, then heap is MaxOnTop
    private int size;

    //Constructors
    public PriorityQueue(){
        heapList = new ArrayList<>();
        reverse = false;
        size = 0;
    }

    public PriorityQueue(boolean maxOnTop){
        heapList = new ArrayList<>();
        reverse = maxOnTop;
        size = 0;
    }

    public PriorityQueue(boolean maxOnTop, ArrayList<T> arr){
        heapList = new ArrayList<>(arr);
        reverse = maxOnTop;
        size = arr.size();

        buildHeap();
    }


    //Iterator Implementation
    @Override
    public Iterator<T> iterator() {
        return new PriorityQueueIterator<T>(reverse, heapList);
    }

    static class PriorityQueueIterator<T extends Comparable<T>> implements Iterator<T>{

        private PriorityQueue<T> pQueue;

        public PriorityQueueIterator(boolean reverse, ArrayList<T> heapList){
            pQueue = new PriorityQueue<T>(reverse, heapList);
        }

        @Override
        public boolean hasNext() {
            return pQueue.size() != 0;
        }

        @Override
        public T next() {
            if(!hasNext())
                throw new NoSuchElementException();
            return pQueue.remove();
        }
    }

    //Adds an element to end of the PQueue
    @Override
    public void add(T element) {
        heapList.add(element);
        size++;

        if(size != 1){
            siftUp(size-1);
        }
    }

    //removes all elements from the heapList in constant time
    @Override
    public void clear() {
        for(T element : heapList){
            heapList.remove(element);
            size--;
        }
    }

    //traverses through heapList to check if element is in PQueue
    @Override
    public boolean contains(T element) {
        for(T node : heapList){
            if(node.equals(element))
                return true;
        }

        return false;
    }

    //same as peek() method
    @Override
    public T element() {
        return peek();
    }

    @Override
    public boolean isEmpty(){ return size == 0; }

    //returns but doesn't remove the item with the highest priority
    @Override
    public T peek() {
        return heapList.get(0);
    }

    //same as remove() method
    @Override
    public T poll() {
        return remove();
    }

    //removes and returns the item with the highest priority
    @Override
    public T remove() {
        T toReturn = peek();

        heapList.set(0, heapList.get(size-1));  //deletion
        heapList.remove(size-1);
        size--;

        if(size == 0)
            return toReturn;

        siftDown(0);    //re-adjust the heapList...it's known the top of the heap will be removed
        return toReturn;
    }

    //finds a specified element, then removes element...returns false if not found
    @Override
    public boolean remove(T element) {
        for(int i=0; i<size; i++){

            if(heapList.get(i).equals(element)){
                if(i == size-1){
                    heapList.remove(size-1);
                    size--;
                }
                else {
                    heapList.set(i, heapList.get(size-1));  //deletion
                    heapList.remove(size-1);
                    size--;

                    T node = heapList.get(i);

                    //check whether item is greater than its children or less than its parent
                    //if so...adjust heapList accordingly
                    if(reverse){
                        if((2*i + 1 < size && node.compareTo(heapList.get(2*i + 1)) < 0) || (2*i + 2 < size && node.compareTo(heapList.get(2*i + 2)) < 0))
                            siftDown(i);

                        else if(i != 0 && node.compareTo(heapList.get((i-1) / 2)) > 0)
                            siftUp(i);
                    }
                    else {
                        if((2*i + 1 < size && node.compareTo(heapList.get(2*i + 1)) > 0) || (2*i + 2 < size && node.compareTo(heapList.get(2*i + 2)) > 0))
                            siftDown(i);

                        else if(i != 0 && node.compareTo(heapList.get((i-1) / 2)) < 0)
                            siftUp(i);
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    public String toString(){
        String toReturn = "[";
        int counter = 0;

        Iterator<T> iterator = iterator();
        while(counter < (size-1)){
            toReturn += iterator.next() + ", ";
            counter++;
        }

        toReturn += iterator.next() + "]";
        return toReturn;
    }

    private void siftUp(int index){
        T toSift = heapList.get(index);    //store the value we're sifting up

        int i = index;           //keep track of indices of current node and its parent
        int parent = (i-1)/2;

        while(i > 0){
            int compareValue = toSift.compareTo(heapList.get(parent));

            //if less than parent, siftUp...vice versa if maxOnTop
            if((compareValue > 0 && reverse) || (compareValue < 0 && !reverse)){
                heapList.set(i, heapList.get(parent));  //swap elements
                heapList.set(parent, toSift);

                i = parent;         //change indices
                parent = (i-1)/2;
            }
            else
                break;
        }
    }

    private void siftDown(int index){       //similar to the siftUp() method above...however we are comparing with two children which requires a couple more if statements
        T toSift = heapList.get(index);

        int parent = index;
        int child = 2*parent + 1;

        while(child < size){

            //decides which child is the smallest
            if(child + 1 < size && ((heapList.get(child).compareTo(heapList.get(child+1)) < 0 && reverse) || (heapList.get(child).compareTo(heapList.get(child+1)) > 0 && !reverse))){
                child++;
            }

            //if conditions are met, no swapping occurs...otherwise, swapping occurs below this if statement
            if((toSift.compareTo(heapList.get(child)) > 0 && reverse) || (toSift.compareTo(heapList.get(child)) < 0 && !reverse))
                break;

            heapList.set(parent, heapList.get(child));
            heapList.set(child, toSift);

            parent = child;
            child = 2*parent + 1;
        }
    }

    //takes an unordered arrayList and forms a heap
    private void buildHeap(){
        if(size != 0){
            for(int i = (size-1)/2; i>=0; i--){
                siftDown(i);
            }
        }
    }
}
