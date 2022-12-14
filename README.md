# SuperUltraStaffChat

This plugin is a remake of the plugin UltraStaffChatPro, made for SootMC, this README basically just says how to make your channels and stuff.


## The Config
The config file is the main part of the plugin, its where you define all the things for your channels.

```yaml
botToken:
```
The botToken config option is where you need to put the token for the Discord bot that will link into your plugin.


### Setting up channels
```yaml
channels:
  staff:
    displayName: "&4&lStaff"
    permission: "staffchat.staff"
    chatColor: "&c"
    chatPrefix: "#"
    command: "sc"
    commandAliases: ["staffchat", "staffc"]
    moveMessages: true
    discordChannel: 328923486723845
    format: "&c[&4&lStaff&c] &f| &c{server} &f| &c{player} &7» &c{message}"
```
This is the standard format for each channel. The name of the channel is defined by the key of the channel. The displayName option is what will display before the outputs for each command.
The permission option is literally what permission is required to be able to interact with the channel. The chatColor is what gets used quite a few of the command outputs. The chatPrefix is what can be put before a message to force it into the channel. 
The command is what the primary command to interact with a channel is and the commandAliases are same. The moveMessages option is what needs to be a boolean as to whether join/leave and server switch messages will be displayed in the channel. The discordChannel option
is the ID of the Discord channel where all the messages sent in a channel in-game will be sent and any messages sent in that channel will be sent in-game. The format option is what each message that gets sent in a channel looks like. All placeholders have been used in the default config.

Channels can be scaled infinitely in the plugin, just copy and paste the default channel and alter what you want and it'll create a new channel :D
