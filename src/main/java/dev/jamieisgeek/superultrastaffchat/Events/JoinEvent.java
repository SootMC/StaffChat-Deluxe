package dev.jamieisgeek.superultrastaffchat.Events;

import dev.jamieisgeek.superultrastaffchat.Manager;
import dev.jamieisgeek.superultrastaffchat.Models.Channel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.ConnectException;

public class JoinEvent implements Listener {
    Manager manager = Manager.getManager();
    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        manager.getChannels().forEach(channel -> {
            if(player.hasPermission(channel.permission()) && channel.moveMessages()) {
                manager.sendJoinLeaveMessage(player.getName(), channel, true);
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        manager.getChannels().forEach(channel -> {
            if(player.hasPermission(channel.permission()) && channel.moveMessages()) {
                manager.sendJoinLeaveMessage(player.getName(), channel, false);
            }

            manager.removePlayerMutedChannel(player.getUniqueId(), channel);
        });

        manager.getPlayerToggledChannel().remove(player.getUniqueId());
    }
}
