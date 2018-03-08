import java.util.NoSuchElementException;

/**
 *  The {@code AVLTreeST} class represents an ordered symbol table of
 *  generic key-value pairs. It supports the usual <em>put</em>, <em>get</em>,
 *  <em>contains</em>, <em>delete</em>, <em>size</em>, and <em>is-empty</em>
 *  methods. It also provides ordered methods for finding the <em>minimum</em>,
 *  <em>maximum</em>, <em>floor</em>, and <em>ceiling</em>. It also provides a
 *  <em>keys</em> method for iterating over all of the keys. A symbol table
 *  implements the <em>associative array</em> abstraction: when associating a
 *  value with a key that is already in the symbol table, the convention is to
 *  replace the old value with the new value. Unlike {@link java.util.Map}, this
 *  class uses the convention that values cannot be {@code null}
 *  —setting the value associated with a key to {@code null} is
 *  equivalent to deleting the key from the symbol table.
 *  <p>
 *  This symbol table implementation uses internally an
 *   AVL tree (Georgy
 *  Adelson-Velsky and Evgenii Landis' tree) which is a self-balancing BST.
 *  In an AVL tree, the heights of the two child subtrees of any
 *  node differ by at most one; if at any time they differ by more than one,
 *  rebalancing is done to restore this property.
 *  <p>
 *  This implementation requires that the key type implements the
 *  {@code Comparable} interface and calls the {@code compareTo()} and
 *  method to compare two keys. It does not call either {@code equals()} or
 *  {@code hashCode()}. The <em>put</em>, <em>get</em>, <em>contains</em>,
 *  <em>delete</em>, <em>minimum</em>, <em>maximum</em>, <em>ceiling</em>, and
 *  <em>floor</em> operations each take logarithmic time in the worst case. The
 *  <em>size</em>, and <em>is-empty</em> operations take constant time.
 *  Construction also takes constant time.
 */
public class AVLTreeST<Key extends Comparable<Key>, Value> {

    /**
     * the representation of an inner node
     */
    private class Node {
        private final Key key; //the key
        private Value value;   //the associated value
        private int height; //the height of the subtree
        private int size; //the number of nodes in the subtree
        private Node left; //left subtree
        private Node right; //right subtree

        /**
         * constructor of Node class
         * @param key key
         * @param value value, can't be null
         * @param height height of the subtree
         * @param size number of nodes in the subtree (including self)
         */
        public Node(Key key, Value value, int height, int size) {
            this.key = key;
            this.value = value;
            this.height = height;
            this.size = size;
        }
    }

    /**
     * the root node
     */
    private Node root;

    /**
     * construct an empty tree
     */
    public AVLTreeST() {
    }

    /**
     * check if the tree is empty
     * @return {@code true} if the tree is empty
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * return the number of key-value pairs in this tree
     * @return the number of key-value pairs in this tree
     */
    public int size() {
        return size(root);
    }

    /**
     * return the number of key-value pairs in this subtree (including self)
     * @param x the subtree
     * @return the number of key-value pairs in this subtree
     */
    private int size(Node x) {
        if (x == null)
            return 0;
        return x.size;
    }

    /**
     * return the height of the tree
     * @return the height of the tree
     */
    public int height() {
        return height(root);
    }

    /**
     * return the height of the subtree
     * @param x the subtree
     * @return the height of the subtree
     */
    private int height(Node x) {
        if (x == null)
            return -1;
        return x.height;
    }

    /**
     * return the associated value with the key
     * null if no such key
     * @param key the key
     * @return the associated value
     * @throws IllegalArgumentException if key is null
     */
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key);
        if (x == null) return null;
        return x.value;
    }

    /**
     * return the associated value with the key in the subtree
     * null if no such key
     * @param x the subtree
     * @param key the key
     * @return the associated value
     */
    private Node get(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return get(x.left, key);
        } else if (cmp > 0) {
            return get(x.right, key);
        } else {
            return x;
        }
    }

    /**
     * check if the tree contains the key
     * @param key the key
     * @return true if the tree contains the key
     * @throws IllegalArgumentException if key is null
     */
    public boolean contains(Key key) {
        return get(key) != null;
    }

    /**
     * put a key value pair into the tree
     * if the value is null, delete the key from the tree
     * @param key the key
     * @param value the value
     */
    public void put(Key key, Value value) {
        if (key == null) throw new IllegalArgumentException("argument to put() is null");
        if (value == null) {
            delete(key);
            return;
        }
        root = put(root, key, value);
        assert check();
    }

    private Node put(Node x, Key key, Value value) {
        if (x == null) return new Node(key, value, 0, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, value);
        } else if (cmp > 0) {
            x.right = put(x.right, key, value);
        } else {
            x.value = value;
            return x;
        }
        x.size = 1 + size(x.left) + size(x.right);
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return balance(x);
    }

    private Node balance(Node x) {
        if (balanceFactor(x) > 1) {
            if (balanceFactor(x.left) < 0) {
                x.left = rotateLeft(x.left);
            }
            x = rotateRight(x);
        } else if (balanceFactor(x) < -1) {
            if (balanceFactor(x.right) > 0) {
                x.right = rotateRight(x.right);
            }
            x = rotateLeft(x);
        }
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        y.size = x.size;
        x.size = 1 + size(x.left) + size(x.right);
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        return y;
    }

    private Node rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        y.right = x;
        y.size = x.size;
        x.size = 1 + size(x.left) + size(x.right);
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        return y;
    }

    private int balanceFactor(Node x) {
        return height(x.left) - height(x.right);
    }

    /**
     * delete a key from the tree if present
     * @param key the key
     */
    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        if (!contains(key)) return;
        root = delete(root, key);
        assert check();
    }

    /**
     * delete a key from the subtree
     * has done contains() before, so key must be present in the subtree
     * x will not be null
     * @param x the subtree
     * @param key the key
     * @return the subtree root
     */
    private Node delete(Node x, Key key) {
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = delete(x.left, key);
        } else if (cmp > 0) {
            x.right = delete(x.right, key);
        } else {
            //key == x.key
            if (x.left == null) {
                return x.right;
            } else if (x.right == null) {
                return x.left;
            } else {
                //exchange x with its successor in in-order traversal
                Node y = x;
                x = min(y.right); //find its successor
                x.right = deleteMin(y.right);
                x.left = y.left;
            }
        }
        x.size = 1 + size(x.left) + size(x.right);
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return balance(x);
    }

    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("call min() with empty tree");
        return min(root).key;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        return min(x.left);
    }

    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("call max() with empty tree");
        return max(root).key;
    }

    private Node max(Node x) {
        if (x.right == null) return x;
        return max(x.right);
    }

    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("call deleteMin() with empty tree");
        root = deleteMin(root);
        assert check();
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.size = 1 + size(x.left) + size(x.right);
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return balance(x);
    }

    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("call deleteMax() with empty tree");
        root = deleteMax(root);
        assert check();
    }

    private Node deleteMax(Node x) {
        if (x.right == null) return x;
        x.right = deleteMax(x.right);
        x.size = 1 + size(x.left) + size(x.right);
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return balance(x);
    }
}
