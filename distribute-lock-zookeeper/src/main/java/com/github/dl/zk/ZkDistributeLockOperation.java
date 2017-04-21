package com.github.dl.zk;

import com.github.dl.api.DistributeLockOperation;
import com.github.dl.api.PostLockCallBack;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;

/**
 * Created by hongshuwei on 6/13/16.
 */
public class ZkDistributeLockOperation implements DistributeLockOperation {

    private String namespace;

    public CuratorFramework curatorFramework;

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }

    public void setCuratorFramework(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    public ZkDistributeLockOperation(String namespace, CuratorFramework curatorFramework) {
        this.namespace = namespace;
        this.curatorFramework = curatorFramework;
    }

    public void tryLockAndExecute(String lockId, PostLockCallBack postLockCallBack) {

        tryLockAndExecute(lockId, 1, TimeUnit.MILLISECONDS, postLockCallBack);
    }

    public void tryLockAndExecute(String lockId, long time, TimeUnit timeUnit, PostLockCallBack postLockCallBack) {

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
