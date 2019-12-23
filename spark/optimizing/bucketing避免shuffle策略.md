
https://dzone.com/articles/tips-and-best-practices-to-take-advantage-of-spark

The Exchange in the physical plan means a shuffle occurred between stages.

Bucketing can improve performance in wide transformations and joins by avoiding "shuffles."
with wide transformation shuffles, data is sent across the network to other nodes and written to disk, 
causing network and disk I/O and making the shuffle a costly operation.


df.write.format("parquet")
.sortBy("id")
.partitionBy("src")
.bucketBy(4,"dst","carrier")
.option("path", "/user/mapr/data/flightsbkdc")
.saveAsTable("flightsbkdc")


CREATE TABLE boxes (width INT, length INT, height INT) USING CSV

CREATE TABLE rectangles
  USING PARQUET
  PARTITIONED BY (width)
  CLUSTERED BY (length) INTO 8 buckets
  AS SELECT * FROM boxes