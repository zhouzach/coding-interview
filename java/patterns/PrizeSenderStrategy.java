
/**
 * 策略模式本质上是一个接口下有多个实现类，而每种实现类会处理某一种情况
 */

public interface PrizeSenderStrategy {

    /**
     *
     * 无论是support()还是sendPrize()方法，都需要传一个对象作为参数，而不是简单的基本类型变量，这样做的好处是后续如果
     * 要在Request中新增字段，那么就不需要修改接口的定义和已经实现的各个子类的逻辑
     *
     */

    boolean support(Request request);

    void sendPrize(Request request);
}

class PointSender implements PrizeSenderStrategy {
    @Override
    public boolean support(Request request){
        return "POINT".equals(request.getPrizeType());
    }

    @Override
    public void sendPrize(Request request){
        System.out.println("发放积分；");
    }
}


class VirtualCurrencySender implements PrizeSenderStrategy {
    @Override
    public boolean support(Request request){
        return "VirtualCurrency".equals(request.getPrizeType());
    }

    @Override
    public void sendPrize(Request request){
        System.out.println("发放虚拟币；");
    }
}


class Request{

    public String getPrizeType(){
        return "";
    }

}
