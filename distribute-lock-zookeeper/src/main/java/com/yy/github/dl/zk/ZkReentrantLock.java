package com.yy.github.dl.zk;

import com.yy.github.dl.api.DistributeLock;
import com.yy.github.dl.api.LockException;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

/**
 * Created by hongshuwei on 6/13/16.
 */
public class ZkReentrantLock implements DistributeLock {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  private String namespace;

  private String lockId;

  private InterProcessMutex mutex;

  public ZkReentrantLock(CuratorFramework curator, String namespace, String lockId) {
    this.namespace = namespace;
    this.lockId = lockId;
    mutex = new InterProcessMutex(curator, "/distribute-lock/" + namespace + "/" + lockId);
  }

  public void lock() {
    try {
      mutex.acquire();
    } catch (Exception e) {
      log.error("", e);
      throw new LockException("Fail to create lock in zookeeper", e);
    }
  }

  public boolean tryLock() {
    return tryLock(1, TimeUnit.MILLISECONDS);
  }

  public boolean tryLock(long time, TimeUnit timeUnit) {

    try {
      return mutex.acquire(time, timeUnit);
    } catch (Exception e) {
      log.error("", e);
      throw new LockException("Fail to create lock in zookeeper", e);
    }
  }

  public void unlock() {
    try {
      mutex.release();
    } catch (Exception e) {
      throw new LockException("Fail to unlock in zookeeper");
    }
  }
}
