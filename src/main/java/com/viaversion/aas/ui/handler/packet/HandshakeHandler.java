package com.viaversion.aas.ui.handler.packet;

import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class HandshakeHandler extends PacketHandler {
    public static Class<?> handshake;
    private static final Method setServerAddress;

    static {
        try {
            handshake = Class.forName("com.velocitypowered.proxy.protocol.packet.Handshake");
            setServerAddress = handshake.getMethod("setServerAddress", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(Channel channel, Object packet) {
        if (!(channel.remoteAddress() instanceof InetSocketAddress)) return;
        try {
            setServerAddress.invoke(packet, ((InetSocketAddress) channel.remoteAddress()).getHostString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
