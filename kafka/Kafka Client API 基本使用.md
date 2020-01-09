
https://www.jianshu.com/p/26adba1bd658

之前讲过了[Kafka基本概念及原理][1]，这次我们来看看Kafka Client的API。要使用Kafka Client的API，首先需要先部署Kafka集群，部署过程请参见[官网][2]。然后在项目中添加Kafka Client的依赖，在这里我们使用0.10.0.1版本：

<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>0.10.0.1</version>
</dependency>

Kafka有以下四个核心API：

Producer
Consumer
Streams
Connect
这里我们来讲解比较基础的Producer和Consumer。假设读者已经按照[官网][2]在本机配置好了Kafka服务，并创建了名为“test”的topic。

Producer API
Producer用来向Kafka集群中发布消息记录的Kafka客户端。Producer是线程安全的，并且通常来讲，在多个线程间共享一个producer要比每个线程都创建一个producer速度更快。Producer的API相对比较简单，下面给出一个较为简单的API实例：

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerDemo {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
                props.put("acks", "all");
                props.put("retries", 0);
                props.put("batch.size", 16384);
                props.put("linger.ms", 1);
                props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("test", Integer.toString(i), Integer.toString(i)));

        producer.close();

    }
}
Producer由一个持有未发送消息记录的资源池和一个用来向Kafka集群发送消息记录的后台IO线程组成。使用后未关闭producer将导致这些资源泄露。

send方法是异步的。当它被调用时，它会将消息记录添加到待发送缓冲区并立即返回。使用这种方式可以使生产者聚集一批消息记录后一起发送，从而提高效率。

ack 配置项用来控制producer要求leader确认多少消息后返回调用成功。当值为0时producer不需要等待任何确认消息。当值为1时只需要等待leader确认。当值为-1或all时需要全部ISR集合返回确认才可以返回成功。

当 retries > 0 时，如果发送失败，会自动尝试重新发送数据。发送次数为retries设置的值。

buffer.memory、batch.size、linger.ms三个参数用来控制缓冲区大小和延迟发送时间，具体含义可以参考官方文档的配置。

bootstrap.servers 配置项处需要填写我们要发送到的Kafka集群地址。

key.serializer 和 value.serializer 指定使用什么序列化方式将用户提供的key和value进行序列化。
运行此程序，在$KAFKA_HOME目录下运行：

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
可以看到写入Kafka的消息。

Consumer API
Consumer的API分为High-level API和Low-level API。前者提供了高度抽象的API，使用起来简单、方便。因此本文将主要讲述High-level API。Low-level API提供了更强的控制能力，但使用起来较为繁琐。下面我们来看一种最简单的方式，自动确认offset：

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class AutoCommitConsumerDemo {

    public static void main(String[] args) {
         Properties props = new Properties();
         props.put("bootstrap.servers", "localhost:9092");
         props.put("group.id", "test");
         props.put("enable.auto.commit", "true");
         props.put("auto.commit.interval.ms", "1000");
         props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         @SuppressWarnings("resource")
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
         consumer.subscribe(Arrays.asList("test"));
         while (true) {
             ConsumerRecords<String, String> records = consumer.poll(100);
             for (ConsumerRecord<String, String> record : records)
                 System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
         }
    }
}
bootstrap.servers配置项指定了consumer需要连接的服务器集群。多台服务器用“,”分隔。这个配置项就算只配置了一台server的地址，也可以通过这一台server发现集群中的其他服务器。但是为了避免这台server挂掉引发单点问题，所以把所有服务器地址列举上去是一个比较好的选择。

enable.auto.commit配置项指定了提交offset的方式为自动提交，auto.commit.interval.ms配置项配置了每次自动提交的时间间隔。

group.id即消费者组标签，本例中消费者组的名称为test。不了解消费者组的概念可以看我的这篇文章：[Kafka基本概念及原理][1]。

自动提交offset的方式非常简单，但多数情况下，我们不会使用自动提交的方式。因为不论从Kafka集群中拉取的数据是否被处理成功，offset都会被更新，也就是如果处理过程中出现错误可能会出现数据丢失的情况。所以多数情况下我们会选择手动提交方式：

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ManualCommitConsumerDemo {

    public static void main(String[] args) {
         Properties props = new Properties();
         props.put("bootstrap.servers", "localhost:9092");
         props.put("group.id", "test");
         props.put("enable.auto.commit", "false");
         props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         @SuppressWarnings("resource")
         KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
         consumer.subscribe(Arrays.asList("foo", "bar"));
         final int minBatchSize = 200;
         List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
         while (true) {
             ConsumerRecords<String, String> records = consumer.poll(100);
             for (ConsumerRecord<String, String> record : records) {
                 buffer.add(record);
             }
             if (buffer.size() >= minBatchSize) {
                 insertIntoDb(buffer);
                 consumer.commitSync();
                 buffer.clear();
             }
         }

    }
    
    private static void insertIntoDb(List<ConsumerRecord<String, String>> buffer) {
        // Insert into db
    }

}
从对比自动提交offset的代码，我们看到 enable.auto.commit 配置项被设置为false，代表手动提交。代码中定义了一个ConsumerRecord的列表作为缓冲，当缓冲中的数据大于200条时，才一次性插入数据库中，并手动提交offset。这样，只有当数据成功插入数据库时才会更新offset，从而保证了数据不丢失。但如果在数据插入数据库后和手动提交offset之间这段时间（虽然很短，但也是有可能的）程序崩溃或服务器down机，那么再次启动会导致重复消费。所以这种方式其实是提供了 at least once 语义。

另外，consumer并不是线程安全的，所以在进行多线程操作时需要在每个线程实例化一个consumer。如果需要跨线程使用consumer，需要进行手动同步。
[1]:http://www.jianshu.com/p/97011dab6c56
[2]:http://kafka.apache.org/quickstart

