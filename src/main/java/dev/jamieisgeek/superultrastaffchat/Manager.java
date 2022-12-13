package dev.jamieisgeek.superultrastaffchat;

import dev.jamieisgeek.superultrastaffchat.Models.Channel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Manager {
    private static Manager manager = null;
    private final SuperUltraStaffChat plugin;
    private final ArrayList<Channel> channels = new ArrayList<>();
    private HashMap<UUID, Channel[]> playerMutedChannels = new HashMap<>();
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
        playerMutedChannels.put(uuid, new Channel[]{channel});
    }

    public void removePlayerMutedChannel(UUID uuid, Channel channel) {
        Channel[] channels = playerMutedChannels.get(uuid);
        if (channels != null) {
            for (int i = 0; i < channels.length; i++) {
                if (channels[i].name().equals(channel.name())) {
                    channels[i] = null;
                }
            }
        }
    }

    public void sendMessageToChannel(Channel channel, String message, String senderName, String serverName) {
        String formatted = String.format(ChatColor.translateAlternateColorCodes('&', channel.chatColor() + "[" + channel.displayName() + channel.chatColor() + "] [%s] | %s -> %s"), serverName, senderName, message);
        plugin.getProxy().getPlayers().forEach(player -> {
            player.sendMessage(new TextComponent(formatted));
        });
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
