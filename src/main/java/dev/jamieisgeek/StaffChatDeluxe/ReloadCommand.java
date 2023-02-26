package dev.jamieisgeek.StaffChatDeluxe;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("scdr", "staffchatdeluxe.reload", "scdr");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 1) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /susc reload"));
            return;
        }

        if (args[0].equals("reload")) {
            try {
                Manager.getManager().reload();
            } catch (IOException | SQLException | LoginException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Reloaded config"));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /susc reload"));
        }

    }
}
