package com.viaversion.aas.ui.handler.packet;

import java.util.HashMap;
import java.util.Map;

public enum PacketHandlerRegistry {
    HANDSHAKE(HandshakeHandler.handshake, new HandshakeHandler());

    private final Class<?> klass;
    private final PacketHandler handler;
    private static final Map<Class<?>, PacketHandler> handlers = new HashMap<>();

    static {
        for (PacketHandlerRegistry value : PacketHandlerRegistry.values()) {
            handlers.put(value.klass, value.handler);
        }
    }

    PacketHandlerRegistry(Class<?> klass, PacketHandler handler) {
        this.klass = klass;
        this.handler = handler;
    }

    public PacketHandler getHandler(Class<?> klass) {
        return handlers.get(klass);
    }
}
