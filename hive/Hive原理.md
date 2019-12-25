
https://zhuanlan.zhihu.com/p/91044262
https://zhuanlan.zhihu.com/p/75550159

1. 用户提交查询等任务给Driver。

2. 编译器获得该用户的任务Plan。

3. 编译器Compiler根据用户任务去MetaStore中获取需要的Hive的元数据信息。

4. 编译器Compiler得到元数据信息，对任务进行编译，先将HiveQL转换为抽象语法树，然后将抽象语法树转换成查询块，将查询块转化为逻辑的查询计划，
重写逻辑查询计划，将逻辑计划转化为物理的计划（MapReduce）, 最后选择最佳的策略。

5. 将最终的计划提交给Driver。

6. Driver将计划Plan转交给ExecutionEngine去执行，获取元数据信息，提交给JobTracker或者SourceManager执行该任务，任务会直接读取HDFS中文件进行相应的操作。

7. 获取执行的结果。

8. 取得并返回执行结果。
