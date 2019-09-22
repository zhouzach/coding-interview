
**Analyze Table**
https://docs.databricks.com/spark/latest/spark-sql/language-manual/analyze-table.html
ANALYZE TABLE [db_name.]table_name COMPUTE STATISTICS [analyze_option]
ANALYZE TABLE [db_name.]table_name COMPUTE STATISTICS [NOSCAN]
ANALYZE TABLE [db_name.]table_name COMPUTE STATISTICS FOR COLUMNS col1 [, col2, ...]
spark.sql("ANALYZE TABLE table_name COMPUTE STATISTICS NOSCAN")

val explain = ExplainCommand(df.queryExecution.logical, extended = true, cost = true)
    spark.sessionState.executePlan(explain).executedPlan.executeCollect().foreach {
      r => println(r.getString(0))
    }

    //or
    df.createOrReplaceTempView("tmp")
    spark.sql(
      """
        |EXPLAIN COST select * from tmp
        |""".stripMargin)
      .show(false)

