## 主要功能
利用第三方组件(zk,redis,mongo)实现的分布式锁，分布式锁主要是为了解决多进程多jvm对于共享资源的并发访问

## 实现原理
1. zookeeper,[官方recipes](https://zookeeper.apache.org/doc/r3.3.6/recipes.html#sc_recipes_Locks) 
2. redis的cms操作，setnx
3. mongodb/mysql的原子操作

## 使用方法
### Zookeeper版本
1. Spting XML配置
```xml
<beans>
  <bean id="retryPolicy" class="org.apache.curator.retry.RetryOneTime">
    <constructor-arg name="sleepMsBetweenRetry" value="5000"/>
  </bean>

  <bean id="curatorFramework" class="org.apache.curator.framework.CuratorFrameworkFactory"
    init-method="start"
    destroy-method="close"
    factory-method="newClient">
    <constructor-arg name="connectString" value="${zk连接字符串}"/>
    <constructor-arg name="retryPolicy" ref="retryPolicy"/>
  </bean>

  <bean id="ZkDistributeLockOperation" class="com.github.dl.zk.ZkDistributeLockOperation">
    <constructor-arg name="curatorFramework" ref="curatorFramework"/>
    <constructor-arg name="namespace" value="sample-app"/>
  </bean>
</beans>
```
2. 使用方法
```java
public class LockTest { 
  
  public static void main(String[] args) {
    DistributeLockOperation zkDistributeLockOperation;
    zkDistributeLockOperation.execute("lock-id", new LockAcquiredCallBack(){
      public void onLockAcquired(){
        //Do something when the lock is acquired.
      }
    });
  }
}
```