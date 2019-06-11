package tree;

public class Tree {
    public Node root;

    public Tree(Node root) {
        this.root = root;
    }

    /**
     * 递归创建二叉搜索树,左子结点总是小于或等于根结点，而右子结点总是大于或等于根结点。
     *
     * @param node 当前结点
     * @param data 待插入的值
     */
    public void buildBinarySearchTree(Node node, String data) {
        if (root == null) {
            Node newNode = new Node();
            newNode.parent = null;
            newNode.left = null;
            newNode.right = null;
            newNode.data = data;
            root = newNode;
        } else {
            if (data.hashCode() < node.data.hashCode()) {
                if (node.left == null) {
                    Node newNode = new Node();
                    newNode.parent = null;
                    newNode.left = null;
                    newNode.right = null;
                    newNode.data = data;

                    node.left = newNode;
                    newNode.parent = node;
                } else {
                    //如果待插入的值，比当前结点的值小，并且当前结点的左孩子不为空，开始递归左子树
                    buildBinarySearchTree(node.left, data);
                }
            } else {
                if (node.right == null) {
                    Node newNode = new Node();
                    newNode.parent = null;
                    newNode.left = null;
                    newNode.right = null;
                    newNode.data = data;

                    node.right = newNode;
                    newNode.parent = node;
                } else {
                    //如果待插入的值，比当前结点的值大，并且当前结点的右孩子不为空，开始递归右子树
                    buildBinarySearchTree(node.right, data);
                }
            }
        }
    }

    public void preorderTravel(INodeHandler nh) {
        this.preorderTravel(nh, root);
    }

    private void preorderTravel(INodeHandler nh, Node node) {
        while (node != null) {
            nh.handle(node);

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
}

class TreeDemo{
    public static void main(String[] args) {
        int[] a = {2, 4, 12, 45, 21, 6, 5, 111};
        Node newNode = new Node();
        newNode.parent = null;
        newNode.left = null;
        newNode.right = null;
        newNode.data = "2";
        Tree bTree = new Tree(newNode);
        for (int i = 1; i < a.length; i++) {
            bTree.buildBinarySearchTree(bTree.root, a[i] +"");
        }

        INodeHandler iNodeHandler = new NodePrinter();
        bTree.preorderTravel(iNodeHandler); // 2,4,12,6,5,45,21,111


//        bTree.inOrder(bTree.getRoot());//2,4,5,6,12,21,45,111
//        System.out.println();
//        bTree.inorderTravel(bTree.getRoot());


//        bTree.postOrder(bTree.getRoot());// 5,6,21,111,45,12,4,2
//        System.out.println();
//        bTree.postorderTravel(bTree.getRoot());

    }
}
