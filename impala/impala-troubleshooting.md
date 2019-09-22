**Impala can't access all hive table**
"invalidate metadata" invalidates the entire catalog metadata. All table metadata will be reloaded on the next access.
"invalidate metadata <table>" invalidates the metadata, load on the next access
"refresh <table>" refreshes the metadata immediately. It is a faster, incremental refresh


**Hive was able to correctly query the impala table created while impala itself wan’t able to give the correct result.**
https://medium.com/@kartik.gupta_56068/hive-vs-impala-schema-loading-case-reading-parquet-files-acd0280c2cb3
原因：
When parquet files are involved
Hive matches both column names as well as the datatype when fetching the data and the ordering of columns doesn’t matter
Impala assumes that the columns in create table statement are present in the parquet file in the same order . 
Hence , at the time of scanning impala will only check if the datatype ordering is same in create table statement and parquet file. 
This also means that it doesn’t matter what the column name is so much so that it doesn’t matter if the column is even present in parquet file or not!
This two points can simply be summarized to say that Hive does pay attention to the schema present in parquet files while Impala simply imposes the create table schema on parquet file data.
So in case , one observes a different query output between Hive and impala and parquet files are involved , 
then its most likely that parquet file and create table statement contains different columns or the same columns in the different order .
So a best practice would be to simply keep the column names in the create table statement in the same order as present in the source parquet file . 
I assume that these observations should extend to other source file formats but i haven’t checked myself.
解决方法：
https://www.cloudera.com/documentation/enterprise/5-8-x/topics/impala_parquet_fallback_schema_resolution.html
hue中直接set PARQUET_FALLBACK_SCHEMA_RESOLUTION=name; 或者
impala shell中：set PARQUET_FALLBACK_SCHEMA_RESOLUTION=1;

