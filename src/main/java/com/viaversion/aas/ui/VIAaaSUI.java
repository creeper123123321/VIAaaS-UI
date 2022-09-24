package com.viaversion.aas.ui;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.viaversion.aas.ui.command.ConnectCommand;
import com.viaversion.aas.ui.command.LobbyCommand;
import com.viaversion.aas.ui.handler.BackendInjector;
import org.slf4j.Logger;

@Plugin(id = "viaaas-ui", name = "VIAaaS UI", authors = {"creeper123123321"})
public class VIAaaSUI {
    @Inject
    private Logger logger;
    @Inject
    private ProxyServer proxy;

    @Subscribe
    public void onInit(ProxyInitializeEvent e) {
        proxy.getCommandManager().register("viaconnect", new ConnectCommand(proxy));
        proxy.getCommandManager().register("vialobby", new LobbyCommand(proxy), "viahub");

        new BackendInjector(proxy).inject();
    }

    @Subscribe
    public void onKicked(KickedFromServerEvent e) {
        e.setResult(KickedFromServerEvent.RedirectPlayer.create(proxy.getServer("lobby").orElseThrow()));
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent e) {
        proxy.getAllServers().stream()
                .filter(it -> it.getPlayersConnected().isEmpty())
                .map(RegisteredServer::getServerInfo)
                .filter(it -> it.getName().startsWith("viaaas-"))
                .forEach(it -> proxy.unregisterServer(it));
    }
}
