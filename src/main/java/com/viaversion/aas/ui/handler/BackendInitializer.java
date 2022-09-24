package com.viaversion.aas.ui.handler;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class BackendInitializer extends ChannelInitializer<Channel> {
    private final ChannelInitializer<?> original;
    private static final Method initChannel;

    static {
        try {
            initChannel = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            initChannel.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public BackendInitializer(ChannelInitializer<?> original) {
        this.original = original;
    }

    @Override
    protected void initChannel(@NotNull Channel ch) throws Exception {
        initChannel.invoke(original, ch);
        ch.pipeline().addAfter("minecraft-encoder", "viaaas-handler", new BackendHandler());
    }
}
