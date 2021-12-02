package org.bigdata.hadoop.common.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * RMI 客户端
 */
public class RMIQueryStatusClient {
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        // 创建 RMIQueryStatusImpl 对象，并选定方法
        RMIQueryStatus query = (RMIQueryStatus)Naming.lookup(RMIQueryStatusServer.RMI_URL);

        // 调用远程方法
        boolean exist = query.getFileExist("b");
        System.out.println(exist);
    }
}
