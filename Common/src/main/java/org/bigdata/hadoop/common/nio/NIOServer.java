package org.bigdata.hadoop.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 服务端
 */
public class NIOServer {

    public static final long TIMEOUT = 5000;

    public static void main(String[] args) throws IOException {

        // 打开一个选择器
        Selector selector = Selector.open();

        // 打开一个 ServerSocketChannel
        ServerSocketChannel listenChannel = ServerSocketChannel.open();
        // 设置非阻塞模式
        listenChannel.configureBlocking(false);
        // 绑定到 TPC 端口上
        listenChannel.socket().bind(new InetSocketAddress(19999));

        // 将 channel 注册到 selector 上,并指定监听事件
        listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 进入死循环，处理事件
        while (true) {
            if (selector.select(TIMEOUT) == 0) {
                System.out.println("没有任何事件...");
                continue;
            }

            // 获取已选键集
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 将已选键从键集上删除
                iterator.remove();

                if (key.isAcceptable()) {
                    /**
                     * 如果是监听事件
                     */
                    // 接收一个 channel
                    SocketChannel channel = listenChannel.accept();
                    // 配置异步
                    channel.configureBlocking(false);
                    // 注册读事件（等待客户端写入数据）
                    SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);
                    // 写入附件
                    String att = "This is att!";
                    clientKey.attach(att);
                } else if (key.isReadable()) {
                    /**
                     * 如果是读事件（说明有客户端写了东西）
                     */
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    // 获取 channel 并开始读取数据
                    SocketChannel sc = (SocketChannel) key.channel();
                    int bytesRead;
                    while ((bytesRead = sc.read(byteBuffer)) > 0) {
                        // 这里由于read操作，向buffer中写入数据了，[pos=10 lim=1024 cap=1024]
                        System.out.println(byteBuffer);
                        System.out.println(new String(byteBuffer.array(), 0, bytesRead, StandardCharsets.UTF_8));
                        byteBuffer.clear();
                    }
                    // 读取完消息，准备回显数据.并追加附件内容
                    handleWrite(sc, (String)key.attachment() + " Get !");
                    //key.interestOps(SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    /**
                     * 如果可以写 ???为什么不能接收到读事件后，添加写事件。这里会报错？
                     * 书上说，可写的话，发送？为什么会有这种情况？
                     */
                }
            }
        }
    }

    public static void handleWrite(SocketChannel sc, String msg) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(msg.getBytes(StandardCharsets.UTF_8));
        // 这里put操作，向buffer写入数据，改变了位置[pos=18 lim=1024 cap=1024]
        System.out.println(byteBuffer);
        // 写出数据，那么要先读buffer的内容，因此需要切换
        byteBuffer.flip();
        // [pos=0 lim=18 cap=1024]
        System.out.println(byteBuffer);

        sc.write(byteBuffer);
        sc.shutdownOutput();
    }
}
