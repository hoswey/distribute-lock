package com.github.dl.api;

import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuwei on 6/13/16.
 */
public interface DistributeLock {

  void lock();

  boolean tryLock();

  boolean tryLock(long timeout, TimeUnit timeUnit);

  void unlock();
}
