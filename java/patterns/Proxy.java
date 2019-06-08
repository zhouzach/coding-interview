/**
 * https://www.runoob.com/design-pattern/proxy-pattern.html
 *
 *
 *
 *
 * 和装饰器模式的区别：装饰器模式为了增强功能，而代理模式是为了加以控制。
 */





public class Proxy {

    public static void main(String[] args){
        Tea tea= new SugurGreenTea();
        tea.showMaterial();
    }
}

class SugurGreenTea implements Tea {

    //持有接口对象
    private final Tea tea;

    //为接口对象指定默认对象
    public SugurGreenTea() {
        this.tea = new GreenTea();
    }


    @Override
    public void showMaterial() {
        //添加行为
        System.out.println("甜的");

        this.tea.showMaterial();
    }
}