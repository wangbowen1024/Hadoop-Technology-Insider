package org.bigdata.hadoop.common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 调用句柄实例，实现 InvocationHandler 接口，该接口只有一个方法，当调用代理接口对象的方法时，会被转发到该调用句柄实例的 invoke() 方法
 */
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

        // 利用了反射机制，相当于：dpsi.method(args)
        Object result = method.invoke(dpsi, args);

        // 增强操作
        System.out.println("判断结束");
        return result;
    }
}
