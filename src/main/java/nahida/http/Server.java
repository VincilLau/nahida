package nahida.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class Server {
  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_BACKLOG = 511;
  private static final int MAX_CONTENT_LENGTH = 1024 * 1024;

  public void run(String host, int port, Handler handler) throws Exception {
    var group = new NioEventLoopGroup(1);
    var bootstrap = getBootstrap(group, handler);

    try {
      var future = bootstrap.bind(host, port).sync();
      future.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }

  public void run(int port, Handler handler) throws Exception {
    run(DEFAULT_HOST, port, handler);
  }

  private ServerBootstrap getBootstrap(EventLoopGroup group, Handler handler) {
    var bootstrap = new ServerBootstrap();
    bootstrap
        .group(group)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, DEFAULT_BACKLOG)
        .option(ChannelOption.SO_REUSEADDR, true)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(
            new ChannelInitializer<SocketChannel>() {
              @Override
              public void initChannel(SocketChannel ch) {
                ch.pipeline()
                    .addLast(new HttpRequestDecoder())
                    .addLast(new HttpResponseEncoder())
                    .addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH))
                    .addLast(new RequestHandler(handler));
              }
            });
    return bootstrap;
  }
}
