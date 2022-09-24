package com.viaversion.aas.ui.handler;

import com.velocitypowered.api.proxy.ProxyServer;
import io.netty.channel.ChannelInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Supplier;

public class BackendInjector {
    private final ProxyServer proxy;

    public BackendInjector(ProxyServer proxy) {
        this.proxy = proxy;
    }

    private ChannelInitializer<?> getBackendInitializer() throws Exception {
        Object connectionManager = getPrivateField(proxy, "cm");
        Supplier<?> channelInitializerHolder = (Supplier<?>) invokePublic(Objects.requireNonNull(connectionManager),
                "getBackendChannelInitializer");
        return (ChannelInitializer<?>) channelInitializerHolder.get();
    }

    private Object invokePublic(Object o, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return o.getClass().getMethod(methodName).invoke(o);
    }

    private Object getPrivateField(Object o, String fieldName) {
        for (Class klass = o.getClass(); klass != Object.class; klass = klass.getSuperclass()) {
            try {
                Field field = klass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(o);
            } catch (IllegalAccessException | NoSuchFieldException ignore) {
            }
        }
        return null;
    }

    public void inject() {
        try {
            Object connectionManager = getPrivateField(proxy, "cm");
            Object backendInitializerHolder = invokePublic(connectionManager, "getBackendChannelInitializer");
            ChannelInitializer<?> original = getBackendInitializer();
            backendInitializerHolder.getClass()
                    .getMethod("set", ChannelInitializer.class)
                    .invoke(backendInitializerHolder, new BackendInitializer(original));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
