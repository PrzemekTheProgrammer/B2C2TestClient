package pszerszenowicz.b2c2.test.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import pszerszenowicz.b2c2.test.client.requests.AuthenticationRequest;
import pszerszenowicz.b2c2.test.client.requests.SubscribeRequest;
import pszerszenowicz.b2c2.test.client.requests.UnsubscribeRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

public class Client {
    private final String targetHost = "localhost";
    private final int targetPort = 8080;
    private final ObjectMapper mapper = new ObjectMapper();

    private Channel channel;

    public void start() throws Exception {
        URI uri = new URI("ws://"+targetHost+":"+targetPort+"/");
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            final WebSocketClientHandler handler =
                    new WebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    handler);
                        }
                    });

            Channel ch = b.connect(targetHost, targetPort).sync().channel();
            handler.handshakeFuture().sync();

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String msg = console.readLine();
                if (msg == null) {
                    break;
                } else if ("bye".equalsIgnoreCase(msg)) {
                    ch.writeAndFlush(new CloseWebSocketFrame());
                    ch.closeFuture().sync();
                    break;
                } else if ("ping".equalsIgnoreCase(msg)) {
                    WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
                    ch.writeAndFlush(frame);
                } else if ("auth".equalsIgnoreCase(msg)){
                    WebSocketFrame frame = new TextWebSocketFrame(mapper.writeValueAsString(new AuthenticationRequest()));
                    ch.writeAndFlush(frame);
                } else if ("subscribe".equalsIgnoreCase(msg)){
                    System.out.println("instrument: ");
                    String instrument = console.readLine();
                    System.out.println("currnecy: ");
                    String currency = console.readLine();
                    System.out.println("1st level: ");
                    Integer firstLevel = Integer.parseInt(console.readLine());
                    System.out.println("2nd level: ");
                    Integer secondLevel = Integer.parseInt(console.readLine());
                    List<Integer> levels = List.of(firstLevel,secondLevel);
                    System.out.println("tag: ");
                    String tag = console.readLine();
                    WebSocketFrame frame = new TextWebSocketFrame(mapper.writeValueAsString(new SubscribeRequest(instrument,currency,levels,tag)));
                    ch.writeAndFlush(frame);
                } else if ("unsubscribe".equalsIgnoreCase(msg)) {
                    System.out.println("instrument: ");
                    String instrument = console.readLine();
                    System.out.println("tag: ");
                    String tag = console.readLine();
                    WebSocketFrame frame = new TextWebSocketFrame(mapper.writeValueAsString(new UnsubscribeRequest(instrument,tag)));
                    ch.writeAndFlush(frame);
                } else {
                    WebSocketFrame frame = new TextWebSocketFrame(msg);
                    ch.writeAndFlush(frame);
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.start();

    }

}
