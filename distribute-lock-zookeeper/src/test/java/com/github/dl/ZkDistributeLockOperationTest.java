package com.github.dl;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

import com.github.dl.zk.ZkDistributeLockOperation;
import com.yy.github.dl.api.LockAcquiredCallBack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

/**
 * Created by hongshuwei on 6/13/16.
 */
public class ZkDistributeLockOperationTest extends StdOutTest {

  private ZkDistributeLockOperation zkDistributeLockOperation;

  private TestingServer testingClient;

  private CuratorFramework curatorFramework;

  @Before
  public void setup() throws Exception {

    super.setUp();
    testingClient = new TestingServer(4711);

    curatorFramework =  CuratorFrameworkFactory.newClient("127.0.0.1:4711",
      new RetryOneTime(500));
    curatorFramework.start();
    curatorFramework.blockUntilConnected();
    zkDistributeLockOperation = new ZkDistributeLockOperation("test-ns", curatorFramework);
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
    testingClient.close();
  }

  @Test
  public void testExecuteLokInSequence() throws Exception {

    ExecutorService executeService = Executors.newFixedThreadPool(2);

    GetLockTask task1 = spy(new GetLockTask("Task 1", 10000));
    GetLockTask task2 = spy(new GetLockTask("Task 2", 10000));

    executeService.submit(task1);
    // Let task1 execute first
    Thread.sleep(500);
    executeService.submit(task2);

    executeService.shutdown();
    try {
      executeService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      System.out.println("Error waiting for ExecutorService shutdown");
    }
    // Write operation will hold the write lock 3000 milliseconds, so here we verify that when two
    // writer execute concurrently, the second writer can only writes only when the first one is
    // finished.
    final InOrder inOrder = inOrder(getStdOutMock());
    inOrder.verify(getStdOutMock())
      .println("Task 1 begin");
    inOrder.verify(getStdOutMock())
      .println("Task 1 finish");
    inOrder.verify(getStdOutMock())
      .println("Task 2 begin");
    inOrder.verify(getStdOutMock())
      .println("Task 2 finish");

  }

  @Test
  public void testExecuteLockTimeout() throws Exception {

    ExecutorService executeService = Executors.newFixedThreadPool(2);

    GetLockTask task1 = spy(new GetLockTask("Task 1", 1));
    GetLockTask task2 = spy(new GetLockTask("Task 2", 1));

    executeService.submit(task1);
    // Let task1 execute first
    Thread.sleep(500);
    executeService.submit(task2);

    executeService.shutdown();
    try {
      executeService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      System.out.println("Error waiting for ExecutorService shutdown");
    }
    // Write operation will hold the write lock 3000 milliseconds, so here we verify that when two
    // writer execute concurrently, the second writer will lock as it only try in 1 mill seconds while
    // the first hold the lock 3 seconds
    final InOrder inOrder = inOrder(getStdOutMock());
    inOrder.verify(getStdOutMock())
      .println("Task 1 begin");
    inOrder.verify(getStdOutMock())
      .println("Task 2 timeout");
    inOrder.verify(getStdOutMock())
      .println("Task 1 finish");
  }

  private class GetLockTask implements Runnable {

    private String name;

    private int timeoutMills;

    public GetLockTask(String name, int timeoutMills) {
      this.name = name;
      this.timeoutMills = timeoutMills;
    }

    public void run() {

      zkDistributeLockOperation
        .execute("lockForTest", timeoutMills, TimeUnit.MILLISECONDS,
          new LockAcquiredCallBack() {

            public void onLockAcquired() {
              try {
                System.out.println(name + " begin");
                Thread.sleep(1000);
                System.out.println(name + " finish");
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          });
    }
  }
}