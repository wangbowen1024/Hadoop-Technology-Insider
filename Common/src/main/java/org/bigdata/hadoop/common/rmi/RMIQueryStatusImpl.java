package org.bigdata.hadoop.common.rmi;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

/**
 * 远程接口实现类
 */
// 远程接口实现类必须继承UnicastRemoteObject，并实现RMIQueryStatus接口
public class RMIQueryStatusImpl extends UnicastRemoteObject implements RMIQueryStatus{

    protected RMIQueryStatusImpl() throws RemoteException {
    }

    protected RMIQueryStatusImpl(int port) throws RemoteException {
        super(port);
    }

    protected RMIQueryStatusImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public boolean getFileExist(String filename) throws RemoteException {
        return filename.startsWith("a");
    }
}
