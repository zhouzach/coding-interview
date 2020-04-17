
https://stackoverflow.com/questions/47934934/difference-between-reduce-and-reducebykey-in-hadoop-spark

reduceByKey is a transformation and reduce is an action

Basically, reduce must pull the entire dataset down into a single location because it is reducing to one final value. reduceByKey on the other hand is one value for each key. And since this action can be run on each machine locally first then it can remain an RDD and have further transformations done on its dataset.

Note, however that there is a reduceByKeyLocally you can use to automatically pull down the Map to a single location also.


reduceByKey is for kv rdd