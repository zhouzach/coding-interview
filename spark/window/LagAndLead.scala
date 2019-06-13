package window

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.lag
import org.apache.spark.sql.functions.lead

/**
  * oracle lag: https://www.cnblogs.com/dqz19892013/archive/2013/04/11/3014239.html
  */
object LagAndLead {

  val sparkSession: SparkSession = SparkSession.builder.appName("Simple Application")
    .master("local")
    .getOrCreate()

  def main(args: Array[String]): Unit = {

  val df = sparkSession.createDataFrame(Seq(
    (201601, 100.5),
    (201602, 120.6),
    (201603, 450.2),
    (201604, 200.7),
    (201605, 121.4)))
    .toDF("date", "volume")

  val orderByDate = Window.orderBy("date")

  val lagDf = df.withColumn("pre1day_volume", lag("volume", 1, 0).over(orderByDate))

    lagDf.show()

  /**
    *
    *
    * +------+------+--------------+
    * |  date|volume|pre1day_volume|
    * +------+------+--------------+
    * |201601| 100.5|           0.0|
    * |201602| 120.6|         100.5|
    * |201603| 450.2|         120.6|
    * |201604| 200.7|         450.2|
    * |201605| 121.4|         200.7|
    * +------+------+--------------+
    *
    *
    */


    val leadDf = df.withColumn("post1day_volume", lead("volume", 1).over(orderByDate))
    leadDf.show()

    /**
      *
      * +------+------+---------------+
      * |  date|volume|post1day_volume|
      * +------+------+---------------+
      * |201601| 100.5|          120.6|
      * |201602| 120.6|          450.2|
      * |201603| 450.2|          200.7|
      * |201604| 200.7|          121.4|
      * |201605| 121.4|           null|
      * +------+------+---------------+
      *
      *
      */

  }

}
