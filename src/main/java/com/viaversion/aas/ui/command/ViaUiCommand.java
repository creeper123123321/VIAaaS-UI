package com.viaversion.aas.ui.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.viaversion.aas.ui.VIAaaSUI;
import com.viaversion.aas.ui.command.sub.ConnectCommand;
import com.viaversion.aas.ui.command.sub.LobbyCommand;
import com.viaversion.aas.ui.command.sub.LoginCommand;

public class ViaUiCommand {
    private VIAaaSUI plugin;

    public ViaUiCommand(VIAaaSUI plugin) {
        this.plugin = plugin;
    }

    public BrigadierCommand brigadierCommand() {
        ConnectCommand connect = new ConnectCommand(plugin.getProxy());
        return new BrigadierCommand(LiteralArgumentBuilder.<CommandSource>literal("viaui")
                .then(LiteralArgumentBuilder.<CommandSource>literal("connect")
                        .then(RequiredArgumentBuilder
                                .<CommandSource, String>argument("args", StringArgumentType.greedyString())
                                .executes(connect))
                        .executes(connect)
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("hub")
                        .executes(new LobbyCommand(plugin.getProxy()))
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("login")
                        .executes(new LoginCommand(plugin)))
        );
    }
}
