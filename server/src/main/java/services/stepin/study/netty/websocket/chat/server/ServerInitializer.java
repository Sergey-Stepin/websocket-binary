package services.stepin.study.netty.websocket.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import services.stepin.study.netty.websocket.chat.server.handler.CalculatorServerFrameHandler;
import services.stepin.study.netty.websocket.chat.server.handler.HttpRequestHandler;

public class ServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch){

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new HttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //pipeline.addLast(new TextWebSocketFrameHandler(group));
        pipeline.addLast(new CalculatorServerFrameHandler());
    }
}
