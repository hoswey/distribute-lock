package com.github.dl.zk;

import com.yy.github.dl.api.DistributeLockOperation;
import com.yy.github.dl.api.LockAcquiredCallBack;
import com.yy.github.dl.api.LockCallBack;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;

/**
 * Created by hongshuwei on 6/13/16.
 */
public class ZkDistributeLockOperation implements DistributeLockOperation {

  private CuratorFramework curatorFramework;
  private String namespace;

  public ZkDistributeLockOperation(String namespace, CuratorFramework curatorFramework) {
    this.namespace = namespace;
    this.curatorFramework = curatorFramework;
  }

  public CuratorFramework getCuratorFramework() {
    return curatorFramework;
  }

  public void setCuratorFramework(CuratorFramework curatorFramework) {
    this.curatorFramework = curatorFramework;
  }

  public void execute(String lockId, LockAcquiredCallBack lockCallBack) {

    execute(lockId, 1, TimeUnit.MILLISECONDS, lockCallBack);
  }

  public void execute(String lockId, long time, TimeUnit timeUnit,
    LockCallBack postLockCallBack) {

    ZkReentrantLock zkReentrantLock = new ZkReentrantLock(curatorFramework, namespace, lockId);
    boolean isLocked = zkReentrantLock.tryLock(time, timeUnit);
    if (isLocked) {
      try {
        postLockCallBack.onLockAcquired();
      } finally {
        zkReentrantLock.unlock();
      }
    } else {
      postLockCallBack.onLockTimeout();
    }
  }
}
