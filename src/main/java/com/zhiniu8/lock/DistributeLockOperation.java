package com.zhiniu8.lock;

import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuwei on 6/13/16.
 */
public interface DistributeLockOperation {

    void executeExclusive(String lockId, PostLockCallBack postLockCallBack);

    void execute(String lockId, long time, TimeUnit timeUnit, PostLockCallBack postLockCallBack);

}
