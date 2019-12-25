
1.创建分桶，分区表


2.对 Hive 输出结果和中间都进行压缩：
set hive.exec.compress.output=true // 默认值是 false，不压缩
set hive.exec.compress.intermediate=true // 默认值是 false，为 true 时 MR 设置的压缩才启用

Map 输出结果也以 Gzip 进行压缩：
set mapred.map.output.compress=true
set mapreduce.map.output.compress.codec=org.apache.hadoop.io.compress.GzipCodec // 默认值是 org.apache.hadoop.io.compress.DefaultCodec 

Job 输出文件按照 block 以 GZip 的方式进行压缩：
set mapreduce.output.fileoutputformat.compress=true // 默认值是 false
set mapreduce.output.fileoutputformat.compress.type=BLOCK   // 默认值是 Record
set mapreduce.output.fileoutputformat.compress.codec=org.apache.hadoop.io.compress.GzipCodec // 默认值是 org.apache.hadoop.io.compress.DefaultCodec
SET mapred.output.compression.codec=org.apache.hadoop.io.compress.SnappyCodec;
set parquet.compression=snappy;

3.小文件进行合并:
在map执行前合并小文件，减少map数：CombineHiveInputFormat具有对小文件进行合并的功能（系统默认的格式）。HiveInputFormat没有对小文件合并功能。
set hive.merge.mapfiles = true                   ##在 map only 的任务结束时合并小文件
set hive.merge.mapredfiles = false               ## true 时在 MapReduce 的任务结束时合并小文件
set hive.merge.size.per.task = 256*1000*1000     ##合并文件的大小
set mapred.max.split.size=256000000;             ##每个 Map 最大分割大小
set mapred.min.split.size.per.node=1;            ##一个节点上 split 的最少值
set hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;    ##执行Map前进行小文件合并

4.开启join倾斜优化
set hive.optimize.skewjoin = true

5.Map Join:
如果不指定MapJoin或者不符合MapJoin的条件，那么Hive解析器会将Join操作转换成Common Join，即：在Reduce阶段完成join。容易发生数据倾斜。
可以用MapJoin把小表全部加载到内存在map端进行join，避免reducer处理。
set hive.auto.convert.join = true; 默认为true
set hive.mapjoin.smalltable.filesize=25000000;（默认25M以下认为是小表）

6.Map端聚合:
set hive.map.aggr = true; //默认为True
set hive.groupby.mapaggr.checkinterval = 100000
set hive.groupby.skewindata = true; //默认是false

7.设置Map数：
df.blocksize   128M
mapreduce.input.fileinputformat.split.minsize     1
mapreduce.input.fileinputformat.split.maxsize     Long.MAX_VALUE
long splitSize = Math.max(minSize, Math.min(maxSize, blockSize))
让maxSize最大值低于blocksize就可以增加map的个数
set mapreduce.input.fileinputformat.split.maxsize=100;

8.设置reduce数量
1.set mapreduce.job.reduces = 15;
2.min(hive.exec.reducers.max,总输入数据量/hive.exec.reducers.bytes.per.reducer)
  hive.exec.reducers.bytes.per.reducer默认为 256000000


9.动态分区调整:
hive.exec.dynamic.partition=true; //默认为True
hive.exec.dynamic.partition.mode=nonstrict;
hive.exec.max.dynamic.partitions=1000;
hive.exec.max.dynamic.partitions.pernode=365;
hive.exec.max.created.files=100000;
hive.error.on.empty.partition=false;

10.查看详细执行计划:
explain extended select deptno, avg(sal) avg_sal from emp group by deptno;
