# Distributed Lock With Zookeeper

### A lock implementation for distributed cron jobs with Dedupe co-ordination management

There are many use cases of Cron Jobs running in Distributed Environment.

Since they need to have redundancy, certain co-ordination is needed so that they both dont fire at any given time.

Hence Zookeeper comes to the rescue.

Zookeeper have nodes which can be used to have a locking mechanism for the same.
 
This project uses quartz to fire 2 jobs at the same time, which tries to acquire the lock.
 
Since they both create a new instance of the ZookeeperDistributedLock, we can consider this setup to be emulating 2 distributed nodes. 