package dev.jamieisgeek.superultrastaffchat.Events;

import dev.jamieisgeek.superultrastaffchat.Manager;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchEvent implements Listener {

    @EventHandler
    public void onServerSwitch(net.md_5.bungee.api.event.ServerSwitchEvent event) {
        String preServer = event.getFrom().getName();

        if(preServer == null) return;

        String postServer = event.getPlayer().getServer().getInfo().getName();
        String playerName = event.getPlayer().getName();

        Manager manager = Manager.getManager();
        manager.getChannels().forEach(channel -> {
            if (channel.moveMessages() && event.getPlayer().hasPermission(channel.permission())) {
                manager.sendMoveMessage(playerName, preServer, postServer, channel);
            }
        });
    }
}
