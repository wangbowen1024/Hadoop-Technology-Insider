package org.bigdata.hadoop.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * NIO 客户端
 */
public class NIOClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(19999));
        socketChannel.configureBlocking(true);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        String msg = "Hello NIO!";
        byteBuffer.put(msg.getBytes(StandardCharsets.UTF_8));
        // 由于put操作，[pos=10 lim=1024 cap=1024]
        System.out.println(byteBuffer);

        // 连接并发送数据
        if (socketChannel.isConnected()) {
            // 这里非常关键，写数据的话，需要读取 buffer 的内容，而之前 put 操作，已经改变了 buffer 下标位置
            byteBuffer.flip();
            // [pos=0 lim=10 cap=1024]
            System.out.println(byteBuffer);
            socketChannel.write(byteBuffer);
            // [pos=10 lim=10 cap=1024]
            System.out.println(byteBuffer);

            // 接下来要往buffer写数据，要清空
            byteBuffer.clear();

            int bytesRead;
            while ((bytesRead = socketChannel.read(byteBuffer)) > 0) {
                // 切换读,因为刚才写出时候，进行了读操作
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(), 0, bytesRead));
                byteBuffer.clear();
            }

            // 关闭
            socketChannel.close();
        }
    }
}
