1.RDD是什么，有哪些特性

1、RDD（Resilient Distributed Dataset）叫做分布式数据集，是Spark中最基本的数据抽象，它代表一个不可变、可分区、里面的元素可并行计算的集合。 
    Dataset：就是一个集合，用于存放数据的
    Distributed：分布式，可以并行在集群计算
    Resilient：表示弹性的 
        弹性表示 
            1、RDD中的数据可以存储在内存或者是磁盘
            2、RDD中的分区是可以改变的
2、五大特性： 
    （1）A list of partitions 
    一个分区列表，RDD中的数据都存在一个分区列表里面
    （2）A function for computing each split 
    作用在每一个分区中的函数
    （3）A list of dependencies on other RDDs 
    一个RDD依赖于其他多个RDD，这个点很重要，RDD的容错机制就是依据这个特性而来的
    （4）Optionally, a Partitioner for key-value RDDs (e.g. to say that the RDD is hash-partitioned) 
    可选的，针对于kv类型的RDD才具有这个特性，作用是决定了数据的来源以及数据处理后的去向
    （5）Optionally, a list of preferred locations to compute each split on (e.g. block locations for an HDFS file) 
    可选项，数据本地性，数据位置最优