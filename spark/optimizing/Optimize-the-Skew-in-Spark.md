

https://blog.clairvoyantsoft.com/optimize-the-skew-in-spark-e523c6ee18ac
http://www.jasongj.com/spark/skew/

1.调整并行度
SQL: spark.default.parallelism	
     SET spark.sql.shuffle.partitions=600; SELECT * FROM df DISTRIBUTEBY key
     select A ,B from table your_table distribute by rand() 
API:
     df.repartition($"key",600)

2.Broadcast机制
SQL: SET spark.sql.autoBroadcastJoinThreshold=104857600;将Broadcast的阈值设置得足够大
API: org.apache.spark.sql.functions.broadcast(dataFrame)

3.将表拆分为两个表

4.设置spark.speculation=true，把预测不乐观的节点去掉来保证程序可稳定运行

5.大表随机添加N种随机前缀，小表扩大N倍

6.开启动态执行计划
--conf spark.sql.adaptive.enabled=true \
--conf spark.sql.adaptive.join.enabled=true \
--conf spark.sql.adaptive.skewedJoin.enabled=true \
