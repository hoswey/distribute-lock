package com.github.dl.api;

import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuwei on 6/13/16.
 */
public interface DistributeLockOperation {

  void tryLockAndExecute(String lockId, PostLockCallBack postLockCallBack);

  void tryLockAndExecute(String lockId, long time, TimeUnit timeUnit,
      PostLockCallBack postLockCallBack);

}
