# Distributed Lock With Zookeeper

### A lock implementation for distributed syncronized cron jobs.

There are many use cases of cron jobs running in distributed environment. 
Since redundancy is required on production, certain co-ordination is needed to guarantee multiple instances don't fire at once.

ZooKeeper is a distributed, open-source coordination service for distributed applications
As Zookeeper data model is styled after the familiar directory tree structure of file systems they are most suited for this.  
I provide here a lock implementation (i.e. ZookeeperDistributedLock), using zookeeper proposed guidelines 

### Steps 
 
 Acquire Lock
 
     1. Call create( ) with a pathname of "_locknode_/lock-" and the sequence and ephemeral flags set.
     2. Call getChildren( ) on the lock node without setting the watch flag (this is important to avoid the herd effect).
     3. If the pathname created in step 1 has the lowest sequence number suffix, the client has the lock and the client exits the protocol.
     4. The client calls exists( ) with the watch flag set on the path in the lock directory with the next lowest sequence number.
     5. if exists( ) returns false, go to step 2. Otherwise, wait for a notification for the pathname from the previous step before going to step 2.
     
 Release Lock
     
     1. The unlock protocol is very simple: clients wishing to release a lock simply delete the node they created in step 1.
  
This project uses Quartz (scheduler framework) to fire 2 jobs at same time trying to acquire the lock.
Since both Jobs create a new instance of the ZookeeperDistributedLock, we can consider this setup to be emulating 2 distributed cron instances.
 
### Test 

 
Since I already use Kafka (which uses Zookeeper for discovery) , I just spawn a container to test the same . 
 
    docker pull spotify/kafka
    docker run -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=localhost --env ADVERTISED_PORT=9092 --name kafka_local spotify/kafka

You can also pull zookeeper image to test the same.
     
    docker pull zookeeper 
 

Run the below commands for starting the app.

Build the executable 

    gradle fatJar
    
Run
    
    java -jar <jar-name>