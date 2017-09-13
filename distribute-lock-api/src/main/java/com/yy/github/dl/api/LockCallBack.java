package com.yy.github.dl.api;

/**
 * Created by hongshuwei on 6/13/16.
 */
public interface LockCallBack {

  /**
   * callback when the lock is acquired
   */
  void onLockAcquired();

  /**
   * call back when it unable to get the lock in the provided timeout, pls note that this is cause
   * as another thread own this thread, not the timeout to connect to the zk
   */
  void onLockTimeout();

}
