package oo;

/**
 * Override(inherit) 只与访问权限有关，与返回值无关
 */
public class OverrideDemo {

    public static void main(String[] args) {
        Sub1 sub1 = new Sub1();
        sub1.print();


        Sub2 sub2 = new Sub2();
        sub2.print();


        Sub3 sub3 = new Sub3();
        sub3.print();
    }


}


class Sub7 extends Base {

    /**
     * it is a new method, and did not inherit from super class
     */
    protected void print(String s){
        System.out.println("print in sub5");
    }
}

class Sub6 extends Base {

    /**
     * can not be compiled,because weaker access privilege than super class
     */
//    private void print(){
//        System.out.println("print in sub4");
//    }
}

class Sub5 extends Base {

    /**
     * can not be compiled,because weaker access privilege than super class
     */
//    void print(){
//        System.out.println("print in sub4");
//    }
}

class Sub4 extends Base {

    /**
     * can not be compiled
     */
//    protected String print(){
//        System.out.println("print in sub4");
//    }
}

class Sub3 extends Base {

    @Override
    protected void print(){
        System.out.println("print in sub3");
    }
}


class Sub2 extends Base {

    @Override
    public void print(){
        System.out.println("print in sub2");
    }
}

class Sub1 extends Base {

}

class Base {
    protected void print() {
        System.out.println("print in super");
    }
}

class Base1 {

    // can not be inherit by sub class, so in can not be overridden
    private void print() {
        System.out.println("super");
    }
}
