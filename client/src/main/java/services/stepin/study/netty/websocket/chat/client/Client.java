package services.stepin.study.netty.websocket.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import services.stepin.study.netty.websocket.chat.client.handler.TextWebSocketFrameHandler;

import java.net.URI;

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