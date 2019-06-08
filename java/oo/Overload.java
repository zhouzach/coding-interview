package oo;

/**
 * Overload 只与方法参数的个数有关，与返回值无关
 * Overload 与访问权限也无关，inherit 与 访问权限有关
 */
public class Overload {

    public void print() {
        System.out.println("print ");
    }

    public String print(String s) {
        System.out.println("print Overload");
        return "";
    }


    public void print(String s1, String s2) {
        System.out.println("print ");
    }

    /**
     * can not be compiled
     */
//    public String print() {
//        System.out.println("print ");
//        return "";
//    }

    /**
     * can not be compiled
     */
//    private void print() {
//        System.out.println("print in super");
//    }



}
