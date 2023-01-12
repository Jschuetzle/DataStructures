import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class AVLTree<T extends Comparable<T>> extends BinarySearchTree<T> {

    private static class AVLTreeNode<T> extends TreeNode<T> {
        private T data;

        private AVLTreeNode<T> left;
        private AVLTreeNode<T> right;
        private AVLTreeNode<T> parent;

        private int balance;

        public AVLTreeNode(T data){
            this.data = data;
            left = right = null;
            parent = null;

            balance = 0;
        }

        @Override
        public T getData(){ return data; }

        @Override
        public TreeNode<T> getLeft() { return left; }

        @Override
        public TreeNode<T> getRight() { return right; }
    }

    private AVLTreeNode<T> root;

    public AVLTree(){
        root = null;
    }

    public AVLTree(T data){
        super(data);
        root = new AVLTreeNode<>(data);
    }

    @Override
    public void add(T data){
        AVLTreeNode<T> temp = root;
        AVLTreeNode<T> parent = null;

        while(temp != null){        //traverse down the tree with the temp variable
            parent = temp;

            if(data.compareTo(temp.data) < 0)
                temp = temp.left;
            else
                temp = temp.right;
        }

        if(parent == null)                  //case where tree started off empty
            root = new AVLTreeNode<T>(data);
        else if(data.compareTo(parent.data) < 0) {      //add new element as a leaf...either left or right child
            parent.left = new AVLTreeNode<T>(data);
            parent.left.parent = parent;
        }
        else {
            parent.right = new AVLTreeNode<T>(data);
            parent.right.parent = parent;
        }

        size++;
        rebalance(parent);
    }

    @Override
    public void addAll(@NotNull Collection<? extends T> c){
        for(T element : c)
            add(element);
    }

    @Override
    public void clear(){
        clear(root.left, root);
        clear(root.right, root);

        root = null;
        size--;
    }

    @Override
    public boolean contains(T data) {
        return contains(data, root);
    }

    @Override
    public boolean containsAll(@NotNull Collection<? extends T> c){
        for(T element : c){
            if(!contains(element)) return false;
        }

        return true;
    }

    @Override
    public int depth(){ return depth(root); }

    @Override
    public T getRoot(){ return root.data; }

    @Override
    public boolean isEmpty(){ return root == null; }

    @Override
    public Collection<T> inOrderTraversal(){
        Collection<T> toReturn = new ArrayList<>();
        inOrder(root, toReturn);
        return toReturn;
    }

    public Collection<T> levelOrderTraversal(){
        return levelOrder(root);
    }

    @Override
    public Collection<T> preOrderTraversal(){
        Collection<T> toReturn = new ArrayList<>();

        preOrder(root, toReturn);
        return toReturn;
    }

    @Override
    public Collection<T> postOrderTraversal(){
        Collection<T> toReturn = new ArrayList<>();

        postOrder(root, toReturn);
        return toReturn;
    }

    @Override
    public T remove(T data){
        /*
                The first part of this method is almost identical to the remove process of a binary search tree
                except the removeHelper() method had to account for updating parent pointers and balances
                of ancestry nodes when the replacement was needed

                The second part of this method deals with investigating the ancestry of the node
                that was deleted in order to make sure the avl tree is still balanced...there
                are many cases that need to be checked so the while loop is quite bulky

         */

        AVLTreeNode<T> parent = null;      //find the nodeToDelete
        AVLTreeNode<T> temp = root;

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

            /*
                  The while loop below conducts the investigation on the ancestry of the node that was deleted.
                  Any reference of parent.balance is the previous balance of the parent before deletion...we
                  need this info for certain cases.

                  Case 1: If the parent's balance did not change, stop while loop

                  Case 2: If the parent's balance changed from originally being zero...
                          - update its balance
                          - stop the while loop

                  Case 3: If the parent's balance was previously +/- 1 and the taller subtree was shortened...
                          - change the balance factor of parent to zero
                          - keep on investigating ancestry

                  Case 4: If the parent's balance was previously +/- 1 and the shorter subtree was shortened...
                          - Let q be the root of the taller subtree

                          - 4a) If balance factor of q is zero...
                                - single rotation around parent
                                - stop the while loop

                          - 4b) If balance factor of q is same as parent...
                                - single rotation around parent
                                - keep on investigating ancestry

                          - 4c) If balance factor of q is different from parent...
                                - double rotation around parent
                                - keep on investigating ancestry
             */

            boolean shorter = true;
            while(shorter && parent != null){
                int actualBalance = findBalance(parent);

                if(parent.balance == actualBalance)     //case 1
                    shorter = false;
                else if(parent.balance == 0){           //case 2
                    parent.balance = actualBalance;
                    shorter = false;
                }
                else {
                    if(actualBalance == 0)                  //case 3
                        parent.balance =  actualBalance;
                    else if(actualBalance == 2){            //this else if and the else below it are identical...just for opposite directions

                        if(parent.left.balance == 0){     //case 4a
                            rightRotation(parent);
                            shorter = false;
                        }
                        else if(parent.left.balance == 1) {     //case 4b
                            rightRotation(parent);
                            parent = parent.parent;
                        }
                        else
                            doubleRotationLeftRight(parent);    //case 4c

                    }
                    else {

                        if(parent.right.balance == 0){      //4a
                            leftRotation(parent);
                            shorter = false;
                        }
                        else if(parent.right.balance == -1){    //4b
                            leftRotation(parent);
                            parent = parent.parent;
                        }
                        else
                            doubleRotationRightLeft(parent);    //4c

                    }
                }
                parent = parent.parent;
            }
            return toReturn;    //Investigation over...return data that was removed
        }
        return null;    //If the data wasn't found in the tree, return null
    }

    @Override
    public boolean removeAll(@NotNull Collection<? extends T> c){
        boolean changed = false;
        for(T element : c){
            if(remove(element) != null) changed = true;
        }

        return changed;
    }

    @Override
    public String toString(){
        return levelOrderTraversal().toString();
    }

    //same function as removeHelper in parent class, just more operations are needed
    private void removeHelper(@NotNull AVLTreeNode<T> toDelete, AVLTreeNode<T> parent){

        //for cases where the node to be deleted has either 1) no children or 2) one child
        if(toDelete.left == null || toDelete.right == null){
            AVLTreeNode<T> toDeleteChild = null;

            if(toDelete.left != null) {
                toDeleteChild = toDelete.left;          //checking if the only child is left or right
                toDelete.left.parent = parent;
            }
            else if(toDelete.right != null) {
                toDeleteChild = toDelete.right;
                toDelete.right.parent = parent;
            }



            if(toDelete.equals(root)) {          //determining whether parent's pointer should point left, right, or to root
                root = toDeleteChild;
            }
            else if(toDelete.data.compareTo(parent.data) < 0)
                parent.left = toDeleteChild;
            else
                parent.right = toDeleteChild;

        }
        else {
            AVLTreeNode<T> replacementParent = toDelete;          //case for when the node to be deleted has 2 children
            AVLTreeNode<T> replacement = toDelete.right;

            while(replacement.left != null){
                replacementParent = replacement;            //find the smallest value on the to be deleted node's
                replacement = replacement.left;             //right subtree
            }

            toDelete.data = replacement.data;

            removeHelper(replacement, replacementParent);       //remove the replacement node from its previous position

            toDelete.balance = findBalance(toDelete);

            AVLTreeNode<T> trav = toDelete.right;       //we need to update the balances of the replacement nodes ancestry
            trav.balance = findBalance(trav);           //because the "shorter" while loop only examines the ancestry of the higher order
            trav = trav.left;                           //node that was deleted

            while(trav != null){
                trav.balance = findBalance(trav);
                trav = trav.left;
            }
        }
    }

    private void rebalance(AVLTreeNode<T> node){
        /*
            This method investigates the ancestry of a node and determines if rotations are needed for rebalancing
            the AVL tree.

            There are 3 specific cases that we have to handle

            Case 1: Node's balance changed from 0 to +/- 1
                    - No rotations
                    - Keep investigating ancestors

            Case 2: Node's balance changed from +/- 1 to 0
                    - No rotation needed
                    - Stop investigation

            Case 3: Node's balance changed from +/- 1 to +/- 2
                    - Let x be the child of node's taller subtree

                    - Case 3a) If the node is added to x's outer subtree
                               - a single right/left rotation is required around node
                               - stop investigation

                    - Case 3b) If the node is added to x's inner subtree
                                - a double rotation is required around x first, then node
                                - stop investigation
         */

        if(node == null) return;

        node.balance = depth(node.left) - depth(node.right);    //update balance

        if(Math.abs(node.balance) == 1){    //case 1
            rebalance(node.parent);
        }
        else if(Math.abs(node.balance) == 2){

            if(node.balance > 0){               //this if statement and the corresponding else are identical...just for different directions
                if(node.left.balance > 0)
                    rightRotation(node);    //case 3a
                else
                    doubleRotationLeftRight(node);  //case 3b
            }
            else {
                if(node.right.balance > 0)
                    doubleRotationRightLeft(node);  //3a
                else
                    leftRotation(node);     //3b
            }
        }
    }

    private void rightRotation(AVLTreeNode<T> node){
        /*
              Let's assume the parameter node is the one we are rotating around

              A right rotation will require these switches to be made...

                    1. The pivot's parent will have to point to the pivot's left child
                    2. The parent variable of the pivot's left child will have to mirror this change  (there will be a temp variable involved in steps 1&2)
                    3. The parent variable of pivot will have to change to its old left child

                    4. The right child of the pivot's left child will now point to pivot
                    5. The left child of pivot will now be the old right child mentioned above  (temp variable needed for steps 3&4)
                    6. If the right child of the pivot's left child isn't null, we need to change its parent variable to be pivot

                    7. Update the balance of the pivot and pivot's old left child

              The reason there's an if statement is to differentiate when we rotate around the root of the AVL Tree...

              -  If we ARE rotating around the root, then calling node.parent.left (or .right) would produce a NullPointerException
              -  If we ARE NOT rotating around the root, we need to determine if we're working with node.parent.left or node.parent.right
         */

        if(node.parent == null){
            root = node.left;       //special case for rotating around root...steps 1,2,3
            root.parent = null;
            node.parent = root;

        }
        else{
            if(node.equals(node.parent.left))       //step 1 general case
                node.parent.left = node.left;
            else
                node.parent.right = node.left;

            node.left.parent = node.parent;         //steps 2,3
            node.parent = node.left;
        }

        AVLTreeNode<T> tempChild = node.left.right;     //swapping...for steps 4,5
        node.left.right = node;
        node.left = tempChild;

        if(tempChild != null)             //if applicable, step 6
            tempChild.parent = node;

        int leftChildDepth = depth(node.left);            //step 7
        int rightChildDepth = depth(node.right);
        int nodeDepth = Math.max(leftChildDepth, rightChildDepth) + 1;

        node.balance = leftChildDepth - rightChildDepth;
        node.parent.balance = depth(node.parent.left) - nodeDepth;
    }

    private void leftRotation(AVLTreeNode<T> node){
        /*
                This is a replica of the rightRotation method with the same 7 steps; the only difference is that the direction
                we are rotating is now opposite. If you are confused with how this method functions, read the description
                for the rightRotation method, switch every left with right and every right with left.

         */

        if(node.parent == null){
            root = node.right;       //special case for rotating around root...steps 1,2,3
            root.parent = null;
            node.parent = root;

        }
        else{
            if(node.equals(node.parent.left))       //step 1 general case
                node.parent.left = node.right;
            else
                node.parent.right = node.right;

            node.right.parent = node.parent;         //steps 2,3
            node.parent = node.right;
        }

        AVLTreeNode<T> tempChild = node.right.left;     //swapping...for steps 4,5
        node.right.left = node;
        node.right = tempChild;

        if(tempChild != null)             //if applicable, step 6
            tempChild.parent = node;

        int leftChildDepth = depth(node.left);            //step 7
        int rightChildDepth = depth(node.right);
        int nodeDepth = Math.max(leftChildDepth, rightChildDepth) + 1;

        node.balance = leftChildDepth - rightChildDepth;
        node.parent.balance = nodeDepth - depth(node.parent.right);
    }

    private void doubleRotationRightLeft(AVLTreeNode<T> node){
        rightRotation(node.right);
        leftRotation(node);
    }

    private void doubleRotationLeftRight(AVLTreeNode<T> node){
        leftRotation(node.left);
        rightRotation(node);
    }

    private int findBalance(@NotNull AVLTreeNode<T> node){
        return depth(node.left) - depth(node.right);
    }
}
