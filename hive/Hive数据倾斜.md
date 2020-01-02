
https://zhuanlan.zhihu.com/p/91044262

倾斜原因： map输出数据按Key Hash分配到reduce中,由于key分布不均匀、或者业务数据本身的特点。等原因造成的reduce上的数据量差异过大。

1.1)key分布不均匀

1.2)业务数据本身的特性

1.3)SQL语句造成数据倾斜, 通常join、group by、count distinct操作的时候可能会发生数据倾斜

解决方案：

1>参数调节：

    hive.map.aggr=true //map端聚合

    hive.groupby.skewindata=true //负载均衡

有数据倾斜的时候进行负载均衡，当选项设定为true,生成的查询计划会有两个MR Job。第一个MR Job中，Map的输出结果集合会随机分布到Reduce中，
每个Reduce做部分聚合操作，并输出结果，这样处理的结果是相同Group By Key有可能被分发到不同的Reduce中，从而达到负载均衡的目的；
第二个MR Job在根据预处理的数据结果按照 Group By Key 分布到Reduce中(这个过程可以保证相同的 Group By Key 被分布到同一个Reduce中)，最后完成最终的聚合操作。

2>SQL语句调节：

   1)选用join key 分布最均匀的表作为驱动表。做好列裁剪和filter操作，以达到两表join的时候，数据量相对变小的效果。

   2)大小表Join： 使用map join让小的维度表（1000条以下的记录条数）先进内存。在Map端完成Reduce。

   3)大表Join大表：把空值的Key变成一个字符串加上一个随机数，把倾斜的数据分到不同的reduce上，由于null值关联不上，处理后并不影响最终的结果。
    select n.* from nullidtable n full join ori o on
    case when n.id is null then concat('hive', rand()) else n.id end = o.id;

   4)count distinct key最终会将所有不同的key汇聚到最后一个reduce统计，如果key值太多，可能需要处理很长时间，我们可以先使用group by key，
      然后为每个组附上一个随机数字段，然后针对随机数进行group by，计算sum，最后在外层对每组随机数的sum进行加总
      select sum(t1.cnt) as res from (
            select tag,count(1) as cnt from (
                select key,rand()*100 as tag from t group by key
            ) t1 group by tag
      ) t2
