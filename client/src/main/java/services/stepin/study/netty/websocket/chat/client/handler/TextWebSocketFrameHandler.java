package services.stepin.study.netty.websocket.chat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

import static io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent handshakeStateEvent)
            processHandshakeStateEvent(ctx, handshakeStateEvent);
    }

    private void processHandshakeStateEvent(
            ChannelHandlerContext ctx,
            WebSocketClientProtocolHandler.ClientHandshakeStateEvent event){

        if(HANDSHAKE_COMPLETE.equals(event)){
            System.out.println("Handshake completed. Sending Hello World");
            ctx.writeAndFlush(new TextWebSocketFrame("Hello World"));
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("Client received message=" + msg.text());
    }

}
