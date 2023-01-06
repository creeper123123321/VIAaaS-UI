package com.viaversion.aas.ui.command.sub;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.viaversion.aas.ui.VIAaaSUI;
import com.viaversion.aas.ui.login.MsLoginInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginCommand implements Subcommand {
    private Executor executor = Executors.newSingleThreadExecutor();
    private VIAaaSUI plugin;

    public LoginCommand(VIAaaSUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        if (!(context.getSource() instanceof Player)) {
            context.getSource().sendMessage(Component.text("Not a player!"));
            return 0;
        }
        context.getSource().sendMessage(Component.text("WIP!!!"));
        MsLoginInfo info = plugin.logins.getUnchecked((Player) context.getSource());
        if (info.generatedCode) {
            CompletableFuture
                    .runAsync(() -> {
                        try {
                            info.service.login();
                        } catch (RequestException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor)
                    .thenRun(() -> {
                        context.getSource().sendMessage(Component.text("login success! todo"));
                        info.service.getAccessToken(); // todo
                    }).exceptionally(err -> {
                        context.getSource().sendMessage(Component.text("Error during login: " + err));
                        plugin.logins.invalidate(((Player) context.getSource()));
                        return null;
                    });
        } else {
            CompletableFuture.supplyAsync(() -> {
                try {
                    MsaAuthenticationService.MsCodeResponse resp = info.service.getAuthCode();
                    info.generatedCode = true;
                    return resp;
                } catch (RequestException e) {
                    throw new RuntimeException(e);
                }
            }, executor).thenAccept((resp) -> {
                context.getSource().sendMessage(Component.text("Use code ")
                        .append(Component.text(resp.user_code))
                        .append(Component.text(" in "))
                        .append(Component.text("https://aka.ms/remoteconnect")
                                .clickEvent(ClickEvent.openUrl("https://aka.ms/remoteconnect")))
                        .append(Component.text(", then run '/viaui login' command again")));

            }).exceptionally(err -> {
                context.getSource().sendMessage(Component.text("Error generating code: " + err));
                return null;
            });
        }
        return 1;
    }
}
