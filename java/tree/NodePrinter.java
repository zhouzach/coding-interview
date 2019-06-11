package tree;

public class NodePrinter implements INodeHandler{
    public void handle(Node n){
        System.out.print(n.data + " ");  // 在控制台上打印出节点，无回车换行。
    }
}

