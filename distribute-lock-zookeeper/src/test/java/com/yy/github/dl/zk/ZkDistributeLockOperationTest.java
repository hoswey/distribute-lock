package com.yy.github.dl.zk;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

import com.yy.github.dl.api.PostLockCallBack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by hongshuwei on 6/13/16.
 */
public class ZkDistributeLockOperationTest extends StdOutTest {

  private ZkDistributeLockOperation zkDistributeLockOperation;

  @Before
  public void setup() throws Exception {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
        "app-context.xml");
    zkDistributeLockOperation = applicationContext.getBean(ZkDistributeLockOperation.class);
  }

  @Test
  public void testExecuteLokInSequence() throws Exception {

    ExecutorService executeService = Executors.newFixedThreadPool(2);

    GetLockTask task1 = spy(new GetLockTask("Task 1", 10000));
    GetLockTask task2 = spy(new GetLockTask("Task 2", 10000));

    executeService.submit(task1);
    // Let task1 tryLockAndExecute first
    Thread.sleep(500);
    executeService.submit(task2);

    executeService.shutdown();
    try {
      executeService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      System.out.println("Error waiting for ExecutorService shutdown");
    }
    // Write operation will hold the write lock 3000 milliseconds, so here we verify that when two
    // writer tryLockAndExecute concurrently, the second writer can only writes only when the first one is
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
  public void testExecuteLokTimeout() throws Exception {

    ExecutorService executeService = Executors.newFixedThreadPool(2);

    GetLockTask task1 = spy(new GetLockTask("Task 1", 1));
    GetLockTask task2 = spy(new GetLockTask("Task 2", 1));

    executeService.submit(task1);
    // Let task1 tryLockAndExecute first
    Thread.sleep(500);
    executeService.submit(task2);

    executeService.shutdown();
    try {
      executeService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      System.out.println("Error waiting for ExecutorService shutdown");
    }
    // Write operation will hold the write lock 3000 milliseconds, so here we verify that when two
    // writer tryLockAndExecute concurrently, the second writer will lock as it only try in 1 mill seconds while
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
          .tryLockAndExecute("lockForTest", timeoutMills, TimeUnit.MILLISECONDS,
              new PostLockCallBack() {

                public void onLockAcquired() {
                  try {
                    System.out.println(name + " begin");
                    Thread.sleep(3000);
                    System.out.println(name + " finish");
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }

                public void onLockTimeout() {
                  System.out.println(name + " timeout");
                }
              });
    }
  }
}