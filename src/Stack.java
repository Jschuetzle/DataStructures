import java.util.*;


//override some of the classes from vector later...
public class Stack<T> extends Vector<T> implements Iterable<T> {

    private ArrayList<T> stack;
    private int size;

    public Stack(){
        stack = new ArrayList<>();
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new StackIterator<T>(stack);
    }

    private static class StackIterator<T> implements Iterator<T>{

        private ArrayList<T> stack;
        private int index;

        public StackIterator(ArrayList<T> s){
            stack = new ArrayList<T>(s);
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return stack.size() != 0;
        }

        @Override
        public T next() {
            if(index >= stack.size())
                throw new NoSuchElementException();

            T toReturn = stack.get(index);
            index++;
            return toReturn;
        }
    }

    public boolean empty(){
        return size == 0;
    }

    public T peek(){
        return stack.get(0);
    }

    public T pop(){
        if(empty())
            throw new EmptyStackException();
        T toReturn = stack.remove(0);
        size--;
        return toReturn;
    }

    public boolean push(T element){
        stack.add(0, element);
        size++;
        return true;
    }

    public int search(T element){
        for(int i=size-1; i>=0; i--){
            if(stack.get(i).equals(element))
                return size - i;
        }

        return -1;
    }

    @Override
    public void clear(){
        for(T element : stack){
            stack.remove(element);
            size--;
        }
    }

    @Override
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
}
