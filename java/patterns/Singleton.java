package patterns;

/**
 * https://www.runoob.com/design-pattern/singleton-pattern.html
 *
 *
 * 1.私有化构造器
 * 2.创建类的对象，同时设置为private的，通过公共方法来获取，体现封装性；
 *   由于构造器被私有化，提供此对象的公共方法应该是static的，因此此对象应该是static的
 * 3.提供公共的方法
 *
 *
 * 为什么选择使用单例模式而不是全局变量：
 * (1)全局变量生命周期很长，有可能很耗资源；
 * (2)滥用全局变量可能导致命名空间污染
 * (3)全局变量并不能保证只有一个实例
 *
 *
 * 1.工厂一般都是单例的
 * 2.JavaWEB中的Filter实例是单例的
 * 3.线程池
 * 4.数据库连接池
 * 5.缓存
 * 6.对话框
 * 7.处理偏好设置和注册表的对象
 * 8.日志对象
 * 9.充当打印机、显卡等设备的驱动程序的对象
 * 10.任务管理器
 *
 * 对于以上类来说，如果制造出多个实例，就会导致许多问题产生，例如：程序行为异常、资源使用过量，或者是不一致的结果
 *
 */


// hungry singleton, 首选
public class Singleton {
    private static Singleton instance = new Singleton();

    private Singleton() {
    }

    public static Singleton getInstance() {
        return instance;
    }
}


//只有在要明确实现 lazy loading 效果时，才会使用这种登记方式
class LazySingleton {
    private static class SingletonHolder {
        private static final LazySingleton INSTANCE = new LazySingleton();
    }
    private LazySingleton (){}
    public static final LazySingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

//如果涉及到反序列化创建对象时，可以尝试使用这种枚举方式
enum SingletonEnum {
    INSTANCE;
    public void whateverMethod() {
    }
}

// 双检锁方式,可能有性能问题 lazy singleton，DCL，即 double-checked locking
class LazySingletonDCL{
    /**
     * volatile 保证从主内存加载到线程工作内存的值是最新的，单并不能保证是线程安全的
     */
    private volatile static LazySingletonDCL instance;

    private LazySingletonDCL(){}

    /**
     * 如果我们不需要这个实例，它就永远不会产生，这就是"延迟实例化(Lazy instance)".
     * 延迟实例化对资源敏感的对象特别重要.
     * @return
     */
    public static LazySingletonDCL getInstance(){
        if(instance == null){

            synchronized(LazySingletonDCL.class){
                if(instance == null){
                    instance = new LazySingletonDCL();
                }
            }
        }

        return instance;
    }
}
