package window

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, dense_rank, max}

object Dense_rankAndMax {

  val sparkSession: SparkSession = SparkSession.builder.appName("Simple Application")
    .master("local")
    .getOrCreate()

  def main(args: Array[String]): Unit = {

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

    /**
      *
      * 1.What are the best-selling and the second best-selling products in every category?
      */

    val windowSpec = Window.partitionBy("category").orderBy(col("revenue").desc)
      .rowsBetween(Window.unboundedPreceding, Window.currentRow)

    dataFrame.withColumn("rank", dense_rank().over(windowSpec))
      .filter("rank <= 2")
      .select("product", "category", "revenue")
      .show()

    /**
      *
      * +----------+----------+-------+
      * |   product|  category|revenue|
      * +----------+----------+-------+
      * |      Thin|Cell phone|   6000|
      * | Very thin|Cell phone|   6000|
      * |Ultra thin|Cell phone|   5000|
      * |      Pro2|    Tablet|   6500|
      * |      Mini|    Tablet|   5500|
      * +----------+----------+-------+
      */


    val sql =
      s"""
         |
         |SELECT
         |  product,
         |  category,
         |  revenue
         |FROM (
         |  SELECT
         |    product,
         |    category,
         |    revenue,
         |    dense_rank() OVER (PARTITION BY category ORDER BY revenue DESC) as rank
         |  FROM productRevenue) tmp
         |WHERE
         |  rank <= 2
         |
         |
       """.stripMargin

    dataFrame.createOrReplaceTempView("productRevenue")
    sparkSession.sql(sql).show()

    /**
      *
      * +----------+----------+-------+
      * |   product|  category|revenue|
      * +----------+----------+-------+
      * |      Thin|Cell phone|   6000|
      * | Very thin|Cell phone|   6000|
      * |Ultra thin|Cell phone|   5000|
      * |      Pro2|    Tablet|   6500|
      * |      Mini|    Tablet|   5500|
      * +----------+----------+-------+
      *
      */


    /**
      *
      * 2.What is the difference between the revenue of each product and the revenue of the best-selling product
      * in the same category of that product?
      */


    val windowSpec2 = Window.partitionBy("category").orderBy(col("revenue").desc)
      .rowsBetween(Window.unboundedPreceding, Window.unboundedFollowing)
    val revenueDifference = max("revenue").over(windowSpec2) - col("revenue")
    dataFrame.withColumn("revenue_difference", revenueDifference).show()


    /**
      *
      * +----------+----------+-------+------------------+
      * |   product|  category|revenue|revenue_difference|
      * +----------+----------+-------+------------------+
      * |      Thin|Cell phone|   6000|                 0|
      * | Very thin|Cell phone|   6000|                 0|
      * |Ultra thin|Cell phone|   5000|              1000|
      * |  Bendable|Cell phone|   3000|              3000|
      * |  Foldable|Cell phone|   3000|              3000|
      * |      Pro2|    Tablet|   6500|                 0|
      * |      Mini|    Tablet|   5500|              1000|
      * |       Pro|    Tablet|   4500|              2000|
      * |       Big|    Tablet|   2500|              4000|
      * |    Normal|    Tablet|   1500|              5000|
      * +----------+----------+-------+------------------+
      *
      */

    val sql2 =
      s"""
         |
         |  SELECT
         |    product,
         |    category,
         |    revenue,
         |    max(revenue) OVER (PARTITION BY category ORDER BY revenue DESC ) - revenue
         |      as revenue_difference_num
         |  FROM productRevenue
         |
       """.stripMargin
    sparkSession.sql(sql2).show()

    /**
      *
      * +----------+----------+-------+----------------------+
      * |   product|  category|revenue|revenue_difference_num|
      * +----------+----------+-------+----------------------+
      * |      Thin|Cell phone|   6000|                     0|
      * | Very thin|Cell phone|   6000|                     0|
      * |Ultra thin|Cell phone|   5000|                  1000|
      * |  Bendable|Cell phone|   3000|                  3000|
      * |  Foldable|Cell phone|   3000|                  3000|
      * |      Pro2|    Tablet|   6500|                     0|
      * |      Mini|    Tablet|   5500|                  1000|
      * |       Pro|    Tablet|   4500|                  2000|
      * |       Big|    Tablet|   2500|                  4000|
      * |    Normal|    Tablet|   1500|                  5000|
      * +----------+----------+-------+----------------------+
      *
      */

  }

}
