package dev.jamieisgeek.superultrastaffchat;

public record Channel(String name, String displayName, String permission, String chatColor, String chatPrefix, String command, String[] aliases) {}