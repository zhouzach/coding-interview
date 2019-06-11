package tree;

import lombok.Data;

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
public class BinarySearchTreeWithParent {

    private Node root;

    /**
     * 内部节点类
     */
    private class Node {
        private Node parent;
        private Node left;
        private Node right;
        private int data;

        public Node(int data) {
            this.parent = null;
            this.left = null;
            this.right = null;
            this.data = data;
        }
    }

    public BinarySearchTreeWithParent() {
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
                    Node newNode = new Node(data);
                    node.left = newNode;
                    newNode.parent = node;
                } else {
                    //如果待插入的值，比当前结点的值小，并且当前结点的左孩子不为空，开始递归左子树
                    buildBinarySearchTree(node.left, data);
                }
            } else {
                if (node.right == null) {
                    Node newNode = new Node(data);
                    node.right = newNode;
                    newNode.parent = node;
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

    // 根，左，右
    public void preorderTravel(Node node) {
        while (node != null) {
            handle(node);

            if (node.left != null) {
                node = node.left;
            } else if (node.right != null) {
                node = node.right;
            } else {

                while (node.parent != null) { //迭代到叶子结点

                    if (node.parent.left == node) { //--------->当前节点为双亲结点的左孩子

                        if (node.parent.right != null) { // 双亲节点右孩子不为空
                            node = node.parent.right;    //到外层迭代
                            break;
                        } else {
                            node = node.parent;          // 双亲节点右孩子为空，继续向上层迭代
                        }
                    } else {                        //--------->当前节点为双亲结点的右孩子
                        node = node.parent; //由于是用前序遍历，右孩子遍历过，根结点与左孩子都已经遍历过，因此继续向上层迭代即可
                        //当中序遍历到最后一个结点时，本句会一直向上，直到到达根结点，即node.parent == null，退出循环
                    }
                }
            }

            if (node.parent == null) { // 当迭代到根结点时，才满足此条件
                node = null;           //此时，二叉树已经全部遍历完，退出外层循环即可
            }


        }

    }


    // 左，根，右
    public void inorderTravel(Node node) {
        while (node != null) {

            while (node.left != null) {
                node = node.left;
            }
            handle(node);


            if (node.right != null) { //右子树不为空时，迭代右子树
                node = node.right;
            } else {                //右子树为空

                while (node.parent != null) { //迭代其双亲结点（本while会一直向上迭代结点，循环体内的if语句会判断双亲结点为左孩子的双亲时，才会打印结点）

                    if (node.parent.left == node) { //当前节点为双亲结点的左孩子
                        handle(node.parent);

                        if (node.parent.right != null) { // 双亲节点右孩子不为空
                            node = node.parent.right;  //到外层迭代
                            break;
                        } else {
                            node = node.parent;       // 双亲节点右孩子为空，继续向上层迭代
                        }


                    } else {                      //当前节点为双亲结点的右孩子
                        node = node.parent;       //由于是用中序遍历，右孩子遍历过，双亲结点一定遍历过，因此继续向上层迭代即可
                        //当中序遍历到最后一个结点时，本句会一直向上，直到到达根结点，即node.parent == null，退出循环
                    }
                }
            }


            if (node.parent == null) { // 当迭代到根结点时，才满足此条件
                node = null;           //此时，二叉树已经全部遍历完，退出外层循环即可
            }


        }

    }

    // 左，右，根
    public void postorderTravel(Node node) {
        while (node != null) {

            if (node.left != null) {
                node = node.left;
            } else if (node.right != null) {
                node = node.right;
            } else {
                handle(node);

                while (node.parent != null) { //迭代其双亲结点（本while会一直向上迭代结点，循环体内的if语句会判断双亲结点为左孩子的双亲时，才会打印结点）

                    if (node.parent.left == node) { //当前节点为双亲结点的左孩子

                        if (node.parent.right != null) { // 双亲节点右孩子不为空
                            node = node.parent.right;  //到外层迭代
                            break;
                        } else {
                            handle(node.parent);
                            node = node.parent;       // 双亲节点右孩子为空，继续向上层迭代
                        }

                    } else {                      //当前节点为双亲结点的右孩子
                        handle(node.parent);
                        node = node.parent;       //由于是用中序遍历，右孩子遍历过，双亲结点一定遍历过，因此继续向上层迭代即可
                        //当中序遍历到最后一个结点时，本句会一直向上，直到到达根结点，即node.parent == null，退出循环
                    }
                }
            }


            if (node.parent == null) { // 当迭代到根结点时，才满足此条件
                node = null;           //此时，二叉树已经全部遍历完，退出外层循环即可
            }


        }

    }


    public void handle(Node n) {
        System.out.print(n.data + " ");
    }


}


class BinarySearchTreeWithParentDemo {
    public static void main(String[] args) {
        int[] a = {2, 4, 12, 45, 21, 6, 5, 111};
        BinarySearchTreeWithParent bTree = new BinarySearchTreeWithParent();
        for (int i = 0; i < a.length; i++) {
            bTree.buildBinarySearchTree(bTree.getRoot(), a[i]);
        }
        bTree.preOrder(bTree.getRoot()); // 2,4,12,6,5,45,21,111
        System.out.println();
        bTree.preorderTravel(bTree.getRoot());


//        bTree.inOrder(bTree.getRoot());//2,4,5,6,12,21,45,111
//        System.out.println();
//        bTree.inorderTravel(bTree.getRoot());


//        bTree.postOrder(bTree.getRoot());// 5,6,21,111,45,12,4,2
//        System.out.println();
//        bTree.postorderTravel(bTree.getRoot());

    }
}
