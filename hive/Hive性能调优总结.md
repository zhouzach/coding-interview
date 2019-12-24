
https://www.cnblogs.com/frankdeng/p/9463897.html

一、Hive的压缩存储
1、合理利用文件存储格式
创建表时，尽量使用 orc、parquet 这些列式存储格式，因为列式存储的表，每一列的数据在物理上是存储在一起的，Hive查询时会只遍历需要列数据，大大减少处理的数据量。

2、压缩的原因
Hive 最终是转为 MapReduce 程序来执行的，而MapReduce 的性能瓶颈在于网络 IO 和 磁盘 IO，要解决性能瓶颈，最主要的是减少数据量，对数据进行压缩是个好的方式。
压缩 虽然是减少了数据量，但是压缩过程要消耗CPU的，但是在Hadoop中， 往往性能瓶颈不在于CPU，CPU压力并不大，所以压缩充分利用了比较空闲的 CPU



二、数据倾斜
1.join中空key转换

有时虽然某个key为空对应的数据很多，但是相应的数据不是异常数据，必须要包含在join的结果中，此时我们可以表a中key为空的字段赋一个随机的值，
使得数据随机均匀地分不到不同的reducer上。例如：

案例实操：

不随机分布空null值：

（1）设置5个reduce个数

set mapreduce.job.reduces = 5;

（2）JOIN两张表

insert overwrite table jointable

select n.* from nullidtable n left join ori b on n.id = b.id;

结果：可以看出来，出现了数据倾斜，某些reducer的资源消耗远大于其他reducer。



随机分布空null值

（1）设置5个reduce个数

set mapreduce.job.reduces = 5;

（2）JOIN两张表

insert overwrite table jointable

select n.* from nullidtable n full join ori o on

case when n.id is null then concat('hive', rand()) else n.id end = o.id;

结果：可以看出来，消除了数据倾斜，负载均衡reducer的资源消耗

2、笛卡尔积
尽量避免笛卡尔积，join的时候不加on条件，或者无效的on条件，Hive只能使用1个reducer来完成笛卡尔积

当 Hive 设定为严格模式（hive.mapred.mode=strict）时，不允许在 HQL 语句中出现笛卡尔积， 这实际说明了 Hive 对笛卡尔积支持较弱。因为找不到 Join key，
Hive 只能使用 1 个 reducer 来完成笛卡尔积。

当然也可以使用 limit 的办法来减少某个表参与 join 的数据量，但对于需要笛卡尔积语义的 需求来说，经常是一个大表和一个小表的 Join 操作，
结果仍然很大（以至于无法用单机处 理），这时 MapJoin才是最好的解决办法。MapJoin，顾名思义，会在 Map 端完成 Join 操作。 这需要将 Join 操作的一个或多个表完全读入内存。

PS：MapJoin 在子查询中可能出现未知 BUG。在大表和小表做笛卡尔积时，规避笛卡尔积的 方法是，给 Join 添加一个 Join key，
原理很简单：将小表扩充一列 join key，并将小表的条 目复制数倍，join key 各不相同；将大表扩充一列 join key 为随机数。

精髓就在于复制几倍，最后就有几个 reduce 来做，而且大表的数据是前面小表扩张 key 值 范围里面随机出来的，所以复制了几倍 n，就相当于这个随机范围就有多大 n，
那么相应的， 大表的数据就被随机的分为了 n 份。并且最后处理所用的 reduce 数量也是 n，而且也不会 出现数据倾斜。



3、Map Join
理论分析

如果不指定MapJoin或者不符合MapJoin的条件，那么Hive解析器会将Join操作转换成Common Join，即：在Reduce阶段完成join。容易发生数据倾斜。
可以用MapJoin把小表全部加载到内存在map端进行join，避免reducer处理。

1）开启MapJoin参数设置：

（1）设置自动选择Mapjoin

set hive.auto.convert.join = true; 默认为true

（2）大表小表的阀值设置（默认25M一下认为是小表）：

set hive.mapjoin.smalltable.filesize=25000000;

2）MapJoin工作机制

首先是Task A，它是一个Local Task（在客户端本地执行的Task），负责扫描小表b的数据，将其转换成一个HashTable的数据结构，并写入本地的文件中，
之后将该文件加载到DistributeCache中。

接下来是Task B，该任务是一个没有Reduce的MR，启动MapTasks扫描大表a,在Map阶段，根据a的每一条记录去和DistributeCache中b表对应的HashTable关联，并直接输出结果。

由于MapJoin没有Reduce，所以由Map直接输出结果文件，有多少个Map Task，就有多少个结果文件。



4、Group By
默认情况下，Map阶段同一Key数据分发给一个reduce，当一个key数据过大时就倾斜了。并不是所有的聚合操作都需要在Reduce端完成，很多聚合操作都可以先在Map端进行部分聚合，
最后在Reduce端得出最终结果。

1）开启Map端聚合参数设置

（1）是否在Map端进行聚合，默认为True

set hive.map.aggr = true

（2）在Map端进行聚合操作的条目数目

set hive.groupby.mapaggr.checkinterval = 100000

（3）有数据倾斜的时候进行负载均衡（默认是false）

set hive.groupby.skewindata = true

    当选项设定为 true，生成的查询计划会有两个MR Job。第一个MR Job中，Map的输出结果会随机分布到Reduce中，每个Reduce做部分聚合操作，并输出结果，
    这样处理的结果是相同的Group By Key有可能被分发到不同的Reduce中，从而达到负载均衡的目的；第二个MR Job再根据预处理的数据结果按照Group By Key分布
    到Reduce中（这个过程可以保证相同的Group By Key被分布到同一个Reduce中），最后完成最终的聚合操作。

三、Sql语句优化
1、Count(Distinct)
数据量小的时候无所谓，数据量大的情况下，由于COUNT DISTINCT操作需要用一个Reduce Task来完成，这一个Reduce需要处理的数据量太大，就会导致整个Job很难完成，
一般COUNT DISTINCT使用先GROUP BY再COUNT的方式替换：

2、left join 替换 in/exists 语句
虽然经过测验，hive1.2.1 也支持 in/exists 操作，但还是推荐使用 hive 的一个高效替代方案：left semi join

比如说：

select a.id, a.name from a where a.id in (select b.id from b);
select a.id, a.name from a where exists (select id from b where a.id = b.id);
应该转换成：

select a.id, a.name from a left semi join b on a.id = b.id;

3、排序选择
cluster by：对同一字段分桶并排序，不能和 sort by 连用

distribute by + sort by：分桶，保证同一字段值只存在一个结果文件当中，结合 sort by 保证 每个 reduceTask 结果有序

sort by：单机排序，单个 reduce 结果有序

order by：全局排序，缺陷是只能使用一个 reduce

4、合并 MapReduce操作
Multi-group by 是 Hive 的一个非常好的特性，它使得 Hive 中利用中间结果变得非常方便。 例如：

FROM (SELECT a.status, b.school, b.gender FROM status_updates a JOIN profiles b ON (a.userid =
b.userid and a.ds='2009-03-20' ) ) subq1
INSERT OVERWRITE TABLE gender_summary PARTITION(ds='2009-03-20')
SELECT subq1.gender, COUNT(1) GROUP BY subq1.gender
INSERT OVERWRITE TABLE school_summary PARTITION(ds='2009-03-20')
SELECT subq1.school, COUNT(1) GROUP BY subq1.school
上述查询语句使用了 multi-group by 特性连续 group by 了 2 次数据，使用不同的 group by key。 这一特性可以减少一次 MapReduce 操作

5、合理利用分桶：Bucketing 和 Sampling
Bucket 是指将数据以指定列的值为 key 进行 hash，hash 到指定数目的桶中。这样就可以支持高效采样了。如下例就是以 userid 这一列为 bucket 的依据，共设置 32 个 buckets

CREATE TABLE page_view(viewTime INT, userid BIGINT,
 page_url STRING, referrer_url STRING,
 ip STRING COMMENT 'IP Address of the User')
 COMMENT 'This is the page view table'
 PARTITIONED BY(dt STRING, country STRING)
 CLUSTERED BY(userid) SORTED BY(viewTime) INTO 32 BUCKETS
 ROW FORMAT DELIMITED
 FIELDS TERMINATED BY '1'
 COLLECTION ITEMS TERMINATED BY '2'
 MAP KEYS TERMINATED BY '3'
 STORED AS SEQUENCEFILE;
 
通常情况下，Sampling 在全体数据上进行采样，这样效率自然就低，它要去访问所有数据。 而如果一个表已经对某一列制作了 bucket，就可以采样所有桶中指定序号的某个桶，这就减少了访问量。

如下例所示就是采样了 page_view 中 32 个桶中的第三个桶的全部数据：

SELECT * FROM page_view TABLESAMPLE(BUCKET 3 OUT OF 32);
如下例所示就是采样了 page_view 中 32 个桶中的第三个桶的一半数据：

SELECT * FROM page_view TABLESAMPLE(BUCKET 3 OUT OF 64);
五、数据倾斜
1、Map数
1）通常情况下，作业会通过input的目录产生一个或者多个map任务。主要的决定因素有：input的文件总个数，input的文件大小，集群设置的文件块大小。

在 MapReduce 的编程案例中，我们得知，一个MR Job的 MapTask 数量是由输入分片 InputSplit 决定的。而输入分片是由 FileInputFormat.getSplit()决定的。一个输入分片对应一个 MapTask， 而输入分片是由三个参数决定的：



输入分片大小的计算是这么计算出来的：

long splitSize = Math.max(minSize, Math.min(maxSize, blockSize))

默认情况下，输入分片大小和 HDFS 集群默认数据块大小一致，也就是默认一个数据块，启用一个 MapTask 进行处理，这样做的好处是避免了服务器节点之间的数据传输，提高 job 处理效率。

2）是不是map数越多越好？（Map 数过大）

答案是否定的。如果一个任务有很多小文件（远远小于块大小128m），则每个小文件也会被当做一个块，用一个map任务来完成，而一个map任务启动和初始化的时间远远大于逻辑处理的时间，就会造成很大的资源浪费。而且，同时可执行的map数是受限的。

3）是不是保证每个map处理接近128m的文件块，就高枕无忧了？（Map 数过小）
答案也是不一定。比如有一个127m的文件，正常会用一个map去完成，但这个文件只有一个或者两个小字段，却有几千万的记录，如果map处理的逻辑比较复杂，用一个map任务去做，肯定也比较耗时。

针对上面的问题2和3，我们需要采取两种方式来解决：即减少map数和增加map数；

2、小文件进行合并
在map执行前合并小文件，减少map数：CombineHiveInputFormat具有对小文件进行合并的功能（系统默认的格式）。HiveInputFormat没有对小文件合并功能。

set hive.merge.mapfiles = true                   ##在 map only 的任务结束时合并小文件
set hive.merge.mapredfiles = false               ## true 时在 MapReduce 的任务结束时合并小文件
set hive.merge.size.per.task = 256*1000*1000     ##合并文件的大小
set mapred.max.split.size=256000000;             ##每个 Map 最大分割大小
set mapred.min.split.size.per.node=1;            ##一个节点上 split 的最少值
set hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;    ##执行Map前进行小文件合并
3、复杂文件增加Map数
当input的文件都很大，任务逻辑复杂，map执行非常慢的时候，可以考虑增加Map数，来使得每个map处理的数据量减少，从而提高任务的执行效率。

增加map的方法为：根据computeSliteSize(Math.max(minSize,Math.min(maxSize,blocksize)))=blocksize=128M公式，调整maxSize最大值。让maxSize最大值低于blocksize就可以增加map的个数。

案例实操：

（1）执行查询

hive (default)> select count(*) from emp;

Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1

（2）设置最大切片值为100个字节

hive (default)> set mapreduce.input.fileinputformat.split.maxsize=100;

hive (default)> select count(*) from emp;

Hadoop job information for Stage-1: number of mappers: 6; number of reducers: 1

4、Reduce数
Hadoop MapReduce 程序中，reducer 个数的设定极大影响执行效率，这使得 Hive 怎样决定 reducer 个数成为一个关键问题。遗憾的是 Hive 的估计机制很弱，不指定 reducer 个数的情况下，Hive 会猜测确定一个 reducer 个数，基于以下两个设定：

1、hive.exec.reducers.bytes.per.reducer（默认为 256000000）

2、hive.exec.reducers.max（默认为 1009）

3、mapreduce.job.reduces=-1（设置一个常量 reducetask 数量）

计算 reducer 数的公式很简单： N=min(参数 2，总输入数据量/参数 1) 通常情况下，有必要手动指定 reducer 个数。考虑到 map 阶段的输出数据量通常会比输入有 大幅减少，因此即使不设定 reducer 个数，重设参数 2 还是必要的。

依据 Hadoop 的经验，可以将参数 2 设定为 0.95*(集群中 datanode 个数)。

1）调整reduce个数方法一

（1）每个Reduce处理的数据量默认是256MB

hive.exec.reducers.bytes.per.reducer=256000000

（2）每个任务最大的reduce数，默认为1009

hive.exec.reducers.max=1009

（3）计算reducer数的公式

N=min(参数2，总输入数据量/参数1)

2）调整reduce个数方法二

在hadoop的mapred-default.xml文件中修改

设置每个job的Reduce个数

set mapreduce.job.reduces = 15;

3）reduce个数并不是越多越好

1）过多的启动和初始化reduce也会消耗时间和资源；

2）另外，有多少个reduce，就会有多少个输出文件，如果生成了很多个小文件，那么如果这些小文件作为下一个任务的输入，则也会出现小文件过多的问题；

在设置reduce个数的时候也需要考虑这两个原则：处理大数据量利用合适的reduce数；使单个reduce任务处理数据量大小要合适；

5、并行执行
Hive会将一个查询转化成一个或者多个阶段。这样的阶段可以是MapReduce阶段、抽样阶段、合并阶段、limit阶段。或者Hive执行过程中可能需要的其他阶段。默认情况下，Hive一次只会执行一个阶段。不过，某个特定的job可能包含众多的阶段，而这些阶段可能并非完全互相依赖的，也就是说有些阶段是可以并行执行的，这样可能使得整个job的执行时间缩短。不过，如果有更多的阶段可以并行执行，那么job可能就越快完成。

通过设置参数hive.exec.parallel值为true，就可以开启并发执行。不过，在共享集群中，需要注意下，如果job中并行阶段增多，那么集群利用率就会增加。

set hive.exec.parallel=true;              //打开任务并行执行

set hive.exec.parallel.thread.number=16;  //同一个sql允许最大并行度，默认为8。

当然，得是在系统资源比较空闲的时候才有优势，否则，没资源，并行也起不来。

6、严格模式
Hive提供了一个严格模式，可以防止用户执行那些可能意向不到的不好的影响的查询。

通过设置属性hive.mapred.mode值为默认是非严格模式nonstrict 。开启严格模式需要修改hive.mapred.mode值为strict，开启严格模式可以禁止3种类型的查询。

<property>

    <name>hive.mapred.mode</name>

    <value>strict</value>

    <description>

      The mode in which the Hive operations are being performed.

      In strict mode, some risky queries are not allowed to run. They include:

        Cartesian Product.

        No partition being picked up for a query.

        Comparing bigints and strings.

        Comparing bigints and doubles.

        Orderby without limit.

    </description>

  </property>

1）对于分区表，除非where语句中含有分区字段过滤条件来限制范围，否则不允许执行。换句话说，就是用户不允许扫描所有分区。进行这个限制的原因是，通常分区表都拥有非常大的数据集，而且数据增加迅速。没有进行分区限制的查询可能会消耗令人不可接受的巨大资源来处理这个表。

2）对于使用了order by语句的查询，要求必须使用limit语句。因为order by为了执行排序过程会将所有的结果数据分发到同一个Reducer中进行处理，强制要求用户增加这个LIMIT语句可以防止Reducer额外执行很长一段时间。

3）限制笛卡尔积的查询。对关系型数据库非常了解的用户可能期望在执行JOIN查询的时候不使用ON语句而是使用where语句，这样关系数据库的执行优化器就可以高效地将WHERE语句转化成那个ON语句。不幸的是，Hive并不会执行这种优化，因此，如果表足够大，那么这个查询就会出现不可控的情况。

7、JVM重用
JVM重用是Hadoop调优参数的内容，其对Hive的性能具有非常大的影响，特别是对于很难避免小文件的场景或task特别多的场景，这类场景大多数执行时间都很短。

Hadoop的默认配置通常是使用派生JVM来执行map和Reduce任务的。这时JVM的启动过程可能会造成相当大的开销，尤其是执行的job包含有成百上千task任务的情况。JVM重用可以使得JVM实例在同一个job中重新使用N次。N的值可以在Hadoop的mapred-site.xml文件中进行配置。通常在10-20之间，具体多少需要根据具体业务场景测试得出。

<property>

  <name>mapreduce.job.jvm.numtasks</name>

  <value>10</value>

  <description>How many tasks to run per jvm. If set to -1, there is

  no limit.

  </description>

</property>

这个功能的缺点是，开启JVM重用将一直占用使用到的task插槽，以便进行重用，直到任务完成后才能释放。如果某个“不平衡的”job中有某几个reduce task执行的时间要比其他Reduce task消耗的时间多的多的话，那么保留的插槽就会一直空闲着却无法被其他的job使用，直到所有的task都结束了才会释放。

8、推测执行
在分布式集群环境下，因为程序Bug（包括Hadoop本身的bug），负载不均衡或者资源分布不均等原因，会造成同一个作业的多个任务之间运行速度不一致，有些任务的运行速度可能明显慢于其他任务（比如一个作业的某个任务进度只有50%，而其他所有任务已经运行完毕），则这些任务会拖慢作业的整体执行进度。为了避免这种情况发生，Hadoop采用了推测执行（Speculative Execution）机制，它根据一定的法则推测出“拖后腿”的任务，并为这样的任务启动一个备份任务，让该任务与原始任务同时处理同一份数据，并最终选用最先成功运行完成任务的计算结果作为最终结果。

设置开启推测执行参数：Hadoop的mapred-site.xml文件中进行配置

<property>

  <name>mapreduce.map.speculative</name>

  <value>true</value>

  <description>If true, then multiple instances of some map tasks

               may be executed in parallel.</description>

</property>



<property>

  <name>mapreduce.reduce.speculative</name>

  <value>true</value>

  <description>If true, then multiple instances of some reduce tasks

               may be executed in parallel.</description>

</property>

不过hive本身也提供了配置项来控制reduce-side的推测执行：

  <property>

    <name>hive.mapred.reduce.tasks.speculative.execution</name>

    <value>true</value>

    <description>Whether speculative execution for reducers should be turned on. </description>

  </property>

关于调优这些推测执行变量，还很难给一个具体的建议。如果用户对于运行时的偏差非常敏感的话，那么可以将这些功能关闭掉。如果用户因为输入数据量很大而需要执行长时间的map或者Reduce task的话，那么启动推测执行造成的浪费是非常巨大大。

9、执行计划（Explain）
1）基本语法

EXPLAIN [EXTENDED | DEPENDENCY | AUTHORIZATION] query

2）案例实操

（1）查看下面这条语句的执行计划

hive (default)> explain select * from emp;

hive (default)> explain select deptno, avg(sal) avg_sal from emp group by deptno;

（2）查看详细执行计划

hive (default)> explain extended select * from emp;

hive (default)> explain extended select deptno, avg(sal) avg_sal from emp group by deptno;


8、动态分区调整
关系型数据库中，对分区表Insert数据时候，数据库自动会根据分区字段的值，将数据插入到相应的分区中，Hive中也提供了类似的机制，即动态分区(Dynamic Partition)，只不过，使用Hive的动态分区，需要进行相应的配置。

1）开启动态分区参数设置

（1）开启动态分区功能（默认true，开启）

hive.exec.dynamic.partition=true

（2）设置为非严格模式（动态分区的模式，默认strict，表示必须指定至少一个分区为静态分区，nonstrict模式表示允许所有的分区字段都可以使用动态分区。）

hive.exec.dynamic.partition.mode=nonstrict

（3）在所有执行MR的节点上，最大一共可以创建多少个动态分区。

hive.exec.max.dynamic.partitions=1000

（4）在每个执行MR的节点上，最大可以创建多少个动态分区。该参数需要根据实际的数据来设定。比如：源数据中包含了一年的数据，即day字段有365个值，那么该参数就需要设置成大于365，如果使用默认值100，则会报错。

hive.exec.max.dynamic.partitions.pernode=100

（5）整个MR Job中，最大可以创建多少个HDFS文件。

hive.exec.max.created.files=100000

（6）当有空分区生成时，是否抛出异常。一般不需要设置。

hive.error.on.empty.partition=false