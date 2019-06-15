package window

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.first
import org.apache.spark.sql.functions.max
import org.apache.spark.sql.functions.last
import org.apache.spark.sql.functions.min

/**
  * http://www.mysqltutorial.org/mysql-window-functions/mysql-cume_dist-function/
  */
object AnalyticFunctions {

  val sparkSession: SparkSession = SparkSession.builder.appName("Simple Application")
    .master("local")
    .enableHiveSupport()
    .getOrCreate()

  def main(args: Array[String]): Unit = {
//    cume_dist
    first_value()
  }


  // 累计分布
  def cume_dist(): Unit = {
    sparkSession.sql("create table  IF NOT EXISTS scores (name VARCHAR(20),    score INT)")
    sparkSession.sql("truncate table  scores")
    sparkSession.sql(
      s"""
         |INSERT INTO
         |scores
         |VALUES
         |('Smith',81),
         |('Jones',55),
         |('Williams',55),
         |('Taylor',62),
         |('Brown',62),
         |('Davies',84),
         |('Evans',87),
         |('Wilson',72),
         |('Thomas',72),
         |('Johnson',100)
       """.stripMargin)

    sparkSession.sql(
      s"""
         |SELECT
         |    name,
         |    score,
         |    ROW_NUMBER() OVER (ORDER BY score) row_num,
         |    CUME_DIST() OVER (ORDER BY score) cume_dist_score
         |FROM
         |scores
       """.stripMargin).show()
  }

  def first_value()={
    val dataFrame = sparkSession.createDataFrame(Seq(
      ("Thin", "Cell phone", 6000),
      ("Normal", "Tablet", 1500),
      ("Mini", "Tablet", 5500),
      ("Ultra thin", "Cell phone", 5000),
      ("Very thin", "Cell phone", 6000),
      ("Big", "Tablet", 2500),
      ("Bendable", "Cell phone", 3000),
      ("Foldable", "Cell phone", 3000),
      ("Pro", "Tablet", 4500),
      ("Pro2", "Tablet", 6500)
    ))
      .toDF("product", "category", "revenue")


    val windowSpec = Window.partitionBy("category").orderBy(col("revenue").desc)
      .rowsBetween(Window.unboundedPreceding, Window.unboundedFollowing)

    dataFrame.withColumn("first_revenue", first("revenue").over(windowSpec))
      .show()

    /**
      * +----------+----------+-------+-------------+
      * |   product|  category|revenue|first_revenue|
      * +----------+----------+-------+-------------+
      * |      Thin|Cell phone|   6000|         6000|
      * | Very thin|Cell phone|   6000|         6000|
      * |Ultra thin|Cell phone|   5000|         6000|
      * |  Bendable|Cell phone|   3000|         6000|
      * |  Foldable|Cell phone|   3000|         6000|
      * |      Pro2|    Tablet|   6500|         6500|
      * |      Mini|    Tablet|   5500|         6500|
      * |       Pro|    Tablet|   4500|         6500|
      * |       Big|    Tablet|   2500|         6500|
      * |    Normal|    Tablet|   1500|         6500|
      * +----------+----------+-------+-------------+
      */

    dataFrame.withColumn("max_revenue", max("revenue").over(windowSpec))
      .show()

    /**
      * +----------+----------+-------+-----------+
      * |   product|  category|revenue|max_revenue|
      * +----------+----------+-------+-----------+
      * |      Thin|Cell phone|   6000|       6000|
      * | Very thin|Cell phone|   6000|       6000|
      * |Ultra thin|Cell phone|   5000|       6000|
      * |  Bendable|Cell phone|   3000|       6000|
      * |  Foldable|Cell phone|   3000|       6000|
      * |      Pro2|    Tablet|   6500|       6500|
      * |      Mini|    Tablet|   5500|       6500|
      * |       Pro|    Tablet|   4500|       6500|
      * |       Big|    Tablet|   2500|       6500|
      * |    Normal|    Tablet|   1500|       6500|
      * +----------+----------+-------+-----------+
      */

    dataFrame.withColumn("last_revenue", last("revenue").over(windowSpec))
      .show()

    /**
      * +----------+----------+-------+------------+
      * |   product|  category|revenue|last_revenue|
      * +----------+----------+-------+------------+
      * |      Thin|Cell phone|   6000|        3000|
      * | Very thin|Cell phone|   6000|        3000|
      * |Ultra thin|Cell phone|   5000|        3000|
      * |  Bendable|Cell phone|   3000|        3000|
      * |  Foldable|Cell phone|   3000|        3000|
      * |      Pro2|    Tablet|   6500|        1500|
      * |      Mini|    Tablet|   5500|        1500|
      * |       Pro|    Tablet|   4500|        1500|
      * |       Big|    Tablet|   2500|        1500|
      * |    Normal|    Tablet|   1500|        1500|
      * +----------+----------+-------+------------+
      */



    dataFrame.withColumn("min_revenue", min("revenue").over(windowSpec))
      .show()

    /**
      * +----------+----------+-------+-----------+
      * |   product|  category|revenue|min_revenue|
      * +----------+----------+-------+-----------+
      * |      Thin|Cell phone|   6000|       3000|
      * | Very thin|Cell phone|   6000|       3000|
      * |Ultra thin|Cell phone|   5000|       3000|
      * |  Bendable|Cell phone|   3000|       3000|
      * |  Foldable|Cell phone|   3000|       3000|
      * |      Pro2|    Tablet|   6500|       1500|
      * |      Mini|    Tablet|   5500|       1500|
      * |       Pro|    Tablet|   4500|       1500|
      * |       Big|    Tablet|   2500|       1500|
      * |    Normal|    Tablet|   1500|       1500|
      * +----------+----------+-------+-----------+
      */
  }
}
