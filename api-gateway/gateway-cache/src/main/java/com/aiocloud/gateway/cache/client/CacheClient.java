package com.aiocloud.gateway.cache.client;

import com.aiocloud.gateway.cache.client.pool.CacheClientPool;
import com.aiocloud.gateway.cache.client.protocol.MessageDecoder;
import com.aiocloud.gateway.cache.client.protocol.MessageEncoder;
import com.aiocloud.gateway.cache.conf.SystemProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @description: CacheClient.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-03 17:47
 */
public class CacheClient {

    private static final Logger log = LoggerFactory.getLogger(CacheClient.class);

    private final String host;
    private final int port;
    private final CacheClientHandler cacheClientHandler;
    private ChannelFuture channelFuture;
    private EventLoopGroup group;

    public CacheClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.cacheClientHandler = new CacheClientHandler();

        try {
            run();
        } catch (Exception ex) {
            log.error("run cache client error, cause by:", ex);
        }
    }

    /**
     * 运行缓存客户端
     *
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 11:47
     * @since 1.0.0
     */
    public void run() throws Exception {

        this.group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
                                new LengthFieldPrepender(4),
                                new MessageEncoder(),
                                new MessageDecoder(),
                                cacheClientHandler
                        );
                    }
                });

        this.channelFuture = b.connect(host, port).sync();
    }

    public void setCache(String key, Object value) {

        if (channelFuture != null && channelFuture.channel().isActive()) {
            this.cacheClientHandler.sendMessage(key, value);
        } else {
            log.error("Channel is not active, cannot send message.");
        }
    }

    public void close() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public boolean isActive() {
        return channelFuture != null && channelFuture.channel().isActive();
    }

    public static void main(String[] args) {

        CacheClientPool cacheClientPool = new CacheClientPool(SystemProperties.serverHost, SystemProperties.serverPort);

        try {

            CacheClient cacheClient = cacheClientPool.borrowObject();
            cacheClient.setCache("test", "test");
        } catch (Exception ex) {
            log.error(" error, cause by:", ex);
        } finally {
            cacheClientPool.close();
        }
    }
}
