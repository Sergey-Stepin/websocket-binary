package services.stepin.study.netty.websocket.chat.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class CalculatorServerFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        System.out.println("process event");

        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {

            System.out.println("Handshake is complete");
            ctx.pipeline().remove(HttpRequestHandler.class);

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {

        ByteBuf byteBuf = msg.content();

        int[] input = read(byteBuf);
        int[] output = calculate(input);

        ByteBuf outputMessage = createOutputMessage(output);
        var out = new BinaryWebSocketFrame(outputMessage);

        ctx.writeAndFlush(out);

        System.out.println("-------------------");
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

    private int[] calculate(int[] input) {

        int[] output = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i] * input[i];
        }

        return output;
    }

    private ByteBuf createOutputMessage(int[] output) {

        int size = output.length;

        ByteBuf buf = Unpooled.buffer();

        int payloadLengthInBytes = size * Integer.BYTES;
        buf.writeByte(payloadLengthInBytes);

        for (int i = 0; i < size; i++) {
            buf.writeInt(output[i]);
        }

        return buf;
    }
}