
docs/cluster-overview.md

Because the driver schedules tasks on the cluster, it should be run close to the worker
nodes, preferably on the same local area network. If you'd like to send requests to the
cluster remotely, it's better to open an RPC to the driver and have it submit operations
from nearby than to run a driver far away from the worker nodes

--master yarn \
--deploy-mode cluster \