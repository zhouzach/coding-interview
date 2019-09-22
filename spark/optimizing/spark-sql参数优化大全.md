
http://www.jasongj.com/spark/adaptive_execution/
1.spark.sql.adaptive.enabled=true 启用 Adaptive Execution 从而启用自动设置 Shuffle Reducer 这一特性
2.spark.sql.adaptive.shuffle.targetPostShuffleInputSize 可设置每个 Reducer 读取的目标数据量，其单位是字节，默认值为 64 MB
3.spark.sql.adaptive.join.enabled 开启 Adaptive Execution 的动态调整 Join 功能,SortMergeJoin 变更为 BroadcastJoin


https://stackoverflow.com/questions/30797724/how-to-optimize-shuffle-spill-in-apache-spark-application
spark.shuffle.memoryFraction from the default of 0.2


https://www.jianshu.com/p/10e91ace3378
若应用shuffle阶段 spill严重，则可以通过调整“spark.shuffle.spill.numElementsForceSpillThreshold”的值，来限制spill使用的内存大小 ，
比如设置（2000000），该值太大不足以解决OOM问题，若太小，则spill会太频繁，影响集群性能，因此，要依据负载类型进行合理伸缩（此处，可设法引入动态伸缩机制，待后续处理


https://blog.csdn.net/hjw199089/article/details/54861989
二、在数据量过大时，若在Spark SQL中 使用sort排序等自定义函数会出现内存不足错误
如Total size of serialized results of 20 tasks (1058.2 MB) is bigger than spark.driver.maxResultSize (1024.0 MB)   
解决办法可在hive中单独建立一张表将数据排序，避免该步骤在spark内存中执行


https://spark.apache.org/docs/2.3.0/sql-programming-guide.html
spark.sql.autoBroadcastJoinThreshold

https://medium.com/@an_chee/why-using-mixed-case-field-names-in-hive-spark-sql-is-a-bad-idea-95da8b6ec1e0
spark.sql.hive.caseSensitiveInferenceMode
