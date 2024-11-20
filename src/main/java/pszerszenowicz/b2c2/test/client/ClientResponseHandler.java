package pszerszenowicz.b2c2.test.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class ClientResponseHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws Exception {

        String message = ((TextWebSocketFrame) webSocketFrame).text();
        channelHandlerContext.channel().writeAndFlush("/n/n" + message + "/n/n");

    }
}
