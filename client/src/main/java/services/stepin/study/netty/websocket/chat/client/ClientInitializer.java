package services.stepin.study.netty.websocket.chat.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import services.stepin.study.netty.websocket.chat.client.handler.TextWebSocketFrameHandler;

import java.net.URI;

public class ClientInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

//                        KeyStore truststore = KeyStore.getInstance("JKS");
//                        truststore.load(NettyWSClient.class.getResourceAsStream("/TestTruststore.jks"), "changeit".toCharArray());
//                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//                        trustManagerFactory.init(truststore);
//
//                        pipeline.addLast(SslContextBuilder.forClient().trustManager(trustManagerFactory).build().newHandler(channel.alloc()));

        pipeline.addLast(new HttpClientCodec(512, 512, 512));
        pipeline.addLast(new HttpObjectAggregator(64_000));
        final String url = "ws://localhost:8080/ws";
        final WebSocketClientHandshaker13 wsHandshaker = new WebSocketClientHandshaker13(new URI(url),
                WebSocketVersion.V13, "", false, new DefaultHttpHeaders(false), 64_000);
        pipeline.addLast(new WebSocketClientProtocolHandler(wsHandshaker));

        pipeline.addLast(new TextWebSocketFrameHandler());
    }

}
