> 本文相关代码 GITHUB：https://github.com/wangbowen1024/Hadoop-Technology-Insider
>
> 参考来自: 蔡斌，陈湘萍. “Hadoop 技术内幕：深入解析Hadoop Common 和HDFS 架构设计与实现原理 (大数据技术丛书)。”
>
> 参考书源代码下载链接：http://www.hzcourse.com/oep/resource/access/L29wZW5yZXNvdXJjZXMvdGVhY2hfcmVzb3VyY2UvZmlsZS8yMDE3LzEwL2YwYTg3YWNlNjY4OWYzNGI0NGMxYzM0ZmUxZjMyNTQxLnRhciTjgIpIYWRvb3DmioDmnK_lhoXluZXjgIso6JSh5paMK-mZiOa5mOiQjSnmupDku6PnoIExMjEyMDcudGFy

# Hadoop - HDFS

## 一、Common 的实现

### 1.1 Hadoop 配置信息处理

**配置简介**

配置文件格式：XML

```xml
＜?xml version=“1.0”?＞
＜?xml-stylesheet type="text/xsl"href="configuration.xsl"?＞
<configuration>
	<property>
    <name>名字</name>
    <value>值</value>
    <description>描述</description>
    <final>true</final>
  </property>
  ......
</configuration>
```

> final一般不出现，但在合并资源的时候，可以防止配置项的值被覆盖：
>
> 比如两个配置文件具有相同的键值对，如果合并的话，后者会覆盖前者，如果具有 final ，则不会被覆盖。

配置类：`org.apache.hadoop.conf.Configuration`

配置类使用过程：

1. 构造 Configuration 对象
2. addResource() 方法添加需要加载的资源
3. get\*/set\* 方法访问/设置配置项（资源会在第一次使用的时候加载到配置项，懒加载）

**资源加载**

**访问/设置配置项**

**Configurable 接口**



### 1.2 序列化与压缩

#### 1.2.1 序列化

#### 1.2.2 压缩



### 1.3 Hadoop 远程过程调用

#### 1.3.1 远程过程调用基础知识



#### 1.3.2 Java 动态代理

##### **代理接口**

java 中有一个 `Proxy` 类，其有一个 `newProxyInstance()` 方法，通过反射机制创建代理接口实例，分解步骤：

```java
/**
 * newProxyInstance 方法具有3个参数：类加载器、接口类数组、调用句柄，
 */

// 1. 通过类加载器、和接口类数组，查找或生成指定的代理类。这里的代理类类型为 $Proxy1、$Proxy2 这样的
Class<?> cl = getProxyClass0(loader, intfs);
// 2. 获取构造方法
final Constructor<?> cons = cl.getConstructor(constructorParams);
// 3. 通过反射创建代理类实例，这里的h就是调用句柄
cons.newInstance(new Object[]{h});
```

##### **调用句柄**

代理接口中前两个参数没有什么好讲的，只是为了创建出代理类的 Class 对象。最后一个参数 `InvocationHandler` 是调用句柄，是一个接口，该接口只有一个 `invoke()` 方法。调用句柄的作用是**当调用代理对象实例的方法时，会转发给该句柄实例进而调用 invoke 方法**。通常调用句柄实例包含被代理对象实例：

```java
public class DPInvocationHandler implements InvocationHandler {

    /**
     * 被代理的对象实例
     */
    private DPQueryStatusImpl dpsi;

    /**
     * 构造函数
     */
    public DPInvocationHandler(DPQueryStatusImpl dpsi) {
        this.dpsi = dpsi;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 增强操作
        System.out.println("开始判断");

        // 调用转发。利用了反射机制，相当于：dpsi.method(args)
        //
        Object result = method.invoke(dpsi, args);

        // 增强操作
        System.out.println("判断结束");
        return result;
    }
}
```

就像上述代码呈现的，当代理对象调用代理方法时，会被转发到该调用句柄实例并调用 invoke 方法。可以发现，invoke 方法具有3个参数：

* 代理对象
* 调用的方法（通过反射机制进行调用）
* 方法参数

同时这里可以对原有方法进行增强功能（即在调用前后添加其他操作，可以是打印日志、也可以进行数据库操作等）

**代码实例**

代理实操。见 GITHUB：`package org.bigdata.hadoop.common.proxy;`



#### 1.3.3 Java NIO

传统的 `ServerSocket` 的 `accept()` 方法是阻塞的，每当有一个客户端来连接就需要一个线程来处理，虽然可以用线程池技术，但是这依然会有数量上的限制。因此我们需要NIO这一个异步的通信，NIO有三个主要的部分：Buffer（缓冲区）、Channel（通道）、Selector（选择器）。

##### **Buffer 缓冲区**

Buffer 实际上就是一个数组，用于存储数据。然而数据读写都使用同一个 Buffer ，唯一变化的就是几个关键索引字段，因此这几个索引字段需要重点理解：

* mark：设置缓冲区位置标记，通过 reset() 方法可以将 position 重置为设置位置
* position：下一个可以读写的索引
* limit：第一个不能读写的索引
* capacity：缓冲区的数量（容量 ）

一些基本的方法：

* ByteBuffer.allocate() / ByteBuffer.wrap()：创建 Buffer
* get()：读
* put()：写

一些对缓冲区下标的变换操作方法，下图就是索引的变换表示：

* flip()：可以对前面的读写操作进行切换
* rewind()：重新读
* clear()：清空数据
* compact()：主要用于在缓冲区中还有未写出的数据时，为读入数据准备空间

![image-20211129204625339](https://gitee.com/wangbowen97/pic-go/raw/master/image-20211129204625339.png)



> 几个操作的注意点：
>
> * **不管如何操作，都只是更新对应的索引下标而已。数组内容数据是不会变化的。**
> * **0 ≤ mark ≤ position ≤ limit ≤ capacity**

##### **Channel 通道**

通道和 TPC 的 socket 套接字有点像，channel 实例代表了一个和设备的连接，可以通过它进行读写操作。接下来简要介绍一下一些特点：

* 对象的创建

  ```java
  // 通过工厂方法创建
  public static SocketChannel open();
  public static SocketChannel open(SocketAddressremote);
  
  public static ServerSockerChannel open();
  ```

  创建完 channel 之后，可以用 `connect()` 连接到远程机器，以及使用 `close()`  进行关闭。

* 数据的读写

  ```java
  // 最基础的都使用 ByteBuffer 进行数据传输
  public int read(ByteBuffer dst);
  public long read(ByteBuffer[] dsts, int offset, int length);
  
  public final long read(ByteBuffer src);
  public final long read(ByteBuffer[] srcs, int offset, int length);
  ```

  同时 ServerSocketChannel 也提供了，`accept()` 、`close()` 方法。其中 accept 方法，返回的是 SocketChannel。同时 ServerSocketChannel 不提供 `bind()` 方法，而是由底层的 Socket 提供：`ServerSocketChannel.socket.bind()`。

* 是否非阻塞状态工作

  ```java
  // 一般情况下，需要把 Channel 设置成非阻塞状态
  public SelectableChannel configureBlocking(boolean block);
  public boolean isBlocking();
  
  // 和普通的连接有一点不一样，非阻塞会立即返回，需要自己判断
  isConnected();
  // 或者等待连接成功：
  finishConnect();
  
  // 同时 read 如果没有数据的时候会立即返回（返回值 0）
  // accept 也不会等待，如果没有连接会返回 null
  ```

* 和选择器配合

##### **Selector 选择器**

通过工厂静态方法创建 Selector 实例，然后通过 Channel 注册方法，将 Selector 实例注册到想要监控的 Channel 上，最后调用 `select() `方法。该方法会阻塞等待，直到有一个准备好的I/O操作或超时。select() 方法将会返回可以进行I/O操作的通道数量。**现在在一个单独的县城中，就可以检查多个通道是否可以进行I/O操作，不需要为每一个通道都准备一个线程了。**

* 打开和关闭 Selector

  ```java
  public static Selector open();
  public boolean isOpen();
  public abstract void close();
  ```

* Channel 和 Selector 的联系

  Channel 和 Selector 关联后，关联的信息会保存在 `SelectorKey` 实例中。该选择器注册标记 SelectorKey 维护了一个通道上感兴趣的事件类型信息：（因为使用位图，所以以下事件可以用 `|` 同时设置）

  * **OP_READ**：通道上有数据可读
  * **OP_WRITE**：通道已经可以写
  * **OP_CONNECT**：通道连接已建立
  * **OP_ACCEPT**：通道上有连接请求

  相关的关联方法：

  ```java
  public SelectorKey register(Selector sel, int ops);
  // 带附件注册
  public SelectorKey register(Selector sel, int ops, Object att);
  // 根据选择器，查找对应的 SelectorKey
  public SelectorKey keyFor(Selector sel);
  // 判断通道时都已经注册
  public boolean isRegister();
  ```

* SelectorKey 的操作

  SelectorKey 关联的 Selector 和 Channel 可以分别调用 selector() 和 channel() 方法获得。并且还可以通过 cancel() 取消注册：

  ```java
  Selector selector();
  SelectbleChannel channel();
  void cancel();
  ```

  一般来说，选择器上会注册多个通道，这些通道通过关联的 SelectorKey 指定它们感兴趣的I/O操作集，接下来就是等通道的I/事件了，这一般通过 Selector 完成。

  SelectorKey 还有一个非常有用的功能：附件

  ```java
  public Object attach(Object ob);
  public Object attachment();
  ```

* Selector 的操作

  选择器提供3种方法，其返回值都是整数，表明已注册通道上发生了感兴趣I/O事件的个数：

  ```java
  // 阻塞等待，直到有一个通道有感兴趣的事件
  public int select();
  // 具有超时的等待
  public int select(long timeout);
  // 非阻塞版本
  public int selectNow();
  ```

  如果上述方法返回值大于0，说明有事件要处理：

  ```java
  // 获取 Selector 上已注册的所有键
  public Set<SelectorKey> keys();
  // 已选键集
  public Set<SelectorKey> selectedKeys();
  ```

  SelectorKey 上有如下判断通道上等待的操作方法：

  ```java
  public int readyOps()
  public boolean isReadable()
  public boolean isWritable()
  public boolean isConnectable()
  public boolean isAcceptable()
  ```

  两次调用 select() 之间，必须要手工对其清空（下面代码中的 remove）：

  ```java
  Iterator＜SelectionKey＞iter=selector.selectedKeys().iterator()；
  while(iter.hasNext()){
    key=iter.next()；
    iter.remove()；
    ……
    if(key.isAcceptable())doAccept(key)；
    else if(key.isReadable())doRead(key)；
    ……
  }
  ```

**代码实例**

NIO实例：回显服务器。见 GITHUB：`package org.bigdata.hadoop.common.nio;`



**总结**

* 首先NIO是为了解决，可以在一个线程中实现多个连接的处理（不然一个连接就要一个线程，数量多了也顶不住）。

* 可以把 Selector 看作是一个容器，里面放着各种端到端的通道，然后定期扫描准备好的通道进行处理，通道中的数据传输只能是 Buffer 。
* 在编写代码的时候，一定一定要注意 Buffer 的那几个索引下标。尤其是读写的时候，要配合 flip()、clear() 等方法。



#### 1.3.4 Hadoop IPC



### 1.4 Hadoop 文件系统



## 二、Hadoop 分布式文件系统（HDFS）



## 三、DataNode



## 四、NameNode



## 五、HDFS 客户端
