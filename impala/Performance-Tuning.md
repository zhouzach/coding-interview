
**Performance Considerations for Join Queries**

https://www.cloudera.com/documentation/enterprise/5-3-x/topics/impala_perf_joins.html
The Impala query planner chooses between different techniques for performing join queries, 
depending on the absolute and relative sizes of the tables. Broadcast joins are the default, 
where the right-hand table is considered to be smaller than the left-hand table, 
and its contents are sent to all the other nodes involved in the query. 
The alternative technique is known as a partitioned join (not related to a partitioned table), 
which is more suitable for large tables of roughly equal size. With this technique, 
portions of each table are sent to appropriate other nodes where those subsets of rows can be processed in parallel. 
The choice of broadcast or partitioned join also depends on statistics being available for all tables in the join, 
gathered by the COMPUTE STATS statement.