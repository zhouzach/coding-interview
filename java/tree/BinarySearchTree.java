package tree;

import lombok.Data;

import java.util.*;

/**
 * 在计算机科学中，二叉树是每个节点最多有两个子树的有序树。通常子树被称作“左子树”（left subtree）和“右子树”（right subtree）。
 * 二叉树的每个结点至多只有二棵子树(不存在度大于2的结点)，二叉树的子树有左右之分，次序不能颠倒。二叉树的第i层至多有2i − 1个结点；
 * 深度为k的二叉树至多有2k − 1个结点；对任何一棵二叉树T，如果其终端结点数为n0，度为2的结点数为n2，则n0 = n2 + 1。
 * <p>
 * 二叉树常被用于实现二叉查找树和二叉堆。
 * 在二叉搜索树中，左子结点总是小于或等于根结点，而右子结点总是大于或等于根结点。
 * 二叉堆分为最大堆和最小堆。
 */
@Data
public class BinarySearchTree {

    private Node root;

    /**
     * 内部节点类
     */
    private class Node {
        private Node left;
        private Node right;
        private int data;

        public Node(int data) {
            this.left = null;
            this.right = null;
            this.data = data;
        }
    }

    public BinarySearchTree() {
        root = null;
    }

    /**
     * 递归创建二叉搜索树,左子结点总是小于或等于根结点，而右子结点总是大于或等于根结点。
     *
     * @param node 当前结点
     * @param data 待插入的值
     */
    public void buildBinarySearchTree(Node node, int data) {
        if (root == null) {
            root = new Node(data);
        } else {
            if (data < node.data) {
                if (node.left == null) {
                    node.left = new Node(data);
                } else {
                    //如果待插入的值，比当前结点的值小，并且当前结点的左孩子不为空，开始递归左子树
                    buildBinarySearchTree(node.left, data);
                }
            } else {
                if (node.right == null) {
                    node.right = new Node(data);
                } else {
                    //如果待插入的值，比当前结点的值大，并且当前结点的右孩子不为空，开始递归右子树
                    buildBinarySearchTree(node.right, data);
                }
            }
        }
    }

    /**
     * 前序遍历
     *
     * @param node
     */
    public void preOrder(Node node) {

        if (node != null) {
            System.out.print(node.data + " ");
            preOrder(node.left);
            preOrder(node.right);
        }
    }

    /**
     * 中序遍历
     *
     * @param node
     */
    public void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.print(node.data + " ");
            inOrder(node.right);
        }
    }

    /**
     * 后序遍历
     *
     * @param node
     */
    public void postOrder(Node node) {
        if (node != null) {
            postOrder(node.left);
            postOrder(node.right);
            System.out.print(node.data + " ");
        }
    }


    /**
     * @param root 树根节点
     *             利用栈实现循环先序遍历二叉树
     *             这种实现类似于图的深度优先遍历（DFS）
     *             维护一个栈，将根节点入栈，然后只要栈不为空，出栈并访问，接着依次将访问节点的右节点、左节点入栈。
     *             这种方式应该是对先序遍历的一种特殊实现（看上去简单明了），但是不具备很好的扩展性，在中序和后序方式中不适用
     */
    public static void preOrderStack1(Node root) {
        if (root == null) return;
        Stack<Node> s = new Stack<Node>();
        s.push(root);
        while (!s.isEmpty()) {
            Node temp = s.pop();
            System.out.print(temp.data + " ");
            if (temp.right != null) s.push(temp.right);
            if (temp.left != null) s.push(temp.left);
        }
    }

    /**
     * @param root 树的根节点
     *             利用栈模拟递归过程实现循环先序遍历二叉树
     *             这种方式具备扩展性，它模拟递归的过程，将左子树点不断的压入栈，直到null，然后处理栈顶节点的右子树
     */
    public static void preOrderStack2(Node root) {
        if (root == null) return;
        Stack<Node> s = new Stack<Node>();
        while (root != null || !s.isEmpty()) {
            while (root != null) {
                System.out.print(root.data + " ");
                s.push(root);//先访问再入栈
                root = root.left;
            }
            root = s.pop();
            root = root.right;//如果是null，出栈并处理右子树
        }
    }

    /**
     * @param root 树根节点
     *             利用栈模拟递归过程实现循环中序遍历二叉树
     *             思想和上面的preOrderStack_2相同，只是访问的时间是在左子树都处理完直到null的时候出栈并访问。
     */
    public static void inOrderStack(Node root) {
        if (root == null) return;
        Stack<Node> s = new Stack<Node>();
        while (root != null || !s.isEmpty()) {
            while (root != null) {
                s.push(root);//先访问再入栈
                root = root.left;
            }
            root = s.pop();
            System.out.print(root.data + " ");
            root = root.right;//如果是null，出栈并处理右子树
        }
    }

    /**
     * @param root 树根节点
     *             后序遍历不同于先序和中序，它是要先处理完左右子树，然后再处理根(回溯)，所以需要一个记录哪些节点已经被访问的结构(可以在树结构里面加一个标记)，这里可以用map实现
     */
    public static void postOrderStack(Node root) {
        if (root == null) return;
        Stack<Node> s = new Stack<Node>();
        Map<Node, Boolean> map = new HashMap<Node, Boolean>();
        s.push(root);
        while (!s.isEmpty()) {
            Node temp = s.peek();
            if (temp.left != null && !map.containsKey(temp.left)) {
                temp = temp.left;
                while (temp != null) {
                    if (map.containsKey(temp)) break;
                    else s.push(temp);
                    temp = temp.left;
                }
                continue;
            }
            if (temp.right != null && !map.containsKey(temp.right)) {
                s.push(temp.right);
                continue;
            }
            Node t = s.pop();
            map.put(t, true);
            System.out.print(t.data + " ");
        }
    }

    /**
     * @param root 树根节点
     *             层序遍历二叉树，用队列实现，先将根节点入队列，只要队列不为空，然后出队列，并访问，接着讲访问节点的左右子树依次入队列
     */
    public static void levelTravel(Node root) {
        if (root == null) return;
        Queue<Node> q = new LinkedList<Node>();
        q.add(root);
        while (!q.isEmpty()) {
            Node temp = q.poll();
            System.out.println(temp.data);
            if (temp.left != null) q.add(temp.left);
            if (temp.right != null) q.add(temp.right);
        }
    }


}


class Demo {
    public static void main(String[] args) {
        int[] a = {2, 4, 12, 45, 21, 6, 111};
        BinarySearchTree bTree = new BinarySearchTree();
        for (int i = 0; i < a.length; i++) {
            bTree.buildBinarySearchTree(bTree.getRoot(), a[i]);
        }
        bTree.preOrder(bTree.getRoot()); // 2,4,12,6,45,21,111
        System.out.println();
        bTree.preOrderStack1(bTree.getRoot());
        System.out.println();
        bTree.preOrderStack2(bTree.getRoot());

        System.out.println();
        System.out.println("****************************");
        bTree.inOrder(bTree.getRoot());//2,4,6,12,21,45,111
        System.out.println();
        bTree.inOrderStack(bTree.getRoot());
        System.out.println();
        System.out.println("****************************");
        bTree.postOrder(bTree.getRoot());// 6,21,111,45,12,4,2
        System.out.println();
        bTree.postOrderStack(bTree.getRoot());

    }
}
