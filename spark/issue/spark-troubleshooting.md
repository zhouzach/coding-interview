
**Container killed by YARN for exceeding memory limits. 9.0 GB of 9 GB physical memory used.
Consider boosting spark.yarn.executor.memoryOverhead.**

--conf spark.yarn.executor.memoryOverhead=1024


**Aggregated Metrics by Executor：CANNOT FIND ADDRESS
ERROR executor.CoarseGrainedExecutorBackend: RECEIVED SIGNAL TERM
不是真正出错，是由于打开spark.dynamicAllocation.enabled的原因**

https://www.zybuluo.com/xtccc/note/254078
https://gite.lirmm.fr/yagoubi/spark/commit/5fccb567b37a94445512c7ec20b830b5e062089f
要避免出现ERROR executor.CoarseGrainedExecutorBackend: RECEIVED SIGNAL TERM，
可以关闭spark.dynamicAllocation.enabled

**ERROR client.TransportResponseHandler: Still have 1 requests outstanding when connection**
**java.util.concurrent.RejectedExecutionException: Task scala.concurrent.impl.CallbackRunnable@746ad0a1 
rejected from java.util.concurrent.ThreadPoolExecutor@1756e824[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 15203]**
https://www.cnblogs.com/hithink/p/9858206.html
.config("spark.speculation", "true")
.config("spark.storage.blockManagerTimeoutIntervalMs", 100000)