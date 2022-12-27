package dev.jamieisgeek.superultrastaffchat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class StaffListCommand extends Command {

    public StaffListCommand() {
        super("stafflist", "superultrastaffchat.stafflist", "staff");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        Manager.getManager().sendStaffList(sender, sender.hasPermission("superultrastaffchat.stafflist.seevanished"));
    }
}
