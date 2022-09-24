package com.viaversion.aas.ui.command;

import com.google.common.net.HostAndPort;
import com.velocitypowered.api.command.SimpleCommand;
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

public class ConnectCommand implements SimpleCommand {
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
        //noinspection unchecked
    }

    protected String getSuffix() {
        return ".via.geyserconnect.net";
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component.text("Not a Player", NamedTextColor.RED));
            return; // todo
        }
        OptionSet parsed = optionParser.parse(invocation.arguments());
        if (parsed.has(help) || !parsed.hasArgument(backAddress)) {
            sendHelp(((Player) invocation.source()));
            return;
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
        invocation.source().sendMessage(Component.text("Connecting to " + socket));
        RegisteredServer server = proxy.registerServer(new ServerInfo("viaaas-" + lastServerId.getAndIncrement(), socket));

        ((Player) invocation.source()).createConnectionRequest(server).connectWithIndication();
    }
}
