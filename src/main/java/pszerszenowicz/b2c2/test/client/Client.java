package pszerszenowicz.b2c2.test.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;

public class Client {
    private final String targetHost = "localhost";
    private final int targetPort = 8080;

    private ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private Channel channel;

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new HttpClientCodec(),
                                    new WebSocketClientProtocolHandler(
                                            WebSocketClientHandshakerFactory.newHandshaker(
                                                    new URI("ws://" + targetHost + ":" + targetPort), WebSocketVersion.V13, null, false, new DefaultHttpHeaders())
                                    ),
                                    new ClientResponseHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(targetHost, targetPort).sync();
            channel = future.channel();
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    group.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
    }

    public void sendMessage(String message) {
        if(channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        }
    }

    private void closeChannel(){
        if(channel != null && channel.isActive()) {
            channel.close();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        client.start();

        client.sendMessage("test");


    }

}
