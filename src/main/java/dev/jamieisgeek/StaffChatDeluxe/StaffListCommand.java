package dev.jamieisgeek.StaffChatDeluxe;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class StaffListCommand extends Command {

    public StaffListCommand() {
        super("stafflist", "staffchatdeluxe.stafflist", "staff");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        Manager.getManager().sendStaffList(sender, sender.hasPermission("staffchatdeluxe.stafflist.seevanished"));
    }
}
