
https://blog.codecentric.de/en/2016/02/lazy-vals-scala-look-hood/

final class LazyCell {
  lazy val value: Int = 42
}
A handwritten snippet equivalent to the code the compiler generates for our LazyCell looks like this:

final class LazyCell {
  @volatile var bitmap_0: Boolean = false                   // (1)
  var value_0: Int = _                                      // (2)
  private def value_lzycompute(): Int = {
    this.synchronized {                                     // (3)
      if (!bitmap_0) {                                      // (4)
        value_0 = 42                                        // (5)
        bitmap_0 = true
      }
    }
    value_0
  }
  def value = if (bitmap_0) value_0 else value_lzycompute() // (6)
}