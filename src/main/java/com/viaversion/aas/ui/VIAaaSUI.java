package com.viaversion.aas.ui;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.viaversion.aas.ui.command.ViaUiCommand;
import com.viaversion.aas.ui.handler.BackendInjector;
import com.viaversion.aas.ui.login.MsLoginInfo;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

@Plugin(id = "viaaas-ui", name = "VIAaaS UI", authors = {"creeper123123321"})
public class VIAaaSUI {
    public static String CLIENT_ID = "a370fff9-7648-4dbf-b96e-2b4f8d539ac2"; // VIAaaS
    @Inject
    private Logger logger;
    @Inject
    private ProxyServer proxy;
    public LoadingCache<Player, MsLoginInfo> logins = CacheBuilder.newBuilder()
            .weakKeys()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(CacheLoader.from((it) -> new MsLoginInfo()));

    @Subscribe
    public void onInit(ProxyInitializeEvent e) {
        proxy.getCommandManager().register(new ViaUiCommand(this).brigadierCommand());

        new BackendInjector(proxy).inject();
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent e) {
        proxy.getAllServers().stream()
                .filter(it -> it.getPlayersConnected().isEmpty())
                .map(RegisteredServer::getServerInfo)
                .filter(it -> it.getName().startsWith("viaaas-"))
                .forEach(it -> proxy.unregisterServer(it));
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getProxy() {
        return proxy;
    }
}
