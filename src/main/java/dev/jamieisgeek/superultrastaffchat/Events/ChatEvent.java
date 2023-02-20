package dev.jamieisgeek.superultrastaffchat.Events;

import dev.jamieisgeek.superultrastaffchat.Manager;
import dev.jamieisgeek.superultrastaffchat.Models.Channel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatEvent implements Listener {
    @EventHandler
    public void onChat(net.md_5.bungee.api.event.ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        String senderName = player.getName();
        String serverName = player.getServer().getInfo().getName();
        boolean isDiscord = false;
        Manager manager = Manager.getManager();

        if (event.isCommand()) {
            return;
        }

        if (event.isProxyCommand()) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        manager.getChannels().forEach(channel -> {
            if (message.startsWith(channel.chatPrefix())) {
                if(manager.getPlayerToggledChannel().get(player.getUniqueId()) != null && manager.getPlayerToggledChannel().get(player.getUniqueId()) == channel) {
                    return;
                }

                if(player.hasPermission(channel.permission())) {
                    String msg = message.replace(channel.chatPrefix(), "");
                    manager.sendMessageToChannel(channel, msg, senderName, serverName, isDiscord);
                    event.setCancelled(true);
                }
            }
        });

        if(manager.getPlayerToggledChannel().get(player.getUniqueId()) != null) {
            Channel channel = manager.getPlayerToggledChannel().get(player.getUniqueId());
            if(player.hasPermission(channel.permission())) {
                manager.sendMessageToChannel(channel, message, senderName, serverName, isDiscord);
                event.setCancelled(true);
            }
        }
    }
}
