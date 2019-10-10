
SQL 错误 [500312] [HY000]: [Simba][ImpalaJDBCDriver](500312) Error in fetching data rows: Disk I/O error: Error seeking to 227456961 in file: hdfs://idc-nn/data/db/table/order_year=2017/order_month=10/order_day=19/data.parquet 
Error(255): Unknown error 255
Root cause: EOFException: Cannot seek after EOF

执行命令：
invalidate metadata <table>