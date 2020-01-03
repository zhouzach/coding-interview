

RDD:
def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)]
def reduceByKey(func: (V, V) => V, numPartitions: Int): RDD[(K, V)]
def reduceByKey(func: (V, V) => V): RDD[(K, V)]

def foldByKey(
      zeroValue: V,
      partitioner: Partitioner)(func: (V, V) => V): RDD[(K, V)]

def aggregateByKey[U: ClassTag](zeroValue: U, partitioner: Partitioner)(seqOp: (U, V) => U,
      combOp: (U, U) => U): RDD[(K, U)]
def aggregateByKey[U: ClassTag](zeroValue: U, numPartitions: Int)(seqOp: (U, V) => U,
      combOp: (U, U) => U): RDD[(K, U)]
def aggregateByKey[U: ClassTag](zeroValue: U)(seqOp: (U, V) => U,
      combOp: (U, U) => U): RDD[(K, U)]

def groupByKey(partitioner: Partitioner): RDD[(K, Iterable[V])]
def groupByKey(numPartitions: Int): RDD[(K, Iterable[V])]
def groupByKey(): RDD[(K, Iterable[V])]

（1）groupByKey()是对RDD中的所有数据做shuffle,根据不同的Key映射到不同的partition中再进行aggregate。
（2）aggregateByKey()是先对每个partition中的数据根据不同的Key进行aggregate，然后将结果进行shuffle，完成各个partition之间的aggregate。因此，和groupByKey()相比，运算量小了很多。
（3）reduceByKey()也是先在单台机器中计算，再将结果进行shuffle，减小运算量

def mapValues[U](f: V => U): RDD[(K, U)]
def flatMapValues[U](f: V => TraversableOnce[U]): RDD[(K, U)]

def filter(f: T => Boolean): RDD[T]
def flatMap[U: ClassTag](f: T => TraversableOnce[U]): RDD[U]


2.Dataset:
2.1
def agg(expr: Column, exprs: Column*): DataFrame = groupBy().agg(expr, exprs : _*)

def groupBy(col1: String, cols: String*): RelationalGroupedDataset
RelationalGroupedDataset:
def count(): DataFrame

2.2
def groupByKey[K](func: MapFunction[T, K], encoder: Encoder[K]): KeyValueGroupedDataset[K, T]
def groupByKey[K: Encoder](func: T => K): KeyValueGroupedDataset[K, T]

KeyValueGroupedDataset:
def agg[U1](col1: TypedColumn[V, U1])
def agg[U1, U2](col1: TypedColumn[V, U1], col2: TypedColumn[V, U2]): Dataset[(K, U1, U2)]
def agg[U1, U2, U3](
      col1: TypedColumn[V, U1],
      col2: TypedColumn[V, U2],
      col3: TypedColumn[V, U3]): Dataset[(K, U1, U2, U3)]
def agg[U1, U2, U3, U4](
      col1: TypedColumn[V, U1],
      col2: TypedColumn[V, U2],
      col3: TypedColumn[V, U3],
      col4: TypedColumn[V, U4]): Dataset[(K, U1, U2, U3, U4)]   //TypedColumn可以是聚合functions，也可以是自定义的Aggregator

2.3
KeyValueGroupedDataset:
def mapValues[W : Encoder](func: V => W): KeyValueGroupedDataset[K, W]  //提取记录中的聚合字段，The grouping key is unchanged by this
def reduceGroups(f: (V, V) => V): Dataset[(K, V)]  //对聚合字段,实现聚合逻辑

2.4
def reduce(func: (T, T) => T): T = rdd.reduce(func)

2.5
def repartition(numPartitions: Int): Dataset[T]
def repartition(numPartitions: Int, partitionExprs: Column*): Dataset[T]
def repartition(partitionExprs: Column*): Dataset[T]
def sortWithinPartitions(sortExprs: Column*)

2.6
def withColumn(colName: String, col: Column): DataFrame
createDataFrame
createOrReplaceTempView
write

2.7
其他类SQL操作