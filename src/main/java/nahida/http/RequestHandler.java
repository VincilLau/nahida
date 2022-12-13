package nahida.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.net.InetSocketAddress;

public class RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private Handler handler;

  RequestHandler(Handler handler) {
    this.handler = handler;
  }

  private void handleUnsupportedHttpVersion(ChannelHandlerContext ctx) {
    var content = HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED.reasonPhrase();
    var resp =
        new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_0,
            HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED,
            Unpooled.wrappedBuffer(content.getBytes()));
    resp.headers().add("Connection", "close");
    resp.headers().add("Content-Type", "text/plain");
    resp.headers().add("Content-Length", Integer.toString(content.length()));
    ctx.writeAndFlush(resp);
    ctx.close();
  }

  private void handleBadRequest(ChannelHandlerContext ctx, String content) {
    var resp =
        new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.BAD_REQUEST,
            Unpooled.wrappedBuffer(content.getBytes()));
    resp.headers().add("Connection", "close");
    resp.headers().add("Content-Type", "text/plain");
    resp.headers().add("Content-Length", Integer.toString(content.length()));
    ctx.writeAndFlush(resp);
    ctx.close();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    if (request.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
      handleUnsupportedHttpVersion(ctx);
      return;
    }

    var ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
    Request req = null;
    try {
      req = new Request(request, ip);
    } catch (Exception e) {
      handleBadRequest(ctx, e.toString());
      return;
    }

    var resp = new Response();
    handler.handle(req, resp);
    resp.headers.add("Connection", "close");
    var response = resp.ToDefaultFullHttpResponse();
    if (req.method.equals(Method.HEAD)) {
      response.content().writerIndex(0);
    }
    ctx.writeAndFlush(response);
    ctx.close();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable except) {
    if (ctx != null) {
      ctx.close();
    }
  }
}
