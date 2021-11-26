> 参考来自: 蔡斌，陈湘萍. “Hadoop 技术内幕：深入解析Hadoop Common 和HDFS 架构设计与实现原理 (大数据技术丛书)。”
>
> 相关代码 GITHUB：https://github.com/wangbowen1024/Hadoop-Technology-Insider

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

**代理接口**

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

**调用句柄**

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



#### 1.3.3 Java NIO

#### 1.3.4 Hadoop IPC



### 1.4 Hadoop 文件系统



## 二、Hadoop 分布式文件系统（HDFS）

