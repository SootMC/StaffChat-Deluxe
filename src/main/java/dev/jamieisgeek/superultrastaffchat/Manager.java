package dev.jamieisgeek.superultrastaffchat;

import dev.jamieisgeek.superultrastaffchat.Models.Channel;
import dev.jamieisgeek.superultrastaffchat.Models.DiscordBot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Manager {
    private static Manager manager = null;
    private final SuperUltraStaffChat plugin;
    private final ArrayList<Channel> channels = new ArrayList<>();
    private HashMap<UUID, ArrayList<Channel>> playerMutedChannels = new HashMap<>();
    private HashMap<UUID, Channel> playerToggledChannel = new HashMap<>();

    public Manager(SuperUltraStaffChat plugin) {
        manager = this;
        this.plugin = plugin;
    }

    public static Manager getManager() {
        return manager;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void addPlayerMutedChannel(UUID uuid, Channel channel) {
        if(playerMutedChannels.containsKey(uuid)) {
            if(!playerMutedChannels.get(uuid).contains(channel)) {
                playerMutedChannels.get(uuid).add(channel);
            }
        } else {
            ArrayList<Channel> channels = new ArrayList<>();
            channels.add(channel);
            playerMutedChannels.put(uuid, channels);
        }
    }

    public void removePlayerMutedChannel(UUID uuid, Channel channel) {
        ArrayList<Channel> channels = playerMutedChannels.get(uuid);
        if (channels != null) {
            for (int i = 0; i < channels.size(); i++) {
                if (channels.get(i).name().equals(channel.name())) {
                    channels.set(i, null);
                }
            }
        }
    }

    public void sendMessageToChannel(Channel channel, String message, String senderName, String serverName, boolean isDiscord) {
        String preColor = channel.format().replace("{server}", serverName).replace("{player}", senderName).replace("{message}", message);
        String formatted = ChatColor.translateAlternateColorCodes('&', preColor);
        plugin.getProxy().getPlayers().forEach(player -> {
            if(playerMutedChannels.get(player.getUniqueId()) == null || !playerMutedChannels.get(player.getUniqueId()).contains(channel)) {
                player.sendMessage(new TextComponent(formatted));
            }
        });

        plugin.getLogger().info(formatted);

        if(isDiscord) return;

        DiscordBot.getDiscordBot().sendChannelMessage(message, channel, serverName, senderName);
    }

    public void sendMoveMessage(String player, String preServer, String postServer, Channel channel) {
        String formatted = String.format(ChatColor.translateAlternateColorCodes('&', channel.displayName() + " &r| " + channel.chatColor() + "%s switched from %s to %s"), player, preServer, postServer);
        plugin.getProxy().getPlayers().forEach(player1 -> {
            if(playerMutedChannels.get(player1.getUniqueId()) == null || !playerMutedChannels.get(player1.getUniqueId()).contains(channel)) {
                player1.sendMessage(new TextComponent(formatted));
            }
        });

        plugin.getLogger().info(formatted);

        DiscordBot.getDiscordBot().sendSwitchMessage(String.format("**%s** moved from **%s** to **%s**", player, preServer, postServer), channel);
    }

    public void sendJoinLeaveMessage(String player, Channel channel, boolean isJoin) {
        if(isJoin) {
            plugin.getProxy().getPlayers().forEach(player1 -> {
                player1.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', channel.displayName() + "&r | " + channel.chatColor() + "[+] " + player)));
            });

            DiscordBot.getDiscordBot().sendJoinLeaveMessage(player, channel, true);
            plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', channel.displayName() + "&r | " + channel.chatColor() + "[+] " + player));
        } else {
            plugin.getProxy().getPlayers().forEach(player1 -> {
                player1.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', channel.displayName() + "&r | " + channel.chatColor() + "[-] " + player)));
            });

            DiscordBot.getDiscordBot().sendJoinLeaveMessage(player, channel, false);
            plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', channel.displayName() + "&r | " + channel.chatColor() + "[-] " + player));
        }
    }

    public void addPlayerToggledChannel(UUID uuid, Channel channel) {
        playerToggledChannel.put(uuid, channel);
    }

    public void removePlayerToggledChannel(UUID uuid) {
        playerToggledChannel.remove(uuid);
    }

    public HashMap<UUID, Channel> getPlayerToggledChannel() {
        return playerToggledChannel;
    }
    public SuperUltraStaffChat getPlugin() {
        return plugin;
    }
}
