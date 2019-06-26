package init;

/**
 *
 * 1.父类【静态成员】和【静态代码块】，按在代码中出现的顺序依次执行。
 * 2.子类【静态成员】和【静态代码块】，按在代码中出现的顺序依次执行。
 * 3.父类的【普通成员变量被普通成员方法赋值】和【普通代码块】，按在代码中出现的顺序依次执行。
 * 4.执行父类的构造方法。
 * 5.子类的【普通成员变量被普通成员方法赋值】和【普通代码块】，按在代码中出现的顺序依次执行。
 * 6.执行子类的构造方法。
 *
 */
public class InitOrderTest {

    public static void main(String[] args){
        print();

        /**
         *
         * 2
         * 3
         * a=110,b=0
         * 1
         * 4
         */
    }

    static InitOrderTest st = new InitOrderTest();


    static int b = 112;


    InitOrderTest(){

        System.out.println("3");
        System.out.println("a=" + a + ",b=" +b);
    }

    {
        System.out.println("2");
    }

    public static void print(){
        System.out.println("4");

    }

    static {
        System.out.println("1");
    }

    int a = 110;
}
