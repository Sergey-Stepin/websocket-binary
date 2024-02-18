package services.stepin.study.netty.websocket.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

    public static void main(String[] args) {
        new Client().start();
    }
    public void start() {

        final EventLoopGroup bossLoop = new NioEventLoopGroup(1);
        Bootstrap client = new Bootstrap()
                .group(bossLoop)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer());

        client
                .connect("localhost", 8080)
                .channel()
                .closeFuture()
                .syncUninterruptibly();
    }
}