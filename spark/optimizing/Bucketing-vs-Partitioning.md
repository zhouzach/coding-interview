
https://dzone.com/articles/tips-and-best-practices-to-take-advantage-of-spark
Partitioning should only be used with columns that have a limited number of values; 
bucketing works well when the number of unique values is large. 
Columns which are used often in queries and provide high selectivity are good choices for bucketing.