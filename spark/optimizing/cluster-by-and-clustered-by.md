
https://medium.com/@ntnmathur/cluster-by-and-clustered-by-in-spark-sql-9af7f8b80978


https://dzone.com/articles/optimize-spark-with-distribute-by-cluster-by
In SQL:
SET spark.sql.shuffle.partitions=2; SELECT * FROM df CLUSTER BY key

Equivalent in DataFrame API:
df.repartition($"key",2).sortWithinPartitions()

if it is a result of grouping by the expressions that will be used in join? 
Well, in that case you don’t have to repartition it once again – a mere sort by will suffice.
sqlContext.sql("SELECT a, count(*) FROM some_other_df GROUP BY a SORT BY a")