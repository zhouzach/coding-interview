package init;

public class InheritInitOrder {
    public static void main(String[] args) {
        Son son = new Son();
        System.out.println(son.getValue());
    }
}

class Parent {
    int i = 1;

    Parent() {
        System.out.println(i);
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