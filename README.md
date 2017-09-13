## 主要功能
利用第三方组件(zk,redis,mongo)实现的分布式锁，分布式锁主要是为了解决多进程多jvm对于共享资源的并发访问

## 实现原理
1. zookeeper sequence recipes note recipes
2. redis的cms操作，setnx
3. mongodb/mysql的原子操作
