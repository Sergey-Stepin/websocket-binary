package services.stepin.study.netty.websocket.chat.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

import static io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE;

public class CalculatorClientFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

        if (evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent handshakeStateEvent)
            processHandshakeStateEvent(ctx, handshakeStateEvent);
    }

    private void processHandshakeStateEvent(
            ChannelHandlerContext ctx,
            WebSocketClientProtocolHandler.ClientHandshakeStateEvent event) {

        if (HANDSHAKE_COMPLETE.equals(event)) {
            System.out.println("Handshake is complete");
            sendInitialData(ctx);
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, BinaryWebSocketFrame msg) {

        ByteBuf byteBuf = msg.content();

        int[] input = read(byteBuf);
        System.out.println("-------------------");
    }

    private void sendInitialData(ChannelHandlerContext ctx){

        ByteBuf buf = Unpooled.buffer();

        buf.writeByte(Integer.BYTES * 20);
        buf.writeInt(1);
        buf.writeInt(2);
        buf.writeInt(3);
        buf.writeInt(4);
        buf.writeInt(5);
        buf.writeInt(6);
        buf.writeInt(7);
        buf.writeInt(8);
        buf.writeInt(9);
        buf.writeInt(0);
        buf.writeInt(1);
        buf.writeInt(2);
        buf.writeInt(3);
        buf.writeInt(4);
        buf.writeInt(5);
        buf.writeInt(6);
        buf.writeInt(7);
        buf.writeInt(8);
        buf.writeInt(9);
        buf.writeInt(0);

        BinaryWebSocketFrame msg = new BinaryWebSocketFrame(buf);
        ctx.writeAndFlush(msg);

        System.out.println("Initial data have been sent");
    }

    private int[] read(ByteBuf msg) {

        int size = msg.readByte() / 4;
        System.out.println("size: " + size);

        int[] input = new int[size];

        for (int i = 0; i < size; i++) {
            input[i] = msg.readInt();
            System.out.println(input[i]);
        }

        return input;
    }

}