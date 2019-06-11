package tree;

public class Tree {
    private Node root;

    public Tree(Node root) {
        this.root = root;
    }

    public void travel(INodeHandler nh) {
        this.travel(nh, root);
    }

    private void travel(INodeHandler nh, Node node) {
        while (node != null) {
            nh.handle(node);

            while (node.left != null) { //迭代到左端最深处
                nh.handle(node);
                node = node.left;
            }

            if(node.right != null){ //右子树不为空时，迭代右子树
                node = node.right;
            } else {

                while (node.parent != null){ //右子树为空时，迭代其双亲结点

                    if(node.parent.right == node){ //当前节点为双亲结点的右孩子
                        nh.handle(node.parent.right);

                        if(node.parent.left != null){ // 双亲节点左孩子不为空
                            node = node.parent.left;  //到外层迭代
                            break;
                        } else {
                            node = node.parent;
                        }
                    } else {
                        node = node.parent; //向上层迭代
                    }
                }
            }




        }

    }
}
