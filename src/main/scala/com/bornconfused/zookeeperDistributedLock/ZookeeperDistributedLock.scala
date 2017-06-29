package com.bornconfused.zookeeperDistributedLock

import java.io.IOException
import java.util.Collections

import com.typesafe.scalalogging.LazyLogging
import org.apache.zookeeper.KeeperException.NoNodeException
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, _}

/**
  * Created by ranand on 6/29/2017 AD.
  */
class ZookeeperDistributedLock(zk: ZooKeeper,
                               lockBasePath: String,
                               lockName: String)
  extends LazyLogging {

  var lockPath = ""

  @throws(classOf[IOException])
  def lock: Boolean = {
    try {
      // lockPath will be formed from lockBasePath , lockName , sequence number ZooKeeper appends
      try {
        lockPath = createLockNode
      } catch {
        case e: NoNodeException =>
          logger.info(lockBasePath + " does not exist, creating")
          zk.create(lockBasePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
          lockPath = createLockNode
      }

      val lock = new Object

      lock.synchronized {
        logger.debug (s"IN synchronized 1 : $lockPath")
        val nodes = zk.getChildren(lockBasePath, new Watcher {
          override def process(event: WatchedEvent): Unit = {
            lock.synchronized {
              logger.debug (s"IN synchronized 2 : $lockPath")
              lock.notifyAll
            }
          }
        })

        Collections.sort(nodes) // ZooKeeper node names can be sorted lexographically
        Collections.reverse(nodes)
        logger.debug (s"Nodes  : $nodes")

        lockPath.endsWith(nodes.get(0))
      }

    } catch {

      case e: KeeperException =>
        throw new IOException(e)

      case e: InterruptedException =>
        throw new IOException(e)
    }
  }

  private def createLockNode = {
    zk.create(
      lockBasePath + "/" + lockName,
      null,
      Ids.OPEN_ACL_UNSAFE,
      CreateMode.EPHEMERAL_SEQUENTIAL
    )
  }

  @throws(classOf[IOException])
  def unlock: Unit = {
    try {

      zk.delete(lockPath, -1)
      lockPath = null;

    } catch {

      case e: KeeperException =>
        throw new IOException(e)

      case e: InterruptedException =>
        throw new IOException(e)
    }
  }
}
