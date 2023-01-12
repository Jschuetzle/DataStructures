import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class BinarySearchTree<T extends Comparable<T>> {

    //RESEARCH ABOUT CHANGING THIS TO PROTECTED RATHER THAN PRIVATE
    static class TreeNode<T> {
        private T data;
        private TreeNode<T> left;
        private TreeNode<T> right;

        public TreeNode(){}

        public TreeNode(T data){
            this.data = data;
            left = right = null;
        }

        public T getData(){ return data; }
        public TreeNode<T> getLeft() { return left; }
        public TreeNode<T> getRight() { return right; }
    }

    private TreeNode<T> root;
    int size;

    public BinarySearchTree (){
        root = null;
        size = 0;
    }

    public BinarySearchTree(T data){
        root = new TreeNode<T>(data);
        size = 1;
    }

    //DO A CONSTRUCTOR LATER HAVING AN ENTIRE COLLECTION BE A PARAMETER

    //Any methods that have a default access modifier are also used by the AVL Tree class...
    //A new method has to be made for the AVL tree class because of the incompatibility of roots between the two classes

    public void add(T data){
        TreeNode<T> temp = root;
        TreeNode<T> parent = null;

        while(temp != null){        //traverse down the tree with the temp variable
            parent = temp;

            if(data.compareTo(temp.data) < 0)
                temp = temp.left;
            else
                temp = temp.right;
        }

        if(parent == null)                  //case where tree started off empty
            root = new TreeNode<T>(data);
        else if(data.compareTo(parent.data) < 0)      //add new element as a leaf...either left or right child
            parent.left = new TreeNode<T>(data);
        else
            parent.right = new TreeNode<T>(data);

        size++;
    }

    public void addAll(@NotNull Collection<? extends T> c){
        for(T element : c)
            add(element);
    }

    public void clear(){
        clear(root.left, root);
        clear(root.right, root);

        root = null;
        size--;
    }

    void clear(TreeNode<T> node, TreeNode<T> parent){
        if(node == null) return;

        clear(node.getLeft(), node);
        clear(node.getRight(), node);

        if(node.equals(parent.getLeft()))
            parent.left = null;
        else
            parent.right = null;

        size--;
    }

    public boolean contains(T data){
        return contains(data, root);
    }

    boolean contains(T data, TreeNode<T> node){
        TreeNode<T> temp = node;        //Traverse down the tree until the data is found/not found

        while(temp != null){
            if(data.equals(temp.getData()))
                return true;
            else if(data.compareTo(temp.getData()) < 0)
                temp = temp.getLeft();
            else
                temp = temp.getRight();
        }

        return false;
    }

    public boolean containsAll(@NotNull Collection<? extends T> c){
        for(T element : c){
            if(!contains(element)) return false;
        }

        return true;
    }

    public T getRoot(){ return root.data; }

    public boolean isEmpty(){ return root == null; }

    public Collection<T> inOrderTraversal(){
        Collection<T> toReturn = new ArrayList<>();
        inOrder(root, toReturn);
        return toReturn;
    }

    void inOrder(TreeNode<T> node, Collection<T> trav){
        if(node != null){
            inOrder(node.getLeft(), trav);
            trav.add(node.getData());
            inOrder(node.getRight(), trav);
        }
    }

    public Collection<T> levelOrderTraversal(){
        return levelOrder(root);
    }

    Collection<T> levelOrder(TreeNode<T> node){
        Collection<T> toReturn = new ArrayList<>();
        if(node == null)
            return toReturn;

        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.add(node);

        while(queue.size() != 0){
            TreeNode<T> temp = queue.remove();
            if(temp != null){
                toReturn.add(temp.getData());

                queue.add(temp.getLeft());
                queue.add(temp.getRight());
            }
        }

        return toReturn;
    }

    public Collection<T> preOrderTraversal(){
        Collection<T> toReturn = new ArrayList<>();

        preOrder(root, toReturn);
        return toReturn;
    }

    void preOrder(TreeNode<T> node, Collection<T> trav){
        if(node != null){
            trav.add(node.getData());
            preOrder(node.getLeft(), trav);
            preOrder(node.getRight(), trav);
        }
    }

    public Collection<T> postOrderTraversal(){
        Collection<T> toReturn = new ArrayList<>();

        postOrder(root, toReturn);
        return toReturn;
    }

    void postOrder(TreeNode<T> node, Collection<T> trav){
        if(node != null){
            postOrder(node.getLeft(), trav);
            postOrder(node.getRight(), trav);
            trav.add(node.getData());
        }
    }

    public int depth(){
        return depth(root);
    }

    int depth(TreeNode<T> node){
        if(node == null)
            return -1;
        else {
            int leftHeight = depth(node.getLeft());
            int rightHeight = depth(node.getRight());

            return Math.max(leftHeight, rightHeight) + 1;
        }
    }

    public T remove(T data){
        TreeNode<T> parent = null;
        TreeNode<T> temp = root;

        //just searching for the nodeToDelete
        while(temp != null && !temp.data.equals(data)){
            parent = temp;
            if(data.compareTo(temp.data) < 0)
                temp = temp.left;
            else
                temp = temp.right;
        }

        if(temp != null){
            T toReturn = temp.data;
            removeHelper(temp, parent);      //actual deletion of the node
            size--;
            return toReturn;
        }

        return null;
    }

    private void removeHelper(@NotNull TreeNode<T> toDelete, TreeNode<T> parent){
        if(toDelete.left == null || toDelete.right == null){
            TreeNode<T> toDeleteChild = null;          //for cases where the node to be deleted has
                                                    //either no children or one child
            if(toDelete.left != null)
                toDeleteChild = toDelete.left;          //checking if the only child is left or right
            else if(toDelete.right != null)
                toDeleteChild = toDelete.right;


            if(toDelete.equals(root))
                root = toDeleteChild;               //value comparisons to determine where the parent's pointer should point
            else if(toDelete.data.compareTo(parent.data) < 0)
                parent.left = toDeleteChild;
            else
                parent.right = toDeleteChild;

        }
        else {
            TreeNode<T> replacementParent = toDelete;          //case for when the node to be deleted has 2 children
            TreeNode<T> replacement = toDelete.right;

            while(replacement.left != null){
                replacementParent = replacement;            //find the largest value on the to be deleted node's
                replacement = replacement.left;             //right subtree
            }

            toDelete.data = replacement.data;                 //replace the to be deleted node

            removeHelper(replacement, replacementParent);
        }
    }

    public boolean removeAll(@NotNull Collection<? extends T> c){
        boolean changed = false;
        for(T element : c){
            if(remove(element) != null) changed = true;
        }

        return changed;
    }

    public int size(){ return size; }

    //IM DEALING WITH THIS LATER BECAUSE IT'S EXTREMELY DIFFICULT...THERE WAS A SOLUTION TO LOOK AT ON STACK OVERFLOW
    @Override
    public String toString(){
        return levelOrder(root).toString();
    }
}
