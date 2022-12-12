package dev.jamieisgeek.superultrastaffchat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChatCommand extends Command {
    private final Manager manager;
    private final Channel channel;
    public ChatCommand(String name, Channel channel, Manager manager) {
        super(name, channel.permission(), channel.aliases());
        this.channel = channel;
        this.manager = manager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer player && args.length == 1) {
            switch (args[1]) {
                case "mute" -> {
                    manager.addPlayerMutedChannel(player.getUniqueId(), channel);
                    player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', channel.displayName() + ChatColor.RESET + ChatColor.RED + "Muted channel " + ChatColor.RESET + channel.name())));
                }
                case "unmute" -> {
                    manager.removePlayerMutedChannel(player.getUniqueId(), channel);
                    player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', channel.displayName() + ChatColor.RESET + ChatColor.GREEN + "Unmuted channel " + ChatColor.RESET + channel.name())));
                }
            }
        }

        if(sender instanceof ProxiedPlayer player && args.length == 0) {
            if(manager.getPlayerToggledChannel().containsKey(player.getUniqueId()) && manager.getPlayerToggledChannel().get(player.getUniqueId()).name().equals(channel.name())) {
                manager.removePlayerToggledChannel(player.getUniqueId());
                player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', channel.displayName() + ChatColor.RESET + ChatColor.GREEN + "Toggled channel " + ChatColor.RESET + channel.name())));
            } else {
                manager.addPlayerToggledChannel(player.getUniqueId(), channel);
                player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', channel.displayName() + ChatColor.RESET + ChatColor.GREEN + "Toggled channel " + ChatColor.RESET + channel.name())));
            }
        }
    }
}
