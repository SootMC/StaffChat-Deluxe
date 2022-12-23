package dev.jamieisgeek.superultrastaffchat.Models;

import dev.jamieisgeek.superultrastaffchat.Manager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiscordBot implements EventListener {
    private final String token;
    private HashMap<Channel, String> discordChannels = new HashMap<>();
    private static JDA BOT;
    private static DiscordBot discordBot;
    private final Manager manager;

    public DiscordBot(String token, Configuration config, ArrayList<Channel> channels) throws LoginException, InterruptedException {
        this.token = token;
        this.manager = Manager.getManager();
        discordBot = this;
        for(Channel channel : channels) {
            discordChannels.put(channel, channel.discordChannelID());
        }

        BOT = JDABuilder.createLight(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS
                )
                .disableIntents(
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGE_TYPING
                )
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setBulkDeleteSplittingEnabled(false)
                .addEventListeners(this)
                .build().awaitReady();
    }


    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof GuildMessageReceivedEvent event) {
            if(!discordChannels.containsValue(event.getChannel().getId())) {
                return;
            }

            if(event.getAuthor().isBot()) {
                return;
            }

            Channel channel = null;

            for(Map.Entry<Channel, String> entry : discordChannels.entrySet()) {
                if(Objects.equals(event.getChannel().getId(), entry.getValue())) {
                    channel = entry.getKey();
                }
            }

            if(channel != null) {
                manager.sendMessageToChannel(channel, event.getMessage().getContentRaw(), event.getAuthor().getAsTag(), "Discord", true);
            } else {
                manager.getPlugin().getLogger().severe("Error when getting channel from DiscordBot.class");
            }
        } else if (genericEvent instanceof ReadyEvent event) {
            manager.getPlugin().getLogger().info("Discord bot logged in as: " + event.getJDA().getSelfUser().getAsTag());
        }
    }

    public void sendJoinLeaveMessage(String player, Channel channel, boolean isJoin) {
        if(discordChannels.containsKey(channel)) {
            if(isJoin) {
                BOT.getTextChannelById(discordChannels.get(channel)).sendMessage("[+] **" + player + "**").queue();
            } else {
                BOT.getTextChannelById(discordChannels.get(channel)).sendMessage("[-] **" + player + "**").queue();
            }
        }
    }

    public void sendSwitchMessage(String message, Channel channel) {
        if(discordChannels.containsKey(channel)) {
            BOT.getTextChannelById(discordChannels.get(channel)).sendMessage(message).queue();
        }
    }
    public void sendChannelMessage(String message, Channel channel, String serverName, String sender) {
        BOT.getTextChannelById(discordChannels.get(channel)).sendMessage("**" + serverName + "** | " + sender + ": " + message).queue();
    }

    public static DiscordBot getDiscordBot() {
        return discordBot;
    }

    public static JDA getBOT() {
        return BOT;
    }
}