package com.viaversion.aas.ui.command.sub;

import com.google.common.net.HostAndPort;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import joptsimple.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectCommand implements Subcommand {
    private final OptionParser optionParser = new OptionParser();
    private final OptionSpec<Void> help = optionParser.acceptsAll(Arrays.asList("h", "help"), "Prints help").forHelp();
    private final OptionSpec<String> version = optionParser.acceptsAll(Arrays.asList("v", "version"), "Sets the version to use when connecting to backend")
            .withOptionalArg();
    private final OptionSpec<String> username = optionParser.acceptsAll(Arrays.asList("u", "username"), "Modifies your username when connecting to backend")
            .withOptionalArg();
    private final NonOptionArgumentSpec<String> backAddress = optionParser.nonOptions("The address and port to connect");
    private final ProxyServer proxy;
    private final static AtomicInteger lastServerId = new AtomicInteger();

    public ConnectCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    private String formatArgName(Collection<String> names) {
        return String.join(", ", names.stream().map(it -> it.length() == 1 ? "-" + it : "--" + it)
                .toArray(String[]::new));
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("Use /viaconnect <server address>[:port] -u [username] -v [version]"));
        optionParser.recognizedOptions()
                .values().stream()
                .distinct()
                .filter(it -> it instanceof OptionDescriptor)
                .forEach((option) -> player.sendMessage(Component.text(formatArgName(option.options()) + ": "
                        + ((OptionDescriptor) option).description())));
    }

    protected String getSuffix() {
        return ".via.geyserconnect.net";  // todo unhardcode
    }

    @Override
    public int run(CommandContext<CommandSource> ctx) {
        if (!(ctx.getSource() instanceof Player)) {
            ctx.getSource().sendMessage(Component.text("Not a Player", NamedTextColor.RED));
            return 0; // todo
        }
        String[] args = ctx.getArguments().containsKey("args") ?
                ctx.getArgument("args", String.class).split(" ") : new String[0];
        OptionSet parsed = optionParser.parse(args);
        if (parsed.has(help) || !parsed.hasArgument(backAddress)) {
            sendHelp(((Player) ctx.getSource()));
            return 0;
        }

        String version = parsed.valueOf(this.version);
        if (version != null) version = version.replace('.', '_');
        String username = parsed.valueOf(this.username);
        HostAndPort hostPort = HostAndPort.fromString(parsed.valueOf(backAddress)).withDefaultPort(25565);

        String finalAddress = hostPort.getHost() + "._p" + hostPort.getPort() + "._of";
        if (version != null) finalAddress += "._v" + version;
        if (username != null) finalAddress += "._u" + username;
        finalAddress += getSuffix();

        InetSocketAddress socket = InetSocketAddress.createUnresolved(finalAddress, 25565);
        ctx.getSource().sendMessage(Component.text("Connecting to " + socket));
        RegisteredServer server = proxy.registerServer(new ServerInfo("viaaas-" + lastServerId.getAndIncrement(), socket));

        ((Player) ctx.getSource()).createConnectionRequest(server).connectWithIndication();
        return 1;
    }
}
