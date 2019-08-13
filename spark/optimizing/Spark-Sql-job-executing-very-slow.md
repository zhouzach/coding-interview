
https://confusedcoders.com/data-engineering/spark/spark-sql-job-executing-very-slow-performance-tuning

I have been facing trouble with a basic spark sql job which was unable to process 10’s of gigs in hours. Thats when I demystified the ‘spark.sql.shuffle.partitions’ which tends to slow down the job insanely.

Adding the below changes to the Spark Sql code fixes the issue for me. Magic.

// For handling large number of smaller files
events = sqlContext.createDataFrame(rows]).coalesce(400)
events.registerTempTable("input_events")
// For overriding default value of 200
sqlContext.sql("SET spark.sql.shuffle.partitions=10")
sqlContext.sql(sql_query)
