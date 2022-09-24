package com.viaversion.aas.ui.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class LobbyCommand implements SimpleCommand {
    private final ProxyServer proxy;

    public LobbyCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component.text("Not a player"));
            return;
        }
        ((Player) invocation.source()).createConnectionRequest(proxy.getServer("lobby").orElseThrow())
                .connectWithIndication();
    }
}
