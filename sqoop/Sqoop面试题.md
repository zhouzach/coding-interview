
Sqoop底层运行的任务是什么？
只有Map阶段，没有Reduce阶段的任务。


https://www.cnblogs.com/sx66/p/12040534.html

1.1.1 Sqoop导入数据到hdfs中的参数

/opt/module/sqoop/bin/sqoop import \
--connect \ # 特殊的jdbc连接的字符串
--username \
--password \
--target-dir \ # hdfs目标的目录
--delete-target-dir \ # 导入的目标目录如果存在则删除那个目录
--num-mappers \ #相当于 -m ,并行导入时map task的个数
--fields-terminated-by \
--query "$2" ' and $CONDITIONS;' # 指定满足sql和条件的数据导入

1.1.2 Sqoop导入hive时的参数
一步将表结构和数据都导入到hive中

bin/sqoop import \
--connect jdbc的url字符串 \
--table mysql中的表名\
--username 账号 \
--password 密码\
--hive-import \
--m mapTask的个数\
--hive-database hive中的数据库名;

1.1.3 Rdbms中的增量数据如何导入？

--check-column 字段名 \ #指定判断检查的依据字段
--incremental 导入模式\ # 用来指定增量导入的模式（Mode），append和lastmodified
--last-value 上一次导入结束的时间\
--m mapTask的个数 \
--merge-key 主键
补充：
·如果使用merge-key合并模式 如果是新增的数据则增加，因为incremental是lastmodified模式，那么当有数据更新了，而主键没有变，则会进行合并。
·--check-column字段当数据更新和修改这个字段的时间也要随之变化，mysql中建表时该字段修饰符，字段名timestamp default current_timestamp on update current_timestamp

1.1.4 Sqoop导入导出Null存储一致性问题
Hive中的Null在底层是以“\N”来存储，而MySQL中的Null在底层就是Null，为了保证数据两端的一致性,转化的过程中遇到null-string,null-non-string数据都转化成指定的类型，通常指定成"\N"。在导出数据时采用–input-null-string “\N” --input-null-non-string “\N” 两个参数。导入数据时采用–null-string “\N” --null-non-string “\N”。

Import导入和export导出的关系如下图所示。

 

1.1.5 Sqoop数据导出一致性问题
1）场景1：如Sqoop在导出到Mysql时，使用4个Map任务，过程中有2个任务失败，那此时MySQL中存储了另外两个Map任务导入的数据，此时老板正好看到了这个报表数据。而开发工程师发现任务失败后，会调试问题并最终将全部数据正确的导入MySQL，那后面老板再次看报表数据，发现本次看到的数据与之前的不一致，这在生产环境是不允许的。

Sqoop官网中的用户指南

使用—staging-table选项，将hdfs中的数据先导入到辅助表中，当hdfs中的数据导出成功后，辅助表中的数据在一个事务中导出到目标表中（也就是说这个过程要不完全成功，要不完全失败）。

为了能够使用staging这个选项，staging表在运行任务前或者是空的，要不就使用—clear-staging-table配置，如果staging表中有数据，并且使用了—clear-staging-table选项,sqoop执行导出任务前会删除staging表中所有的数据。

注意：–direct导入时staging方式是不可用的，使用了—update-key选项时staging方式也不能用。

sqoop export \
--connect url \
--username root \
--password 123456 \
--table app_cource_study_report \
--columns watch_video_cnt,complete_video_cnt,dt \
--fields-terminated-by "\t" \
--export-dir "/user/hive/warehouse/tmp.db/app_cource_study_analysi_${day}" \
--staging-table app_cource_study_report_tmp \
--clear-staging-table \
--input-null-string '\\N' \
--null-non-string "\\N"
2）场景2：设置map数量为1个（不推荐，面试官想要的答案不只这个）

多个Map任务时，采用–staging-table方式，仍然可以解决数据一致性问题。

1.1.6 Sqoop底层运行的任务是什么
只有Map阶段，没有Reduce阶段的任务。

1.1.7 Map task并行度设置大于1的问题
并行度导入数据的 时候 需要指定根据哪个字段进行切分 该字段通常是主键或者是自增长不重复的数值类型字段，否则会报下面的错误。

Import failed: No primary key could be found for table. Please specify one with --split-by or perform a sequential import with ‘-m 1’.

那么就是说当map task并行度大于1时，下面两个参数要同时使用

–split-by id 指定根据id字段进行切分

–m n 指定map并行度n个

1.1.8 Sqoop数据导出的时候一次执行多长时间
Sqoop任务5分钟-2个小时的都有。取决于数据量。