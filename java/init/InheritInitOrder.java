package init;

public class InheritInitOrder {
    public static void main(String[] args) {
        Son son = new Son();
        System.out.println(son.getValue());
        /**
         * 2
         * 0
         * 2
         */
    }
}

class Parent {
    int i = 1;

    Parent() {
        System.out.println(i);

        //对象没有初始化完，通过成员方法获取的成员变量还是类加载过程中准备阶段赋值的元素初始值
        int x = getValue();
        System.out.println(x);
    }

    {
        i = 2;
    }

    protected int getValue() {
        return i;
    }
}

class Son extends Parent {
    int j = 1;

    Son() {
        j = 2;
    }


    {
        j = 3;
    }

    @Override
    protected int getValue() {
        return j;
    }
}