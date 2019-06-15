package window

import org.apache.spark.sql.SparkSession

/**
  * https://blog.jooq.org/2014/08/12/the-difference-between-row_number-rank-and-dense_rank/
  *
  * https://docs.aws.amazon.com/zh_cn/redshift/latest/dg/r_WF_PERCENT_RANK.html
  *
  * PERCENT_RANK 窗口函数
  * 计算给定行的百分比排名。使用以下公式确定百分比排名：
  *
  * (x - 1) / (the number of rows in the window or partition - 1)
  *
  * 其中，x 为当前行的排名。以下数据集说明了此公式的使用：
  *
  * Row#	Value	Rank	Calculation	PERCENT_RANK
  * 1	15	1	(1-1)/(7-1)	0.0000
  * 2	20	2	(2-1)/(7-1)	0.1666
  * 3	20	2	(2-1)/(7-1)	0.1666
  * 4	20	2	(2-1)/(7-1)	0.1666
  * 5	30	5	(5-1)/(7-1)	0.6666
  * 6	30	5	(5-1)/(7-1)	0.6666
  * 7	40	7	(7-1)/(7-1)	1.0000
  * 返回值范围介于 0 和 1（含 1）之间。任何集合中的第一行的 PERCENT_RANK 均为 0。
  *
  *
  *
  * http://www.sqltutorial.org/sql-window-functions/sql-ntile/
  *
  * The syntax of the NTILE() function is as follows:
  *
  * NTILE(buckets) OVER (
  * PARTITION BY expr1, expr2,...
  * ORDER BY expr1 [ASC|DESC], expr2 ...
  * )
  */
object RankingFunctions {
  val sparkSession: SparkSession = SparkSession.builder.appName("Simple Application")
    .master("local")
    .enableHiveSupport()
    .getOrCreate()

  def main(args: Array[String]): Unit = {

    sparkSession.sql("create table  IF NOT EXISTS t (v string)")
    sparkSession.sql("truncate table  t")
    sparkSession.sql(
      s"""
         |  insert into t
         |  VALUES('a'),('a'),('a'),('b'),
         |        ('c'),('c'),('d'),('e')
       """.stripMargin)

    sparkSession.sql(
      s"""
         |select * from t
       """.stripMargin).show()

    sparkSession.sql(
      s"""
         |
         |SELECT
         |  v,
         |  ROW_NUMBER() OVER(ORDER BY v) row_number,
         |  RANK()       OVER(ORDER BY v) rank,
         |  percent_rank()       OVER(ORDER BY v) percent_rank,
         |  DENSE_RANK() OVER(ORDER BY v) dense_rank,
         |  NTILE(3)     OVER(ORDER BY v) buckets
         |FROM t
         |
       """.stripMargin).show()
  }



}
