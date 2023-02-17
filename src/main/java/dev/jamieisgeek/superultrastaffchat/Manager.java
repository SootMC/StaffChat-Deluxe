package dev.jamieisgeek.superultrastaffchat;

import dev.jamieisgeek.superultrastaffchat.Models.Channel;
import dev.jamieisgeek.superultrastaffchat.Models.Database;
import dev.jamieisgeek.superultrastaffchat.Models.DiscordBot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Manager {
    private static Manager manager = null;
    private Configuration messages;
    private final SuperUltraStaffChat plugin;
    private final ArrayList<Channel> channels = new ArrayList<>();
    private HashMap<UUID, ArrayList<Channel>> playerMutedChannels = new HashMap<>();
    private HashMap<UUID, Channel> playerToggledChannel = new HashMap<>();
    private String formattedForJoinLeave;

    public Manager(SuperUltraStaffChat plugin, Configuration messages) {
        manager = this;
        this.plugin = plugin;
        this.messages = messages;
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
            ArrayList<Channel> mutedChannels = playerMutedChannels.get(uuid);
            if(!mutedChannels.contains(channel) && !mutedChannels.contains(null)) {
                mutedChannels.add(channel);
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
                    channels.remove(i);
                }
            }
        }
    }

    public void sendMessageToChannel(Channel channel, String message, String senderName, String serverName, boolean isDiscord) {
        String msg = messages.getString("chat")
                .replace("{server}", serverName)
                .replace("{player}", senderName)
                .replace("{message}", message)
                .replace("{displayName}", channel.displayName())
                .replace("{color}", channel.chatColor());

        String formatted = ChatColor.translateAlternateColorCodes('&', msg);
        plugin.getProxy().getPlayers().forEach(player -> {
            if(playerMutedChannels.get(player.getUniqueId()) == null || !playerMutedChannels.get(player.getUniqueId()).contains(channel)) {
                if(!player.hasPermission(channel.permission())) return;

                player.sendMessage(new TextComponent(formatted));
            }
        });

        plugin.getLogger().info(formatted);

        if(isDiscord) return;

        String discordMessage = messages.getString("discord")
                .replace("{server}", serverName)
                .replace("{player}", senderName)
                .replace("{message}", message)
                .replace("{displayName}", channel.displayName())
                .replace("{color}", channel.chatColor());

        DiscordBot.getDiscordBot().sendChannelMessage(discordMessage, channel);
    }

    public void sendMoveMessage(String player, String preServer, String postServer, Channel channel) {
        String msg = messages.getString("switch")
                .replace("{player}", player)
                .replace("{serverFrom}", preServer)
                .replace("{server}", postServer)
                .replace("{displayName}", channel.displayName())
                .replace("{color}", channel.chatColor());

        String formatted = ChatColor.translateAlternateColorCodes('&', msg);

        plugin.getProxy().getPlayers().forEach(player1 -> {
            if(playerMutedChannels.get(player1.getUniqueId()) == null || !playerMutedChannels.get(player1.getUniqueId()).contains(channel)) {
                if(!player1.hasPermission(channel.permission())) return;

                player1.sendMessage(new TextComponent(formatted));
            }
        });

        plugin.getLogger().info(formatted);

        String discordMsg = messages.getString("discordSwitch")
                .replace("{player}", player)
                .replace("{serverFrom}", preServer)
                .replace("{server}", postServer)
                .replace("{displayName}", channel.displayName())
                .replace("{color}", channel.chatColor());
        DiscordBot.getDiscordBot().sendChannelMessage(discordMsg, channel);
    }

    public void sendJoinLeaveMessage(String player, Channel channel, boolean isJoin) {
        if(isJoin) {

            plugin.getProxy().getPlayers().forEach(player1 -> {
                if(playerMutedChannels.get(player1.getUniqueId()) == null || !playerMutedChannels.get(player1.getUniqueId()).contains(channel)) {
                    if (!player1.hasPermission(channel.permission())) return;

                    String msg = messages.getString("join")
                            .replace("{player}", player)
                            .replace("{displayName}", channel.displayName())
                            .replace("{color}", channel.chatColor());
                    formattedForJoinLeave = ChatColor.translateAlternateColorCodes('&', msg);
                    player1.sendMessage(new TextComponent(formattedForJoinLeave));
                }
            });

            String msg = messages.getString("discordJoin")
                    .replace("{player}", player)
                    .replace("{displayName}", channel.displayName())
                    .replace("{color}", channel.chatColor());

            DiscordBot.getDiscordBot().sendChannelMessage(msg, channel);
            plugin.getLogger().info(formattedForJoinLeave);
        } else {
            plugin.getProxy().getPlayers().forEach(player1 -> {
                if (!player1.hasPermission(channel.permission())) return;
                String msg = messages.getString("leave")
                        .replace("{player}", player)
                        .replace("{displayName}", channel.displayName())
                        .replace("{color}", channel.chatColor());
                formattedForJoinLeave = ChatColor.translateAlternateColorCodes('&', msg);
                player1.sendMessage(new TextComponent(formattedForJoinLeave));
            });

            String msg = messages.getString("discordLeave")
                    .replace("{player}", player)
                    .replace("{displayName}", channel.displayName())
                    .replace("{color}", channel.chatColor());
            DiscordBot.getDiscordBot().sendChannelMessage(msg, channel);
            plugin.getLogger().info(formattedForJoinLeave);
        }
    }

    public void sendStaffList(CommandSender sender, boolean vanishedPlayers) {
        ArrayList<String> staff = new ArrayList<>();
        if(vanishedPlayers) {
            plugin.getProxy().getPlayers().forEach(player -> {
                if(!player.hasPermission("superultrastaffchat.staff")) {
                    return;
                }

                if(Database.getDatabase().getPlayerVanished(player.getUniqueId().toString())) {
                    staff.add(ChatColor.AQUA + "[" + player.getServer().getInfo().getName() + "] " + player.getName() + " (Vanished)");
                } else {
                    staff.add(ChatColor.AQUA + "[" + player.getServer().getInfo().getName() + "] " + player.getName());
                }
            });
        } else {
            plugin.getProxy().getPlayers().forEach(player -> {
                if(!player.hasPermission("superultrastaffchat.staff")) {
                    return;
                }

                if(Database.getDatabase().getPlayerVanished(player.getUniqueId().toString())) {
                    return;
                }

                staff.add(ChatColor.AQUA + "[" + player.getServer().getInfo().getName() + "] " + player.getName());
            });
        }

        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8&m--------------&r &6Online Staff &8&m--------------")));
        if(staff.size() == 0) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cThere are no staff online!")));
        } else {
            for(String staffMember : staff) {
                sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', staffMember)));
            }
        }
    }

    public void reload() throws IOException, SQLException, LoginException, InterruptedException {
        messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "messages.yml"));
        plugin.reload();
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
