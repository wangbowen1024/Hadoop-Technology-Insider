package org.bigdata.hadoop.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * java 远程方法调用（RMI）
 * 远程接口
 */

// 远程接口必须要声明为 public
// 远程接口必须继承 java.rmi.Remote
public interface RMIQueryStatus extends Remote {

    // 必须声明 throws RemoteException
    boolean getFileExist(String filename) throws RemoteException;

}
