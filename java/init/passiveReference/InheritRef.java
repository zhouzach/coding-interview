package init.passiveReference;

public class InheritRef{

    public static void main(String[] args) {
        // because initialize SuperClass static field, SubClass can not be initialized
        System.out.println(SubClass.value);
        /**
         *
         * SuperClass init!
         * 123
         *
         */
    }
}

class SuperClass {
    static {
        System.out.println("SuperClass init!");
    }

    public static int value = 123;
}

class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init!");
    }


}

