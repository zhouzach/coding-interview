
https://zhuanlan.zhihu.com/p/75550159


HIVE与RDBMS关系数据库的区别

1）存储，hive 存储在 hdfs 上，oracle 存储在本地文件系统。
2）扩展性，hive 可以扩展到数千节点，oracle 理论上只可扩展到 100 台左右。
3）单表存储，数据量大 hive 可以分区分桶，oracle 数据量大只能分表。

1、hive存储的数据量比较大，适合海量数据，适合存储轨迹类历史数据，适合用来做离线分析、数据挖掘运算，

事务性较差，实时性较差

 rdbms一般数据量相对来说不会太大，适合事务性计算，实时性较好，更加接近上层业务

2、hive的计算引擎是hadoop的mapreduce，存储是hadoop的hdfs文件系统，

 rdbms的引擎由数据库自己设计实现例如mysql的innoDB，存储用的是数据库服务器本地的文件系统

3、hive由于基于hadoop所以存储和计算的扩展能力都很好，

 rdbms在这方面比较弱，比如orcale的分表和扩容就很头疼

4、hive表格没有主键、没有索引、不支持对具体某一行的操作，适合对批量数据的操作，不支持对数据的update操作，

更新的话一般是先删除表然后重新落数据

 rdbms事务性强，有主键、索引，支持对具体某一行的增删改查等操作

5、hive的SQL为HQL，与标准的RDBMS的SQL存在有不少的区别，相对来说功能有限

rdbms的SQL为标准SQL，功能较为强大。


hive 中的压缩格式 RCFile、 TextFile、 SequenceFile 各有什么区别？
TextFile：默认格式，数据不做压缩，磁盘开销大，数据解析开销大
SequenceFile：Hadoop API提供的一种二进制文件支持，使用方便，可分割，可压缩，支持三种压缩，NONE，RECORD，BLOCK。
RCFILE 是一种行列存储相结合的方式。首先，将数据按行分块，保证同一个 record 在同一个块上，避免读一个记录读取多个block。
其次，块数据列式存储，有利于数据压缩和快速的列存取。数据加载的时候性能消耗大，但具有较好的压缩比和查询响应。