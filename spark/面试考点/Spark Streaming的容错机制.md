
https://zhuanlan.zhihu.com/p/110299187

一、Spark Streaming的容错
此时我们启动一个Application任务，根据我们启动的模式和运行集群的类型，会根据一定的策略选择一台服务器当做Driver服务器，在其初始化完成之后，就会顺带把这些Executor给初始化完成。

之后Driver就会发送Receiver到某一个Executor上面，Receiver就是负责用来接收数据的（你也可以当成是一个task任务）。它每隔200毫秒就会将自己接收上来的数据存储成一个block，而且这个block肯定就会有副本机制。

当block块完成生成，就会给Driver返回报告信息，让它得知block们的存在，之后Driver会生成一个个小的任务分发给这些Executor完成。

1.1 Executor挂掉
那此时我们的Executor挂掉怎么办呢？

首先我们图中下方的Executor挂掉，那是完全不碍事的，因为所有的数据都在上面的Executor中，可是当上方的Executor也挂掉的话，它的Receiver自然就无法正常工作了，那数据就会丢失了。

你以为我会这么说吗？其实不然，因为Spark Streaming自身就存在很好的容错机制，当存在有Receiver的Executor挂掉之后Driver会自动又寻找一个Executor把Receiver给再创建回来，因为数据都是有备份block的，所以也不太需要担心数据丢失的问题

而那些本身分发给挂掉的Executor的任务也会重新分发出来重新执行，这一点是不需要人工干预的。

1.2 Driver挂掉
那如果Driver挂掉，那所有的Executor也会失败，这时我们就需要使用checkPoint机制，它会定期的将Driver信息写入到HDFS中。

根据这个思路，我们想达到的目标是，让Driver挂掉时能够自动恢复，接着上一次的结果进行计算，而不需要人为干预

1.2.1 设置自动重启Driver
此时假设我们是在集群模式下，因为只有在集群模式下才能实现容错。

Standalone

在spark-submit中增加以下两个参数：

--deploy-mode cluster

--supervise（这个就是让任务自动重启的参数）
复制代码
Yarn（大多数的情况）

在spark-submit中增加以下参数：

--deploy-mode cluster
复制代码
在yarn配置中设置 yarn.resourcemanager.am.max-attemps，这个是设置一个任务最多可失败多少次。假设这个参数配置为3（我们公司就是3次😶），那我们的任务挂掉的时候，Yarn会自动帮我们重启，第三次后再挂掉，那就真的挂了。

Mesos

Marathon 可以重启 Mesos应用

1.2.2 设置HDFS的checkPoint目录
指定一个checkPoint目录保存你的关键信息，不然就无法恢复了。一般我们设置的目录就是HDFS目录，本身HDFS就有高可用的特性，没有单点故障问题。

就一行代码，没啥特别的

streamingContext.setCheckpoint(hdfsDirectory) 
复制代码
1.2.3 code
// Function to create and setup a new StreamingContext
def functionToCreateContext(): StreamingContext = {
  val ssc = new StreamingContext(...)   // new context
  val lines = ssc.socketTextStream(...) // create DStreams
  ...
  ssc.checkpoint(checkpointDirectory)   // set checkpoint directory
  ssc
}

// Get StreamingContext from checkpoint data or create a new one


val context = StreamingContext.getOrCreate(checkpointDirectory, functionToCreateContext _)

// Do additional setup on context that needs to be done,
// irrespective of whether it is being started or restarted
context. ...

// Start the context
context.start()
context.awaitTermination()
代码逻辑其实就是getOrCreate，如果checkpointDirectory存在数据，那就恢复，如果不存在数据，那就自己创建一个Driver

1.3 Executor的数据丢失问题
此时我们可以把Driver给恢复回来，可是Driver宕机导致Executor的内存中存在的数据也一并丢失掉了，所以我们也得对这部分数据进行一些容错机制

WAL机制就是：比如我们现在Kafka通过Data Stream发送数据过来了,Receiver接收后会开启WAL机制，就会将数据写入到HDFS里面，待写入完成后Receiver会通知Kafka告知数据已经成功接收到了（此时Kafka就是acks设置为-1了），这个做法其实就是和checkPoint是一模一样的，只是换了个WAL的名号

1.3.1 设置checkpoint目录
streamingContext.setCheckpoint(hdfsDirectory)
复制代码
这个就不用展开了吧，刚刚才说完的东西

1.3.2 开启WAL日志
它默认是不开启的，所以要手动设置为true

sparkConf.set(“spark.streaming.receiver.writeAheadLog.enable”, “true”)
复制代码
1.3.3 reliable receiver
这里不能被误导，其实在这里说的是可靠的数据源问题。这个可靠的数据源就是指Kafka，当数据写完了WAL后，才告诉Kafka数据已经消费，对于没有反馈给Kafka的数据，可以从Kafka中重新消费数据

这话好像有点绕口，其实就是这样：Receiver要往HDFS上去写入数据，这时如果程序挂了，根本就没来得及去写，那此时我们就会再次从Kafka中去重新获取，那Kafka确实就是能够支持数据的回溯，再发送之前发送过的数据给Receiver（Kafka系列的文章中已经写过了）。

那什么才算是不可靠的数据源呢，比如Socket就是，它并不能保留之前的数据，丢了就是丢了，无法再次从它那里获取。

1.3.4 取消备份
使用StorageLevel.MEMORY_AND_DISK_SER来存储数据源，不需要后缀为2的策略了（默认是后缀为2的策略），因为HDFS已经是多副本了。

1.4 解决某一个task运行很慢的问题
我们需要开启推测机制：

spark.speculation=true，
复制代码
这个机制开启后每隔一段时间来检查有哪些正在运行的task需要重新调度，时间间隔设置如下

spark.speculation.interval=100ms
复制代码
此时我们如何判断一个task需要重新调度呢？有两个需要达到的条件。此时我们假设总的task有10个

成功task的数量 > 0.75 * 10，0.75的对应参数为， spark.speculation.quantile=0.75
0.75是此参数的默认值

正在运行的task的运行时间 > 1.5 * 成功 运行task的 平均 时间，1.5的对应参数为， spark.speculation.multiplier=1.5
1.5是此参数的默认值

满足上述两个条件，则这个正在运行的task需要重新等待调度。