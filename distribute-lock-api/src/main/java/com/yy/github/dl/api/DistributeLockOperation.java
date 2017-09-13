package com.yy.github.dl.api;

import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuwei on 6/13/16.
 */
public interface DistributeLockOperation {

  /**
   * 阻塞等待锁
   *
   * @param lockId
   * @param lockCallBack
   */
  void execute(String lockId, LockAcquiredCallBack lockCallBack);

  /**
   *
   * 指定超时的锁
   *
   * @param lockId
   * @param time
   * @param timeUnit
   * @param postLockCallBack
   * @return
   */
   void execute(String lockId, long time, TimeUnit timeUnit,
      LockCallBack postLockCallBack);

}
