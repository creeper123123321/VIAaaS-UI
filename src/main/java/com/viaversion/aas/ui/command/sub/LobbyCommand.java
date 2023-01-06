package com.viaversion.aas.ui.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class LobbyCommand implements Subcommand {
    private final ProxyServer proxy;

    public LobbyCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public int run(CommandContext<CommandSource> ctx) {
        if (!(ctx.getSource() instanceof Player)) {
            ctx.getSource().sendMessage(Component.text("Not a player"));
            return 0;
        }
        // todo unhardcode
        ((Player) ctx.getSource()).createConnectionRequest(proxy.getServer("lobby").orElseThrow())
                .connectWithIndication();
        return 1;
    }
}
