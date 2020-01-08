
https://www.cnblogs.com/frankdeng/p/9308585.html

Spark Streaming + Kafka集成指南
Kafka项目在版本0.8和0.10之间引入了一个新的消费者API，因此有两个独立的相应Spark Streaming包可用。请选择正确的包， 请注意，0.8集成与后来的0.9和0.10代理兼容，但0.10集成与早期的代理不兼容。

注意：从Spark 2.3.0开始，不推荐使用Kafka 0.8支持。

Spark Streaming从Kafka接收数据，转换为spark streaming中的数据结构Dstream。数据接收方式有两种 ：1 使用Receiver接收的旧方法：2使用Direct拉取的新方法（在Spark 1.3中引入）。

https://spark.apache.org/docs/1.6.3/streaming-kafka-integration.html

https://spark.apache.org/docs/2.3.1/streaming-kafka-0-10-integration.html

Receiver方式
     Received是使用Kafka高级Consumer API实现的。与所有接收器一样，从Kafka通过Receiver接收的数据存储在Spark Executor的内存中，然后由Spark Streaming启动的job来处理数据。然而默认配置下，这种方式可能会因为底层的失败而丢失数据（请参阅接收器可靠性）。如果要启用高可靠机制，确保零数据丢失，要启用Spark Streaming的预写日志机制（Write Ahead Log，（已引入）在Spark 1.2）。该机制会同步地将接收到的Kafka数据保存到分布式文件系统（比如HDFS）上的预写日志中，以便底层节点在发生故障时也可以使用预写日志中的数据进行恢复。

如下图：



接下来，我们将讨论如何在流应用程序中使用此方法。

1 链接 
对于使用Maven项目定义的Scala / Java应用程序时，我们需要添加相应的依赖包：

<dependency><!-- Spark Streaming Kafka -->
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-streaming-kafka_2.11</artifactId>
    <version>1.6.3</version>
</dependency>
2 编程 
在流应用程序代码中，导入KafkaUtils并创建输入DStream，如下所示。

Scala编程：

import org.apache.spark.streaming.kafka._

   val kafkaStream = KafkaUtils.createStream(streamingContext, 
     [ZK quorum], [consumer group id], [per-topic number of Kafka partitions to consume])
Java编程

 import org.apache.spark.streaming.kafka.*;

 JavaPairReceiverInputDStream<String, String> kafkaStream = 
     KafkaUtils.createStream(streamingContext,
     [ZK quorum], [consumer group id], [per-topic number of Kafka partitions to consume]);
还有几个需要注意的点：

Kafka中topic的partition与Spark Streaming中生成的RDD的partition无关，因此，在KafkaUtils.createStream()中，增加某个topic的partition的数量，只会增加单个Receiver消费topic的线程数，也就是读取Kafka中topic partition的线程数量，它不会增加Spark在处理数据时的并行性。
可以使用不同的consumer group和topic创建多个Kafka输入DStream，以使用多个receiver并行接收数据。
如果已使用HDFS等复制文件系统启用了“预读日志”，则接收的数据已在日志中复制。因此，输入流的存储级别的存储级别StorageLevel.MEMORY_AND_DISK_SER（即，使用KafkaUtils.createStream(..., StorageLevel.MEMORY_AND_DISK_SER)）。
3 部署
与任何Spark应用程序一样，spark-submit用于启动应用程序。但是，Scala / Java应用程序和Python应用程序的细节略有不同。

对于Scala和Java应用程序，如果您使用SBT或Maven进行项目管理，则将spark-streaming-kafka_2.11其及其依赖项打包到应用程序JAR中。确保spark-core_2.10并spark-streaming_2.10标记为providedSpark安装中已存在的依赖项。然后使用spark-submit启动应用程序。

对于缺少SBT / Maven项目管理的Python应用程序，spark-streaming-kafka_2.11可以直接将其依赖项添加到spark-submit使用中--packages。那是，

 ./bin/spark-submit --packages org.apache.spark:spark-streaming-kafka_2.11:1.6.3 ...
另外，您也可以下载Maven构件的JAR spark-streaming-kafka-assembly从 Maven仓库，并将其添加到spark-submit用--jars。

Direct方式
在spark1.3之后，引入了Direct方式。不同于Receiver的方式，Direct方式没有receiver这一层，其会周期性的获取Kafka中每个topic的每个partition中的最新offsets，之后根据设定的maxRatePerPartition来处理每个batch。其形式如下图：

这种方法相较于Receiver方式的优势在于：

简化的并行：在Receiver的方式中我们提到创建多个Receiver之后利用union来合并成一个Dstream的方式提高数据传输并行度。而在Direct方式中，Kafka中的partition与RDD中的partition是一一对应的并行读取Kafka数据，这种映射关系也更利于理解和优化。
高效：在Receiver的方式中，为了达到0数据丢失需要将数据存入Write Ahead Log中，这样在Kafka和日志中就保存了两份数据，浪费！而第二种方式不存在这个问题，只要我们Kafka的数据保留时间足够长，我们都能够从Kafka进行数据恢复。
精确一次：在Receiver的方式中，使用的是Kafka的高阶API接口从Zookeeper中获取offset值，这也是传统的从Kafka中读取数据的方式，但由于Spark Streaming消费的数据和Zookeeper中记录的offset不同步，这种方式偶尔会造成数据重复消费。而第二种方式，直接使用了简单的低阶Kafka API，Offsets则利用Spark Streaming的checkpoints进行记录，消除了这种不一致性。
请注意，此方法的一个缺点是它不会更新Zookeeper中的偏移量，因此基于Zookeeper的Kafka监视工具将不会显示进度。但是，您可以在每个批处理中访问此方法处理的偏移量，并自行更新Zookeeper。

接下来，我们将讨论如何在流应用程序中使用此方法。

1 链接
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-streaming-kafka-0-10_2.11</artifactId>
    <version>2.3.1</version>
</dependency>
2 编程
请注意，导入的命名空间包括版本org.apache.spark.streaming.kafka010

Scala编程

复制代码
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

val kafkaParams = Map[String, Object](
  "bootstrap.servers" -> "node21:9092,node22:9092,node23:9092",
  "key.deserializer" -> classOf[StringDeserializer],
  "value.deserializer" -> classOf[StringDeserializer],
  "group.id" -> "use_a_separate_group_id_for_each_stream",
  "auto.offset.reset" -> "latest",
  "enable.auto.commit" -> (false: java.lang.Boolean)
)

val topics = Array("topicA", "topicB")
val stream = KafkaUtils.createDirectStream[String, String](
  streamingContext,
  PreferConsistent,
  Subscribe[String, String](topics, kafkaParams)
)

stream.map(record => (record.key, record.value))
复制代码
流中的每个项目都是ConsumerRecord，有关可能的kafkaParams，请参阅Kafka使用者配置文档。如果Spark批处理持续时间大于默认的Kafka心跳会话超时（30秒），请适当增加heartbeat.interval.ms和session.timeout.ms。对于大于5分钟的批次，这将需要在代理上更改group.max.session.timeout.ms。请注意，该示例将enable.auto.commit设置为false，有关讨论，请参阅存储偏移。

3 Direct方式案例
复制代码
package com.xyg.spark
 
import kafka.serializer.{StringDecoder, Decoder}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}
 
import scala.reflect.ClassTag
 
/**
  * Created by Administrator on 2018/7/28.
  */
object SparkStreamDemo {
  def main(args: Array[String]) {
 
    val conf = new SparkConf()
    conf.setAppName("spark_streaming")
    conf.setMaster("local[*]")
 
    val sc = new SparkContext(conf)
    sc.setCheckpointDir("D:/checkpoints")
    sc.setLogLevel("ERROR")
 
    val ssc = new StreamingContext(sc, Seconds(5))
 
    // val topics = Map("spark" -> 2)
 
    val kafkaParams = Map[String, String](
      "bootstrap.servers" -> "node21:9092,node22:9092,node23:9092",
      "group.id" -> "spark",
      "auto.offset.reset" -> "smallest"
    )
    // 直连方式拉取数据，这种方式不会修改数据的偏移量，需要手动的更新
    val lines =  KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set("spark")).map(_._2)
    // val lines = KafkaUtils.createStream(ssc, "node21:2181,node22:2181,node23:2181", "spark", topics).map(_._2)
 
    val ds1 = lines.flatMap(_.split(" ")).map((_, 1))
 
    val ds2 = ds1.updateStateByKey[Int]((x:Seq[Int], y:Option[Int]) => {
      Some(x.sum + y.getOrElse(0))
    })
 
    ds2.print()
 
    ssc.start()
    ssc.awaitTermination()
 
  }
}
复制代码
Spark向kafka中写入数据
上文阐述了Spark如何从Kafka中流式的读取数据，下面我整理向Kafka中写数据。与读数据不同，Spark并没有提供统一的接口用于写入Kafka，所以我们需要使用底层Kafka接口进行包装。
最直接的做法我们可以想到如下这种方式：

复制代码
input.foreachRDD(rdd =>
  // 不能在这里创建KafkaProducer
  rdd.foreachPartition(partition =>
    partition.foreach{
      case x:String=>{
        val props = new HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        println(x)
        val producer = new KafkaProducer[String,String](props)
        val message=new ProducerRecord[String, String]("output",null,x)
        producer.send(message)
      }
    }
  )
) 
复制代码
但是这种方式缺点很明显，对于每个partition的每条记录，我们都需要创建KafkaProducer，然后利用producer进行输出操作，注意这里我们并不能将KafkaProducer的新建任务放在foreachPartition外边，因为KafkaProducer是不可序列化的（not serializable）。显然这种做法是不灵活且低效的，因为每条记录都需要建立一次连接。如何解决呢？

1.首先，我们需要将KafkaProducer利用lazy val的方式进行包装如下：

复制代码
import java.util.concurrent.Future
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord, RecordMetadata }
class KafkaSink[K, V](createProducer: () => KafkaProducer[K, V]) extends Serializable {
  /* This is the key idea that allows us to work around running into
     NotSerializableExceptions. */
  lazy val producer = createProducer()
  def send(topic: String, key: K, value: V): Future[RecordMetadata] =
    producer.send(new ProducerRecord[K, V](topic, key, value))
  def send(topic: String, value: V): Future[RecordMetadata] =
    producer.send(new ProducerRecord[K, V](topic, value))
}

object KafkaSink {
  import scala.collection.JavaConversions._
  def apply[K, V](config: Map[String, Object]): KafkaSink[K, V] = {
    val createProducerFunc = () => {
      val producer = new KafkaProducer[K, V](config)
      sys.addShutdownHook {
        // Ensure that, on executor JVM shutdown, the Kafka producer sends
        // any buffered messages to Kafka before shutting down.
        producer.close()
      }
      producer
    }
    new KafkaSink(createProducerFunc)
  }
  def apply[K, V](config: java.util.Properties): KafkaSink[K, V] = apply(config.toMap)
}
复制代码
2.之后我们利用广播变量的形式，将KafkaProducer广播到每一个executor，如下：

复制代码
// 广播KafkaSink
val kafkaProducer: Broadcast[KafkaSink[String, String]] = {
  val kafkaProducerConfig = {
    val p = new Properties()
    p.setProperty("bootstrap.servers", Conf.brokers)
    p.setProperty("key.serializer", classOf[StringSerializer].getName)
    p.setProperty("value.serializer", classOf[StringSerializer].getName)
    p
  }
  log.warn("kafka producer init done!")
  ssc.sparkContext.broadcast(KafkaSink[String, String](kafkaProducerConfig))
}
复制代码
这样我们就能在每个executor中愉快的将数据输入到kafka当中：

复制代码
//输出到kafka
segmentedStream.foreachRDD(rdd => {
  if (!rdd.isEmpty) {
    rdd.foreach(record => {
      kafkaProducer.value.send(Conf.outTopics, record._1.toString, record._2)
      // do something else
    })
  }
})
复制代码
Spark streaming+Kafka应用
一般Spark Streaming进行流式处理，首先利用上文我们阐述的Direct方式从Kafka拉取batch，之后经过分词、统计等相关处理，回写到DB上（一般为Hbase或者Mysql），
由此高效实时的完成每天大量数据的词频统计任务。