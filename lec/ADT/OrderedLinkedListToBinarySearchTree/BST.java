package ADT.OrderedLinkedListMap.BinarySearchTree;

import jdk.internal.dynalink.beans.StaticClass;

public class BST<Key>{
    private Key key;
    private BST left;
    private BST right;

    public BST(Key key, BST left, BST right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }

    public BST(Key key) {
        this.key = key;
    }

    /** Search */
    public static <Key extends Comparable<Key>> BST find(BST<Key> T, Key sk) {
        if (T == null) {
            return null;
        }
        if (sk.compareTo(T.key) < 0) {
            return find(T.left, sk);
        }
        if (sk.compareTo(T.key) > 0) {
            return find(T.right, sk);
        }
        return T;
    }

    /** Insert */
    public static <Key extends Comparable<Key>> BST insert(BST<Key> T, Key ik) {
        if (T == null) {
            return new BST(ik);
        }
        if (ik.compareTo(T.key) > 0) {
            T.right = insert(T.right, ik);
        } else if (ik.compareTo(T.key) < 0){
            T.left = insert(T.left, ik);
        }
        return T;
    }

    /** Delete */
    public static <Key extends Comparable<Key>> BST<Key> delete(BST<Key> T, Key dk) {
        if (T == null) {
            return T;
        }
        if (dk.equals(T.key)) {
            // case1: node has zero or one child
            if (T.left == null) {
                T = T.right;
            } else if (T.right == null) {
                T = T.left;
            }
            // case2: node has two children , replace it with its predecessor(right-most node in the left subtree)
            else {
                Key newKey = predecessor(T);
                T = delete(T, newKey);
                T.key = newKey;
            }
        } else if (dk.compareTo(T.key) < 0) {
            T.left = delete(T.left, dk);
        } else {
            T.right = delete(T, dk);
        }
        return T;
    }

    /** find the right-most node in the left subtree*/
    private static <Key> Key predecessor(BST<Key> T) {
        BST t = T;
        t = t.left;
        while (t.right != null) {
            t = t.right;
        }
        return (Key) t.key;
    }

}
