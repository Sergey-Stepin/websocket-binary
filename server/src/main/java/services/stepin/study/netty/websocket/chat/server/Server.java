package services.stepin.study.netty.websocket.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class Server {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private Channel channel;
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public ChannelFuture start() {

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup));

        ChannelFuture future = bootstrap.bind(new InetSocketAddress(port));
        future.syncUninterruptibly();
        channel = future.channel();

        System.out.println("### SERVER is listening at port: " + port);

        return future;
    }
    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        eventLoopGroup.shutdownGracefully();
    }

    private ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup) {
            return new ServerInitializer(channelGroup);
    }
}
