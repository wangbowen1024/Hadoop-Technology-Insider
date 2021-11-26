package org.bigdata.hadoop.common.proxy;

import java.lang.reflect.Proxy;

/**
 * JAVA 动态代理（本例只为了学习动态代理，该类主要是说明步骤以便记录，以及测试入口）
 *
 * 步骤：
 *   1.要想进行代理，就必须要有一个接口，因此创建一个文件接口 DPQueryStatus，提供一个判断文件是否存在的方法 exist()
 *   2.实现上诉接口，提供具体查询的方法的实现类 DPQueryStatusImpl
 *   3.创建该实现类的调用句柄实例（实现 InvocationHandler 接口的对象），用于转发调用的方法给句柄实例
 *   4.创建代理接口（接受接口实现类实例对象，返回的是该实例对象的接口），这里通过 DPMain.create() 方法来创建
 *   5.调用方法
 */
public class DPMain {
    public static void main(String[] args) {
        // 创建代理
        DPQueryStatus dpQueryStatus = create(new DPQueryStatusImpl());
        // 调用方法
        boolean exist = dpQueryStatus.exist("abc.txt");
        System.out.println("Exist: " + exist);
    }

    /**
     * 创建代理接口实例
     * @param dpsi  被代理的对象实例
     * @return      代理接口
     */
    public static DPQueryStatus create(DPQueryStatusImpl dpsi) {
         Object proxyInstance = Proxy.newProxyInstance(dpsi.getClass().getClassLoader(),
                                                       new Class[]{DPQueryStatus.class},
                                                       new DPInvocationHandler(dpsi));
         return (DPQueryStatus) proxyInstance;

    }
}
