https://www.jianshu.com/p/f3e3a4c3b429

Sqoop架构
Sqoop 架构
Sqoop import原理
从传统数据库获取元数据信息(schema、table、field、field type)，把导入功能转换为只有Map的Mapreduce作业，在mapreduce中有很多map，每个map读一片数据，进而并行的完成数据的拷贝

Sqoop 在 import 时，需要制定 split-by 参数。
Sqoop 根据不同的 split-by参数值 来进行切分, 然后将切分出来的区域分配到不同 map 中。每个map中再处理数据库中获取的一行一行的值，写入到 HDFS 中。同时split-by 根据不同的参数类型有不同的切分方法，如比较简单的int型，Sqoop会取最大和最小split-by字段值，然后根据传入的 num-mappers来确定划分几个区域。

Sqoop export 原理
获取导出表的schema、meta信息，和Hadoop中的字段match；多个map only作业同时运行，完成hdfs中数据导出到关系型数据库中

导入到 Hive
sqoop import \
  --connect ${jdbc_url} \
  --username ${jdbc_username} --password  ${jdbc_passwd} \
  --table ${jdbc_table} --fields-terminated-by "\001" --lines-terminated-by "\n" \
  --hive-import --hive-overwrite --hive-table ${hive_table} \
  --null-string '\\N' --null-non-string '\\N'
Ref:

Sqoop导入关系数据库到Hive
Sqoop introduce

