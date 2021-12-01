package org.bigdata.hadoop.common.nio;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


/**
 * 总结：
 *   1. 使用 get 或者 put 的时候，position 都会增加（切换读写用的flip，只是将limit设置成position，然后position设置为0）
 */
public class BufferTest {

    /**
     * 创建 Buffer 3种方式
     */
    @Test
    public void testNew() {
        // 创建 Buffer 1 [pos=0 lim=10 cap=10]
        ByteBuffer bf1 = ByteBuffer.allocate(10);
        System.out.println(bf1.mark());

        // 创建 Buffer 2 [pos=0 lim=11 cap=11]
        byte[] bytes = "Hello word!".getBytes(StandardCharsets.UTF_8);
        ByteBuffer bf2 = ByteBuffer.wrap(bytes);
        System.out.println(bf2);

        // 创建 Buffer 3 [pos=6 lim=10 cap=11]
        ByteBuffer bf3 = ByteBuffer.wrap(bytes, 6, 4);
        System.out.println(bf3);
    }

    /**
     * 测试 获取数据-1  单字节
     */
    @Test
    public void testGet1() {
        // 创建 Buffer 2 [pos=0 lim=11 cap=11]
        byte[] bytes = "Hello word!".getBytes(StandardCharsets.UTF_8);
        ByteBuffer bf2 = ByteBuffer.wrap(bytes);
        System.out.println(bf2);

        for (int i = 0; i < 20; i++) {
            final byte b = bf2.get();
            System.out.println((char)b);
            System.out.println(bf2); // [pos=1 lim=11 cap=11] 每次读取下标都会+1，最终报错
        }

    }

    /**
     * 测试 写入数据-1 单字节
     */
    @Test
    public void testPut1() {
        // 创建 Buffer 1 [pos=0 lim=10 cap=10]
        ByteBuffer bf1 = ByteBuffer.allocate(10);
        System.out.println(bf1.mark());

        bf1.put((byte) '!');
        System.out.println(bf1); // [pos=1 lim=10 cap=10]
        bf1.put("hello".getBytes(StandardCharsets.UTF_8));
        System.out.println(bf1);  // [pos=6 lim=10 cap=10]
        bf1.put("hahahahhahahahha".getBytes(StandardCharsets.UTF_8), 2, 5);
        System.out.println(bf1); // 报错，超过限制

    }

    /**
     * 测试 flip 同时读写数据
     */
    @Test
    public void testFlip() {
        // 创建 Buffer 1 [pos=0 lim=10 cap=10]
        ByteBuffer bf1 = ByteBuffer.allocate(10);
        System.out.println(bf1.mark());

        // 写入
        bf1.put("hello".getBytes(StandardCharsets.UTF_8));
        System.out.println(bf1); // [pos=5 lim=10 cap=10]

        final byte b = bf1.get();   // 向数组的下一个位置获取数据
        System.out.println(b);      // 输出0，因为创建的字节数组默认赋值0
        System.out.println(bf1);  // [pos=6 lim=10 cap=10]

        bf1.flip();
        System.out.println(bf1); // [pos=0 lim=6 cap=10]
        byte[] bytes = new byte[24];
        bf1.get(bytes,0,4);

        System.out.println(new String(bytes, StandardCharsets.UTF_8));

    }

}
