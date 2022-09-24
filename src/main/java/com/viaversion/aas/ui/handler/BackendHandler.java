package com.viaversion.aas.ui.handler;

import com.viaversion.aas.ui.handler.packet.PacketHandler;
import com.viaversion.aas.ui.handler.packet.PacketHandlerRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class BackendHandler extends MessageToMessageEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) {
        PacketHandler handler = PacketHandlerRegistry.HANDSHAKE.getHandler(msg.getClass());
        if (handler != null) {
            handler.handle(ctx.channel(), msg);
        }
        out.add(ReferenceCountUtil.retain(msg));
    }
}
