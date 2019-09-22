
>java.net.UnknownHostException: idc-nn
将hdfs-site.xml添加到src/main/resources

>No FileSystem for scheme: hdfs
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-hdfs</artifactId>
    <version>2.6.0</version>
</dependency>

>SIMPLE authentication is not enabled.  Available:[TOKEN, KERBEROS]
将core-site.xml添加到src/main/resources