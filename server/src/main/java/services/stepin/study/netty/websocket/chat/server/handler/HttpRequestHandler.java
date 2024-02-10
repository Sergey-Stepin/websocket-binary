package services.stepin.study.netty.websocket.chat.server.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;

@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final File INDEX_FILE = new File("/home/stepin/_project/Study/NIO/examples/NettyWebSocketChat/server/src/main/resources/index.html");

    private final String wsUri;

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        System.out.println("Received a request: " + request.getUri());

        if (wsUri.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());
            System.out.println("WS request");
            return;
        }

        System.out.println("HTTP request");
        if (HttpHeaders.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        RandomAccessFile file = new RandomAccessFile(INDEX_FILE, "r");

        HttpResponse response = new DefaultHttpResponse(
                request.getProtocolVersion(),
                HttpResponseStatus.OK);

        response.headers().set(
                HttpHeaders.Names.CONTENT_TYPE,
                "text/plain; charset=UTF-8");

        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        if (keepAlive) {
            response.headers().set(
                    HttpHeaders.Names.CONTENT_LENGTH,
                    file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION,
                    HttpHeaders.Values.KEEP_ALIVE);
        }

        ctx.write(response);

        if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        } else {
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }

        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.CONTINUE);

        ctx.writeAndFlush(response);
    }
}