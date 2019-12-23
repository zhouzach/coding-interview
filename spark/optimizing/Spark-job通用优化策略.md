
https://dzone.com/articles/tips-and-best-practices-to-take-advantage-of-spark

一、SQL Tab
You can see details about the query plan produced by Catalyst on the web UI SQL tab. In the query plan details, 
you can check and see:

1.The amount of time for each stage.
2.If partition filters, projection, and filter pushdown are occurring.
3.Shuffles between stages (Exchange) and the amount of data shuffled. If joins or aggregations are shuffling a lot of data, 
consider bucketing. You can set the number of partitions to use when shuffling with the spark.sql.shuffle.partitions option.
4.The join algorithm being used. Broadcast join should be used when one table is small; sort-merge join should be used 
for large tables. You can use the broadcast hint to guide Spark to broadcast a table in a join. For faster joins with 
large tables using the sort-merge join algorithm, you can use bucketing to pre-sort and group tables; this will avoid 
shuffling in the sort merge.
5.Use the Spark SQL "ANALYZE TABLE tablename COMPUTE STATISTICS" to take advantage of cost-based optimization in the Catalyst Planner.

二、Stages Tab
You can use the stage detail metrics to identify problems with an executor or task distribution. Things to look for:

1.Tasks that are taking longer and/or killed tasks. If your task process time is not balanced, resources could be wasted.
2.Shuffle read size that is not balanced.
3.If your partitions/tasks are not balanced, then consider repartition as described under partitioning.

三、Storage Tab
  Caching Datasets can make execution faster if the data will be reused. You can use the storage tab to see 
  if important Datasets are fitting into memory.
  
四、Executors Tab
  You can use the executors tab to confirm that your application has the amount of resources needed.
  
  1.Shuffle Read Write Columns: shows size of data transferred between stages
  2.Storage Memory Column: shows the current used/available memory
  3.Task Time Column: shows task time/garbage collection time