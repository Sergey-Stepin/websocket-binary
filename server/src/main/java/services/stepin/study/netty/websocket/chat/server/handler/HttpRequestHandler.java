package services.stepin.study.netty.websocket.chat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "/home/stepin/_project/Study/NIO/examples/NettyWebSocketChat/server/src/main/resources/index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            //INDEX = new File(path);
            INDEX = new File("/home/stepin/_project/Study/NIO/examples/NettyWebSocketChat/server/src/main/resources/index.html");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(
                    "Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (wsUri.equalsIgnoreCase(request.uri()))
            processWsRequest(ctx, request);
        else
            processHttpRequest(ctx, request);

    }

    private void processWsRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        System.out.println(" WS-request");
        ctx.fireChannelRead(request.retain());
    }

    private void processHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        System.out.println(" Http-request");
        if (HttpHeaders.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        sendIndexPage(ctx, request);
    }

    private void sendIndexPage(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        HttpResponse response = new DefaultHttpResponse(
                request.protocolVersion(),
                HttpResponseStatus.OK);

        boolean isToKeepAlive = HttpHeaders.isKeepAlive(request);

        RandomAccessFile file = new RandomAccessFile(INDEX, "r");

        response.headers().set(
                HttpHeaders.Names.CONTENT_TYPE,
                "text/html; charset=UTF-8");

        if (isToKeepAlive)
            setKeepAliveHeaders(response, file.length());

        ctx.write(response);

        if (ctx.pipeline().get(SslHandler.class) == null)
            ctx.write(createDefaultFileRegion(file));
        else
            ctx.write(new ChunkedNioFile(file.getChannel()));

        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!isToKeepAlive)
            future.addListener(ChannelFutureListener.CLOSE);
    }

    private static void send100Continue(ChannelHandlerContext ctx) {

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);

        ctx.writeAndFlush(response);
    }

    private DefaultFileRegion createDefaultFileRegion(RandomAccessFile file) throws IOException {
        return new DefaultFileRegion(
                file.getChannel(),
                0,
                file.length());
    }

    private void setKeepAliveHeaders(HttpResponse response, long length){

        response.headers().set(
                HttpHeaders.Names.CONTENT_LENGTH, length);

        response.headers().set(HttpHeaders.Names.CONNECTION,
                HttpHeaders.Values.KEEP_ALIVE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}