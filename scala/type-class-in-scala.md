1.Type class in scala
https://www.sczyh30.com/posts/Functional-Programming/typeclass-scala-haskell/
https://www.jianshu.com/p/52450e251c84

Scala中并没有直接通过关键字支持type class(毕竟还混合了OOP)。Scala中type class成为了一种pattern，可以通过trait加上implicit来实现。
type class和trait/interface作用类似，都是对某一系列类型抽象出特定的行为。那么type class与trait/interface相比有什么优点呢？
想象一下，如果用interface的话，每个sub-class都要在其body内实现对应的函数。这样如果要给现有的类实现这个interface的话，就必须要修改原类，
在原类中增加对应的实现，这显然不符合Open Closed Principle(对扩展开放，对修改关闭)。所以OOP中提出了诸如 适配器模式 这样的设计模式用于扩展已有的类，
但写各种adapter增加了代码的冗杂程度。

而对于type class pattern来说，实现type class实例的代码并不写在类型定义中，而是在外部实现一个对应type class的实例。这样，
我们要给现有的类型实现一个type class的话就不需要更改原有类型的定义了，只需要实现对应的type class实例就可以了。这其实就是 抽象与实现分离，
即类型定义与约束实现是分离的，某个类型并不清楚自己属于某个type class。与接口的方式相比，type class符合Open Closed Principle。