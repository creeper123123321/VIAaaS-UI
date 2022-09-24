package com.viaversion.aas.ui.handler.packet;

import io.netty.channel.Channel;

public abstract class PacketHandler {
    public abstract void handle(Channel channel, Object packet);
}
