package patterns;

/**
 * https://www.runoob.com/design-pattern/decorator-pattern.html
 *
 * 装饰器模式（Decorator Pattern）允许向一个现有的对象添加新的功能，同时又不改变其结构。这种类型的设计模式属于结构型模式，
 * 它是作为现有的类的一个包装。
 * 这种模式创建了一个装饰类，用来包装原有的类，并在保持类方法签名完整性的前提下，提供了额外的功能。
 */


public class Decorator {

    public static void main(String[] agrs) {
        Tea tea = new GreenTea();
        tea = new SugurTea(tea);
        tea.showMaterial();
    }
}


class SugurTea implements Tea {

    //持有接口对象
    private final Tea tea;

    //有参构造器动态为接口对象赋值
    public SugurTea(Tea tea) {
        this.tea = tea;
    }


    @Override
    public void showMaterial() {
        //添加装饰行为
        System.out.println("甜的");

        this.tea.showMaterial();
    }
}
