

https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/

Prior to 0.11.x, Apache Kafka supported at-least once delivery semantics and in-order delivery per partition.
As you can tell from the example above, that means producer retries can cause duplicate messages.
In the new exactly once semantics feature, we’ve strengthened Kafka’s software processing semantics in three different and interrelated ways.

1.An idempotent operation is one which can be performed many times without causing a different effect than only being performed once.
The producer send operation is now idempotent to set “enable.idempotence=true”.
Under the covers  each batch of messages sent to Kafka will contain a sequence number which the broker will use to dedupe any duplicate send


2.Kafka now supports atomic writes across multiple partitions through the new transactions API.
This allows a producer to send a batch of messages to multiple partitions such that either all messages
in the batch are eventually visible to any consumer or none are ever visible to consumers.
This feature also allows you to commit your consumer offsets in the same transaction along with the data you have processed,
(consumer commits the message offset to indicate that it has completed its processing. It will not receive it again, even if the consumer application fails and restarts)
thereby allowing end-to-end exactly once semantics. Here’s an example code snippet to demonstrate the use of the transactions API — 

producer.initTransactions();
try {
  producer.beginTransaction();
  producer.send(record1);
  producer.send(record2);
  producer.commitTransaction();
} catch(ProducerFencedException e) {
  producer.close();
} catch(KafkaException e) {
  producer.abortTransaction();
}
The code snippet above describes how you can use the new Producer APIs to send messages atomically to a set of topic partitions.

So on the Consumer side, you have two options for reading transactional messages, expressed through the “isolation.level” consumer config:

read_committed: In addition to reading messages that are not part of a transaction, also be able to read ones that are, after the transaction is committed.
read_uncommitted: Read all messages in offset order without waiting for transactions to be committed. This option is similar to the current semantics of a Kafka consumer.

To use transactions, you need to configure the Consumer to use the right “isolation.level”, use the new Producer APIs,
and set a producer config “transactional.id” to some unique ID. This unique ID is needed to provide continuity of transactional state across application restarts.


3.Building on idempotency and atomicity, exactly once stream processing is now possible through the Streams API in Apache Kafka.
All you need to make your Streams application employ exactly once semantics, is to set this config “processing.guarantee=exactly_once”.
This causes all of the processing to happen exactly once; this includes making both the processing and also
all of the materialized state created by the processing job that is written back to Kafka, exactly once.

“This is why the exactly once guarantees provided by Kafka’s Streams API are the strongest guarantees offered by any stream processing system so far.
It offers end-to-end exactly once guarantees for a stream processing application that extends from the data read from Kafka,
any state materialized to Kafka by the Streams app, to the final output written back to Kafka.
Stream processing systems that only rely on external data systems to materialize state support weaker guarantees for exactly once stream processing.
Even when they use Kafka as a source for stream processing and need to recover from a failure,
they can only rewind their Kafka offset to reconsume and reprocess messages, but cannot rollback the associated state in an external system,
leading to incorrect results when the state update is not idempotent.”

Let me explain that in a little more detail. The critical question for a stream processing system is “does my stream processing application get the right answer,
even if one of the instances crashes in the middle of processing?” The key, when recovering a failed instance, is to resume processing in exactly the same state as before the crash.

Now, stream processing is nothing but a read-process-write operation on a Kafka topic;
a consumer reads messages from a Kafka topic, some processing logic transforms those messages or modifies state maintained by the processor,
and a producer writes the resulting messages to another Kafka topic. Exactly once stream processing is simply the ability to execute a read-process-write operation exactly one time.
In this case, “getting the right answer” means not missing any input messages or producing any duplicate output. This is the behavior users expect from an exactly once stream processor.