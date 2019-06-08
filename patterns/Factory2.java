import java.util.List;

/**
 * 工厂方法模式，就是定义一个工厂方法，通过传入的参数，返回某个实例，然后通过该实例来处理后续的业务逻辑。
 * 一般的，工厂方法的返回值类型是一个接口类型，而选择具体子类实例的逻辑则封装到工厂方法中。通过这种方式，
 * 来将外层调用逻辑与具体的子类的获取逻辑进行分离。
 *
 */
public class Factory2 {
    private List<PrizeSenderStrategy> prizeSenders;

    public PrizeSenderStrategy getPrizeSender(Request request){
        for(PrizeSenderStrategy prizeSender : prizeSenders){
            if(prizeSender.support(request)){
                return prizeSender;
            }
        }

        throw new UnsupportedOperationException("unsupported request: " + request);
    }
}
