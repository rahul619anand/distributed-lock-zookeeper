package com.bornconfused.zookeeperDistributedLock.lock

import java.io.IOException
import java.util.Collections

import com.typesafe.scalalogging.LazyLogging
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, _}

/**
  * Created by ranand on 6/29/2017 AD.
  */

/**
  *
  * @param zk zookeeper instance
  * @param rootNodePath base path
  * @param lockNode lock node to be appended to path
  */
class ZookeeperDistributedLock(zk: ZooKeeper,
                               rootNodePath: String,
                               lockNode: String)
    extends LazyLogging {

  var absoluteLockNodePath : String = ""

  /**
    * Method for acquiring a lock
    *
    * @throws java.io.IOException
    * @return boolean representing if lock is acquired or not
    */
  @throws(classOf[IOException])
  def acquire: Boolean = {
    try {
      if(zk.exists(rootNodePath,true) == null) {
        createNode(rootNodePath, null, CreateMode.PERSISTENT)
      }
      absoluteLockNodePath = createNode(rootNodePath + "/" + lockNode,
                            null,
                            CreateMode.EPHEMERAL_SEQUENTIAL)

      val lock = new Object

      lock.synchronized {
        logger.debug(s"synchronized block 1 : $absoluteLockNodePath")
        val rootNodeChildList = zk.getChildren(
          rootNodePath,
          new Watcher {
            override def process(event: WatchedEvent): Unit = {
              lock.synchronized {
                logger.debug(s"synchronized block 2 : $absoluteLockNodePath")
                lock.notifyAll
              }
            }
          }
        )

        Collections.sort(rootNodeChildList)
        Collections.reverse(rootNodeChildList)

        logger.debug(s" Children Nodes of $rootNodePath  : $rootNodeChildList")
        absoluteLockNodePath.endsWith(rootNodeChildList.get(0))
      }

    } catch {

      case e: KeeperException =>
        throw new IOException(e)

      case e: InterruptedException =>
        throw new IOException(e)
    }
  }

  /**
    * Method for releasing lock
    *
    * @throws java.io.IOException
    */
  @throws(classOf[IOException])
  def release: Unit = {
    try {

      zk.delete(absoluteLockNodePath, -1)
      absoluteLockNodePath = null ;

    } catch {

      case e: KeeperException =>
        throw new IOException(e)

      case e: InterruptedException =>
        throw new IOException(e)
    }
  }

  /**
    *
    * @param nodePath for creation
    * @param data info to be stored at the node path
    * @param createMode mode of creation (i.e. PERSISTENT vs EPHEMERAL)
    * @return
    */
  private def createNode(nodePath: String,
                         data: Array[Byte],
                         createMode: CreateMode) = {
    zk.create(
      nodePath,
      data,
      Ids.OPEN_ACL_UNSAFE,
      createMode
    )
  }
}
