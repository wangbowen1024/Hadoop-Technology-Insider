package org.bigdata.hadoop.common.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * RMI 服务端
 */
public class RMIQueryStatusServer {

    // 注意这个URL的格式
    public static final String RMI_URL = "rmi://127.0.0.1:19999/query";

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // 创建调用方法实例对象
        RMIQueryStatusImpl queryServer = new RMIQueryStatusImpl();
        // 创建注册端口
        LocateRegistry.createRegistry(19999);
        // 绑定远端对象到名字
        Naming.rebind(RMI_URL, queryServer);
        System.out.println("服务启动");
    }
}
