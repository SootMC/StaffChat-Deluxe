package dev.jamieisgeek.StaffChatDeluxe.Events;

import dev.jamieisgeek.StaffChatDeluxe.Manager;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchEvent implements Listener {

    @EventHandler
    public void onServerSwitch(net.md_5.bungee.api.event.ServerSwitchEvent event) {
        if(event.getFrom() == null) return;

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
